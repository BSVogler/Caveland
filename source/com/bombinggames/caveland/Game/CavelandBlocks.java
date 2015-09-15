package com.bombinggames.caveland.Game;

import com.bombinggames.caveland.GameObjects.ConstructionSiteRender;
import com.bombinggames.caveland.GameObjects.CustomTree;
import com.bombinggames.caveland.GameObjects.Torch;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.caveland.GameObjects.logicblocks.ConstructionSite;
import com.bombinggames.caveland.GameObjects.logicblocks.LiftLogic;
import com.bombinggames.caveland.GameObjects.logicblocks.OvenLogic;
import com.bombinggames.caveland.GameObjects.logicblocks.PortalBlockLogic;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.CustomBlocks;
import com.bombinggames.wurfelengine.core.Gameobjects.DestructionParticle;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class CavelandBlocks implements CustomBlocks {

	/**
	 *
	 */
	public enum CLBlocks {
		CONSTRUCTIONSITE((byte) 11, "Construction Site", false),
		OVEN((byte) 12, "Oven", false),
		TORCH((byte) 13, "Torch", false),
		POWERSTATION((byte) 14, "Power Station", false),
		LIFT((byte) 15, "Lift", false),
		ENTRY((byte) 16, "Cave Entry", true),
		INDESTRUCTIBLEOBSTACLE((byte) 17, "Indestructible Obstacle", false),
		CRYSTAL((byte) 41, "Crystal Block", true),
		SULFUR((byte) 42, "Sulfur Block", true),
		IRONORE((byte) 43, "Iron Ore Block", true),
		COAL((byte) 44, "Coal Block", true),
		RAILS((byte) 55, "rails", false),
		TREE((byte) 72, "tree", false),
		UNDEFINED((byte) -1, "undefined", true);
		
		/**
		 * reverse loookup
		 * @param id
		 * @return 
		 */
		public static CLBlocks valueOf(byte id){
			if (id==11) return CONSTRUCTIONSITE;
			else if (id==12) return OVEN;
			else if (id==13) return TORCH;
			else if (id==14) return POWERSTATION;
			else if (id==15) return LIFT;
			else if (id==16) return ENTRY;
			else if (id==17) return INDESTRUCTIBLEOBSTACLE;
			else if (id==41) return CRYSTAL;
			else if (id==42) return SULFUR;
			else if (id==43) return IRONORE;
			else if (id==44) return COAL;
			else if (id==55) return RAILS;
			else if (id==72) return TREE;
			else return UNDEFINED;
		}
		
		private final byte id;
		private final String name;
		private final boolean hasSides;

		private CLBlocks(byte id, String name, boolean hasSides) {
			this.id = id;
			this.name = name;
			this.hasSides = hasSides;
		}

		@Override
		public String toString(){
			return name;
		}
		
		public boolean hasSides() {
			return hasSides;
		}

		final public byte getId() {
			return id;
		}

		public Block getInstance() {
			return Block.getInstance(id);
		}

	}
	@Override
	public RenderBlock toRenderBlock(Block data) {
		if (data.getId() == CLBlocks.INDESTRUCTIBLEOBSTACLE.id) {
			RenderBlock a = new RenderBlock(data);
			if (data.getValue() > 0){
				a.setSpriteId((byte) 3);
			}
			return a;
		} else if (data.getId() == CLBlocks.TORCH.id) {
			return new Torch(data);
		} else if (data.getId() == CLBlocks.TREE.id) {
			return new CustomTree(data);
		} else if (data.getId() == CLBlocks.CONSTRUCTIONSITE.id) {
			return new ConstructionSiteRender(data);
		}else {
			return new RenderBlock(data);
		}
	}

	@Override
	public String getName(byte id, byte value) {
		return CLBlocks.valueOf(id).toString();
	}

	@Override
	public boolean hasSides(byte id, byte value) {
		if (id == CLBlocks.INDESTRUCTIBLEOBSTACLE.id && value > 0) {
			return true;
		}
		return CLBlocks.valueOf(id).hasSides;
	}

	@Override
	public boolean isObstacle(byte id, byte value) {
		if (id==12) return true;
		if (id==13) return false;
		if (id==14) return true;
		if (id == 16 && value == 1) return true;
		if (id==17) return true;
		if (id==11) return false;
		if (id>=41 && id<=44)//ores
			return true;
		if (id==72) return true;
		if (id==46) return true;
			return false;
		}

	@Override
	public boolean isTransparent(byte id, byte value) {
		if (id==12) return true;
		if (id==13) return true;
		if (id==14) return true;
		if (id==17) return true;
		if (id==11) return true;
		if (id==55) return true;
		if (id==72) return true;
		return false;
	}

	@Override
	public boolean isLiquid(byte id, byte value) {
		return false;
	}

	@Override
	public boolean isIndestructible(byte id, byte value) {
		return id == CLBlocks.INDESTRUCTIBLEOBSTACLE.id;
	}

	
	@Override
	public void onSetHealth(Coordinate coord, byte health, byte id, byte value) {
		if (health <= 0) {
			switch (id) {
				case 3:
					CollectibleType.Stone.createInstance().spawn(coord.toPoint());
					break;
				case 41:
					CollectibleType.Cristall.createInstance().spawn(coord.toPoint());
					break;
				case 42:
					CollectibleType.Sulfur.createInstance().spawn(coord.toPoint());
					break;
				case 43:
					CollectibleType.Ironore.createInstance().spawn(coord.toPoint());
					break;
				case 44:
					CollectibleType.Coal.createInstance().spawn(coord.toPoint());
					break;
				case 72:
					CollectibleType.Wood.createInstance().spawn(coord.toPoint());
					break;
				default:
			}

			//view only relevant. should only be done if visible
			//todo, check if visible
			for (int i = 0; i < 10; i++) {
				new DestructionParticle((byte) 44).spawn(coord.toPoint());
			}
		}
	}

	//Caveland specific details
	/**
	 * indestructable by diggin
	 *
	 * @param id
	 * @return
	 */
	public static boolean hardMaterial(byte id) {
		if (id == 17) {
			return true;
		}
		if (id == 41) {
			return true;
		}
		if (id == 43) {
			return true;
		}
		if (id == 3) {
			return true;//stone
		}
		return false;
	}

	@Override
	public AbstractBlockLogicExtension newLogicInstance(Block block, Coordinate coord) {
		if (block.getId() == 16) {
			return new PortalBlockLogic(block, coord);
		}
		if (block.getId() == 11) {
			return new ConstructionSite(block, coord);
		}
		if (block.getId() == 12) {
			return new OvenLogic(block, coord);
		}
		if (block.getId() == 15) {
			return new LiftLogic(block, coord);
		}
		return null;
	}
}
