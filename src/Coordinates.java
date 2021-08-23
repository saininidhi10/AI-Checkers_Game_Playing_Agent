
class Coordinates {
	int x,y;
	static final String[] mapping = {"", "a", "b", "c", "d", "e", "f", "g", "h"};
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Coordinates other = (Coordinates) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Coordinates(Coordinates c) {
		this.x = c.x;
		this.y = c.y;
	}
	
	Coordinates getFrontRight() {
		return new Coordinates(this.x+1, this.y+1);
	}
	
	Coordinates getFrontLeft() {
		return new Coordinates(this.x+1, this.y-1);
	}
	
	Coordinates getBackLeft() {
		return new Coordinates(this.x-1, this.y-1);
	}
	
	Coordinates getBackRight() {
		return new Coordinates(this.x-1, this.y+1);
	}
	
	int getDist(Coordinates c2) {
		return Math.abs(c2.x - this.x) + Math.abs(c2.y - this.y);
	}
	
	@Override
	public String toString() {
		return (mapping[y]+Integer.toString(x));
	}
	
}
