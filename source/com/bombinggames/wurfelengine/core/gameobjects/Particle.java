package com.bombinggames.wurfelengine.core.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Pool;

/**
 *
 * @author Benedikt Vogler
 */
public class Particle extends MovableEntity {

	private static final long serialVersionUID = 2L;

	/**
	 * the TTL at the start
	 */
	private float maxtime;
	/**
	 * if this reaches zero it is destroyed
	 */
	private float timeTillDeath;
	private final Color startingColor = new Color(1, 1, 1, 0.5f);
	private float startingAlpha = 1f;
	private ParticleType type = ParticleType.REGULAR;
	private boolean rotateRight;
	private Pool<Particle> pool;

	/**
	 * With TTL 2000.
	 */
	public Particle() {
		this((byte) 22, 2000f);
	}

	/**
	 * With TTL 2000
	 *
	 * @param id
	 */
	public Particle(byte id) {
		this(id, 2000f);
	}

	/**
	 *
	 * @param id
	 * @param maxtime TTL in ms
	 */
	public Particle(byte id, float maxtime) {
		super(id, 0, false);
		init(maxtime);
	}
	
	public void init(float maxtime){
		this.maxtime = maxtime;
		timeTillDeath = maxtime;
		setSaveToDisk(false);
		if (type.isGrowing()) {
			setScaling(0);
		} else {
			setScaling(0.3f);
		}
		setFloating(true);
		setName("Particle");
		super.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));
		startingColor.set(getColor());
		setMass(0.0005f);
		rotateRight = Math.random() > 0.5f;
	}

	/**
	 *
	 * @param type
	 */
	public void setType(ParticleType type) {
		this.type = type;
	}

	/**
	 *
	 * @return
	 */
	public ParticleType getType() {
		return type;
	}

	@Override
	public void setColor(Color color) {
		super.getColor().set(color);
		startingColor.set(color);
		startingAlpha = color.a;
	}

	/**
	 * Time to live for each particle.
	 *
	 * @param time in ms
	 */
	public void setTTL(float time) {
		maxtime = time;
		//clamp
		if (timeTillDeath > maxtime) {
			timeTillDeath = maxtime;
		}
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		timeTillDeath -= dt;

		if (timeTillDeath <= 0) {
			dispose();
			return;
		}
//		//spread on floor
//		if (direction.z <0 && isOnGround()){
//			direction.x *= 2;
//			direction.y *= 2;
//			direction.z = 0;
//		}
//		Vector3 step = direction.cpy().scl(dt/1000f);
//		getPosition().addVector(step);
//		CoreData block = getPosition().getBlock();
//		if (block!=null && block.isObstacle())
//			getPosition().addVector(step.scl(-1));//reverse step
		if (rotateRight) {
			setRotation(getRotation() - dt / 10f);
		} else {
			setRotation(getRotation() + dt / 10f);
		}
		if (type.isGrowing()) {
			setScaling(getScaling() + dt / 800f);
		}
		if (type.fade()) {
			float t = (timeTillDeath) / maxtime;
			getColor().a = startingAlpha * Interpolation.fade.apply(t);
		}
		if (type.fadeToBlack()) {
			getColor().r = startingColor.r * (timeTillDeath / maxtime);
			getColor().g = startingColor.g * (timeTillDeath / maxtime);
			getColor().b = startingColor.b * (timeTillDeath / maxtime);
		}
	}

	/**
	 *
	 * @return
	 */
	public float getPercentageOfLife() {
		return timeTillDeath / maxtime;
	}

	/**
	 * the amount of time the object lives maximum.
	 *
	 * @return
	 */
	public float getLivingTime() {
		return maxtime;
	}

	void setPool(Pool<Particle> pool) {
		this.pool = pool;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (pool != null) {
			pool.free(this);
		}
	}

}
