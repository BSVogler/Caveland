package com.bombinggames.wurfelengine.core.gameobjects;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import java.util.LinkedList;

/**
 *
 * @author Benedikt Vogler
 */
public class Explosion extends AbstractEntity implements Telegraph {

	private static final long serialVersionUID = 1L;
	private static String explosionsound;

	private final int radius;
	private final int damage;
	private transient Camera camera;

	/**
	 * simple explosion without screen shake. Default radius is 2. Damage 500.
	 */
	public Explosion() {
		super((byte) 0);
		this.radius = 2;
		damage = 50;
		setSavePersistent(false);
	}

	/**
	 *
	 * @param radius the radius in game world blocks
	 * @param damage Damage at center.
	 * @param camera can be null. used for screen shake
	 */
	public Explosion(int radius, int damage, Camera camera) {
		super((byte) 0);
		this.radius = radius;
		this.damage = damage;
		if (explosionsound == null) {
			explosionsound = "explosion";
		}
		this.camera = camera;
		setSavePersistent(false);
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
					Coordinate coord = point.toCoord().add(x, y, z);
   					int intdamage = (int) (damage
						* (1 - getPosition().distanceToSquared(coord)
						/ (radius * radius * RenderCell.GAME_EDGELENGTH * RenderCell.GAME_EDGELENGTH)));
					if (intdamage > 0) {
						if (intdamage > 100) {
							intdamage = 100; //clamp so it's under 127 to avoid byte overflow
						}
						coord.damage(
							(byte) intdamage
						);
					}
					
					//get every entity which is attacked
					LinkedList<MovableEntity> list
						= Controller.getMap().getEntitysOnCoord(
							coord,
							MovableEntity.class
						);

					for (MovableEntity ent : list) {
						intdamage = (int) (damage
						* (1 - getPosition().distanceToSquared(ent)
						/ (radius * radius * RenderCell.GAME_EDGELENGTH * RenderCell.GAME_EDGELENGTH)));
						intdamage*=1.2;//entities should break a little easier
						if (intdamage > 100) {
							intdamage = 100; //clamp so it's under 127 to avoid byte overflow
						}
						MessageManager.getInstance().dispatchMessage(
							this,
							(Telegraph) ent,
							Events.damage.getId(),
							(byte) intdamage
						);
					}

					Particle dust = (Particle) new Particle(
						(byte) 22,
						1700
					).spawn(point.cpy().add((float) Math.random()*20f, (float) Math.random()*20f, (float) Math.random()*20f));//spawn at center
					dust.getColor().set(0.6f, 0.55f, 0.4f, 1f);
					dust.setType(ParticleType.FIRE);
					dust.addMovement(
						coord.toPoint().sub(point).nor().scl(4f)
					);//move from center to outside
				}
			}
		}

		if (camera != null) {
			camera.shake(radius * 100 / 3f, 100);
		}
		if (explosionsound != null) {
			WE.SOUND.play(explosionsound, getPosition());
		}
		dispose();
		return this;
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return true;
	}
}
