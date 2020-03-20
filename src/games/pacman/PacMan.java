package games.pacman;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import led.Direction;
import led.Game;
import led.KeyState;
import led.Types;

public class PacMan extends Game {
	private static final long serialVersionUID = 1L;
	private PacManBasic player = new PacManBasic(Direction.Right, 30, 1, 1);
	private ArrayList<PacManGhost> ghosts = new ArrayList<PacManGhost>() { private static final long serialVersionUID = 1L;
	{
		add(new PacManGhost(Direction.None, 0, 9, 9, "Z"));
		add(new PacManGhost(Direction.None, 0, 10, 9, "X"));
		add(new PacManGhost(Direction.None, 0, 9, 10, "C"));
		add(new PacManGhost(Direction.None, 0, 10, 10, "V"));
	}};
	private ArrayList<PacManBasic> snackss = new ArrayList<PacManBasic>();
	private ArrayList<PacManBasic> snacksb = new ArrayList<PacManBasic>();
	private ArrayList<PacManTagged[]> portals = new ArrayList<PacManTagged[]>();
	
	private boolean teleported, teleport, gameover, superpac, won;
	private PacManTagged teleportTo;
	private int maxscore;
	
	public PacMan(boolean mode) throws FileNotFoundException {
		super(4, mode);
		
		score = 0;
		teleport = false;
		teleported = false;
		gameover = false;
		superpac = false;
		
		
		mapC.put("N", -1); // Nothing
		mapC.put("W", 160); // Wall
		mapC.put("G", 96); // Gate
		mapC.put("P", 64); // Player
		mapC.put("Z", 32); // Ghost 1
		mapC.put("X", 0); // Ghost 2
		mapC.put("C", 128); // Ghost 3
		mapC.put("V", 224); // Ghost 4
		mapC.put("E", 0); // Blue Ghost
		mapC.put("S", -2); // Snack
		mapC.put("B", 32); // Big Snack
		mapC.put("O", -2); // Portal
		
		mapC.put("0", -1);
		mapC.put("1", 160);
		mapC.put("2", 70);
		mapC.put("3", 160);
		mapC.put("4", 70);
		mapC.put("5", 70);
		mapC.put("6", 160);
		mapC.put("7", 70);
		mapC.put("8", 160);
		mapC.put("9", -1);
		
		loadBoard("pacman1.txt");
		
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				if (board[y][x].getID().equals("N") || board[y][x].getID().equals("W")) {
					board[y][x].setType(Types.NonLiving);
				} else {
					board[y][x].setType(Types.Living);
				}
			}
		}
		
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				if (board[y][x].getID().equals("P")) {
					player.setPosition(new int[] { x, y });
				} else if (board[y][x].getID().equals("S")) {
					snackss.add(new PacManBasic(Direction.None, 0, x, y));
				} else if (board[y][x].getID().equals("B")) {
					snacksb.add(new PacManBasic(Direction.None, 0, x, y));
				} else if (board[y][x].getID().equals("O") || board[y][x].getID().equals("J") || board[y][x].getID().equals("K")) {
					boolean added = false;
					for (PacManTagged[] pm : portals) {
						if (pm[0].getID().equals(board[y][x].getID())) {
							pm[1] = new PacManTagged(Direction.None, 0, x, y, pm[0].getID());
							added = true;
							continue;
						}
					}
					if (!added) {
						PacManTagged npm[] = { new PacManTagged(Direction.None, 0, x, y, board[y][x].getID()), null };
						portals.add(npm);
					}
				}
			}
		}
		maxscore = (int) snackss.size();
		
		
		(new Thread(new freeGhostThread(ghosts.get(0), 4000))).start();
		(new Thread(new freeGhostThread(ghosts.get(1), 6000))).start();
		(new Thread(new freeGhostThread(ghosts.get(2), 8000))).start();
		(new Thread(new freeGhostThread(ghosts.get(3), 10000))).start();
	}
	
	private class superpacThread implements Runnable {
		private int seconds;
		public superpacThread(int seconds) { 
			this.seconds = seconds; 
		}
        public void run() {
        	try {Thread.sleep(seconds * 1000); } catch (InterruptedException e) {  }
        	superpac = false;
        }
    };
	
    private class freeGhostThread implements Runnable {
    	private int time;
    	private PacManGhost pmg;
    	public freeGhostThread(PacManGhost pmg, int time) {
    		this.pmg = pmg;
    		this.time = time;
    	}
    	public void run() {
    		try { Thread.sleep(time); } catch (InterruptedException e) {}
    		pmg.setPosition(new int[] { 10, 7 });
    		pmg.unFreeze();
    		
    		if (rand.nextInt(2) + 1 == 1) {
    			pmg.changeDirection(Direction.Left);
    		} else {
    			pmg.changeDirection(Direction.Right);
    		}
    	}
    }
    
    private void checkGhostPlayerCollision() {
    	int currPos[] = player.getPosition();
    	
		for (int i = 0; i < ghosts.size(); i++) {
			PacManBasic pm = ghosts.get(i);
			if (Arrays.equals(currPos, pm.getPosition())) {
				if (superpac) {
					pm.setPosition(new int[] { 10, 10 });
					pm.changeDirection(Direction.None);
				} else {
					gameover = true;
					return;
				}
			}
		}
    }
    
    private void updatePlayer(boolean draw) {
		int lastPos[] = player.getPosition();
		//String lastID = board[lastPos[0]][lastPos[1]].getID();
		if (!teleport && !teleported) player.move();
		int currPos[] = player.getPosition();
		
		// Collision
		
		for (PacManTagged[] pm : portals) {
			placeObject(pm[0].getPosition()[0], pm[0].getPosition()[1], pm[0].getID());
			placeObject(pm[1].getPosition()[0], pm[1].getPosition()[1], pm[1].getID());
		}
		
		switch (board[currPos[1]][currPos[0]].getID()) {
			// Wall - Gate
			case "W":
			case "G":
				player.setPosition(new int[] { lastPos[0], lastPos[1] });
				checkGhostPlayerCollision();
				break;
			// Portal
			case "O":
			case "J":
			case "K":
				for (PacManTagged[] pm : portals) {
					if (teleported || teleport) {
						break;
					} else if (Arrays.equals(currPos, pm[0].getPosition())){
						teleportTo = new PacManTagged(Direction.None, 0, pm[1].getPosition()[0], pm[1].getPosition()[1], pm[1].getID());
						teleport = true;
					} else if (Arrays.equals(currPos, pm[1].getPosition())) {
						teleportTo = new PacManTagged(Direction.None, 0, pm[0].getPosition()[0], pm[0].getPosition()[1], pm[0].getID());
						teleport = true;
					}
				}
				break;
			// Ghosts
			case "Z":
			case "X":
			case "C":
			case "V":
				checkGhostPlayerCollision();
				break;
			// Snacks
			case "B":
				superpac = true;
				(new Thread(new superpacThread(10))).start();
				score++;
				for (int i = 0; i < snacksb.size(); i++) {
					if (Arrays.equals(snacksb.get(i).getPosition(), currPos)) {
						snacksb.remove(i);
					}
				}
				break;
			case "S":
				score++;
				for (int i = 0; i < snackss.size(); i++) {
					if (Arrays.equals(snackss.get(i).getPosition(), currPos)) {
						snackss.remove(i);
					}
				}
				break;
			// Nothing	
			case "N": default: break;
		}
		
		checkGhostPlayerCollision();
		// Draw Player
		if (draw && !gameover) {
			
			//placeObject(lastPos[0], lastPos[1], "N");
			//placeObject(player.getPosition()[0], player.getPosition()[1], "P");
			
			if (teleport) {
				teleported = true;
				teleport = false;
				player.setPosition(new int[]{ teleportTo.getPosition()[0], teleportTo.getPosition()[1] });
			} else if (teleported) {
				teleported = false;
			} else {
				//placeObject(lastPos[0], lastPos[1], "N");
				placeObject(player.getPosition()[0], player.getPosition()[1], "P");
			}
		}
    }
    
    private boolean updateGhost(PacManGhost ghost, boolean draw) {
    	if (ghost.canMove()) {
	    	int lastPos[] = ghost.getPosition();
			if (!ghost.teleport && !ghost.teleported) ghost.move();
			int currPos[] = ghost.getPosition();
			
			// Collision
			switch (board[currPos[1]][currPos[0]].getID()) {
				// Wall - Gate
				case "W":
				case "G":
					ghost.setPosition(new int[] { lastPos[0], lastPos[1] });
					ghost.changeDirection(randomDirection());
					
					if (!draw) return false;
					
					while (true) {
						if (updateGhost(ghost, false)) break;
					}
					
					break;
				// Portal
				case "O":
				case "J":
				case "K":
					for (PacManTagged[] pm : portals) {
						if (ghost.teleported || ghost.teleport) {
							break;
						} else if (Arrays.equals(currPos, pm[0].getPosition())){
							ghost.teleportTo = new PacManTagged(Direction.None, 0, pm[1].getPosition()[0], pm[1].getPosition()[1], pm[1].getID());
							ghost.teleport = true;
						} else if (Arrays.equals(currPos, pm[1].getPosition())) {
							ghost.teleportTo = new PacManTagged(Direction.None, 0, pm[0].getPosition()[0], pm[0].getPosition()[1], pm[0].getID());
							ghost.teleport = true;
						}
					}
					break;
				// Snacks
				case "B":
				case "S":
				// Nothing	
				case "N":
				// Ghosts
				case "Z":
				case "X":
				case "C":
				case "V":
				default: break;	
			}
			
			if (draw) {
				/*if ((!teleport && !teleported) && (ghost.teleport || ghost.teleported)) {
					for (PacManTagged[] pm : portals) {
						placeObject(pm[0].getPosition()[0], pm[0].getPosition()[1], pm[0].getID());
						placeObject(pm[1].getPosition()[0], pm[1].getPosition()[1], pm[1].getID());
					}
				}*/
					
				//placeObject(ghost.getPosition()[0], ghost.getPosition()[1], ghost.getID());
				
				if (ghost.teleport) {
					ghost.teleported = true;
					ghost.teleport = false;
					ghost.setPosition(new int[]{ ghost.teleportTo.getPosition()[0], ghost.teleportTo.getPosition()[1] });
				} else if (ghost.teleported) {
					ghost.teleported = false;
				} else {
					//placeObject(lastPos[0], lastPos[1], lastID);
				}
				int tpos[] = new int[]{ ghost.getPosition()[0], ghost.getPosition()[1] };
				int lcount = 0;
				
				for (int y = tpos[1] - 1; y < tpos[1] + 1; y++) {
					for (int x = tpos[0] - 1; x < tpos[0] + 1; x++) {
						if (board[y][x].isLiving()) {
							lcount++;
							if (lcount > 2 && rand.nextInt(5) < 1) {
								if (y < 9 && rand.nextInt(100) < 35) {
									ghost.changeDirection(Direction.Down);
								} else if (y > 10 && rand.nextInt(100) < 35) {
									ghost.changeDirection(Direction.Up);
								} else {
									ghost.changeDirection(randomDirection());
								}
								break;
							}
						}
					}
				}
			} else {
				ghost.setPosition(new int[] { lastPos[0], lastPos[1] });
			}
			
    	}
		// Draw Ghost
		if (draw) {
			if (superpac) {
				placeObject(ghost.getPosition()[0], ghost.getPosition()[1], "E");
			} else {
				placeObject(ghost.getPosition()[0], ghost.getPosition()[1], ghost.getID());
			}
		}
		
		return true;
    }
    
    private void updateGhosts() {
    	for (PacManGhost pmg : ghosts) {
    		updateGhost(pmg, true);
    	}
    }
    
    private void updateSnacks() {
    	for (PacManBasic pm : snackss) {
    		if (pm == null) continue;
    		placeObject(pm.getPosition()[0], pm.getPosition()[1], "S");
    	}
    	for (PacManBasic pm : snacksb) {
    		if (pm == null) continue;
    		placeObject(pm.getPosition()[0], pm.getPosition()[1], "B");
    	}
    }
    
	@Override
	public void tick() throws FileNotFoundException {
		if (!trytick() || gameover || won) return;
		clearBoard();
		
		updateSnacks();
		updateGhosts();
		updatePlayer(true);
		
		if (score == maxscore) {
			won = true;
		}
		
		if (gameover) {
			gameoverAnimation();
		} else if (won) {
			wonAnimation();
		}
	}

	@Override
	public void input() {
		if (getKeyState("Up") == KeyState.Pressed) {
			player.changeDirection(Direction.Up);
		} else if (getKeyState("Down") == KeyState.Pressed) {
			player.changeDirection(Direction.Down);
		} else if (getKeyState("Right") == KeyState.Pressed) {
			player.changeDirection(Direction.Right);
		} else if (getKeyState("Left") == KeyState.Pressed) {
			player.changeDirection(Direction.Left);
		}
	}
}
