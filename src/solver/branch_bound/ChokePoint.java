package solver.branch_bound;

import solver.*;
import solver.structs.*;

public class ChokePoint {
	private State tempS;
	
	public ChokePoint() {
		tempS = new State();
	}
	
	public boolean exist(State S) {
		int lastFlow = S.last;		
		Point lastPoint = new Point(S.cur[lastFlow]);
		
		for (int dir=0; dir<4; dir++) {
			Point filler = new Point(lastPoint, dir);
			if (!filler.isValid(Map.N) || S.map[filler.getPos()] != -1)
				continue;
			
			tempS.copy(S);
			int bottleneckSize = 0;
			while (bottleneckSize < 3) {
				tempS.map[filler.getPos()] = (byte)lastFlow;
				bottleneckSize++;
				filler.move(dir);
				if (!filler.isValid(Map.N) || tempS.map[filler.getPos()] != -1) {
					filler.move((dir + 2) >> 2);
					break;
				}
			}
			
			tempS.cur[lastFlow] = filler.toByte();
			
			if (Map.stranded.check(tempS, lastFlow) > bottleneckSize) {
				//System.out.println("oh no");
				return true;
			}
				
		}
		
		return false;
	}
}
