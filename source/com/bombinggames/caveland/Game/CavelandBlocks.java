package com.bombinggames.caveland.Game;

import com.bombinggames.caveland.GameObjects.CustomTree;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.caveland.GameObjects.Machine;
import com.bombinggames.caveland.GameObjects.OvenLogic;
import com.bombinggames.caveland.GameObjects.Torch;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.CustomBlocks;
import com.bombinggames.wurfelengine.core.Gameobjects.DestructionParticle;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class CavelandBlocks implements CustomBlocks {

@Override
public RenderBlock toRenderBlock(Block data) {
	switch (data.getId()){
		case 13:
			return new Torch(data);
		case 60:
			return new Machine(data);
		case 72:
			return new CustomTree(data);
//		case 72:
//			block = new AnimatedBlock(id, new int[]{1000,1000},true, true);//animation lighting
//			block.setObstacle(true);
//		break;
		default:
			return new RenderBlock(data);
		}
	}	

	@Override
	public String getName(byte id, byte value) {
		switch (id){
			case 11:
				return "Construction Site";
			case 12:
				return "Oven";	
			case 13:
				return "Torch";
			case 40:
				return "Entity Spawner";
			case 41:
				return "Crystal Block";
			case 42:
				return "Sulfur Block";
			case 43:
				return "Iron Ore Block";
			case 44:
				return "Coal Block";
			case 46:
				return "Sand Block";	
			case 55:
				return "rails";	
			case 72:
				return "tree";	
		}
			
		return "not named yet";
	}

	@Override
	public boolean hasSides(byte id, byte value) {
		if (id==13) return false;
		if (id==11) return false;
		if (id==12) return false;
		if (id==55) return false;
		if (id==72) return false;
		return true;
	}
	
	@Override
	public boolean isObstacle(byte id, byte value) {
		if (id==12) return true;
		if (id==13) return false;
		if (id==11) return false;
		if (id>=41 && id<=44)//ores
			return true;
		if (id==72) return true;
		if (id==46) return true;
		return false;
	}

	@Override
	public boolean isTransparent(byte id, byte value) {
		if (id==13) return true;
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
	public void setHealth(Coordinate coord, byte health, byte id, byte value) {
		if (health <= 0 ){
			if (id==3) {
				CollectibleType.Stone.createInstance().spawn(coord.toPoint().cpy());
			} else if (id==41) {
				CollectibleType.Cristall.createInstance().spawn(coord.toPoint().cpy());
			} else if (id==42){
				CollectibleType.Sulfur.createInstance().spawn(coord.toPoint().cpy());
			} else if (id==43){
				CollectibleType.Ironore.createInstance().spawn(coord.toPoint().cpy());
			} else if (id==44){
				CollectibleType.Coal.createInstance().spawn(coord.toPoint().cpy());
			}else if (id==72){
				CollectibleType.Wood.createInstance().spawn(coord.toPoint().cpy());
			}
			
			//view only relevant. should only be done if visible
			//todo, check if visible
			for (int i = 0; i < 10; i++) {
				new DestructionParticle((byte) 44).spawn(coord.toPoint().cpy());
			}
		}
	}
	
	
	//Caveland specific details
	
	/**
	 * indestructable by diggin
	 * @param id
	 * @return 
	 */
	public static boolean hardMaterial(byte id){
		if (id==41)
			return true;
		if (id==43)
			return true;
		if (id==3) return true;//stone
		return false;
	}
	
	/**
	 * is it possible to interact with this
	 * @param id
	 * @return 
	 */
	private static boolean interactAble(byte id){
		if (id==11 || id==12) return true;
		return false;
	}
	
	/**
	 * 
	 * @param coord
	 * @return null if not interactable 
	 */
	public static Interactable verifyInteractableExistence(Coordinate coord){
		byte id = coord.getBlock().getId();
		byte value = coord.getBlock().getId();
		if (interactAble(id)) {
			ArrayList<AbstractEntity> everyEntity = coord.getEntitiesInside();
			for (AbstractEntity ent : everyEntity) {
				if (id==11) {
					if (ent instanceof OvenLogic) {
						return (Interactable) ent;
					}
				}
			}
			//not found, so create new one
			if (id==11) {
				return (Interactable) new OvenLogic().spawn(coord.toPoint());
			} else return null;
		} else {
			return null;
		}
	}
}
