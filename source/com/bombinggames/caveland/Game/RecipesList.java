package com.bombinggames.caveland.Game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import java.util.ArrayList;

/**
 *A list which stores the possibles recipes.
 * @author Benedikt Vogler
 */
public class RecipesList extends Table {
	private final ArrayList<Recipe> receipts= new ArrayList<>(10);
	
	/**
	 * adds teh receipes to a list
	 */
	public RecipesList() {
		setBackground(WE.getEngineView().getSkin().getDrawable("default-window"));
		receipts.add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.SULFUR,
					CollectibleType.COAL
				},
				"TFlint",
				CollectibleType.EXPLOSIVES
			)
		);
		
		receipts.add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.WOOD,
					CollectibleType.WOOD
				},
				"Construction Kit",
				CollectibleType.TOOLKIT
			)
		);
		
		receipts.add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.WOOD,
					CollectibleType.COAL
				},
				"Torch",
				CollectibleType.TORCH
			)
		);
		
		int y = 0;
		for (Recipe receipt : receipts) {
			Image actor = receipt.getImage();
			actor.setPosition(0, y);
			y+=100;
			addActor(actor);
		}
	}

	public ArrayList<Recipe> getReceipts() {
		return receipts;
	}
	
	
	
	class Recipe {
		protected final CollectibleType[] ingredients;
		protected final String name;
		/**
		 * what is the results of the recipe.
		 */
		protected final CollectibleType result;

		public Recipe(CollectibleType[] ingredients, String name, CollectibleType result) {
			this.ingredients = ingredients;
			this.name = name;
			this.result = result;
		}
		
		public Image getImage(){
			return new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', result.getId(), 0)
					)
				)
			);
		}
		
	}
	
}
