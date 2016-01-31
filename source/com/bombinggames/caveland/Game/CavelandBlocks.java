package com.bombinggames.caveland.Game;

import com.bombinggames.caveland.GameObjects.GrassBlock;
import com.bombinggames.caveland.GameObjects.ConstructionSiteRender;
import com.bombinggames.caveland.GameObjects.CustomTree;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.caveland.GameObjects.logicblocks.BoosterLogic;
import com.bombinggames.caveland.GameObjects.logicblocks.CableBlock;
import com.bombinggames.caveland.GameObjects.logicblocks.CaveEntryBlockLogic;
import com.bombinggames.caveland.GameObjects.logicblocks.ConstructionSite;
import com.bombinggames.caveland.GameObjects.logicblocks.Flagpole;
import com.bombinggames.caveland.GameObjects.logicblocks.LiftLogic;
import com.bombinggames.caveland.GameObjects.logicblocks.LiftLogicGround;
import com.bombinggames.caveland.GameObjects.logicblocks.OvenLogic;
import com.bombinggames.caveland.GameObjects.logicblocks.PowerStationLogic;
import com.bombinggames.caveland.GameObjects.logicblocks.PowerTorch;
import com.bombinggames.caveland.GameObjects.logicblocks.RobotFactory;
import com.bombinggames.caveland.GameObjects.logicblocks.Turret;
import com.bombinggames.wurfelengine.WE;
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

		/**
		 *
		 */
		CONSTRUCTIONSITE((byte) 11, "Construction Site", false),

		/**
		 *
		 */
		OVEN((byte) 12, "Oven", false),

		/**
		 *
		 */
		TORCH((byte) 13, "Torch", false),

		/**
		 *
		 */
		POWERSTATION((byte) 14, "Power Station", false),

		/**
		 *
		 */
		LIFT((byte) 15, "Lift", false),

		/**
		 *
		 */
		ENTRY((byte) 16, "Cave Entry", true),

		/**
		 *
		 */
		INDESTRUCTIBLEOBSTACLE((byte) 17, "Indestructible Obstacle", true),
		
		/**
		 *
		 */
		LIFT_Ground((byte) 18, "Lift (Ground)", true),

		/**
		 *
		 */
		CRYSTAL((byte) 41, "Crystal Block", true),

		/**
		 *
		 */
		SULFUR((byte) 42, "Sulfur Block", true),

		/**
		 *
		 */
		IRONORE((byte) 43, "Iron Ore Block", true),

		/**
		 *
		 */
		COAL((byte) 44, "Coal Block", true),

		/**
		 *
		 */
		TURRET((byte) 52, "Turret", false),

		/**
		 *
		 */
		ROBOTFACTORY((byte) 53, "robot factory", false),

		/**
		 *
		 */
		POWERCABLE((byte) 54, "power cable", false),

		/**
		 *
		 */
		RAILS((byte) 55, "rails", false),

		/**
		 *
		 */
		RAILSBOOSTER((byte) 56, "booster rails", false),
		
		/**
		 *
		 */
		FLAGPOLE((byte) 60, "flag pole", false),

		/**
		 *
		 */
		TREE((byte) 72, "tree", false),

		/**
		 *
		 */
		UNDEFINED((byte) -1, "undefined", true);
		
		/**
		 * reverse loookup
		 * @param id
		 * @return 
		 */
		public static CLBlocks valueOf(byte id){
			switch (id) {
				case 11:
					return CONSTRUCTIONSITE;
				case 12:
					return OVEN;
				case 13:
					return TORCH;
				case 14:
					return POWERSTATION;
				case 15:
					return LIFT;
				case 16:
					return ENTRY;
				case 17:
					return INDESTRUCTIBLEOBSTACLE;
				case 18:
					return LIFT_Ground;
				case 41:
					return CRYSTAL;
				case 42:
					return SULFUR;
				case 43:
					return IRONORE;
				case 44:
					return COAL;
				case 52:
					return TURRET;
				case 53:
					return ROBOTFACTORY;	
				case 54:
					return POWERCABLE;
				case 55:
					return RAILS;
				case 56:
					return RAILSBOOSTER;
				case 60:
					return FLAGPOLE;
				case 72:
					return TREE;
				default:
					return UNDEFINED;
			}
		}
		
		private final byte id;
		private final String name;
		private final boolean hasSides;

		/**
		 * 
		 * @param id
		 * @param name
		 * @param hasSides rendered with sides or without
		 */
		private CLBlocks(byte id, String name, boolean hasSides) {
			this.id = id;
			this.name = name;
			this.hasSides = hasSides;
		}

		@Override
		public String toString(){
			return name;
		}
		
		/**
		 *
		 * @return
		 */
		public boolean hasSides() {
			return hasSides;
		}

		/**
		 *
		 * @return
		 */
		final public byte getId() {
			return id;
		}

		/**
		 *
		 * @return
		 */
		public Block getInstance() {
			return Block.getInstance(id);
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
		if (id == 60) {
			return true;
		}
		if (id == 3) {
			return true;//stone
		}
		return false;
	}
	
	public static CollectibleType getLoot(byte id) {
		switch (id) {
			case 3:
				return CollectibleType.Stone;
			case 41:
				return CollectibleType.Cristall;
			case 42:
				return CollectibleType.Sulfur;
			case 43:
				return CollectibleType.Ironore;
			case 44:
				return CollectibleType.Coal;
			case 72:
				return CollectibleType.Wood;
		}
		return null;
	}
	
	//overwrites
	@Override
	public RenderBlock toRenderBlock(Block data) {
		byte dataId = data.getId();
		if (dataId == 1) {
			GrassBlock grass = new GrassBlock(data);
			return grass;
		} else if (dataId == CLBlocks.INDESTRUCTIBLEOBSTACLE.id) {
			RenderBlock iO = new RenderBlock(data);
			if (data.getValue()> 0){
				iO.setSpriteId((byte) 3);
			}
			return iO;
		} else if (dataId == CLBlocks.TREE.id) {
			return new CustomTree(data);
		} else if (dataId == CLBlocks.CONSTRUCTIONSITE.id) {
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
		return CLBlocks.valueOf(id).hasSides;
	}

	@Override
	public boolean isObstacle(byte id, byte value) {
		if (id==12) return true;
		if (id==13) return false;
		if (id==14) return true;
		if (id == 16 && value == 1) return true;
		if (id==17) return true;
		if (id==18) return true;
		if (id==11) return false;
		if (id>=41 && id<=44)//ores
			return true;
		if (id==72) return true;
		if (id==46) return true;
		if (id==52) return true;
		if (id==60) return true;
		return false;
	}

	@Override
	public boolean isTransparent(byte id, byte value) {
		if (id==12) return true;
		if (id==13) return true;
		if (id==14) return true;
		if (id==15) return true;
		if (id==17) return true;
		if (id==11) return true;
		if (id==52) return true;
		if (id==53) return true;
		if (id==54) return true;
		if (id==55) return true;
		if (id==56) return true;
		if (id==60) return true;
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
			CollectibleType lootType = getLoot(id);
			if (lootType != null) {
				lootType.createInstance().spawn(coord.toPoint());
			}
			if (id == CLBlocks.TREE.id) {
				//destroy other half
				Coordinate otherHalf;
				if (value == CustomTree.TREETOPVALUE) {
					otherHalf = coord.cpy().add(0, 0, -1);
				} else {
					otherHalf = coord.cpy().add(0, 0, 1);
				}

				if (otherHalf.getBlock() != null && otherHalf.getBlock().getId() == id) {
					otherHalf.destroy();
				}
			}
			WE.SOUND.play("blockDestroy", coord);//to-do should be a wood chop down sound for tree

			//view only relevant. should only be done if visible
			//todo, check if visible
			for (int i = 0; i < 10; i++) {
				new DestructionParticle((byte) 44).spawn(coord.toPoint());
			}
		}
	}

	@Override
	public AbstractBlockLogicExtension newLogicInstance(Block block, Coordinate coord) {
		byte id = block.getId();
		if (id == CLBlocks.ENTRY.id) {
			return new CaveEntryBlockLogic(block, coord);
		}
		if (id == CLBlocks.CONSTRUCTIONSITE.id) {
			return new ConstructionSite(block, coord);
		}
		if (id == CLBlocks.OVEN.id) {
			return new OvenLogic(block, coord);
		}
		if (id == CLBlocks.POWERSTATION.id) {
			return new PowerStationLogic(block, coord);
		}
		if (id == CLBlocks.LIFT.id) {
			return new LiftLogic(block, coord);
		}
		
		if (id == CLBlocks.LIFT_Ground.id) {
			return new LiftLogicGround(block, coord);
		}
		if (id == CLBlocks.ROBOTFACTORY.id) {
			return new RobotFactory(block, coord);
		}
		if (id == CLBlocks.POWERCABLE.id) {
			return new CableBlock(block, coord);
		}
		if (id == CLBlocks.RAILSBOOSTER.id) {
			return new BoosterLogic(block, coord);
		}
		if (id == CLBlocks.TURRET.id) {
			return new Turret(block, coord);
		}
		if (id == CLBlocks.TORCH.id) {
			return new PowerTorch(block, coord);
		}
		if (id == CLBlocks.FLAGPOLE.id) {
			return new Flagpole(block, coord);
		}
		return null;
	}
}
