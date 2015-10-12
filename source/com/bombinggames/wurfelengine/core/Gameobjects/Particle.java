package com.bombinggames.wurfelengine.core.Gameobjects;

import com.badlogic.gdx.graphics.Color;

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
	private Color startingColor;
	private ParticleType type = ParticleType.REGULAR;

	/**
	 * With TTL 2000. 
	 */
	public Particle() {
		this((byte) 22, 2000f);
	}

	/**
	 * With TTL 2000
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
		this.maxtime = maxtime;
		timeTillDeath = maxtime;
		setSaveToDisk(false);
		if (type.isGrowing())
			setScaling(-1);
		setFloating(true);
		setName("Particle");
	}

	public void setType(ParticleType type) {
		this.type = type;
	}

	public ParticleType getType() {
		return type;
	}

	@Override
	public void setColor(Color color) {
		super.setColor(color);
		startingColor = color.cpy();
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

		setRotation(getRotation() - dt / 10f);
		if (type.isGrowing()){
			setScaling(getScaling() + dt / 300f);
		}
		if (type.fade()) {
			getColor().a = timeTillDeath / maxtime;
		}
		if (type.fadeToBlack()) {
			getColor().r = startingColor.r * (timeTillDeath / maxtime);
			getColor().g = startingColor.g * (timeTillDeath / maxtime);
			getColor().b = startingColor.b * (timeTillDeath / maxtime);
		}
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * the amount of time the object lives maximum.
	 *
	 * @return
	 */
	public float getLivingTime() {
		return maxtime;
	}
}
