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
public class ProtoCopyRat extends RatBot{
    
    private int[] actions;
    private int moveNumber;
    
    public ProtoCopyRat(int[] actions){
        this.actions = actions;
        moveNumber = -1;
    }
    
    public int chooseAction(){
        moveNumber++;
        System.out.println(moveNumber);
        return actions[moveNumber];
    }
    
}
