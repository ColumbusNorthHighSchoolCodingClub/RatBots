package grid;

import actor.RatBotActor;

/**
 * This is just a collection class to link walls with grid spaces.
 * @author Spock
 */
public class GridSpace 
{
    public boolean wallNorth;
    public boolean wallEast;
    public boolean wallSouth;
    public boolean wallWest;
    public RatBotActor occupant;
    
    public GridSpace()
    {
        wallNorth = false;
        wallEast = false;
        wallSouth = false;
        wallWest = false;  
        occupant = null;
    }
    
}
