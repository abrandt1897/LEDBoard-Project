package led;

public class Object {
	private String ID;
	private int color;
	private Types type;
	
	public Object(int color, String ID) {
		this.color = color;
		this.ID = ID;
		this.type = Types.Living;
	}
	
	public void setColor(int color) {
		this.color = color;
	}
	public void setID(String ID) {
		this.ID = ID;
	}
	public void setType(Types type) {
		this.type = type;
	}
	public void nullifyObject() {
		this.color = -1;
		this.ID = "N";
	}
	
	public String getColor() {
		return Integer.toString(color);
	}
	public String getID() {
		return ID;
	}
	
	public boolean isLiving() {
		return type == Types.Living;
	}
}
