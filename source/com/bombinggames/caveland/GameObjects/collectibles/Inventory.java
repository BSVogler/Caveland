package com.bombinggames.caveland.GameObjects.collectibles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.SuperGlue;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;

/**
 * The inventory is a special limited collectible container. It also moves the content
 * at the players position (kinda like in its backpack).
 *
 * @author Benedikt Vogler
 */
public class Inventory extends CollectibleContainer {

	private static final long serialVersionUID = 3L;
	private final Ejira player;

	/**
	 *
	 *an invnetory needs a player where it is attached to
	 * @param player
	 */
	public Inventory(Ejira player) {
		super();
		this.player = player;
		setBackpack(true);
		setName("Inventory");
		setHidden(true);
	}

	/**
	 * Attaches/glues the inventory to the player
	 * @return
	 */
	public AbstractEntity spawn() {
		super.spawn(player.getPosition());
		new SuperGlue(player, this).spawn(player.getPosition());//glue the inventory container at the player
		return this;
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
		result.disposeFromMap();
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
		if (get(2) != null) {
			return get(2);
		} else if (get(1) != null) {
			return get(1);
		} else if (get(0) != null) {
			return get(0);
		}
		return null;
	}
	
	/**
	 * Add item at the back.
	 * @param col the item you add
	 * @return false if inventory is full. True if sucessfull.
	 */
	public final boolean add(Collectible col) {
		if (size() < 3) {
			addCollectible(col);
			return true;
		}
		return false;
	}
	
	/**
	 * Add item at the front.
	 * @param col
	 * @return false if inventory is full. True if sucessfull.
	 */
	public final boolean addFront(Collectible col) {
		if (size() < 3) {
			addCollectibleFront(col);
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
		Collectible[] tmp = getContentAsArray();
		return new CollectibleType[]{
			(tmp[0] == null ? null : tmp[0].getType()),
			(tmp[1] == null ? null : tmp[1].getType()),
			(tmp[2] == null ? null : tmp[2].getType()),};
	}

	/**
	 * Renders the inventory in the HUD.
	 *
	 * @param view
	 * @param camera
	 */
	@Override
	public void render(GameView view, Camera camera) {
		super.render(view, camera);
		int inventoryPadding = 100;
		float left = (view.getStage().getWidth() - 400) / view.getEqualizationScale();
		int y = (int) ((view.getStage().getHeight() - camera.getScreenPosY() - camera.getHeightInScreenSpc() + 10) / view.getEqualizationScale());

		//draw background for highlit sprite
		Sprite bgSprite = new Sprite(AbstractGameObject.getSprite('i', 10, 0));
		float leftbgSprite = (view.getStage().getWidth() - 400 - bgSprite.getWidth()/2) / view.getEqualizationScale();
		bgSprite.setPosition(leftbgSprite, y);
		bgSprite.draw(view.getSpriteBatch());
		bgSprite.setX(leftbgSprite + inventoryPadding / view.getEqualizationScale());
		bgSprite.setScale(0.5f);
		bgSprite.setY(bgSprite.getY()-20);
		bgSprite.draw(view.getSpriteBatch());
		bgSprite.setX(leftbgSprite + 2 * inventoryPadding / view.getEqualizationScale());
		bgSprite.draw(view.getSpriteBatch());

		for (int i = 0; i < size(); i++) {
			MovableEntity ent = get(i);
			if (ent != null) {
				int x = (int) ((int) left + i * inventoryPadding / view.getEqualizationScale());
				if (i!=0) ent.setScaling(-0.4f);
				ent.render(view, x, y);
				if (i!=0) ent.setScaling(0);
			}
		}
	}

	/**
	 * calls the action method for the first slot item.
	 *
	 * @param view
	 * @param actor
	 */
	public void action(CustomGameView view, AbstractEntity actor) {
		//Get the first item and activate it. Then put it back.
		Collectible item = retrieveFrontItem();
		if (item != null) {
			item.action(view, actor);
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
