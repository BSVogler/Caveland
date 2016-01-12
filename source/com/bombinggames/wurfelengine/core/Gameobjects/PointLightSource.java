package com.bombinggames.wurfelengine.core.Gameobjects;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import com.bombinggames.wurfelengine.core.Map.Intersection;
import com.bombinggames.wurfelengine.core.Map.Point;
import com.bombinggames.wurfelengine.core.Map.Position;

/**
 * A light source is an invisible entity which spawns light from one point.
 * @author Benedikt Vogler
 */
public class PointLightSource extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	private final int radius;
	private final float floatradius;
	/**
	 * brightness coordiante relative to center, last dimension is side
	 */
	private final float[][][][] lightcache;
	/**
	 * color of the light of this source
	 */
	private final transient Color color;
	private float brightness;
	private boolean enabled = true;
	private Point lastPos;
	private final GameView view;

	/**
	 *
	 * @param color
	 * @param maxRadius cut at distance of this amount of meters. boosts
	 * game performance if smaller
	 * @param brightness empirical factor ~5-30
	 * @param view
	 */
	public PointLightSource(Color color, float maxRadius, float brightness, GameView view) {
		super((byte) 0);
		setName("LightSource");
		disableShadow();
		this.floatradius = maxRadius;
		this.radius = (int) Math.ceil(maxRadius);
		this.brightness = brightness;
		this.color = color;
		if (radius == 0) {
			this.lightcache = new float[1][1][1][3];
		} else {
			this.lightcache = new float[this.radius * 2][this.radius * 4][this.radius * 2][3];
		}
		this.view = view;
	}
	
	private void clearCache() {
		for (float[][][] x : lightcache) {
			for (float[][] y : x) {
				for (float[] z : y) {
					z[0] = 0;
					z[1] = 0;
					z[2] = 0;
				}
			}
		}
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		return this;
	}

	@Override
	public void setPosition(Point pos) {
		super.setPosition(pos);
		
		//if moved
		if (hasPosition() && enabled && (lastPos==null || !lastPos.equals(getPosition()))){
			clearCache();
		}
	}

	@Override
	public void setPosition(Position pos) {
		super.setPosition(pos);
		
		//if moved
		if (hasPosition() && enabled && (lastPos==null || !lastPos.equals(getPosition()))){
			clearCache();
		}
	}
	

	/**
	 * fills the cache by sending rays starting at the position
	 * @param delta 
	 */
	public void lightNearbyBlocks(float delta) {
		if (hasPosition()) {
			Point lightPos = getPosition();
			lastPos = lightPos.cpy();
			
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
								(Block t) -> !t.isTransparent()
							);
//							Particle dust = (Particle) new Particle(
//								(byte) 22,
//								200f
//							).spawn(lightPos.cpy());
//							dust.setMovement(dir.cpy().scl(9f));
							if (inters != null && inters.getPoint() != null) {
								float pow = lightPos.distanceTo(getPosition().toCoord().add(x, y, z).toPoint()) / Block.GAME_EDGELENGTH;
								float l = (1 + brightness) / (pow * pow);
								Vector3 target = getPosition().toCoord().add(x, y, z).toPoint().cpy();
								
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
			if (lastPos == null || !lastPos.equals(getPosition())) {
				lightNearbyBlocks(dt);
			}

			//apply cache
			Coordinate tmp = getPosition().toCoord();
			int xCenter = tmp.getX();
			int yCenter = tmp.getY();
			int zCenter = tmp.getZ();
			for (int x = -radius; x < radius; x++) {
				for (int y = -radius * 2; y < radius * 2; y++) {
					for (int z = -radius; z < radius; z++) {
						//get the light in the cache
						float[] blocklight = lightcache[x + radius][y + radius * 2][z + radius];
						tmp.set(xCenter + x, yCenter + y, zCenter + z);
						if (tmp.getRenderBlock(view) != null) {
							tmp.addLightlevel(view, color.cpy().mul(blocklight[0]), Side.LEFT);
							tmp.addLightlevel(view, color.cpy().mul(blocklight[1]), Side.TOP);
							tmp.addLightlevel(view, color.cpy().mul(blocklight[2]), Side.RIGHT);
						}
					}
				}
			}
		}
	}

	/**
	 *
	 */
	public void enable() {
		enabled = true;
	}

	/**
	 *
	 */
	public void disable() {
		enabled = false;
	}

	/**
	 *
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 *
	 * @param brightness
	 */
	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		return true;
	}
	
}
