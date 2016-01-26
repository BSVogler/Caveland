package com.bombinggames.wurfelengine.core.Gameobjects;

/**
 *Interface for objects whether they are blocks and entities.
 * @author Benedikt Vogler
 */
public interface Renderable {
	
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
}
