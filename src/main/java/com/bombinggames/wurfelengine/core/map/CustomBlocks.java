package com.bombinggames.wurfelengine.core.map;

import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;

/**
 *
 * @author Benedikt Vogler
 */
public interface CustomBlocks {

	/**
	 *
	 * @param id
	 * @param value
	 * @return
	 */
	public boolean isObstacle(byte id, byte value);

	/**
	 *
	 * @param spriteId
	 * @param spriteValue
	 * @return
	 */
	public boolean isTransparent(byte spriteId, byte spriteValue);

	/**
	 * Check if the block is liquid.
	 *
	 * @param id
	 * @param value
	 * @return true if liquid, false if not
	 */
	public boolean isLiquid(byte id, byte value);

	/**
	 * Default is "undefined".
	 *
	 * @param id
	 * @param value
	 * @return
	 */
	public String getName(byte id, byte value);

	/**
	 *
	 * @param id there are id's &lt; 10 which are filtered before
	 * @param value
	 * @return
	 */
	public RenderCell toRenderBlock(byte id, byte value);

	/**
	 * Is the block a true block with three sides or does it get rendered by a
	 * single sprite?<br>
	 * This field is only used for representation (view) related data.<br>
	 * Only used for blocks. Entities should return <i>false</i>.
	 *
	 * @param id
	 * @param value
	 * @return <i>true</i> if it has sides, <i>false</i> if is rendered as a
	 * single sprite
	 */
	public boolean hasSides(byte id, byte value);

	/**
	 *
	 * @param id
	 * @param value
	 * @return
	 */
	public boolean isIndestructible(byte id, byte value);

	/**
	 *
	 * @param id
	 * @param value
	 * @return
	 */
	public boolean hasLogic(byte id, byte value);
	
	/**
	 *
	 * @param id
	 * @param value
	 * @param coord
	 * @return
	 */
	public AbstractBlockLogicExtension newLogicInstance(byte id, byte value, Coordinate coord);
	
}
