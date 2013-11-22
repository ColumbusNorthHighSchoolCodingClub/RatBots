package ratbot;

import actor.RatBot;
import java.util.Random;
/**
 * @author Spock
 * RandomRat chooses a random move each turn.
 */
public class RandomRat extends RatBot
{
    Random randy = new Random();
    
    public RandomRat()
    {
        setName("RandomRat");
    }
    
    @Override
    public int chooseAction()
    {        
        return randy.nextInt(360);  //move some Random direction!
    }
    
}
