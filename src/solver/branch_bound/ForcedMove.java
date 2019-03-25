// XU LY TIM FORCED MOVE (NUOC DI BAT BUOC)
package solver.branch_bound;

import solver.*;
import solver.structs.*;

public class ForcedMove {
	
	// Kiem tra tai trang thai S, huong "direct" cua flow nay co phai nuoc di bat buoc?
	private boolean isForced(State S, int flow, int direct) {
		Point P = new Point(S.cur[flow], direct);
		if (!Param.selfTouchable && P.equals(Map.end[flow]))
			return true;
		
		int countFree = 0;
		int countOtherEnds = 0;
		
		for (int dir=0; dir<4; dir++) {
			Point Q = new Point(P, dir);
			
			if (!Q.isValid(Map.N) || Q.toByte() == S.cur[flow])
				continue;
			if (!Param.selfTouchable && Q.equals(Map.end[flow]))
				continue;
			int pos = Q.getPos();
			if (S.map[pos] == -1)
				countFree++;
			else if (S.map[pos] == -2)
				countOtherEnds++;
			else if (((S.finished & (1 << S.map[pos])) == 0) && Q.toByte() == S.cur[S.map[pos]])
				countOtherEnds++;
		}
		
		return (countFree == 1) && (countOtherEnds == 0);
	}
	
	// Tim huong di bat buoc cua flow tai trang thai S
	public int findForcedDir(State S, int flow) {
		int moveCount = 0, oneDir = -1;;
		
		for (int dir=0; dir<4; dir++)
			if (S.tryMove(flow, dir) != -1) {
				moveCount++;
				oneDir = dir;
				if (isForced(S, flow, dir)) {
					//System.out.println("Forced: " + Map.flowColor.get(flow) + " " + dir);
					//S.printState();
					return dir;
				}
			}

		if (moveCount == 1)
			return oneDir;
		return -1;
	}
	
	// Tim xem trang thai S co nuoc di bat buoc nao ko?
	// - Neu ko, tra ve -1
	// - Neu co, tra ve 2 bit cuoi la huong di, con lai la id cua flow
	public int findForcedMove(State S) {
		for (int c=0; c<Map.numFlow; c++) {
			if ((S.finished & (1 << c)) != 0)
				continue;

			int dir = findForcedDir(S, c);
			if (dir != -1)
				return (c << 2) | dir;
		}
		
		return -1;
	}
	
}
