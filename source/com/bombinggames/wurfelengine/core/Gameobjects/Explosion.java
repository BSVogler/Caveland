package com.bombinggames.wurfelengine.core.Gameobjects;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Color;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import com.bombinggames.wurfelengine.core.Map.Point;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class Explosion extends AbstractEntity implements Telegraph {

	private static final long serialVersionUID = 1L;
	private static String explosionsound;

	private final int radius;
	private final byte damage;
	private transient Camera camera;

	/**
	 * simple explosion without screen shake. Default radius is 2. Damage 500.
	 */
	public Explosion() {
		super((byte) 0);
		this.radius = 2;
		damage = 50;
		setSaveToDisk(false);
	}

	/**
	 *
	 * @param radius the radius in game world blocks
	 * @param damage Damage at center.
	 * @param camera can be null. used for screen shake
	 */
	public Explosion(int radius, byte damage, Camera camera) {
		super((byte) 0);
		this.radius = radius;
		this.damage = damage;
		if (explosionsound == null) {
			explosionsound = "explosion";
		}
		this.camera = camera;
		setSaveToDisk(false);
	}

	@Override
	public void update(float dt) {
	}

	/**
	 * explodes
	 *
	 * @return
	 */
	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		//replace blocks by air
		for (int x = -radius; x < radius; x++) {
			for (int y = -radius * 2; y < radius * 2; y++) {
				for (int z = -radius; z < radius; z++) {
					Coordinate coord = point.toCoord().addVector(x, y, z);
					if (x * x + (y / 2) * (y / 2) + z * z <= radius * radius) {//check if in radius
						coord.damage((byte) (100 * (1 - (x * x + (y / 2) * (y / 2) + z * z) / (radius * radius))));
//						coord.damage((byte) (damage * (1 - (x * x + (y / 2) * (y / 2) + z * z) / (radius * radius))));

						//get every entity which is attacked
						ArrayList<MovableEntity> list
							= Controller.getMap().getEntitysOnCoord(
								coord,
								MovableEntity.class
							);

						for (MovableEntity ent : list) {
							MessageManager.getInstance().dispatchMessage(
								this,
								(Telegraph) ent,
								Events.damage.getId(),
								damage
							);
						}
						

						Particle dust = (Particle) new Particle(
							(byte) 22,
							1700
						).spawn(coord.toPoint());//spawn at center
						dust.setColor(new Color(0.5f, 0.45f, 0.4f, 1f));
						dust.setType(ParticleType.FIRE);
						dust.addMovement(
							coord.toPoint().sub(point).nor().scl(4f)
						);//move from center to outside
					}
				}
			}
		}

		if (camera != null) {
			camera.shake(radius * 100 / 3f, 100);
		}
		if (explosionsound != null) {
			WE.SOUND.play(explosionsound);
		}
		dispose();
		return this;
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return true;
	}
}
