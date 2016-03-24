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
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.map.CustomBlocks;
import com.bombinggames.wurfelengine.core.gameobjects.DestructionParticle;
import com.bombinggames.wurfelengine.core.map.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import java.util.ArrayList;

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
		CONSTRUCTIONSITE((byte) 11, "Construction Site", false, true),

		/**
		 *
		 */
		OVEN((byte) 12, "Oven", false, true),

		/**
		 *
		 */
		TORCH((byte) 13, "Torch", false, true),

		/**
		 *
		 */
		POWERSTATION((byte) 14, "Power Station", false, false),

		/**
		 *
		 */
		LIFT((byte) 15, "Lift", false, true),

		/**
		 *
		 */
		ENTRY((byte) 16, "Cave Entry", true, true),

		/**
		 *
		 */
		INDESTRUCTIBLEOBSTACLE((byte) 17, "Indestructible Obstacle", false, false),
		
		/**
		 *
		 */
		LIFT_Ground((byte) 18, "Lift (Ground)", true, false),

		/**
		 *
		 */
		CRYSTAL((byte) 41, "Crystal Block", true, false),

		/**
		 *
		 */
		SULFUR((byte) 42, "Sulfur Block", true, false),

		/**
		 *
		 */
		IRONORE((byte) 43, "Iron Ore Block", true, false),

		/**
		 *
		 */
		COAL((byte) 44, "Coal Block", true, false),

		/**
		 *
		 */
		TURRET((byte) 52, "Turret", false, true),

		/**
		 *
		 */
		ROBOTFACTORY((byte) 53, "robot factory", false, true),

		/**
		 *
		 */
		POWERCABLE((byte) 54, "power cable", false, true),

		/**
		 *
		 */
		RAILS((byte) 55, "rails", false, false),

		/**
		 *
		 */
		RAILSBOOSTER((byte) 56, "booster rails", false, true),
		
		/**
		 *
		 */
		FLAGPOLE((byte) 60, "flag pole", false, true),

		/**
		 *
		 */
		TREE((byte) 72, "tree", false, false),

		/**
		 *
		 */
		UNDEFINED((byte) -1, "undefined", true, false);
		
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
		private final boolean hasLogic;
		private final static ArrayList<CLBlocks> typesWithLogic = new ArrayList<>();
		
		static {
			for (CLBlocks e : CLBlocks.values()) {
				if (e.hasLogic) {
					typesWithLogic.add(e);
				}
			}
		}
		/**
		 * 
		 * @param id
		 * @param name
		 * @param hasSides rendered with sides or without
		 */
		private CLBlocks(byte id, String name, boolean hasSides, boolean hasLogic) {
			this.id = id;
			this.name = name;
			this.hasSides = hasSides;
			this.hasLogic = hasLogic;
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
	public RenderCell toRenderBlock(byte id, byte value) {
		if (id == 1) {
			GrassBlock grass = new GrassBlock(id, value);
			return grass;
		} else if (id == CLBlocks.INDESTRUCTIBLEOBSTACLE.id) {
			RenderCell iO = new RenderCell(id, value);
			if (value> 0){
				iO.setSpriteId((byte) 3);
			}
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

				if (otherHalf.getBlockId() != 0 && otherHalf.getBlockId() == id) {
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
	public boolean hasLogic(byte id, byte value) {
		for (CLBlocks e : CLBlocks.typesWithLogic) {
			if (e.hasLogic && id == e.id) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public AbstractBlockLogicExtension newLogicInstance(byte id, byte value, Coordinate coord) {
		if (id == CLBlocks.ENTRY.id) {
			return new CaveEntryBlockLogic(id, coord);
		}else if (id == CLBlocks.CONSTRUCTIONSITE.id) {
			return new ConstructionSite(id, coord);
		}else if (id == CLBlocks.OVEN.id) {
			return new OvenLogic(id, coord);
		}else if (id == CLBlocks.POWERSTATION.id) {
			return new PowerStationLogic(id, coord);
		} else if (id == CLBlocks.LIFT.id) {
			return new LiftLogic(id, coord);
		} else if (id == CLBlocks.LIFT_Ground.id) {
			return new LiftLogicGround(id, coord);
		} else if (id == CLBlocks.ROBOTFACTORY.id) {
			return new RobotFactory(id, coord);
		} else if (id == CLBlocks.POWERCABLE.id) {
			return new CableBlock(id, value, coord);
		} else if (id == CLBlocks.RAILSBOOSTER.id) {
			return new BoosterLogic(id, coord);
		} else if (id == CLBlocks.TURRET.id) {
			return new Turret(id, coord);
		} else if (id == CLBlocks.TORCH.id) {
			return new PowerTorch(id, coord);
		} else if (id == CLBlocks.FLAGPOLE.id) {
			return new Flagpole(id, coord);
		}
		return null;
	}
}
