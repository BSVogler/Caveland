package com.bombinggames.caveland.Game;

import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.GameObjects.Collectible;
import com.bombinggames.caveland.GameObjects.CustomPlayer;
import com.bombinggames.caveland.GameObjects.CustomTree;
import com.bombinggames.caveland.GameObjects.Machine;
import com.bombinggames.wurfelengine.Core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.Core.Gameobjects.BlockDirt;
import com.bombinggames.wurfelengine.Core.Gameobjects.CoreData;
import com.bombinggames.wurfelengine.Core.Gameobjects.CustomBlocks;
import com.bombinggames.wurfelengine.Core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.Core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.Core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class CavelandBlocks implements CustomBlocks {

@Override
public RenderBlock toRenderBlock(CoreData data) {
	RenderBlock block;
	byte id = data.getId();
	byte value = data.getValue();
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
			block = new RenderBlock(id, value);
		}
		return block;
	}	

	@Override
	public String getName(byte id, byte value) {
		switch (id){
			case 11:
				return "construction site";
			case 12:
				return "oven";	
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
		if (id==11) return false;
		if (id==12) return false;
		if (id==55) return false;
		if (id==72) return false;
		return true;
	}
	
	@Override
	public boolean isObstacle(byte id, byte value) {
		if (id==11) return false;
		if (id>=41 && id<=44)//ores
			return true;
		if (id==72) return true;
		if (id==46) return true;
		return false;
	}

	@Override
	public boolean isTransparent(byte id, byte value) {
		if (id==11) return true;
		if (id==55) return true;
		return false;
	}

	@Override
	public boolean isLiquid(byte id, byte value) {
		return false;
	}

	@Override
	public void setHealth(Coordinate coord, byte health, byte id, byte value) {
		if (health <= 0 ){
			if (id==41) {
				Collectible.create(Collectible.CollectibleType.CRISTALL).spawn(coord.toPoint().cpy());
			} else if (id==42){
				Collectible.create(Collectible.CollectibleType.SULFUR).spawn(coord.toPoint().cpy());
			} else if (id==43){
				Collectible.create(Collectible.CollectibleType.IRONORE).spawn(coord.toPoint().cpy());
			} else if (id==44){
				Collectible.create(Collectible.CollectibleType.COAL).spawn(coord.toPoint().cpy());
			}else if (id==72){
				Collectible.create(Collectible.CollectibleType.WOOD).spawn(coord.toPoint().cpy());
			}
			
			//view only relevant. should only be done if visible
			//todo, check if visible
			for (int i = 0; i < 10; i++) {
				MovableEntity dirt = (MovableEntity) new BlockDirt().spawn(coord.toPoint().cpy());
				dirt.addMovement(new Vector3((float) Math.random()-0.5f, (float) Math.random()-0.5f,(float) Math.random()*5f));
				dirt.setRotation((float) Math.random()*360);
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
	 * @param value
	 * @return 
	 */
	public static boolean interactAble(byte id, byte value){
		if (id==11 || id==12) return true;
		return false;
	}
	
	
	public static void interact(Coordinate coord, AbstractEntity actor){
		CoreData block = coord.getBlock();
		if (block!=null) {
			byte id = coord.getBlock().getId();
			if (id==11) {//construction site
				if (actor instanceof CustomPlayer){
					//lege objekte aus Inventar  hier rein
					Collectible frontItem = ((CustomPlayer) actor).getInventory().fetchFrontItem();
					if (frontItem!=null)
						frontItem.spawn(coord.toPoint());
				}
				//sind alle da dannn baue
			}
		}
		
	}
	
}
