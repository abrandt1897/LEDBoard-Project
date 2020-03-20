package games.pacman;

import led.Direction;

public class PacManTagged extends PacManBasic {
	private String ID;
	
	public PacManTagged(Direction direction, int speed, int x, int y, String ID) {
		super(direction, speed, x, y);
		this.ID = ID;
	}
	public String getID() {
		return ID;
	}
}
