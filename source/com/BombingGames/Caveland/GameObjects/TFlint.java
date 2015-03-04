package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Explosion;
import com.BombingGames.WurfelEngine.WE;

/**
 *
 * @author Benedikt Vogler
 */
public class TFlint extends Collectible {
	private static final long serialVersionUID = 2L;
	private static final float TIMETILLEXPLOSION = 1000;
	private float timer = TIMETILLEXPLOSION;
	private boolean lit;

	public TFlint(CollectibleType def) {
		super(def);
		setFriction(0.02f);
	}

	public TFlint(TFlint collectible) {
		super(collectible);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (lit)
			timer-=dt;
		if (timer <= 0) {
			new Explosion(
				1,
				500,
				WE.getGameplay().getView().getCameras().get(0)
			).spawn(getPosition());
			dispose();
		}
	}
	
	@Override
	public Collectible clone() throws CloneNotSupportedException {
		return new TFlint(this);
	}

	@Override
	public void action() {
		super.action();
		Controller.getSoundEngine().play("hiss", getPosition());
		lit = true;
		timer = TIMETILLEXPLOSION;
	}
}
