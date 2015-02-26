package com.BombingGames.Caveland.Game;

import com.BombingGames.Caveland.GameObjects.Collectible;
import com.BombingGames.Caveland.GameObjects.Machine;
import com.BombingGames.Caveland.GameObjects.Tree;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.BlockFactory;
import com.BombingGames.WurfelEngine.Core.Gameobjects.ExplosiveBarrel;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.badlogic.gdx.Gdx;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomBlockFactory implements BlockFactory {

@Override
public Block produce(int id, int value) {
	Block block;
	switch (id){
		case 44:
			block = Block.createBasicInstance(id); //textureless
			block.setObstacle(true);
		break;  
		case 55:
			block = new RailBlock();
		break;
		case 56:
			block = new Ore(id, Collectible.ColTypes.GOLD);
		break;
		case 57:
			block = new Ore(id, Collectible.ColTypes.COAL);
		break;
		case 58:
			block = new Ore(id, Collectible.ColTypes.IRONORE);
		break;
		case 60:
			block = new Machine();
		break;
		case 70:
			block = Block.createBasicInstance(id); 
			block.setTransparent(true);
			block.setNoSides();
		break;
		case 71:
			block = new ExplosiveBarrel(id);
			block.setNoSides();
		break;
		case 72:
			block = new Tree(value);
		break;
//		case 72:
//			block = new AnimatedBlock(id, new int[]{1000,1000},true, true);//animation lighting
//			block.setObstacle(true);
//		break;
		default:
			Gdx.app.error("CustomBlockFactory", "Block "+id+" not defined.");
			block = Block.createBasicInstance(id);;
		}
		return block;
	}	

	@Override
	public String getName(int id, int value) {
		switch (id){
			case 40:
				return "Entity Spawner";
			case 44:
				return "textureless block";
		}
			
		return "too lazy";
	}



	private static class RailBlock extends Block {
		private static final long serialVersionUID = 1L;

		RailBlock() {
			super(55);
			setNoSides();
			setTransparent(true);
			setObstacle(false);
		}
		
		
	}

	private static class Ore extends Block {
		private final Collectible.ColTypes def;

		Ore(int id, Collectible.ColTypes def) {
			super(id);
			setObstacle(true);
			this.def = def;
		}

		@Override
		public void onDestroy(AbstractPosition pos) {
			new Collectible(def).spawn(pos.cpy().getPoint());
			new Collectible(def).spawn(pos.getPoint());
			new Collectible(def).spawn(pos.getPoint());
		}
		
		
	}
}
