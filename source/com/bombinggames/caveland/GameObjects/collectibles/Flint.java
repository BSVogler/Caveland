package com.bombinggames.caveland.GameObjects.collectibles;

import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.Explosion;

/**
 *
 * @author Benedikt Vogler
 */
public class Flint extends Collectible{
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
	public void onCollide(){
		new Explosion(
			1,
			(byte)50,
			WE.getGameplay().getView().getCameras().get(0)
		).spawn(getPosition());
		dispose();
	}

	@Override
	public Collectible clone() throws CloneNotSupportedException {
		return new Flint(this);
	}
}
