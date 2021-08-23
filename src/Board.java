import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeMap;

class boardComparator implements Comparator<Board> {
	public int compare(Board x, Board y) {
		if (x.evalValue == y.evalValue)
			return -1;
		else if (x.evalValue > y.evalValue)
			return 1;
		else
			return -1;
	}
}

public class Board {
	Player white, black;
	HashSet<Coordinates> emptyPosition;
	int hueristicValue;
	double evalValue;
	int type;
	
	public Board() {
		this.white = new Player();
		this.black = new Player();
		this.emptyPosition = new HashSet<Coordinates>();
	}
	
	public Board(Board b) {
		this.white = new Player(b.white);
		this.black = new Player(b.black);
		this.emptyPosition = new HashSet<Coordinates>(b.emptyPosition);
		this.type = b.type;
		this.evalValue = b.evalValue;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((black == null) ? 0 : black.hashCode());
		result = prime * result + ((emptyPosition == null) ? 0 : emptyPosition.hashCode());
		result = prime * result + ((white == null) ? 0 : white.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Board other = (Board) obj;
		if (black == null) {
			if (other.black != null)
				return false;
		} else if (!black.equals(other.black))
			return false;
		if (emptyPosition == null) {
			if (other.emptyPosition != null)
				return false;
		} else if (!emptyPosition.equals(other.emptyPosition))
			return false;
		if (white == null) {
			if (other.white != null)
				return false;
		} else if (!white.equals(other.white))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String[][] board = new String[8][8];
		for (String[] row : board)
			Arrays.fill(row, ".");
		
		for (Coordinates c : white.menPosition)
			board[c.x-1][c.y-1] = "w";
		
		for (Coordinates c : white.kingPosition)
			board[c.x-1][c.y-1] = "W";
		
		for (Coordinates c : black.menPosition)
			board[c.x-1][c.y-1] = "b";
		
		for (Coordinates c : black.kingPosition)
			board[c.x-1][c.y-1] = "B";
		
		String lineSeparator = System.lineSeparator();
		StringBuilder sb = new StringBuilder();

		for (int i=7; i>=0; i--) {
		    sb.append(String.join(" ", board[i]))
		      .append(lineSeparator);
		}

		return sb.toString();
	}
	
	public void getHeuristic(String player)
	{
		this.hueristicValue=0;
		// First row defense
		Player p = player.equals("WHITE")? white:black;
		Player _p = player.equals("WHITE")? black:white;
		int firstRow = player.equals("WHITE")? 1:8;
		if(p.getCount()>4) {			
			for (Coordinates c : p.menPosition) {
				if(c.x == firstRow)
					this.hueristicValue+=5; 
			}
			for (Coordinates c : p.kingPosition) {
				if(c.x == firstRow)
					this.hueristicValue+=5;
			}
		}
		
		// Block Opponent
		for (Coordinates c : _p.menPosition) {
			if(c.x == 1 || c.y==1 || c.x==8 || c.y==8)
				this.hueristicValue+=1; 
		}
		for (Coordinates c : _p.kingPosition) {
			if(c.x == 1 || c.y==1 || c.x==8 || c.y==8)
				this.hueristicValue+=1;
		}		
	}
	
	public void getValue()
	{
		int wAdv=0,wBck=0,bAdv=0,bBck=0;
		this.evalValue=0;
		for (Coordinates coordinates : white.menPosition) {
			if(coordinates.x>4)
				wAdv+= 1;
			else
				wBck+= 1;
		}	
		for (Coordinates coordinates : black.menPosition) {
			if(coordinates.x<4)
				bAdv+= 1;
			else
				bBck+= 1;
		}
		this.evalValue+= ((7*bAdv)+(5*bBck)+(10*black.kingPosition.size()))-((7*wAdv)+(5*wBck)+(10*white.kingPosition.size()));
	}
	
	TreeMap<Board,ArrayList<Move>> nextBoards(String player) {
		TreeMap<Board, ArrayList<Move>> nextJumps = player.equals("WHITE")? new TreeMap<Board, ArrayList<Move>>(new boardComparator()): new TreeMap<Board, ArrayList<Move>>(Collections.reverseOrder(new boardComparator()));
		TreeMap<Board, ArrayList<Move>> nextMoves = player.equals("WHITE")? new TreeMap<Board, ArrayList<Move>>(new boardComparator()): new TreeMap<Board, ArrayList<Move>>(Collections.reverseOrder(new boardComparator()));
		Player p = (player.equals("WHITE")) ? white : black;
		Player _p = (player.equals("WHITE")) ? black : white;
		//Jumps
		for (Coordinates c : p.menPosition) {
			JumpTree j = new JumpTree(c, player, p, _p, this.emptyPosition);
			j.constructJumps();
			nextJumps.putAll(j.getMoves(this));
		}
		for (Coordinates c : p.kingPosition) {
			JumpTree j = new JumpTree(c, player, p, _p, this.emptyPosition);
			j.constructJumps();
			nextJumps.putAll(j.getMoves(this));
		}
		if (nextJumps.size()!=0)
		{
			type = 1;
			return nextJumps;
		}
		//Moves
		type = 0;
		for (Coordinates c : p.menPosition) {
			JumpTree j = new JumpTree(c, player, p, _p, this.emptyPosition);
			j.constructMoves();
			nextMoves.putAll(j.getMoves(this));		}
		for (Coordinates c : p.kingPosition) {
			JumpTree j = new JumpTree(c, player, p, _p, this.emptyPosition);
			j.constructMoves();
			nextMoves.putAll(j.getMoves(this));
		}	
		return nextMoves;
	}
}