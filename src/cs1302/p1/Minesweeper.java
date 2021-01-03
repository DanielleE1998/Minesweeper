//@author Danielle Mawson (dem07541@uga.edu) 
package cs1302.p1;
import java.util.Scanner;
import java.util.Random;
import java.io.File;
public class Minesweeper {

	//constants!!
	//mine states
	private final int MIN_NEIGHBOR_BOMBS = 0;
	private final int MAX_NEIGHBOR_BOMBS = 8;
	private final int COVERED_EMPTY = 9;
	private final int COVERED_BOMB = 10;
	private final int UNCOVERED_BOMB = 11;
	private final int FLAGGED_EMPTY = 12;
	private final int FLAGGED_BOMB = 13;
	private final int GUESSED_EMPTY = 14;
	private final int GUESSED_BOMB = 15;
	//grid states
	private final static int MAX_DIMMENSION = 10;
	private int rowsMax = MAX_DIMMENSION;
	private int colsMax = MAX_DIMMENSION;

	//instance variables
	int[][] grid = new int[rowsMax][colsMax];
	private int bombs = 0;
	private int rounds = 0;
	private int score;
	boolean nofog = false;

	Scanner keyboard = new Scanner(System.in); // user input	
	//Constructor
	public Minesweeper(int rows, int cols){
		if (rows < 1 || rows > MAX_DIMMENSION || cols < 1 || rows > MAX_DIMMENSION){
			System.out.print("Enter values between 1 and " + MAX_DIMMENSION);
			System.exit(0);
		}
		rowsMax = rows;
		colsMax = cols;

		Scanner keyboard = new Scanner(System.in);
		resetGrid();
		setBombs();
	}

	public Minesweeper(File seedFile){ 
		int seedInt;

		try {
			Scanner seedScanner = new Scanner(seedFile);
			seedFile = new File("seed1.txt");
			// use scanner here
			try{
				seedInt = seedScanner.nextInt(); //rows
				if (seedInt > 0 || seedInt <= MAX_DIMMENSION)
					rowsMax = seedInt;
				else{
					System.out.println("à² _à²  says, \"Cannot create a mine field with that"
							+ " many rows and/or columns!\"");
					seedScanner.close();
					System.exit(0);
				}
				seedInt = seedScanner.nextInt(); //cols
				if (seedInt > 0 || seedInt <= MAX_DIMMENSION)
					colsMax = seedInt;
				else{
					System.out.println("à² _à²  says, \"Cannot create a mine field with that many rows and/or columns!\"");
					seedScanner.close();
					System.exit(0);
				}

				seedScanner.nextLine(); //next line
				resetGrid();
				seedInt = seedScanner.nextInt(); //bombs
				if (seedInt > 0 || seedInt <= rowsMax * colsMax)
					bombs = seedInt;
				else{
					System.out.println(seedFile+ " mine count is invalid: " + seedInt);
					seedScanner.close();
					System.exit(0);
				}

			} catch (Exception e) {
				// handler exception here

				seedScanner.nextLine(); //next line
				for (int i = 0; i < bombs; i++){
					int xBomb = 1, yBomb = 1; //assume (0,0)
					seedInt = seedScanner.nextInt(); // X-coordinate
					if (seedInt > 0 || seedInt <= rowsMax) 
						xBomb = seedInt - 1;
					else{
						System.out.println(seedFile+ " mine coordinates are invalid: " + seedInt);
						seedScanner.close();
						System.exit(0);
					}

					seedInt = seedScanner.nextInt(); // Y-coordinate
					if (seedInt > 0 || seedInt <= colsMax) 
						yBomb = seedInt - 1;
					else{
						System.out.println(seedFile+ " mine coordinates are invalid: " + seedInt);
						seedScanner.close();
						System.exit(0);
					}

					makeMine(xBomb, yBomb);

				}
				seedScanner.close();
			} 
		}catch (Exception e){
				System.out.println("Exception " + e + " while scanning" + seedFile +".");
		}
	}

	//Methods for Grid Display and Setup

	public void run(){
		while(true){
			showCurrentMap();
			if (nextCommand() == false){
				keyboard.close();
				System.exit(0);
			}
			rounds++;
		}
	}

	private boolean nextCommand(){
		boolean returnValue = true; //assume true
		String userEntry = keyboard.nextLine().trim().toLowerCase().replaceAll("\\s +", " ");;							

		if (userEntry.equals("h") || userEntry.equals("help")){
			displayHelp();
		}
		else if (userEntry.equals("q") || userEntry.equals("quit")){
			displayQuit();
			System.exit(0);
		}
		else if (userEntry.equals("nofog")){
			nofog = true;
			return true;
		}
		else {
			String[] commandParts = userEntry.split(" ");
			if (commandParts.length != 3){
				invalidCommand();
				return true;
			}

			//validate coordinates
			Integer xCoordinate, yCoordinate;
			try{
				xCoordinate = Integer.parseInt(commandParts[1]);
				yCoordinate = Integer.parseInt(commandParts[2]);
				if (xCoordinate > rowsMax - 1  || xCoordinate < 0 || yCoordinate > colsMax - 1 || yCoordinate < 0){
					System.out.println("Invalid row and/ or column number.");
					return true;
				}
			} catch (Exception e){
				invalidCommand();
				return true;
			}
			if (commandParts[0].equals("r") || commandParts[0].equals("reveal")){
				reveal(xCoordinate, yCoordinate);
				return true;
			}
			else if (commandParts[0].equals("m") || commandParts[0].equals("mark")){
				mark(xCoordinate, yCoordinate);
				return true;
			}
			else if (commandParts[0].equals("g") || commandParts[0].equals("guess")){
				guess(xCoordinate, yCoordinate);
				return true;
			}
			else 
				invalidCommand();
		}

		return true;

	}

	public boolean noFog(){
		boolean fog = true;
		return fog;
	}

	public int getCoordinates(String string){
		int returnValue = -1; //assume fail
		returnValue = Integer.parseInt(string);
		return returnValue;
	}

	private void showCurrentMap(){
		// show the current status
		int hiddenMines = 0;
		int guessedMines = 0;
		int markedMines = 0;
		System.out.println("\n Rounds Completed: "+ rounds + "\n");
		for (int i=0;i<rowsMax;i++){
			System.out.print("\n " + i + " |");
			for (int j=0;j<colsMax;j++){
				if (nofog == true){
					if (grid[i][j] == COVERED_BOMB)
						System.out.print("< >|");
					else if (grid[i][j] == FLAGGED_BOMB)
						System.out.print("<F>|");
					else if (grid[i][j] == GUESSED_BOMB)
						System.out.print("<?>|");
					else 
						System.out.print("   |");
					hiddenMines++; //prevents gameover
				}
				else if (grid[i][j] == COVERED_EMPTY)
					System.out.print("   |");
				else if (grid[i][j] >= MIN_NEIGHBOR_BOMBS && grid[i][j] <= MAX_NEIGHBOR_BOMBS)
					System.out.print(" " + grid[i][j]+" |");
				else if (grid[i][j] == FLAGGED_EMPTY || grid[i][j] == FLAGGED_BOMB){
					System.out.print(" F |");
					if (grid[i][j] == FLAGGED_BOMB)
						markedMines++;
				}
				else if (grid[i][j] == GUESSED_BOMB || grid[i][j] == GUESSED_EMPTY){
					System.out.print(" ? |");
					if (grid[i][j] == GUESSED_BOMB)
						guessedMines++;
				}
				else if (grid[i][j] == COVERED_BOMB){
					System.out.print("   |");
					hiddenMines++;
				}
			}
		}
		System.out.println();
		System.out.print("     ");
		for(int h = 0; h < colsMax; h++)
			System.out.print(h + "   ");
		System.out.print("\n\n minesweeper-alpha$ ");
		//finished game??
		int mineCount = hiddenMines + guessedMines + markedMines;
		if (hiddenMines == 0 && guessedMines == 0 && markedMines == mineCount){
			score = (rowsMax * colsMax) - bombs - rounds;
			displayWin();
			System.exit(0);
		}
		nofog = false;
	}

	public void resetGrid(){
		for (int i = 0; i < grid.length; i++){
			for (int j = 0; j < grid[i].length; j++){
				grid[i][j] = COVERED_EMPTY;
			}
		}
	}

	public void setBombs(){ //5% of spaces are mines
		bombs = (int)Math.ceil(rowsMax*colsMax*2/(double)10);

		for(int i = 0; i < bombs; i++){
			Random randy = new Random();
			int xRandom = randy.nextInt(rowsMax);
			int yRandom = randy.nextInt(colsMax);
			if (grid [xRandom][yRandom] == COVERED_BOMB)
				i--;
			grid[xRandom][yRandom] = COVERED_BOMB;
		}
	}

	public int getNeighboringMines(int x, int y){
		int neighboringMines = 0; //assume 0
		int xMinRange = x - 1;
		int xMaxRange = x + 1;
		int yMinRange = y - 1;
		int yMaxRange = y + 1;

		if (xMinRange < 0)
			xMinRange = 0;
		if (yMinRange < 0)
			yMinRange = 0;
		if (xMaxRange > rowsMax - 1)
			xMaxRange = rowsMax - 1;
		if (yMaxRange > colsMax - 1)
			yMaxRange = colsMax - 1;

		for (int i = xMinRange; i <= xMaxRange; i++){
			for (int j = yMinRange; j <= yMaxRange; j++){
				if (grid[i][j] == COVERED_BOMB || grid[i][j] == FLAGGED_BOMB ||
						grid[i][j] == GUESSED_BOMB){
					neighboringMines++;
				}
			}
		}
		return neighboringMines;
	}	

	public void makeMine(int x, int y){
		grid[x][y] = COVERED_BOMB;
	}
	public  int getCoordinateValue(String string){
		int returnValue = -1; //fail
		try  
		{  
			returnValue = Integer.parseInt(string);  
		}  
		catch(NumberFormatException nfe)  
		{  
			returnValue =-1;  
		}  

		return returnValue;  

	}

	//User Command Methods

	private void mark(int x,int y){
		//Mark a square as definitely containing a mine.

		System.out.println("marking "+x+","+y);
		if (grid[x][y] == COVERED_EMPTY) 
			grid[x][y]  = FLAGGED_EMPTY;

		else grid[x][y]  = FLAGGED_BOMB;

	}

	private void guess(int x,int y){
		//Mark a square as definitely containing a mine.

		System.out.println("guessing "+x+","+y);
		if (grid[x][y] == COVERED_EMPTY) grid[x][y]  = GUESSED_EMPTY;

		else grid[x][y]  = GUESSED_BOMB;

	}

	private void reveal(int x,int y){
		boolean returnValue = false; //assume no bomb
		if (grid[x][y] == COVERED_BOMB) {
			grid[x][y]  = UNCOVERED_BOMB;
			displayExplosion();
		}

		else {
			grid[x][y]  = getNeighboringMines(x,y);  
		}
	}

	//Message Methods

	public void displayWin(){
		System.out.println("\n\n\nâ–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–„â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–„â–‘â–‘â–‘â–‘ \"So Doge\"");
		System.out.println("â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–Œâ–’â–ˆâ–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–„â–€â–’â–Œâ–‘â–‘â–‘");
		System.out.println("â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–Œâ–’â–’â–ˆâ–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–„â–€â–’â–’â–’â–â–‘â–‘â–‘ \"Such Score\"");
		System.out.println("â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–â–„â–€â–’â–’â–€â–€â–€â–€â–„â–„â–„â–€â–’â–’â–’â–’â–’â–â–‘â–‘â–‘");
		System.out.println("â–‘â–‘â–‘â–‘â–‘â–„â–„â–€â–’â–‘â–’â–’â–’â–’â–’â–’â–’â–’â–’â–ˆâ–’â–’â–„â–ˆâ–’â–â–‘â–‘â–‘ \"Much Minesweeping\"");
		System.out.println("â–‘â–‘â–‘â–„â–€â–’â–’â–’â–‘â–‘â–‘â–’â–’â–’â–‘â–‘â–‘â–’â–’â–’â–€â–ˆâ–ˆâ–€â–’â–Œâ–‘â–‘â–‘");
		System.out.println("â–‘â–‘â–â–’â–’â–’â–„â–„â–’â–’â–’â–’â–‘â–‘â–‘â–’â–’â–’â–’â–’â–’â–’â–€â–„â–’â–’â–Œâ–‘â–‘ \"Wow\"");
		System.out.println("â–‘â–‘â–Œâ–‘â–‘â–Œâ–ˆâ–€â–’â–’â–’â–’â–’â–„â–€â–ˆâ–„â–’â–’â–’â–’â–’â–’â–’â–ˆâ–’â–â–‘â–‘");
		System.out.println("â–‘â–â–‘â–‘â–‘â–’â–’â–’â–’â–’â–’â–’â–’â–Œâ–ˆâ–ˆâ–€â–’â–’â–‘â–‘â–‘â–’â–’â–’â–€â–„â–Œâ–‘");
		System.out.println("â–‘â–Œâ–‘â–’â–„â–ˆâ–ˆâ–„â–’â–’â–’â–’â–’â–’â–’â–’â–’â–‘â–‘â–‘â–‘â–‘â–‘â–’â–’â–’â–’â–Œâ–‘");
		System.out.println("â–€â–’â–€â–â–„â–ˆâ–„â–ˆâ–Œâ–„â–‘â–€â–’â–’â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–’â–’â–’â–â–‘");
		System.out.println("â–â–’â–’â–â–€â–â–€â–’â–‘â–„â–„â–’â–„â–’â–’â–’â–’â–’â–’â–‘â–’â–‘â–’â–‘â–’â–’â–’â–’â–Œ");
		System.out.println("â–â–’â–’â–’â–€â–€â–„â–„â–’â–’â–’â–„â–’â–’â–’â–’â–’â–’â–’â–’â–‘â–’â–‘â–’â–‘â–’â–’â–â–‘");
		System.out.println("â–‘â–Œâ–’â–’â–’â–’â–’â–’â–€â–€â–€â–’â–’â–’â–’â–’â–’â–‘â–’â–‘â–’â–‘â–’â–‘â–’â–’â–’â–Œâ–‘");
		System.out.println("â–‘â–â–’â–’â–’â–’â–’â–’â–’â–’â–’â–’â–’â–’â–’â–’â–‘â–’â–‘â–’â–‘â–’â–’â–„â–’â–’â–â–‘â–‘");
		System.out.println("â–‘â–‘â–€â–„â–’â–’â–’â–’â–’â–’â–’â–’â–’â–’â–’â–‘â–’â–‘â–’â–‘â–’â–„â–’â–’â–’â–’â–Œâ–‘â–‘");
		System.out.println("â–‘â–‘â–‘â–‘â–€â–„â–’â–’â–’â–’â–’â–’â–’â–’â–’â–’â–„â–„â–„â–€â–’â–’â–’â–’â–„â–€â–‘â–‘â–‘ CONGRATULATIONS!");
		System.out.println("â–‘â–‘â–‘â–‘â–‘â–‘â–€â–„â–„â–„â–„â–„â–„â–€â–€â–€â–’â–’â–’â–’â–’â–„â–„â–€â–‘â–‘â–‘â–‘â–‘ YOU HAVE WON!");
		System.out.println("â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–’â–’â–’â–’â–’â–’â–’â–’â–’â–’â–€â–€â–‘â–‘â–‘â–‘â–‘â–‘â–‘â–‘ SCORE: " + score);

	}

	public void displayHelp(){
		System.out.println("Commands Available\n" +
				"- Reveal: r/reveal row col\n" +
				"-   Mark: m/mark   row col\n" +
				"-  Guess: g/guess  row col\n" +
				"-   Help: h/help\n" +
				"-   Quit: q/quit\n");
	}

	public void displayQuit(){
		System.out.println("áƒš(à² _à² áƒš)\n" + "Y U NO PLAY MORE?\n" + "Bye!");	
	}

	private void  displayExplosion(){
		System.out.println("\n\n"+
				"Oh no... You revealed a mine!"+
				"\n  __ _  __ _ _ __ ___   ___    _____   _____ _ __ "+
				"\n / _` |/ _` | '_ ` _ \\ / _ \\  / _ \\ \\ / / _ \\ '__|"+
				"\n| (_| | (_| | | | | | |  __/ | (_) \\ V /  __/ |   "+
				"\n \\__, |\\__,_|_| |_| |_|\\___|  \\___/ \\_/ \\___|_|"   +
				"\n |___/  "+
				"\n");
		System.exit(0);
	}

	public void invalidCommand(){
		System.out.println("à² _à²  says, \"Command not recognized!\"");
	}

	//Main Method
	public static void main(String[] args) {	

		System.out.println("        _");
		System.out.println("  /\\/\\ (_)_ __   ___  _____      _____  ___ _ __   ___ _ __");
		System.out.println(" /    \\| | '_ \\ / _ \\/ __\\ \\ /\\ / / _ \\/ _ \\ '_ \\ / _ \\ '__|");
		System.out.println("/ /\\/\\ \\ | | | |  __/\\__ \\\\ V  V /  __/  __/ |_) |  __/ |");
		System.out.println("\\/    \\/_|_| |_|\\___||___/ \\_/\\_/ \\___|\\___| .__/ \\___|_|");
		System.out.println("                             ALPHA EDITION |_| v2017.f");

		File folder = new File(".");
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		        System.out.println("File " + listOfFiles[i].getName());
		      } else if (listOfFiles[i].isDirectory()) {
		        System.out.println("Directory " + listOfFiles[i].getName());
		      }
		    }
		Minesweeper game = null;
		if (args.length == 2){
			try{
				int rows, cols;
				// try to parse the arguments and create a game
				rows = Integer.parseInt(args[0]);
				cols = Integer.parseInt(args[1]);
				if (rows>=0 && rows <= MAX_DIMMENSION && cols >=0 && cols <= MAX_DIMMENSION){
					game = new Minesweeper(rows, cols);
				}
				else{
					System.out.println("rows and columns must be a number from 1 to " + MAX_DIMMENSION);
					System.exit(0);
				}
			} catch (NumberFormatException nfe) { 
				System.out.println("You entered invalid values [ROWS] and [COLS] must be a number"
						+ " from 1 to " + MAX_DIMMENSION);
				System.exit(0);
			}
		}
		else if (args.length == 1){
			String filename = args[0]; //seedFile argument at index 0
			System.out.println("Reading " + filename + "...");
			File file = new File(filename);
			if (file.isFile()){
				System.out.println(filename + " accepted.");
				game = new Minesweeper(file);
				System.exit(0);
			}
			else{
				System.out.println(filename + " not accepted.");
				System.exit(0);
			}

		}
		else{
			System.out.println("Invalid amount of arguments.");
			System.exit(0);
		}
		game.run();
		game.displayHelp();

	}

}
