package com.bombinggames.caveland.gameobjects.collectibles;

import com.badlogic.gdx.ai.msg.Telegram;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.gameobjects.Explosion;

/**
 *
 * @author Benedikt Vogler
 */
public class Flint extends Collectible {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public Flint() {
		super(CollectibleType.Explosives);
		setFriction(2000);
		setName("Flint");
	}

	/**
	 *
	 * @param flint
	 */
	public Flint(Flint flint) {
		super(flint);
		setFriction(2000);
	}

	@Override
	public Collectible clone() throws CloneNotSupportedException {
		return new Flint(this);
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		super.handleMessage(msg);
		
		if (msg.sender == this || msg.message == Events.collided.getId()){
			new Explosion(
				1,
				(byte) 50,
				WE.getGameplay().getView().getCameras().get(0)
			).spawn(getPosition());
			dispose();
		}
		
		return false;
	}
	
	
}
