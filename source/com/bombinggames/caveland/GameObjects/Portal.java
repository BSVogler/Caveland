package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *Teleports every object in this cell.
 * @author Benedikt Vogler
 */
public class Portal extends AbstractEntity  {
	
	private static final long serialVersionUID = 2L;
	private Coordinate target = new Coordinate(0, 0, Chunk.getBlocksZ()-1);
	/**
	 * indicates whether the portal is open or not
	 */
	private transient boolean active = true;
	private transient AimBand particleBand;

	/**
	 * teleports to 0 0 Chunk.getBlocksZ()-1 by default
	 */
	public Portal() {
		super((byte) 0);
		setIndestructible(true);
		setName("Portal");
	}
	
	/**
	 * teleports to 0 0 Chunk.getBlocksZ()-1 by default
	 * @param id
	 */
	public Portal(byte id) {
		super(id);
		setIndestructible(true);
		setName("Portal");
	}
	
	/**
	 * copy safe
	 * @return 
	 */
	public Coordinate getTarget() {
		return target.cpy();
	}
	
	/**
	 * Set the coordiante where the portal teleports to.
	 * @param target 
	 */
	public void setTarget(Coordinate target) {
		this.target = target;
	}

	@Override
	public void update(float dt) {
		if (hasPosition() && active){
			if (particleBand != null){
				particleBand.update();
			}
			
			//move things in the portal
			getPosition().toCoord().getEntitiesInside(MovableEntity.class)
				.forEach((AbstractEntity e) -> {
					if (e.getPosition().getZ() <= getPosition().toPoint().getZ() + 10
						&& e != this
						&& ((MovableEntity) e).isColiding()
					) {
						e.setPosition(target.cpy());
					}
				}
			);
		}
	}

	/**
	 * Set the portal to "open" or "closed"
	 * @param b 
	 */
	public void setActive(boolean b) {
		active = b;
	}

	/**
	 * indicates whether the portal is open or not
	 * @return 
	 */
	public boolean isActive() {
		return active;
	}
	
	@Override
	public void onSelectInEditor(){
		if (particleBand == null) {
			particleBand = new AimBand(this, target);
		} else {
			particleBand.setGoal(target);
		}
	}
	
	@Override
	public void onUnSelectInEditor(){
		if (particleBand != null)
			particleBand = null;
	}
}
