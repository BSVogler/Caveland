package com.BombingGames.WurfelEngine.Core.Gameobjects;

/**
 *Interface for objects whether they are blocks and entities.
 * @author Benedikt Vogler
 */
public interface HasID {
	
	/**
     * returns the id of a object
     * @return getId
     */
	public byte getId();
	
	/**
     * Get the value. It is like a sub-id and can identify the status.
     * @return in range [0;{@link AbstractGameObject#VALUESNUM}]. Is -1 if about to destroyed.
     */
	public byte getValue();

	/**
	 * How bright is the object?
	 * The lightlevel is a scale applied to the color. 1 is default value.
	 * @return 1 is default bright. 0 is black.
	 */
	float getLightlevel();

	/**
	 * Set the brightness of the object.
	 * The lightlevel is a scaling factor between.
	 * @param lightlevel 1 is default bright. 0 is black.
	 */
	void setLightlevel(float lightlevel);

	/**
	 * Can light travel through object?
	 * @return
	 */
	boolean isTransparent();
	
	    /**
     * Is this object an obstacle or can you pass through?
     * @return
     */
    public abstract boolean isObstacle();
	
	/**
	 * Read the name of a id value combination.
	 * @return the name for this block.
	 */
	public abstract String getName();
    
}
