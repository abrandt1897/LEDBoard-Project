package games.pacman;

import led.Direction;

public class PacManGhost extends PacManBasic {
	private String ID;
	private boolean canMove;
	
	public boolean teleport, teleported;
	public PacManTagged teleportTo;
	
	public PacManGhost(Direction direction, int speed, int x, int y, String ID) {
		super(direction, speed, x, y);
		this.ID = ID;
		canMove = false;
	}
	public String getID() {
		return ID;
	}
	public boolean canMove() {
		return canMove;
	}
	public void unFreeze() {
		canMove = true;
	}
}
