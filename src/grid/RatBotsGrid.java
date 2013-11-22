package grid;

/* A RatBotsGrid is adapted from the BoundedGrid class. 
 * 
 * The code for this class is adapted from the BoundedGrid class in the 
 * AP(r) Computer Science GridWorld Case Study:
 * Copyright(c) 2002-2006 College Entrance Examination Board 
 * (http://www.collegeboard.com).
 * @author Alyce Brady
 * @author APCS Development Committee
 * @author Cay Horstmann
 * 
 * adapted by Spock
 */
//TODO: fix emptyLocations to ignore Tails!
//TODO: Make a removeAllActors method


import actor.Rat;
import actor.Cheese;
import java.util.ArrayList;

/**
 * A <code>RatBotsGrid</code> is a rectangular grid with a finite number of
 * rows and columns. <br />
 * It contains objects both on the grid as well as 'off-grid' objects.  
 * @param <E> 
 */
public class RatBotsGrid<E> extends AbstractGrid<E>
{
    private Object[][] occupantArray; // the array storing the grid elements
    private boolean[][][] walls;

    /**
     * Constructs an empty bounded grid with the given dimensions.
     * (Precondition: <code>rows > 0</code> and <code>cols > 0</code>.)
     * @param rows number of rows in RatBotsGrid
     * @param cols number of columns in RatBotsGrid
     */
    public RatBotsGrid(int rows, int cols)
    {
        if (rows <= 0)
            throw new IllegalArgumentException("rows <= 0");
        if (cols <= 0)
            throw new IllegalArgumentException("cols <= 0");
        occupantArray = new Object[rows][cols];
        walls = new boolean[rows][cols][4];
    }
    
    public boolean isWall(Location loc, int dir)
    {       
        if(isValid(loc) && dir >= 0)
        {
            return walls[loc.getRow()][loc.getCol()][(dir%360)/90];
        }
        return false;
    }
    public void setWall(Location loc, int dir)
    {
//        System.out.println("Attempting to setWall at "+loc);
        if(isValid(loc) && dir >= 0)
        {
            walls[loc.getRow()][loc.getCol()][(dir%360)/90] = true;
//            System.out.println("setWall at "+loc);
        }
    }
    public void setWalls(boolean[][][] in){
        if(in.length!=getNumRows() || in[0].length!=getNumCols() || in[0][0].length!=4){
            throw new IllegalArgumentException("walls array of incorrect dimensions");
        }
        walls = in;
    }
    public void removeWall(Location loc, int dir)
    {
        if(isValid(loc) && dir >= 0)
        {
            walls[loc.getRow()][loc.getCol()][(dir%360)/90] = false;
        }
    }
    public void removeAllWallsAroundSpace(Location loc)
    {
        if(isValid(loc))
            for(int d = 0; d < 4; d++)
                walls[loc.getRow()][loc.getCol()][d] = false;
    }
    public void addWallPair(Location loc, int d)
    {
        setWall(loc, d);
        Location border = loc.getAdjacentLocation(d);
        setWall(border, d+180);
    }
    
    public void removeWallPair(Location loc, int d)
    {
        removeWall(loc, d);
        Location border = loc.getAdjacentLocation(d);
        removeWall(border, d+180);
    }
    

    @Override
    public int getNumRows()
    {
        return occupantArray.length;
    }

    @Override
    public int getNumCols()
    {
        // Note: according to the constructor precondition, numRows() > 0, so
        // theGrid[0] is non-null.
        return occupantArray[0].length;
    }
    
    public boolean[][][] getWalls(){
        return walls;
    }

    @Override
    public boolean isValid(Location loc)
    {
        return 0 <= loc.getRow() && loc.getRow() < getNumRows()
                && 0 <= loc.getCol() && loc.getCol() < getNumCols();
    }

    @Override
    public ArrayList<Location> getOccupiedLocations()
    {
        ArrayList<Location> theLocations = new ArrayList<Location>();

        // Look at all grid locations.
        for (int r = 0; r < getNumRows(); r++)
        {
            for (int c = 0; c < getNumCols(); c++)
            {
                // If there's an object at this location, put it in the array.
                Location loc = new Location(r, c);
                if (get(loc) != null) 
                    theLocations.add(loc);
            }
        }

        return theLocations;
    }

    @Override
    public E get(Location loc)
    {
        if (!isValid(loc))
            throw new IllegalArgumentException("Location " + loc
                    + " is not valid");
        return (E) occupantArray[loc.getRow()][loc.getCol()]; // unavoidable warning
    }

    @Override
    public E put(Location loc, E obj)
    {
        if (!isValid(loc))
            throw new IllegalArgumentException("Location " + loc
                    + " is not valid");
        if (obj == null)
            throw new NullPointerException("obj == null");

        // Add the object to the grid.
        E oldOccupant = get(loc);
        occupantArray[loc.getRow()][loc.getCol()] = obj;
        return oldOccupant;
    }

    @Override
    public E remove(Location loc)
    {
        if (!isValid(loc))
            throw new IllegalArgumentException("Location " + loc
                    + " is not valid");
        
        // Remove the object from the grid.
        E r = get(loc);
        occupantArray[loc.getRow()][loc.getCol()] = null;
        return r;
    }
    /**
     * Gets all of the Rats that are in the Grid
     * @return an ArrayList filled with all Rats in this grid.
     */
    public ArrayList<Rat> getAllRats()
    {
        ArrayList<Rat> rats = new ArrayList<Rat>();
        
        ArrayList<Location> occupied = getOccupiedLocations();
        
        for(Location loc : occupied)
        {
            if(get(loc) instanceof Rat)
                rats.add((Rat)get(loc));
        }
        
        return rats;
    }
    
    public ArrayList<Rat> getAllRatsByName(){
        ArrayList<Rat> rats = getAllRats();
        ArrayList<Rat> sorted = new ArrayList();
        for(int index = 0; index < rats.size(); index++){
            String name = rats.get(0).getRatBot().getName();
            int nextRatIndex = 0;
            for(int a = 1; a < rats.size(); a++){
                if(rats.get(a).getRatBot().getName().compareTo(name)<0){
                    name = rats.get(a).getRatBot().getName();
                    nextRatIndex = a;
                }
            }
            sorted.add(rats.get(nextRatIndex));
            rats.remove(nextRatIndex);
            index--;
        }
        return sorted;
    }
    
    public ArrayList<Cheese> getLittleCheeses(){
        ArrayList<Cheese> cheeses = new ArrayList();
        ArrayList<Location> occupied = getOccupiedLocations();
        
        for(Location loc : occupied){
            if(get(loc) instanceof Cheese){
                Cheese c = (Cheese)get(loc);
                if(!c.isCorner()){
                    cheeses.add(c);
                }
            }
        }
        return cheeses;
    }
    
}
