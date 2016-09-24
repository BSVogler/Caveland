package com.bombinggames.wurfelengine.core.gameobjects;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Intersection;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.core.map.Position;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;

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
	private float brightness;
	private boolean enabled = true;
	private final Point lastPos = new Point(0, 0, 0);
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
		this.floatradius = maxRadius;
		this.radius = (int) Math.ceil(maxRadius);
		this.brightness = brightness;
		setColor(color);
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
			Point origin = getPosition();
			lastPos.set(origin);
			
			//light blocks around
			Vector3 dir = new Vector3();
			for (int z = -radius; z < radius; z++) {
				for (int x = -radius; x < radius; x++) {
					for (int y = -radius * 2; y < radius * 2; y++) {

						//reset cell in cache
						if (lightcache[x + radius][y + radius * 2][z + radius][0] > 0) {
							lightcache[x + radius][y + radius * 2][z + radius][0] = 0;
						}
						if (lightcache[x + radius][y + radius * 2][z + radius][1] > 0) {
							lightcache[x + radius][y + radius * 2][z + radius][1] = 0;
						}
						if (lightcache[x + radius][y + radius * 2][z + radius][2] > 0) {
							lightcache[x + radius][y + radius * 2][z + radius][2] = 0;
						}

						//send rays
						dir.set(x + 0.1f, y + 0.2f, z - 0.4f).nor();//offset because???
						Intersection inters = origin.raycast(dir,
							floatradius * 2,
							null,
							(Byte t) -> !RenderCell.isTransparent(t, (byte) 0)
						);
						//check if intersected
						if (inters != null && inters.getPoint() != null) {
							//get back edge of block
							Point impactP = getPosition().toCoord().add(x, y, z).toPoint().add(0, -RenderCell.GAME_DIAGLENGTH2, 0);
							//this should work in the future:getPoint().cpy().setToCenterOfCell().addCoord(x, y, z).add(0, -RenderCell.GAME_DIAGLENGTH2, 0)
							float pow = origin.distanceTo(impactP) / RenderCell.GAME_EDGELENGTH;
							float l = (1 + brightness) / (pow * pow);

							Vector3 vecToBlock = origin.cpy().sub(impactP).nor();
							//side 0
							float lambert = vecToBlock.dot(Side.LEFT.toVector());

							float newbright = l *lambert* (0.15f + 0.1f * 0.005f);
							if (lambert > 0 && newbright > lightcache[x + radius][y + radius * 2][z + radius][0]) {
								lightcache[x + radius][y + radius * 2][z + radius][0] = newbright;
							}

							//side 1
							lambert = vecToBlock.dot(Side.TOP.toVector());

							newbright = l * lambert * (0.15f + 0.2f * 0.005f);
							if (lambert > 0 && newbright > lightcache[x + radius][y + radius * 2][z + radius][1]) {
								lightcache[x + radius][y + radius * 2][z + radius][1] = newbright;
							}

							//side 2
							lambert = vecToBlock.dot(Side.RIGHT.toVector());

							newbright = l *lambert* (0.15f + 0.25f * 0.005f);
							if (lambert > 0 && newbright > lightcache[x + radius][y + radius * 2][z + radius][2]) {
								lightcache[x + radius][y + radius * 2][z + radius][2] = newbright;
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
			//check if moved and therefore has to be recalculated
			if (!getPosition().equals(lastPos)) {
				lightNearbyBlocks(dt);
			}

			//apply cache
			Coordinate tmpCoord = getPosition().toCoord();
			Color tmpColor = new Color();
			Color color = getColor();
			int xCenter = tmpCoord.getX();
			int yCenter = tmpCoord.getY();
			int zCenter = tmpCoord.getZ();
			for (int x = -radius; x < radius; x++) {
				for (int y = -radius * 2; y < radius * 2; y++) {
					for (int z = -radius; z < radius; z++) {
						//get the light in the cache
						float[] blocklight = lightcache[x + radius][y + radius * 2][z + radius];
						tmpCoord.set(xCenter + x, yCenter + y, zCenter + z);
						RenderCell rB = tmpCoord.getRenderCell(view.getRenderStorage());
						if (rB != null && !rB.isHidden()) {
							tmpCoord.addLightToBackEdge(view, Side.LEFT, tmpColor.set(color).mul(blocklight[0]));
							tmpCoord.addLightToBackEdge(view, Side.TOP, tmpColor.set(color).mul(blocklight[1]));
							tmpCoord.addLightToBackEdge(view, Side.RIGHT, tmpColor.set(color).mul(blocklight[2]));
						}
					}
				}
			}
		}
	}

	/**
	 *Turn light on.
	 */
	public void enable() {
		enabled = true;
	}

	/**
	 * Turn light off.
	 */
	public void disable() {
		enabled = false;
	}

	/**
	 * Is light on?
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * 
	 * @param brightness value  &gt;= 0
	 */
	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		return true;
	}
	
}
