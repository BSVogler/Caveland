package com.bombinggames.wurfelengine.core.Gameobjects;

import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 * Manages the game logic for a block. The instances are not saved in the map save file therfore every data saved in the fields are lost after quitting.<br>
 * Points to a {@link Coordinate} in the map. If the content of the coordinate changes it will be removed via {@link  com.bombinggames.wurfelengine.core.Map.Map}. Check if is about to be removed via {@link #isValid() }.<br> If you want to save information in the save file you have to use and spawn an {@link AbstractEntity}.
 * @author Benedikt Vogler
 */
public abstract class AbstractBlockLogicExtension {
	private static final long serialVersionUID = 1L;
	/**
	 * pointer
	 */
	private final Coordinate coord;
	/**
	 * Logic points to which block? Should be used only for validity check and acces via coordinate.
	 */
	private final Block block;

	/**
	 * Called when spawned. Should not access the map because during map creating this method is called and the map still empty.
	 * @param block the block at the position
	 * @param coord the position where the logic block is placed
	 */
	public AbstractBlockLogicExtension(Block block, Coordinate coord) {
		this.block = block;
		this.coord = coord;
	}

	/**
	 * This method be named "getPosition" so that this method can implement the interface {@link com.bombinggames.caveland.GameObjects.Interactable}
	 * @return not copy safe
	 */
	public Coordinate getPosition() {
		return coord;
	}

	/**
	 * A logicblock is still valid if the pointer shows to a block with the same
	 * id as during creation.
	 *
	 * @return false if should be deleted
	 */
	public boolean isValid() {
		Block blockatCoord = coord.getBlock();
		return blockatCoord != null && blockatCoord.getId() == block.getId();
	}

	public abstract void update(float dt);

	/**
	 * called when removed
	 */
	public abstract void dispose();
	
}
