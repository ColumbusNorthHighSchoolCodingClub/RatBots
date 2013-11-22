
package world;

import actor.Cheese;
import actor.Rat;
import actor.RatBot;
import actor.RatBotActor;
import actor.Tail;
import grid.Grid;
import grid.Location;
import grid.RatBotsGrid;
import gui.RatBotsArena;
import gui.RatBotsColorAssigner;
import gui.RatBotsScoreBoard;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import util.GridRecorder;
import java.util.ArrayList;
import java.util.Random;
import ratbot.CopyRat;
import util.RoundData;

/**
 * A RatBotWorld is full of RatBotActors used in the game RatBots.  
 * @author Spock
 */
public class RatBotWorld extends ActorWorld
{
    /**
     * The number of moves in a round of RatBots competition.
     */
    public static final int NUM_MOVES_IN_ROUND = 500;
    /**
     * The number of rounds in a match of RatBots competition.  
     */
    public static final int NUM_ROUNDS_IN_MATCH = 100;

    private static final String DEFAULT_MESSAGE = "RatBots is awesome.";
    private static Random randy = new Random();
    private File matchFile = null;

    private static int moveNum = 0;
    private static int roundNum = 1;    //changed from 0 to 1 by CVH
    
    private boolean roundRobin = false;
    private int rr1=0; 
    private int rr2=0;
    private boolean matchReady = false;   
    private boolean isRecording = false;
    private boolean isReplaying = false;
    
    
    private RatBotsArena arena = new RatBotsArena();
    
    private ArrayList<Rat> ratsInMaze = new ArrayList();
    private ArrayList<Rat> allrats = new ArrayList();
    private ArrayList<RoundData> rounds = new ArrayList();
    
    private GridRecorder recorder = null;
    
    public void setMatchFile(File in){matchFile = in; convertMatchFile();}
    
    public void setIsRecording(boolean in){
        isRecording = in;
        if(isRecording){
            System.out.println("recorder activated              RatBotWorld");
            recorder = new GridRecorder((RatBotsGrid<RatBotActor>)getGrid());
            recorder.setRoundsInAMatch(NUM_ROUNDS_IN_MATCH);
//            for(Rat r:ratsInMaze){
//                recorder.increaseRatArraySize(NUM_MOVES_IN_ROUND);
//            }
        }
    }
    
    public void setIsReplaying(boolean in){isReplaying=in;}
    
    
    /**
     * Constructs a RatBot world with a default grid.
     */
    public RatBotWorld()
    {
        initializeGridForRound();
        initializeMatch();
    }

    /**
     * Constructs a RatBot world with a given grid.
     * @param grid the grid for this world.
     */
    public RatBotWorld(Grid<RatBotActor> grid)
    {
        super(grid);
        initializeGridForRound();
        initializeMatch();
    }

    /**
     * gets the Arena used in this World.
     * @return the Arena
     */
    public RatBotsArena getArena() { return arena; }
    /**
     * Gets the current move number in the round being played.  
     * @return the move number in the current round.
     */
    public static int getMoveNum() { return moveNum; }
    /**
     * Gets the current round number in the match being played.  
     * @return the round number in the current match.
     */
    public static int getRoundNum() { return roundNum; }
    
    public GridRecorder getGridRecorder(){return recorder;}
    
    private void initializeMatch()
    {
//        System.out.println("INITIALIZING MATCH");
        moveNum = 0;
        roundNum = 1;   //changed from 0 to 1 by CVH
        matchReady = true;
        
        if(roundRobin)
        {
            initializeRoundRobinMatch();
        }
        initializeGridForRound();
    }
    
    private void initializeRoundRobinMatch()
    {
        if(ratsInMaze.size() > 1)
        {
            System.out.println(ratsInMaze.get(0).getRatBot().getName()+","+ratsInMaze.get(0).getRoundsWon()
                    +","+  ratsInMaze.get(1).getRatBot().getName()+","+ratsInMaze.get(1).getRoundsWon() );
//                        +"      time(sec)="+(int)((System.currentTimeMillis() - matchstart)/1000));
            if(ratsInMaze.get(0).getRoundsWon()+ratsInMaze.get(1).getRoundsWon()<100)
                System.out.println("Incomplete match...???");
            //score match
            if(ratsInMaze.get(0).getRoundsWon()>ratsInMaze.get(1).getRoundsWon())
            {
                ratsInMaze.get(0).increaseMatchesWon();
                ratsInMaze.get(1).increaseMatchesLost();
            }
            if(ratsInMaze.get(0).getRoundsWon()==ratsInMaze.get(1).getRoundsWon())
            {
                ratsInMaze.get(0).increaseMatchesTied();
                ratsInMaze.get(1).increaseMatchesTied();
            }
            if(ratsInMaze.get(0).getRoundsWon()<ratsInMaze.get(1).getRoundsWon())
            {
                ratsInMaze.get(1).increaseMatchesWon();
                ratsInMaze.get(0).increaseMatchesLost();
            }
            ratsInMaze.get(0).setRoundsWon(0);
            ratsInMaze.get(1).setRoundsWon(0);

        }
        ratsInMaze.clear(); 
        rr2++;
        if(rr2==allrats.size())
        {
            rr1++;
            rr2=rr1+1;
            if(rr1==allrats.size()-1)
            {
                for(Rat x : allrats)
                {
                    System.out.println(x.getRatBot().getName()+
                            ",  TP=,"+x.getTotalScore() +
                            ",  w=,"+x.getMatchesWon()+
                            ",  t=,"+x.getMatchesTied()+
                            ",  l=,"+x.getMatchesLost()                              
                            );
                }
                System.out.println("TOURNEY COMPLETE");

            }
        }
        ratsInMaze.add(allrats.get(rr1));   //11,8  10,8    10,7
        ratsInMaze.add(allrats.get(rr2));
        
    }
    
    
    /**
     * Initialize the arena and each of the RatBots for a round of competition.
     */
    public final void initializeGridForRound()
    {
        clearAllObjectsFromGrid();
        arena.initializeArena(this);
        for(Rat r : ratsInMaze) 
        {
            r.initialize();
            r.putSelfInGrid(getGrid(), getRandomEmptyCenterLocation());
            r.setDirection(getRandomDirection());
        }
        moveNum = 0;
        
        if(isRecording){
            recorder.initForRound();
        }
    }
    
    public void convertMatchFile(){
        try{
            FileReader f = new FileReader(matchFile);
            BufferedReader br = new BufferedReader(f);
            String line;
            while((line=br.readLine())!=null){
                if(line.contains("Round #:")){
                    readRound(br);
                }
            }
            br.close();
            f.close();
            
        }catch(IOException e){
            System.out.println(e);
        }
    }
    
    public void beginReplay(int roundNumber){
        setIsReplaying(true);
        RoundData round = rounds.get(roundNumber-1);        
        ratsInMaze = new ArrayList();
        RatBotsColorAssigner.reset();
        for(int index = 0; index < round.rats; index++){   
            int[][] actions = new int[NUM_ROUNDS_IN_MATCH][NUM_MOVES_IN_ROUND];
            for(int r = 0; r < NUM_ROUNDS_IN_MATCH; r++){
                actions[r] = rounds.get(r).actions[index];
            }
            Rat r = new Rat(new CopyRat(actions),RatBotsColorAssigner.getAssignedColor());
            r.getRatBot().setName(round.names[index]);
            allrats.add(r);
            ratsInMaze.add(r);
        }
        loadRecordedRound(roundNumber);
    }
    
    public void loadRecordedRound(int roundNumber){
        if(matchFile==null) return;
        clearAllObjectsFromGrid();
        RoundData round = rounds.get(roundNumber-1);
        arena.initializeRecordedArena(this, round);
//        RatBotsColorAssigner.reset();
//        for(int index = 0; index < round.rats; index++){
//            Rat r = new Rat(new ProtoCopyRat(round.actions[index]),RatBotsColorAssigner.getAssignedColor());
//            r.getRatBot().setName(round.names[index]);
//            int row = round.startRatLocs[index][0];
//            int col = round.startRatLocs[index][1];
//            r.putSelfInGrid(getGrid(),new Location(row,col));
//        }
        for(int index = 0; index < round.rats; index++){
            ratsInMaze.get(index).initialize();
            int row = round.startRatLocs[index][0];
            int col = round.startRatLocs[index][1];
            ratsInMaze.get(index).putSelfInGrid(getGrid(), new Location(row,col));
        }
        moveNum = 0;
        roundNum = roundNumber;
//        setGrid(new RatBotsGrid<RatBotActor>(getDefaultRows(), getDefaultCols()));
//        ((RatBotsGrid)this.getGrid()).setWalls(rounds.get(0).walls);
        
        getFrame().repaint();
        /*
         * set up the grid with the walls and cheeses in the correct positions--THE WORLD OWNS THE GRID
         * remove any existing rats in this world
         * add the rats/ratbots to the necessary arraylists in the correct locations on the grid
         */  
    }
    
    public void readRound(BufferedReader br){
//        System.out.println("Found a round!              RatBotWorld");
        RoundData round = new RoundData();
        String line;
        int rats;
        int currentRatIndex = 0;
        int cheesesPlaced = 0;
        int roundMoveNum = 500;
        boolean isReadingCheese = false;
        boolean isReadingWalls = false;
        boolean isReadingRandoms = false;
        try{
            while((line=br.readLine())!=null){
                if(line.contains("Number of rats:")){
                    String r = new String(line).trim().replace("Number of rats: ", "");
                    rats = Integer.parseInt(r);
                    round.rats = rats;
                    round.startRatLocs = new int[rats][2];
                    round.actions = new int[rats][500];
                    round.names = new String[rats];
                }else if(line.contains("Ratbot #")){                    
                    String ratString = new String(line);
                    currentRatIndex = Integer.parseInt(ratString.substring(ratString.indexOf('#')+1,ratString.indexOf(':')))-1;
                    int commaIndex = ratString.lastIndexOf(',');
                    int col = Integer.parseInt(ratString.substring(commaIndex-2,commaIndex).trim());
                    int row = Integer.parseInt(ratString.substring(commaIndex+1).trim());
                    round.startRatLocs[currentRatIndex][0] = col;
                    round.startRatLocs[currentRatIndex][1] = row;
                    String name = ratString.substring(ratString.indexOf(':')+1,commaIndex-2).trim();
                    round.names[currentRatIndex] = name;
                    roundMoveNum = 0;
                }else if(roundMoveNum<500){
                    round.actions[currentRatIndex][roundMoveNum] = Integer.parseInt(line.substring(0,line.indexOf(',')).trim());
                    roundMoveNum++;
                }else if(line.contains("Start cheese locations:")){
                    isReadingCheese = true;
                }else if(isReadingCheese && !line.contains("Walls:")){
                    //put cheese reading into this spot
                    String row = line.substring(0,line.indexOf(',')).trim();
                    String col = line.substring(line.indexOf(',')+1,line.length()).trim();
                    int cheeseRow = Integer.parseInt(row);
                    int cheeseCol = Integer.parseInt(col);
                    round.cheeseLocs[cheesesPlaced][0] = cheeseRow;
                    round.cheeseLocs[cheesesPlaced][1] = cheeseCol;
                    cheesesPlaced++;
                }else if(line.contains("Walls:")){
                    isReadingCheese = false;
                    isReadingWalls = true;
                }else if(isReadingWalls && !line.contains("Random")){
                    //takes the number before the first comma
                    //the number between the first and last commas
                    //and the number after the last comma
                    //and sets walls[first][second][third] equal to true
                    round.walls[Integer.parseInt(line.substring(0,line.indexOf(',')).trim())][Integer.parseInt(line.substring(line.indexOf(',')+1,line.lastIndexOf(',')).trim())][Integer.parseInt(line.substring(line.lastIndexOf(',')+1,line.length()).trim())]=true;
                }else if(line.contains("Random numbers:")){
                    isReadingWalls = false;
                    isReadingRandoms = true;
                }else if(isReadingRandoms && !line.contains("End")){
                    round.randoms.add(Integer.parseInt(line.trim()));
                }else if(line.contains("End of round")){
                    rounds.add(round);
                    break;
                }
                
            }
        }catch(IOException e){
            System.out.println(e);
        }
    }
    /**
     * Clears the Arena in preparation of starting a new round. 
     * @return an ArrayList of the Rats in the arena.
     */
    public void clearAllObjectsFromGrid()
    {
        RatBotsGrid<RatBotActor> gr = (RatBotsGrid<RatBotActor>)this.getGrid();
        for(int x=0; x<gr.getNumCols(); x++)
        {
            for(int y=0; y<gr.getNumRows(); y++)
            {
                Location loc = new Location(y,x);
                RatBotActor a = gr.get(loc);
                if(a != null)
                {
                    a.removeSelfFromGrid();
                }
            }
        }
    }
    /**
     * Scores the results from a round of competition.
     */
    public void scoreRound()
    {
        int max = RatBotsScoreBoard.getMaxScore();
        for(Rat r : ratsInMaze)
        {
            if(r.getScore() == max){
                r.increaseRoundsWon();
            }
        }
        roundNum++;
    }
    
    //inheirits javadoc comment from world.
    @Override
    public void show()
    {
        if (getMessage() == null)
            setMessage(DEFAULT_MESSAGE);
        super.show();
    }

    //inherits javadoc comment from world.
    @Override
    public void step()
    {
        Grid<RatBotActor> gr = getGrid();

        if(!matchReady){
            initializeMatch();
        }
            
        
        moveNum++;
        if(moveNum > NUM_MOVES_IN_ROUND)
        {
//            clearAllObjectsFromGrid();
            scoreRound();   
            if(isRecording) recorder.setForNewRound(roundNum); 
            if(!isReplaying){
                initializeGridForRound();
            }else{
                loadRecordedRound(roundNum);
            }
            RatBotsGrid g = (RatBotsGrid)this.getGrid();
            if(isRecording) recorder.setWalls(g.getWalls());
            return; //added by CVH
        }
        if(roundNum >= NUM_ROUNDS_IN_MATCH && roundRobin) 
        {
            initializeMatch();
            return;
        } 
        ArrayList<RatBotActor> actors = new ArrayList();
        // Look at all grid locations.
        for (int r = 0; r < gr.getNumRows(); r++)
        {
            for (int c = 0; c < gr.getNumCols(); c++)
            {
                // If there's an object at this location, put it in the array.
                Location loc = new Location(r, c);
                if (gr.get(loc) != null) {
                    actors.add(gr.get(loc));
//                    if(gr.get(loc) instanceof Rat){
//                        System.out.println("Found a rat "+moveNum);
//                    }
                }
            }
        }
        
        if(actors.size() > 1 && !isReplaying)
        {
            //shuffle their order for acting.
            for(int z=0;z<actors.size()*2;z++)
            {
                //Pick a random one.
                int from = randy.nextInt(actors.size());
                
                if(isRecording)recorder.addRandom(from);
                System.out.println("SHUFFLE");
                
                //Swap it to the front.
                RatBotActor a = actors.get(from);
                RatBotActor b = actors.get(0);
                actors.set(from,b);
                actors.set(0,a);              
            }
            System.out.println("Shuffled");
        }
        if(isReplaying){
            //sort the actors by previous action priority
        }

        
            for (RatBotActor a : actors)
            {
                // only act if another actor hasn't removed a
                if (a.getGrid() == gr){
                    a.act();
//                    if(a instanceof Rat){
//                        System.out.println(((Rat)a).getRatBot().getName()+" "+actors.indexOf(a));
//                    }
                }
            }
            
        if(isRecording) recorder.takeSnapshot();
        
//        if(moveNum == NUM_MOVES_IN_ROUND){
//            recorder.setForNewRound(roundNum);  
//        }
                
    }
    
    public void undo(){ //unfinished method to get ratbots to move back in time
                        //theoretically more efficient than jumpToMove if completed correctly
        
        if(!isRecording || moveNum==0){
            System.out.println("Undo move cancelled");
            return;
        }
        
        //I'll just start by having all of the rats undo their moves.
        
        ArrayList<Rat> rats = ((RatBotsGrid)getGrid()).getAllRatsByName();
        for(int index = 0; index < rats.size(); index++){
            int move = recorder.getActions()[index][moveNum-1];
            int action = -1;
            if(move<0){
                
            }else if(move<180){
                action = move+180;
            }else if(move<360){
                action = move-180;
            }else if(move<720){
                //take down the wall!
            }else{
                
            }
            rats.get(index).act(action);
            System.out.println(action+"             RatBotsWorld");
        }
        getFrame().repaint();
    }     
    
    public void jumpToMove(int moveNumber){   //puts all rats at their start positions
                                        //them moves them through their past actions until a certain move
                                        //insufficiently tested and probably currently causes horrible bugs
                                        //but it works
                                        //implemented in RatBotsWorldFrame class
        RatBotsGrid g = (RatBotsGrid)(this.getGrid());
        
        if(moveNumber<0 || moveNumber>500){
            System.out.println("Invalid move.               RatBotWorld");
            return;
        } 
        if(recorder == null)return;
        
        ArrayList<Rat> rats = g.getAllRatsByName();
        int[][] ratLocs = recorder.getRatLocs();
        int[][] actions = recorder.getActions();
        int[][] cheeses = recorder.getCheeseLocs();
        
        //troubleshooting code
//        for(int x = 0; x < ratLocs.length; x++){
//            for(int y = 0; y < ratLocs[0].length; y++){
//                System.out.println(ratLocs[x][y]+" ");
//            }
//            System.out.println();
//        }
        //
        
        for(int index = 0; index < rats.size(); index++){   //position the rats at their starting locations
            Rat r = rats.get(index);
            r.removeSelfFromGrid();
            int col = ratLocs[index][0];
            int row = ratLocs[index][1];
            System.out.println(col+", "+row);
            r.putSelfInGrid(g,new Location(row,col));            
        }
        
        for(int index = 0; index < cheeses.length; index++){    //put little cheeses back in place
            if(g.get(new Location(cheeses[index][1],cheeses[index][0]))==null){
                Cheese c = new Cheese();
                c.putSelfInGrid(g,new Location(cheeses[index][1],cheeses[index][0]));
            }
        }
        
        for(int row = 0; row<g.getNumRows(); row+=g.getNumRows()-1){
            for(int col = 0; col<g.getNumCols(); col+=g.getNumCols()-1){
                if(g.get(new Location(row,col))==null){
                    Cheese c = new Cheese(true);
                    c.putSelfInGrid(g, new Location(row,col));
                }
            }
        }
        
        for(int move = 0; move < moveNumber; move++){  //then move have them reenact their past actions to a point
            for(int rat = 0; rat < rats.size(); rat++){
                Rat r = rats.get(rat);
                System.out.println(actions[rat][move]);
                r.act(actions[rat][move]);
            }
        }
        
        g.setWalls(recorder.getWalls());
        
        this.getFrame().repaint();  //draws the new grid with the rats in their new positions
    }
    
    public boolean[][][] getWalls(){
        return ((RatBotsGrid<RatBotActor>)this.getGrid()).getWalls();
    }
    
    /**
     * Add a new RatBot to the arena. 
     * @param bot the RatBot to be added.  
     */
    public void add(RatBot bot)
    {
        Rat newRat = new Rat(bot,RatBotsColorAssigner.getAssignedColor());
        Location inCenter = this.getRandomEmptyCenterLocation();
        allrats.add(newRat);
        
        ratsInMaze.add(newRat);
        initializeGridForRound();
        System.out.println("Rat added.");
        
        if(recorder!=null){ //adjusts the grid recorder for an additional rat
//            for(Rat r:ratsInMaze)recorder.increaseRatArraySize(); this should not be looped
            recorder.increaseRatArraySize();
            System.out.println("Array size increased            RatBotWorld");
        }
    }  
    /**
     * Gets one of the 4 possible directions.
     * @return a random direction.
     */
    public int getRandomDirection()
    {
        int i = randy.nextInt(4);
        
        if(isRecording) recorder.addRandom(i);
        System.out.println("RANDOM DIRECTION");
        
        return i;
    }
    /**
     * Gets an empty Location in the center room.  
     * @return a random empty Location from the center room.  
     */
    public Location getRandomEmptyCenterLocation()
    {
        Grid<RatBotActor> gr = getGrid();
        int rows = gr.getNumRows();
        int cols = gr.getNumCols();

        // get all valid empty locations and pick one at random
        ArrayList<Location> emptyLocs = new ArrayList();
        for (int i = (rows-RatBotsArena.CENTER_SIZE)/2; i < (rows+RatBotsArena.CENTER_SIZE)/2; i++)
        {
            for (int j = (cols-RatBotsArena.CENTER_SIZE)/2; j < (cols+RatBotsArena.CENTER_SIZE)/2; j++)
            {
                Location loc = new Location(i, j);
                if (gr.isValid(loc) && (gr.get(loc) == null || gr.get(loc) instanceof Tail))
                    emptyLocs.add(loc);
            }
        }
        if (emptyLocs.isEmpty())
        {
            System.out.println("WARNING: could not find an empty center location!!!");
            return new Location(15,15);
        }
        int r = randy.nextInt(emptyLocs.size());
        
        if(isRecording)recorder.addRandom(r);
        System.out.println("RANDOM EMPTY");
        
        return emptyLocs.get(r);       
    }
      
}
