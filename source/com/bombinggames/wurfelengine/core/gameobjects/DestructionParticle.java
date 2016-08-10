package com.bombinggames.wurfelengine.core.gameobjects;

import com.badlogic.gdx.math.Vector3;

/**
 * A piece of dirt which flies around.
 * @author Benedikt Vogler
 */
public class DestructionParticle extends MovableEntity {
	private static final long serialVersionUID = 1L;
	private float timeofExistance;
	private final float rotateEachNMeters = 0.06f;
	/**
	 * in meters
	 */
	private float modMoved =0;

	/**
	 *
	 * @param id
	 */
	public DestructionParticle(byte id) {
		super(id,(byte) 0, false);
		setSavePersistent(false);
		addMovement(new Vector3((float) Math.random()-0.5f, (float) Math.random()-0.5f,(float) Math.random()*5f));
		setRotation((float) Math.random()*360);
		setMass(1.5f);
		setName("Destruction Particle");
	}


	@Override
	public void update(float dt) {
		super.update(dt);
		timeofExistance+=dt;
		modMoved += getSpeed()*dt/1000f;
		if (modMoved  > rotateEachNMeters) {//if over step size then rotate
			setSpriteValue((byte) (int) (Math.random()*3));
			modMoved = modMoved % rotateEachNMeters;
		}
		
		if (timeofExistance>2000) dispose();
		
	}
	
}
