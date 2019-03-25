package solver;

import java.util.*;

import solver.branch_bound.*;
import solver.structs.Point;

public class Map {	
	public static byte[] map;	
	public static int N, numFlow;
	public static Point begin[], end[];
	
	public static HashMap<Character, Byte> flowId = new HashMap<>();
	public static ArrayList<Character> flowColor = new ArrayList<>();
	
	public static ForcedMove forcedMove;
	public static DeadEnd deadEnd;
	public static Stranded stranded;
	public static ChokePoint chokePoint;
	
	public static void initMap(String[] puzzle) {
		flowId.clear();
		flowColor.clear();

		numFlow = 0;
		N = puzzle[0].length();
		map = new byte[N * N];
		
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++) {
				char c = puzzle[i].charAt(j);
				if (c == '.')
					map[i * N + j] = -1;
				else {
					char C = Character.toUpperCase(c);
					byte id;
					if (flowId.containsKey(C))
						id = flowId.get(C);
					else {
						id = (byte)numFlow;
						flowId.put(C, id);
						flowColor.add(C);
						numFlow++;
					}
					map[i * N + j] = id;
				}
			}
		
		begin = new Point[numFlow];
		end = new Point[numFlow];
		
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++) {
				char c = puzzle[i].charAt(j);
				if (c == '.')
					continue;
				byte id = map[i * N + j];
				if (c >= 'A' && c <= 'Z')
					begin[id] = new Point(i, j);
				else {
					end[id] = new Point(i, j);
					map[i * N + j] = -2;
				}
					
			}
		
		forcedMove = new ForcedMove();
		deadEnd = new DeadEnd();
		stranded = new Stranded();
		chokePoint = new ChokePoint();
	}

}
