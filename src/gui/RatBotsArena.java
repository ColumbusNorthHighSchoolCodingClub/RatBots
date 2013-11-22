package gui;

import actor.Cheese;
import actor.RatBotActor;
import grid.Grid;
import grid.Location;
import grid.RatBotsGrid;
import java.util.ArrayList;
import java.util.Random;
import util.RoundData;
import world.World;

/**
 * The Arena class includes all of the methods needed to setup the arena 
 * according to the rules of the game.  
 * @author Spock
 */
public class RatBotsArena 
{
    private final int PERCENT_WALLS = 12;
    /**
     * The size of a side of the central starting room in the arena. 
     */
    public static final int CENTER_SIZE = 4;
    public static final int NUM_CHEESE_AT_START = 10;
    
    private Random randy = new Random();
    
    private boolean showWalls = true;

    
    /**
     * Toggles whether the grid will include Blocks or not.  
     * This is an option in the Arena menu.
     */
    public void toggleShowBlocks(World world) 
    { 
        showWalls = ! showWalls; 
    }

    /**
     * Initializes the Arena based on the selected rules.  
     * @param world the world that the Arena is within
     */
    public void initializeArena(World world)
    {
        clearAllWalls(world);
        //clean up this code!!!!**************************************
        if(showWalls) addRandomWalls(world);
        if(showWalls) addOuterWalls(world);
        if(showWalls) addCentralRoomWalls(world);
        cutOutExitDoors(world);
        cutOutStartRoomDoors(world);
        addRandomCheese(world);
        addCornerCheese(world);
    }
    
    public void initializeRecordedArena(World world, RoundData round){
        clearAllWalls(world);
        ((RatBotsGrid)world.getGrid()).setWalls(round.walls);
        addRecordedCheese(world, round);
        addCornerCheese(world);
    }
    
    private void clearAllWalls(World world)
    {
        RatBotsGrid grid = (RatBotsGrid)world.getGrid();
        int rows = grid.getNumRows();
        int cols = grid.getNumCols();

        //This section adds the random walls throughout the maze.
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
            {
                Location loc = new Location(i, j);
                grid.removeAllWallsAroundSpace(loc);
            }
    }
    
    private void addRandomWalls(World world)
    {
        RatBotsGrid grid = (RatBotsGrid)world.getGrid();
        int rows = grid.getNumRows();
        int cols = grid.getNumCols();

        //This section adds the random walls throughout the maze.
        for (int i = 1; i < rows-1; i++)
            for (int j = 1; j < cols-1; j++)
            {
                Location loc = new Location(i, j);
                for(int d = 0; d < 360; d+=90)
                {
                    if(randy.nextInt(100)<PERCENT_WALLS && !isInCenter(loc, grid))
                    {
                        grid.addWallPair(loc,d);
                    }                   
                }
            }        
    }
    
    private void addOuterWalls(World world)
    {
        RatBotsGrid grid = (RatBotsGrid)world.getGrid();
        int rows = grid.getNumRows();
        int cols = grid.getNumCols();
        
        for(int z = 0; z < cols; z++)  //Assumes square grid!
        {
            Location top = new Location(0,z);
            Location bottom = new Location(rows-1,z);
            Location right = new Location(z,cols-1);
            Location left = new Location(z,0);
            
            grid.setWall(top, Location.NORTH);
            grid.setWall(bottom, Location.SOUTH);
            grid.setWall(right, Location.EAST);
            grid.setWall(left, Location.WEST);
            if(z>0 && z<cols-1)  //No inner on corners.
            {
                grid.addWallPair(top, Location.SOUTH);
                grid.addWallPair(bottom, Location.NORTH);
                grid.addWallPair(right, Location.WEST);
                grid.addWallPair(left, Location.EAST);
            }
        }
    }
    
    private void addCentralRoomWalls(World world)
    {
        RatBotsGrid grid = (RatBotsGrid)world.getGrid();
        
        int start = (grid.getNumCols()-CENTER_SIZE)/2;
        int end = (grid.getNumCols()+CENTER_SIZE)/2;
        for(int z = start; z < end; z++)  //Assumes square grid!
        {
//            if(Math.abs(z-grid.getNumCols()/2+.5) >= 1)
            {
                Location top = new Location(start,z);
                Location bottom = new Location(end-1,z);
                Location right = new Location(z,end-1);
                Location left = new Location(z,start);
                
                grid.addWallPair(top, Location.NORTH);
                grid.addWallPair(bottom, Location.SOUTH);
                grid.addWallPair(right, Location.EAST);
                grid.addWallPair(left, Location.WEST);    
            }
        }        
    }
    
    private void cutOutExitDoors(World world)
    {
        RatBotsGrid grid = (RatBotsGrid)world.getGrid();
        int rows = grid.getNumRows();
        int cols = grid.getNumCols();

        Location northDoor = new Location(0, randy.nextInt(cols-4)+2);
        grid.removeWallPair(northDoor, Location.SOUTH);
        Location eastDoor = new Location(randy.nextInt(rows-4)+2, cols-1);
        grid.removeWallPair(eastDoor, Location.WEST);
        Location southDoor = new Location(rows-1, randy.nextInt(cols-4)+2);
        grid.removeWallPair(southDoor, Location.NORTH);
        Location westDoor = new Location(randy.nextInt(rows-4)+2, 0);
        grid.removeWallPair(westDoor, Location.EAST);
    }
    
    private void cutOutStartRoomDoors(World world)
    {
        RatBotsGrid grid = (RatBotsGrid)world.getGrid();

        int small = (grid.getNumCols()-CENTER_SIZE)/2;
        int big = small+CENTER_SIZE-1;
        
        Location northDoor = new Location(small, small+randy.nextInt(CENTER_SIZE));
        grid.removeWallPair(northDoor, Location.NORTH);
        Location eastDoor = new Location(randy.nextInt(CENTER_SIZE)+small, big);
        grid.removeWallPair(eastDoor, Location.EAST);
        Location southDoor = new Location(big, randy.nextInt(CENTER_SIZE)+small);
        grid.removeWallPair(southDoor, Location.SOUTH);
        Location westDoor = new Location(randy.nextInt(CENTER_SIZE)+small, small);
        grid.removeWallPair(westDoor, Location.WEST);     
    }
    
    private void addRandomCheese(World world)
    {
        RatBotsGrid grid = (RatBotsGrid)world.getGrid();
        
        for(int z = 0; z < NUM_CHEESE_AT_START; z++)
        {
            int row = randy.nextInt(grid.getNumRows());
            int col = randy.nextInt(grid.getNumCols());
            Location loc = new Location(row, col);
            Cheese c = new Cheese();
            c.putSelfInGrid(grid, loc);
        }
    }
    
    private void addRecordedCheese(World world, RoundData round){
        RatBotsGrid grid = (RatBotsGrid)world.getGrid();
        for(int cheese = 0; cheese < round.cheeseLocs.length; cheese++){
                Cheese c = new Cheese();
                c.putSelfInGrid(grid, new Location(round.cheeseLocs[cheese][0],round.cheeseLocs[cheese][1]));
        }
    }
    
    private void addCornerCheese(World world)
    {
        RatBotsGrid grid = (RatBotsGrid)world.getGrid();

        new Cheese(true).putSelfInGrid(grid, new Location(0,0));
        new Cheese(true).putSelfInGrid(grid,  new Location(0,grid.getNumCols()-1));
        new Cheese(true).putSelfInGrid(grid,  new Location(grid.getNumRows()-1,0));
        new Cheese(true).putSelfInGrid(grid,  new Location(grid.getNumRows()-1,grid.getNumCols()-1));
    }
       
    private Location getRandomCenterLocation(World world)
    {
        Grid<RatBotActor> gr = world.getGrid();
        int tailCount = 0;
        // get all valid empty locations and pick one at random
        ArrayList<Location> emptyLocs = new ArrayList<Location>();
        for(int x=0; x<gr.getNumCols(); x++)
        {
            for(int y=0; y<gr.getNumRows(); y++)
            {
                Location loc = new Location(y,x);
                if( gr.isValid(loc) && gr.get(loc)==null && this.isInCenter(loc, gr) )
                {
                    emptyLocs.add(loc);
                }
            }
        }
        
        if (emptyLocs.isEmpty())
        {
            System.out.println("WARNING: could not find an empty non-center location!!! " + tailCount);
            return new Location(15,15);
        }
        int r = randy.nextInt(emptyLocs.size());
        return emptyLocs.get(r);       

    }
        
    private boolean isInCenter(Location loc, Grid<RatBotActor> grid)
    {
        if( loc.getCol() >= (grid.getNumCols()-CENTER_SIZE)/2 &&
            loc.getCol() < (grid.getNumCols()+CENTER_SIZE)/2 &&
            loc.getRow() >= (grid.getNumRows()-CENTER_SIZE)/2 &&
            loc.getRow() < (grid.getNumRows()+CENTER_SIZE)/2 )
            return true;
        return false;                 
    }
    
    
}
