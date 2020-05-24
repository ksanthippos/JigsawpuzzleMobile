package com.mygdx.game.base_classes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class BaseActor extends Group {

    private Animation<TextureRegion> animation;
    private float elapsedTime;
    private boolean animationPaused;

    private Vector2 velocityVec;
    private Vector2 accelerationVec;
    private float acceleration;
    private float deceleration;
    private float maxSpeed;

    private Polygon boundaryPolygon;

    private static Rectangle worldBounds;

    public BaseActor(float x, float y, Stage s) {
        super();
        setPosition(x, y);
        s.addActor(this);

        animation = null;
        elapsedTime = 0;
        animationPaused = false;

        velocityVec = new Vector2(0, 0);
        accelerationVec = new Vector2(0,0);
        acceleration = 0;
        deceleration = 0;
        maxSpeed = 1000;
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        if (!animationPaused)
            elapsedTime += dt;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);

        if (animation != null && isVisible()) {
            batch.draw(animation.getKeyFrame(elapsedTime), getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
        super.draw(batch, parentAlpha);
    }

    // ANIMATION
    // ***********************
    // loading from separate img files
    public Animation<TextureRegion> loadAnimationFromFiles(String[] filenames, float frameDuration, boolean loop) {
        int fileCount = filenames.length;
        Array<TextureRegion> textureArray = new Array<>();  // ei luokkanotatiota lopussa?

        // adds single images to an array for animation
        for (int i = 0; i < fileCount; i++) {
            String fileName = filenames[i];
            Texture texture = new Texture(Gdx.files.internal(fileName));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);  // WHATS THIS?
            textureArray.add(new TextureRegion(texture));
        }

        // create new animation from array
        Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);

        // check if animation is looped
        if (loop)
            anim.setPlayMode(Animation.PlayMode.LOOP);
        else
            anim.setPlayMode(Animation.PlayMode.NORMAL);

        if (this.animation == null)
            setAnimation(anim);

        return anim;
    }

    // loading from spritesheet
    public Animation<TextureRegion> loadAnimationFromSheet(String fileName, int rows, int cols, float frameDuration, boolean loop) {
        Texture texture = new Texture(Gdx.files.internal(fileName), true);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        int frameWidth = texture.getWidth() / cols;
        int frameHeight = texture.getHeight() / rows;

        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);
        Array<TextureRegion> textureArray = new Array<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                textureArray.add(temp[i][j]);
            }
        }

        // create new animation from array (identical part with above)
        Animation<TextureRegion> anim = new Animation<TextureRegion>(frameDuration, textureArray);

        // check if animation is looped
        if (loop)
            anim.setPlayMode(Animation.PlayMode.LOOP);
        else
            anim.setPlayMode(Animation.PlayMode.NORMAL);

        if (this.animation == null)
            setAnimation(anim);

        return anim;
    }

    // loading single textures is also handled via animation method for consistency
    public Animation<TextureRegion> loadTexture(String fileName) {
        String[] fileNames = new String[1];
        fileNames[0] = fileName;
        return loadAnimationFromFiles(fileNames, 1, true);
    }

    // check if animation is finished
    public boolean isAnimationFinished() {
        return animation.isAnimationFinished(elapsedTime);
    }

    // animation setters
    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
        TextureRegion tr = animation.getKeyFrame(0);
        float w = tr.getRegionWidth();
        float h = tr.getRegionHeight();
        setSize(w, h);
        setOrigin(w/2, h/2);

        if (boundaryPolygon == null)    // set boundaries
            setBoundaryRectangle();
    }

    public void setAnimationPaused(boolean value) {
        this.animationPaused = value;
    }
    // ***********************

    // PHYSICS
    // speed
    // ***********************
    public void setSpeed(float speed) {
        if (velocityVec.len() == 0) // vector length = 0 --> speed = 0
            velocityVec.set(speed, 0);
        else
            velocityVec.setLength(speed);
    }

    public float getSpeed() {
        return velocityVec.len();
    }

    public void setMotionAngle(float angle) {
        velocityVec.setAngle(angle);
    }

    public float getMotionAngle() {
        return velocityVec.angle();
    }

    public boolean isMoving() {
        return (getSpeed() > 0);
    }

    // acceleration
    public void setAcceleration(float acceleration) {
        this.acceleration = acceleration;
    }

    public void accelerateAtAngle(float angle) {
        accelerationVec.add(new Vector2(acceleration, 0).setAngle(angle));
    }

    public void accelerateForward() {
        accelerateAtAngle(getRotation());
    }

    public void setDeceleration(float deceleration) {
        this.deceleration = deceleration;
    }

    public void setMaxSpeed(float speed) {
        this.maxSpeed = speed;
    }

    // apply all above
    public void applyPhysics(float dt) {
        velocityVec.add(accelerationVec.x * dt, accelerationVec.y * dt);        // v = a*t
        float speed = getSpeed();

        // if not accelerating --> decelerate (decrease speed)
        if (accelerationVec.len() == 0)
            speed -= deceleration * dt;

        // keeps speed within bounds, set speed and move actor to new coordinates
        speed = MathUtils.clamp(speed, 0, maxSpeed);
        setSpeed(speed);
        moveBy(velocityVec.x * dt, velocityVec.y * dt);     // s = v*t

        // reset acceleration to 0
        accelerationVec.set(0, 0);
    }
    // ***********************

    // BOUNDARIES & COLLISION
    // ***********************
    public void setBoundaryRectangle() {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        boundaryPolygon = new Polygon(vertices);
    }

    public void setBoundaryPolygon(int numSides) {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = new float[2 * numSides];

        for (int i = 0; i < numSides; i++) {
            float angle = i * 6.28f / numSides;
            // x-coord
            vertices[2*i] = w/2 * MathUtils.cos(angle) + w/2;
            // y-coord
            vertices[2*i + 1] = h/2 * MathUtils.sin(angle) + h/2;
        }

        boundaryPolygon = new Polygon(vertices);
    }

    public Polygon getBoundaryPolygon() {
        boundaryPolygon.setPosition(getX(), getY());
        boundaryPolygon.setOrigin(getOriginX(), getOriginY());
        boundaryPolygon.setRotation(getRotation());
        boundaryPolygon.setScale(getScaleX(), getScaleY());

        return boundaryPolygon;
    }

    // check polygon collisision
    public boolean overlaps(BaseActor other) {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return false;

        return Intersector.overlapConvexPolygons(poly1, poly2);
    }

    // prevent overlapping (rock collisions) --> returns null, if not collided
    public Vector2 preventOverlap(BaseActor other) {
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return null;

        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
        boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

        if (!polygonOverlap)
            return null;

        // in case of collision, move actor "smoothly" along the colliding objects' edges
        this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);

        return mtv.normal;
    }

    // ***********************

    // POSITION, CAMERA & BOUNDS
    // ***********************
    public void centerAtPosition(float x, float y) {
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    public void centerAtActor(BaseActor other) {
        centerAtPosition(other.getX() + other.getWidth() / 2, other.getY() + other.getHeight() / 2);
    }

    public void setOpacity(float opacity) {
        this.getColor().a = opacity;
    }

    public void alignCamera() {
        Camera cam = this.getStage().getCamera();
        Viewport view = this.getStage().getViewport();

        // center camera on actor
        cam.position.set(this.getX() + this.getOriginX(), this.getY() + this.getOriginY(), 0);

        // bound camera to layout
        cam.position.x = MathUtils.clamp(cam.position.x, cam.viewportWidth / 2, worldBounds.width - cam.viewportWidth / 2);
        cam.position.y = MathUtils.clamp(cam.position.y, cam.viewportHeight / 2, worldBounds.height - cam.viewportHeight / 2);
        cam.update();
    }

    // world bounds check
    public void boundToWorld() {
        if (getX() < 0) // left
            setX(0);
        if (getX() + getWidth() > worldBounds.width) // right
            setX(worldBounds.width - getWidth());
        if (getY() < 0) // bottom
            setY(0);
        if (getY() + getHeight() > worldBounds.height)  // top
            setY(worldBounds.height - getHeight());
    }

    public void wrapAroundWorld() {
        if (getX() + getWidth() < 0)
            setX(worldBounds.width);
        if (getX() > worldBounds.width)
            setX(-getWidth());
        if (getY() + getHeight() < 0)
            setY(worldBounds.height);
        if (getY() > worldBounds.height)
            setY(-getHeight());
    }

    // proximity check
    public boolean isWithinDistance(float distance, BaseActor other) {

        Polygon poly1 = this.getBoundaryPolygon();
        float scaleX = (this.getWidth() + 2 * distance) / this.getWidth();
        float scaleY = (this.getHeight() + 2 * distance) / this.getHeight();
        poly1.setScale(scaleX, scaleY);

        Polygon poly2 = other.getBoundaryPolygon();

        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return false;

        return Intersector.overlapConvexPolygons(poly1, poly2);
    }


    // these are static methods
    public static void setWorldBounds(float width, float height) {
        worldBounds = new Rectangle(0, 0, width, height);
    }

    public static void setWorldBounds(BaseActor actor) {
        setWorldBounds(actor.getWidth(), actor.getHeight());
    }

    public static Rectangle getWorldBounds() { return worldBounds; }


    // ------------------------------
    // IMPORTANT!! these two methods have to be called: BaseActor.getList(mainStage, CLASSNAME_HERE.class.getCanonicalName())
    public static ArrayList<BaseActor> getList(Stage stage, String className) {

        ArrayList<BaseActor> list = new ArrayList<>();
        Class theClass = null;
        try {
            theClass = Class.forName(className);
        }
        catch (Exception error) {
            error.printStackTrace();
        }

        for (Actor a: stage.getActors()) {
            if (theClass.isInstance(a)) {
                list.add((BaseActor) a);
            }
        }

        return list;
    }

    public static int count(Stage stage, String className) {
        return getList(stage, className).size();
    }
    // ------------------------------


    // ***********************










}
