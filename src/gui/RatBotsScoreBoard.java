package gui;

import actor.RatBotActor;
import actor.Rat;
import grid.Grid;
import grid.Location;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.FontMetrics;
import java.util.ArrayList;
import javax.swing.JPanel;
import world.RatBotWorld;

/**
 *
 * @authors Spock, Cox, Angle
 */
public class RatBotsScoreBoard extends JPanel
{
    private ArrayList<Rat> rats = new ArrayList<Rat>(); 
    private static int maxScore = 0;
    
    //sorting variables
    public static final int POINTS = 0;
    public static final int TOTAL_POINTS = 1;
    public static final int ROUNDS_WON = 2;
    public static final int NAME = 3;
    
    private int sortBy = TOTAL_POINTS;
    private Grid<RatBotActor> theGrid; 
    
    public RatBotsScoreBoard(Grid gr)
    {
        theGrid = gr;
    }
    
    public void findRatsInGrid()
    {
        rats.clear();     
        ArrayList<Location> occupied = theGrid.getOccupiedLocations();
        
        for(Location loc : occupied)
        {
            if(theGrid.get(loc) instanceof Rat)
                rats.add((Rat)theGrid.get(loc));
        }
    }
    
//    public static void add(Rat in)
//    {
//        rats.add(in);
//    }
//    public static void remove(Rat in)
//    {
//        rats.remove(in);
//    }
   
    public static int X_OFFSET = 20;
    public static int ROW_SIZE = 55; 
    
    /**
     * Paint this component.
     * @param g the Graphics object to use to render this component
     */
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        findRatsInGrid();
        
        super.paintComponent(g2);
        
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g.setFont(g.getFont().deriveFont((float) 16));
        g.drawString("Round: "+RatBotWorld.getRoundNum()+"   Moves Completed: "+RatBotWorld.getMoveNum(), X_OFFSET, 30);

        ArrayList<Rat> sorted = sortRats();
        
        for(int rb=0; rb<rats.size(); rb++)
        {
            Rat r = sorted.get(rb);
            int w = ROW_SIZE*2;
            int h = ROW_SIZE-5;

            g.setFont(new Font("Helvetica", Font.PLAIN, 10));
            
            //draw rectangle
            g.setColor(r.getColor());
            g.fillRect(X_OFFSET, 50 + ROW_SIZE*rb, 300, ROW_SIZE - 2);

            //write text info (score, RW, etc) to screen
            g.setColor(invertColor(r.getColor()));
            String roundPoints = String.valueOf(r.getScore());
            String totalPoints = String.valueOf(r.getTotalScore());
            String roundsWon = String.valueOf(r.getRoundsWon());
            String name = r.getRatBot().getName();
            
            //Round Points
            if(sortBy == POINTS) g.setFont(new Font("Helvetica", Font.PLAIN, 20));
            else g.setFont(new Font("Helvetica", Font.BOLD, 20));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(roundPoints, X_OFFSET + 40 - fm.stringWidth(roundPoints),
                    50 + ROW_SIZE*rb + 28);
            g.setFont(new Font("Helvetica", Font.PLAIN, 12));
            fm = g.getFontMetrics();
            g.drawString("Score", X_OFFSET + 40 - fm.stringWidth("Score"), 
                    50 + ROW_SIZE*rb + h/2 + 20);
            
            //Total Points
            if(sortBy == TOTAL_POINTS) g.setFont(new Font("Helvetica", Font.PLAIN, 20));
            else g.setFont(new Font("Helvetica", Font.BOLD, 20));
            fm = g.getFontMetrics();
            g.drawString(totalPoints, X_OFFSET + 110 - fm.stringWidth(totalPoints),
                    50 + ROW_SIZE*rb + 28);
            g.setFont(new Font("Helvetica", Font.PLAIN, 12));
            fm = g.getFontMetrics();
            g.drawString("Total", X_OFFSET + 110 - fm.stringWidth("Total"), 
                    50 + ROW_SIZE*rb + h/2 + 20);
            
            //Rounds Won
            if(sortBy == ROUNDS_WON) g.setFont(new Font("Helvetica", Font.PLAIN, 30));
            else g.setFont(new Font("Helvetica", Font.BOLD, 30));
            fm = g.getFontMetrics();
            g.drawString(roundsWon, X_OFFSET + 160 - fm.stringWidth(roundsWon),
                    50 + ROW_SIZE*rb + 38);           
            
            g.setFont(new Font("Helvetica", Font.PLAIN, 20));
            g.drawString(r.getRatBot().getName(),X_OFFSET + w + 55, 50 + ROW_SIZE*rb + 20);
            
        }
        calcMaxScore();
    }
    
    public void calcMaxScore() 
    {
        maxScore = -5000;
        for(Rat r : rats)
        {
            if(r.getScore() > maxScore)
                maxScore = r.getScore();
        }
    }
    
    public static int getMaxScore() { return maxScore; }
    
    /**
     * Returns the desired size of the display, for use by layout manager.
     * @return preferred size
     */
    public Dimension getPreferredSize()
    {
        return new Dimension(300,400);
    }

    /**
     * Returns the minimum size of the display, for use by layout manager.
     * @return minimum size
     */
    public Dimension getMinimumSize()
    {
        return new Dimension(300,150);
    }
    
    public void setSortOrder(int so){
        if(so <= 4 && so >= 0) sortBy = so;
        else sortBy = 0;
    }
    
    private ArrayList<Rat> sortRats(){
        ArrayList<Rat> sorted = new ArrayList();
        if(rats.size() == 0) 
            return sorted;
        
        if(sortBy == NAME){
            ArrayList<String> origNames = new ArrayList();
            for(int i = 0; i < rats.size(); i++) 
                origNames.add(rats.get(i).getRatBot().getName());
            
            ArrayList<String> sortedNames = sortAlphabetically(origNames);
            for(int i = 0; i < sortedNames.size(); i++){
                int n = origNames.indexOf(sortedNames.get(i));
                sorted.add(rats.get(n));
                origNames.remove(n);
                origNames.add(n, null);
            }
        } else {
            int val1 = 0;
            int val2 = 0;
            sorted.add(rats.get(0));
            
            for(int i = 1; i < rats.size(); i++){
                if(sortBy == POINTS){
                    val1 = rats.get(i).getScore();
                } if(sortBy == TOTAL_POINTS){
                    val1 = rats.get(i).getTotalScore();
                } if(sortBy == ROUNDS_WON){
                    val1 = rats.get(i).getRoundsWon();
                } 
                for(int k = 0; k < sorted.size(); k++){
                    if(sortBy == POINTS){
                        val2 = sorted.get(k).getScore();
                    } if(sortBy == TOTAL_POINTS){
                        val2 = sorted.get(k).getTotalScore();
                    } if(sortBy == ROUNDS_WON){
                        val2 = sorted.get(k).getRoundsWon();
                    } 
                    
                    if(val1 > val2){ 
                        sorted.add(k, rats.get(i));
                        break;
                    }
                }
                
                if(!sorted.contains(rats.get(i))) sorted.add(rats.get(i));
            }
        }
        
        return sorted;
    }
    
    private ArrayList<String> sortAlphabetically(ArrayList<String> s){
        ArrayList<String> sorted = new ArrayList();
        sorted.add(s.get(0));
        for(int i = 1; i < s.size(); i++){
            for(int k = 0; k < sorted.size(); k++){
                if(sorted.get(k).compareTo(s.get(i)) > 0 || 
                        sorted.get(k).compareTo(s.get(i)) == 0){
                    sorted.add(k, s.get(i));
                    break;
                }
            }
            
            if(!sorted.contains(s.get(i))) sorted.add(s.get(i));
        }
        
        return sorted;
    }

    private Color invertColor(Color in)
    {
        float[] vals = new float[3];
        
        Color.RGBtoHSB(in.getRed(), in.getGreen(), in.getBlue(), vals);
        
        if(vals[2] < 0.85)
            return Color.WHITE;
            
        //otherwise return the inverse of the color.    
        return new Color(255-in.getRed(), 255-in.getGreen(), 255-in.getBlue());
    }
}
