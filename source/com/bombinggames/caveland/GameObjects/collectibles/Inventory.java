package com.bombinggames.caveland.GameObjects.collectibles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;

/**
 * The inventory is a special limited collectible container. It also moves the
 * content at the players position (kinda like in its backpack).
 *
 * @author Benedikt Vogler
 */
public class Inventory extends CollectibleContainer {

	private static final long serialVersionUID = 3L;

	/**
	 *
	 * an invnetory needs a player where it is attached to
	 *
	 * @param player
	 */
	public Inventory(Ejira player) {
		super(player);
		setName("Inventory");
		setHidden(true);
	}

	/**
	 * Attaches/glues the inventory to the player
	 *
	 * @return
	 */
	public AbstractEntity spawn() {
		super.spawn(((Ejira)getOwner()).getPosition());
		return this;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (hasPosition() && ((Ejira)getOwner()).hasPosition())
			this.getPosition().setValues(((Ejira)getOwner()).getPosition());
	}

	/**
	 * Deletes the object from inventory. Makes the object appear in the world.
	 *
	 * @return the frontmost element. Can return null if empty.
	 * @see #retrieveFrontItemReference()
	 */
	public Collectible retrieveFrontItem() {
		Collectible result = null;
		if (get(0) != null) {
			result = retrieveCollectible(0);
		}

		return result;
	}

	/**
	 * Deletes the object from inventory. Makes the object appear not in the
	 * world.
	 *
	 * @return the frontmost element. can return null if empty.
	 * @see #retrieveFrontItem()
	 */
	public Collectible retrieveFrontItemReference() {
		Collectible result = null;
		if (get(0) != null) {
			result = retrieveCollectibleReference(0);
		}

		if (result == null) {
			return null;
		}
		return result;
	}

	/**
	 * Get a reference to the prototype and keeps the item in inventory.
	 *
	 * @param def
	 * @return can return null
	 */
	@Override
	public Collectible getCollectible(CollectibleType def) {
		if (get(2) != null && get(2).getType() == def) {
			return get(2);
		} else if (get(1) != null && get(1).getType() == def) {
			return get(1);
		} else if (get(0) != null && get(0).getType() == def) {
			return get(0);
		}
		return null;
	}

	/**
	 * Does not alter the invetory.
	 *
	 * @return Only reference. Can return null.
	 */
	public Collectible getFrontCollectible() {
		if (get(0) != null) {
			return get(0);
		} else if (get(1) != null) {
			return get(1);
		} else if (get(2) != null) {
			return get(2);
		}
		return null;
	}

	/**
	 * Add item at the back.
	 *
	 * @param col the item you add
	 * @return false if inventory is full. True if sucessfull.
	 */
	@Override
	public final boolean add(Collectible col) {
		if (size() < 3) {
			super.add(col);
			return true;
		}
		return false;
	}

	/**
	 * Add item at the front.
	 *
	 * @param col
	 * @return false if inventory is full. True if sucessfull.
	 */
	@Override
	public final boolean addFront(Collectible col) {
		if (size() < 3) {
			super.addFront(col);
			return true;
		}
		return false;
	}

	/**
	 * Get a copy of the content. Does not change anything
	 *
	 * @return can have null in array
	 */
	public Collectible[] getContentAsArray() {
		return new Collectible[]{get(0), get(1), get(2)};
	}

	/**
	 * Get type definitions. Does not alter anything.
	 *
	 * @return can have null in array
	 */
	public CollectibleType[] getContentDef() {
		return new CollectibleType[]{
			(get(0) == null ? null : get(0).getType()),
			(get(1) == null ? null : get(1).getType()),
			(get(2) == null ? null : get(2).getType())};
	}
	
	/**
	 * check if the inventory contains a type of this item
	 *
	 * @param ingredient
	 * @return the amount the item is contained
	 */
	public int contains(CollectibleType ingredient) {
		int count = 0;
		if (getContentDef()[0] != null && getContentDef()[0] == ingredient) {
			count++;
		}
		if (getContentDef()[1] != null && getContentDef()[1] == ingredient) {
			count++;
		}
		if (getContentDef()[2] != null && getContentDef()[2] == ingredient) {
			count++;
		}
		return count;
	}

	/**
	 *
	 * @param view
	 * @param camera
	 */
	public void drawHUD(GameView view, Camera camera){
		//draw background for highlit sprite
		Sprite bgSprite = new Sprite(AbstractGameObject.getSprite('i', (byte) 10, (byte) 0));

		float left = (camera.getScreenPosX() + camera.getWidthInScreenSpc() * 0.75f) / view.getEqualizationScale() + bgSprite.getWidth() / 2;
		int y = (int) ((view.getStage().getHeight() - camera.getScreenPosY() - camera.getHeightInScreenSpc() + 10) / view.getEqualizationScale());

		float leftbgSprite = (camera.getScreenPosX() + camera.getWidthInScreenSpc() * 0.75f) / view.getEqualizationScale();
		// / view.getEqualizationScale()
		bgSprite.setPosition(leftbgSprite, y);
		bgSprite.draw(view.getSpriteBatch());
		bgSprite.setX(leftbgSprite + 80);
		bgSprite.setScale(0.5f);
		bgSprite.setY(bgSprite.getY() - 20);
		bgSprite.draw(view.getSpriteBatch());
		bgSprite.setX(leftbgSprite + 140);
		bgSprite.draw(view.getSpriteBatch());

		Collectible ent = get(0);
		if (ent != null) {
			ent.render(view, (int) left, y);
			if (ent instanceof Interactable) {
				Sprite button = new Sprite(AbstractGameObject.getSprite('i', (byte) 23, Interactable.YUp));
				button.setPosition(left-90, y-30);
				button.setScale(0.4f);
				button.draw(view.getSpriteBatch());
			}
		}
		ent = get(1);
		if (ent != null) {
			ent.setScaling(-0.4f);
			ent.render(view, (int) (left + 80), y);
			ent.setScaling(0);
		}
		ent = get(2);
		if (ent != null) {
			ent.setScaling(-0.4f);
			ent.render(view, (int) (left + 140), y);
			ent.setScaling(0);
		}
		
//		bgSprite.setX(leftbgSprite + 80);
//		bgSprite.setScale(0.5f);
//		bgSprite.setY(bgSprite.getY() - 20);
//		bgSprite.draw(view.getSpriteBatch());
//		bgSprite.setX(leftbgSprite + 140);
//		bgSprite.draw(view.getSpriteBatch());
		
//		for (int i = 0; i < size(); i++) {
//			MovableEntity ent = get(i);
//			if (ent != null) {
//				int x = (int) ((int) left + i * inventoryPadding / view.getEqualizationScale());
//				if (i != 0) {
//					ent.setScaling(-0.4f);
//				}
//				ent.render(view, x, y);
//				if (i != 0) {
//					ent.setScaling(0);
//				}
//			}
//		}
	}

	/**
	 * calls the action method for the first slot item.
	 *
	 * @param view
	 * @param actor
	 */
	public void action(CLGameView view, AbstractEntity actor) {
		//Get the first item and activate it. Then put it back.
		Collectible item = retrieveFrontItem();
		if (item != null && item instanceof Interactable) {
			((Interactable) item).interact(view, actor);
			addFront(item);
		}
	}

	/**
	 *
	 * @return
	 */
	public boolean isEmpty() {
		return size() == 0;
	}
}
