package cs1302.p1;

import java.io.File;
import java.util.Random;
import java.util.Scanner;


/**
 * This class represents a Minesweeper game.
 *
 * @author YOUR NAME <YOUR EMAIL>
 */
public class MyMineSweeper {
	
	// Constant defnitions for square possible states
	private final int RESOLVED_LOWEST_COUNT = 0;  	// Resolved smallest neighboring mine count 
	private final int RESOLVED_HIGHEST_COUNT = 8;  	// Resolved highest neighboring mine count 
	private final int UNTOUCHED_NO_MINE = 9;  		// untouched square with no mine
	private final int UNTOUCHED_MINE =10;     		// untouched square with mine
	private final int MINE_EXPLOSION =11;
	private final int MARKED_NO_MINE_PRESENT = 12;
	private final int MARKED_MINE_PRESENT = 13;
	private final int GUESSED_NO_MINE_PRESENT = 14;
	private final int GUESSED_MINE_PRESENT = 14;
	
	// Other game parameters
	
	private final static int MAX_GRID_SIZE = 10;
	
	// allocate needed variables
     int[][] map = new int[MAX_GRID_SIZE][MAX_GRID_SIZE];  // allocate max possible grid
     private int gameRows = MAX_GRID_SIZE; //assume max
     private int gameCols = MAX_GRID_SIZE; // assume max
     private int mineCount = 0; // assume no mines
     private int roundCount = 0; 
     
     private Scanner scanner; // used to get user moves
     
    /**
     * Constructs an object instance of the {@link Minesweeper} class using the
     * information provided in <code>seedFile</code>. Documentation about the 
     * format of seed files can be found in the project's <code>README.md</code>
     * file.
     *
     * @param seedFile the seed file used to construct the game
     * @see            <a href="https://github.com/mepcotterell-cs1302/cs1302-minesweeper-alpha/blob/master/README.md#seed-files">README.md#seed-files</a>
     */
    public MyMineSweeper(File seedFile) {
    	scanner = new Scanner(System.in);  // create user input scanner
    	//read seed file to set up game
    	try {
    		 
    		  Scanner fileScanner = new Scanner(seedFile); // create seed file reader
    		  // note seed file format is:
    		  // rowCount colCount
    		  // mine count
    		  // x y  (mine coordinates, as many lines as mine count)
    		  
    		  int intInFile;
    		  // get the row count
    		  intInFile = fileScanner.nextInt();  // non digits caught at exception handling
    		  if (intInFile>=1 && intInFile<=MAX_GRID_SIZE){  // validate digits
    			  gameRows = intInFile;
    		  }
    		  else{
    			  System.out.println("Invalid row count = "+intInFile+" in file "+seedFile.getName());
    			  fileScanner.close();
    			  System.exit(0);
    			  
    		  }
    		 //get column value
    		  intInFile = fileScanner.nextInt(); // non digits caught at exception handling
    		  if (intInFile>=1 && intInFile<=MAX_GRID_SIZE){  // validate digits
    			  gameCols = intInFile;
    		  }
    		  else{
    			  System.out.println("Invalid columnt count = "+intInFile+" in file "+seedFile.getName());
    			  fileScanner.close();
    			  System.exit(0);		  
    		  }
    		  
    		  // verify new line
    		  fileScanner.nextLine();
    		  
    	      clearMap();	//set all positions to untouched with no mine
    	        
    		  // now get the mine count
    		  intInFile = fileScanner.nextInt(); //get mine count
    		  
    		  if (intInFile>=0 || intInFile<(gameRows*gameCols)){
    			  mineCount = intInFile;
    		  }
    		  else{
    			  System.out.println("Invalid mine count = "+intInFile+" in file "+seedFile.getName());
    			  fileScanner.close();
    			  System.exit(0);  
    		  }
    		  fileScanner.nextLine();
    		  
    		  // now set up the mine squares
    		  for (int i=0;i<mineCount;i++){
    			  int x = -1;  // mine X coordinate
    			  int y = -1;  // mine Y coordinate
        		  intInFile = fileScanner.nextInt(); //get mine row position
        		  if (intInFile>=0 && intInFile<gameRows)
        			  x = intInFile;
        		  
        		  intInFile = fileScanner.nextInt(); //get mine row position
        		  if (intInFile>=0 && intInFile<gameCols)
        			  y = intInFile;
        		  
        		  if (x==-1 || y==-1) {
        			  System.out.println("Invalid mine coordinates in file "+seedFile.getName());
        			  fileScanner.close();
        			  System.exit(0);  
        		  }
        		  
        		  installMine(x,y); // place the mine
    		  }
    		  fileScanner.close();
    		  
    		  
    		} catch (Exception e) {
    		  System.out.println("Error "+ e+ "while scanning "+seedFile.getName());
    		} // try
    	


    	

    } // Minesweeper


    /**
     * Constructs an object instance of the {@link Minesweeper} class using the
     * <code>rows</code> and <code>cols</code> values as the game grid's number
     * of rows and columns respectively. Additionally, One quarter (rounded up) 
     * of the squares in the grid will will be assigned mines, randomly.
     *
     * @param rows the number of rows in the game grid
     * @param cols the number of cols in the game grid
     */
    public MyMineSweeper(int rows, int cols) {
    gameRows = rows;
    gameCols = cols;
    scanner = new Scanner(System.in);
	// TODO implement
    System.out.println("about to start with "+rows+","+cols);
    clearMap();	

    // 5% of the squares will be mines, with one as minimum
    mineCount = (gameRows*gameCols + 19)/20;
    
    // now randomly place the mines
    Random r = new Random();
    
    for (int i=0;i<mineCount;i++){
    	// figure out the coordinates
    	 int x = r.nextInt(9);
    	 int y = r.nextInt(9);
    	 if (map[x][y] == UNTOUCHED_NO_MINE )
    		 installMine(x,y); // place the mine
    	 else i--; // try again
    }
       
    } // Minesweeper
    

   
    /**
     * Starts the game and execute the game loop.
     */
    public void run() {

	
	
	while(true){
		showCurrentMap();
		//do for ever until nextCommand returns false
		if (nextCommand() == false){
			scanner.close();
			System.exit(0);
		}
		roundCount++;		
	}

    } // run

private void clearMap(){
	// init map positions to No mine and hidden
	
	for (int i=0;i<gameRows;i++){
		for (int j=0;j<gameCols;j++){
			map[i][j] = UNTOUCHED_NO_MINE;
		}
	}
}

private void installMine(int x,int y){
	map[x][y] = UNTOUCHED_MINE;
}
private void showCurrentMap(){
	// show the current status
	int hiddenMinesCount =0;
	int guessMinesCount = 0;
	int markedMinesCount =0;
    System.out.println("```\n\nRounds Completed: "+roundCount);
    for (int i=0;i<gameRows;i++){
    	System.out.print("\n"+i + " | ");
    	for (int j=0;j<gameCols;j++){
    		if (map[i][j] == UNTOUCHED_NO_MINE)
    			System.out.print(" "+" | ");
    		else if (map[i][j] >= RESOLVED_LOWEST_COUNT && map[i][j] <= RESOLVED_HIGHEST_COUNT )
    			System.out.print(map[i][j]+" | ");
    		
    		else if (map[i][j] == MARKED_NO_MINE_PRESENT || map[i][j] == MARKED_MINE_PRESENT ){
    			System.out.print("F"+" | ");
    			markedMinesCount++;
    		}
    		else if (map[i][j] == GUESSED_NO_MINE_PRESENT || map[i][j] == GUESSED_MINE_PRESENT ){
    			System.out.print("?"+" | ");
    			guessMinesCount++;
    		}
    		else if (map[i][j] == UNTOUCHED_MINE){
    			System.out.print("X"+" | ");
    			//System.out.print(" "+" | ");
    			hiddenMinesCount++;
    		}
    	}
    }
    System.out.print("\n "); //margin for columnt values
    for (int j=0;j<gameCols;j++){
		System.out.print("   "+j);
	}
    System.out.println("\n\nminesweeper-alpha$\n```");
    
    // do some checking to see if game is over
    if (hiddenMinesCount ==0){
    	// validate all counts
    	if (guessMinesCount==0 && markedMinesCount == mineCount){
    		handleWinner();
    		System.exit(0);
    	}
    }
}

private boolean nextCommand(){
	boolean retVal = true; //assume success
	String choice = scanner.nextLine();
    System.out.println("initial string is "+choice);
    choice = choice.trim().replaceAll("\\s+", " "); // eliminate multiple spaces
    System.out.println("after string is "+choice);
    
    String[] entries = choice.split(" "); // trim to remove leading and trailing space
    System.out.println("entries legnht is "+entries.length);
    
	System.out.print("\nminesweeper-alpha$ "); // show prompt
    
   
    
    if (entries.length==1){
    	// either quit or help
    	if (entries[0].length()==1){
    		//do one letter checks
    		if (entries[0].charAt(0)=='h')
    			showHelp();
    		
    		else if (entries[0].charAt(0)=='q'){
    			showQuit();
    			retVal = false;  //indicate we are done
    		}
    		else showCommandNotRecognized();
    	}
    	else {
    		//do word checks
    		if (entries[0].length() == 4 &&
    			entries[0].toLowerCase().contains("help"))
    			showHelp();	
    	
    		else if (entries[0].length() == 4 &&
    			entries[0].toLowerCase().contains("quit")){
        		showQuit();	
    		    retVal = false;  //indicate we are done
    		}
    		else showCommandNotRecognized();
    	}
    }
    
    else if (entries.length == 3){
    	//could be mark, guess, reveal each with coordinate values
    	//get the coordinates first
    	int x = getCoordinateValue(entries[1]);
    	int y = getCoordinateValue(entries[2]);
    	if (x==-1 || y==-1) showCommandNotRecognized();
    	else if (x<0 || x>gameRows-1 || y<0 || y>gameCols-1) showCommandNotRecognized();
    	else{
    		// now figure out which command
    		if (entries[0].length()==1){
        		//do one letter checks
        		if (entries[0].charAt(0)=='m')
        			mark(x,y);
        		
        		else if (entries[0].charAt(0)=='g')
        			guess(x,y);
        		
        		else if (entries[0].charAt(0)=='r'){
        			boolean explosion = reveal(x,y);
        			// handle explosion message
        			if (explosion) {
        				handleExplosion(x,y);
        				retVal = false;  //indicate we are done
        			}
    				
        		}
        		
        		else showCommandNotRecognized();
        	}
        	else {
        		//do word checks
        		if (entries[0].length() == 4 &&
        			entries[0].toLowerCase().contains("mark"))
        			mark(x,y);
        		
        		else if (entries[0].length() == 5 &&
            			entries[0].toLowerCase().contains("guess"))
            			guess(x,y);
        	
        		else if (entries[0].length() == 6 &&
        			entries[0].toLowerCase().contains("reveal")){
        			boolean explosion = reveal(x,y);
        			if (explosion == true){
        				if (explosion) {
            				handleExplosion(x,y);
            				retVal = false;  //indicate we are done
            			}
        			}
        		   
        		}
        		
        		else showCommandNotRecognized();
        		
        	}
    		
    	}
    }
    else showCommandNotRecognized();
	return retVal;
}

private int getCoordinateValue(String str){
	int retVal = -1; //assume fail

	try  
	  {  
	    retVal = Integer.parseInt(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    retVal =-1;  
	  }  
	
	  return retVal;  
	
}

private void showCommandNotRecognized(){
    System.out.println("Command not recognized");
}

private void showHelp(){
	System.out.println("\nCommands Available..."+
                       "\n- Reveal: r/reveal row col"+
                       "\n-   Mark: m/mark   row col"+
                       "\n-  Guess: g/guess  row col"+
                       "\n-   Help: h/help"+
                       "\n-   Quit: q/quit");
}

private void showQuit(){
	System.out.println("\nლ(ಠ_ಠლ"+
					   "\nY U NO PLAY MORE?"+
					   "\nBye!"+
                       "\n\n```");
}

private void mark(int x,int y){
	//Mark a square as definitely containing a mine.

	System.out.println("marking "+x+","+y);
	if (map[x][y] == UNTOUCHED_NO_MINE) map[x][y]  = MARKED_NO_MINE_PRESENT;
	
	else map[x][y]  = MARKED_MINE_PRESENT;
	
}

private void guess(int x,int y){
	//Mark a square as potentially containing a mine.

	if (map[x][y] == UNTOUCHED_NO_MINE) map[x][y]  = GUESSED_NO_MINE_PRESENT;
	
	else map[x][y]  = GUESSED_MINE_PRESENT;
}

//following returns true if explosion occurs
private boolean reveal(int x,int y){
	boolean retVal = false; //assume no explosion
	if (map[x][y] == UNTOUCHED_MINE) {
		map[x][y]  = MINE_EXPLOSION;
		retVal = true;
	}
	
	else {
		map[x][y]  = countAdjacentMines(x,y);  
	}
	return retVal;
}

private void  handleExplosion(int x, int y){
	System.out.println("\n```\n"+
	                   "Oh no... You revealed a mine!"+
			           "\n  __ _  __ _ _ __ ___   ___    _____   _____ _ __ "+
			           "\n / _` |/ _` | '_ ` _ \\ / _ \\  / _ \\ \\ / / _ \\ '__|"+
	                   "\n| (_| | (_| | | | | | |  __/ | (_) \\ V /  __/ |   "+
			           "\n \\__, |\\__,_|_| |_| |_|\\___|  \\___/ \\_/ \\___|_|"   +
	                   "\n |___/  "+
			           "\n```");
			           
}

private void handleWinner(){
	System.out.println("figure score and report winner");
	int score = (gameRows * gameCols) - mineCount - roundCount;
    System.out.println("Congratulations you won!");
    System.out.println("your score is "+score);
}

private int countAdjacentMines(int x,int y){
	int count = 0; //assume no adjacent mines
	
	int rangeXMin = x-1;
	int rangeXMax = x+1;
	int rangeYMin = y-1;
	int rangeYMax = y+1;
	
	if (rangeXMin<0) rangeXMin =0;
	if (rangeYMin<0) rangeYMin =0;
	if (rangeXMax>gameRows-1) rangeXMax =gameRows-1;
	if (rangeYMax>gameCols-1) rangeYMax =gameCols-1;
	
	for (int i=rangeXMin;i<=rangeXMax;i++){
		for (int j=rangeYMin;j<=rangeYMax;j++){
			if (map[i][j] == UNTOUCHED_MINE) count++;
		}
	}
	System.out.println("return count = "+count);
	return count;
}
    /**
     * The entry point into the program. This main method does implement some
     * logic for handling command line arguments. If two integers are provided
     * as arguments, then a Minesweeper game is created and started with a 
     * grid size corresponding to the integers provided and with 10% (rounded
     * up) of the squares containing mines, placed randomly. If a single word 
     * string is provided as an argument then it is treated as a seed file and 
     * a Minesweeper game is created and started using the information contained
     * in the seed file. If none of the above applies, then a usage statement
     * is displayed and the program exits gracefully. 
     *
     * @param args the shell arguments provided to the program
     */
    public static void main(String[] args) {

	/*
	  The following switch statement has been designed in such a way that if
	  errors occur within the first two cases, the default case still gets
	  executed. This was accomplished by special placement of the break
	  statements.
	*/
    System.out.println("```"+
    				   "\n    	_"+
    		           "\n  /\\/\\ (_)_ __   ___  _____      _____  ___ _ __   ___ _ __"+
    		           "\n /    \\| | '_ \\ / _ \\/ __\\ \\ /\\ / / _ \\/ _ \\ '_ \\ / _ \\ '__|"+
    		           "\n/ /\\/\\ \\ | | | |  __/\\__ \\\\ V  V /  __/  __/ |_) |  __/ |"+
    		           "\n\\/    \\/_|_| |_|\\___||___/ \\_/\\_/ \\___|\\___| .__/ \\___|_|"+
    		           "\n                             ALPHA EDITION |_| v2017.f"+
    		           "\n```");


	Minesweeper game = null;

	switch (args.length) {

        // random game
	case 2: 
        try{
        	int rows, cols;
        	// try to parse the arguments and create a game
	    	rows = Integer.parseInt(args[0]);
	    	cols = Integer.parseInt(args[1]);
	    	if (rows>=0 && rows <=MAX_GRID_SIZE && cols >=0 && cols<=MAX_GRID_SIZE){
	    		game = new Minesweeper(rows, cols);
	    		break;
	    	}
	    	else
	    		System.out.println("You entered [ROWS]="+rows+" [COLS]="+cols+" [ROWS] and [COLS] must be a number from 1 to " + MAX_GRID_SIZE);
        
    	} catch (NumberFormatException nfe) { 
    		System.out.println("You entered invalid values [ROWS] and [COLS] must be a number from 1 to " + MAX_GRID_SIZE);
    	} // try

	// seed file game
	case 1: 
        
	    String filename = args[0];
	    System.out.println("need to read "+filename);
	    
	    File file = new File(filename);

	    if (file.isFile()) {
	    	System.out.println("file is file");
	    	game = new Minesweeper(file);
	    	break;
	    } // if
	    else
	    	System.out.println("did not find file "+filename);
	    
        // display usage statement
	default:

	    System.out.println("Usage: java Minesweeper [FILE]");
	    System.out.println("Usage: java Minesweeper [ROWS] [COLS]");
	    System.exit(0);

	} // switch

	// if all is good, then run the game
	game.run();

    } // main


} // Minesweeper
