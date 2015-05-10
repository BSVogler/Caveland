package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;

/**
 *
 * @author Benedikt Vogler
 */
public class Bausatz extends Collectible {
	private static final long serialVersionUID = 1L;

	public Bausatz() {
		super(CollectibleType.TOOLKIT);
	}

	@Override
	public void action(AbstractEntity actor) {
		Controller.getSoundEngine().play("robotHit", getPosition());
		actor.getPosition().getCoord().setBlock(CoreData.getInstance((byte) 11));
	}
	
	
}
