import java.util.HashSet;

public class Player {
	HashSet<Coordinates> menPosition;
	HashSet<Coordinates> kingPosition;
	
	public Player() {
		menPosition = new HashSet<Coordinates>();
		kingPosition = new HashSet<Coordinates>();
	}
	
	public Player(Player p) {
		menPosition = new HashSet<Coordinates>(p.menPosition);
		kingPosition = new HashSet<Coordinates>(p.kingPosition);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kingPosition == null) ? 0 : kingPosition.hashCode());
		result = prime * result + ((menPosition == null) ? 0 : menPosition.hashCode());
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
		Player other = (Player) obj;
		if (kingPosition == null) {
			if (other.kingPosition != null)
				return false;
		} else if (!kingPosition.equals(other.kingPosition))
			return false;
		if (menPosition == null) {
			if (other.menPosition != null)
				return false;
		} else if (!menPosition.equals(other.menPosition))
			return false;
		return true;
	}

	String getType(Coordinates c) {
		if(menPosition.contains(c))
			return "men";
		if(kingPosition.contains(c))
			return "king";
		else
			return "Cannot Determine";
	}
	
	HashSet<Coordinates> getAllCoordinates(){
		HashSet<Coordinates> h = new HashSet<Coordinates>(this.menPosition);
		h.addAll(this.kingPosition);
		return h;
	}
	
	int getCount() {
		return this.kingPosition.size() + this.menPosition.size();
	}
	
	@Override
	public String toString() {
		return "Men Position:"+ menPosition + "\nKing Position:" + kingPosition;
	}
}
