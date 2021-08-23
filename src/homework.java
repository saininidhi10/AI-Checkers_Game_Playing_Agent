import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class homework {
	Board init;
	String inputFile;
	String mode;
	String currentPlayer;
	double time;
	static int depthLimit = 8;
	
	public homework(String inputFile) {
		this.inputFile = inputFile;
	}
	
	BufferedReader getFileReader() {
		try {
			return new BufferedReader(new FileReader(inputFile));
		} catch (FileNotFoundException e) {
			System.out.println("File "+inputFile+" does not exist.");
			e.printStackTrace();	
		}
		return null;
	}
	
	void processInputFile(BufferedReader br) throws IOException {
		this.mode = br.readLine().trim();
		this.currentPlayer = br.readLine().trim();
		this.time = Double.parseDouble(br.readLine().trim());
		init = new Board();
		for(int i=8; i>0; i--) {
			String temp = br.readLine().trim();
			for(int j=0; j<8; j++) {
				switch (temp.charAt(j)) {
				case 'b':
					init.black.menPosition.add(new Coordinates(i, j+1));
					break;
				
				case 'w':
					init.white.menPosition.add(new Coordinates(i, j+1));
					break;
				
				case 'B':
					init.black.kingPosition.add(new Coordinates(i, j+1));
					break;
				
				case 'W':
					init.white.kingPosition.add(new Coordinates(i, j+1));
					break;

				default:
					init.emptyPosition.add(new Coordinates(i, j+1));
					break;
				}
			}
		}
	}
	
	double minimax(Board board,int depth,String player,double alpha,double beta, boolean flag)
	{
		if(depth == depthLimit) {
//			board.getValue();
			if(!flag) board.getHeuristic(player);
			return board.evalValue + (flag? 0 : (player.equals("BLACK")? board.hueristicValue: -board.hueristicValue));
		}
		if (player.equals("BLACK")){
			double best = Integer.MIN_VALUE;
			for (Map.Entry<Board, ArrayList<Move>> child: board.nextBoards(player).entrySet()) {
				double val = minimax(child.getKey(), depth+1, "WHITE", alpha, beta, flag);
				best = Math.max(val, best);
				alpha = Math.max(alpha, best);
				if (alpha >= beta)
					break;
			}
			return best;
		}
		else {
			double best = Integer.MAX_VALUE;
			for (Map.Entry<Board, ArrayList<Move>> child: board.nextBoards(player).entrySet()) {
				double val = minimax(child.getKey(), depth+1, "BLACK", alpha, beta, flag);
				best = Math.min(val, best);
				beta = Math.min(beta, best);
				if (alpha >= beta)
					break;
			}
			return best;
		}
	}
	
	ArrayList<Move> minimaxDriver()
	{
		ArrayList<Move> bestMove = null;
		Board bestBoard = null;
		double best = 0;
		double alpha = Integer.MIN_VALUE;
		double beta = Integer.MAX_VALUE;
		if (currentPlayer.equals("BLACK")){			
			best = Integer.MIN_VALUE;
			TreeMap<Board, ArrayList<Move>> nxtMoves = init.nextBoards("BLACK");
			bestMove = nxtMoves.firstEntry().getValue();
			bestBoard = nxtMoves.firstEntry().getKey();
			for (Map.Entry<Board, ArrayList<Move>> child: nxtMoves.entrySet()) {
//				System.out.println("-------------------"+child.getValue()+"--------------------");
				double val = Integer.MIN_VALUE;
				if(nxtMoves.size()>1) 
					val = minimax(child.getKey(), 0, "WHITE", alpha, beta, this.init.emptyPosition.size()>=54? true:false );
//				System.out.println(val);
//				System.out.println(child.getKey());
//				System.out.println("Black:\n"+child.getKey().black);
//				System.out.println("White:\n"+child.getKey().white);
//				System.out.println("Empty:\n"+child.getKey().emptyPosition);
//				System.out.println("-----------------------------------------------------------");
				if(best < val) {
					best = val;
					bestMove = child.getValue();
					bestBoard = child.getKey();
				}
				alpha = Math.max(alpha, best);
				if (alpha >= beta)
					break;
			}
		}
		else {
			best = Integer.MAX_VALUE;
			TreeMap<Board, ArrayList<Move>> nxtMoves = init.nextBoards("WHITE");
			bestMove = nxtMoves.firstEntry().getValue();
			bestBoard = nxtMoves.firstEntry().getKey();
			for (Map.Entry<Board, ArrayList<Move>> child: nxtMoves.entrySet()) {
				double val = Integer.MAX_VALUE;
				if(nxtMoves.size() > 1) 
					val = minimax(child.getKey(), 0, "BLACK", alpha, beta, this.init.emptyPosition.size()>=54? true:false);
//				System.out.println("-------------------"+child.getValue()+"--------------------");
//				System.out.println(val);
//				System.out.println(child.getKey());
//				System.out.println("-----------------------------------------------------------");
				if(best > val) {
					best = val;
					bestMove = child.getValue();
					bestBoard = child.getKey();
				}
				beta = Math.min(beta, best);
				if (alpha >= beta)
					break;
			}
		}
		return bestMove;
//		System.out.println(currentPlayer);
//		System.out.println("Best value is:"+best);
//		System.out.println(bestMove);
//		System.out.println(bestBoard);
//		return bestBoard;
	}
	

	private void gameMode() throws NumberFormatException, FileNotFoundException, IOException {
		if(time<0.5)
			depthLimit = 1;
		else if(time<2)
			depthLimit = 5;
		else
			depthLimit = 8;
//		long start = System.currentTimeMillis();
		System.out.println("Calling minimax using depth "+depthLimit);
		writeToFile(minimaxDriver());
//		long end = System.currentTimeMillis();
//		System.out.println("Function took " + (end - start) + "ms");
	}

	private void singleMode() {
		writeToFile(this.init.nextBoards(this.currentPlayer).firstEntry().getValue());
	}
	
	private void writeToFile(ArrayList<Move> value) {
		try {
			FileWriter myWriter = new FileWriter("output.txt");
			for (int j = 0; j <=value.size()-1; j++) {
				if (j==value.size()-1)
					myWriter.write(value.get(j).toString());
				else
					myWriter.write(value.get(j).toString()+"\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		homework hw2 = new homework("Test cases/input.txt");
		hw2.processInputFile(hw2.getFileReader());
		if(hw2.mode.equals("SINGLE"))
			hw2.singleMode();
		else
			hw2.gameMode();
		
		// TEST
//		hw2.processInputFile(hw2.getFileReader());
//		System.out.println(hw2.mode);
//		System.out.println(hw2.currentPlayer);
//		System.out.println(hw2.time);
//		System.out.println(hw2.init);
//		System.out.println("Black:\n"+hw2.init.black);
//		System.out.println("White:\n"+hw2.init.white);
//		System.out.println("Empty Position:"+hw2.init.emptyPosition);
//		System.out.println("-------------------------------------");
//		for (Map.Entry<Board, ArrayList<Move>> entry: hw2.init.nextBoards("WHITE").entrySet()) {
//			Board b = entry.getKey();
//			System.out.println(b);
//			System.out.println("Black:\n"+b.black);
//			System.out.println("White:\n"+b.white);
//			System.out.println("Empty Position:"+b.emptyPosition);
//			System.out.println("-------------------------------------");
//		}
		//1st player mode
//		Scanner s = new Scanner(System.in);
//		int tot = 0;
//		for(int i=0;i<200;i++) {
//			long start = System.currentTimeMillis();
//			hw2.init = hw2.minimaxDriver();
//			long end = System.currentTimeMillis();
//			tot += (end-start);
//	        System.out.println("Function took " + (end - start) + "ms");
//	        System.out.println("Total time until now: "+tot+"ms");
//	        System.out.println("-----------------"+i+"--------------------");
//			System.out.println(hw2.init);
//			System.out.println("Black:\n"+hw2.init.black);
//			System.out.println("White:\n"+hw2.init.white);
//			System.out.println("Empty Position: size:"+hw2.init.emptyPosition.size()+" "+hw2.init.emptyPosition);
//			boolean flag = false;
//			do {
//				System.out.println("Which black?");
//				int j = 0;
//				ArrayList<Board> brds = new ArrayList<Board>();
//				for (Map.Entry<Board, ArrayList<Move>> entry : hw2.init.nextBoards("BLACK").entrySet()) {
//					System.out.println(j + ":"+entry.getValue());
//					brds.add(entry.getKey());
//					j+=1;
//				}
//				System.out.println("Enter choice:");
//				int ch = s.nextInt();
//				if(ch<brds.size()) {
//					hw2.init = brds.get(ch);
//					flag = true;
//				}
//			}while(!flag);
//		}
//		s.close();
		
		//2nd player mode
//		Scanner s = new Scanner(System.in);
//		int tot = 0;
//		for(int i=0;i<200;i++) {
//			boolean flag = false;
//			do {
//				System.out.println("Which black?");
//				int j = 0;
//				ArrayList<Board> brds = new ArrayList<Board>();
//				for (Map.Entry<Board, ArrayList<Move>> entry : hw2.init.nextBoards("BLACK").entrySet()) {
//					System.out.println(j + ":"+entry.getValue());
//					brds.add(entry.getKey());
//					j+=1;
//				}
//				System.out.println("Enter choice:");
//				int ch = s.nextInt();
//				if(ch<brds.size()) {
//					hw2.init = brds.get(ch);
//					flag = true;
//				}
//			}while(!flag);
//			long start = System.currentTimeMillis();
//			hw2.init = hw2.minimaxDriver();
//			long end = System.currentTimeMillis();
//			tot += (end-start);
//	        System.out.println("Function took " + (end - start) + "ms");
//	        System.out.println("Total time until now: "+tot+"ms");
//	        System.out.println("-----------------"+i+"--------------------");
//			System.out.println(hw2.init);
//			System.out.println("Black:\n"+hw2.init.black);
//			System.out.println("White:\n"+hw2.init.white);
//			System.out.println("Empty Position: size:"+hw2.init.emptyPosition.size()+" "+hw2.init.emptyPosition);
//		}
//		s.close();
	}
}
