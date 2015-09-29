package com.bombinggames.wurfelengine.core.Gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.bombinggames.wurfelengine.core.Map.Point;

/**
 *
 * @author Benedikt Vogler
 */
public class Particle extends MovableEntity {
	private static final long serialVersionUID = 2L;
	
	private float maxtime;
	private float timeTillDeath;
	private Color startingColor;
	private ParticleType type;
	private float brightness = 0f;

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
		super(id, 0, false);
		this.maxtime = maxtime;
		timeTillDeath = maxtime;
		setSaveToDisk(false);
		setScaling(-1);
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
	
	/**
	 * Time to live for each particle.
	 * @param time in ms
	 */
	public void setTTL(float time){
		maxtime = time;
		//clamp
		if (timeTillDeath > maxtime)
			timeTillDeath=maxtime;
	}
	
	/**
	 * if the particle emits light youcan set the brightness
	 * @param brightness
	 */
	public void setBrightness(float brightness){
		this.brightness = brightness;
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		timeTillDeath-=dt;
		
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
			
		setRotation(getRotation()-dt/10f);
		setScaling(getScaling()+dt/300f);
		if (type.fade())
			getColor().a = timeTillDeath/maxtime;
		if (type.fadeToBlack()) {
			getColor().r = startingColor.r*((timeTillDeath*2)/maxtime);
			getColor().g = startingColor.g*((timeTillDeath)/maxtime);
			getColor().b = startingColor.b*((timeTillDeath)/maxtime);
		}
		
		if (getPosition()!=null && type==ParticleType.FIRE) {
			//licht
			Point pos = getPosition().toPoint();
			float flicker = (float) Math.random();
			for (int x = -3; x < 3; x++) {
				for (int y = -6; y < 6; y++) {
					for (int z = -3; z < 3; z++) {
						Block blockToLight = getPosition().toCoord().addVector(x, y, z).getBlock(); 
						if (blockToLight != null) {
							float pow = pos.distanceTo(getPosition().cpy().addVector(x, y, 0).toPoint())/(float) Block.GAME_EDGELENGTH+1;
							float l  = (1 +brightness) / (pow*pow);
							blockToLight.addLightlevel(l*(0.15f+flicker*0.03f), Side.TOP, 0);
							blockToLight.addLightlevel(l*(0.15f+flicker*0.000005f), Side.TOP, 1);
							blockToLight.addLightlevel(l*(0.15f+flicker*0.000005f), Side.TOP, 2);
							blockToLight.addLightlevel(l*(0.15f+flicker*0.03f), Side.RIGHT, 0);
							blockToLight.addLightlevel(l*(0.15f+flicker*0.005f), Side.RIGHT, 1);
							blockToLight.addLightlevel(l*(0.15f+flicker*0.005f), Side.RIGHT, 2);
							blockToLight.addLightlevel(l*(0.15f+flicker*0.03f), Side.LEFT, 0);
							blockToLight.addLightlevel(l*(0.15f+flicker*0.005f), Side.LEFT, 1);
							blockToLight.addLightlevel(l*(0.15f+flicker*0.005f), Side.LEFT, 2);
						}
					}
				}
			}
		}
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
