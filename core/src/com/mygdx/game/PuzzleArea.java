package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.drag_drop_classes.DropTargetActor;

public class PuzzleArea extends com.mygdx.game.drag_drop_classes.DropTargetActor {

    private int row;
    private int col;

    public PuzzleArea(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("border.jpg");
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
