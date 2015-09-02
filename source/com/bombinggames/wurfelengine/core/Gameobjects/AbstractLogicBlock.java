package com.bombinggames.wurfelengine.core.Gameobjects;

import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 * Manages the game logic for a block. Points to a coordinate in the map. If the content of the coordinate changes it will be removed via {@link  com.bombinggames.wurfelengine.core.Map.Map}. Check this via {@link #isValid() }
 * @author Benedikt Vogler
 */
public abstract class AbstractLogicBlock {
	private static final long serialVersionUID = 1L;
	/**
	 * pointer
	 */
	private final Coordinate coord;
	/**
	 * logic points to which block?
	 */
	private final Block block;

	/**
	 * Called when spawned.
	 * @param block
	 * @param coord 
	 */
	public AbstractLogicBlock(Block block, Coordinate coord) {
		this.block = block;
		this.coord = coord;
	}

	/**
	 * This method be named "getPosition" so that this method can implement the interface {@link com.bombinggames.caveland.GameObjects.Interactable}
	 * @return 
	 */
	public Coordinate getPosition() {
		return coord;
	}
	
	/**
	 * A logicblock is still valid if the pointer shows to a block with the same id as during creation.
	 * @return 
	 */
	public boolean isValid(){
		if (coord.getBlock() == null)
			return false;
		return coord.getBlock().getId() == block.getId();
	
	}
	
	public abstract void update(float dt);
	
}
