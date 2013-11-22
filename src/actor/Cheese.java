package actor;

import java.awt.Color;

/**
 * A <code>Block</code> is an actor that does nothing. It is commonly used to
 * block other actors from moving. <br />
 */

public class Cheese extends RatBotActor
{     
    private static final Color DEFAULT_COLOR = Color.YELLOW;
    private final int CORNER_CHEESE_VALUE = 150;
    private final int DROP_CHEESE_MAX_VALUE = 50;
    private final int START_CHEESE_MAX_VALUE = 10;
    private double pointValue;
    private int maxPointValue;
    private boolean cornerCheese;    

    /**
     * Constructs a black block.
     */
    public Cheese()
    {
        pointValue = 1;
        maxPointValue = START_CHEESE_MAX_VALUE;
        cornerCheese = false;
        setColor();
    }
    
    public Cheese(boolean corner)
    {
        pointValue = 1;
        maxPointValue = DROP_CHEESE_MAX_VALUE;
        cornerCheese = corner;
        if(corner)
        {
            pointValue = CORNER_CHEESE_VALUE;
            maxPointValue = CORNER_CHEESE_VALUE;
        }
        setColor();
    }
    /**
     * Constructs a block that is a copy of another block.
     * @param in the block to be copied.
     */
    public Cheese(Cheese in)
    {
        super(in);
    }

    /**
     * Overrides the <code>act</code> method in the <code>Actor</code> class
     * to do nothing.
     */
    @Override
    public void act()
    {
        if(pointValue < maxPointValue)
            pointValue += 0.1;
        
        setColor();
     }
    
    private void setColor()
    {
        int red = (int)(255.0*pointValue/DROP_CHEESE_MAX_VALUE);
        if(red>255) red = 255;
        if(red<0) red = 0;
        setColor(new Color(red,255,0));        
    }
    
    public boolean isCorner()
    {
        return cornerCheese;
    }
    public int getPointValue()
    {
        return (int)pointValue;
    }
    
    @Override
    public RatBotActor getClone()
    {
        RatBotActor clone = new Cheese(this);
        return clone;
    }

    @Override
    public String toString()
    {
        if(cornerCheese)
            return "CornerCheese worth "+getPointValue();     
        else 
            return "Cheese worth "+getPointValue()+" out of "+maxPointValue;
    }
}
