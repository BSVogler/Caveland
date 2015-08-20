package com.bombinggames.wurfelengine.core.Gameobjects;

import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 * Manages the game logic for a block
 * @author Benedikt Vogler
 */
public abstract class AbstractLogicBlock {
	private static final long serialVersionUID = 1L;
	private final Coordinate coord;
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
	 * must be named "getPosition" so that this method can implement the interface {@link com.bombinggames.caveland.GameObjects.Interactable}
	 * @return 
	 */
	public Coordinate getPosition() {
		return coord;
	}

	public Block getBlock() {
		return block;
	}
	
	public abstract void update(float dt);
	
}
