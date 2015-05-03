package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import static com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject.GAME_EDGELENGTH;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.SimpleEntity;

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
	public void update(float dt) {
		super.update(dt);
		if (interactButton != null) {
			interactButton.setPosition(getPosition().cpy().addVector(0, 0, GAME_EDGELENGTH * 1.5f));
		}
	}

	/**
	 * display the interact button
	 * @param buttonID
	 */
	public void showButton(byte buttonID) {
		if (interactButton == null) {
			interactButton = (SimpleEntity) new SimpleEntity((byte) 23, buttonID).spawn(
				getPosition().cpy().addVector(0, 0, GAME_EDGELENGTH)
			);
			interactButton.setLightlevel(1);
			interactButton.setSaveToDisk(false);
		}
	}

	/**
	 * hide the interact button
	 */
	public void hideButton() {
		if (interactButton != null) {
			interactButton.dispose();
			interactButton = null;
		}
	}

	@Override
	public void disposeFromMap() {
		super.disposeFromMap();
		if (interactButton != null) {
			interactButton.dispose();
			interactButton = null;
		}
	}

}
