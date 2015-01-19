package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;

/**
 *
 * @author Benedikt Vogler
 */
public interface Interactable {
	
	/**
	 * interact with something in the world
	 * @param actor the thing which interacts with this
	 * @param view the view by which the interaction is caused and feedback is send to
	 */
	public void interact(AbstractEntity actor, GameView view);
	
	public void showButton();
	public void hideButton();
}
