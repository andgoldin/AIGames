import java.util.Scanner;

public class Sokoban {

	// cla's: <filename> <searchtype> <depthlimit (if searchtype is DFS)> <heuristic (if searchtype is GBFS or A*)> <stats>
	// if only filename is provided, will start interactive game
	public static void main(String[] args) throws Exception {

		PuzzleState p = PuzzleState.parseFile(args[0], Solver.BFS, Solver.MANHATTAN);
		
		// if only one argument, start interactive game
		if (args.length == 1) {
			Scanner input = new Scanner(System.in);
			System.out.println("Sokoban Interactive!");
			while (!p.isGoalState()) {
				System.out.println("\nBoard:\n" + p);
				System.out.print("Choose move (u, d, l, r): ");
				String move = input.nextLine();
				if (move.equals("u") && p.canMove(PuzzleState.UP)) {
					p = p.generateMove(PuzzleState.UP);
				}
				if (move.equals("d") && p.canMove(PuzzleState.DOWN)) {
					p = p.generateMove(PuzzleState.DOWN);
				}
				if (move.equals("l") && p.canMove(PuzzleState.LEFT)) {
					p = p.generateMove(PuzzleState.LEFT);
				}
				if (move.equals("r") && p.canMove(PuzzleState.RIGHT)) {
					p = p.generateMove(PuzzleState.RIGHT);
				}
			}
			input.close();			
			System.out.println("\nBoard\n" + p + "\nYou did it! It took you " + p.getNumMoves()
					+ " moves, with " + p.getNumPushes() + " pushes.");
		}
		
		// else setup and run solver
		else {
			boolean includeStats = args[args.length - 1].equalsIgnoreCase("s") ? true : false;
			Solver s = new Solver(includeStats);
			
			System.out.println("Working . . .\n");
			
			if (args[1].equalsIgnoreCase("bfs")) {
				s.breadthFirstSearch(p);
			}
			else if (args[1].equalsIgnoreCase("ucs")) {
				s.uniformCostSearch(p);
			}
			else if (args[1].equalsIgnoreCase("dfs")) {
				s.depthFirstSearch(p, Integer.parseInt(args[2]));
			}
			else if (args[1].equalsIgnoreCase("gbfs")) {
				int heuristic = args[2].equals("1") ? Solver.MANHATTAN : Solver.STRAIGHT_LINE;
				s.greedyBestFirstSearch(p, heuristic);
			}
			else if (args[1].equalsIgnoreCase("astar")) {
				int heuristic = args[2].equals("1") ? Solver.MANHATTAN : Solver.STRAIGHT_LINE;
				s.aStarSearch(p, heuristic);
			}
			
			System.out.println(s.report());
		}

	}

}