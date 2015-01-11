package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;

/**
 *
 * @author Benedikt Vogler
 */
public interface Interactable {
	
	public void interact(AbstractEntity actor);
	
	public void showButton();
	public void hideButton();
}
