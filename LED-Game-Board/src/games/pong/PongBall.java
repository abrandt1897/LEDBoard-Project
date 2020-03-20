package games.pong;

import java.util.Random;

public class PongBall {
	private int direction = 0;
	private int speed = 0;
	private boolean paused = true;
	
	public PongBall() {
		
	}
	private void startBall() {
		Random rand = new Random();
		int randomDir = rand.nextInt()%2;
		if(randomDir == 0)
			direction = -1;
		else
			direction = 1;
	}
	public void setSpeed() {
		speed = 2;
	}
	public void toggleState() {
		if(paused) {
			paused = false;
			startBall();
		}
		else {
			paused = true;
			
		}
			
	}
	public boolean isPaused() {
		return paused;
	}
	public int getDirection() {
		return direction;
	}
	public void toggleDirection() {
		if(direction == 1)
			direction = -1;
		else
			direction = 1;
	}
	
}

