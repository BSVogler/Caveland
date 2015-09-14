package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
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

	/**
	 * teleports to 0 0 Chunk.getBlocksZ()-1 by default
	 */
	public Portal() {
		super((byte) 0);
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
	
	public void setTarget(Coordinate target) {
		this.target = target;
	}

	@Override
	public void update(float dt) {
		if (isSpawned() && active){
			getPosition().toCoord().getEntitiesInside()
			.forEach( (AbstractEntity e) -> {
				 if (e.getPosition().getZ() <= getPosition().toPoint().getZ() + 10 && e != this)
					e.setPosition(target.cpy());
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
	

}
