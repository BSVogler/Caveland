package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author Benedikt Vogler
 */
public class Vanya extends MovableEntity {
	private static final long serialVersionUID = 1L;

	public Vanya() {
		super(40, 0);
		setFloating(false);
	}

	@Override
	public void update(float dt) {
		float beforeUpdate = getMovementDirection().z;
		super.update(dt);
		
		//höchster Punkt erreicht
		if (beforeUpdate>0 && getMovementDirection().z<0)
			new BlümchenKacke().spawn(getPosition().cpy());
		
		if (getPosition().isInMemoryHorizontal() && isOnGround()) jump();
	}
	
	@Override
	public void jump() {
		setMovementDir(new Vector3((float) Math.random()-0.5f, (float) Math.random()-0.5f, getMovementDirection().z));
		super.jump(6, true);
	}
	
	private class BlümchenKacke extends MovableEntity {
		private static final long serialVersionUID = 1L;

		BlümchenKacke() {
			super(41, 0);
			setMovementDir(new Vector3(0,0,-1));
			setFloating(false);
		}

	}
}

