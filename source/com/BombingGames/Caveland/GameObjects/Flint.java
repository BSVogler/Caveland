package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Explosion;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.WE;

/**
 *
 * @author Benedikt Vogler
 */
public class Flint extends MovableEntity{

	public Flint() {
		super(43, 0);
		setFriction(2000);
		setCollectable(true);
	}
	
	public Flint(Flint flint) {
		super(flint);
		setFriction(2000);
	}
	
	

	@Override
	public void onCollide(){
		new Explosion(
			1,
			WE.getGameplay().getView().getCameras().get(0)
		).spawn(getPosition());
		dispose();
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return new Flint(this);
	}
}
