package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;

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
		actor.getPosition().toCoord().setBlock(Block.getInstance((byte) 11));
		dispose();
	}

	
}
