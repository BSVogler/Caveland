package com.bombinggames.caveland.GameObjects.collectibles;

import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.CustomPlayer;
import com.bombinggames.caveland.GameObjects.SuperGlue;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Map.Point;

/**
 *The inventory stores the items with a reference in a list but also moves them at the players position (kinda like in its backpack).
 * @author Benedikt Vogler
 */
public class Inventory {
	private static final long serialVersionUID = 2L;
	/**
	 * contains the objects
	 */
	private final CollectibleContainer container;

	/**
	 *Creating a new inventory spawns a container at the position of the player
	 * @param player
	 */
	public Inventory(CustomPlayer player) {
		container = (CollectibleContainer) new CollectibleContainer().spawn(player.getPosition());
		container.setBackpack(true);
		new SuperGlue(player, container).spawn(player.getPosition());//glue the inventory container at the player
	}
	
	/**
	 * Deletes the object from inventory. Makes the object appear in the world.
	 * @return the frontmost element. Can return null if empty.
	 * @see #retrieveFrontItemReference()
	 */
	public Collectible retrieveFrontItem() {
		Collectible result = null;
		if (container.get(0) != null){
			result = container.retrieveCollectible(0);
		}
		
		return result;
	}
	
	/**
	 * Deletes the object from inventory. Makes the object appear not in the world.
	 * @return the frontmost element. can return null if empty.
	 * @see #retrieveFrontItem() 
	 */
	public Collectible retrieveFrontItemReference() {
		Collectible result = null;
		if (container.get(0) != null){
			result = container.retrieveCollectibleReference(0);
		}
		
		if (result==null) return null;
		result.disposeFromMap();
		return result;
	}
	
	/**
	 * Get a reference to the prototype and keeps the item in inventory.
	 * @param def
	 * @return can return null
	 */
	public Collectible getCollectible(CollectibleType def){
		if (container.get(2)!=null && container.get(2).getType() == def)
			return container.get(2);
		else if (container.get(1) !=null && container.get(1).getType() == def)
			return container.get(1);
		else if (container.get(0) !=null && container.get(0).getType() == def)
			return container.get(0);
		return null;
	}
	
	/**
	 * Does not alter the invetory.
	 * @return Only reference. Can return null.
	 */
	public Collectible getFrontCollectible(){
		if (container.get(2)!=null)
			return container.get(2);
		else if (container.get(1) !=null)
			return container.get(1);
		else if (container.get(0) !=null)
			return container.get(0);
		return null;
	}
	
	/**
	 * Get a reference to the item and removes the item from inventory. Makes the object appear not in the world.
	 * @param def
	 * @return can return null
	 */
	public Collectible retrieveCollectible(CollectibleType def){
		Collectible result = null;
		if (container.get(2)!=null && container.get(2).getType() == def)
			result = container.retrieveCollectible(2);
		else if (container.get(1) !=null && container.get(1).getType() == def)
			result = container.retrieveCollectible(1);
		else if (container.get(0) !=null && container.get(0).getType() == def)
			result = container.retrieveCollectible(0);
		if (result==null) return null;
		result.disposeFromMap();
		return result;
	}
	
	/**
	 * 
	 * @param ent
	 * @return false if inventory is full
	 */
	public final boolean add(Collectible ent){
		if (container.size()<3 ) {
			ent.setPosition(container.getPosition().cpy());
			container.addChild(ent);
			return true;
		}	
		return false;
	}
	
	
	/**
	 * Get a copy of the content. Does not change anything
	 * @return can have null in array
	 */
	public Collectible[] getContent(){
		return new Collectible[]{container.get(0), container.get(1), container.get(2)};
	}
	
	/**
	 * Get a copy of the content. Does not change anything
	 * @return can have null in array
	 */
	public CollectibleType[] getContentDef(){
		Collectible[] tmp = getContent();
		return new CollectibleType[]{
			(tmp[0]==null ? null : tmp[0].getType()),
			(tmp[1]==null ? null : tmp[1].getType()),
			(tmp[2]==null ? null : tmp[2].getType()),
		};
	}
	
	/**
	 * Renders the inventory in the HUD.
	 * @param view
	 * @param camera
	 */
	public void render(GameView view, Camera camera){
		for (int i = 0; i < container.size(); i++) {
			MovableEntity ent = container.get(i);
			if (ent != null) {
				int x = (int) ((int) (view.getStage().getWidth()-400+i*100)/view.getEqualizationScale());
				int y = (int) ((view.getStage().getHeight()-camera.getScreenPosY()-camera.getHeightInScreenSpc()+10)/view.getEqualizationScale()); 
				ent.render(view, x, y);
			}
		}
	}
		
	/**
	 * Works only for three stacks.
	 * @param left true if left, false to right
	 */
	public void switchItems(boolean left){
		container.switchItems(left);
	}

	/**
	 * calls the action method for the first slot item.
	 * @param view
	 * @param actor
	 */
	public void action(CustomGameView view, AbstractEntity actor){
		//Get the first item and activate it. Then put it back.
		Collectible item = retrieveFrontItem();
		if(item!=null) {
			item.action(view, actor);
			add(item);
		}
	}
	
	/**
	 *
	 * @return
	 */
	public boolean isEmpty(){
		return container.size() == 0;
	}
	
	public Point getPosition(){
		return container.getPosition();
	}
	
}
