package com.BombingGames.Caveland.Game;

import com.BombingGames.Caveland.GameObjects.Collectible;
import com.BombingGames.Caveland.GameObjects.CustomTree;
import com.BombingGames.Caveland.GameObjects.Machine;
import com.BombingGames.WurfelEngine.Core.Gameobjects.BlockFactory;
import com.BombingGames.WurfelEngine.Core.Gameobjects.ExplosiveBarrel;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.badlogic.gdx.Gdx;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomBlockFactory implements BlockFactory {

@Override
public RenderBlock produce(int id, int value) {
	RenderBlock block;
	switch (id){
		case 41:
			block = new Ore(id, Collectible.CollectibleType.CRISTALL);
		break;
		case 42:
			block = new Ore(id, Collectible.CollectibleType.EXPLOSIVES);
		break;
		case 43:
			block = new Ore(id, Collectible.CollectibleType.IRONORE);
		break;	
		case 44:
			block = new Ore(id, Collectible.CollectibleType.COAL);
		break;
		case 46://sand
			block = new RenderBlock(id);
		break;  
		case 55:
			block = new RailBlock();
		break;
		case 60:
			block = new Machine();
		break;
		case 70:
			block = new RenderBlock(id); 
			//block.setNoSides();
		break;
		case 71:
			block = new ExplosiveBarrel(id);
			//block.setNoSides();
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
	public String getName(int id, int value) {
		switch (id){
			case 40:
				return "Entity Spawner";
			case 44:
				return "textureless block";
		}
			
		return "too lazy";
	}



	private static class RailBlock extends RenderBlock {
		private static final long serialVersionUID = 1L;

		RailBlock() {
			super(55);
			//setNoSides();
		}
		
		
	}

	private static class Ore extends RenderBlock {
		private final Collectible.CollectibleType def;

		Ore(int id, Collectible.CollectibleType def) {
			super(id);
			this.def = def;
		}

		@Override
		public void onDestroy() {
			Collectible.create(def).spawn(getPosition().getPoint().cpy());
			Collectible.create(def).spawn(getPosition().getPoint().cpy());
			Collectible.create(def).spawn(getPosition().getPoint().cpy());
		}
		
		
	}
}
