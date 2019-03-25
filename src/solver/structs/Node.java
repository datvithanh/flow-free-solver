// CAU TRUC 1 NODE TRONG A*
package solver.structs;

import java.util.ArrayList;
import java.util.Comparator;

import solver.Map;
import solver.Param;
import solver.branch_bound.ForcedMove;

public class Node {
	
	public State state;	// Trang thai bang
	private int g;		// Chi phi da qua
	private int h;		// Chi phi du kien
	public Node parent;	// Nut cha
	private int[] moveCounts = new int[Map.numFlow];
	
	// Tao nut dau tien ung voi trang thai goc
	public Node(boolean isInit) {		
		if (!isInit)
			return;
		
		state = new State();
		System.arraycopy(Map.map, 0, state.map, 0, Map.map.length);
		for (int c=0; c<Map.numFlow; c++)
			state.cur[c] = Map.begin[c].toByte();
		state.free = Map.N * Map.N - Map.numFlow;
		
		g = 0;
		h = h();	
	}
	
	// Tao nut con khi di chuyen "flow" theo huong "dir", voi chi phi "cost"
	public Node makeMove(int flow, int dir, int cost, boolean real) {
		Node next = new Node(false);
		
		next.state = state.makeMove(flow, dir);
				
		next.g = g + (Param.g?cost:1);		
		next.h = next.h();	
		next.parent = this;
		
		if (!Param.selfTouchable && real) {
			for (int d=0; d<4; d++) {
				Point P = new Point(next.state.cur[flow], d);
				if (P.isValid(Map.N) && P.equals(Map.end[flow]))
					return next.makeMove(flow, d, 0, false);
			}
		}
		
		if (solver.Param.diagnose && next.state.isUnsolvable())
			return null;
		
		return next;
	}
	
	// Sinh tat ca cac nut con co the
	public ArrayList<Node> makeAllMoves() {
		ArrayList<Node> allMoves = new ArrayList<Node>();

		if (Param.forcedMove) {
			Node N = this;
			int forced = Map.forcedMove.findForcedMove(N.state);
			if (forced != -1) {
				while (forced != -1) {
					N = N.makeMove(forced >> 2, forced & 3, 0, true);
					if (N == null)
						return allMoves;
					forced = Map.forcedMove.findForcedMove(N.state);
					//N.state.printState();
				}
				allMoves.add(N);
				return allMoves;
			}
		}
		
		ArrayList<Integer> color = new ArrayList<>();
		for (int c=0; c<Map.numFlow; c++) {
			if ((state.finished & (1 << c)) != 0) {
				moveCounts[c] = -1;
				continue;
			}
			
			color.add(c);
			for (int d=0; d<4; d++)
				if (state.tryMove(c, d) != -1)
					moveCounts[c]++;
		}
		
		if (Param.sortColor != 0) {
			color.sort(new Comparator<Integer>() {
	
				@Override
				public int compare(Integer o1, Integer o2) {
					int cp;
					if (Param.sortColor == 1) {
						cp = ((Integer)moveCounts[o1]).compareTo(moveCounts[o2]);
						if (!Param.out_in || cp != 0) return cp;
					}
					Point P1 = new Point(state.cur[o1]), P2 = new Point(state.cur[o2]);
					cp = ((Integer)P1.getWallMinDist(Map.N)).compareTo(P2.getWallMinDist(Map.N));
					if (cp != 0) return cp;
					return ((Integer)P1.getWallDist(Map.N)).compareTo(P2.getWallDist(Map.N));
				}
				
			});
		}
		
		if (color.size() == 0)
			return allMoves;
		
		for (Integer c: color) {
			for (int d=0; d<4; d++) {
				int move = state.tryMove(c, d);
				if (move == -1) continue;
				
				int cost = 1;
				if (move == 0 || moveCounts[c] == 1)
					cost = 0;
				Node N = makeMove(c, d, cost, true);
				if (N != null)
					allMoves.add(N);
			}
			if(Param.activeColor)
				break;
		}
		
		return allMoves;
	}
	
	private int h_Manhattan() {
		int h = 0;
		for (int c=0; c<Map.numFlow; c++)
			h += Point.getManDist(new Point(state.cur[c]), Map.end[c]);
		return h;
	}
	
	private int h_Wall() {
		int h = 0;
		for (int c=0; c<Map.numFlow; c++) {
			//if ((finished & (1 << c)) != 0) continue;
			h += new Point(state.cur[c]).getWallMinDist(Map.N);
		}
		return h;
	}
	
	private int realWallDist(int c) {
		Point P = new Point(state.cur[c]);
		//System.out.println(c);
		int minWD = P.getWallMinDist(Map.N);
		for (int d=0; d<4; d++) {
			Point Q = new Point(P, d);
			if (!Q.isValid(Map.N) || state.map[Q.getPos()] != -1)
				return 0;
			int wD = 0;
			while (wD < minWD) {
				wD++;
				Q.move(d);
				if (!Q.isValid(Map.N) || state.map[Q.getPos()] != -1)
					break;
			}
			
			minWD = Integer.min(minWD, wD);
		}
		//System.out.println(c + " " + minWD);
		return minWD;
	}
	
	private int h_RealWall() {
		int h = 0;
		for (int c=0; c<Map.numFlow; c++) {
			//System.out.println(finished);
			if ((state.finished & (1 << c)) != 0) continue;
			h += realWallDist(c);
		}
		//System.out.println(h);
		return h;
	}
	
	private int h_Move() {
		int h = 0;
		for (int c=0; c<Map.numFlow; c++) {
			if (moveCounts[c] == -1) continue;
			h += moveCounts[c];
		}
		return h;
	}
	
	private int h_Completed() {
		//System.out.println("B: " +Integer.bitCount(finished));
		return Map.numFlow - Integer.bitCount(state.finished);
	}
	
	public int h() {
		switch (Param.h) {
		case 0: return state.free;
		case 1: return h_Wall();
		case 2: return h_Manhattan();
		case 3: return state.free + h_Wall();
		case 4: return state.free + 5 * h_Wall() + h_Manhattan();	
		default: return 0;
		}
	}
	
	// Tinh ham f de sap xep thu tu cac Node trong A*
	public Double f() {
		return Param.bias * g + (1 - Param.bias) * h;
	}
	
	// Kiem tra nut hien tai da la dich chua
	public boolean isGoal() {
		return state.isGoal();
	}
}
