package games.pacman;

import led.Direction;

public class PacManBasic {
	private Direction direction;
	//private int speed;
	private int position[];
	
	
	public PacManBasic(Direction direction, int speed, int x, int y) {
		//this.speed = speed;
		this.direction = direction;
		this.position = new int[2]; position[0] = x; position[1] = y;
		
	}
	
	public int[] getPosition() {
		return new int[] { position[0], position[1] };
	}
	public void setPosition(int position[]) {
		this.position[0] = position[0];
		this.position[1] = position[1];
	}
	public void changeDirection(Direction direction) {
		this.direction = direction;
	}
	
	public void move() {
		switch (direction) {
			case Up: position[1]--; break;
			case Down: position[1]++; break;
			case Left: position[0]--; break;
			case Right: position[0]++; break;
			case None: default: break;
		}
	}
}
