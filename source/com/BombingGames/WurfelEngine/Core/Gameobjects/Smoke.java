package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Benedikt Vogler
 */
public class Smoke extends MovableEntity {
	private static final long serialVersionUID = 1L;
	private final float maxtime;
	
	private float timeTillDeath;
	private Color startingColor;

	public Smoke() {
		this(2000f, new Color(0.5f, 0.5f, 0.5f, 1));
	}
	
	/**
	 * 
	 * @param maxtime in ms
	 * @param color
	 */
	public Smoke(float maxtime, Color color) {
		super((byte)22,0);
		this.maxtime = maxtime;
		timeTillDeath=maxtime;
		setColor(color);
		startingColor =color.cpy();
		setTransparent(true);
		setSaveToDisk(false);
		setScaling(-1);
		disableShadow();
		setFloating(true);
		setName("Smoke Particle");
	}

	@Override
	public void setColor(Color color) {
		super.setColor(color);
		startingColor =color.cpy();
	}
	
	
	@Override
	public void update(float dt) {
		super.update(dt);
		timeTillDeath-=dt;
//		//spread on floor
//		if (direction.z <0 && isOnGround()){
//			direction.x *= 2;
//			direction.y *= 2;
//			direction.z = 0;
//		}
//		Vector3 step = direction.cpy().scl(dt/1000f);
//		getPosition().addVector(step);
//		CoreData block = getPosition().getBlock();
//		if (block!=null && block.isObstacle())
//			getPosition().addVector(step.scl(-1));//reverse step
			
		setRotation(getRotation()-dt/10f);
		setScaling(getScaling()+dt/300f);
		getColor().a = timeTillDeath/maxtime;
		getColor().r = startingColor.r*((timeTillDeath*2)/maxtime);
		getColor().g = startingColor.g*((timeTillDeath)/maxtime);
		getColor().b = startingColor.b*((timeTillDeath)/maxtime);
		if (timeTillDeath <= 0) dispose();
	}
}
