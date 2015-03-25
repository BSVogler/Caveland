package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Explosion;
import com.BombingGames.WurfelEngine.WE;

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
		super(CollectibleType.EXPLOSIVES);
		setFriction(2000);
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
			500,
			WE.getGameplay().getView().getCameras().get(0)
		).spawn(getPosition());
		dispose();
	}

	@Override
	public Collectible clone() throws CloneNotSupportedException {
		return new Flint(this);
	}
}
