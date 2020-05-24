package com.mygdx.game.drag_drop_classes;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.base_classes.BaseActor;

public class DropTargetActor extends BaseActor {

    private boolean targetable;

    public DropTargetActor(float x, float y, Stage s) {
        super(x, y, s);
        targetable = true;
    }

    public boolean isTargetable() {
        return targetable;
    }

    public void setTargetable(boolean targetable) {
        this.targetable = targetable;
    }
}
