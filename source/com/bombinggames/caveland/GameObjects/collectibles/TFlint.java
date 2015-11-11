package com.bombinggames.caveland.GameObjects.collectibles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.Explosion;
import com.bombinggames.wurfelengine.core.Gameobjects.Particle;
import com.bombinggames.wurfelengine.core.Gameobjects.ParticleEmitter;
import com.bombinggames.wurfelengine.core.Gameobjects.ParticleType;
import com.bombinggames.wurfelengine.core.Map.Point;

/**
 *
 * @author Benedikt Vogler
 */
public class TFlint extends Collectible implements Interactable {
	private static final long serialVersionUID = 2L;
	private static final float TIMETILLEXPLOSION = 2000;
	private float timer = TIMETILLEXPLOSION;
	private boolean lit;
	private ParticleEmitter sparksGenerator;

	/**
	 *
	 */
	public TFlint() {
		super(CollectibleType.Explosives);
		setFriction(0.02f);
		sparksGenerator = new ParticleEmitter();
		sparksGenerator.setHidden(true);
		sparksGenerator.setParticleStartMovement(new Vector3(0, 0, 0));
		sparksGenerator.setParticleSpread(new Vector3(0.4f, 0.4f, 0.08f));
		Particle sparkle = new Particle();
		sparkle.setTTL(400);
		sparkle.setType(ParticleType.FIRE);
		sparkle.setScaling(-0.5f);
		sparkle.setColor(new Color(0.9f,0.8f, 0.5f,1f));
		sparksGenerator.setPrototype(sparkle);
	}

	/**
	 * copy constructor
	 * @param collectible 
	 */
	public TFlint(TFlint collectible) {
		super(collectible);
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		sparksGenerator.spawn(point);
		return this;
	}
	
	

	@Override
	public void update(float dt) {
		super.update(dt);
		
		if (!shouldBeDisposed()) {
			sparksGenerator.getPosition().setValues(getPosition()).addVector(0, 0, Block.GAME_EDGELENGTH*0.8f);
			sparksGenerator.setActive(lit);
			if (lit) {
				timer-=dt;
			}
			if (timer <= 0) {
				new Explosion(
					3,
					(byte) 50,
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
	public void interact(CLGameView view, AbstractEntity actor) {
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

	@Override
	public void dispose() {
		super.dispose();
		sparksGenerator.dispose();
	}
	
}
