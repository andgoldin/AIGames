import java.util.LinkedList;
import java.util.PriorityQueue;

public class Solver {

	public static final int BFS = 1,
			DFS = 2,
			UCS = 3,
			GBFS = 4,
			A_STAR = 5;
	
	public static final int MANHATTAN = 1,
			STRAIGHT_LINE = 2;
	
	private long nodesGenerated;
	private long nodesWithPrevStates;
	private long nodesOnFringe;
	private long nodesOnExplored;
	private long runtime;
	
	private boolean foundExplored, statistics;
	
	private String board, sequence, searchType, heuristic;
	
	/**
	 * Default constructor.
	 */
	public Solver(boolean stat) {
		foundExplored = false;
		statistics = stat;
	}
	
	
	/**
	 * Returns the results of the search as a string.
	 * @return the search results
	 */
	public String report() {
		String report = "Board:\n" + board
				+ "\nSearch type: " + searchType
				+ (heuristic.length() > 0 ? " (Cost heuristic: " + heuristic + ")" : "")
				+ "\nSequence: " + sequence
				+ (statistics ? "\n\nStatistics:\nNodes generated: " + nodesGenerated
						+ "\nNodes containing previous states: " + nodesWithPrevStates
						+ "\nNodes on the fringe: " + nodesOnFringe
						+ "\nNodes on explored list: " + nodesOnExplored
						+ "\nTotal runtime: " + (((double) runtime) / 1000) + " seconds" : "");
		return report;
	}
	
	
	/**
	 * Performs a breadth first search to find the solution.
	 * @param start the puzzle start state
	 */
	public void breadthFirstSearch(PuzzleState start) {
		
		board = start.toString();
		searchType = "breadth-first search";
		heuristic = "";
		
		start = start.setSearchType(BFS);
		
		runtime = System.currentTimeMillis();
		nodesGenerated = 0;
		nodesWithPrevStates = 0;
		LinkedList<PuzzleState> fringe = new LinkedList<PuzzleState>();
		LinkedList<PuzzleState> explored = new LinkedList<PuzzleState>();
		fringe.add(start);
		while (!fringe.isEmpty()) {
			PuzzleState current = fringe.remove();
			if (current.isGoalState()) {
				// set stats
				runtime = System.currentTimeMillis() - runtime;
				sequence = current.getSequence();
				nodesOnFringe = fringe.size();
				nodesOnExplored = explored.size();
				return;
			}
			foundExplored = false;
			for (int i = PuzzleState.UP; i <= PuzzleState.RIGHT; i++) {
				// add moves to the fringe if they are valid and do not point back to the previous state
				if (current.canMove(i)) {
					nodesGenerated++;
					PuzzleState next = current.generateMove(i);
					if (!explored.contains(next)) {
						fringe.add(next);
						explored.add(next);
					}
					else {
						foundExplored = true;
					}
				}
			}
			if (foundExplored) nodesWithPrevStates++;
		}
	}

	
	/**
	 * Performs a depth first search with a depth limit to avoid infinite loops.
	 * @param start the puzzle start state
	 * @param limit the depth limit
	 */
	public void depthFirstSearch(PuzzleState start, int limit) {
		
		board = start.toString();
		searchType = "depth-first search (depth limit: " + limit + ")";
		heuristic = "";
		
		start = start.setSearchType(DFS);
		
		runtime = System.currentTimeMillis();
		nodesGenerated = 0;
		nodesWithPrevStates = 0;
		LinkedList<PuzzleState> fringe = new LinkedList<PuzzleState>();
		LinkedList<PuzzleState> explored = new LinkedList<PuzzleState>();
		fringe.push(start);
		while (!fringe.isEmpty()) {
			PuzzleState current = fringe.pop();
			if (current.isGoalState()) {
				runtime = System.currentTimeMillis() - runtime;
				sequence = current.getSequence();
				nodesOnFringe = fringe.size();
				nodesOnExplored = explored.size();
				return;
			}
			// push expansions to the fringe if the limit has not been reached
			if (current.getNumMoves() < limit) {
				foundExplored = false;
				for (int i = PuzzleState.RIGHT; i >= PuzzleState.UP; i--) {
					// add moves to the fringe if they are valid and do not point back to the previous state
					if (current.canMove(i)) {
						nodesGenerated++;
						PuzzleState next = current.generateMove(i);
						if (!explored.contains(next)) {
							fringe.push(next);
							explored.add(next);
						}
						else {
							foundExplored = true;
						}
					}
				}
				if (foundExplored) nodesWithPrevStates++;
			}
		}
	}
	
	/**
	 * Performs a uniform cost search with normal moves having cost 1 and box pushes having cost 2.
	 * @param start the puzzle start state
	 */
	public void uniformCostSearch(PuzzleState start) {
		searchType = "uniform cost search";
		heuristic = "";
		priorityQueueSearch(start.setSearchType(UCS));
	}
	
	/**
	 * Performs a greedy best-first search with a given heuristic function.
	 * @param start the puzzle start state
	 * @param heur the type of heuristic function
	 */
	public void greedyBestFirstSearch(PuzzleState start, int heur) {
		searchType = "greedy best first search";
		heuristic = heur == MANHATTAN ? "total Manhattan distance" : "total straight line distance";
		priorityQueueSearch(start.setSearchType(GBFS).setSearchHeuristic(heur));
	}
	
	/**
	 * Performs an A* search with the given heuristic function.
	 * @param start the puzzle start state
	 * @param heur the type of heuristic function
	 */
	public void aStarSearch(PuzzleState start, int heur) {
		searchType = "A* search";
		heuristic = heur == MANHATTAN ? "total Manhattan distance" : "total straight line distance";
		priorityQueueSearch(start.setSearchType(A_STAR).setSearchHeuristic(heur));
	}
	
	
	// UCS, GBFS, and A* are all based on a priority queue
	private void priorityQueueSearch(PuzzleState start) {
		board = start.toString();
		
		runtime = System.currentTimeMillis();
		nodesGenerated = 0;
		nodesWithPrevStates = 0;
		PriorityQueue<PuzzleState> fringe = new PriorityQueue<PuzzleState>();
		LinkedList<PuzzleState> explored = new LinkedList<PuzzleState>();
		fringe.add(start);
		while (!fringe.isEmpty()) {
			PuzzleState current = fringe.remove();
			if (current.isGoalState()) {
				runtime = System.currentTimeMillis() - runtime;
				sequence = current.getSequence();
				nodesOnFringe = fringe.size();
				nodesOnExplored = explored.size();
				return;
			}
			foundExplored = false;
			for (int i = PuzzleState.UP; i <= PuzzleState.RIGHT; i++) {
				// add moves to the fringe if they are valid and do not point back to the previous state
				if (current.canMove(i)) {
					nodesGenerated++;
					PuzzleState next = current.generateMove(i);
					if (!explored.contains(next)) {
						fringe.add(next);
						explored.add(next);
					}
					else {
						foundExplored = true;
					}
				}
			}
			if (foundExplored) nodesWithPrevStates++;
		}
	}
	
}
