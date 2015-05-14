package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.WE;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Benedikt Vogler
 */
public class Inventory implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * filled from back to front
	 */
	private Slot[] slot = new Slot[3];
	private static final boolean enableStacking = false;
	private CustomPlayer player;

	/**
	 *
	 * @param player
	 */
	public Inventory(CustomPlayer player) {
		slot[0] = new Slot();
		slot[1] = new Slot();
		slot[2] = new Slot();
		this.player = player;
	}
	
	/**
	 * reduces the counter and deletes the object from inventory
	 * @return the frontmost element. can return null if empty.
	 */
	public Collectible fetchFrontItem() {
		Collectible result = null;
		if (slot[0].counter>0){
			result = slot[0].take();
		} else if (slot[1].counter>0){
			result = slot[1].take();
		}else if (slot[2].counter>0){
			result = slot[2].take();
		}
		
		if (result==null) return null;
		return result;
	}
	
	/**
	 * tries to take the wanted type out of the inventory 
	 * @param def
	 * @return can return null
	 * @see #getCollectible(com.BombingGames.Caveland.GameObjects.Collectible.CollectibleType) 
	 */
	public Collectible fetchCollectible(Collectible.CollectibleType def){
		if (slot[2].prototype!=null && slot[2].prototype.getType() == def)
			return slot[2].take();
		if (slot[1].prototype!=null && slot[1].prototype.getType() == def)
			return slot[1].take();
		if (slot[0].prototype!=null && slot[0].prototype.getType() == def)
			return slot[0].take();
		return null;
	}
	
		/**
	 * Get a reference to the prototype and keeps the item in inventory.
	 * @param def
	 * @return can return null
	 * @see #fetchCollectible(com.BombingGames.Caveland.GameObjects.Collectible.CollectibleType) 
	 */
	public Collectible getCollectible(Collectible.CollectibleType def){
		if (slot[2].prototype!=null && slot[2].prototype.getType() == def)
			return slot[2].getPrototype();
		if (slot[1].prototype!=null && slot[1].prototype.getType() == def)
			return slot[1].getPrototype();
		if (slot[0].prototype!=null && slot[0].prototype.getType() == def)
			return slot[0].getPrototype();
		return null;
	}
	
	/**
	 * 
	 * @param ent
	 * @return false if inventory is full
	 */
	public final boolean add(Collectible ent){
		if (enableStacking) {
			if (slot[0].prototype != null && slot[0].prototype.getId() ==ent.getId() && slot[0].counter<10){
				slot[0].increase();
				return true;
			} else if (slot[1].prototype != null && slot[1].prototype.getId() ==ent.getId() && slot[1].counter<10){
				slot[1].increase();
				return true;
			} else if (slot[2].prototype != null && slot[2].prototype.getId() ==ent.getId() && slot[2].counter<10){
				slot[2].increase();
				return true;
			} else if (slot[2].prototype == null ) {
				slot[2].setPrototype(ent);
				return true;
			} else if (slot[1].prototype == null ) {
				slot[1].setPrototype(ent);
				return true;
			} else if (slot[0].prototype == null ) {
				slot[0].setPrototype(ent);
				return true;
			}
		} else {
			if (slot[2].prototype == null ) {
				slot[2].setPrototype(ent);
				return true;
			} else if (slot[1].prototype == null ) {
				slot[1].setPrototype(ent);
				return true;
			} else if (slot[0].prototype == null ) {
				slot[0].setPrototype(ent);
				return true;
			}	
		}
		return false;
	}
	
	/**
	 * Get a copy of the content. Does not change anything
	 * @return can have null in array
	 */
	public Collectible[] getContent(){
		return new Collectible[]{slot[0].getPrototype(), slot[1].getPrototype(), slot[2].getPrototype()};
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
	 * put everything if in the inventory.
	 * @param list
	 * @return everything 
	 */
	public ArrayList<Collectible> addAll(ArrayList<Collectible> list) {
		if (list != null) {
		Iterator<Collectible> it = list.iterator();
			while (it.hasNext()) {
			  Collectible ent = it.next();
				if (add(ent))
					it.remove();
			}
		}
		return list;
	}
	
	/**
	 *Renders the inventory in the HUD.
	 * @param view
	 * @param camera
	 */
	public void render(GameView view, Camera camera){
		for (int i = 0; i < slot.length; i++) {
			MovableEntity ent = slot[i].prototype;
			if (ent!=null) {
				int x = (int) ((int) (view.getStage().getWidth()-400+i*100)/view.getEqualizationScale());
				int y = (int) ((view.getStage().getHeight()-camera.getScreenPosY()-camera.getHeightInScreenSpc()+10)/view.getEqualizationScale()); 
				ent.render(view, x, y);
				if (enableStacking)
					view.drawString(Integer.toString(slot[i].counter),  x+20, y+30,false);
			}
		}
	}
	
	/**
	 * Updates the items in the slots.
	 * @param dt 
	 */
	public void update(float dt){
		for (Slot currentSlot : slot) {
			if (currentSlot.prototype != null){
				currentSlot.prototype.update(dt);
				if (currentSlot.prototype.shouldBeDisposed()){
					currentSlot.prototype = null;
					currentSlot.counter = 0;
				}
			}
		}
	}
	
	/**
	 * Works only for three stacks.
	 * @param left true if left, false to right
	 */
	public void switchItems(boolean left){
		if (slot[0].counter>0) {//switch three items
			if (left){
				Slot tmp = slot[0];
				slot[0] = slot[1];
				slot[1] = slot[2];
				slot[2] = tmp;
			}else {
				Slot tmp = slot[1];
				slot[1] = slot[0];
				slot[0] = slot[2];
				slot[2] = tmp;
			}
		} else if (slot[1].counter>0){
			Slot tmp = slot[1];
			slot[1] = slot[2];
			slot[2] = tmp;
		}
	}

	/**
	 * the amount of stacks in the inventory
	 * @return 
	 */
	public int size() {
		return slot.length;
	}
	
	/**
	 * calls the action method for the first slot item.
	 * @param actor
	 */
	public void action(AbstractEntity actor){
		//Get the first item and activate it. Then put it back.
		Collectible item = fetchFrontItem();
		if(item!=null) {
			item.action(actor);
			add(item);
		}
	}
	
	/**
	 *
	 * @return
	 */
	public boolean isEmpty(){
		boolean empty = true;
		for (Slot currentSlot : slot) {
			if (!currentSlot.isEmpty())
				empty=false;
		}
		return empty;
	}
	
	private class Slot implements Serializable {
		private static final long serialVersionUID = 1L;
		private int counter;
		private Collectible prototype;

		/**
		 * Takes one object from the slot
		 * @return 
		 */
		private Collectible take() {
			counter--;
			Collectible tmp;
			if (enableStacking) {
				try {
					tmp = prototype.clone();
				} catch (CloneNotSupportedException ex) {
					//if clone fails remove this item
					counter=0;
					prototype=null;
					WE.getConsole().add("Cloning of inventory item failed.");
				}
			} else {
				tmp = prototype;
			}
			if (counter <= 0)
				prototype=null;
			return tmp;
		}
		
		/**
		 * 
		 * @return can return null
		 */
		private Collectible getPrototype(){
			return prototype;
		}

		protected void increase(){
			counter++;
		}

		public void setPrototype(Collectible prototype) {
			this.prototype = prototype;
			counter=1;
		}

		private boolean isEmpty() {
			return counter<=0;
		}
	}

}
