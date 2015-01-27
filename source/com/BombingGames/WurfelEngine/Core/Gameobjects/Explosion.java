package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class Explosion extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	private static Sound explosionsound;
	
	private final int radius;
	private final int damage;
	private transient Camera camera;

	/**
	 * simple explosion without screen shake. Default radius is 2. Damage 500.
	 */
	public Explosion() {
		super(0);
		this.radius = 2;
		damage = 500;
		setSaveToDisk(false);
	}

	
	/**
	 * 
	 * @param radius the radius in game world blocks
	 * @param damage [0;1000]
	 * @param camera can be null. used for screen shake
	 */
	public Explosion(int radius, int damage, Camera camera) {
		super(0);
		this.radius = radius;
		this.damage = damage;
		if (explosionsound == null)
            explosionsound = WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/explosion2.ogg");
		this.camera = camera;
		setSaveToDisk(false);
    }

	@Override
	public void update(float dt) {
	}

	/**
	 * explodes
	 * @return 
	 */
	
	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		//replace blocks by air
		for (int x=-radius; x<radius; x++){
			for (int y=-radius*2; y<radius*2; y++) {
				for (int z=-radius; z<radius; z++){
					Coordinate pos = getPosition().cpy().getCoord().addVector(x, y, z);
					//place air
					if (x*x + (y/2)*(y/2)+ z*z <= radius*radius){
						pos.destroy();
						
						//get every entity which is attacked
						ArrayList<MovableEntity> list =
							Controller.getMap().getEntitysOnCoord(
								pos,
								MovableEntity.class
							);
						for (MovableEntity ent : list) {
							if (!(ent instanceof PlayerWithWeapon))//don't damage player with weapons
								ent.damage(damage);
						}
					}
					
					//spawn effect
					if (x*x + (y/2)*(y/2)+ z*z >= radius*radius-4 &&
						x*x + (y/2)*(y/2)+ z*z <= radius*radius){
						new SimpleEntity(31).spawn(pos.getPoint()).setAnimation(
							new EntityAnimation(
								new int[]{700,2000},
								true,
								false
							)
						);
					}
				}
			}	
		}
		
		if (camera!=null)
			camera.shake(radius*100/3f, 100);
		if (explosionsound != null)
			explosionsound.play();
		dispose();
		return this;
	}
}