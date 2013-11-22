package ratbot;

import actor.RatBot;
import java.util.Random;
/**
 * @author Spock
 * DartRat chooses to keep going the same direction as long as possible.
 */
public class DartRat extends RatBot
{
    Random randy = new Random();
    
    public DartRat()
    {
        setName("DartRat");
        setDirection(randy.nextInt(4)*90);
    }
    
    @Override
    public int chooseAction()
    {     
//        System.out.println("Location: "+getLocation()+"Direction"+getDirection()+"Can Move:"+canMove());
        if(!canMove()) //Only change directions when needed.
//            return randy.nextInt(4)*90;  //move some Random direction!     
            return randy.nextInt(360);  //move some Random direction!     
        else
            return getDirection();
    }
    
}
