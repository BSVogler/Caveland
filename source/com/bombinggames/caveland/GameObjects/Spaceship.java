package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Explosion;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;

/**
 *
 * @author Benedikt Vogler
 */
public class Spaceship extends MovableEntity {

	private static final long serialVersionUID = 2L;
	private boolean crashing = false;
	private boolean crashed = false;
	private float timeTillCrash = 5000;//crash after 5 seconds
	private AbstractEntity passenger;
	private SuperGlue passengerGlue;

	/**
	 *
	 * @param introcompleted
	 */
	public Spaceship(boolean introcompleted) {
		super((byte) 80, 0);
		if (introcompleted) {
			crashed = true;
		}
	}

	public void setPassenger(AbstractEntity ent) {
		if (passenger == null) {
			passenger = ent;
			passengerGlue = (SuperGlue) new SuperGlue(this, ent).spawn(getPosition());
			passenger.setPosition(getPosition().cpy());
			passenger.setHidden(true);
		}
	}

	/**
	 *
	 */
	public void ejectPassenger() {
		if (passenger != null) {
			passenger.setHidden(false);
			passengerGlue.dispose();
		}
	}

	/**
	 *
	 * @return
	 */
	public boolean isCrashed() {
		return crashed;
	}

	/**
	 *
	 */
	public void crash() {
		new Explosion(3, (byte) 0, null).spawn(getPosition());
		setFloating(false);
		crashing = true;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (crashing == false) {
			timeTillCrash -= dt;

			if (timeTillCrash <= 0) {
				crash();
			}
		}

		//crash on ground
		if (crashing && !crashed && isOnGround()) {
			ejectPassenger();
			passenger.setIndestructible(true);
			setIndestructible(true);
			new Explosion(2, (byte) 100, null).spawn(getPosition());
			setIndestructible(false);
			SmokeEmitter fireEmitter = (SmokeEmitter) new SmokeEmitter().spawn(getPosition().cpy());
			fireEmitter.setActive(true);
			fireEmitter.setHidden(true);
			passenger.setIndestructible(false);
			crashed = true;
			//save that already crashed
			WE.CVARS.getChildSystem().getChildSystem().get("IntroCutsceneCompleted").setValue(true);
		}
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
