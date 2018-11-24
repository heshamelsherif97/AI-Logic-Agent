package SaveWesteros;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

import Cell.Cell;

public class genGrid {
	private Cell [][] grid;
	//Jon Snow row position
	private int positionI;
	//Jon Snow column position
	private int positionJ;
	//Dragon stone row position
	private int positionIDragon;
	//Dragon Stone column position
	private int positionJDragon;
	//Max number of Dragon glass the dragon stone can provide
	private int maxDragonGlass;
	//Number of dragon Glass Jon Snow holds
	private int currentDragonGlass;
	//Number of white walkers present in the grid

	
	public void printGrid(Cell [][] grid) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				System.out.print(grid[i][j].getType().substring(0, 3) + ", ");
			}
			System.out.println("");
		}
		System.out.println("------------------------------------");
	}
	
	//Generates a Random Grid
	public void genGrid() throws IOException {	
		Random r = new Random();
		//Generate a grid with random number of rows and cols not less than 4 and not greater than 10
		int randomX = r.nextInt(3) + 3;
		int randomY = r.nextInt(3) + 3;
		//Random number of dragons glass between 1 and 10 a dragon stone can provide at a time
		maxDragonGlass = r.nextInt(10) + 2;
		this.positionI = randomX - 1;
		this.positionJ = randomY - 1;
		grid = new Cell[randomX][randomY];
		//Initialize Jon Snow position
		grid[this.positionI][this.positionJ] = new Cell("JonSnow", 0);
		//Get a random position for the dragon stone not the same as Jon Snow position
		while(true) {
			int randomDragonX = r.nextInt(randomX);
			int randomDragonY = r.nextInt(randomY);
			if(randomDragonX != positionI && randomDragonY != positionJ) {
				grid[randomDragonX][randomDragonY] = new Cell("dragonStone", maxDragonGlass);
				positionIDragon = randomDragonX;
				positionJDragon = randomDragonY;
				break;
			}
		}
		//Generate the rest of the grid
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if(grid[i][j] == null) {
					int randomSpawn = r.nextInt(3);
					grid[i][j] = new Cell(generateCell(randomSpawn), 0);
				}
			}
		}
	}
	
	//Generates a grid from a txt file
	public void genGrid2(String filePath) throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(filePath));
		String st;
		ArrayList<String []> cells = new ArrayList<>();
		int maxD = Integer.parseInt(bf.readLine());
		while ((st = bf.readLine()) != null) {
			String [] splitComma = st.split(",");
			cells.add(splitComma);
		}
		int rows = cells.size();
		int cols = cells.get(0).length;
		grid = new Cell[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				switch(cells.get(i)[j].trim()) {
				case "emp": 
					grid[i][j] = new Cell("empty", 0);
					break;
				case "dra": 
					grid[i][j] = new Cell("dragonStone", maxD);
					maxDragonGlass = maxD;
					positionIDragon = i;
					positionJDragon = j;
					break;
				case "whi": 
					grid[i][j] = new Cell("whiteWalker", 0);
					break;
				case "Jon":
					grid[i][j] = new Cell("JonSnow", 0);
					this.positionI = i;
					this.positionJ = j;
					break;
				case "obs":
					grid[i][j] = new Cell("obstacle", 0);
					break;
				}
			}
		}
	}
	
	//Used to randomly select a type of cell in the grid from the genGrid() function
	public static String generateCell(int x) {
		switch(x) {
		case 0 : return "empty";
		case 1 : return "whiteWalker";
		case 2 : return "obstacle";
		default: return "empty";
		}
	}
	
	public void generatePrologFile() throws IOException {
		FileOutputStream outputStream = new FileOutputStream("facts.pl");
		String file = "";
		
		file += "gridSize(" + grid.length + ", " + grid[0].length + ").\n";
		file += "maxDragon(" + maxDragonGlass + ").\n";
		file += "currentDragonInit(" + currentDragonGlass + ").\n";
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				
				if(grid[i][j].getType().equals("obstacle")) {
					file += "obstacle(" + i + ", " + j + ").\n";
				}
				else if(grid[i][j].getType().equals("whiteWalker")) {
					file += "whiteWalker(" + i + ", " + j + ").\n";
				}
				else if(grid[i][j].getType().equals("empty")) {
					file += "emptyInit(" + i + ", " + j + ").\n";
				}
				else if(grid[i][j].getType().equals("JonSnow")){
					file += "jonInit(" + i + ", " + j + ").\n";
					file += "emptyInit(" + i + ", " + j + ").\n";
				}
				else {
					file += "dragonStone(" + i + ", " + j + ").\n";
					file += "emptyInit(" + i + ", " + j + ").\n";
				}
			}
		}
		
	    byte[] strToBytes = file.getBytes();
	    outputStream.write(strToBytes);
	    outputStream.close();
	}
	
	//Deletes all files
	public void deleteFile() {
        File file = new File("facts.pl");
        file.delete();
	}

	public static void main(String[] args) throws IOException {
		genGrid g = new genGrid();
		g.deleteFile();
//		try {
//			g.genGrid2("p1.txt");
//		}catch(Exception e) {
//			System.out.println("File not Found");
//		}
		g.genGrid();
		g.printGrid(g.grid);
		g.generatePrologFile();
	}
}
