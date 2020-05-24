package com.mygdx.game.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.mygdx.game.JigsawPuzzleGame;


public class DesktopLauncher {
	public static void main (String[] arg) {

		Game game = new JigsawPuzzleGame();
		new LwjglApplication(game, "Jigsaw Puzzle Game", 800, 600);

	}
}
