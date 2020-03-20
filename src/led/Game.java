package led;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends JPanel {
	private static final long serialVersionUID = 1L;

	public final int SIZE = 20;
	public final int GUIH = 400;
	public final int GUIW = 400;
	
	public Object board[][] = new Object[SIZE][SIZE];
	public Network soc;
	public int fpscap, fps, fpscount, score;
	
	public boolean ticked, gameover, won, drawmode;
	
	public Map<String, Integer> mapC = new HashMap<String, Integer>();
	public Map<String, KeyState> keyboard = new HashMap<String, KeyState>();
	
	public Random rand = new Random(System.currentTimeMillis());
	
	public InputEnvironment ie;
	
	public long lastTime, currTime;
	
	public Game(int fps, boolean mode) {
		lastTime = System.currentTimeMillis();
		currTime = System.currentTimeMillis();
		
		ticked = false;
		this.fpscap = fps;
		score = 0;
		
		// Start Socket
		if (mode) soc = new Network("192.168.1.25", 6969); 
		this.drawmode = !mode;
		
		// Update Keyboard Map
		String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		for (int i = 0; i < abc.length(); i++) {
			keyboard.put(""+abc.charAt(i), KeyState.Released);
		}
		keyboard.put("Up", KeyState.Released);
		keyboard.put("Down", KeyState.Released);
		keyboard.put("Right", KeyState.Released);
		keyboard.put("Left", KeyState.Released);
		keyboard.put("Enter", KeyState.Released);
		keyboard.put("Backspace", KeyState.Released);
		keyboard.put("Escape", KeyState.Released);
		keyboard.put("Space", KeyState.Released);
		
		// FPS Thread
		(new Thread(new fpsThread())).start();
	}
	
	/********* Getter Methods *************/
	
	
	/********* Board Methods *************/
	public void loadBoard() {
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 20; x++) {
				board[y][x] = new Object(-1, "N");
			}
		}
	}
	public void loadBoard(String file) throws FileNotFoundException {
		File f = new File(file);
		@SuppressWarnings("resource")
		Scanner s = new Scanner(f);
		
		for (int y = 0; y < 20; y++) {
			for (int x = 0; x < 20; x++) {
				String ID = s.next();
				board[y][x] = new Object(mapC.get(ID), ID);
			}
		}
	}
	public void placeObject(int x, int y, String ID) {
		board[y][x].setID(ID);
		board[y][x].setColor(mapC.get(ID));
	}
	public void clearBoard() {
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				if (board[y][x].isLiving()) {
					board[y][x].nullifyObject();
				}
			}
		}
	}
	public void nukeBoard() {
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				board[y][x].nullifyObject();
			}
		}
	}
	
	
	/********** Drawing Methods *************/
	public void drawGame(Graphics graph) {
		graph.setColor(Color.WHITE);
		graph.fillRect(0, 0, GUIW, GUIH);
		
		//graph.setColor(Color.BLACK);
		//for (int x = 0; x < 20; x++) graph.drawLine(x*SIZE, 0, x*SIZE, GUIH);
		//for (int y = 0; y < 20; y++) graph.drawLine(0, y*SIZE, GUIW, y*SIZE);
		
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				int num1 = Integer.parseInt(board[y][x].getColor());
				
				Color nc = null;
				if (num1 == -1) {
					nc = new Color(0, 0, 0);
				} else if (num1 == -2) {
					nc = new Color(255, 255, 255);
				} else {
					// Red to Yellow
					if (num1 <= 64) 					nc = new Color(255							, (int) (num1/64.0 * 255)		, 0);
					// Yellow to Green
					else if (num1 > 64 && num1 <= 96)  nc = new Color((int) ((96-num1)/32.0 * 255) 	, 255							, 0);
					// Green to Aqua
					else if (num1 > 96 && num1 <= 128) nc = new Color(0								, 255							, (128-num1)/32*255);
					// Aqua to Blue
					else if (num1 > 128 && num1 <= 160) nc = new Color(0							, (int) ((160-num1)/32.0*255)	, 255);
					// Blue to Purple
					else if (num1 > 160 && num1 <= 192) nc = new Color((int) ((192-num1)/32.0*255)	, 0								, 255);
					// Purple to Pink
					else if (num1 > 192 && num1 <= 224) nc = new Color(255					, 0										, (int) ((224-num1)/32.0*255));
					//System.out.print("(" + nc.getRed() + ", " + nc.getGreen() + ", " + nc.getBlue() + ")");
					/*
					 * if (num1 < 85) {
						nc = new Color((num1*3), 255-(num1*3), 0);
					} else if (num1 < 170) {
						num1 -= 85;
						nc = new Color(255-(num1*3), 0, (num1*3));
					} else {
						num1 -= 170;
						nc = new Color(0, (num1*3), 255-(num1*3));
					}
					 */
				}
				graph.setColor(nc);
				graph.fillRect(x*SIZE, y*SIZE, SIZE, SIZE);
				//System.out.print(board[y][x].getID() + " ");
			}
			//System.out.println();
		}
		Toolkit.getDefaultToolkit().sync();
	}
	public void gameoverAnimation() throws FileNotFoundException {
		nukeBoard();
		(new Thread(new gameoverThread("gameover.txt"))).start();
	}
	public void wonAnimation() throws FileNotFoundException  {
		nukeBoard();
	}

	
	/********** Game Methods *************/
	public Direction randomDirection() {
		int rd = rand.nextInt(4) + 1;
		switch (rd) {
			case 1: return Direction.Down;
			case 2: return Direction.Up;
			case 3: return Direction.Right;
			case 4: return Direction.Left;
			default: return Direction.None;
		}
	}
	public boolean trytick() {
		currTime = System.currentTimeMillis();
		long delta = currTime - lastTime;
		ticked = false;
		
		if (delta < 1000 / fpscap) return false;
		
		ticked = true;
		lastTime = currTime;
		fpscount++;
		return true;
	}
	
	public class Position {
		public int x, y;
		public Position() {
			
		}
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
		public void setX(int x) {
			this.x = x;
		}
		public void setY(int y) {
			this.y = y;
		}
		public int[] getXY() {
			return new int[] { x, y };
		}
	}
	
	
	/********* Input Handling and Frame *************/
	public void enableKeyboard() {
		ie = new InputEnvironment();
		ie.startListen();
	}
	public void removeFrame() {
		ie.close();
	}	
	public class InputEnvironment extends JPanel {
		private static final long serialVersionUID = 1L;
		public JFrame frame;
		
		public InputEnvironment() {
			KeyListener listener = new MyKeyListener();
			addKeyListener(listener);
			setFocusable(true);
			if (drawmode) {
				Thread th = new Thread() {
					public void run() {
						while (true) {
							repaint();
							try { Thread.sleep((long) ((1.0/fpscap)*1000)); } catch (InterruptedException e) {}
						}
					}
				};
				th.start();
			}
		}
		public void startListen() {
			frame = new JFrame("Input");
			InputEnvironment keyboardExample = new InputEnvironment();
			frame.add(keyboardExample);
			if (drawmode) frame.setSize(GUIW+15, GUIH+40); else frame.setSize(50,150);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		}
		@Override
	    public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			drawGame(g);
	    }
		public void close() {
			frame.dispose();
		}
		public class MyKeyListener implements KeyListener {
			@Override
			public void keyTyped(KeyEvent e) {  }
			@Override
			public void keyPressed(KeyEvent e) {
				//System.out.println("keyPressed = "+KeyEvent.getKeyText(e.getKeyCode()));
				keyboard.put(KeyEvent.getKeyText(e.getKeyCode()), KeyState.Pressed);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				//System.out.println("keyReleased = "+KeyEvent.getKeyText(e.getKeyCode()));
				keyboard.put(KeyEvent.getKeyText(e.getKeyCode()), KeyState.Clicked);
			}
		}
	}
	public KeyState getKeyState(String key) {
		return keyboard.get(key);
	}
	public void checkClickedKeys() {
		for (Entry<String, KeyState> key : keyboard.entrySet()) {
			if (key.getValue() == KeyState.Clicked) {
				key.setValue(KeyState.Released);
			}
		}
	}
	
	
	/********* Threads *************/
	public class fpsThread implements Runnable{
		public fpsThread() {  }
		public void run() {
			while (true) {
				fps = fpscount;
				fpscount = 0;
				try { Thread.sleep(1000); } catch (InterruptedException e) { }
			}
		}
	}
	public class gameoverThread implements Runnable {
		Scanner s;
		public gameoverThread(String file) throws FileNotFoundException { 
			File f = new File(file);
			s = new Scanner(f);
		}
		public void run() {
			ticked = true;
			for (int y = 0; y < SIZE; y++) {
				for (int x = 0; x < SIZE; x++) {
					String ID = s.next();
					placeObject(x, y, ID);
				}
				if (y < 15) try { Thread.sleep(300); } catch (InterruptedException e) {  }
			}
			
			String s = Integer.toString(score);
			int cursor[] = { 7, 15 };
			String [][] l = null;
			
			for (int i = 0; i < s.length(); i++) {
				switch (s.charAt(i)) {
					case '0': l = ScoreBoardNumbers.zero; break;
					case '1': l = ScoreBoardNumbers.one; break;
					case '2': l = ScoreBoardNumbers.two; break;
					case '3': l = ScoreBoardNumbers.three; break;
					case '4': l = ScoreBoardNumbers.four; break;
					case '5': l = ScoreBoardNumbers.five; break;
					case '6': l = ScoreBoardNumbers.six; break;
					case '7': l = ScoreBoardNumbers.seven; break;
					case '8': l = ScoreBoardNumbers.eight; break;
					case '9': l = ScoreBoardNumbers.nine; break;
					default: l = ScoreBoardNumbers.pound; break;
				}
				
				int x = 0;
				for (int y = cursor[1]; y < SIZE; y++) {
					for (x = cursor[0]; x < Math.min(cursor[0] + 5, SIZE); x++) {
						placeObject(x, y, l[y-cursor[1]][x-cursor[0]]);
					}
				}
				cursor[0] = x;
			}
		}
	}

	
	/********* Networking *************/
	public boolean output() {
		String s = "";
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				s += board[y][x].getColor() + " ";
			}
		}
		//System.out.println(s);
		return soc.sendData(s);
	}
	
	
	/********* Overridden Methods *************/
	public void tick() throws FileNotFoundException {}
	public void input() {}
}