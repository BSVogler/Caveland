package com.BombingGames.WurfelEngine.Core.Gameobjects;

/**
 *
 * @author Benedikt Vogler
 */
public interface HasID {
	/**
     * returns the id of a object
     * @return getId
     */
	public int getId();
	
	/**
     * Get the value. It is like a sub-id and can identify the status.
     * @return in range [0;{@link #VALUESNUM}]. Is -1 if about to destroyed.
     */
	public int getValue();

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
}
