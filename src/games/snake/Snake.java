package games.snake;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import led.Direction;
import led.Game;
import led.KeyState;

public class Snake extends Game {
	private static final long serialVersionUID = 1L;
	Random rand = new Random();
	ArrayList<position> arr = new ArrayList<position>();
	position snack;
	position head = new position(10, 10);
	Direction dir = Direction.None; 
	boolean isAlive = true;
	int xFruit;
	int yFruit;
	public Snake(boolean mode) {
		super(5, mode);
		mapC.put("S", 0);
		mapC.put("F", 160);
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
		xFruit = rand.nextInt(20);
		yFruit = rand.nextInt(20);
		arr.add(head);
	}
	
	@Override 
	public void tick() throws FileNotFoundException {
		if (!trytick() || gameover) return;
		clearBoard();
		
		//update location
		for(int i = arr.size()-1; i > 0; i--) { 
			arr.get(i).x = arr.get(i-1).x;
			arr.get(i).y = arr.get(i-1).y;
		}
		
		if(dir == Direction.Up) {
			head.y--;
		}
		else if(dir == Direction.Down) {
			head.y++;
		}
		else if(dir == Direction.Right) {
			head.x++;
		}
		else if(dir == Direction.Left) {
			head.x--;
		}
		//check to see if you are out of bounds
		if(arr.get(0).x >= SIZE || arr.get(0).y  >= SIZE || arr.get(0).x < 0 || arr.get(0).y < 0) {
			gameover = true;
		}
		int arrSize = arr.size();
		//check to see if you are on fruit
		if(arr.get(0).x == xFruit && arr.get(0).y == yFruit) {
			arr.add(new position(head.x, head.y));
			xFruit = rand.nextInt (20);
			yFruit = rand.nextInt(20);
			score++;
		}
		for(int i = arrSize - 1; i > 1; i--) {
			 //check to see if hit yourself
			if(arr.get(0).x == arr.get(i).x && arr.get(0).y == arr.get(i).y) {
				gameover = true;
				break;
			}
			
		}
		
		//draw to board
		
		if(isAlive && !gameover) {
			placeObject(xFruit, yFruit, "F");
			for(int i = 0; i < arr.size(); i++) {
				placeObject(arr.get(i).x, arr.get(i).y, "S");
			}
		}
		
		if(gameover) {
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
		if (getKeyState("Up") == KeyState.Pressed && !(dir == Direction.Down)) {
			dir = Direction.Up;
		}
		if (getKeyState("Down") == KeyState.Pressed && !(dir == Direction.Up)) {
			dir = Direction.Down;
		}
		if (getKeyState("Right") == KeyState.Pressed && !(dir == Direction.Left)) {
			dir = Direction.Right;
		}
		if (getKeyState("Left") == KeyState.Pressed && !(dir == Direction.Right)) {
			dir = Direction.Left;
		}
	}
}

