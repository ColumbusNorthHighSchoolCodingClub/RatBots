/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ratbot;

import actor.RatBot;

/**
 *
 * @author Chris Von Hoene
 */
public class CopyRat extends RatBot{
    
    private int[][] actions;
    private int moveNumber;
    private int roundNumber;
    
    public CopyRat(int[][] actions){
        this.actions = actions;
        moveNumber = -1;
        roundNumber = 0;
    }
    
    @Override
    public int chooseAction(){
        moveNumber++;
        if(moveNumber==499){
            if(roundNumber==99){
                return 0;
            }
            moveNumber = -1;
            roundNumber++;
            return actions[roundNumber-1][499];
        }
        
//        System.out.println(moveNumber);
        
        return actions[roundNumber][moveNumber];
    }
    
}
