package games.breakout;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import led.Game;
import led.KeyState;
import led.Types;

public class Breakout extends Game {
	private static final long serialVersionUID = 1L;
	private ArrayList<block> blocks = new ArrayList<block>();
	
	int lives = 1;
	
	int boardX = 8;
	int boardLength = 4;
	
	int ball[], ballVel[];
	
	public Breakout(boolean mode) throws FileNotFoundException {
		super(5, mode);
		
		mapC.put("W", 180);
		mapC.put("N", -1);
		
		mapC.put("R", 0);
		mapC.put("O", 32);
		mapC.put("B", 128);
		mapC.put("G", 76);
		
		mapC.put("I", -2);
		mapC.put("L", 110);
		
		mapC.put("0", -1);
		mapC.put("1", 0);
		mapC.put("2", 32);
		mapC.put("3", 64);
		mapC.put("4", 96);
		mapC.put("5", 128);
		mapC.put("6", 160);
		mapC.put("7", 192);
		mapC.put("8", 224);
		mapC.put("9", -2);
		
		loadBoard("breakout1.txt");
		
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				if (board[y][x].getID().equals("W")) {
					board[y][x].setType(Types.NonLiving);
				} else {
					board[y][x].setType(Types.Living);
				}
				
				if (board[y][x].getID().equals("R") || board[y][x].getID().equals("O") || board[y][x].getID().equals("B") || board[y][x].getID().equals("G")) {
					blocks.add(new block(x, y, board[y][x].getID()));
				}
			}
		}
		
		
		resetBall();
	}
	
	public void resetBall() {
		ball = new int[]{ 10, 17 };
		ballVel = new int[]{ -1, -1 };
	}
	
	@Override
	public void tick() throws FileNotFoundException {
		if (!trytick() || gameover || won) return;
		clearBoard();
		
		ball[0] += ballVel[0];
		ball[1] += ballVel[1];
		
		if (ball[0] < 1) {
			ball[0] = 1;
			ballVel[0] = 1;
		} else if (ball[0] > SIZE - 2) {
			ball[0] = SIZE - 2;
			ballVel[0] = -1;
		}
		
		if (ball[1] < 3) {
			ball[1] = 3;
			ballVel[1] = 1;
		} else if (ball[1] > SIZE - 1) {
			lives--;
			resetBall();
		}
		
		if (ball[1] == SIZE - 2) {
			ballVel[0] = 0;
		}
		
		for (int i = 0; i < blocks.size(); i++) {
			if (Arrays.equals(ball, new int[] { blocks.get(i).x, blocks.get(i).y })) {
				blocks.remove(i);
				score++;
				ballVel[1] = -ballVel[1];
			}
		}
		for (int x = boardX; x < boardX + boardLength; x++) {
			if (Arrays.equals(ball, new int[] { x, SIZE - 2 }) && ballVel[1] > 0) {
				ballVel[1] = -ballVel[1];
				if (x == boardX) {
					ballVel[0] = -2;
				} else if (x == boardX + boardLength - 1) {
					ballVel[0] = 2;
				} else if (x < boardX + (boardLength/2)) {
					ballVel[0] = -1;
				} else {
					ballVel[0] = 1;
				}
			}
		}
		
		// Draw Lives
		for (int i = 0, o = 18; i < lives; i++) placeObject(o--, 1, "L");
		// Draw Blocks
		for (block b : blocks) placeObject(b.x, b.y, b.ID);
		// Draw Player
		for (int x = boardX; x < boardX + boardLength; x++) placeObject(x, SIZE-1, "I");
		// Draw Ball
		placeObject(ball[0], ball[1], "I");
		
		if (lives < 1) {
			gameover = true;	
		} else if (score == blocks.size()) {
			won = true;
		}
		
		
		if (gameover) {
			gameoverAnimation();
		}
	}
	
	@Override
	public void input() {
		if (!ticked) return;
		
		if (getKeyState("Right") == KeyState.Pressed && boardX + boardLength < SIZE - 1) {
			boardX++;
		} else if (getKeyState("Left") == KeyState.Pressed && boardX > 1) {
			boardX--;
		}
	}
	
	class block {
		int x, y;
		String ID;
		public block(int x, int y, String ID) {
			this.x = x;
			this.y = y;
			this.ID = ID;
		}
	}
}
