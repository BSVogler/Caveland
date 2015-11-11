package com.bombinggames.caveland.Game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.bombinggames.caveland.GameObjects.MineCart;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *A list which stores the possibles recipes.
 * @author Benedikt Vogler
 */
public class CraftingRecipesList extends Table {
	private final ArrayList<Recipe> receipts= new ArrayList<>(10);
	
	/**
	 * adds teh receipes to a list
	 */
	public CraftingRecipesList() {
		receipts.add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Sulfur,
					CollectibleType.Coal
				},
				"TFlint",
				CollectibleType.Explosives
			)
		);
		
		receipts.add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Wood,
					CollectibleType.Wood
				},
				"Construction Kit",
				CollectibleType.Toolkit
			)
		);
		
		receipts.add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Wood,
					CollectibleType.Coal
				},
				"Torch",
				CollectibleType.Torch
			)
		);
		
		receipts.add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Iron,
					CollectibleType.Iron
				},
				"Rails Construction Kit",
				CollectibleType.Rails
			)
		);
		
		receipts.add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Iron,
					CollectibleType.Iron
				},
				"Power Cable",
				CollectibleType.Powercable
			)
		);
		
		receipts.add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Iron,
					CollectibleType.Iron,
					CollectibleType.Wood
				},
				"Minecart",
				MineCart.class
			)
		);
	}

	/**
	 * 
	 * @return 
	 */
	public ArrayList<Recipe> getReceipts() {
		return receipts;
	}
	
	
	
	public class Recipe {
		/**
		 * a list of the ingredient
		 */
		protected final CollectibleType[] ingredients;
		protected final String name;
		/**
		 * what is the result of the recipe.
		 */
		private final CollectibleType result;
		private final Class<? extends AbstractEntity> resultClass;

		/**
		 * 
		 * @param ingredients
		 * @param name
		 * @param result 
		 * @see #Recipe(CollectibleType[], String, java.lang.Class) 
		 */
		public Recipe(CollectibleType[] ingredients, String name, CollectibleType result) {
			this.ingredients = ingredients;
			this.name = name;
			this.result = result;
			this.resultClass = null;
		}
		
		/**
		 * 
		 * @param ingredients
		 * @param name
		 * @param result 
		 * @see #Recipe(CollectibleType[], String, CollectibleType) 
		 */
		public Recipe(CollectibleType[] ingredients, String name, Class<? extends AbstractEntity> result) {
			this.ingredients = ingredients;
			this.name = name;
			this.result = null;
			this.resultClass = result;
		}
		
		/**
		 * 
		 * @return can return null if failed to get image
		 */
		public Image getResultImage(){
			if (resultIsCollectible()) {
				return new Image(
					new SpriteDrawable(
						new Sprite(
							AbstractGameObject.getSprite('e', result.getId(),(byte)  0)
						)
					)
				);
			} else {
				try {
					return new Image(
						new SpriteDrawable(
							new Sprite(
								AbstractGameObject.getSprite('e', resultClass.newInstance().getSpriteId(), (byte) 0)
							)
						)
					);
				} catch (InstantiationException | IllegalAccessException ex) {
					Logger.getLogger(CraftingRecipesList.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			return null;
		}
		
		public Image getIngredientImage(int slot){
			return new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', ingredients[slot].getId(), (byte) 0)
					)
				)
			);
		}
		
		public boolean resultIsCollectible(){
			return this.result != null;
		}

		public CollectibleType getResultType() {
			return result;
		}
		
		public Class<? extends AbstractEntity> getResultClass() {
			return resultClass;
		}
		
		
	}
}
