package com.BombingGames.WurfelEngine.Core.Gameobjects;

/**
 *
 * @author Benedikt Vogler
 */
public interface BlockFactory {
	
	public boolean isObstacle(int id, int value);

	public boolean isTransparent(int id, int value);
	
    /**
     * Check if the block is liquid.
	 * @param id
	 * @param value
     * @return true if liquid, false if not 
     */
	public boolean isLiquid(int id, int value);
	
    /**
     * Is the block a true block with sides or represents it another thing like a flower?
	 * @param id
	 * @param value
     * @return 
     */
	public boolean hasSides(int id, int value);

	public String getName(int id, int value);
}
