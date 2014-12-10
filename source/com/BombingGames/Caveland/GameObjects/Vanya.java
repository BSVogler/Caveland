package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Map.Point;
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
		float beforeUpdate = getMovement().z;
		super.update(dt);
		
		//höchster Punkt erreicht
		if (beforeUpdate>0 && getMovement().z<0)
			new BlümchenKacke().spawn(getPosition().cpy());
		
		if (getPosition().isInMemoryHorizontal() && isOnGround()) jump();
	}
	
	@Override
	public void jump() {
		setMovement(new Vector3((float) Math.random()-0.5f, (float) Math.random()-0.5f, getMovement().z));
		super.jump(6);
	}
	
	private class BlümchenKacke extends MovableEntity {
		private static final long serialVersionUID = 1L;

		public BlümchenKacke() {
			super(41, 0);
			setMovement(new Vector3(0,0,-1));
			setFloating(false);
		}

		@Override
		public MovableEntity spawn(Point p) {
			return super.spawn(p);
		}
		
		
	}
}

