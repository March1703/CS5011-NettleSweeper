package agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import game.Cell;
import game.NettleSweeper;

public class LogicalAgent {
	private final int UNCOVERED = -2;
	private final int MARKED = -3; 
	private NettleSweeper ns;
	private Cell[][] currentWorld;
	private boolean gameOver = false;
	private ArrayList<Cell> covered = new ArrayList<Cell>();
	private ArrayList<Cell> uncovered = new ArrayList<Cell>();
	private ArrayList<Cell> marked = new ArrayList<Cell>();
	private boolean worldChanged;
	private int randomGuess = 0;
	private int totalNettle;
	
	public LogicalAgent(NettleSweeper ns) {
		this.ns = ns;
		totalNettle = ns.getNumberOfNettle();
		createGameWorld();
	}
	
	public void easyEquationStrategy() {
		// 
		ArrayList<Cell> frontier = getFrontier();
		System.out.println(frontier);
		ArrayList<ArrayList<Cell>> borderingPairs = getBorderingPairs(frontier);
		System.out.println(borderingPairs);
	}
	
	private ArrayList<Cell> getFrontier() {
		ArrayList<Cell> frontier = new ArrayList<Cell>();
		// get uncovered cells which have at least one covered neighbor
		for (int row = 0; row < currentWorld.length; row++) {
			for (int col = 0; col < currentWorld.length; col++) {
				if (coveredNeighborExist(row, col) 
						&& uncovered.contains(currentWorld[row][col])) {
					frontier.add(currentWorld[row][col]);
				}
			}
		}
		return frontier;
	}
	
	private ArrayList<ArrayList<Cell>> getBorderingPairs(ArrayList<Cell> frontier) {
		// need to refine this
		ArrayList<ArrayList<Cell>> borderingPairs = new ArrayList<ArrayList<Cell>>();
		ArrayList<Cell> borderingCells = new ArrayList<Cell>();
		boolean duplicate;
		for (int i = 0; i < frontier.size(); i++) {
			borderingCells = getBorderingCells(frontier.get(i).getRow(), frontier.get(i).getCol());
			for (Cell cell : borderingCells) {
				if (frontier.contains(cell)) {
					ArrayList<Cell> pair = new ArrayList<Cell>();
					pair.add(frontier.get(i));
					pair.add(cell);
					duplicate = checkDuplicatePairs(borderingPairs, pair);
					if (!duplicate) {
						borderingPairs.add(pair);
					}
				}
			}
		}
		return borderingPairs;
	}
	
	private boolean checkDuplicatePairs(ArrayList<ArrayList<Cell>> borderingPairs
			, ArrayList<Cell> pair) {
		boolean duplicate = false;
		for (int i = 0; i < borderingPairs.size(); i++) {
			if (borderingPairs.get(i).containsAll(pair)) {
				duplicate = true;
				break;
			}
		}
		
		return duplicate;
	}
	
	private ArrayList<Cell> getBorderingCells(int row, int col) {
		ArrayList<Cell> borderingCells = new ArrayList<Cell>();
		// cell to the north
		if (isValidNeighbor(row - 1, col)) {
			borderingCells.add(currentWorld[row-1][col]);
		}
		// cell to the south
 		if(isValidNeighbor(row + 1, col)) {
 			borderingCells.add(currentWorld[row+1][col]);
 	    }
 		// cell to the east
 		if(isValidNeighbor(row, col + 1)) {
 			borderingCells.add(currentWorld[row][col+1]);
 	    }
 		// cell to the west
 		if(isValidNeighbor(row, col - 1)) {
 			borderingCells.add(currentWorld[row][col-1]);
 	    }
 		return borderingCells;
	}
	
	private boolean coveredNeighborExist(int row, int col) {
		ArrayList<Cell> neighbors = getAllNeighbors(row, col);
		for (Cell neighbor : neighbors) {
			if (covered.contains(neighbor)) {
				return true;
			}
		}
		return false;
	}
	
	public void singlePointStrategy() {
		for (int row = 0; row < currentWorld.length; row++) {
			for (int col = 0; col < currentWorld.length; col++) {
				if (covered.contains(currentWorld[row][col])) {
					checkAllNeighbors(row, col);
				}
			}
		}
	}
	
	private void checkAllNeighbors(int row, int col) {
		ArrayList<Cell> neighbors = getAllNeighbors(row, col);
		for (Cell neighbor : neighbors) {
			if (uncovered.contains(neighbor) || marked.contains(neighbor)) {
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
			if (neighbor.getNumber() == MARKED) {
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
			if (neighbor.getNumber() == MARKED) {
				nettleCount++;
			}
		}
		if (cell.getNumber() - nettleCount == unmarkedCount) {
			return true;
		} else {
			return false;
		}
	}
	
	public void randomGuessStrategy() {
		// pick a random cell from covered list and uncover that cell
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(covered.size());
		Cell cell = covered.get(index);
		openCell(cell.getRow(), cell.getCol());
		randomGuess++;
	}
	
	public void openCell(int row, int col) {
		System.out.println("reveal " + row + " " + col);
		// ask the game to reveal the number behind the cell 
		int number = ns.getCellNumber(row, col);
		currentWorld[row][col].setNumber(number);
		// update list of uncovered and covered cells
		covered.remove(currentWorld[row][col]);
		uncovered.add(currentWorld[row][col]);
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
			if (!uncovered.contains(neighbor) && !marked.contains(neighbor)) {
				openCell(neighbor.getRow(), neighbor.getCol());
			}
		}
	}
	
	public void openAllCells() {
		// open the remaining cells
		ArrayList<Cell> remainingCells = new ArrayList<Cell>();
		remainingCells.addAll(covered);
		for (Cell cell : remainingCells) {
			openCell(cell.getRow(), cell.getCol());
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
		// returns true if cell is not out of bound
		return !(row < 0 || row >= currentWorld.length || col < 0 || col >= currentWorld.length);
	}
	
	public void markCell(int row, int col) {
		System.out.println("mark " + row + " " + col);
		// mark the cell indicating that it contains nettle
		currentWorld[row][col].setNumber(MARKED);
		// update list of uncovered and covered cells
		covered.remove(currentWorld[row][col]);
		marked.add(currentWorld[row][col]);
		worldChanged = true;
	}
	
	public boolean getGameOver() {
		return this.gameOver;
	}
	
	public ArrayList<Cell> getCovered() {
		return this.covered;
	}
	
	public ArrayList<Cell> getUncovered() {
		return this.uncovered;
	}
	
	public ArrayList<Cell> getMarked() {
		return this.marked;
	}
	
	public boolean getWorldChanged() {
		return this.worldChanged;
	}
	
	public int getTotalNettle() {
		return this.totalNettle;
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
	
	public void printSummary() {
		if (!gameOver) {
			System.out.println("\nSummary");
			printWorld();
			System.out.println("game won");
			System.out.println("random guess: " + randomGuess);
		} else {
			System.out.println("\nSummary");
			printWorld();
			System.out.println("game lost");
			System.out.println("random guess: " + randomGuess);
		}
	}
	
	private void createGameWorld() {
		// create game world for the agent
		// all cells are covered at the beginning
		int dimension = ns.getDimension();
		currentWorld = new Cell[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0;j < dimension;j++) {
				currentWorld[i][j] = new Cell(i, j, UNCOVERED);
				covered.add(currentWorld[i][j]);
			}
		}
	}
}