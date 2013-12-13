package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import world.RatBotWorld;
import actor.Rat;
import actor.RatBot;

public class RatBotsScoreCard {
	
	private RatBot ratbot;
	private Rat rat;
	
	public RatBotsScoreCard(Rat rat) {
		
		this.rat = rat;
		this.ratbot = rat.getRatBot();
	}
	
	public void draw(Graphics g, int y) {
			
		String name = ratbot.getName(),
			   roundPoints = "Current Score: " + rat.getScore(),
			   totalPoints = "Total Score: " + rat.getTotalScore(),
			   roundsWon = "Rounds Won: " + rat.getRoundsWon(),
			   averPoints = "Average Score: " +  (int) ((double) rat.getTotalScore() / (double) RatBotWorld.getRoundNum());
	
		g.setColor(rat.getColor());
		g.fillRect(5, y, 365, 70);
		     		
		g.setFont(new Font("Helvetica", 3, 16));
		Rectangle2D rect = g.getFontMetrics().getStringBounds(name, g);
		
		g.setColor(rat.getColor().darker().darker());
		g.fillRect(5,  y + ((int) rect.getHeight()/4) - 1, 365, (int) rect.getHeight() + 2);
		
		g.setColor(invertColor(rat.getColor()));
		g.drawString(name, ((365 / 2) - (int) (rect.getWidth() / 2)), (int) (y + rect.getHeight()));

		y += 40;
		
		g.setFont(new Font("Helvetica", Font.BOLD, 14));
		g.drawString(roundPoints, 7, y);
	
		rect = g.getFontMetrics().getStringBounds(averPoints, g);
		g.drawString(averPoints, 348 - (int) rect.getWidth(), y);
		
		y += 2;
		
		g.setFont(new Font("Helvetica", Font.BOLD, 15));
		rect = g.getFontMetrics().getStringBounds(totalPoints, g);
		
		g.setColor(rat.getColor().darker());
	    g.fillRect(5,  y + ((int) rect.getHeight()/4) - 1, 365, (int) rect.getHeight() + 2);
	    	
	    g.setColor(invertColor(rat.getColor()));
	    g.drawString(totalPoints, 7, (int) (y + rect.getHeight()));
		
	    rect = g.getFontMetrics().getStringBounds(roundsWon, g);
	    g.drawString(roundsWon, 348 - (int) rect.getWidth(),(int) (y + rect.getHeight()));
	}
	
	public void drawSmall(Graphics g, int y) {
		
		String name = ratbot.getName(),
			   roundPoints = "Won: "+ rat.getRoundsWon() + " | Score: " + rat.getScore(),
			   totalPoints = "Avg: "+ ((int) ((double) rat.getTotalScore() / (double) RatBotWorld.getRoundNum())) +" | Total: " + rat.getTotalScore();
	
		g.setColor(rat.getColor());
		g.fillRect(5, y, 365, 46);
		     		
		g.setFont(new Font("Helvetica", 3, 16));
		Rectangle2D rect = g.getFontMetrics().getStringBounds(name, g);
		
		g.setColor(rat.getColor().darker().darker());
		g.fillRect(5,  y + ((int) rect.getHeight()/4) - 1, 365, (int) rect.getHeight() + 2);
		
		g.setColor(invertColor(rat.getColor()));
		g.drawString(name, ((365 / 2) - (int) (rect.getWidth() / 2)), (int) (y + rect.getHeight()));

		rect = g.getFontMetrics().getStringBounds(totalPoints, g);
				
		y += 40;
		
		g.setColor(rat.getColor().darker());
	    g.fillRect(5, y - (int) rect.getHeight() + 6, 365, (int) rect.getHeight() - 4);
		
	    y -= 1;
	    
	    g.setColor(invertColor(rat.getColor()));
		g.setFont(new Font("Helvetica", Font.BOLD, 14));
		g.drawString(roundPoints, 7, y);
	
		rect = g.getFontMetrics().getStringBounds(totalPoints, g);
		g.drawString(totalPoints, 348 - (int) rect.getWidth(), y);
		
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
