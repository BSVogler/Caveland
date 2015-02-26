package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block.BlockDestructionAction;
import com.BombingGames.WurfelEngine.Core.Gameobjects.BlockDirt;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomBlockDestructionAction implements BlockDestructionAction {

	@Override
	public void action(AbstractPosition pos) {
		for (int i = 0; i < 10; i++) {
			MovableEntity dirt = (MovableEntity) new BlockDirt().spawn(pos.getPoint().cpy());
			dirt.addMovement(new Vector3((float) Math.random()-0.5f, (float) Math.random()-0.5f,(float) Math.random()*5f));
		}
	}
	
}
