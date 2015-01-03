package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author Benedikt Vogler
 */
public class Dust extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	private final float maxtime;
	
	private float timeTillDeath;
	private final Vector3 direction;

	/**
	 * 
	 * @param maxtime
	 * @param direction in m/s
	 */
	public Dust(float maxtime, Vector3 direction) {
		super(22);
		this.maxtime = maxtime;
		this.direction = direction;
		timeTillDeath=maxtime;
		setTransparent(true);
		setSaveToDisk(false);
	}

	@Override
	public void update(float dt) {
		timeTillDeath-=dt;
		//spread on floor
		if (direction.z <0 && isOnGround()){
			direction.x *= 2;
			direction.y *= 2;
			direction.z = 0;
		}
		getPosition().addVector(direction.cpy().scl(dt/1000f));
		setRotation(getRotation()-dt/40f);
		getColor().a = timeTillDeath/maxtime;
		if (timeTillDeath <= 0) dispose();
	}
	
	@Override
	public void render(GameView view, Camera camera, Color color) {
        render(
            view,
            camera,
            color,
            1f
        );
    }

	
}
