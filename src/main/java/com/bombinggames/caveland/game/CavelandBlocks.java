package com.bombinggames.caveland.game;

import com.bombinggames.caveland.gameobjects.ConstructionSiteRender;
import com.bombinggames.caveland.gameobjects.CustomTree;
import com.bombinggames.caveland.gameobjects.GrassBlock;
import com.bombinggames.caveland.gameobjects.collectibles.CollectibleType;
import com.bombinggames.caveland.gameobjects.logicblocks.BoosterLogic;
import com.bombinggames.caveland.gameobjects.logicblocks.CableBlock;
import com.bombinggames.caveland.gameobjects.logicblocks.CaveEntryBlockLogic;
import com.bombinggames.caveland.gameobjects.logicblocks.ConstructionSite;
import com.bombinggames.caveland.gameobjects.logicblocks.Flagpole;
import com.bombinggames.caveland.gameobjects.logicblocks.LiftLogic;
import com.bombinggames.caveland.gameobjects.logicblocks.LiftLogicGround;
import com.bombinggames.caveland.gameobjects.logicblocks.OvenLogic;
import com.bombinggames.caveland.gameobjects.logicblocks.PowerStationLogic;
import com.bombinggames.caveland.gameobjects.logicblocks.PowerTorch;
import com.bombinggames.caveland.gameobjects.logicblocks.RobotFactory;
import com.bombinggames.caveland.gameobjects.logicblocks.Turret;
import com.bombinggames.wurfelengine.core.map.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.map.BlockConfig;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;

/**
 * Using this class registers the logicblocks.
 * @see RenderCell#setCustomBlocks(com.bombinggames.wurfelengine.core.map.CustomBlocks) 
 * @author Benedikt Vogler
 */
public class CavelandBlocks extends BlockConfig {

	static {
		//this could be refacoted to ClBlocks constructor but then here a simple call to CLBlocks.values() is needed so the enums are initialized
		for (CLBlocks blockdef : CLBlocks.values()) {
			if (blockdef.logicClass != null){
				AbstractBlockLogicExtension.registerClass(blockdef.id, blockdef.logicClass);	
			}
		}

	}
	/**
	 *
	 */
	public static enum CLBlocks {

		/**
		 *
		 */
		CONSTRUCTIONSITE((byte) 11, "Construction Site", false, ConstructionSite.class),

		/**
		 *
		 */
		OVEN((byte) 12, "Oven", false, OvenLogic.class),

		/**
		 *
		 */
		TORCH((byte) 13, "Torch", false, PowerTorch.class),

		/**
		 *
		 */
		POWERSTATION((byte) 14, "Power Station", false, PowerStationLogic.class),

		/**
		 *
		 */
		LIFT((byte) 15, "Lift", false, LiftLogic.class),

		/**
		 *
		 */
		ENTRY((byte) 16, "Cave Entry", true, CaveEntryBlockLogic.class),

		/**
		 *
		 */
		INDESTRUCTIBLEOBSTACLE((byte) 17, "Indestructible Obstacle", false, null),
		
		/**
		 *
		 */
		LIFT_Ground((byte) 18, "Lift (Ground)", true, LiftLogicGround.class),

		/**
		 *
		 */
		CRYSTAL((byte) 41, "Crystal Block", true, null),

		/**
		 *
		 */
		SULFUR((byte) 42, "Sulfur Block", true, null),

		/**
		 *
		 */
		IRONORE((byte) 43, "Iron Ore Block", true, null),

		/**
		 *
		 */
		COAL((byte) 44, "Coal Block", true, null),

		/**
		 *
		 */
		TURRET((byte) 52, "Turret", false, Turret.class),

		/**
		 *
		 */
		ROBOTFACTORY((byte) 53, "robot factory", false, RobotFactory.class),

		/**
		 *
		 */
		POWERCABLE((byte) 54, "power cable", false, CableBlock.class),

		/**
		 *
		 */
		RAILS((byte) 55, "rails", false, null),

		/**
		 *
		 */
		RAILSBOOSTER((byte) 56, "booster rails", false, BoosterLogic.class),
		
		/**
		 *
		 */
		FLAGPOLE((byte) 60, "flag pole", false, Flagpole.class),

		/**
		 *
		 */
		TREE((byte) 72, "tree", false, null),

		/**
		 *
		 */
		UNDEFINED((byte) -1, "undefined", true, null);
		
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
		private Class<? extends AbstractBlockLogicExtension> logicClass;
		
		/**
		 * 
		 * @param id
		 * @param name
		 * @param hasSides rendered with sides or without
		 */
		private CLBlocks(byte id, String name, boolean hasSides, Class<? extends AbstractBlockLogicExtension> logic) {
			this.id = id;
			this.name = name;
			this.hasSides = hasSides;
			this.logicClass = logic;
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
	
	/**
	 *
	 * @param id
	 * @return
	 */
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
	
	@Override
	public RenderCell toRenderBlock(byte id, byte value) {
		if (id == 1) {
			GrassBlock grass = new GrassBlock(id, value);
			return grass;
		} else if (id == CLBlocks.INDESTRUCTIBLEOBSTACLE.id) {
			RenderCell iO = new RenderCell(id, value) {
				private static final long serialVersionUID = -4260228293280367069L;
				@Override
				public byte getSpriteId() {
					if (getValue() > 0) {
						return 3;
					}else return super.getSpriteId();
				}
			};
			return iO;
		} else if (id == CLBlocks.TREE.id) {
			return new CustomTree(id, value);
		} else if (id == CLBlocks.CONSTRUCTIONSITE.id) {
			return new ConstructionSiteRender(id, value);
		}else {
			return new RenderCell(id, value);
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
		if (id < 10) {
			return super.isObstacle(id, value);
		}
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
		if (id==17 && value==0) return true;
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
}
