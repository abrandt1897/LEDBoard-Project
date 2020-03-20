package games.tetris;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import led.Game;
import led.KeyState;
import led.Types;

public class Tetris extends Game {
	private static final long serialVersionUID = 1L;

	private Piece piece;
	int cursor[];
	int wallOffset = 5;
	boolean movable = true;
	boolean automove = false;
	ArrayList<Block> pieces = new ArrayList<Block>();
	ArrayList<Block> tetrisBoard = new ArrayList<Block>();
	
	public Tetris(boolean mode) {
		super(5, mode);
		
		mapC.put("W", -4);
		mapC.put("X", -1);
		mapC.put("R", 0);
		mapC.put("O", 32);
		mapC.put("Y", 64);
		mapC.put("G", 96);
		mapC.put("A", 128);
		mapC.put("B", 160);
		mapC.put("P", 192);
		mapC.put("K", 224);
		
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
		
		for (int y = 0; y < board.length; y++) {
			for (int x = 0; x < wallOffset; x++) {
				tetrisBoard.add(new Block(x, y, "W"));
				tetrisBoard.add(new Block(SIZE-1-x, y, "W"));
				board[y][x].setType(Types.NonLiving);
				board[y][SIZE-1-x].setType(Types.NonLiving);
			}
		}
		
		resetPiece(false);
		
		(new Thread(new gravity())).start();
	}
	
	private static int[][] rotateMatrix(int matrix[][]){
		int h = matrix.length;
		int w = matrix[0].length;
		
		int mat[][] = new int[w][h];
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				mat[x][y] = matrix[y][x];
			}
		}
		
		for (int y = 0; y < w; y++) {
			for (int x = 0; x < h / 2; x++) {
				int temp = mat[y][h-x-1];
				mat[y][h-x-1] = mat[y][x];
				mat[y][x] = temp; 
			}
		}
		
		return mat;
	}
	private Piece getNewPiece(String p) {
		switch(p) {
			case "L": return new L(cursor[0], cursor[1], ""+"ROYGBP".charAt(rand.nextInt(6)));
			case "J": return new J(cursor[0], cursor[1], ""+"ROYGBP".charAt(rand.nextInt(6)));
			case "I": return new I(cursor[0], cursor[1], ""+"ROYGBP".charAt(rand.nextInt(6)));
			case "O": return new O(cursor[0], cursor[1], ""+"ROYGBP".charAt(rand.nextInt(6)));
			case "S": return new S(cursor[0], cursor[1], ""+"ROYGBP".charAt(rand.nextInt(6)));
			case "Z": return new Z(cursor[0], cursor[1], ""+"ROYGBP".charAt(rand.nextInt(6)));
			case "T": return new T(cursor[0], cursor[1], ""+"ROYGBP".charAt(rand.nextInt(6)));
			default: return new Block(cursor[0], cursor[1], ""+"ROYGBP".charAt(rand.nextInt(6)));
		}
	}
	private void loadCursorPieces() {
		pieces.clear();
		if (cursor[0] < wallOffset) {
			cursor[0] = wallOffset;
		} else if (cursor[0] + piece.shape[0].length > SIZE - wallOffset) {
			cursor[0] = SIZE - wallOffset - piece.shape[0].length;
		}
		for (int y = 0; y < piece.shape.length; y++) {
			for (int x = 0; x < piece.shape[0].length; x++) {
				if (piece.shape[y][x] == 1) pieces.add(new Block(cursor[0]+x, cursor[1]+y, piece.color));
			}
		}
	}
	private boolean checkCollision(int x, int y) {
		for (Block b : pieces) {
			for (Block p : tetrisBoard) {
				if (b.pos.x + x == p.pos.x && b.pos.y + y == p.pos.y) {
					return true;
				}
			}
		}
		return false;
	}
	private int[] countBoard() {
		int c[] = new int[SIZE];
		for (Block b : tetrisBoard) {
			c[b.pos.y]++;
		}
		return c;
	}
	private void resetPiece(boolean place) {
		while (checkCollision(0,0)) { for (Block b : pieces) { b.moveUp(); } }
		
		if (place) {
			for (Block b : pieces) {
				if (b.pos.y < 1) {
					gameover = true;
					return;
				}
				tetrisBoard.add(b);
			}
			
			int c[] = countBoard();
			for (int i = SIZE - 1; i > 0; i--) {
				if (c[i] == 20) {
					ArrayList<Block> temp = new ArrayList<Block>();
					for (Block b : tetrisBoard) {
						if (b.pos.y == i && b.pos.x > wallOffset - 1 && b.pos.x < SIZE - wallOffset) {
						} else {
							temp.add(b);
						}
					}
					tetrisBoard.clear();
					for (Block b : temp) tetrisBoard.add(b);
					
					for (Block b : tetrisBoard) {
						if (b.pos.y < i &&  b.pos.x > wallOffset - 1 && b.pos.x < SIZE - wallOffset) b.moveDown();
					}
					i++;
					c = countBoard();
					score++;
				}
				
			}
		}
		
		cursor = new int[] { 10, 0 };
		piece = getNewPiece(""+"LJIOSZT".charAt(rand.nextInt(7)));
		loadCursorPieces();
	}

	
	@Override
	public void tick() throws FileNotFoundException {
		if (!trytick() || gameover) return;
		clearBoard();
		
		if (automove) {
			automove = false;
			if (cursor[1] + piece.shape.length < SIZE) {
				if (checkCollision(0, 1)) {
					resetPiece(true);
				}
				cursor[1]++;
				for (Block b : pieces) {
					b.moveDown();
				}
			} else {
				resetPiece(true);
			}
		}
		
		for (Block p : tetrisBoard) {
			placeObject(p.pos.x, p.pos.y, p.color);
		}
		
		for (Block p : pieces) {
			if (p.pos.y < 0) {
				gameover = true;
				break;
			} else placeObject(p.pos.x, p.pos.y, p.color);
		}
		
		if (gameover) {
			gameoverAnimation();
		}
	}
	@Override 
	public void input() {
		if (getKeyState("Right") == KeyState.Clicked && cursor[0] + piece.shape[0].length < SIZE - wallOffset && !checkCollision(1, 0)) {
			cursor[0]++;
			for (Block b : pieces) b.moveRight();
		} else if (getKeyState("Left") == KeyState.Clicked && cursor[0] > wallOffset && !checkCollision(-1, 0)) {
			cursor[0]--;
			for (Block b : pieces) b.moveLeft();
		} else if (getKeyState("Down") == KeyState.Pressed) {
			automove = true;
		} else if (getKeyState("Z") == KeyState.Clicked) {
			Piece tempP = new Piece(piece);
			tempP.shape = rotateMatrix(tempP.shape);
			piece = tempP;
			loadCursorPieces();
		}
	}

	
	private class gravity implements Runnable{
		public void run() {
			while (movable) {
				if (gameover) break;
				try { Thread.sleep(2500); automove = true;  } catch (InterruptedException e) {}
			}
		}
	}
	
	private class Piece {
		public Position pos = new Position();
		public int shape[][];
		public String color;
		public Piece(Piece piece) {
			this.shape = piece.shape;
			this.color = piece.color;
			this.pos = piece.pos;
		}
		public Piece(int x, int y, String color) {
			pos.x = x;
			pos.y = y;
			this.color = color;
		}
		public void moveUp() {
			pos.y--;
		}
		public void moveDown() {
			if (pos.y < SIZE -1) pos.y++;
		}
		public void moveLeft() {
			pos.x--;
		}
		public void moveRight() {
			pos.x++;
		}
	}
	private class Block extends Piece{
		public Block(int x, int y, String color) {
			super(x, y, color);
			shape = new int[][] { { 1 } };
		}
	}

	private class L extends Piece {
		public L(int x, int y, String color) {
			super(x, y, color);
			shape = new int[][]{
				{ 1, 0 },
				{ 1, 0 },
				{ 1, 1 }
			};
		}
		
	}
	private class J extends Piece {
		public J(int x, int y, String color) {
			super(x, y, color);
			shape = new int[][]{
				{ 0, 1 },
				{ 0, 1 },
				{ 1, 1 }
			};
		}
		
	}
	private class I extends Piece {
		public I(int x, int y, String color) {
			super(x, y, color);
			shape = new int[][]{
				{ 1 },
				{ 1 },
				{ 1 },
				{ 1 }
			};
		}
	}
	private class O extends Piece {
		public O(int x, int y, String color) {
			super(x, y, color);
			shape = new int[][]{
				{ 1, 1 },
				{ 1, 1 }
			};
		}
	}
	private class S extends Piece {
		public S(int x, int y, String color) {
			super(x, y, color);
			shape = new int[][]{
				{ 0, 1, 1 },
				{ 1, 1, 0 }
			};
		}
	}
	private class Z extends Piece {
		public Z(int x, int y, String color) {
			super(x, y, color);
			shape = new int[][]{
				{ 1, 1, 0 },
				{ 0, 1, 1 }
			};
		}
	}
	private class T extends Piece {
		public T(int x, int y, String color) {
			super(x, y, color);
			shape = new int[][]{
				{ 1, 1, 1 },
				{ 0, 1, 0 }
			};
		}
		
	}
}
