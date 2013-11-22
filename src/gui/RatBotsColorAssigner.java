package gui;

import java.awt.Color;

/**
 * The ColorAssigner class is a utility that keeps track of colors used in 
 * the game.  
 * @author Spock
 */
public class RatBotsColorAssigner 
{
    private static int count = 0;
    
    /**
     * This method assigns a new color every time it is asked.  
     * Each time a RatBot is added it is assigned a color.  
     * @return the next color on the list.  
     */
    public static Color getAssignedColor()
    {
        count++;
        switch(count%12) { // One more than count to include Grey.
            case 1: return new Color(239, 16, 5);       // Bright Red
            case 2: return new Color(0, 98, 209);       // Bright Blue
            case 3: return new Color(98, 154, 64);      // Green
            case 4: return new Color(252, 231, 59);     // Yellow
            case 5: return new Color(124, 45, 155);     // Purple
            case 6: return new Color(219, 153, 36);    // Light Orange  
            case 7: return new Color(45, 42, 145);      // Navy Blue
            case 8: return new Color(123,234,45);      // Lime Green
            case 9: return new Color(0, 143, 178);      // Cyan
            case 10: return new Color(161, 33, 78);      // Mauve
            case 11: return new Color(249, 74, 15);      // Orange
        }
        return Color.GRAY;                              // GRAY

        //        switch(count%16)
//        {
//            case 1: return Color.RED;
//            case 2: return Color.BLUE;
//            case 3: return Color.GREEN;
//            case 4: return Color.MAGENTA;
//            case 5: return Color.PINK;
//            case 6: return Color.CYAN;
//            case 7: return Color.ORANGE;
//            case 8: return Color.WHITE;
//            case 9: return new Color(255,100,100);
//            case 10: return new Color(128,50,90);
//            case 11: return new Color(50,120,190);
//            case 12: return new Color(0,200,100);
//                    
//        }
//        return new Color(123,234,45);
    }
    
    public static void reset(){
        count = 0;
    }
}
