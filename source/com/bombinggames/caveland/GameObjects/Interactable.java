package com.bombinggames.caveland.gameobjects;

import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.wurfelengine.core.map.Position;

/**
 *
 * @author Benedikt Vogler
 */
public interface Interactable {
	/**
	 * Xbox controller sprite values.
	 */
	public static final byte AUp = 0;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte ADown = 1;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte BUp = 2;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte BDown = 3;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte KeyUp = 4;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte KeyLeft = 5;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte KeyDown = 6;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte KeyRight = 7;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte LB = 8;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte LT = 9;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte RB = 10;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte RT = 11;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte SELECT = 12;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte START = 13;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte XUp = 14;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte XDown = 15;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte YUp = 16;

	/**
	 *Xbox controller sprite values.
	 */
	public static final byte YDown = 17;
	
	/**
	 * interact with something in the world
	 *
	 * @param actor the thing which interacts with this
	 * @param view the view by which the interaction is caused and feedback is
	 * send to
	 */
	public abstract void interact(CLGameView view, com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity actor);
	
	/**
	 * 
	 * @return 
	 */
	public abstract Position getPosition();
	
	/**
	 * Is the interaction active or deactivated?
	 * @return true if it interactable
	 */
	public abstract boolean interactable();
	
	/**
	 * Is it only interactable if you pick it up?
	 * @return true if only usable when in inventory
	 */
	public abstract boolean interactableOnlyWithPickup();
}
