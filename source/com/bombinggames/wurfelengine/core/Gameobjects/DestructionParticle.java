package com.bombinggames.wurfelengine.core.Gameobjects;

import com.badlogic.gdx.math.Vector3;

/**
 * A piece of dirt which flies around.
 * @author Benedikt Vogler
 */
public class DestructionParticle extends MovableEntity {
	private static final long serialVersionUID = 1L;
	private float timeofExistance;

	/**
	 *
	 * @param id
	 */
	public DestructionParticle(byte id) {
		super(id,(byte) 0);
		setSaveToDisk(false);
		addMovement(new Vector3((float) Math.random()-0.5f, (float) Math.random()-0.5f,(float) Math.random()*5f));
		setRotation((float) Math.random()*360);
	}


	@Override
	public void update(float dt) {
		super.update(dt);
		timeofExistance+=dt;
		if (timeofExistance % 500 > 250) {
			setValue((byte) (int) (Math.random()*3));
		}

		if (timeofExistance>2000) dispose();
		
	}
	
}
