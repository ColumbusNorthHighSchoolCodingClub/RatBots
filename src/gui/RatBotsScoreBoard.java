package gui;

import grid.Grid;
import grid.Location;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import world.RatBotWorld;
import actor.Rat;
import actor.RatBotActor;

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
    
    private int sortBy = POINTS;
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
		
        g.setFont(g.getFont().deriveFont((float) 18));
        g.drawString("Round # "+RatBotWorld.getRoundNum()+"   Move # "+RatBotWorld.getMoveNum(), X_OFFSET, 30);

        ArrayList<RatBotsScoreCard> cards = new ArrayList<RatBotsScoreCard>();
        
        for(Rat rat : sortRats()) cards.add(new RatBotsScoreCard(rat));
        
        int height = 40,
        	spacing = 72;
        
        if(cards.size() <= 7)
	        for(RatBotsScoreCard card: cards) {
	        	
	        	card.draw(g2, height);
	        	height += spacing;
	        }
        else {
        	
        	spacing = 48;
        	for(RatBotsScoreCard card: cards) {
	        	
	        	card.drawSmall(g2, height);
	        	height += spacing;
	        }
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
        return new Dimension(350,400);
    }

    /**
     * Returns the minimum size of the display, for use by layout manager.
     * @return minimum size
     */
    public Dimension getMinimumSize()
    {
        return new Dimension(350,150);
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
}
