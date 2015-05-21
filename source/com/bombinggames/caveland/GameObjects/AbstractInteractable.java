package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.Core.GameView;
import com.bombinggames.wurfelengine.Core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.Core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.Core.Gameobjects.SimpleEntity;

/**
 *
 * @author Benedikt Vogler
 */
public abstract class AbstractInteractable extends MovableEntity {

	private static final long serialVersionUID = 1L;
	private transient SimpleEntity interactButton = null;

	/**
	 * Xbox controller sprite values.
	 */
	private static final byte AUp = 0;

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
	 *
	 * @param id
	 * @param spritesPerDir
	 */
	public AbstractInteractable(byte id, int spritesPerDir) {
		super(id, spritesPerDir);
	}

	/**
	 *
	 * @param entity
	 */
	public AbstractInteractable(MovableEntity entity) {
		super(entity);
	}

	/**
	 * interact with something in the world
	 *
	 * @param actor the thing which interacts with this
	 * @param view the view by which the interaction is caused and feedback is
	 * send to
	 */
	public abstract void interact(AbstractEntity actor, GameView view);

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}
}