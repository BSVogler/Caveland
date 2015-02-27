package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
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
		setScaling(-1);
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
		setRotation(getRotation()-dt/10f);
		setScaling(getScaling()+dt/300f);
		getColor().a = timeTillDeath/maxtime;
		if (timeTillDeath <= 0) dispose();
	}
}
