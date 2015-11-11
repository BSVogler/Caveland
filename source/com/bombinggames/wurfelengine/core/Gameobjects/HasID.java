package com.bombinggames.wurfelengine.core.Gameobjects;

/**
 *Interface for objects whether they are blocks and entities.
 * @author Benedikt Vogler
 */
public interface HasID {
	
	/**
     * returns the id of a object
     * @return 
     */
	public byte getSpriteId();
	
	/**
     * Get the value. It is like a sub-id and can identify the status.
     * @return in range [0;{@link Block#VALUESNUM}]. Is -1 if about to destroyed.
     */
	public byte getSpriteValue();
	
	/**
     * Set the value.
     * @param value in range [0;{@link Block#VALUESNUM}]. Is -1 if about to destroyed.
     */
	public void setSpriteValue(byte value);

	/**
	 * How bright is the object?
	 * The lightlevel is a scale applied to the color. 1 is default value.
	 * @return 1 is default bright. 0 is black.
	 */
	float getLightlevelR();
	/**
	 * How bright is the object?
	 * The lightlevel is a scale applied to the color. 1 is default value.
	 * @return 1 is default bright. 0 is black.
	 */
	float getLightlevelG();
	/**
	 * How bright is the object?
	 * The lightlevel is a scale applied to the color. 1 is default value.
	 * @return 1 is default bright. 0 is black.
	 */
	float getLightlevelB();

	/**
	 * Set the brightness of the object.
	 * The lightlevel is a scaling factor. 1 is default value.
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
	 * Is the block a true block with three sides or does it get rendered by a single sprite?<br>
	 * This field is only used for representation (view) related data.<br>
	 * Only used for blocks. Entities should return <i>false</i>.
	 * @return <i>true</i> if it has sides, <i>false</i> if is rendered as a single sprite
	 */
	public abstract boolean hasSides();
	
	/**
	 * A liquid has some physical properties which differ from solid blocks. The surface is rendered only once.
	 * @return 
	 */
	public abstract boolean isLiquid();
	
	/**
	 * Read the name of a id value combination.
	 * @return the name for this block.
	 */
	public abstract String getName();
	
	/**
	 * 
	 * @return 
	 */
	public abstract boolean isIndestructible();
    
}
