package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;

/**
 *
 * @author Benedikt Vogler
 */
public class Spaceship extends MovableEntity {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public Spaceship() {
		super(80,0);
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
