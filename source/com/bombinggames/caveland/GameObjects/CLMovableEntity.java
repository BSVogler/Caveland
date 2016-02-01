package com.bombinggames.caveland.gameobjects;

import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Point;

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

	public CLMovableEntity(byte id, int spritesPerDir, boolean shadow) {
		super(id, spritesPerDir, shadow);
	}

	/**
	 * 
	 * @param entity 
	 */
	public CLMovableEntity(MovableEntity entity) {
		super(entity);
	}

	@Override
	public boolean collidesWithWorld(Point pos, float colissionRadius) {
		//check for total height blocking blocks
		for (int z = 0; z < Chunk.getBlocksZ(); z++) {
			Point checkpos = pos.cpy();
			checkpos.setZ(Block.GAME_EDGELENGTH*z);//set height
			
			Block block = checkpos.add(0, -colissionRadius, 0).getBlock();
			if (block != null
				&& block.getId() == CavelandBlocks.CLBlocks.INDESTRUCTIBLEOBSTACLE.getId()) {
				return true;
			}
			block = checkpos.add(0, 2*colissionRadius, 0).getBlock();
			if (block != null
				&& block.getId() == CavelandBlocks.CLBlocks.INDESTRUCTIBLEOBSTACLE.getId()) {
				return true;
			}
			block = checkpos.add(-colissionRadius, -colissionRadius, 0).getBlock();
			if (block != null
				&& block.getId() == CavelandBlocks.CLBlocks.INDESTRUCTIBLEOBSTACLE.getId()) {
				return true;
			}
			block = checkpos.add(2*colissionRadius, 0, 0).getBlock();
			if (block != null
				&& block.getId() == CavelandBlocks.CLBlocks.INDESTRUCTIBLEOBSTACLE.getId()) {
				return true;
			}
		}
		
		
		//do regular check
		return super.collidesWithWorld(pos, colissionRadius);
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
