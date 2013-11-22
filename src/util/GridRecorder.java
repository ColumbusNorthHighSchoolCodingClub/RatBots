/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import actor.Cheese;
import actor.Rat;
import grid.RatBotsGrid;
import actor.RatBotActor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

/*
 * @author Chris Von Hoene
 */
public class GridRecorder {

    private RatBotsGrid<RatBotActor> grid;
    private ArrayList<int[][]> ratActions = new ArrayList();  //[round][rats][actions]
    private ArrayList<int[][]> startRatLocs = new ArrayList(); //[round][start rat x][start rat y]
    private ArrayList<int[][]> startCheeseLocs = new ArrayList();  //[round][start cheese x][start cheese y]
    private ArrayList<String> record = new ArrayList();
    private ArrayList<String> old1 = new ArrayList();
    private ArrayList<String> old2 = new ArrayList();
    private ArrayList<String> old3 = new ArrayList();
    
    private ArrayList<Integer> randoms = new ArrayList();
    
    private int[][] actions;
    private int[][] cheeseLocs;
    private int[][] ratLocs;
    private boolean[][][] walls;
    private int moveNum;
    private int roundsSaved = 0;
    private int matchNum = 0;
    private int roundsInAMatch = 100;
    private File matchFile;
    private FileWriter fw;
    private BufferedWriter bw;

    public int[][] getActions() {return actions;}
    public void setActions(int[][] in) {actions = in;}
    
    public int getRoundsInAMatch(){return roundsInAMatch;}
    public void setRoundsInAMatch(int in){roundsInAMatch = in;}
    
    public int[][] getCheeseLocs(){return cheeseLocs;}
    
    public boolean[][][]getWalls(){return walls;}

    public void setWalls(boolean[][][] in) {walls = in;}
    
    public void addRandom(int in){randoms.add(in);}
    
    //modifies the location and action arrays to accomodate an additional rat
    public void increaseRatArraySize(){
        System.out.println("Ratlocs length: "+ratLocs.length);
        System.out.println("Actions length: "+actions.length);
        System.out.println("Ratlocs 0 length: "+ratLocs[0].length);
        if(actions.length>0) System.out.println("Actions 0 length: "+actions[0].length);
        int[][] newRatLocs = new int[ratLocs.length+1][2];
        int[][] newActions = new int[actions.length+1][500];
        for(int x = 0; x < ratLocs.length; x++){
            for(int y = 0; y < ratLocs[0].length; y++){
                newRatLocs[x][y] = ratLocs[x][y];
            }
            for(int y2 = 0; y2 < actions[0].length; y2++){
                newActions[x][y2] = actions[x][y2];
            }
        }
        actions = newActions;
        ratLocs = newRatLocs;
    }
    
    //modifies the action and location arrays to get rid of an additional rat
    //never used and not updated to preserve original array contents
    public void decreaseRatArraySize(int num){
        actions = new int[actions.length-1][2];
        ratLocs = new int[ratLocs.length-1][num];
    }

    public BufferedWriter getBufferedWriter() {
        return bw;
    }
    
    public int getMoveNum(){return moveNum;}
    
    public ArrayList<String> getRecord(){return record;}
    
    public int[][] getRatLocs(){return ratLocs;}

    public static void main(String args[]) { //written to test
        GridRecorder g = new GridRecorder(new RatBotsGrid<RatBotActor>(24, 24));
        g.testMySkill();
    }

    public GridRecorder(RatBotsGrid<RatBotActor> g) {    //create one of these with every grid
        grid = g;
        moveNum = 0;
        initForRound();
        System.out.println("New grid recorder created.              GridRecorder");
    }

    public void takeSnapshot() { //call this method every move--records the actions of each rat in alphabetical order
        moveNum++;
        if (moveNum <= 500) {
            for (int index = 0; index < grid.getAllRats().size(); index++) {
                actions[index][moveNum - 1] = grid.getAllRatsByName().get(index).getLastAction();
            }
        }
    }

    public void setForNewRound(int roundNumber) {   //call this method AFTER each round
        if (roundNumber == 1) {
            return;
        }
        ratActions.add(actions.clone());
        saveRound();
        actions = new int[grid.getAllRats().size()][500];
        moveNum = 0;
    }

    public void saveRound() {   //updates the class records of previous rounds
        addRoundToArrayList(record);
        old3 = (ArrayList<String>) old2.clone();
        old2 = (ArrayList<String>) old1.clone();
        old1 = new ArrayList();
        addRoundToArrayList(old1);
        roundsSaved++;

        if (roundsSaved % roundsInAMatch == 0) { //saves the rounds to a match file every x rounds
            createNewMatchFile();
        }
    }

    public void addRoundToArrayList(ArrayList<String> list) {   //adds the round data, in the form of a bunch of strings, to an array list that serves as the record
        list.add("Round #: " + (roundsSaved + 1));
        list.add("Number of rats: " + grid.getAllRats().size());
        ArrayList<Rat> r = grid.getAllRatsByName();
        for (int rat = 0; rat < r.size(); rat++) {
            list.add("Ratbot #" + (rat + 1) + ": " +r.get(rat).getRatBot().getName()+" "+ ratLocs[rat][0] + ", " + ratLocs[rat][1]);
            for (int move = 0; move < actions[0].length; move++) {
                list.add(actions[rat][move]+",");
            }
        }
        list.add("Start cheese locations: ");
        for (int cheese = 0; cheese < cheeseLocs.length; cheese++) {
            list.add(cheeseLocs[cheese][0] + "," + cheeseLocs[cheese][1] + " ");
        }
        list.add("Walls: ");
        for (int row = 0; row < walls.length; row++) {
            for (int col = 0; col < walls[0].length; col++) {
                for (int side = 0; side < walls[0][0].length; side++) {
                    if (walls[row][col][side]) {
                        list.add(row + ", " + col + ", " + side);
                    }
                }
            }
        }
        list.add("Random numbers: ");
        for(int index = 0; index < randoms.size(); index++){
            list.add(randoms.get(index)+"");
        }
        list.add("End of round");
    }
    
    public void createNewMatchFile() {  //saves the last x rounds in a txt file
        try {
            matchNum++;
            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);
            File matFile = new File("D:/match" + matchNum + " " + day + "_" + hour + "_" + minute + "_" + second + ".txt");
            if (matFile.exists()) {
                matFile.createNewFile();
            }
            fw = new FileWriter(matFile);
            bw = new BufferedWriter(fw);
            for (int index = 0; index < record.size(); index++) {
                bw.write(record.get(index) + ' ');
                bw.newLine();
            }            
            bw.close();

            System.out.println("Match file saved.");

        } catch (IOException e) {
            System.out.println("The recorder failed to create a new match file");
            System.out.println(e);
        }
        record = new ArrayList();
    }
    
    public void initForRound(){ //resets everything for a new round
        actions = new int[grid.getAllRats().size()][500];
        getStartCheesePositions();
        getStartRatPositions();
        setWalls(grid.getWalls());
        randoms = new ArrayList();
    }

    public void getStartCheesePositions() {  //call this method at the beginning of each round
        ArrayList<Cheese> cheeses = grid.getLittleCheeses();
        cheeseLocs = new int[cheeses.size()][2];
        for (int index = 0; index < cheeses.size(); index++) {
            cheeseLocs[index][0] = cheeses.get(index).getLocation().getRow();
            cheeseLocs[index][1] = cheeses.get(index).getLocation().getCol();
        }
        startCheeseLocs.add(cheeseLocs.clone());
    }

    public void getStartRatPositions() {    //gets the locations of the rats at round 0 in alphabetical order
        ArrayList<Rat> rats = grid.getAllRatsByName();
        ratLocs = new int[rats.size()][2];
        for (int index = 0; index < rats.size(); index++) {
            int col = rats.get(index).getLocation().getCol();
            int row = rats.get(index).getLocation().getRow();
            ratLocs[index][0] = row;
            ratLocs[index][1] = col;
            System.out.println(row+", "+col+"                 GridRecorder");
        }
        startRatLocs.add(ratLocs.clone());
        
        System.out.println("Start rat positions recorded.                   GridRecorder");
    }

    public void testMySkill() {
        try {
            String tString = "HAHAHA IT WORKS";
            File tFile = new File("D:/testFile.txt");
            if (!tFile.exists()) {
                tFile.createNewFile();
            }

            FileWriter Filew = new FileWriter(tFile.getAbsoluteFile());
            BufferedWriter Bufferedw = new BufferedWriter(Filew);
            Bufferedw.write(tString);
            Bufferedw.close();

        } catch (IOException exception) {
            System.out.println("Input/output exception: " + exception);
        }
    }   
    

    //pretty sure that the next three methods are garbage
    //holding on to them just in case
    public void saveRoundToMatch() {    //currently junk
        try {
            System.out.println("Saved.");

//            File matchFile = new File("D:/match" + matchNum + ".txt");
//            FileWriter fw = new FileWriter(matchFile.getAbsoluteFile());
//            bw = new BufferedWriter(fw);

            PrintWriter pw = new PrintWriter("D:/match" + matchNum + ".txt");



            pw.write("Round #: " + (roundsSaved + 1));
            pw.println();
            pw.write("Number of rats: " + grid.getAllRats().size());
            pw.println();
            for (int rat = 0; rat < grid.getAllRats().size(); rat++) {
                pw.write("Ratbot #" + (rat + 1) + ":");
                for (int move = 0; move < actions[0].length; move++) {
                    pw.write(" " + actions[rat][move]);
                }
                pw.println();
            }
            pw.write("Start cheese locations: ");
            for (int cheese = 0; cheese < cheeseLocs.length; cheese++) {
                pw.write(cheeseLocs[cheese][0] + "," + cheeseLocs[cheese][1] + " ");
            }
            pw.println();

            pw.close();

            roundsSaved++;

            if (roundsSaved % 10 == 0) {
                createNewMatchFile();
            }

        } catch (IOException exception) {
            System.out.println("The round was not properly saved to the match.");
            System.out.println(exception);
        }
    }

    public void saveRoundToMatchTwo() throws IOException {  //currently junk
        bw = new BufferedWriter(fw);

        bw.write("Round #: " + (roundsSaved + 1));
        bw.newLine();
        bw.write("Number of rats: " + grid.getAllRats().size());
        bw.newLine();
        for (int rat = 0; rat < grid.getAllRats().size(); rat++) {
            bw.write("Ratbot #" + (rat + 1) + ":");
            for (int move = 0; move < actions[0].length; move++) {
                bw.write(" " + actions[rat][move]);
            }
            bw.newLine();
        }
        bw.write("Start cheese locations: ");
        for (int cheese = 0; cheese < cheeseLocs.length; cheese++) {
            bw.write(cheeseLocs[cheese][0] + "," + cheeseLocs[cheese][1] + " ");
        }
        bw.newLine();

        roundsSaved++;

        if (roundsSaved % 10 == 0) {
            createNewMatchFile();
        }

    }

    public void saveOneRound() {    //currently junk
        try {
            File roundFile = new File("D:/round" + (roundsSaved + 1) + ".txt");
            roundsSaved++;
            if (!roundFile.exists()) {
                roundFile.createNewFile();
            }
            FileWriter phil = new FileWriter(roundFile.getAbsoluteFile());
            BufferedWriter bill = new BufferedWriter(phil);
            bill.write("Round #: " + (roundsSaved));
            bill.newLine();
            bill.write("Number of rats: " + grid.getAllRats().size());
            bill.newLine();
            for (int rat = 0; rat < grid.getAllRats().size(); rat++) {
                bill.write("Ratbot #" + (rat + 1) + ":");
                for (int move = 0; move < actions[0].length; move++) {
                    bill.write(" " + actions[rat][move]);
                }
                bill.newLine();
            }
            bill.write("Start cheese locations: ");
            for (int cheese = 0; cheese < cheeseLocs.length; cheese++) {
                bill.write(cheeseLocs[cheese][0] + "," + cheeseLocs[cheese][1] + " ");
            }
            bill.newLine();
            bill.close();
        } catch (IOException exception) {
            System.out.println("The single round failed to save.");
            System.out.println(exception);
        }
    }
}
