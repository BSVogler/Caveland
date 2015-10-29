package com.bombinggames.caveland.GameObjects;

import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Point;

/**
 * A movable entity with special caveland logic.
 * @author Benedikt Vogler
 */
public class CLMovableEntity extends MovableEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param id
	 * @param spritesPerDir The number of animation sprites per walking direction. if 0 then it only uses the value 0
	 */
	public CLMovableEntity(byte id, int spritesPerDir) {
		super(id, spritesPerDir);
	}

	/**
	 * 
	 * @param entity 
	 */
	public CLMovableEntity(MovableEntity entity) {
		super(entity);
	}

	@Override
	public boolean collidesHorizontal(Point pos, float colissionRadius) {
		//check for total height blocking blocks
		for (int z = 0; z < Chunk.getBlocksZ(); z++) {
			Point checkpos = pos.cpy();
			checkpos.setZ(Block.GAME_EDGELENGTH*z);//set height
			
			Block block = checkpos.addVector(0, -colissionRadius, 0).getBlock();
			if (block != null
				&& block.getId() == CavelandBlocks.CLBlocks.INDESTRUCTIBLEOBSTACLE.getId()) {
				return true;
			}
			block = checkpos.addVector(0, 2*colissionRadius, 0).getBlock();
			if (block != null
				&& block.getId() == CavelandBlocks.CLBlocks.INDESTRUCTIBLEOBSTACLE.getId()) {
				return true;
			}
			block = checkpos.addVector(-colissionRadius, -colissionRadius, 0).getBlock();
			if (block != null
				&& block.getId() == CavelandBlocks.CLBlocks.INDESTRUCTIBLEOBSTACLE.getId()) {
				return true;
			}
			block = checkpos.addVector(2*colissionRadius, 0, 0).getBlock();
			if (block != null
				&& block.getId() == CavelandBlocks.CLBlocks.INDESTRUCTIBLEOBSTACLE.getId()) {
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
