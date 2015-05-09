package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Benedikt Vogler
 */
public class Particle extends MovableEntity {
	
	public static enum ParticleType {
		/**
		 *Starting as fire, then becomes smoke
		 */
		FIRE(true, true),
		/**
		 * Just fades.
		 */
		SMOKE(true, false),
		/**
		 *Just dissapeares.
		 */
		REGULAR(false, false);

//		private static Collectible.CollectibleType fromValue(String value) {
//			if (value != null) {  
//				for (Collectible.CollectibleType type : values()) {  
//					if (type.name().equals(value)) {  
//						return type;  
//					}  
//				}
//			} return null;
//		}  

		private boolean fade;
		private boolean fadeToBlack;

		private ParticleType(boolean fade, boolean fadeToBlack) {
			this.fade = fade;
			this.fadeToBlack = fadeToBlack;
		}

		public boolean fade() {
			return fade;
		}
		
		public boolean fadeToBlack() {
			return fadeToBlack;
		}
	}
private static final long serialVersionUID = 1L;
	private final float maxtime;
	
	private float timeTillDeath;
	private Color startingColor;
	private ParticleType type;

	public Particle() {
		this((byte) 22, 2000f);
	}
	
	public Particle(byte id) {
		this(id, 2000f);
	}
	
	/**
	 * 
	 * @param id
	 * @param maxtime TTL in ms
	 */
	public Particle(byte id, float maxtime) {
		super(id, 0);
		this.maxtime = maxtime;
		timeTillDeath=maxtime;
		setSaveToDisk(false);
		setScaling(-1);
		disableShadow();
		setFloating(true);
		setName("Particle");
		type = ParticleType.REGULAR;
	}
	
	public void setType(ParticleType type){
		this.type = type;
	}

	@Override
	public void setColor(Color color) {
		super.setColor(color);
		startingColor =color.cpy();
	}
	
	
	@Override
	public void update(float dt) {
		super.update(dt);
		timeTillDeath-=dt;
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
			
		setRotation(getRotation()-dt/10f);
		setScaling(getScaling()+dt/300f);
		if (type.fade())
			getColor().a = timeTillDeath/maxtime;
		if (type.fadeToBlack()) {
			getColor().r = startingColor.r*((timeTillDeath*2)/maxtime);
			getColor().g = startingColor.g*((timeTillDeath)/maxtime);
			getColor().b = startingColor.b*((timeTillDeath)/maxtime);
		}
		if (timeTillDeath <= 0) dispose();
	}
}
