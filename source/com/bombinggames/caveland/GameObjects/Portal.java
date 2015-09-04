package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
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
	 * teleports to 0 0 Chunk.getBlocksZ()-1 by default
	 */
	public Portal() {
		super((byte) -1);
		setIndestructible(true);
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
		if (isSpawned()){
			getPosition().toCoord().getEntitiesInside()
			.forEach( (Object e) -> {
				 if (((AbstractEntity) e).getPosition().getZ() <= getPosition().toPoint().getZ() + 10)
					((AbstractGameObject) e).setPosition(target.cpy());
				}
			);
		}
	}

}
