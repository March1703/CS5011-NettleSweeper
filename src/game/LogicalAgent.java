package game;

import java.util.ArrayList;
import java.util.Random;

public class LogicalAgent {
	private final int UNCOVERED = -2;
	private final int FLAGGED = -3; 
	private NettleSweeper ns;
	private Cell[][] currentWorld;
	private boolean gameOver = false;
	private ArrayList<Cell> unopened = new ArrayList<Cell>();
	private ArrayList<Cell> opened = new ArrayList<Cell>();
	private boolean worldChanged;
	private int randomGuess = 0;
	
	public LogicalAgent(NettleSweeper ns) {
		this.ns = ns;
		createGameWorld();
	}
	
	public void randomGuessStrategy() {
		// pick a random cell from unopened list and uncover that cell
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(unopened.size());
		Cell cell = unopened.get(index);
		openCell(cell.getRow(), cell.getCol());
		randomGuess++;
	}
	
	public void singlePointStrategy() {
		for (int row = 0; row < currentWorld.length; row++) {
			for (int col = 0; col < currentWorld.length; col++) {
				if (unopened.contains(currentWorld[row][col])) {
					checkAllNeighbors(row, col);
				}
			}
		}
	}
	
	private void checkAllNeighbors(int row, int col) {
		ArrayList<Cell> neighbors = getAllNeighbors(row, col);
		for (Cell neighbor : neighbors) {
			if (opened.contains(neighbor)) {
				if (allFreeNeighbors(neighbor)) {
					openCell(row, col);
					
					break;
				} else if (allMarkedNeighbors(neighbor)) {
					markCell(row, col);
					break;
				}
			}
		}
	}
	
	private boolean allFreeNeighbors(Cell cell) {
		// true if: cellNumber == nettleCount
		ArrayList<Cell> neighbors = getAllNeighbors(cell.getRow(), cell.getCol());
		int nettleCount = 0;
		for (Cell neighbor : neighbors) {
			if (neighbor.getNumber() == FLAGGED) {
				nettleCount++;
			}
		}
		if (cell.getNumber() == nettleCount) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean allMarkedNeighbors(Cell cell) {
		// true if: cellNumber - nettleCount ==  unmarkedCount
		ArrayList<Cell> neighbors = getAllNeighbors(cell.getRow(), cell.getCol());
		int nettleCount = 0;
		int unmarkedCount = 0;
		for (Cell neighbor : neighbors) {
			if (neighbor.getNumber() == UNCOVERED) {
				unmarkedCount++;
			}
			if (neighbor.getNumber() == FLAGGED) {
				nettleCount++;
			}
		}
		if (cell.getNumber() - nettleCount == unmarkedCount) {
			return true;
		} else {
			return false;
		}
	}
	
	public void openCell(int row, int col) {
		System.out.println("reveal " + row + " " + col);
		// ask the game to reveal the number behind the cell 
		int number = ns.getCellNumber(row, col);
		currentWorld[row][col].setNumber(number);
		// update list of opened and unopened cells
		unopened.remove(currentWorld[row][col]);
		opened.add(currentWorld[row][col]);
		if (number == 0) {
			openAllNeighborCells(row, col);
		}
		worldChanged = true;
		// check if the cell contain nettle
		gameOver = ns.isGameOver(currentWorld[row][col].getNumber());
	}
	
	public void openAllNeighborCells(int row, int col) {
		// open all valid neighbors of current cell
		ArrayList<Cell> neighbors = getAllNeighbors(row, col);
		
		for (Cell neighbor : neighbors) {
			if (!opened.contains(neighbor)) {
				openCell(neighbor.getRow(), neighbor.getCol());
			}
		}
	}
	
	private ArrayList<Cell> getAllNeighbors(int row, int col) {
		ArrayList<Cell> neighbors = new ArrayList<Cell>();
		// cell to the north
		if (isValidNeighbor(row - 1, col)) {
			neighbors.add(currentWorld[row-1][col]);
		}
		// cell to the south
 		if(isValidNeighbor(row + 1, col)) {
 			neighbors.add(currentWorld[row+1][col]);
 	    }
 		// cell to the east
 		if(isValidNeighbor(row, col + 1)) {
 			neighbors.add(currentWorld[row][col+1]);
 	    }
 		// cell to the west
 		if(isValidNeighbor(row, col - 1)) {
 			neighbors.add(currentWorld[row][col-1]);
 	    }
 		// cell to the northeast
		if (isValidNeighbor(row - 1, col + 1)) {
			neighbors.add(currentWorld[row - 1][col+1]);
		}
		// cell to the northwest
		if (isValidNeighbor(row - 1, col - 1)) {
			neighbors.add(currentWorld[row - 1][col-1]);
		}
		// cell to the southeast
 		if(isValidNeighbor(row + 1, col + 1)) {
 			neighbors.add(currentWorld[row+1][col+1]);
 	    }
 		// cell to the southwest
 		if(isValidNeighbor(row + 1, col - 1)) {
 			neighbors.add(currentWorld[row+1][col-1]);
 	    }
 		return neighbors;
	}
	
	private boolean isValidNeighbor(int row, int col) {
		// returns true if cell is unopened and not out of bound
		return !(row < 0 || row >= currentWorld.length || col < 0 || col >= currentWorld.length);
	}
	
	public void markCell(int row, int col) {
		System.out.println("mark " + row + " " + col);
		// mark the cell indicating that it contains nettle
		currentWorld[row][col].setNumber(FLAGGED);
		// update list of opened and unopened cells
		unopened.remove(currentWorld[row][col]);
		opened.add(currentWorld[row][col]);
		worldChanged = true;
	}
	
	public boolean getGameOver() {
		return this.gameOver;
	}
	
	public ArrayList<Cell> getUnopened() {
		return this.unopened;
	}
	
	public ArrayList<Cell> getOpened() {
		return this.opened;
	}
	
	public boolean getWorldChanged() {
		return this.worldChanged;
	}
	
	public int getRandomGuess() {
		return this.randomGuess;
	}
	
	public void setWorldChanged(boolean status) {
		worldChanged = status;
	}
	
	public void printWorld() {
		// print the status of current world
		System.out.println("--------------------");
		for (int i = 0; i < currentWorld.length; i++) {
			for (int j = 0;j < currentWorld[i].length;j++) {
				System.out.printf("%-4s", currentWorld[i][j]);
			}
			System.out.print("\n");
		}
		System.out.println("--------------------");
	}
	
	private void createGameWorld() {
		// create game world for the agent
		// all cells are unopened at the beginning
		int dimension = ns.getDimension();
		currentWorld = new Cell[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0;j < dimension;j++) {
				currentWorld[i][j] = new Cell(i, j, UNCOVERED);
				unopened.add(currentWorld[i][j]);
			}
		}
	}
}
