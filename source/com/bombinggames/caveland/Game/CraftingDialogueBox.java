package com.bombinggames.caveland.Game;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.bombinggames.caveland.Game.CraftingRecipesList.Recipe;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.collectibles.Collectible;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.caveland.GameObjects.collectibles.Inventory;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *A HUD for crafting via inventory
 * @author Benedikt Vogler
 */
public class CraftingDialogueBox extends ActionBox {
	private final Inventory inventory;
	private final CraftingRecipesList knownRecipes = new CraftingRecipesList();
	private int selectionNum = -1;

	/**
	 * creates a new inventory
	 * @param view
	 * @param player 
	 */
	public CraftingDialogueBox(CLGameView view, Ejira player) {
		super("Crafting", BoxModes.CUSTOM, null);
		this.inventory = player.getInventory();
		
		fillWindowContent();
		
		setSelectAction((boolean up, int result, AbstractEntity actor) -> {
			ArrayList<Recipe> recList = findMatchingRecipes();
			if (!recList.isEmpty()) {
				
				if (!up) {
					selectionNum++;
				} else {
					selectionNum--;
				}

				//clamp
				if (selectionNum < 0) {
					selectionNum = recList.size()-1;
				}

				if (selectionNum >= recList.size()) {
					selectionNum = 0;
				}

				fillWindowContent();
			}
		});
	}
	
	private void fillWindowContent(){
		ArrayList<Recipe> recipes = findMatchingRecipes();
		if (!recipes.isEmpty()) {
			getWindow().clearChildren();
			Recipe recipe = recipes.get(selectionNum<0?0:selectionNum);
			//A
			Image imgA = recipe.getIngredientImage(0);
			getWindow().addActor(imgA);

			//+
			Label plus = new Label("+", WE.getEngineView().getSkin());
			plus.setPosition(100, 0);
			getWindow().addActor(plus);

			//B
			Image imgB = recipe.getIngredientImage(1);
			imgB.setPosition(150, 0);
			getWindow().addActor(imgB);
			//+ maybe C
			if (recipe.ingredients.length > 2) {
				Label plus2 = new Label("+", WE.getEngineView().getSkin());
				plus2.setPosition(250, 0);
				getWindow().addActor(plus2);

				//C
				Image imgC = recipe.getIngredientImage(2);
				imgC.setPosition(300, 0);
				getWindow().addActor(imgC);

			}
			
			//=
			Label equals = new Label("=", WE.getEngineView().getSkin());
			getWindow().addActor(equals);
			equals.setPosition(420, 0);
			
			//result
			Image resultImage = findMatchingRecipes().get(selectionNum<0?0:selectionNum).getResultImage();
			resultImage.setPosition(450, 0);
			getWindow().addActor(resultImage);
		} else {
			getWindow().addActor(new Label("Not enough ingredients.", WE.getEngineView().getSkin()));
		}
	}
	/**
	 * check wheter you can craft with the ingredients
	 * @return 
	 */
	public ArrayList<CraftingRecipesList.Recipe> findMatchingRecipes(){
		ArrayList<CraftingRecipesList.Recipe> matchingRecipes = new ArrayList<>(2);
		CollectibleType[] invent = inventory.getContentDef();
		for (CraftingRecipesList.Recipe recipe : knownRecipes.getReceipts()) {
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
			if (ing1 != 1 && ing1 != 1  && recipe.ingredients[1]==invent[1])
				ing2 = 1;
			if (ing1 != 2 && ing1 != 2  && recipe.ingredients[1]==invent[2])
				ing2 = 2;
			
			if (recipe.ingredients.length > 2) {
				int ing3 = -1;//not found in receipt
				if (ing1 != -1 && ing1 != 0 && ing2 != -1 && ing2 != 0 && recipe.ingredients[2]==invent[0])
					ing3 = 0;
				if (ing1 != -1 && ing1 != 1 && ing2 != -1 && ing2 != 1 && recipe.ingredients[2]==invent[1])
					ing3 = 1;
				if (ing1 != -1 && ing1 != 2 && ing2 != -1 && ing2 != 2 && recipe.ingredients[2]==invent[2])
					ing3 = 2;

				if (ing1 != -1 && ing2 != -1 && ing3 != -1)
					matchingRecipes.add(recipe);
			} else {
				if (ing1 != -1 && ing2 != -1)
					matchingRecipes.add(recipe);
			}
		}
		return matchingRecipes;
	}
	
	/**
	 * removes the items from inventory
	 * @param recipe
	 */
	public void craft(Recipe recipe){
		if (knownRecipes != null){
			Collectible a = inventory.retrieveCollectible(recipe.ingredients[0]);
			Collectible b = inventory.retrieveCollectible(recipe.ingredients[1]);
			boolean thirdingredient = true;
			Collectible c = null;
			if (recipe.ingredients.length > 2) {
				thirdingredient = false;
				c = inventory.getCollectible(recipe.ingredients[2]);
			}
			if (a != null && b != null && (thirdingredient || c != null)) {//can be crafted
				//create new object at same position of inventory
				if (recipe.resultIsCollectible()) {
					recipe.getResultType().createInstance().spawn(inventory.getPosition().cpy());
				} else {
					try {
						recipe.getResultClass().newInstance().spawn(inventory.getPosition().cpy());
					} catch (InstantiationException | IllegalAccessException ex) {
						Logger.getLogger(CraftingDialogueBox.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				//delete them
				a.dispose();
				a = null;
				b.dispose();
				b = null;
				if (c != null) {
					c.dispose();
				}
				WE.SOUND.play("craft");
				clear();//empty the crafting menu
			}
			//crafting failed: items still there, so put them back
			if (a != null) {
				inventory.add(a);
			}
			if (b != null) {
				inventory.add(b);
			}
		}
	}

	@Override
	public int confirm(AbstractEntity actor) {
		super.confirm(actor);
		ArrayList<CraftingRecipesList.Recipe> recipes = findMatchingRecipes();
		if (!recipes.isEmpty()) {
			craft(recipes.get(selectionNum));
		}
		return selectionNum;
	}
}
