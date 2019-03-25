package solver.branch_bound;

import solver.*;
import solver.structs.*;

public class DeadEnd {
	// Kiem tra trang thai S co bi dead end tai diem P
	public boolean isDeadEnd(State S, Point P) {
		int countFreeBorders = 0;
		for (int dir=0; dir<4; dir++) {
			Point neighbor = new Point(P, dir);
			if (!neighbor.isValid(Map.N))
				continue;
			int nbFlow = S.map[neighbor.getPos()];
			
			if (nbFlow <= -1 || ((S.finished & (1 << nbFlow)) == 0) && (neighbor.toByte() == S.cur[nbFlow]))
				countFreeBorders++;
		}
		
		return countFreeBorders <= 1;
	}
		
	// Kiem tra trang thai S co ton tai dead end
	public boolean exist(State S) {
		Point P = new Point(S.cur[S.last]);
		
		for (int dir=0; dir<4; dir++) {
			Point Q = new Point(P, dir);
			if (Q.isValid(Map.N) && S.map[Q.getPos()] == -1 && isDeadEnd(S, Q))
				return true;
		}
		return false;
	}
}
