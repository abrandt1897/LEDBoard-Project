package games.pong;

import java.io.FileNotFoundException;
import led.Direction;
import led.Game;
import led.KeyState;


public class Pong extends Game {
	private static final long serialVersionUID = 1L;
	public boolean hasWon = false;
	Direction dir = Direction.None; 
	Direction dir2 = Direction.None;
	position player = new position(0, 8);
	position player2 = new position(19, 8);
	position ball = new position(10, 10);
	PongBall p = new PongBall();
	private int velY = 0;
	public Pong(boolean mode) {
		super(5, mode);
		mapC.put("P", -2); 
		mapC.put("B", -2);
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
		
		loadBoard();
		for(int i = 0; i < 4; i++) {
			placeObject(player.x, player.y+i, "P");
			placeObject(player2.x, player2.y+i, "P");
		}
		placeObject(ball.x, ball.y, "B");	
	}
	public boolean hasWon() {
		return hasWon == true ? true : false;
	}
	@Override 
	public void tick() throws FileNotFoundException {
		if (!trytick() || gameover) return;
		clearBoard();
		placeObject(ball.x, ball.y, "B");
		ball.y += velY;
		//System.out.println("Ball X: "+ball.x+" Ball Y: "+ ball.y);
		for(int i = 0; i < 4; i++) {
			if(player.y+i >= 0 && player.y+i < SIZE)
				placeObject(player.x, player.y+i, "P");
			if(player2.y+i >= 0 && player2.y+i < SIZE)
				placeObject(player2.x, player2.y+i, "P");
		}
		if(dir == Direction.Up) {
			if(p.isPaused()) {
				p.toggleState();
			}
			if(player.y > 0) {
				player.y--;
			}
		}
		else if(dir == Direction.Down) {
			if(p.isPaused()) {
				p.toggleState();
			}
			if(SIZE > player.y+4) {
				player.y++;
			}
		}
		if(dir2 == Direction.Up) {
			if(p.isPaused()) {
				p.toggleState();
			}
			if(player2.y > 0) {
				player2.y--;
			}
		}
		else if(dir2 == Direction.Down) {
			if(p.isPaused()) {
				p.toggleState();
			}
			if(SIZE > player2.y+4) {
				player2.y++;
			}
		}
		if(ball.y + 2 >= SIZE || ball.y <= 0) {
			velY = -velY;
		}
		
			
		if(ball.x-1 <= player.x && !p.isPaused()) {
			for(int i = 0; i < 4; i ++) {
				if(ball.y == player.y+i) {
						p.toggleDirection();
						switch (i) {
						case 1:
							velY = -1;
							break;
						case 2:
							velY = -1;
							break;
						case 3:
							velY = 1;
							break;
						case 4:
							velY = 1;
							break;
						}
				}
			}
		}
		if(ball.x+1 >= player2.x && !p.isPaused()) {
			for(int i = 0; i < 4; i ++) {
				if(ball.y == player2.y+i) {
					p.toggleDirection();
					switch (i) {
					case 1:
						velY = 1;
						break;
					case 2:
						velY = 1;
						break;
					case 3:
						velY = -1;
						break;
					case 4:
						velY = -1;
						break;
					}
				}
			}
		}
		if(p.getDirection() == -1) {
			if(ball.x > 0) {
				ball.x--;
			}
			else {
				gameover = true;
				score = 2;
			}
		} else if(p.getDirection() == 1) {
			if(ball.x < SIZE-1) {
				ball.x++;
			} else {
				gameover = true;
				score = 1;
			}
		}
		
		if(ball.x >= SIZE || ball.x < 0) {
			p.toggleDirection();
		}
		if (gameover) {
			gameoverAnimation();
		}
	}
	
	static class position {
		int x;
		int y;
		position(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
	
	@Override 
	public void input() {
		if (getKeyState("Up") == KeyState.Pressed) {
			dir = Direction.Up;
		} 
		if (getKeyState("Down") == KeyState.Pressed) {
			dir = Direction.Down;
		}
		if (getKeyState("W") == KeyState.Pressed) {
			dir2 = Direction.Up;
		} 
		if (getKeyState("S") == KeyState.Pressed) {
			dir2 = Direction.Down;
		}
		
	}
}
