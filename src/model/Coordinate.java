package model;

public class Coordinate {
	private int xco, yco;
	
	public Coordinate(int x, int y) {
		xco = x;
		yco = y;
	}
	
	public int getX() {
		return xco;
	}
	
	public int getY() {
		return yco;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (!(obj instanceof Coordinate)) {
			return false;
		}
		else{
			Coordinate newObj = (Coordinate) obj;
			return compareTo((Coordinate) newObj) == 0;
		}
	}

	public int compareTo(Coordinate obj) {
		if (this.xco == obj.getX() && this.yco == obj.getY()) {
			return 0;
		}
		return -1;
	}
	
	@Override
	public String toString() {
		return "(" + xco + "," + yco + ")";
	}
}
