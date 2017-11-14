package game;

public class LogicalAgent {
	private NettleSweeper ns;
	private Cell[][] currentWorld;
	private final int UNMARKED = -2;
	private final int FLAGGED = -3; 
	
	public LogicalAgent(NettleSweeper ns) {
		this.ns = ns;
		createGameWorld();
	}
	
	public void openCell(int row, int col) {
		// ask the game to reveal the number behind the cell 
		int number = ns.getCellNumber(row, col);
		currentWorld[row][col].setNumber(number);
	}
	
	public void markCell(int row, int col) {
		// mark the cell indicating that it contains nettle
		currentWorld[row][col].setNumber(FLAGGED);
	}
	
	public void printWorld() {
		// print the status of current world
		for (int i = 0; i < currentWorld.length; i++) {
			for (int j = 0;j < currentWorld[i].length;j++) {
				System.out.print(currentWorld[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
	
	private void createGameWorld() {
		// create game world for the agent
		// all cells are unprobed at the beginning
		int dimension = ns.getDimension();
		currentWorld = new Cell[dimension][dimension];
		for (int i = 0; i < dimension; i++) {
			for (int j = 0;j < dimension;j++) {
				currentWorld[i][j] = new Cell(i, j, UNMARKED);
			}
		}
	}
}
