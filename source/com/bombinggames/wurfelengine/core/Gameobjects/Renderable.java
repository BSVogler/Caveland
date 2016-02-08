package com.bombinggames.wurfelengine.core.gameobjects;

import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.map.Position;
import com.bombinggames.wurfelengine.core.map.rendering.RenderStorage;
import java.util.ArrayList;

/**
 *Interface for objects whether they are blocks and entities.
 * @author Benedikt Vogler
 */
public interface Renderable {
	
	/**
	 * the id of the sprite using for rendering.<br>
	 * By default is the same as the object id but in some cases some
	 * objects share one sprite so they can have the same.
	 *
	 * @return if spritevalue is not custom set uses value.
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
	 * Marked as visited.
	 */
	public void markPermanent();

	/**
	 *
	 * @return
	 */
	public boolean isMarked();
	
	/**
	 *
	 */
	public void unmarkTemporarily();
	
	/**
	 * Mark temporarily for depth sort.
	 */
	public void markTemporarily();

	/**
	 *
	 * @return
	 */
	public boolean isMarkedTemporarily();

	/**
	 * Draws an object if it is not hidden and not clipped.
	 *
	 * @param view the view using this render method
	 * @param camera The camera rendering the scene
	 */
	public void render(GameView view, Camera camera);

	/**
	 * Return the coordinates of the object in the game world.
	 *
	 * @return Reference to the position object which points to the location in
	 * the game world.
	 */
	public Position getPosition();

	/**
	 *
	 * @param camera
	 * @return
	 */
	public boolean shouldBeRendered(Camera camera);

	/**
	 * get the stuff which must be rendered before
	 * @param rs
	 * @return 
	 */
	public ArrayList<Renderable> getCovered(RenderStorage rs);
}
