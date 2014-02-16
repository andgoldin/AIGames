import java.util.ArrayList;
import java.util.Scanner;

class Player {

	public static final int HUMAN = 0, RANDOM = 1, AGENT = 2;

	private char myChar, opponentChar;
	private int evalScore, mode;
	private ChainInfo evalChain, potentialLossChain, childChain;
	private ArrayList<ChainInfo> childChains;
	private Coord chosenMove, bestMove;
	private Scanner input;
	private long timeLimit, startTime;

	public Player(int playerMode, int timeLimit, char me, char them) {
		mode = playerMode;
		if (mode == HUMAN) input = new Scanner(System.in);
		this.timeLimit = (long) (timeLimit * 1000);
		myChar = me;
		opponentChar = them;
		chosenMove = new Coord(0, 0);
		childChains = new ArrayList<ChainInfo>();
	}
	
	public int alphaBeta(GameState start, int depth, int alpha, int beta, boolean maximizingPlayer) {
		if (depth == 0 || start.isDraw()) return evaluate(start);
		GameState[] children = start.getChildStates();
		if (children == null || children.length == 0) return evaluate(start);
		if (maximizingPlayer) {
			if (System.currentTimeMillis() - startTime >= timeLimit) return evaluate(start);
			for (int i = 0; i < children.length; i++) {
				alpha = Math.max(alpha, alphaBeta(children[i], depth - 1, alpha, beta, false));
				if (alpha >= beta) break;
			}
			return alpha;
		}
		else {
			for (int i = 0; i < children.length; i++) {
				beta = Math.min(beta, alphaBeta(children[i], depth - 1, alpha, beta, true));
				if (alpha >= beta) break;
			}
			return beta;
		}
	}

	public Coord chooseMove(GameState state) {
		if (mode == HUMAN) {
			System.out.println("\n" + state);
			//System.out.println(g.containsChain(4, 0, false));
			System.gc();
			System.out.print("\nPlayer " + myChar + ": choose move (row col): ");
			String[] move = input.nextLine().split(" ");
			chosenMove.row = Integer.parseInt(move[0]);
			chosenMove.col = Integer.parseInt(move[1]);
			while (!state.canMove(chosenMove.row, chosenMove.col)) {
				System.out.print("Invalid move! Try again: ");
				move = input.nextLine().split(" ");
				chosenMove.row = Integer.parseInt(move[0]);
				chosenMove.col = Integer.parseInt(move[1]);
			}
		}
		if (mode == RANDOM) {
			System.out.println("\n" + state);
			do {
				chosenMove.row = (int) (Math.random() * state.getBoardSize());
				chosenMove.col = (int) (Math.random() * state.getBoardSize());
			} while (!state.canMove(chosenMove.row, chosenMove.col));

		}
		if (mode == AGENT) {
			System.out.println("\n" + state);
			System.out.println("\nComputer is thinking . . .");
			if (state.isFirstMove()) {
				chosenMove.row = (int) (Math.random() * state.getBoardSize());
				chosenMove.col = (int) (Math.random() * state.getBoardSize());
			}
			else {
				chosenMove = rootSearch(state, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE);
			}
		}
		return chosenMove;
	}

	public int evaluate(GameState state) {
		evalScore = 0;

		// win (winLength ever) or unbounded (winLength - 1) are insta-max
		evalChain = state.containsChain(myChar, state.getWinLength(), -1, true);
		if (evalChain != null) return Integer.MAX_VALUE;
		evalChain = state.containsChain(myChar, state.getWinLength() - 1, 0, false);
		if (evalChain != null) return Integer.MAX_VALUE;

		// loss or unbounded (winLength - 1) from opponent is insta-min
		evalChain = state.containsChain(opponentChar, state.getWinLength(), -1, true);
		if (evalChain != null) return Integer.MIN_VALUE;
		evalChain = state.containsChain(opponentChar, state.getWinLength() - 1, 0, false);
		if (evalChain != null) return Integer.MIN_VALUE;


		// THE GOOD STUFF
		// add 2 for each unbounded chain >= 2
		for (int i = state.getWinLength() - 1; i >= 2; i--) {
			evalChain = state.containsChain(myChar, i, 0, false);
			if (evalChain != null) evalScore += 2;
		}
		// add 1 for each 1-bounded chain >= 2
		for (int i = state.getWinLength() - 1; i >= 2; i--) {
			evalChain = state.containsChain(myChar, i, 1, false);
			if (evalChain != null) evalScore++;
		}

		// THE BAD STUFF
		// subtract 2 for each unbounded chain >= 2 from opponent
		for (int i = state.getWinLength() - 1; i >= 2; i--) {
			evalChain = state.containsChain(opponentChar, i, 0, false);
			if (evalChain != null) evalScore -= 2;
		}
		// subtract 1 for each 1-bounded chain >= 2 from opponent
		for (int i = state.getWinLength() - 1; i >= 2; i--) {
			evalChain = state.containsChain(opponentChar, i, 1, false);
			if (evalChain != null) evalScore--;
		}

		return evalScore;
	}

	public char getChar() {
		return myChar;
	}

	// initial setup for alpha beta to get the actual move rather than just the score
	public Coord rootSearch(GameState start, int depth, int alpha, int beta, int temp) {
		//childChains.clear();
		startTime = System.currentTimeMillis(); // start timer
		if (depth == 0 || start.isDraw()) return start.getMostRecentMove();
		potentialLossChain = start.containsChain(opponentChar, start.getWinLength() - 1, 1, false);
		GameState[] children = start.getChildStates();
		for (int i = 0; i < children.length; i++) {
			if (children[i].isWinState()) {
				//System.out.println("win state detected!");
				return children[i].getMostRecentMove();
			}
			childChain = children[i].containsChain(opponentChar, start.getWinLength() - 1, 2, false);
			if (potentialLossChain != null && childChain != null
					&& potentialLossChain.chainStart.equals(childChain.chainStart)
					&& childChain.numBounds - potentialLossChain.numBounds == 1
					&& !childChains.contains(childChain)) {
				//System.out.println("blocked!");
				childChains.add(childChain);
				return children[i].getMostRecentMove();
			}
		}
		if (children.length < 6) depth = 6;
		else if (children.length < 10) depth = 4;
		else depth = 2;
		for (int i = 0; i < children.length; i++) {
			temp = alphaBeta(children[i], depth, alpha, beta, false);
			if (temp > alpha) {
				bestMove = children[i].getMostRecentMove();
				alpha = temp;
				//System.out.println("Best utility acheived on current path: " + alpha);
			}
		}
		return bestMove;
	}

}