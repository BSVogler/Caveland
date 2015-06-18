package com.bombinggames.caveland.Game;

import com.bombinggames.caveland.GameObjects.Collectible;
import com.bombinggames.caveland.GameObjects.CustomPlayer;
import com.bombinggames.caveland.GameObjects.InteractableCollectibleContainer;
import com.bombinggames.caveland.GameObjects.SuperGlue;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;

/**
 *The inventory stores the items with a reference in a list but also moves them at the players position (kinda like in its backpack).
 * @author Benedikt Vogler
 */
public class Inventory {
	private static final long serialVersionUID = 2L;
	/**
	 * contains the objects
	 */
	private final InteractableCollectibleContainer container;

	/**
	 *Creating a new inventory spawns a container at the position of the player
	 * @param player
	 */
	public Inventory(CustomPlayer player) {
		container = (InteractableCollectibleContainer) new InteractableCollectibleContainer().spawn(player.getPosition());
		container.setSaveToDisk(false);
		container.setBackpack(true);
		new SuperGlue(player, container).spawn(player.getPosition());
	}
	
	/**
	 * reduces the counter and deletes the object from inventory. Makes the object appear in the world.
	 * @return the frontmost element. Can return null if empty.
	 * @see #fetchFrontItemAndDisposeFromWorld() 
	 */
	public Collectible retrieveFrontItem() {
		Collectible result = null;
		if (container.get(0) != null){
			result = container.retrieveCollectible(0);
		} else if (container.get(1) != null){
			result = container.retrieveCollectible(1);
		}else if (container.get(2) != null){
			result = container.retrieveCollectible(2);
		}
		
		if (result==null) return null;
		return result;
	}
	
	/**
	 * reduces the counter and deletes the object from inventory. makes the object appear not in the world.
	 * @return the frontmost element. can return null if empty.
	 * @see #retrieveFrontItem() 
	 */
	public Collectible fetchFrontItemReference() {
		Collectible result = null;
		if (container.get(0) != null){
			result = container.retrieveCollectibleReference(0);
		} else if (container.getChildren().get(1) != null){
			result = container.retrieveCollectibleReference(1);
		}else if (container.get(2) != null){
			result = container.retrieveCollectibleReference(2);
		}
		
		if (result==null) return null;
		result.disposeFromMap();
		return result;
	}
	
	/**
	 * tries to take the wanted type out of the inventory 
	 * @param def
	 * @return can return null
	 * @see #getCollectible(com.bombinggames.caveland.GameObjects.Collectible.CollectibleType) 
	 */
	public Collectible fetchCollectible(Collectible.CollectibleType def){
		if (container.get(2)!=null && container.get(2).getType() == def)
			return container.retrieveCollectible(2);
		if (container.get(1) !=null && container.get(1).getType() == def)
			return container.retrieveCollectible(1);
		if (container.get(0) !=null && container.get(0).getType() == def)
			return container.retrieveCollectible(0);
		return null;
	}
	
		/**
	 * Get a reference to the prototype and keeps the item in inventory.
	 * @param def
	 * @return can return null
	 * @see #fetchCollectible(com.bombinggames.caveland.GameObjects.Collectible.CollectibleType) 
	 */
	public Collectible getCollectible(Collectible.CollectibleType def){
		if (container.get(2)!=null && container.get(2).getType() == def)
			return container.get(2);
		if (container.get(1) !=null && container.get(1).getType() == def)
			return container.get(1);
		if (container.get(0) !=null && container.get(0).getType() == def)
			return container.get(0);
		return null;
	}
	
	/**
	 * 
	 * @param ent
	 * @return false if inventory is full
	 */
	public final boolean add(Collectible ent){
		if (container.size()<3 ) {
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
	public Collectible.CollectibleType[] getContentDef(){
		Collectible[] tmp = getContent();
		return new Collectible.CollectibleType[]{
			(tmp[0]==null ? null : tmp[0].getType()),
			(tmp[1]==null ? null : tmp[1].getType()),
			(tmp[2]==null ? null : tmp[2].getType()),
		};
	}
	
	/**
	 *Renders the inventory in the HUD.
	 * @param view
	 * @param camera
	 */
	public void render(GameView view, Camera camera){
		for (int i = 0; i < 3; i++) {
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
	
}
