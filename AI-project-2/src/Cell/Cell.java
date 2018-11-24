package Cell;

/*Class Cell describes the cell object which has a type and a number of dragon glass that 
 *can provide to Jon Snow. 
 */

public class Cell {
	private String type;
	private int dragonGlass = 0;
	
	public Cell(String type, int dragonGlass) {
		super();
		this.type = type;
		this.dragonGlass = dragonGlass;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getDragonGlass() {
		return dragonGlass;
	}
	public void setDragonGlass(int dragonGlass) {
		this.dragonGlass = dragonGlass;
	}
	
}
