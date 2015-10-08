package com.bombinggames.caveland.GameObjects.collectibles;

import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Explosion;

/**
 *
 * @author Benedikt Vogler
 */
public class TFlint extends Collectible implements Interactable {
	private static final long serialVersionUID = 2L;
	private static final float TIMETILLEXPLOSION = 2000;
	private float timer = TIMETILLEXPLOSION;
	private boolean lit;

	/**
	 *
	 */
	public TFlint() {
		super(CollectibleType.Explosives);
		setFriction(0.02f);
	}

	/**
	 * copy constructor
	 * @param collectible 
	 */
	public TFlint(TFlint collectible) {
		super(collectible);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (!shouldBeDisposed()) {
			if (lit)
				timer-=dt;
			if (timer <= 0) {
				new Explosion(
					2,
					(byte) 100,
					WE.getGameplay().getView().getCameras().get(0)
				).spawn(getPosition());
				dispose();
			}
		}
	}
	
	@Override
	public Collectible clone() throws CloneNotSupportedException {
		return new TFlint(this);
	}

	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
		WE.SOUND.play("hiss", getPosition());
		setValue((byte) 8);//ignite sprite
		lit = true;
		timer = TIMETILLEXPLOSION;
	}

	@Override
	public boolean interactable() {
		return true;
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return true;
	}
	
	
}
