package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.Explosion;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class Spaceship extends MovableEntity {

	private static final long serialVersionUID = 3L;
	private transient boolean crashing = false;
	private transient boolean crashed = true;
	private transient Coordinate crashCoordinates;//crash after 5 seconds
	private transient AbstractEntity passenger;
	private transient SuperGlue passengerGlue;

	/**
	 *
	 */
	public Spaceship() {
		super((byte) 80, 0);
		crashed = true;
	}
	
	/**
	 * 
	 * @param coord 
	 */
	public void enableCrash(Coordinate coord){
		crashed = false;
		crashCoordinates = coord;
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
			passenger = null;
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
		if (crashing == false && crashCoordinates != null && crashCoordinates.distanceToHorizontal(getPosition()) < Block.GAME_EDGELENGTH*10) {
			crash();
		}

		//crash on ground
		if (crashing && !crashed && isOnGround()) {
			passenger.setIndestructible(true);
			setIndestructible(true);
			new Explosion(2, (byte) 100, null).spawn(getPosition());
			setIndestructible(false);
			SmokeEmitter fireEmitter = (SmokeEmitter) new SmokeEmitter().spawn(getPosition().cpy());
			fireEmitter.setActive(true);
			fireEmitter.setHidden(true);
			passenger.setIndestructible(false);
			ejectPassenger();
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
