// NHAP XUAT DU LIEU VA XU LY A*

package solver;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import solver.structs.*;

import java.io.*;

public class AStar {
	private static class Order {
		public Point P;
		public int dir;
		
		public Order(Point P, int dir) {
			this.P = P;
			this.dir = dir;
		}
	}
	
	
	// Tap "open" trong ly thuyet
	private PriorityQueue<Node> open = new PriorityQueue<>(new Comparator<Node>() {
		public int compare(Node o1, Node o2) {
			return o1.f().compareTo(o2.f());
		};
	});

	// Tap "closed" trong ly thuyet
	private HashSet<State> closed = new HashSet<>(); 
	
	// Nhap du lieu tu file
	public AStar(File test) throws Exception {
		Scanner inp = new Scanner(test);
		
		String one = inp.nextLine();
		int N = one.length();

		String[] puzzle = new String[N];
		puzzle[0] = one;
		for (int i=1; i<N; i++)
			puzzle[i] = inp.nextLine();
		
		Map.initMap(puzzle);
		inp.close();
	}
	
	// Doc chi thi cua nguoi dung va tao nut dau tien
	private Node initNode() throws Exception {
		Node init = new Node(true);
		
		Scanner order = new Scanner(new File("Move.txt"));
		while (order.hasNextInt()) {
			Point P = new Point(order.nextInt(), order.nextInt());
			int flow = init.state.map[P.getPos()];
			int dir = order.nextInt();
			init = init.makeMove(flow, dir, 0, false);
			if (init == null)
				return null;
		}
		init.parent = null;
		order.close();
		
		return init;
	}
	
	// Giai thuat A*
	public String solve(AtomicInteger nodeCnt) throws Exception {
		int nodeCount = 0;
		
		Node init = initNode();
		if (init == null) {
			makeSolution(null, "NoSolution");
			return "NoSolution";
		}
			
		open.add(init);
		
		while (!open.isEmpty()) {
			
			Node P = open.poll();
			closed.add(P.state);
			
			//P.state.printState();
			++nodeCount;
			
			if (nodeCount == Param.maxNode) {
				nodeCnt.set(nodeCount);
				makeSolution(null, "LimitExceed");
				return "LimitExceed";
			}
			
			if (P.isGoal()) {
				//P.state.printState();
				nodeCnt.set(nodeCount);
				makeSolution(P, "Solved");
				return "Solved";
			}
			
			ArrayList<Node> nodeList = P.makeAllMoves();

			for (Node Q: nodeList) {
				if (closed.contains(Q.state) || open.contains(Q))
					continue;
				
				open.add(Q);
			}
		}

		nodeCnt.set(nodeCount);
		makeSolution(null, "NoSolution");
		return "NoSolution";
	}
	
	// Xuat ket qua ra file
	public void makeSolution(Node P, String status) throws Exception {
		FileWriter res = new FileWriter("Solution.txt");
		Stack<Order> list = new Stack<>();
		
		res.write(status);
		
		if (P != null) {
			res.write(System.lineSeparator());
			while (P.parent != null) {
				Node parent = P.parent;
				int last = P.state.last;
				
				Point cur = new Point(P.state.cur[last]);
				Point past = new Point(parent.state.cur[last]);
				
				//res.write(new String(cur.x + " " + cur.y + " " + cur.howToMove(past) + System.lineSeparator()));
				list.push(new Order(past, past.howToMove(cur)));
				
				P = parent;
			}
		}
		
		while (!list.isEmpty()) {
			Order o = list.pop();
			res.write(o.P.x + " " + o.P.y + " " + o.dir + System.lineSeparator());
		}
		
		res.close();
	}
}
