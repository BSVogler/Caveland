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
	private transient Camera camera;

	/**
	 * simple explosion without screen shake
	 */
	public Explosion() {
		super(0);
		this.radius = 2;
	}

	
	/**
	 * 
	 * @param radius the radius in game world blocks
	 * @param camera can be null. used for screen shake
	 */
	public Explosion(int radius, Camera camera) {
		super(0);
		this.radius = radius;
		if (explosionsound == null)
            explosionsound = WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/explosion2.ogg");
		this.camera = camera;
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
								ent.damage(1000);
						}
					}
					
					//spawn effect
					if (x*x + (y/2)*(y/2)+ z*z >= radius*radius-4 &&
						x*x + (y/2)*(y/2)+ z*z <= radius*radius){
						new AnimatedEntity(
							31,
							0,
							new int[]{700,2000},
							true,
							false
						).spawn(pos.getPoint());
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