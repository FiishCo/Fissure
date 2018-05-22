package com.gmail.studios.co.fiish.fissure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class Miner extends Actor {
    public Viewport mViewport;
    public boolean isAlive;
    public float mTileX, mTileY, mTileWidth, mTileHeight;
    public boolean isDeathDone;

    private float mElapsedTime, mDeadTime, mPixelsPerTileX, mPixelsPerTileY;
    private TextureAtlas mRunningAtlas;
    private Animation<TextureRegion> mRunningAnimation;
    private TextureAtlas mFallingAtlas;
    private Animation<TextureRegion> mFallingAnimation;

    public Miner(Viewport viewport) {
        this.mViewport = viewport;
        mRunningAtlas = new TextureAtlas(Gdx.files.internal("spritesheets/minersheet.atlas"));
        mRunningAnimation = new Animation(1f/10f, mRunningAtlas.getRegions());
        mFallingAtlas = new TextureAtlas((Gdx.files.internal("spritesheets/fallingsheet.atlas")));
        mFallingAnimation = new Animation(1f/10f, mFallingAtlas.getRegions());

        mElapsedTime = 0.0f;
    }

    public void init() {
        this.setWidth(mViewport.getScreenWidth() / 16f * 20 / 32 * 0.85f);
        this.setHeight(mViewport.getScreenHeight() / 9f * 0.85f);

        this.setX(mViewport.getScreenWidth() / 2 - getWidth() / 2);
        this.setY(mViewport.getScreenHeight() / 2 - getHeight() / 2);

        this.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        this.setTouchable(Touchable.disabled);

        isAlive = true;
        mPixelsPerTileX = mViewport.getScreenWidth() / 16.0f;
        mPixelsPerTileY = mViewport.getScreenHeight() / 9.0f;

        mTileWidth = getWidth() / mPixelsPerTileX;
        mTileHeight = getHeight() / mPixelsPerTileY;

        isDeathDone = false;
        isAlive = true;

        mElapsedTime = 0.0f;
        mDeadTime = 0.0f;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (getX() < 0) {
            clearActions();
            this.setX(0.0f);
        }
        if (getX() + getWidth() > mViewport.getScreenWidth()) {
            clearActions();
            this.setX(mViewport.getScreenWidth() - getWidth());
        }
        if (getY() < 0) {
            clearActions();
            this.setY(0.0f);
        }
        if (getY() + getHeight() > mViewport.getScreenHeight()) {
            clearActions();
            this.setY(mViewport.getScreenHeight() - getHeight());
        }

        mElapsedTime += delta;

        if (!isAlive) mDeadTime += delta;

        if (isAlive) {
            mTileX = getX() / mPixelsPerTileX;
            mTileY = getY() / mPixelsPerTileY;
        }
    }

    @Override
    public void draw(Batch batch, float alpha) {
       if (isAlive) {
            batch.draw(mRunningAnimation.getKeyFrame(mElapsedTime, true),
                    this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

       if(!isAlive) {
            batch.draw(mFallingAnimation.getKeyFrame(mDeadTime, false),
                    MathUtils.floor(mTileX) * mPixelsPerTileX, MathUtils.floor(mTileY) * mPixelsPerTileY, mViewport.getScreenWidth() / 16f,
                    mViewport.getScreenHeight() / 9f);

            if (mFallingAnimation.isAnimationFinished(mDeadTime)) {
                isDeathDone = true;
            }
       }
    }

    public void dispose() {
        mRunningAtlas.dispose();
        mFallingAtlas.dispose();
    }

    public void checkSafe(Array<Tile> tiles) {
        int id1, id2, id3, id4;

        id1 = MathUtils.floor(mTileY) * 16 + MathUtils.floor(mTileX);
        id2 = MathUtils.floor(mTileY + mTileHeight) * 16 + MathUtils.floor(mTileX);
        id3 = MathUtils.floor(mTileY) * 16 + MathUtils.floor(mTileX + mTileWidth);
        id4 = MathUtils.floor(mTileY + mTileHeight) * 16 + MathUtils.floor(mTileX + mTileWidth);
        if (id1 >= 0 && id1 <=143 && tiles.get(id1).isFissure) {
            kill();
        } else if (id2 >= 0 && id2 <=143 && tiles.get(id2).isFissure) {
            mTileY = mTileY + mTileHeight;
            kill();
        }else if (id3 >= 0 && id3 <=143 && tiles.get(id3).isFissure) {
            mTileX = mTileX + mTileWidth;
            kill();
        } else if (id4 >= 0 && id4 <=143 && tiles.get(id4).isFissure) {
            mTileX = mTileX + mTileWidth;
            mTileY = mTileY + mTileHeight;
            kill();
        }
        /* Gdx.app.log("x, y:            ", "" + getX() + ", " + getY());
        Gdx.app.log("mPixelsPerTile:  ", "" + mPixelsPerTileX);
        Gdx.app.log("Width:           ", "" + getWidth());
        Gdx.app.log("Height:          ", "" + getHeight());
        Gdx.app.log("mTileWidth:      ", "" + mTileWidth);
        Gdx.app.log("mTileHeight:     ", "" + mTileHeight);
        Gdx.app.log("mTileX, mTileY:  ", "" + mTileX + ", " + mTileY);
        Gdx.app.log("Bottom left:     ", "" + id1);
        Gdx.app.log("Top left:        ", "" + id2);
        Gdx.app.log("Bottom right:    ", "" + id3);
        Gdx.app.log("Top right:       ", "" + id4); */
    }


    public void kill() {
        clearActions();
        this.isAlive = false;
    }
}
