/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Chris Von Hoene
 */

public class RoundData {
    
    public int roundNumber;
    public int rats;
    public String[] names;
    public int[][] actions;
    public int[][] startRatLocs;
    public int[][] cheeseLocs;
    public boolean[][][] walls;    
    
    public ArrayList<Integer> randoms;
    
    public RoundData(){
        cheeseLocs = new int[10][2];
        walls = new boolean[20][20][4];
        for(int row = 0; row<walls.length; row++){
            for(int col = 0; col < walls[0].length; col++){
                    Arrays.fill(walls[row][col], false);
            }            
        }
        randoms = new ArrayList();
    }
    
}
