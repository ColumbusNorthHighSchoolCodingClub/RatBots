package ratbot;

import actor.RatBot;
import java.util.Random;
/**
 * @author Spock
 * SmartRandomRat chooses to keep going the same direction as long as possible.
 */
public class SmartRandomRat extends RatBot
{
    Random randy = new Random();
    
    public SmartRandomRat()
    {
        setName("SmartRandomRat");
    }
    
    @Override
    public int chooseAction()
    {     
        int loopCount = 0;
    
        do
        {
            setDirection(randy.nextInt(360));  //move some Random direction!     
//            System.out.println("LoopCount: "+loopCount+" "+getDirection()+" "+canMove());
            loopCount++;
        } while(!canMove() && loopCount < 10);
             
        return getDirection();  
    }
    
}
