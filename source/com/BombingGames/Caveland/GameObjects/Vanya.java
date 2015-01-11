package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;
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
		setJumpingSound((Sound) WE.getAsset("com/BombingGames/Caveland/sounds/vanya_jump.wav"));
	}

	@Override
	public void update(float dt) {
		float beforeUpdate = getMovement().z;
		super.update(dt);
		
		//höchster Punkt erreicht
		//if (beforeUpdate>0 && getMovement().z<0)
			//new BlümchenKacke().spawn(getPosition().cpy());
		
		if (getPosition().isInMemoryHorizontal() && isOnGround()) jump();
	}
	
	@Override
	public void jump() {
		super.jump(6, true);
	}
	
	private class BlümchenKacke extends MovableEntity {
		private static final long serialVersionUID = 1L;

		BlümchenKacke() {
			super(41, 0);
			setMovement(new Vector3(0,0,-1));
			setFloating(false);
		}

	}
}

