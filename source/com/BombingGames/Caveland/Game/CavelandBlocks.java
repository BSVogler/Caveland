package com.BombingGames.Caveland.Game;

import com.BombingGames.Caveland.GameObjects.Collectible;
import com.BombingGames.Caveland.GameObjects.CustomTree;
import com.BombingGames.Caveland.GameObjects.Machine;
import com.BombingGames.WurfelEngine.Core.Gameobjects.BlockDirt;
import com.BombingGames.WurfelEngine.Core.Gameobjects.CustomBlocks;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author Benedikt Vogler
 */
public class CavelandBlocks implements CustomBlocks {

@Override
public RenderBlock toRenderBlock(byte id, byte value) {
	RenderBlock block;
	switch (id){
		case 60:
			block = new Machine();
		break;
		case 72:
			block = new CustomTree(value);
		break;
//		case 72:
//			block = new AnimatedBlock(id, new int[]{1000,1000},true, true);//animation lighting
//			block.setObstacle(true);
//		break;
		default:
			Gdx.app.error("CustomBlockFactory", "Block "+id+" not defined.");
			block = new RenderBlock(id);
		}
		return block;
	}	

	@Override
	public String getName(byte id, byte value) {
		switch (id){
			case 40:
				return "Entity Spawner";
			case 44:
				return "textureless block";
			case 55:
				return "rails";	
		}
			
		return "not named yet";
	}

	@Override
	public boolean isObstacle(byte id, byte value) {
		if (id>=41 && id<=44)
			return true;
		return false;
	}

	@Override
	public boolean isTransparent(byte id, byte value) {
		if (id==55) return true;
		return false;
	}

	@Override
	public boolean isLiquid(byte id, byte value) {
		return false;
	}

	@Override
	public boolean hasSides(byte id, byte value) {
		if (id==55) return false;
		return true;
	}

	@Override
	public void setHealth(Coordinate coord, byte health, byte id, byte value) {
		if (health <= 0 ){
			if (id==41) {
				Collectible.create(Collectible.CollectibleType.CRISTALL).spawn(coord.getPoint().cpy());
			} else if (id==42){
				Collectible.create(Collectible.CollectibleType.SULFUR).spawn(coord.getPoint().cpy());
			} else if (id==43){
				Collectible.create(Collectible.CollectibleType.IRONORE).spawn(coord.getPoint().cpy());
			} else if (id==44){
				Collectible.create(Collectible.CollectibleType.COAL).spawn(coord.getPoint().cpy());
			}
			
			//view only relevant. should only be done if visible
			//todo, check if visible
			for (int i = 0; i < 10; i++) {
				MovableEntity dirt = (MovableEntity) new BlockDirt().spawn(coord.getPoint().cpy());
				dirt.addMovement(new Vector3((float) Math.random()-0.5f, (float) Math.random()-0.5f,(float) Math.random()*5f));
				dirt.setRotation((float) Math.random()*360);
			}
		}
	}
	
}
