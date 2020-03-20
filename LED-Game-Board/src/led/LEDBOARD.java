package led;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import games.avoidable.Avoidable;
import games.breakout.Breakout;
import games.pacman.PacMan;
import games.pong.Pong;
import games.snake.Snake;
import games.tetris.Tetris;
import led.Game.InputEnvironment;
import led.Game.InputEnvironment.MyKeyListener;


@SuppressWarnings("unused")
public class LEDBOARD {
	public static class option {
		public String logo;
		public String game;
		public option(String logo, String game) {
			this.logo = logo;
			this.game = game;
		}
	}
	
	public static Game getGame(String Game, boolean mode) throws FileNotFoundException {
		switch (Game) {
			case "PacMan": return new PacMan(mode);
			case "Breakout": return new Breakout(mode);
			case "Snake": return new Snake(mode);
			case "Pong": return new Pong(mode);
			case "Tetris": return new Tetris(mode);
			case "Avoidable": return new Avoidable(mode);
			default: System.out.println("Error loading game: " + Game); break;
		}
		return null;
	}
	
	public static void main(String args[]) throws InterruptedException, FileNotFoundException {
		option games[] = { 
				new option("pacmanlogo.txt", "PacMan"), 
				new option("breakoutlogo.txt", "Breakout"),
				new option("snakelogo.txt", "Snake"), 
				new option("ponglogo.txt", "Pong"),
				new option("tetrislogo.txt", "Tetris"),
				new option("avoidablelogo.txt", "Avoidable")
		};
		int cursor = 0;
		boolean playing = false;
		boolean mode = true;
		
		Game game;
		Game menu = new Game(5, mode); menu.loadBoard();
		
		menu.mapC.put("W", -2);
		menu.mapC.put("X", -1);
		menu.mapC.put("R", 0);
		menu.mapC.put("O", 32);
		menu.mapC.put("Y", 64);
		menu.mapC.put("G", 96);
		menu.mapC.put("A", 128);
		menu.mapC.put("B", 160);
		menu.mapC.put("P", 192);
		menu.mapC.put("K", 224);
		
		menu.loadBoard(games[cursor].logo);
		menu.enableKeyboard();
		if (mode) menu.output();
		
		
		while (true) {
			if (!menu.trytick()) continue;
			
			boolean updateMenuScreen = false;
			if (menu.getKeyState("Right") == KeyState.Clicked && cursor < games.length-1) {
				cursor++;
				updateMenuScreen = true;
			} else if (menu.getKeyState("Left") == KeyState.Clicked && cursor > 0) {
				cursor--;
				updateMenuScreen = true;
			} else if (menu.getKeyState("Enter") == KeyState.Clicked && !playing) {
				playing = true;
				game = getGame(games[cursor].game, mode);
				game.enableKeyboard();
				
				while (true) {
					game.input();
					game.tick();
					if (game.ticked) {
						if (mode) game.output();
					}
					
					if (game.gameover || game.won) {
						if (game.getKeyState("Escape") == KeyState.Clicked) {
							game.removeFrame();
							game = null;
							playing = false;
							cursor = 0;
							updateMenuScreen = true;
							break;
						}
					}
					game.checkClickedKeys();
				}
			}
			
			if (updateMenuScreen) {
				menu.nukeBoard();
				menu.loadBoard(games[cursor].logo);
				
				if (mode) menu.output();
			}
			menu.checkClickedKeys();
		}
	}
}
