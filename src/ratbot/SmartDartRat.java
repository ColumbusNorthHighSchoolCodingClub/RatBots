package ratbot;

import actor.RatBot;
import java.util.Random;
/**
 * @author Spock
 * SmartRat chooses to keep going the same direction as long as possible.
 */
public class SmartDartRat extends RatBot
{
    Random randy = new Random();
    
    public SmartDartRat()
    {
        setName("SmartDartRat");
        setDirection(randy.nextInt(4)*90);
    }
    
    @Override
    public int chooseAction()
    {     
        int loopCount = 0;
    
        while(!canMove() && loopCount < 10) //Only change directions when needed.
        {
            setDirection(randy.nextInt(360));  //move some Random direction!     
            loopCount++;
        }
        
        return getDirection();
    }
    
}
