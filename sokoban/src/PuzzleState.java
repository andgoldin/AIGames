import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Represents a state in a Sokoban Puzzle.
 * @author Andrew Goldin
 */
public class PuzzleState implements Comparable<PuzzleState> {

	public static final int UP = 1,
			DOWN = 2,
			LEFT = 3,
			RIGHT = 4;

	public static final char WALL = '#',
			EMPTY_GOAL = '.',
			PLAYER_ON_FLOOR = '@',
			PLAYER_ON_GOAL = '+',
			BOX_ON_FLOOR = '$',
			BOX_ON_GOAL = '*',
			FLOOR = ' ';

	private char[][] grid, newGrid;
	private int pRow, pCol, prNew, pcNew;
	private String moveList;
	private char dir;

	private int searchType, heuristic;
	
	private coord[] boxes, newBoxes, goals;


	/**
	 * Creates a state with a given board, the moves leading up to that state,
	 * and whether or not the state is presently involved in a uniform cost search.
	 * @param state the current board state as a 2D char array
	 * @param moves the sequence of moves leading up to this state
	 * @param ucSearch whether the puzzle is involved in a uniform cost search
	 */
	public PuzzleState(char[][] state, String moves, int type, int heur) {
		grid = state;
		resetNewGrid();
		moveList = new String(moves);
		updatePositions();
		searchType = type;
		heuristic = heur;
	}


	/**
	 * Creates a state with a given board, the moves leading up to that state,
	 * the coordinates of the player on the grid, and whether or not the state
	 * is presently involved in a uniform cost search.
	 * @param state the current board state as a 2D char array
	 * @param movesthe sequence of moves leading up to this state
	 * @param playerRow the player's row in the new state
	 * @param playerCol the player's column in the new state
	 * @param ucSearch whether the puzzle is involved in a uniform cost search
	 */
	public PuzzleState(char[][] state, String moves, int playerRow, int playerCol,
			coord[] boxes, coord[] goals, int type, int heur) {
		grid = state;
		resetNewGrid();
		moveList = new String(moves);
		pRow = playerRow;
		pCol = playerCol;
		this.boxes = boxes;
		this.goals = goals;
		searchType = type;
		heuristic = heur;
	}


	/**
	 * Returns a new PuzzleState with the search type altered.
	 * @param type the new search type
	 * @return a new PuzzleState
	 */
	public PuzzleState setSearchType(int type) {
		return new PuzzleState(grid, moveList, pRow, pCol, boxes, goals, type, heuristic);
	}
	
	
	/**
	 * Returns a new PuzzleState with the search heuristic altered.
	 * Only relevant for greedy best-first and A* searches.
	 * @param type the new search heuristic
	 * @return a new PuzzleState
	 */
	public PuzzleState setSearchHeuristic(int heur) {
		return new PuzzleState(grid, moveList, pRow, pCol, boxes, goals, searchType, heur);
	}


	// private method
	private void resetNewGrid() {
		newGrid = grid.clone();
		for (int i = 0; i < grid.length; i++) {
			newGrid[i] = grid[i].clone();
		}
	}


	// private method
	private void resetNewBoxes() {
		newBoxes = boxes.clone();
		for (int i = 0; i < newBoxes.length; i++) {
			newBoxes[i] = new coord(boxes[i]);
		}
	}


	/**
	 * The number of moves leading up to the current state
	 * @return the number of moves
	 */
	public int getNumMoves() {
		return moveList.length();
	}


	/**
	 * The list of moves as a string containing a sequence of the letters (u, d, l, r).
	 * If a letter is upper case, a box push occurred at that step.
	 * @return the list of moves
	 */
	public String getMoves() {
		return moveList;
	}


	/**
	 * Returns the move sequence as a comma-separated list. For printing purposes.
	 * @return the formatted move sequence.
	 */
	public String getSequence() {
		String seq = "";
		for (int i = 0; i < moveList.length(); i++) {
			if (i == 0) seq += moveList.charAt(i);
			else seq += ", " + moveList.charAt(i);
		}
		return seq;
	}


	/**
	 * Returns the number of box pushes that have occurred leading up to this state.
	 * @return the number of pushes
	 */
	public int getNumPushes() {
		int amount = 0;
		for (int i = 0; i < moveList.length(); i++) {
			if (Character.isUpperCase(moveList.charAt(i))) {
				amount++;
			}
		}
		return amount;
	}


	// private method
	private int getBoxIndex(int row, int col) {
		for (int i = 0; i < boxes.length; i++) {
			if (boxes[i].row == row && boxes[i].col == col) {
				return i;
			}
		}
		return -1;
	}

	// private method, updates the positions of boxes, goals, and player
	private void updatePositions() {
		LinkedList<coord> boxLocs = new LinkedList<coord>();
		LinkedList<coord> goalLocs = new LinkedList<coord>();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (grid[i][j] == PLAYER_ON_FLOOR || grid[i][j] == PLAYER_ON_GOAL) {
					pRow = i;
					pCol = j;
				}
				if (grid[i][j] == BOX_ON_FLOOR || grid[i][j] == BOX_ON_GOAL) {
					boxLocs.add(new coord(i, j));
				}
				if (grid[i][j] == PLAYER_ON_GOAL || grid[i][j] == EMPTY_GOAL || grid[i][j] == BOX_ON_GOAL) {
					goalLocs.add(new coord(i, j));
				}
			}
		}
		boxes = boxLocs.toArray(new coord[boxLocs.size()]);
		goals = goalLocs.toArray(new coord[goalLocs.size()]);
	}


	/**
	 * Determines if the puzzle is in a goal state. That is to say, there are no
	 * empty goals or boxes on the floor.
	 * @return true if goal state is reached, false otherwise.
	 */
	public boolean isGoalState() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if (grid[i][j] == EMPTY_GOAL || grid[i][j] == BOX_ON_FLOOR) {
					return false;
				}
			}
		}
		return true;
	}


	// first search heuristic: computes the total manhattan distance between box/goal pairs
	private int computeMinManhattanDistance() {
		boolean[] goalsTaken = new boolean[goals.length];
		int[][] pairings = new int[boxes.length][goals.length];
		int currentMin = 0, manhattan = 0, minIndex = 0;
		for (int i = 0; i < boxes.length; i++) {
			currentMin = Integer.MAX_VALUE;
			for (int j = i; j < goals.length; j++) {
				if (!goalsTaken[j]) {
					manhattan = Math.abs(boxes[i].row - goals[j].row) + Math.abs(boxes[i].col - goals[j].col);
					if (manhattan < currentMin && !goalsTaken[j]) {
						currentMin = manhattan;
						minIndex = j;
					}
				}
			}
			goalsTaken[minIndex] = true;
			pairings[i][minIndex] = currentMin;
		}
		int sum = 0;
		for (int i = 0; i < pairings.length; i++) {
			for (int j = 0; j < pairings[i].length; j++) {
				sum += pairings[i][j];
			}
		}
		return sum;
	}
	
	
	// second search heuristic: computes the total straight line distance between box/goal pairs
	private int computeMinStraightLineDistance() {
		boolean[] goalsTaken = new boolean[goals.length];
		double[][] pairings = new double[boxes.length][goals.length];
		int minIndex = 0;
		double straightLine = 0.0, currentMin = 0.0;
		for (int i = 0; i < boxes.length; i++) {
			currentMin = Double.MAX_VALUE;
			for (int j = i; j < goals.length; j++) {
				if (!goalsTaken[j]) {
					straightLine = Math.sqrt((boxes[i].row - goals[j].row) * (boxes[i].row - goals[j].row)
							+ (boxes[i].col - goals[j].col) * (boxes[i].col - goals[j].col));
					if (straightLine < currentMin && !goalsTaken[j]) {
						currentMin = straightLine;
						minIndex = j;
					}
				}
			}
			goalsTaken[minIndex] = true;
			pairings[i][minIndex] = currentMin;
		}
		double sum = 0;
		for (int i = 0; i < pairings.length; i++) {
			for (int j = 0; j < pairings[i].length; j++) {
				sum += pairings[i][j];
			}
		}
		return (int) Math.round(sum);
	}


	/**
	 * Returns the result of the evaluation function, which is dependent upon the search type.
	 * @param searchType the search type being used
	 * @return the function result
	 */
	public int evaluate(int searchType) {
		if (searchType == Solver.BFS) {
			return getNumMoves();
		}
		if (searchType == Solver.DFS) {
			return getNumMoves();
		}
		if (searchType == Solver.UCS) {
			return getNumMoves() + getNumPushes();
		}
		if (searchType == Solver.GBFS) {
			if (heuristic == Solver.MANHATTAN) {
				return computeMinManhattanDistance();
			}
			else {
				return computeMinStraightLineDistance();
			}
		}
		if (searchType == Solver.A_STAR) {
			if (heuristic == Solver.MANHATTAN) {
				return getNumMoves() + computeMinManhattanDistance();
			}
			else {
				return getNumMoves() + computeMinStraightLineDistance();
			}
		}
		return -1;
	}


	/**
	 * Generates a new PuzzleState based on a move direction.
	 * @param direction the direction to move the player
	 * @return an updated PuzzleState with positions recalculated and move list updated
	 */
	public PuzzleState generateMove(int direction) {
		resetNewGrid();
		resetNewBoxes();
		dir = ' ';
		prNew = pRow;
		pcNew = pCol;
		if (direction == UP) {
			dir = 'u';
			prNew--;
			// if there's a box, move the box
			if (grid[pRow - 1][pCol] == BOX_ON_FLOOR || grid[pRow - 1][pCol] == BOX_ON_GOAL) {
				// capital means a push
				dir = 'U';
				newGrid[pRow - 2][pCol] = grid[pRow - 2][pCol] == EMPTY_GOAL ? BOX_ON_GOAL : BOX_ON_FLOOR;
				newBoxes[getBoxIndex(pRow - 1, pCol)].row--;
			}
			// move the player
			newGrid[pRow - 1][pCol] = grid[pRow - 1][pCol] == EMPTY_GOAL || grid[pRow - 1][pCol] == BOX_ON_GOAL ?
					PLAYER_ON_GOAL : PLAYER_ON_FLOOR;
		}
		else if (direction == DOWN) {
			dir = 'd';
			prNew++;
			// if there's a box, move the box
			if (grid[pRow + 1][pCol] == BOX_ON_FLOOR || grid[pRow + 1][pCol] == BOX_ON_GOAL) {
				// capital means a push
				dir = 'D';
				newGrid[pRow + 2][pCol] = grid[pRow + 2][pCol] == EMPTY_GOAL ? BOX_ON_GOAL : BOX_ON_FLOOR;
				newBoxes[getBoxIndex(pRow + 1, pCol)].row++;
			}
			// move the player
			newGrid[pRow + 1][pCol] = grid[pRow + 1][pCol] == EMPTY_GOAL || grid[pRow + 1][pCol] == BOX_ON_GOAL ?
					PLAYER_ON_GOAL : PLAYER_ON_FLOOR;
		}
		else if (direction == LEFT) {
			dir = 'l';
			pcNew--;
			// if there's a box, move the box
			if (grid[pRow][pCol - 1] == BOX_ON_FLOOR || grid[pRow][pCol - 1] == BOX_ON_GOAL) {
				// capital means a push
				dir = 'L';
				newGrid[pRow][pCol - 2] = grid[pRow][pCol - 2] == EMPTY_GOAL ? BOX_ON_GOAL : BOX_ON_FLOOR;
				newBoxes[getBoxIndex(pRow, pCol - 1)].col--;
			}
			// move the player
			newGrid[pRow][pCol - 1] = grid[pRow][pCol - 1] == EMPTY_GOAL || grid[pRow][pCol - 1] == BOX_ON_GOAL ?
					PLAYER_ON_GOAL : PLAYER_ON_FLOOR;			
		}
		else if (direction == RIGHT) {
			dir = 'r';
			pcNew++;
			// if there's a box, move the box
			if (grid[pRow][pCol + 1] == BOX_ON_FLOOR || grid[pRow][pCol + 1] == BOX_ON_GOAL) {
				// capital means a push
				dir = 'R';
				newGrid[pRow][pCol + 2] = grid[pRow][pCol + 2] == EMPTY_GOAL ? BOX_ON_GOAL : BOX_ON_FLOOR;
				newBoxes[getBoxIndex(pRow, pCol + 1)].col++;
			}
			// move the player
			newGrid[pRow][pCol + 1] = grid[pRow][pCol + 1] == EMPTY_GOAL || grid[pRow][pCol + 1] == BOX_ON_GOAL ?
					PLAYER_ON_GOAL : PLAYER_ON_FLOOR;
		}

		// update where the player left from
		newGrid[pRow][pCol] = grid[pRow][pCol] == PLAYER_ON_GOAL ? EMPTY_GOAL : FLOOR;

		return new PuzzleState(newGrid, moveList + dir, prNew, pcNew, newBoxes, goals, searchType, heuristic);
	}


	/**
	 * Determines if the player can move in a given direction in the current state.
	 * @param direction the attempted direction of movement
	 * @return true if a move can be performed, false otherwise
	 */
	public boolean canMove(int direction) {
		if (direction == UP) {
			if (grid[pRow - 1][pCol] == WALL) {
				return false;
			}
			if ((grid[pRow - 1][pCol] == BOX_ON_FLOOR || grid[pRow - 1][pCol] == BOX_ON_GOAL)
					&& (grid[pRow - 2][pCol] == BOX_ON_FLOOR || grid[pRow - 2][pCol] == BOX_ON_GOAL || grid[pRow - 2][pCol] == WALL)) {
				return false;
			}
		}
		else if (direction == DOWN) {
			if (grid[pRow + 1][pCol] == WALL) {
				return false;
			}
			if ((grid[pRow + 1][pCol] == BOX_ON_FLOOR || grid[pRow + 1][pCol] == BOX_ON_GOAL)
					&& (grid[pRow + 2][pCol] == BOX_ON_FLOOR || grid[pRow + 2][pCol] == BOX_ON_GOAL || grid[pRow + 2][pCol] == WALL)) {
				return false;
			}
		}
		else if (direction == LEFT) {
			if (grid[pRow][pCol - 1] == WALL) {
				return false;
			}
			if ((grid[pRow][pCol - 1] == BOX_ON_FLOOR || grid[pRow][pCol - 1] == BOX_ON_GOAL)
					&& (grid[pRow][pCol - 2] == BOX_ON_FLOOR || grid[pRow][pCol - 2] == BOX_ON_GOAL || grid[pRow][pCol - 2] == WALL)) {
				return false;
			}
		}
		else if (direction == RIGHT) {
			if (grid[pRow][pCol + 1] == WALL) {
				return false;
			}
			if ((grid[pRow][pCol + 1] == BOX_ON_FLOOR || grid[pRow][pCol + 1] == BOX_ON_GOAL)
					&& (grid[pRow][pCol + 2] == BOX_ON_FLOOR || grid[pRow][pCol + 2] == BOX_ON_GOAL || grid[pRow][pCol + 2] == WALL)) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Determines if two Puzzle states are equal by comparing the board contents.
	 * @return true if the boards (or grids) of each PuzzleState are equal.
	 */
	public boolean equals(Object other) {
		return Arrays.deepEquals(this.grid, ((PuzzleState) other).grid);
	}


	/**
	 * Returns a string representation of the current state, including the board and player position.
	 * @return a String representing the current state
	 */
	public String toString() {
		String puzzle = "";
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				puzzle += grid[i][j];
			}
			puzzle += "\n";
		}
		//puzzle += "Player: (" + pRow + ", " + pCol + ")";
		return puzzle;
	}

	/**
	 * Generates a PuzzleState from a text file.
	 * @param filename the specified file path
	 * @param uc whether uniform cost will be performed
	 * @return a PuzzleState based on the given file
	 */
	public static PuzzleState parseFile(String filename, int type, int heur) {
		Scanner read = null;
		try {
			read = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int length = Integer.parseInt(read.nextLine());
		char[][] pgrid = new char[length][];
		for (int i = 0; i < length; i++) {
			pgrid[i] = read.nextLine().toCharArray();
		}
		read.close();
		return new PuzzleState(pgrid, "", type, heur);
	}


	/**
	 * Compares two PuzzleStates by cost from root.
	 * @return 1 if this.cost > other.cost, -1 if this.cost < other.cost, 0 otherwise
	 */
	public int compareTo(PuzzleState other) {
		if (this.evaluate(searchType) > other.evaluate(other.searchType)) return 1;
		else if (this.evaluate(searchType) < other.evaluate(other.searchType)) return -1;
		else return 0;
	}


	// represents a grid coordinate
	private class coord {
		public int row, col;
		public coord(int r, int c) {
			row = r; col = c;
		}
		public coord(coord o) {
			row = o.row; col = o.col;
		}
	}

}