package ratbot;

import actor.RatBot;
import java.util.Random;
/**
 * @author Spock
 * WallBuilder chooses a random move or build each turn.
 */
public class WallBuilder extends RatBot
{
    Random randy = new Random();
    
    public WallBuilder()
    {
        setName("WallBuilder");
    }
    
    @Override
    public int chooseAction()
    {        
        return randy.nextInt(720);  //move or build some Random direction!
    }
    
}
