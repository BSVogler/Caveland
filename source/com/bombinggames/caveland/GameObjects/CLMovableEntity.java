package com.bombinggames.caveland.GameObjects;

import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Point;

/**
 *
 * @author Benedikt Vogler
 */
public class CLMovableEntity extends MovableEntity {

	private static final long serialVersionUID = 1L;

	public CLMovableEntity(byte id, int spritesPerDir) {
		super(id, spritesPerDir);
	}

	public CLMovableEntity(MovableEntity entity) {
		super(entity);
	}

	@Override
	public boolean collidesHorizontal(Point pos, float colissionRadius) {
		//check for total height blocking blocks
		int x = pos.toCoord().getX();
		int y = pos.toCoord().getY();
		for (int z = 0; z < Chunk.getBlocksZ(); z++) {
			Block block = Controller.getMap().getBlock(x, y, z);
			if (
				block != null
				&& block.getId() == CavelandBlocks.CLBlocks.INDESTRUCTIBLEOBSTACLE.getId()//dornbusch
			) {
				return true;
			}
		}
		//do regular check
		return super.collidesHorizontal(pos, colissionRadius);
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
