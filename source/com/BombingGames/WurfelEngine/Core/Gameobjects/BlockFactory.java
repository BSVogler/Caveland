package com.BombingGames.WurfelEngine.Core.Gameobjects;

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
}
