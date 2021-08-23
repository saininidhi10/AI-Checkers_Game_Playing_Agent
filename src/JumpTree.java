import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;


class Node{
	Coordinates c;
	ArrayList<Node> children;
	
	public Node(Coordinates c) {
		this.c = c;
		children = new ArrayList<Node>();
	}
	
	@Override
	public String toString() {
		String cString = "";
		for (Node node : children) {
			cString += node.toString();
		}
		return "{" + c + ": ["+ cString +"]}, ";  
	}
}

public class JumpTree {
	Node root;
	String player;
	Player p, _p;
	HashSet<Coordinates> empty;
	String type;
	
	public JumpTree(Coordinates root, String player, Player p, Player _p, HashSet<Coordinates> empty) {
		this.root = new Node(root);
		this.player = player;
		this.p = p;
		this._p = _p;
		this.empty = empty;
	}
	
	void constructJumps() {
		this.type = "J";
		_constructJumps(this.root, new HashSet<Coordinates>());
	}
	
	void constructMoves() {
		this.type = "E";
		_constructMoves(this.root);
	}

	private void _constructMoves(Node node) {
		Coordinates fl = this.player.equals("WHITE")? node.c.getFrontLeft() : node.c.getBackRight();
		Coordinates fr = this.player.equals("WHITE")? node.c.getFrontRight() : node.c.getBackLeft();
		Coordinates bl = this.player.equals("WHITE")? node.c.getBackLeft() : node.c.getFrontRight();
		Coordinates br = this.player.equals("WHITE")? node.c.getBackRight() : node.c.getFrontLeft();
		
		if(empty.contains(fl))
			node.children.add(new Node(fl));
		if(empty.contains(fr))
			node.children.add(new Node(fr));
		if(p.getType(root.c).equals("king")) {
			if(empty.contains(bl))
				node.children.add(new Node(bl));
			if(empty.contains(br))
				node.children.add(new Node(br));
		}
	}

	private void _constructJumps(Node node, HashSet<Coordinates> killed) {
		Coordinates fl = this.player.equals("WHITE")? node.c.getFrontLeft() : node.c.getBackRight();
		Coordinates flfl = this.player.equals("WHITE")? fl.getFrontLeft() : fl.getBackRight();
		Coordinates fr = this.player.equals("WHITE")? node.c.getFrontRight() : node.c.getBackLeft();
		Coordinates frfr = this.player.equals("WHITE")? fr.getFrontRight() : fr.getBackLeft();

		Coordinates bl = this.player.equals("WHITE")? node.c.getBackLeft() : node.c.getFrontRight();
		Coordinates blbl = this.player.equals("WHITE")? bl.getBackLeft() : bl.getFrontRight();
		Coordinates br = this.player.equals("WHITE")? node.c.getBackRight() : node.c.getFrontLeft();
		Coordinates brbr = this.player.equals("WHITE")? br.getBackRight() : br.getFrontLeft();

		if(!killed.contains(fl) && (_p.menPosition.contains(fl) || _p.kingPosition.contains(fl)) && empty.contains(flfl)) {
			killed.add(fl);
			Node t = new Node(flfl);
			node.children.add(t);
			_constructJumps(t, killed);
			killed.remove(fl);
		}
		if(!killed.contains(fr) && (_p.menPosition.contains(fr) || _p.kingPosition.contains(fr)) && empty.contains(frfr)) {
			killed.add(fr);
			Node t = new Node(frfr);
			node.children.add(t);
			_constructJumps(t, killed);
			killed.remove(fr);
		}
		if(p.getType(root.c).equals("king")) {
			if(!killed.contains(bl) && (_p.menPosition.contains(bl) || _p.kingPosition.contains(bl)) && empty.contains(blbl)) {
				killed.add(bl);
				Node t = new Node(blbl);
				node.children.add(t);
				_constructJumps(t, killed);
				killed.remove(bl);
			}
			if(!killed.contains(br) && (_p.menPosition.contains(br) || _p.kingPosition.contains(br)) && empty.contains(brbr)) {
				killed.add(br);
				Node t = new Node(brbr);
				node.children.add(t);
				_constructJumps(t, killed);
				killed.remove(br);
			}
		}	
	}
	
	TreeMap<Board,ArrayList<Move>> getMoves(Board b){
		TreeMap<Board,ArrayList<Move>> moves= new TreeMap<Board,ArrayList<Move>>(new boardComparator());
		if(root.children.size() != 0)
			_getMoves(root, moves, new Board(b), new ArrayList<Move>()); 
		return moves;
	}
	
	private void _getMoves(Node parent, TreeMap<Board, ArrayList<Move>> moves, Board prevBoard, ArrayList<Move> prevMoves) {
		if(parent.children.size() == 0) {
			//System.out.println(prevMoves);
			prevBoard.getValue();
			moves.put(new Board(prevBoard), new ArrayList<Move>(prevMoves));
			return;
		}
		for (Node child : parent.children) {
			Move m = new Move(this.type,parent.c,child.c);
			prevMoves.add(m);
			boolean isKing = _makeMove(prevBoard,m);
			_getMoves(child, moves, prevBoard, prevMoves);
			_undoMove(prevBoard, m, isKing);
			prevMoves.remove(m);
		}
	}

	private boolean _makeMove(Board oldBoard,Move todoMove)
	{
		Player p = player.equals("WHITE")? oldBoard.white : oldBoard.black;
		Player _p = player.equals("WHITE")? oldBoard.black : oldBoard.white;
		if(p.getType(todoMove.src).equals("king") || (player.equals("WHITE") && todoMove.dst.x==8) || (player.equals("BLACK") && todoMove.dst.x==1))
			p.kingPosition.add(todoMove.dst);
		else
			p.menPosition.add(todoMove.dst);
		
		if(p.getType(todoMove.src).equals("king"))
			p.kingPosition.remove(todoMove.src);
		else
			p.menPosition.remove(todoMove.src);
		
		oldBoard.emptyPosition.add(todoMove.src);
		oldBoard.emptyPosition.remove(todoMove.dst);
		
		boolean isKing = false;
		if(todoMove.type.equals("J")) {
			int s = (todoMove.dst.x - todoMove.src.x)/2;
			int t = (todoMove.dst.y - todoMove.src.y)/2;
			Coordinates loc = new Coordinates((todoMove.src.x + s),(todoMove.src.y + t));
			if(_p.kingPosition.contains(loc)) {
				_p.kingPosition.remove(loc);
				isKing = true;
			}
			else
				_p.menPosition.remove(loc);
			oldBoard.emptyPosition.add(loc);
			return _p.kingPosition.contains(loc);
		}
		return isKing;
	}
	
	private void _undoMove(Board oldBoard, Move undoMove, boolean isKing) {
		Player p = player.equals("WHITE")? oldBoard.white : oldBoard.black;
		Player _p = player.equals("WHITE")? oldBoard.black : oldBoard.white;
		
		if(p.getType(undoMove.dst).equals("king"))
			p.kingPosition.add(undoMove.src);
		else
			p.menPosition.add(undoMove.src);
		
		if(p.getType(undoMove.dst).equals("king") || (player.equals("WHITE") && undoMove.dst.x==8) || (player.equals("BLACK") && undoMove.dst.x==1))
			p.kingPosition.remove(undoMove.dst);
		else
			p.menPosition.remove(undoMove.dst);
		
		oldBoard.emptyPosition.remove(undoMove.src);
		oldBoard.emptyPosition.add(undoMove.dst);
		
		if(undoMove.type.equals("J")) {
			int s = (undoMove.dst.x - undoMove.src.x)/2;
			int t = (undoMove.dst.y - undoMove.src.y)/2;
			Coordinates loc = new Coordinates((undoMove.src.x + s),(undoMove.src.y + t));
			if(isKing)
				_p.kingPosition.add(loc);
			else
				_p.menPosition.add(loc);
			oldBoard.emptyPosition.remove(loc);
		}
	}
	
	@Override
	public String toString() {
		return root.toString();
	}
	
}
