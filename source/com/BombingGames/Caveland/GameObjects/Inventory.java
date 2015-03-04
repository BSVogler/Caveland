package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.badlogic.gdx.Gdx;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Benedikt Vogler
 */
public class Inventory implements Serializable {
	private static final long serialVersionUID = 1L;
	private Slot[] slot = new Slot[3];

	public Inventory() {
		slot[0] = new Slot();
		slot[1] = new Slot();
		slot[2] = new Slot();
//		add(new Flint());
//		add(new Flint());
//		add(new Flint());
	}
	
	/**
	 * reduces the counter and deletes the object from inventory
	 * @return the frontmost element. can return null if empty.
	 * @throws java.lang.CloneNotSupportedException
	 */
	public MovableEntity getFrontItem() throws CloneNotSupportedException {
		MovableEntity tmp = null;
		if (slot[0].counter>0){
			tmp = slot[0].take();
		} else if (slot[1].counter>0){
			tmp = slot[1].take();
		}else if (slot[2].counter>0){
			tmp = slot[2].take();
		}
		if (tmp==null) return null;	
		return tmp.clone();
	}
	
	/**
	 * 
	 * @param ent
	 * @return false if inventory is full
	 */
	public final boolean add(Collectible ent){
		if (ent.isCollectable()) {
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
		}
		return false;
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
				int x = (int) (view.getStage().getWidth()-400+i*100);
				int y =Gdx.graphics.getHeight()-camera.getScreenPosY()-camera.getHeightInScreenSpc()+10; 
				ent.render(view, x, y);
				view.drawString(Integer.toString(slot[i].counter),  x+20, y+30,false);
			}
		}
	}
	
	/**
	 * 
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

	public int size() {
		return slot.length;
	}
	
	/**
	 * calls the action method for the first slot item.
	 */
	public void action(){
		if (slot[0].counter>0){
			slot[0].prototype.action();
		} else if (slot[1].counter>0){
			slot[1].prototype.action();
		}else if (slot[2].counter>0){
			slot[2].prototype.action();
		}
	}
	
	private class Slot implements Serializable {
		private int counter;
		private Collectible prototype;

		private MovableEntity take() {
			counter--;
			MovableEntity tmp = prototype;
			if (counter <= 0)
				prototype=null;
			return tmp;
		}

		protected void increase(){
			counter++;
		}

		public void setPrototype(Collectible prototype) {
			this.prototype = prototype;
			counter=1;
		}
	}

}
