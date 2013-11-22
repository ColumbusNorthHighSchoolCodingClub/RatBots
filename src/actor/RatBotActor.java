package actor;

/* 
 * This class is an adaptation of the Actor class from GridWorld.  
 * 
 * AP(r) Computer Science GridWorld Case Study:
 * Copyright(c) 2005-2006 Cay S. Horstmann (http://horstmann.com)
 *
 * This code is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * @author Cay Horstmann
 */



import grid.Grid;
import grid.Location;
import grid.RatBotsGrid;
import java.awt.Color;

/**
 * A <code>RatBotActor</code> is an entity with a color and direction that 
 * can act. <br />
  */
public class RatBotActor
{
    private Grid<RatBotActor> grid;
    private Location location;
    private int direction;
    private Color color;

    /**
     * Constructs a blue actor that is facing north.
     */
    public RatBotActor()
    {
        color = Color.BLUE;
        direction = Location.NORTH;
        grid = null;
        location = null;
    }
    /**
     * Constructs a RatBotActor that copies the properties of another RatBotActor.
     * @param in the RatBotActor to be copied to construct this RatBotActor.  
     */
    public RatBotActor(RatBotActor in)
    {
        color = in.getColor();
        direction = in.getDirection();
        grid = null;
        location = null;        
    }

    /**
     * Gets the color of this RatBotActor.
     * @return the color of this RatBotActor
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * Sets the color of this RatBotActor.
     * @param newColor the new color
     */
    public void setColor(Color newColor)
    {
        color = newColor;
    }

    /**
     * Gets the current direction of this RatBotActor.
     * @return the direction of this RatBotActor as an angle. (0,90,180 or 270)
     */
    public int getDirection()
    {
        return direction;
    }

    /**
     * Sets the current direction of this RatBotActor.
     * @param newDirection the new direction. The direction of this RatBotActor 
     * is set to the nearest lower multiple of 90 degrees of
     * <code>newDirection</code>. (0,90,180 or 270)
     */
    public void setDirection(int newDirection)
    {
        direction = newDirection % Location.FULL_CIRCLE;
        if (direction < 0)
            direction += Location.FULL_CIRCLE;
        //RatBots can move diagonally in RatBots 14
        if(direction%Location.HALF_RIGHT != 0)
            direction = direction - direction%Location.HALF_RIGHT;
    }

    /**
     * Gets the grid in which this RatBotActor is located.
     * @return the grid of this RatBotActor, or <code>null</code> if this 
     * RatBotActor is not contained in a grid
     */
    public RatBotsGrid<RatBotActor> getGrid()
    {
        return (RatBotsGrid)grid;
    }

    /**
     * Gets the location of this RatBotActor.
     * @return the location of this RatBotActor, or <code>null</code> if this 
     * RatBotActor is not contained in a grid
     */
    public Location getLocation()
    {
        return location;
    }

    /**
     * Puts this actor into a grid. If there is another actor at the given
     * location, it is removed. <br />
     * Precondition: (1) This actor is not contained in a grid (2)
     * <code>loc</code> is valid in <code>gr</code>
     * @param gr the grid into which this actor should be placed
     * @param loc the location into which the actor should be placed
     */
    public void putSelfInGrid(Grid<RatBotActor> gr, Location loc)
    {
        if (grid != null)
            throw new IllegalStateException(
                    "This actor is already contained in a grid.");

        RatBotActor actor = gr.get(loc);
        if (actor != null)
            actor.removeSelfFromGrid();
        gr.put(loc, this);
        grid = gr;
        location = loc;
    }
/**
 * Puts this actor into a grid, but not into a specific Location.  
 * This is used for 'OffGrid' Actors such as PaintBalls and Explosions.  
 * @param gr the grid into which this actor should be placed
 */
    public void putSelfInGridNoLocation(Grid<RatBotActor> gr)
    {
        grid = gr;
    }

    /**
     * Removes this actor from its grid. <br />
     * Precondition: This actor is contained in a grid
     */
    public void removeSelfFromGrid()
    {
        if (grid == null)
            throw new IllegalStateException(
                    "This actor is not contained in a grid.");
        if (grid.get(location) != this)
            throw new IllegalStateException(
                    "The grid contains a different actor at location "
                            + location + ".");

        grid.remove(location);
        grid = null;
        location = null;
    }

    /**
     * Moves this actor to a new location. If there is another actor at the
     * given location, it is removed. <br />
     * Precondition: (1) This actor is contained in a grid (2)
     * <code>newLocation</code> is valid in the grid of this actor
     * @param newLocation the new location
     */
    public void moveTo(Location newLocation)
    {
        if (grid == null)
            throw new IllegalStateException("This actor is not in a grid.");
        if (grid.get(location) != this)
            throw new IllegalStateException(
                    "The grid contains a different actor at location "
                            + location + ".");
        if (!grid.isValid(newLocation))
            throw new IllegalArgumentException("Location " + newLocation
                    + " is not valid.");

        if (newLocation.equals(location))
            return;
        grid.remove(location);
        RatBotActor other = grid.get(newLocation);
        if (other != null)
            other.removeSelfFromGrid();
        location = newLocation;
        grid.put(location, this);
    }
    
    public boolean canMove()
    {
        return canMove(location, direction);
    }
    
    public boolean canMove(Location loc, int dir)
    {
        boolean ableToMove = true;
        Location next = loc.getAdjacentLocation(dir);
        if(!getGrid().isValid(next))
            return false;

        if(dir%90 == 0)
        {   //For N-S-E-W
            if(getGrid().isWall(loc, dir))
                ableToMove = false;
        }
        else
        {   //For NE-NW-SE-SW
            ableToMove = canMoveDiagonally(loc, dir);
        }
        
        //Make sure destination is not occupied by another RatBotActor
        if(getGrid().get(next) instanceof Rat)
            return false;
        //************************************************************
       
        return ableToMove;
    }
    
    public boolean canMoveDiagonally(Location loc, int dir)
    {
        boolean ableToMove = true;
        //Make sure next space isValid
        Location next = location.getAdjacentLocation(direction);
        if(!getGrid().isValid(next))
            return false;
        
        if(getGrid().isWall(loc, dir+Location.HALF_LEFT))
            ableToMove = false;
        if(getGrid().isWall(loc, dir+Location.HALF_RIGHT))
            ableToMove = false;
        if(getGrid().isWall(next, dir+Location.HALF_LEFT+Location.HALF_CIRCLE))
            ableToMove = false;
        if(getGrid().isWall(next, dir+Location.HALF_RIGHT+Location.HALF_CIRCLE))
            ableToMove = false;
        
        return ableToMove;        
    }

    public boolean isNextSpaceValid()
    {
        Location next = location.getAdjacentLocation(direction);
        if(!getGrid().isValid(next))
            return false;
        return true;
    }
    /**
     * Reverses the direction of this actor. Override this method in subclasses
     * of <code>Actor</code> to define types of actors with different behavior
     * 
     */
    public void act()
    {
        setDirection(getDirection() + Location.HALF_CIRCLE);
    }

    /**
     * Creates a string that describes this actor.
     * @return a string with the location, direction, and color of this actor
     */
    public String toString()
    {
        return getClass().getName() + "[location=" + location + ",direction="
                + direction + ",color=" + color + "]";
    }
    /**
     * Gets a clone of this RatBotActor
     * @return a new RatBotActor which copies the properties of this RatBotActor
     */
    public RatBotActor getClone()
    {
        RatBotActor clone = new RatBotActor(this);
        return clone;
    }
}