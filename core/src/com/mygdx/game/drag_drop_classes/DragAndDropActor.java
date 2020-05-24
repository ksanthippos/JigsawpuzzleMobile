package com.mygdx.game.drag_drop_classes;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.mygdx.game.base_classes.BaseActor;

public class DragAndDropActor extends BaseActor {

    private DragAndDropActor self;
    private DropTargetActor dropTarget;
    private float grabOffsetX;
    private float grabOffsetY;
    private float startPositionX;
    private float startPositionY;
    private boolean draggable;

    public DragAndDropActor(float x, float y, Stage s) {
        super(x, y, s);
        self = this;
        draggable = true;

        addListener(new InputListener() {

           public boolean touchDown(InputEvent event, float offsetX, float offsetY, int pointer, int button) {

               if (!self.isDraggable())
                   return false;

               self.grabOffsetX = offsetX;
               self.grabOffsetY = offsetY;
               self.startPositionX = self.getX();
               self.startPositionY = self.getY();
               self.toFront();
               self.addAction(Actions.scaleTo(1.1f, 1.1f, 0.25f));  // makes item slightly larger during touchdown
               self.onDragStart();

               return true;
           }

           public void touchDragged(InputEvent event, float offsetX, float offsetY, int pointer) {
               float deltaX = offsetX - self.grabOffsetX;
               float deltaY = offsetY - self.grabOffsetY;
               self.moveBy(deltaX, deltaY);
           }

           public void touchUp(InputEvent event, float offsetX, float offsetY, int pointer, int button) {
               self.setDropTarget(null);
               float closestDistance = Float.MAX_VALUE;

               for (BaseActor actor: BaseActor.getList(self.getStage(), DropTargetActor.class.getCanonicalName())) {

                   DropTargetActor target = (DropTargetActor) actor;
                   if (target.isTargetable() && self.overlaps(target)) {
                       float currentDistance = Vector2.dst(self.getX(), self.getY(), target.getX(), target.getY());

                       // check if distance is even closer
                       if (currentDistance < closestDistance) {
                           self.setDropTarget(target);
                           closestDistance = currentDistance;
                       }
                   }
               }

               self.addAction(Actions.scaleTo(1.0f, 1.0f, 0.25f));  // scales item back to normal when touch released
               self.onDrop();
           }
        });
    }

    @Override
    public void act(float dt) {
        super.act(dt);
    }

    public void onDragStart() { }

    public void onDrop() { }

    //************************
    // getters & setters
    private void setDropTarget(DropTargetActor dropTarget) {
        this.dropTarget = dropTarget;
    }

    public DropTargetActor getDropTarget() {
        return dropTarget;
    }

    public boolean hasDropTarget() {
        return (dropTarget != null);
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }
    //************************

    public void moveToActor(BaseActor other) {

        float x = other.getX() + (other.getWidth() - this.getWidth()) / 2;
        float y = other.getY() + (other.getHeight() - this.getHeight()) / 2;
        addAction(Actions.moveTo(x, y, 0.5f, Interpolation.pow3));
    }

    public void moveToStart() {
        addAction(Actions.moveTo(startPositionX, startPositionY, 0.5f, Interpolation.pow3));
    }



}
