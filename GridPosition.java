
public class GridPosition {
	public double x,y;
	public GridPosition(){
		
	}
	public GridPosition(double x,double y){
		this.x = x;
		this.y = y;
	}
	public GridPosition(int x,int y){
		this.x = x;
		this.y = y;
	}
	public boolean isEqual(GridPosition destination) {
		if(destination.x == this.x && destination.y == this.y)
		return true;
		else
			return false;
	}
	public String toString(){
		return "["+this.x+","+this.y+"]";
	}
}
