// Gomoku Agent (Project 3)
// Andrew Goldin (adg2160)
// COMS W4701 Artificial Intelligence


public class Gomoku {

	public static void main(String[] args) {

		int mode = 0, dimension = 3, winLength = 3, timeLimit = 10;
		boolean agentFirst = false;
		
		if (args.length < 3 && args.length > 5) {
			System.err.println("Invalid argument list, please retry.");
			System.exit(0);
		}

		mode = Integer.parseInt(args[0]);
		if (args.length == 3 && mode != 0) {
			System.err.println("Modes other than 0 (human vs human) require additional parameters, please retry.");
			System.exit(0);
		}
		dimension = Integer.parseInt(args[1]);
		winLength = Integer.parseInt(args[2]);
		if (args.length > 3) {
			timeLimit = Integer.parseInt(args[3]);
			if (args.length == 5) agentFirst = Boolean.parseBoolean(args[4]);
		}

		Player p1 = null, p2 = null;

		GameState g = new GameState(dimension, winLength);

		if (mode == 0) {
			System.out.println("Gomoku Interactive!");
			p1 = new Player(Player.HUMAN, timeLimit, GameState.PLAYER_1, GameState.PLAYER_2);
			p2 = new Player(Player.HUMAN, timeLimit, GameState.PLAYER_2, GameState.PLAYER_1);
		}
		if (mode == 1) {
			System.out.println("Gomoku Interactive!");
			if (agentFirst) {
				p1 = new Player(Player.AGENT, timeLimit, GameState.PLAYER_1, GameState.PLAYER_2);
				p2 = new Player(Player.HUMAN, timeLimit, GameState.PLAYER_2, GameState.PLAYER_1);
			}
			else {
				p1 = new Player(Player.HUMAN, timeLimit, GameState.PLAYER_1, GameState.PLAYER_2);
				p2 = new Player(Player.AGENT, timeLimit, GameState.PLAYER_2, GameState.PLAYER_1);
			}
		}
		if (mode == 2) {
			if (agentFirst) {
				p1 = new Player(Player.AGENT, timeLimit, GameState.PLAYER_1, GameState.PLAYER_2);
				p2 = new Player(Player.RANDOM, timeLimit, GameState.PLAYER_2, GameState.PLAYER_1);
			}
			else {
				p1 = new Player(Player.RANDOM, timeLimit, GameState.PLAYER_1, GameState.PLAYER_2);
				p2 = new Player(Player.AGENT, timeLimit, GameState.PLAYER_2, GameState.PLAYER_1);
			}
		}
		if (mode == 3) {
			p1 = new Player(Player.AGENT, timeLimit, GameState.PLAYER_1, GameState.PLAYER_2);
			p2 = new Player(Player.AGENT, timeLimit, GameState.PLAYER_2, GameState.PLAYER_1);
		}
		char winner = GameState.EMPTY_SPACE;
		while (!g.isWinState() && !g.isDraw()) {
			Coord move = p1.chooseMove(g);
			System.out.println("Player " + p1.getChar() + " moves: " + move);
			g = g.makeMove(move);
			if (g.isWinState()) {
				winner = g.getWinner();
				break;
			}
			if (g.isDraw()) {
				break;
			}
			move = p2.chooseMove(g);
			System.out.println("Player " + p2.getChar() + " moves: " + move);
			g = g.makeMove(move);
			if (g.isWinState()) {
				winner = g.getWinner();
				break;
			}
		}
		System.out.println("\n" + g + "\n\nGame over! " + (g.isDraw() ? "DRAW." : "Player " + winner + " wins!"));
	}
}