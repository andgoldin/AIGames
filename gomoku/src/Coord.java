public class Coord {
	public int row, col;
	public Coord(int r, int c) {
		row = r;
		col = c;
	}
	public Coord(Coord other) {
		row = other.row;
		col = other.col;
	}
	public boolean equals(Coord other) {
		return row == other.row && col == other.col;
	}
	public String toString() {
		return "[" + row + ", " + col + "]";
	}
}