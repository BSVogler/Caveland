package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock.BlockDestructionAction;
import com.BombingGames.WurfelEngine.Core.Gameobjects.BlockDirt;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomBlockDestructionAction implements BlockDestructionAction {

	@Override
	public void action(RenderBlock block) {
		for (int i = 0; i < 10; i++) {
			MovableEntity dirt = (MovableEntity) new BlockDirt().spawn(block.getPosition().getPoint().cpy());
			dirt.addMovement(new Vector3((float) Math.random()-0.5f, (float) Math.random()-0.5f,(float) Math.random()*5f));
			dirt.setRotation((float) Math.random()*360);
		}
	}
	
}
