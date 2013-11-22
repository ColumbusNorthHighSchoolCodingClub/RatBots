package actor;

import java.awt.Color;

/**
 * A <code>Tail</code> is a RatBotActor that darkens over time. 
 * Rats leave a tail as they move. <br />
 */

public class Tail extends RatBotActor
{
    private static final Color DEFAULT_COLOR = Color.PINK;
    private static final double DARKENING_FACTOR = 0.30;
    private static final int LIMIT = 10;
    
    /**
     * Tails that have become 'disconnected' from a Rat are no longer alive.
     */
    private boolean alive;
    /**
     * Tails created by the BlackCat are unlucky.  All unlucky Tails 
     * (alive or destroyed) carry a penalty when run over. (stepped on)
     */
    private boolean unlucky;

 
    /**
     * Constructs a pink tail.
     */
    public Tail()
    {
        setColor(DEFAULT_COLOR);
        alive = true;
        unlucky = false;
    }

    /**
     * Constructs a tail of a given color.
     * @param initialColor the initial color of this tail
     */
    public Tail(Color initialColor)
    {
        setColor(initialColor);
        alive = true;
        unlucky = false;
    }

    /**
     * Constructs a copy of this Tail.
     * @param in the Tail being copied.
     */
    public Tail(Tail in)
    {
        super(in);
        alive = in.isAlive();
        unlucky = in.isUnlucky();
        setColor(in.getColor());
    }
    /**
     * Causes the color of this tail to darken.
     */
    @Override
    public void act()
    {
        if (!alive || unlucky)
            fadeOut();
    }
    
    /**
     * Fades out the color of a destroyed tail until it is nearly the same 
     * color as the arena (light gray) at which point it is removed.
     */   
    public void fadeOut()
    {
        Color c = getColor();
        Color base = Color.LIGHT_GRAY;
        int red = (int) (c.getRed() - (c.getRed()-base.getRed()) * (DARKENING_FACTOR));
        int green = (int) (c.getGreen() - (c.getGreen()-base.getGreen()) * (DARKENING_FACTOR));
        int blue = (int) (c.getBlue() - (c.getBlue()-base.getBlue()) * (DARKENING_FACTOR));

        setColor(new Color(red, green, blue));
        
        if(Math.abs(c.getRed()-base.getRed()) < LIMIT && 
                Math.abs(c.getGreen()-base.getGreen()) < LIMIT && 
                Math.abs(c.getBlue()-base.getBlue()) < LIMIT)
            removeSelfFromGrid();        
    }
    
    /**
     * Once a Tail has been run over, this method is called. 
     * RatBots should never call this method.  
     * (It would be futile, since RatBots only have access to a cloned Grid.)
     */
    public void destroy()
    {
        alive = false;
    }
    /**
     * Responds whether or not this Tail is in the process of fading away.
     * @return true if the Tail is fading away (no longer worth points.)
     */
    public boolean isAlive()
    {
        return alive;
    }
    /**
     * Only BlackCats have unlucky Tails.
     */
    public void makeUnlucky()
    {
        unlucky = true;
    }
    /**
     * This method will return true when the Tail was placed by a BlackCat. 
     * Running over such a tail would cause a penalty :-( 
     * @return true if running over this Tail would cause a penalty.  
     */
    public boolean isUnlucky()
    {
        return unlucky; 
    }
    
    public String toString()
    {
        return "Tail";
    }
    
    @Override
    public RatBotActor getClone()
    {
        RatBotActor clone = new Tail(this);
        return clone;
    }

}
