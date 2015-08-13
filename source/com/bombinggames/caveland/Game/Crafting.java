package com.bombinggames.caveland.Game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.collectibles.Collectible;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.caveland.GameObjects.collectibles.Inventory;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;

/**
 *A HUD for crafting via inventory
 * @author Benedikt Vogler
 */
public class Crafting extends ActionBox {
	private final Inventory inventory;
	private final CraftingRecipesList recipes = new CraftingRecipesList();

	/**
	 * creates a new inventory
	 * @param view
	 * @param player 
	 */
	public Crafting(CustomGameView view, Ejira player) {
		super(view, "Crafting", BoxModes.CUSTOM, null);
		this.inventory = player.getInventory();
		CraftingRecipesList.Recipe recipe = findRecipe();
		if (recipe!=null) {
			//A
			Image a = new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', recipe.ingredients[0].getId(), 0)
					)
				)
			);
			getWindow().addActor(a);


			//+
			Label plus =new Label("+", WE.getEngineView().getSkin());
			plus.setPosition(100, 0);
			getWindow().addActor(plus);

			//B
			Image b = new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', recipe.ingredients[1].getId(), 0)
					)
				)
			);
			b.setPosition(150, 0);
			getWindow().addActor(b);

//			//+ maybe C
//			if (ingredients.length>2 && ingredients[0]!=null) {
//				Label plus2 =new Label("+", WE.getEngineView().getSkin());
//				plus2.setPosition(300, 0);
//				addActor(plus2);
//			}

			//=
			Label equals =new Label("=", WE.getEngineView().getSkin());
			equals.setPosition(300, 0);
			getWindow().addActor(equals);

			//result
			Image resultImage = new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', recipe.result.getId(), 0)
					)
				)
			);
			resultImage.setPosition(350, 0);
			getWindow().addActor(resultImage);
		} else {
			getWindow().addActor(new Label("not enough ingredients", WE.getEngineView().getSkin()));
		}
	}
	
	/**
	 * check wheter you can craft with the ingredients
	 * @return 
	 */
	public CraftingRecipesList.Recipe findRecipe(){
		CollectibleType[] invent = inventory.getContentDef();
		CraftingRecipesList.Recipe finalRecipe = null;
		for (CraftingRecipesList.Recipe recipe : recipes.getReceipts()) {
			int ing1 = -1;//not found in receipt
			if (recipe.ingredients[0]==invent[0])
				ing1 = 0;
			if (recipe.ingredients[0]==invent[1])
				ing1 = 1;
			if (recipe.ingredients[0]==invent[2])
				ing1 = 2;
			int ing2 = -1;//not found in receipt
			if (ing1 != -1 && ing1 != 0 && recipe.ingredients[1]==invent[0])
				ing2 = 0;
			if (ing1 != 1 && recipe.ingredients[1]==invent[1])
				ing2 = 1;
			if (ing1 != 2 && recipe.ingredients[1]==invent[2])
				ing2 = 2;
			
			if (recipe.ingredients.length>2) {
				int ing3 = -1;//not found in receipt
				if (ing1 != -1 && ing1 != 0 && ing2 != -1 && ing2 != 0 && recipe.ingredients[2]==invent[0])
					ing3 = 0;
				if (ing1 != -1 && ing1 != 1 && ing2 != -1 && ing2 != 1 && recipe.ingredients[2]==invent[1])
					ing3 = 1;
				if (ing1 != -1 && ing1 != 2 && ing2 != -1 && ing2 != 2 && recipe.ingredients[2]==invent[2])
					ing3 = 2;

				if (ing1 != -1 && ing2 != -1 && ing3 != -1)
					finalRecipe = recipe;
			} else {
				if (ing1 != -1 && ing2 != -1)
					finalRecipe = recipe;
			}
		}
		return finalRecipe;
	}
	
	/**
	 * removes the items from inventory
	 */
	public void craft(){
		CraftingRecipesList.Recipe recipe = findRecipe();
		if (recipe != null){
			Collectible a = inventory.retrieveCollectible(recipe.ingredients[0]);
			Collectible b = inventory.retrieveCollectible(recipe.ingredients[1]);
			boolean thirdingredient = true;
			Collectible c = null;
			if (recipe.ingredients.length>2) {
				thirdingredient = false;
				c = inventory.getCollectible(recipe.ingredients[2]);
			}
			if (a != null && b != null && (thirdingredient || c != null)) {//can be crafted
				//create new object at same position of inventory
				recipe.result.createInstance().spawn(inventory.getPosition());
				//delete them
				a.dispose();
				a = null;
				b.dispose();
				b = null;
				if (c != null)
					c.dispose();
				Controller.getSoundEngine().play("metallic");
				clear();//empty the crafting menu
			}
			//crafting failed: items still there, so put them back
			if (a != null)
				inventory.add(a);
			if (b != null)
				inventory.add(b);
		}
	}

	@Override
	public int confirm(CustomGameView view, AbstractEntity actor) {
		craft();
		return super.confirm(view, actor);
	}

	@Override
	public int cancel(CustomGameView view, AbstractEntity actor) {
		return super.cancel(view, actor);
	}
}
