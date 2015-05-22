package com.bombinggames.caveland.Game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.bombinggames.caveland.GameObjects.Collectible;
import com.bombinggames.caveland.GameObjects.Collectible.CollectibleType;
import com.bombinggames.caveland.GameObjects.Inventory;
import com.bombinggames.wurfelengine.Core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.WE;

/**
 *Shows a HUD for crafting via inventory
 * @author Benedikt Vogler
 */
public class Crafting extends Table {
	private final Inventory inventory;
	private final RecipesList recipes = new RecipesList();

	/**
	 * creates a new inventory
	 * @param inventory 
	 */
	public Crafting(Inventory inventory) {
		this.inventory = inventory;
		RecipesList.Recipe recipe = findRecipe();
		if (recipe!=null) {
			//A
			Image a = new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', recipe.ingredients[0].getId(), 0)
					)
				)
			);
			addActor(a);


			//+
			Label plus =new Label("+", WE.getEngineView().getSkin());
			plus.setPosition(100, 0);
			addActor(plus);

			//B
			Image b = new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', recipe.ingredients[1].getId(), 0)
					)
				)
			);
			b.setPosition(150, 0);
			addActor(b);

//			//+ maybe C
//			if (ingredients.length>2 && ingredients[0]!=null) {
//				Label plus2 =new Label("+", WE.getEngineView().getSkin());
//				plus2.setPosition(300, 0);
//				addActor(plus2);
//			}

			//=
			Label equals =new Label("=", WE.getEngineView().getSkin());
			equals.setPosition(300, 0);
			addActor(equals);

			//result
			Image resultImage = new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', recipe.result.getId(), 0)
					)
				)
			);
			resultImage.setPosition(350, 0);
			addActor(resultImage);
		} else {
			addActor(new Label("not enough ingredients", WE.getEngineView().getSkin()));
		}
	}
	
	/**
	 * check wheter you can craft with the ingredients
	 * @return 
	 */
	public RecipesList.Recipe findRecipe(){
		CollectibleType[] invent = inventory.getContentDef();
		RecipesList.Recipe finalRecipe = null;
		for (RecipesList.Recipe recipe : recipes.receipts) {
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
		RecipesList.Recipe recipe = findRecipe();
		if (recipe != null){
			Collectible a = inventory.fetchCollectible(recipe.ingredients[0]);
			Collectible b = inventory.fetchCollectible(recipe.ingredients[1]);
			//Collectible c = inventory.fetchCollectible(recipe.ingredients[2]);
			if (a!=null && b!=null) {
				//create new object at same position of old iinventory items
				Collectible.create(recipe.result).spawn(a.getPosition().cpy());
				//delete them
				a.disposeFromMap();
				b.disposeFromMap();
				clear();//empty the crafting menu
			}
		}
	}
}
