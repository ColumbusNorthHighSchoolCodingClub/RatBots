package actor;

import grid.Location;
import grid.RatBotsGrid;
import gui.RatBotsArena;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
/**
 * Rats are the competitors in the RatBots game.  Every Rat has a RatBot 
 * that acts as its 'brain' by making decisions for its actions.  
 * @author Spock
 */
public class Rat extends RatBotActor
{       
    /**
     * Each Rat has a RatBot as it's 'brain'.  The Rat 'tells' the RatBot 
     * what it 'sees' and the RatBot makes the decision on how to act.  
     */
    private RatBot ratBot; 
    
    public final int CHEESES_TO_PLACE = 40;
    private int score;
    private int numCheeseToPlace;
    private int roundsWon;
    private int matchesWon;
    private int matchesTied;
    private int matchesLost;
    private int totalScore = 0;
    private int lastChoice = 0;
    private int choice = 0;
    
    private ArrayList<Tail> tails = new ArrayList();
    
    /**
     * Constructs a red Rat with a generic RatBot.  
     */
    public Rat()
    {
        setColor(Color.RED);
        ratBot = new RatBot();
        initialize();
    }
    /**
     * Constructs a Rat with the given color and given RatBot for its 'brain'.
     * All rats have a RatBot that chooses their action each turn.  
     * @param rb the RatBot that makes decisions for this Rat.
     * @param ratColor the color of this Rat.
     */
    public Rat(RatBot rb, Color ratColor)
    {
        ratBot = rb;
        setColor(ratColor);
        initialize();
    }
    /**
     * Constructs a copy of this Rat (that does not include the RatBot.)
     * @param in the Rat being copied.
     */
    public Rat(Rat in)
    {
        super(in);
        this.setScore(in.getScore());
    }

    /**
     * Overrides the <code>act</code> method in the <code>RatBotActor</code> 
     * class to act based on what the RatBot decides.
     */
    @Override
    public void act()
    {
        //Ask the RatBot what to do. 
        giveDataToRatBot();
        choice = ratBot.chooseAction();
        lastChoice = choice;
        
        if(choice < 0)
        {
            //REST
        }
        else if(choice<360)
        {
            //MOVE
            turn(choice);
            move();
        }
        else if(choice<720)
        {
            //BUILD
            turn((choice/90)*90); //no diagonal walls, must be mult of 90deg
            buildWall();
        }
        if(numCheeseToPlace > 0)
            numCheeseToPlace--; //Decrement cheeses to place.
        
    } //end of act() method
    
    /*
     * This act method takes an input as the rat's action.
     * This was implemented for replaying recorded matches.
     */
    public void act(int in){
        lastChoice = in;
        
        if(in < 0)
        {
            //REST
        }
        else if(in<360)
        {
            //MOVE
            turn(in);
            move();
        }
        else if(in<720)
        {
            //BUILD
            turn((in/90)*90); //no diagonal walls, must be mult of 90deg
            buildWall();
        }
        if(numCheeseToPlace > 0)
            numCheeseToPlace--; //Decrement cheeses to place.
    }
    
    /**
     * Turns the Rat
     * @param newDirection the direction to turn to.   
     */
    public void turn(int newDirection)
    {
        setDirection(newDirection);
    }

    /**
     * Moves the rat forward, putting a Tail into the location it previously
     * occupied.
     */
    public void move()
    {        
        if(canMove())
        {
            Location old = getLocation();
            Location next = old.getAdjacentLocation(getDirection());
            boolean corner = false;
            
            //Check to see if next has a Cheese and process it
            if(getGrid().get(next) instanceof Cheese)
            {
                Cheese c = (Cheese)getGrid().get(next);
                score += c.getPointValue();
                if(c.isCorner()) corner = true;
            }
            
            moveTo(next);
            if(numCheeseToPlace > 0)
            {   //place a cheese in old location
                Cheese newCheese = new Cheese(false);
                newCheese.putSelfInGrid(getGrid(), old);
            }

            if(corner)
            {
                numCheeseToPlace += CHEESES_TO_PLACE;
                //Move to a center Location!!!
//                moveTo(getRandomUnnocupiedCenterLocation());  
                moveTo(getDeterministicCenterLocation());
            }
        }
    }
    
    private void buildWall()
    {
        getGrid().addWallPair(getLocation(), getDirection());
    }
    private Location processCheese(Cheese in)
    {
        Location loc = in.getLocation();
        
        if(in.isCorner())
        {
            numCheeseToPlace += CHEESES_TO_PLACE;
            //Move to a center Location!!!
//            loc = getRandomUnnocupiedCenterLocation();     
            loc = getDeterministicCenterLocation();
        }
        score += in.getPointValue();
        return loc;
    }
            
    private Location getRandomUnnocupiedCenterLocation()
    {
//        Location loc = new Location(1,1); //FIX THIS ****************
        ArrayList<Location> unnocupiedCenterLocs = new ArrayList<Location>();
        Random randy = new Random();
        
        int small = (getGrid().getNumCols()-RatBotsArena.CENTER_SIZE)/2;
        int big = small+RatBotsArena.CENTER_SIZE-1;
        
        for(int r = small; r < big; r++)
        {
            for(int c = small; c < big; c++)
            {
                Location loc = new Location(r,c);
                if(!(getGrid().get(loc) instanceof Rat))
                    unnocupiedCenterLocs.add(loc);
            }
        } 
        
        if(unnocupiedCenterLocs.isEmpty())
        {
            System.out.println("Uh-oh! THE CENTER IS FULL!!!");
            return new Location(small,small);
        }
        return unnocupiedCenterLocs.get(randy.nextInt(unnocupiedCenterLocs.size()) );
    }
    
    private Location getDeterministicCenterLocation(){        
        
        int small = (getGrid().getNumCols()-RatBotsArena.CENTER_SIZE)/2;
        int big = small+RatBotsArena.CENTER_SIZE-1;
        
        for(int r = small; r < big; r++)
        {
            for(int c = small; c < big; c++)
            {
                Location loc = new Location(r,c);
                if(!(getGrid().get(loc) instanceof Rat))
                    return loc;
            }
        } 
        return new Location(small,small);
    }
    
    @Override
    public String toString()
    {
        return "Rat: "+ratBot.getName();
    }

    /**
     * Updates the most recent data (location, grid and status)
     * information to the RatBot.  This allows the RatBot to make a decision
     * based on current data every turn.  
     */
    public final void giveDataToRatBot()
    {
        RatBotsGrid<RatBotActor> old = (RatBotsGrid)getGrid();
        RatBotsGrid<RatBotActor> newGrid = createFoggedGrid(old);
        
        /*
         * A Rat can see until it sees a wall in the eight directions. 
         */
        for(int dir = Location.NORTH; dir < 360; dir+=45)
        {
            Location loc = getLocation(); //Start from the Rat's location.
            boolean continueLoop = true;
            while(continueLoop)
            {
                //Check if loc is on the Grid (valid)
                if(old.isValid(loc))
                {      
                    copyGridLocation(old, newGrid, loc);
                    if(!canMove(loc,dir))
                        continueLoop = false;
                    //Switch value of loc to next Location.
                    loc = loc.getAdjacentLocation(dir);
//                    loc = new Location(loc.getAdjacentLocation(dir).getRow(),
//                                    loc.getAdjacentLocation(dir).getCol() );
                } 
                else
                    continueLoop = false;
            }//end of while loop
        }
             
        ratBot.setSensorGrid(newGrid);
        ratBot.setDirection(getDirection());
        ratBot.setLocation(getLocation());
        ratBot.setScore(score);
    }
    
    public void copyGridLocation(RatBotsGrid from, RatBotsGrid to, Location loc)
    {
        //Copy the Actor
        RatBotActor actor = (RatBotActor)from.get(loc);
        if(actor != null)
        {
            RatBotActor clone = actor.getClone();
            clone.putSelfInGrid(to, loc);
        }  
        else
        {
            to.remove(loc); //remove the fog.
        }
        //Copy the walls.
        for(int d = 0; d < 360; d+=90)
        {
            if(from.isWall(loc, d))
                to.setWall(loc, d);
        }
        
    }

    private RatBotsGrid createFoggedGrid(RatBotsGrid in)
    {
        RatBotsGrid<RatBotActor> newGrid = new RatBotsGrid<RatBotActor>(in.getNumCols(),in.getNumRows());
        for (int i = 0; i < in.getNumRows(); i++)
        {
            for (int j = 0; j < in.getNumCols(); j++)
            {
                Location loc = new Location(i, j);
                Fog fog = new Fog();
                fog.putSelfInGrid(newGrid, loc);
            }
        }
      return newGrid;
    }
    
    /**
     * Accessor method to get the RatBot from a Rat.
     * @return the RatBot 'brain' of this RatBot.
     */
    public RatBot getRatBot()
    {
        return ratBot;
    }

    /**
     * Gets the current score (from this round).  
     * @return the score
     */
    public int getScore() { return score; }
    /**
     * Sets the current score of this Rat.
     * @param in the score
     */
    public void setScore(int in) { score = in; }
    /**
     * Adds the given amount to score of this Rat.  
     * @param add the amount to add
     */
    
    public void addToScore(int add) { score += add; }
    /**
     * Gets the total points scored over all rounds in this match for this Rat.
     * @return the total score
     */
    public int getTotalScore() { return totalScore; }

    /**
     * Gets the number of rounds won by this Rat in the match.
     * @return the rounds won
     */
    public int getRoundsWon() { return roundsWon; }
    
    
    /**
     * Sets the number of rounds won by this Rat in the match.
     * @param in the rounds won
     */
    public void setRoundsWon(int in) { roundsWon = in; }
    /**
     * Increases the number of rounds won in this match by this Rat by one.
     */
    public void increaseRoundsWon() { roundsWon++; }

    // These methods are used for the RoundRobin tourney.
    public int getMatchesWon() { return matchesWon; }
    public int getMatchesTied() { return matchesTied; }
    public int getMatchesLost() { return matchesLost; }
    public int getLastAction(){return lastChoice;}
    public void increaseMatchesWon() { matchesWon++; }
    public void increaseMatchesTied() { matchesTied++; }
    public void increaseMatchesLost() { matchesLost++; }
     /**
     * Initializes this Rat for a new round.  
     */
    public final void initialize()
    {
        totalScore += score;
        score = 0;
        ratBot.initForRound();
    }

    @Override
    public RatBotActor getClone()
    {
        RatBotActor clone = new Rat(this);
        return clone;
    }
    
}