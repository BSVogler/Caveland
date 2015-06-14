package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.Core2.Controller;
import com.bombinggames.wurfelengine.Core2.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.Core2.Gameobjects.Block;

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
