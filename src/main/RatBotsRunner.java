package main;

import util.PAGuiUtil;
import util.RatBotManager;
import world.RatBotWorld;

public class RatBotsRunner
{
    public static void main(String[] args)
    {
        PAGuiUtil.setLookAndFeelToOperatingSystemLookAndFeel();
        
        RatBotWorld world = new RatBotWorld();
        world.show();  //Just so that they see something while rats are loading.
        
        // Load RatBots from the 'ratbot' package
        RatBotManager.loadRatBotsFromClasspath("ratbot", world);
        
//        //This is another place where you can add RatBots to the match.  
//        world.add(new RandomRat());

        world.show();
    }
}
