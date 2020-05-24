package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.drag_drop_classes.DragAndDropActor;

public class PuzzlePiece extends com.mygdx.game.drag_drop_classes.DragAndDropActor {

    private int row;
    private int col;
    private PuzzleArea puzzleArea;

    public PuzzlePiece(float x, float y, Stage s) {
        super(x, y, s);
    }

    public void onDragStart() {
        if (hasPuzzleArea()) {
            PuzzleArea pa = getPuzzleArea();
            pa.setTargetable(true);
            clearPuzzleArea();
        }
    }

    public void onDrop() {
        if (hasDropTarget()) {
            PuzzleArea pa = (PuzzleArea) getDropTarget();
            moveToActor(pa);
            setPuzzleArea(pa);
            pa.setTargetable(false);
        }
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

    public PuzzleArea getPuzzleArea() {
        return puzzleArea;
    }

    public void setPuzzleArea(PuzzleArea puzzleArea) {
        this.puzzleArea = puzzleArea;
    }

    public boolean hasPuzzleArea() {
        return puzzleArea != null;
    }

    public void clearPuzzleArea() {
        puzzleArea = null;
    }

    public boolean isCorrectlyPlaced() {
        return hasPuzzleArea() && this.getRow() == puzzleArea.getRow() && this.getCol() == puzzleArea.getCol();
    }

}
