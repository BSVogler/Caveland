package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public interface BlockFactory {
	
	public boolean isObstacle(byte id, byte value);

	public boolean isTransparent(byte id, byte value);
	
    /**
     * Check if the block is liquid.
	 * @param id
	 * @param value
     * @return true if liquid, false if not 
     */
	public boolean isLiquid(byte id, byte value);
	
    /**
     * Is the block a true block with sides or represents it another thing like a flower?
	 * @param id
	 * @param value
     * @return 
     */
	public boolean hasSides(byte id, byte value);

	public String getName(byte id, byte value);
	
	public RenderBlock toRenderBlock(byte id, byte value);
	
	/**
	 * define what should happen if you alter the health. If =0 automatically get's destroyed on exiting this method.
	 * @param coord
	 * @param health the new health
	 * @param id 
	 * @param value 
	 */
	public void setHealth(Coordinate coord, byte health, byte id, byte value);
}
