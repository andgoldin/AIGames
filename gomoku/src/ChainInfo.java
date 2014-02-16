class ChainInfo {
	public char player;
	public int chainSize, numBounds;
	public Coord chainStart;
	public char dir;
	public ChainInfo(char p, int size, int bounds, int row, int col, char direction) {
		player = p;
		chainSize = size;
		numBounds = bounds;
		chainStart = new Coord(row, col);
		dir = direction;
	}
	public String toString() {
		return chainSize + "" + player + "" + numBounds + " " + chainStart.row + "," + chainStart.col + " " + dir;
	}
	public boolean equals(Object other) {
		return player == ((ChainInfo) other).player
				&& chainSize == ((ChainInfo) other).chainSize
				&& numBounds == ((ChainInfo) other).numBounds
				&& chainStart.equals(((ChainInfo) other).chainStart)
				&& dir == ((ChainInfo) other).dir;
	}
}