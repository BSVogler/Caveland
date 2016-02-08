package com.bombinggames.wurfelengine.core.gameobjects;

import com.bombinggames.wurfelengine.core.map.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.rendering.RenderBlock;

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
	 * @param id
	 * @param value
	 * @return
	 */
	public boolean isTransparent(byte id, byte value);

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
	 * @param data there are id's &lt; 10 which are filtered before
	 * @return
	 */
	public RenderBlock toRenderBlock(Block data);

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
	 * define what should happen if you alter the health. If =0 automatically
	 * get's destroyed after exiting this method.
	 *
	 * @param coord
	 * @param health the new health
	 * @param id
	 * @param value
	 */
	public void onSetHealth(Coordinate coord, byte health, byte id, byte value);

	/**
	 *
	 * @param block
	 * @param coord
	 * @return
	 */
	public AbstractBlockLogicExtension newLogicInstance(Block block, Coordinate coord);
}
