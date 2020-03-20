package games.avoidable;

import java.io.FileNotFoundException;

import led.Game;
import led.KeyState;

public class Avoidable extends Game {
	private static final long serialVersionUID = 1L;
	private int[] plr;
	private int[] ai;
	private int aiSpeed;
	
	public Avoidable(boolean mode) {
		super(10, mode);
		
		mapC.put("W", -2);
		mapC.put("R", 0);
		mapC.put("G", 96);
		
		mapC.put("0", -1);
		mapC.put("1", 96);
		mapC.put("2", 0);
		mapC.put("3", 96);
		mapC.put("4", 96);
		mapC.put("5", 0);
		mapC.put("6", 96);
		mapC.put("7", 0);
		mapC.put("8", 96);
		mapC.put("9", -2);
		
		aiSpeed = 1000;
		score = 0;
		
		loadBoard();
		
		plr = new int[]{ 0, 0 };
		ai = new int[] { 1, 1 };
		
		(new Thread(new moveAI())).start();
	}
	
	private class moveAI implements Runnable {
		@Override
		public void run() {
			while (!gameover) {
				try { Thread.sleep(aiSpeed); } catch (InterruptedException e) {}
				if (rand.nextInt(2) == 0) {
					ai[0] = (ai[0] + 1) % 2;
				} else {
					ai[1] = (ai[1] + 1) % 2;
				}
				score++;
			}
		}
	}
	
	@Override
	public void tick() throws FileNotFoundException {
		if (!trytick() || gameover) return;
		clearBoard();
		
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				if (x == 0 || y == 0 || x == SIZE - 1 || y == SIZE - 1) {
					placeObject(x, y, "W");
				}
			}
		}
		
		int size = ((SIZE - 2) / 2);
		
		for (int y = plr[1] * size + 1; y < (plr[1]+1) * size + 1; y++) {
			for (int x = plr[0] * size + 1; x < (plr[0]+1) * size + 1; x++) {
				placeObject(x, y, "G");
			}
		}
		for (int y = ai[1] * size + 1; y < (ai[1]+1) * size + 1; y++) {
			for (int x = ai[0] * size + 1; x < (ai[0]+1) * size + 1; x++) {
				placeObject(x, y, "R");
			}
		}
		
		if (plr[0] == ai[0] && plr[1] == ai[1]) gameover = true;
		
		if (gameover) {
			gameoverAnimation();
		}
	}
	@Override
	public void input() {
		if (getKeyState("Right") == KeyState.Pressed) {
			plr[0] = 1;
		} else if (getKeyState("Left") == KeyState.Pressed) {
			plr[0] = 0;
		} else if (getKeyState("Up") == KeyState.Pressed) {
			plr[1] = 0;
		} else if (getKeyState("Down") == KeyState.Pressed) {
			plr[1] = 1;
		}
	}
	
}