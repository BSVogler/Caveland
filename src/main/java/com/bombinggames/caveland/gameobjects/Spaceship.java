package com.bombinggames.caveland.gameobjects;

import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.cvar.CVarSystemSave;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Explosion;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Particle;
import com.bombinggames.wurfelengine.core.gameobjects.ParticleEmitter;
import com.bombinggames.wurfelengine.core.gameobjects.ParticleType;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
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
		setScaling(2);
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

	/**
	 *
	 * @param ent
	 */
	public void addContent(AbstractEntity ent) {
		content.add(ent);
		ent.getPosition().set(getPosition());
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
			content.forEach(c -> {c.getPosition().set(getPosition());});
		}
		
		if (!crashed && !crashing && crashCoordinates != null) {
			Vector3 dir = crashCoordinates.toPoint().sub(getPosition());//vector from ship to crashsite
			dir.z = 0;
			setMovement(dir.nor().scl(11));//always fly to startCrash point
		}
		if (crashing == false && crashCoordinates != null && crashCoordinates.distanceToHorizontal(getPosition()) < RenderCell.GAME_EDGELENGTH*25) {
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
			fireEmitter.getPosition().set(getPosition()).add(0, 0, 0);
			ejectContent();
			crashed = true;
			//save that already crashed
			CVarSystemSave saveCvars = Controller.getMap().getCVars().getSaveCVars();
			saveCvars.get("IntroCutsceneCompleted").setValue(true);
		}
	}
}
