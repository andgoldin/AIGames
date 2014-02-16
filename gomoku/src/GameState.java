import java.util.ArrayList;
import java.util.Arrays;

public class GameState {

	public static final char PLAYER_1 = 'X',
			PLAYER_2 = 'O',
			EMPTY_SPACE = '-';

	private int boardSize, winLength, currentChainSize, boundCount, nextRow, nextCol;
	private char[][] board;
	private boolean initialMove;
	private Coord lastMove;
	private char playerWhoJustWent, playerWhoGoesNext, winner;
	private String gui;
	private ChainInfo winnerChain;
	private ArrayList<GameState> childStates;

	public GameState(int size, int length) {
		boardSize = size;
		winLength = length;
		board = new char[size][size];
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = EMPTY_SPACE;
			}
		}
		lastMove = null;
		childStates = new ArrayList<GameState>();
		playerWhoJustWent = PLAYER_2;
		playerWhoGoesNext = PLAYER_1;
		initialMove = true;
		winner = EMPTY_SPACE;
	}
	
	// constructor for new board with the given player and move
	public GameState(char[][] currentBoard, char player, int row, int col, int length) {
		boardSize = currentBoard.length;
		winLength = length;
		board = new char[boardSize][boardSize];
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = currentBoard[i][j];
			}
		}
		board[row][col] = player;
		lastMove = new Coord(row, col);
		playerWhoJustWent = player;
		playerWhoGoesNext = player == PLAYER_1 ? PLAYER_2 : PLAYER_1;
		childStates = new ArrayList<GameState>();
		initialMove = false;
		winner = EMPTY_SPACE;
	}

	public boolean canMove(int row, int col) {
		if (row < 0 || col < 0 || row >= boardSize || col >= boardSize) return false;
		else if (board[row][col] != EMPTY_SPACE) return false;
		return true;
	}

	// returns desired chain
	public ChainInfo containsChain(char player, int desiredSize, int desiredBounds, boolean checkWinState) {
		boundCount = 0;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == player) {
					// check down
					currentChainSize = 1;
					while (i + currentChainSize < boardSize && board[i + currentChainSize][j] == board[i][j]
							&& currentChainSize < desiredSize) currentChainSize++;
					boundCount = 0;
					if (i - 1 < 0 || board[i - 1][j] != EMPTY_SPACE && board[i - 1][j] != board[i][j]) boundCount++;
					if (i + currentChainSize >= boardSize || board[i + currentChainSize][j] != EMPTY_SPACE) boundCount++;
					if (currentChainSize == desiredSize && (checkWinState || boundCount == desiredBounds)
							&& (i - 1 < 0 || board[i - 1][j] != board[i][j])
							&& (i + currentChainSize >= boardSize || board[i + currentChainSize][j] != board[i][j])) {
						return new ChainInfo(board[i][j], currentChainSize, boundCount, i, j, 'd');
					}

					// check right
					currentChainSize = 1;
					while (j + currentChainSize < boardSize && board[i][j + currentChainSize] == board[i][j]
							&& currentChainSize < desiredSize) currentChainSize++;
					boundCount = 0;
					if (j - 1 < 0 || board[i][j - 1] != EMPTY_SPACE && board[i][j - 1] != board[i][j]) boundCount++;
					if (j + currentChainSize >= boardSize || board[i][j + currentChainSize] != EMPTY_SPACE) boundCount++;
					if (currentChainSize == desiredSize && (checkWinState || boundCount == desiredBounds)
							&& (j - 1 < 0 || board[i][j - 1] != board[i][j])
							&& (j + currentChainSize >= boardSize || board[i][j + currentChainSize] != board[i][j])) {
						return new ChainInfo(board[i][j], currentChainSize, boundCount, i, j, 'r');
					}

					// check down right
					currentChainSize = 1;
					while (i + currentChainSize < boardSize && j + currentChainSize < boardSize
							&& board[i + currentChainSize][j + currentChainSize] == board[i][j]
									&& currentChainSize < desiredSize) currentChainSize++;
					boundCount = 0;
					if (i - 1 < 0 || j - 1 < 0 || board[i - 1][j - 1] != EMPTY_SPACE && board[i - 1][j - 1] != board[i][j]) boundCount++;
					if (i + currentChainSize >= boardSize || j + currentChainSize >= boardSize
							|| board[i + currentChainSize][j + currentChainSize] != EMPTY_SPACE) boundCount++;
					if (currentChainSize == desiredSize && (checkWinState || boundCount == desiredBounds)
							&& (i - 1 < 0 || j - 1 < 0 || board[i - 1][j - 1] != board[i][j])
							&& (i + currentChainSize >= boardSize || j + currentChainSize >= boardSize
							|| board[i + currentChainSize][j + currentChainSize] != board[i][j])) {
						return new ChainInfo(board[i][j], currentChainSize, boundCount, i, j, 'b');
					}

					// check down left
					currentChainSize = 1;
					while (i + currentChainSize < boardSize && j - currentChainSize >= 0
							&& board[i + currentChainSize][j - currentChainSize] == board[i][j]
									&& currentChainSize < desiredSize) currentChainSize++;
					boundCount = 0;
					if (i - 1 < 0 || j + 1 >= boardSize || board[i - 1][j + 1] != EMPTY_SPACE && board[i - 1][j + 1] != board[i][j]) boundCount++;
					if (i + currentChainSize >= boardSize || j - currentChainSize < 0
							|| board[i + currentChainSize][j - currentChainSize] != EMPTY_SPACE) boundCount++;
					if (currentChainSize == desiredSize && (checkWinState || boundCount == desiredBounds)
							&& (i - 1 < 0 || j + 1 >= boardSize || board[i - 1][j + 1] != board[i][j])
							&& (i + currentChainSize >= boardSize || j - currentChainSize < 0
							|| board[i + currentChainSize][j - currentChainSize] != board[i][j])) {
						return new ChainInfo(board[i][j], currentChainSize, boundCount, i, j, 'f');
					}
				}
			}
		}
		return null;
	}

	public int getBoardSize() {
		return boardSize;
	}

	public GameState[] getChildStates() {
		childStates.clear();
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] != EMPTY_SPACE) {
					for (int k = i - 1; k <= i + 1; k++) {
						for (int l = j - 1; l <= j + 1; l++) {
							if (canMove(k, l)) {
								childStates.add(makeMove(new Coord(k, l)));
							}
						}
					}
				}
			}
		}
		if (childStates.size() == 0) return null;
		return childStates.toArray(new GameState[childStates.size()]);
	}

	public char getCurrentPlayerTurn() {
		return playerWhoGoesNext;
	}

	public Coord getMostRecentMove() {
		return new Coord(lastMove);
	}

	public int getWinLength() {
		return winLength;
	}

	public char getWinner() {
		return winner;
	}

	public boolean isDraw() {
		// draw when board is full
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == EMPTY_SPACE) return false;
			}
		}
		return isWinState() == false;
	}

	public boolean isFirstMove() {
		return initialMove;
	}

	public boolean isWinState() {
		winnerChain = containsChain(playerWhoJustWent, winLength, -1, true);
		if (winnerChain != null) {
			winner = playerWhoJustWent;
			return true;
		}
		return false;
	}

	public GameState makeMove(Coord c) {
		return new GameState(board, playerWhoGoesNext, c.row, c.col, winLength);
	}

	public GameState randomMove() {
		do {
			nextRow = (int) (Math.random() * boardSize);
			nextCol = (int) (Math.random() * boardSize);
		} while (!canMove(nextRow, nextCol));
		return new GameState(board, playerWhoGoesNext, nextRow, nextCol, winLength);
	}

	public String toString() {
		gui = "   _";
		for (int i = 0; i < boardSize * 3; i++) gui += "_";
		gui += "_  \n";
		gui += "  |";
		for (int i = 0; i < boardSize * 3; i++) gui += " ";
		gui += "  | \n";
		for (int i = 0; i < boardSize; i++) {
			gui += Arrays.toString(board[i]).replace(",", " ").replace("[", (i < 10 ? " " + i : i) + "|  ").replace("]", "  |") + "\n";
		}
		gui += "  |_";
		for (int i = 0; i < boardSize * 3; i++) gui += "_";
		gui += "_| \n";
		gui += "     ";
		for (int i = 0; i < boardSize; i++) gui += (i < 10 ? i + " " : i) + " ";
		return gui;
	}

}