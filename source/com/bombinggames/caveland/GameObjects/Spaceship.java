package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.Explosion;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Particle;
import com.bombinggames.wurfelengine.core.Gameobjects.ParticleEmitter;
import com.bombinggames.wurfelengine.core.Gameobjects.ParticleType;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class Spaceship extends MovableEntity {

	private static final long serialVersionUID = 3L;
	/**
	 * true after first explosion
	 */
	private transient boolean crashing = false;
	private boolean crashed = false;
	private transient Coordinate crashCoordinates;//crash if near
	private final transient ArrayList<AbstractEntity> content = new ArrayList<>(2);

	/**
	 *
	 */
	public Spaceship() {
		super((byte) 80, 0);
		setScaling(1);
		setName("Spaceship");
		setMass(1000);
	}

	/**
	 * floating ship
	 * @param coord 
	 */
	public void enableCrash(Coordinate coord){
		crashed = false;
		crashCoordinates = coord;
		setFloating(true);
	}

	public void addContent(AbstractEntity ent) {
		content.add(ent);
		ent.getPosition().setValues(getPosition());
		ent.setHidden(true);
	}

	/**
	 *
	 */
	public void ejectContent() {
		content.forEach(p -> {p.setHidden(false);});
	}

	/**
	 * 
	 * @return true if destroyed
	 */
	public boolean isCrashed() {
		return crashed;
	}

	/**
	 *
	 */
	public void startCrash() {
		new Explosion(3, (byte) 0, null).spawn(getPosition());
		setFloating(false);
		crashing = true;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		
		if (!crashed && content != null){
			content.forEach(c -> {c.getPosition().setValues(getPosition());});
		}
		
		if (!crashed && !crashing && crashCoordinates != null) {
			Vector3 dir = crashCoordinates.cpy().sub(getPosition());
			dir.z = 0;
			setMovement(dir.nor().scl(11));//always fly to startCrash point
		}
		if (crashing == false && crashCoordinates != null && crashCoordinates.distanceToHorizontal(getPosition()) < Block.GAME_EDGELENGTH*25) {
			startCrash();
		}

		//crash on ground
		if (crashing && !crashed && isOnGround()) {
			content.forEach(c -> {
				c.setIndestructible(true);
			});
			setIndestructible(true);
			new Explosion(2, (byte) 100, null).spawn(getPosition());
			setIndestructible(false);
			content.forEach(c -> {
				c.setIndestructible(false);
			});

			ParticleEmitter fireEmitter = (ParticleEmitter) new ParticleEmitter().spawn(getPosition().cpy());
			fireEmitter.setParticleStartMovement(new Vector3(0, 0, 3));
			fireEmitter.setHidden(true);
			Particle particle = new Particle((byte) 22, 4000);
			particle.setType(ParticleType.FIRE);
			fireEmitter.setPrototype(particle);
			fireEmitter.getPosition().setValues(getPosition()).add(0, 0, 0);
			ejectContent();
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
