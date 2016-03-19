package com.bombinggames.wurfelengine.core.map;

/**
 * Manages the game logic for a block. The instances are not saved in the map
 * save file therfore every data saved in the fields are lost after
 * quitting.<br>
 * Points to a {@link Coordinate} in the map. If the content of the coordinate
 * changes it will be removed via
 * {@link  com.bombinggames.wurfelengine.core.map.Map}. Check if is about to be
 * removed via {@link #isValid() }.<br> If you want to save information in the
 * save file you have to use and spawn an {@link AbstractEntity}.
 *
 * @author Benedikt Vogler
 */
public abstract class AbstractBlockLogicExtension {

	private static final long serialVersionUID = 2L;
	/**
	 * pointer to the according coordinate
	 */
	private final Coordinate coord;
	/**
	 * Is only used for validity check.
	 */
	private final byte id;

	/**
	 * Called when spawned. Should not access the map because during map
	 * creating this method is called and the map still empty. Also entities can not be spawned here.
	 *
	 * @param blockId the block at the position
	 * @param coord the position where the logic block is placed
	 */
	public AbstractBlockLogicExtension(byte blockId, Coordinate coord) {
		this.id = blockId;
		if (coord == null) {
			throw new NullPointerException();
		}
		this.coord = coord;
	}

	/**
	 * This method must be named "getPosition" so that this method can implement
	 * other interfaces using this API signature
	 *
	 * @return not copy safe. never null
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
		return coord.getBlockId() == id;
	}

	/**
	 *
	 * @param dt
	 */
	public abstract void update(float dt);

	/**
	 * called when removed
	 */
	public abstract void dispose();

}
