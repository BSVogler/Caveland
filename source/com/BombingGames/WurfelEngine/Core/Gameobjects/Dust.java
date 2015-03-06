package com.BombingGames.WurfelEngine.Core.Gameobjects;

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
	 * @param color
	 */
	public Dust(float maxtime, Vector3 direction, Color color) {
		super(22);
		this.maxtime = maxtime;
		this.direction = direction;
		timeTillDeath=maxtime;
		setColor(color);
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
		Vector3 step = direction.cpy().scl(dt/1000f);
		getPosition().addVector(step);
		if (getPosition().getBlock().isObstacle())
			getPosition().addVector(step.scl(-1));
			
		setRotation(getRotation()-dt/10f);
		setScaling(getScaling()+dt/300f);
		getColor().a = timeTillDeath/maxtime;
		if (timeTillDeath <= 0) dispose();
	}
}
