/**
 * This class is public domain.
 */
package com.bombinggames.wurfelengine.core.loading;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * @author Mats Svensson
 */
public class LoadingBar extends Actor {

    private final Animation<TextureRegion> animation;
    private TextureRegion reg;
    private float stateTime;

    /**
     *
     * @param animation
     */
    public LoadingBar(Animation<TextureRegion> animation) {
        this.animation = animation;
        reg = animation.getKeyFrame(0);
    }

    @Override
    public void act(float dt) {
        stateTime += dt;
        reg = animation.getKeyFrame(stateTime);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(reg, getX(), getY());
    }
}
