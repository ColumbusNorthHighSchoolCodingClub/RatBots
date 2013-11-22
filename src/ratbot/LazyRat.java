package ratbot;

import actor.RatBot;
import java.util.Random;
/**
 * @author Spock
 * LazyRat chooses a random move each turn, and rests after 100 turns.
 */
public class LazyRat extends RatBot
{
    Random randy = new Random();
    private int moveNum;
    
    public LazyRat()
    {
        setName("LazyRat");
        moveNum = 0;
    }
    
    @Override
    public int chooseAction()
    {        
        moveNum++;
        if(moveNum > 350)
            return REST;
        
        return randy.nextInt(360);  //move some Random direction!
    }
    
    public void initForRound()
    {
        moveNum = 0;
    }
}
