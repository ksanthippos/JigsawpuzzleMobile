package com.mygdx.game.screens;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.game.JigsawPuzzleGame;
import com.mygdx.game.base_classes.BaseActor;
import com.mygdx.game.base_classes.BaseGame;
import com.mygdx.game.base_classes.BaseScreen;

public class MenuScreen extends BaseScreen {

    public static int difficulty;

    @Override
    public void initialize() {

        // background
        BaseActor background = new BaseActor(0, 0, mainStage);
        background.loadTexture("background.jpg");
        background.setSize(800, 600);
        BaseActor.setWorldBounds(background);
        Label title = new Label("Jigsaw Puzzle Game", BaseGame.labelStyle);
        title.moveBy(0, 100);

        // start and quit buttons
        TextButton easyButton = new TextButton("Easy", BaseGame.textButtonStyle);
        TextButton normalButton = new TextButton("Normal", BaseGame.textButtonStyle);
        TextButton hardButton = new TextButton("Hard", BaseGame.textButtonStyle);
        TextButton quitButton = new TextButton("Quit", BaseGame.textButtonStyle);
        uiStage.addActor(easyButton);
        uiStage.addActor(quitButton);

        easyButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(InputEvent.Type.touchDown))
                return false;
            difficulty = 2;
            JigsawPuzzleGame.setActiveScreen(new GameScreen());
            return false;
        });

        normalButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(InputEvent.Type.touchDown))
                return false;
            difficulty = 3;
            JigsawPuzzleGame.setActiveScreen(new GameScreen());
            return false;
        });

        hardButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(InputEvent.Type.touchDown))
                return false;
            difficulty = 4;
            JigsawPuzzleGame.setActiveScreen(new GameScreen());
            return false;
        });

        quitButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(InputEvent.Type.touchDown))
                return false;
            System.exit(0);
            return false;
        });

        // arrange title and buttons
        uiTable.add(title).colspan(4);
        uiTable.row();
        uiTable.add(easyButton);
        uiTable.add(normalButton);
        uiTable.add(hardButton);
        uiTable.row();
        uiTable.add(quitButton).colspan(4);

    }

    @Override
    public void update(float dt) { }
}
