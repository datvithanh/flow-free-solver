package solver.structs;

import solver.Map;

public class Point {
	public int x, y;
	
	public Point(byte B) {
		x = (B >> 4) & 0xf;
		y = B & 0xf;
	}
	
	public Point(byte B, int dir) {
		x = (B >> 4) & 0xf;
		y = B & 0xf;
		this.move(dir);
	}
	
	public Point(Point P, int dir) {
		this.x = P.x;
		this.y = P.y;
		this.move(dir);
	}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		return x * Map.N + y;
	}
	
	@Override
	public boolean equals(Object obj) {
		return x == ((Point)obj).x && y == ((Point)obj).y;
	}
	
	public byte toByte() {
		return (byte)((x << 4) | y);
	}
	
	public int getPos() {
		return x * Map.N + y;
	}
	
	public static int getPos(int x, int y) {
		return x * Map.N + y;
	}
	
	public void move(int dir) {
		switch(dir) {
		case 0: --x; break;
		case 1: ++y; break;
		case 2: ++x; break;
		case 3: --y; break;
		default:
		}
	}
	
	public int howToMove(Point P) {
		if (x - P.x == 1)
			return 0;
		if (y - P.y == -1)
			return 1;
		if (x - P.x == -1)
			return 2;
		if (y -P.y == 1)
			return 3;
		return -1;
	}
	
	public static byte move(byte B, int dir) {
		Point P = new Point(B, dir);
		return P.toByte();
	}
	
	public boolean isValid(int N) {
		return x >= 0 && x < N && y >= 0 && y < N;
	}
	
	public static int getManDist(Point A, Point B) {
		return Math.abs(A.x - B.x) + Math.abs(A.y - B.y);
	}
	
	public int getWallDist(int N) {
		return Math.min(x, N - 1 - x) + Math.min(y, N - 1 - y);
	}
	
	public int getWallMinDist(int N) {
		return Math.min(Math.min(x, N - 1 - x), Math.min(y, N - 1 - y));
	}

}
