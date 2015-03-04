package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Explosion;
import com.BombingGames.WurfelEngine.WE;

/**
 *
 * @author Benedikt Vogler
 */
public class TFlint extends Collectible {
	private static final long serialVersionUID = 1L;

	public TFlint(CollectibleType def) {
		super(def);
		setFriction(0.02f);
	}

	public TFlint(TFlint collectible) {
		super(collectible);
	}

	@Override
	public void onCollide() {
		new Explosion(
			1,
			500,
			WE.getGameplay().getView().getCameras().get(0)
		).spawn(getPosition());
		dispose();
	}
	
	

	@Override
	public Collectible clone() throws CloneNotSupportedException {
		return new TFlint(this);
	}
	
	
	
}
