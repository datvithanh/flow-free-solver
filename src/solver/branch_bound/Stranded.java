package solver.branch_bound;

import java.util.HashMap;
import solver.*;
import solver.structs.*;

public class Stranded {
	public int[] par = null;
	public int[][] zone = null;
	private HashMap<Integer, Integer> zoneId = new HashMap<>(); 
	private int zoneNum, unstrandedFlows;
	private int[] adjCur, adjEnd;
		
	public Stranded() {
		par = new int[Map.N * Map.N];
		zone = new int[Map.N][Map.N];
		
		adjCur = new int[Map.N * Map.N];
		adjEnd = new int[Map.N * Map.N];
	}
	
	private int anc(int pos) {
		if (pos == par[pos])
			return pos;
		return anc(par[pos]);
	}
	
	// Phan vung o trong bang Disjoint-set
	private void buildZones(State S) {	
		
		// Thuc hien Disjoint-set tren vung cac o trong
		for (int i=0; i<Map.N; i++)
			for (int j=0; j<Map.N; j++) {
				int pos = Point.getPos(i, j);
				par[pos] = pos;
			}
		
		for (int i=0; i<Map.N; i++)
			for (int j=0; j<Map.N; j++)
				if (S.map[i * Map.N + j] == -1) {
					Point P = new Point(i, j);
					for (int dir=1; dir<=2; dir++) {
						Point Q = new Point(P, dir);
						if (!Q.isValid(Map.N) || S.map[Q.getPos()] != -1)
							continue;
						int ancP = anc(P.getPos()), ancQ = anc(Q.getPos());
						if (ancP != ancQ)
							par[ancQ] = ancP;
					}
				}
		
		// Danh dau o nao thuoc vung nao theo Id
		zoneId.clear();
		zoneNum = 0;
		
		for (int i=0; i<Map.N; i++)
			for (int j=0; j<Map.N; j++) {
				int pos = Point.getPos(i, j);
				if (S.map[pos] != -1) {
					zone[i][j] = -1;
					continue;
				}
				int anc = anc(pos);
				if (zoneId.containsKey(anc))
					zone[i][j] = zoneId.get(anc);
				else {
					zoneId.put(anc, zoneNum);
					zone[i][j] = zoneNum;
					zoneNum++;
				}
			}
		
		/*for (int i=0; i<Map.N; i++) {
			for (int j=0; j<Map.N; j++)
				System.out.print(zone[i][j] + " ");
			System.out.println();
		}*/
	}
	
	// Danh dau vung nao ke voi vi tri hien tai va cuoi cua flow bang bitmask
	private void markNeighborZones(State S) {
		unstrandedFlows = S.finished;
		
		for (int z=0; z<zoneNum; z++)
			adjCur[z] = adjEnd[z] = 0;
		
		for (int c=0; c<Map.numFlow; c++) {
			if ((S.finished & (1 << c)) != 0)
				continue;
			
			for (int dir=0; dir<4; dir++) {
				
				// Vung nao ke voi vi tri hien tai cua flow, them flow vao vung do
				Point P = new Point(S.cur[c], dir);
				if (P.isValid(Map.N)) {
					if (Param.selfTouchable && P.equals(Map.end[c]))
						unstrandedFlows |= 1 << c;
					if (zone[P.x][P.y] != -1)			
						adjCur[zone[P.x][P.y]] |= 1 << c;
				}
				
				// Vung nao ke voi vi tri cuoi cua flow, them flow vao vung do
				P = new Point(Map.end[c], dir);
				if (P.isValid(Map.N) && zone[P.x][P.y] != -1)		
					adjEnd[zone[P.x][P.y]] |= 1 << c;
			}
		}
		
		//for (int z=0; z<zoneNum; z++)
			//System.out.println("Zone " + z + ": " + adjCur[z] + " " + adjEnd[z]);

	}
	
	/* Kiem tra vung bi chan va flow bi chan
		- Neu co flow bi chan: Tra ve so flow bi chan
		- Neu khong co flow bi chan:
			+ Tra ve 0 neu khong co vung bi chan
			+ Tra ve -1 neu co vung bi chan
	*/
	public int check(State S, int chokeFlow) {
		buildZones(S);
		markNeighborZones(S);
		
		if (chokeFlow != -1)
			unstrandedFlows |= 1 << chokeFlow;
		boolean haveStrandedZone = false;
		
		for (int z=0; z<zoneNum; z++) {
			int flowsThroughZone = adjCur[z] & adjEnd[z];
			//System.out.println("Zone " + z + ": " + flowsThroughZone);
			if (flowsThroughZone == 0)
				haveStrandedZone = true;
			else
				unstrandedFlows |= flowsThroughZone;
		}
		
		if (unstrandedFlows != (1 << Map.numFlow) - 1)
			return Map.numFlow - Integer.bitCount(unstrandedFlows);
		
		if (chokeFlow == -1 && haveStrandedZone)
			return -1;
		return 0;
	}
}
