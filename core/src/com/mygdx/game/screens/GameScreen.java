package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.game.JigsawPuzzleGame;
import com.mygdx.game.PuzzleArea;
import com.mygdx.game.PuzzlePiece;
import com.mygdx.game.base_classes.BaseActor;
import com.mygdx.game.base_classes.BaseGame;
import com.mygdx.game.base_classes.BaseScreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GameScreen extends BaseScreen {

    private Label messageLabel;
    private List<Texture> images;

    @Override
    public void initialize() {

        int numberRows = MenuScreen.difficulty;
        int numberCols = MenuScreen.difficulty;

        BaseActor background = new BaseActor(0, 0, mainStage);
        background.loadTexture("background.jpg");
        BaseActor.setWorldBounds(background);

        // images
        images = new ArrayList<>();
        images.add(new Texture(Gdx.files.internal("images/dog.jpg"), true));
        images.add(new Texture(Gdx.files.internal("images/eye.jpg"), true));
        images.add(new Texture(Gdx.files.internal("images/forest.jpg"), true));
        images.add(new Texture(Gdx.files.internal("images/leaf.jpg"), true));
        images.add(new Texture(Gdx.files.internal("images/mario.jpg"), true));
        images.add(new Texture(Gdx.files.internal("images/space.jpg"), true));
        images.add(new Texture(Gdx.files.internal("images/sunset.jpg"), true));
        images.add(new Texture(Gdx.files.internal("images/sun.jpg"), true));
        images.add(new Texture(Gdx.files.internal("images/flowers.jpg"), true));
        images.add(new Texture(Gdx.files.internal("images/leipa.jpg"), true));
        images.add(new Texture(Gdx.files.internal("images/luumu.jpg"), true));
        images.add(new Texture(Gdx.files.internal("images/mansikat.jpg"), true));


        // pick a random image
        Collections.shuffle(images);
        Texture texture = images.get(0);

        int imageWidth = texture.getWidth();
        int imageHeight = texture.getHeight();
        int pieceWidth = imageWidth / numberCols;
        int pieceHeight = imageHeight / numberRows;

        TextureRegion[][] temp = TextureRegion.split(texture, pieceWidth, pieceHeight);

        for (int i = 0; i < numberRows; i++) {
            for (int j = 0; j < numberCols; j++) {

                // create pieces and randomize order
                int pieceX = MathUtils.random(0, 400 - pieceWidth);
                int pieceY = MathUtils.random(100, 600 - pieceHeight);
                PuzzlePiece pp = new PuzzlePiece(pieceX, pieceY, mainStage);

                // dragging causes animation (piece gets zoomed in a bit)
                Animation<TextureRegion> anim = new Animation<>(1, temp[i][j]);
                pp.setAnimation(anim);
                pp.setRow(i);
                pp.setCol(j);

                int marginX = (400 - imageWidth) / 2;
                int marginY = (600 - imageHeight) / 2;
                // board setup
                int areaX = (400 + marginX) + pieceWidth * j;
                int areaY = (600 - marginY - pieceHeight) - pieceHeight * i;

                PuzzleArea pa = new PuzzleArea(areaX, areaY, mainStage);
                pa.setSize(pieceWidth, pieceHeight);
                pa.setBoundaryRectangle();
                pa.setRow(i);
                pa.setCol(j);
            }
        }

        // win message
        messageLabel = new Label("...", BaseGame.labelStyle);
        messageLabel.setColor(Color.CYAN);
        uiTable.add(messageLabel).expandX().expandY().top().pad(50);
        messageLabel.setVisible(false);

        // buttons
        TextButton quitButton = new TextButton("Back", BaseGame.textButtonStyle);
        uiTable.add(quitButton);
        uiStage.addActor(quitButton);

        quitButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(InputEvent.Type.touchDown))
                return false;
            JigsawPuzzleGame.setActiveScreen(new MenuScreen());
            return false;
        });

    }

    @Override
    public void update(float dt) {

        // check win conditions
        boolean solved = true;
        for (BaseActor actor: BaseActor.getList(mainStage, PuzzlePiece.class.getCanonicalName())) {

            PuzzlePiece pp = (PuzzlePiece) actor;
            if (!pp.isCorrectlyPlaced())
                solved = false;

            if (solved) {
                messageLabel.setText("You solved the puzzle!");
                messageLabel.setVisible(true);
            }

            else {
                messageLabel.setText("...");
                messageLabel.setVisible(false);
            }
        }

    }


}
