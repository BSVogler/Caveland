package com.bombinggames.wurfelengine.core.Gameobjects;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Map.AbstractPosition;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import com.bombinggames.wurfelengine.core.Map.Intersection;
import com.bombinggames.wurfelengine.core.Map.Point;

/**
 * A light source is an invisible entity which spawns light from one point.
 * @author Benedikt Vogler
 */
public class PointLightSource extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private final int radius;
	private final float floatradius;
	private final float[][][][] lightcache;
	private final transient Color color;
	private float brightness;
	private boolean enabled = true;
	private Point lastPos;

	/**
	 *
	 * @param color
	 * @param maxRadius cut at distance of this amount of meters. boosts
	 * game performance if smaller
	 * @param brightness
	 */
	public PointLightSource(Color color, float maxRadius, float brightness) {
		super((byte) 0);
		setName("LightSource");
		disableShadow();
		this.floatradius = maxRadius;
		this.radius = (int) Math.ceil(maxRadius);
		this.brightness = brightness;
		this.color = color;
		if (radius==0)
			this.lightcache = new float[1][1][1][3];
		else
			this.lightcache = new float[this.radius * 2][this.radius * 4][this.radius * 2][3];
	}
	
	private void clearCache(){
		for (float[][][] x : lightcache) {
			for (float[][] y : x) {
				for (float[] z : y) {
					z[0]=0;
					z[1]=0;
					z[2]=0;
				}
			}
		}
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		//lastPos = point;
		//lightNearbyBlocks(0);
		return this;
	}

	@Override
	public void setPosition(Point pos) {
		super.setPosition(pos);
		//lastPos = pos;
		
		//if moved
		if (hasPosition() && enabled && (lastPos==null || !lastPos.equals(getPosition()))){
			clearCache();
			//lightNearbyBlocks(0);
		}
	}

	@Override
	public void setPosition(AbstractPosition pos) {
		super.setPosition(pos);
		//lastPos = pos.toPoint();
		
		//if moved
		if (hasPosition() && enabled && (lastPos==null || !lastPos.equals(getPosition()))){
			clearCache();
			//lightNearbyBlocks(0);
		}
	}
	

	/**
	 * fills the cache by sending rays starting at the position
	 * @param delta 
	 */
	public void lightNearbyBlocks(float delta) {
		if (hasPosition()) {
			Point lightPos = getPosition();
			lastPos = lightPos;
			
			float rand = (float) Math.random();
			float noiseX = rand * 2 - 1;
			float noiseY = (float) Math.random() * 2 - 1;
			
			//light blocks under the torch
			for (int z = -radius; z < radius; z++) {
				for (int x = -radius; x < radius; x++) {
					for (int y = -radius * 2; y < radius * 2; y++) {

						//slowly decrease
						if (lightcache[x + radius][y + radius * 2][z + radius][0] > 0) {
							lightcache[x + radius][y + radius * 2][z + radius][0] -= delta * 0.0001f;
							if (lightcache[x + radius][y + radius * 2][z + radius][0] < 0) {
								lightcache[x + radius][y + radius * 2][z + radius][0] = 0;
							}
						}
						if (lightcache[x + radius][y + radius * 2][z + radius][1] > 0) {
							lightcache[x + radius][y + radius * 2][z + radius][1] -= delta * 0.0001f;
							if (lightcache[x + radius][y + radius * 2][z + radius][1] < 0) {
								lightcache[x + radius][y + radius * 2][z + radius][1] = 0;
							}
						}
						if (lightcache[x + radius][y + radius * 2][z + radius][2] > 0) {
							lightcache[x + radius][y + radius * 2][z + radius][2] -= delta * 0.0001f;
							if (lightcache[x + radius][y + radius * 2][z + radius][2] < 0) {
								lightcache[x + radius][y + radius * 2][z + radius][2] = 0;
							}
						}

						//send rays
						Vector3 dir = new Vector3(x + noiseX, y + noiseY, z + rand * 2 - 1).nor();
						if (dir.len2() > 0) {//filter some rays
							Intersection inters = lightPos.raycast(
								dir,
								floatradius * 2,
								null,
								true
							);
//							Particle dust = (Particle) new Particle(
//								(byte) 22,
//								200f
//							).spawn(lightPos.cpy());
//							dust.setMovement(dir.cpy().scl(9f));
							if (inters != null && inters.getPoint() != null) {
								float pow = lightPos.distanceTo(getPosition().toCoord().addVector(x, y, z).toPoint()) / Block.GAME_EDGELENGTH;
								float l = (1 + brightness) / (pow * pow);
								Vector3 target = getPosition().toCoord().addVector(x, y, z).toPoint().cpy();
								
								//side 0
								float lambert = lightPos.cpy().sub(
									target.add(-Block.GAME_DIAGLENGTH2, Block.GAME_DIAGLENGTH2, 0)
								).nor().dot(Side.LEFT.toVector());
								
								float newbright = l *lambert* (0.15f + rand * 0.005f);
								if (lambert > 0 && newbright > lightcache[x + radius][y + radius * 2][z + radius][0]) {
									lightcache[x + radius][y + radius * 2][z + radius][0] = newbright;
								}
								
								//side 1
								lambert = lightPos.cpy().sub(
									target.add(0, 0, Block.GAME_EDGELENGTH)
								).nor().dot(Side.TOP.toVector());

								newbright = l * lambert * (0.15f + rand * 0.005f);
								if (lambert > 0 && newbright > lightcache[x + radius][y + radius * 2][z + radius][1]) {
									lightcache[x + radius][y + radius * 2][z + radius][1] = newbright;
								}

								//side 2
								lambert = lightPos.cpy().sub(
									target.add(Block.GAME_DIAGLENGTH2, Block.GAME_DIAGLENGTH2, 0)
								).nor().dot(Side.RIGHT.toVector());

								newbright = l *lambert* (0.15f + rand * 0.005f);
								if (lambert > 0 && newbright > lightcache[x + radius][y + radius * 2][z + radius][2]) {
									lightcache[x + radius][y + radius * 2][z + radius][2] = newbright;
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		
		if (enabled && hasPosition()) {
			if (lastPos==null || !lastPos.equals(getPosition())) {
				lightNearbyBlocks(dt);
			}

			//apply cache
			Coordinate center = getPosition().toCoord();
			int xCenter = center.getX();
			int yCenter = center.getY();
			int zCenter = center.getZ();
			for (int x = -radius; x < radius; x++) {
				for (int y = -radius * 2; y < radius * 2; y++) {
					for (int z = -radius; z < radius; z++) {
						float[] blocklight = lightcache[x + radius][y + radius * 2][z + radius];
						Block block = Controller.getMap().getBlock(xCenter+x, yCenter+y, zCenter+z);
						if (block != null) {
							block.addLightlevel(blocklight[0] * color.r, Side.LEFT, 0);
							block.addLightlevel(blocklight[0] * color.g, Side.LEFT, 1);
							block.addLightlevel(blocklight[0] * color.b, Side.LEFT, 2);
							block.addLightlevel(blocklight[1] * color.r, Side.TOP, 0);
							block.addLightlevel(blocklight[1] * color.g, Side.TOP, 1);
							block.addLightlevel(blocklight[1] * color.b, Side.TOP, 2);
							block.addLightlevel(blocklight[2] * color.r, Side.RIGHT, 0);
							block.addLightlevel(blocklight[2] * color.g, Side.RIGHT, 1);
							block.addLightlevel(blocklight[2] * color.b, Side.RIGHT, 2);
						}
					}
				}
			}
		}
	}

	public void enable() {
		enabled = true;
	}

	public void disable() {
		enabled = false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		return true;
	}
	
}
