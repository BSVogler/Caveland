package com.bombinggames.caveland.Game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
	private static final Color COLORNOTAVAILABLE = new Color(1, 1, 1, 0.3f);

	/**
	 * creates a new inventory
	 * @param view
	 * @param player 
	 */
	public CraftingDialogueBox(CLGameView view, Ejira player) {
		super("Crafting", BoxModes.CUSTOM, null);
		this.inventory = player.getInventory();
		
		//add matching first then unavailable ones
		ArrayList<Recipe> orderedList = getRecipeOrdered();
		for (byte i = 0; i < orderedList.size(); i++) {
			if (orderedList.get(i).resultIsCollectible()) {
				addSelection(new SelectionOption(i, orderedList.get(i).getResultType().toString()));
			} else {
				addSelection(new SelectionOption(i, orderedList.get(i).getResultClass().getSimpleName()));
			}
		}
		
		setSelectAction((boolean up, ActionBox.SelectionOption result, AbstractEntity actor) -> {
			ArrayList<Recipe> recList = getRecipeOrdered();
			if (!recList.isEmpty()) {
				updateContent();
			}
		});
	}
	
	private ArrayList<Recipe> getRecipeOrdered(){
		ArrayList<Recipe> list = findMatchingRecipes();
		CraftingRecipesList rest = (CraftingRecipesList) knownRecipes.clone();
		rest.removeAll(findMatchingRecipes());
		list.addAll(rest);
		return list;
	}
	
	@Override
	public void updateContent(){
		ArrayList<Recipe> orderedList = getRecipeOrdered();
		if (!orderedList.isEmpty()) {
			getWindow().clearChildren();
			
			Recipe recipe = orderedList.get(getSelected().id);
			//A
			Image imgA = recipe.getIngredientImage(0);
			if (inventory.contains(recipe.ingredients[0]) == 0) {
				imgA.setColor(COLORNOTAVAILABLE);
			}
			getWindow().addActor(imgA);

			//+
			Label plus = new Label("+", WE.getEngineView().getSkin());
			plus.setPosition(100, 0);
			getWindow().addActor(plus);

			//B
			Image imgB = recipe.getIngredientImage(1);
			imgB.setPosition(150, 0);
			if (inventory.contains(recipe.ingredients[1]) == 0) {
				imgB.setColor(COLORNOTAVAILABLE);
			}
			getWindow().addActor(imgB);
			//+ maybe C
			if (recipe.ingredients.length > 2) {
				Label plus2 = new Label("+", WE.getEngineView().getSkin());
				plus2.setPosition(250, 0);
				getWindow().addActor(plus2);

				//C
				Image imgC = recipe.getIngredientImage(2);
				imgC.setPosition(300, 0);
				if (inventory.contains(recipe.ingredients[2]) == 0) {
					imgC.setColor(COLORNOTAVAILABLE);
				}
				getWindow().addActor(imgC);
			}
			
			//=
			Label equals = new Label("=", WE.getEngineView().getSkin());
			getWindow().addActor(equals);
			equals.setPosition(420, 0);
			
			//result
			Image resultImage = orderedList.get(getSelected().id).getResultImage();
			resultImage.setPosition(450, 0);
			if (!canCraft(recipe, inventory.getContentDef())) {
				resultImage.setColor(COLORNOTAVAILABLE);
			}
			getWindow().addActor(resultImage);
		} else {
			getWindow().addActor(new Label("Not enough ingredients.", WE.getEngineView().getSkin()));
		}
	}
	
	/**
	 * 
	 * @param recipe
	 * @param inventory
	 * @return 
	 */
	public boolean canCraft(Recipe recipe, CollectibleType[] inventory){
		int ing1 = -1;//not found in receipt
		if (recipe.ingredients[0]==inventory[0])
			ing1 = 0;
		if (recipe.ingredients[0]==inventory[1])
			ing1 = 1;
		if (recipe.ingredients[0]==inventory[2])
			ing1 = 2;

		int ing2 = -1;//not found in receipt
		if (ing1 != -1 && ing1 != 0 && recipe.ingredients[1]==inventory[0])
			ing2 = 0;
		if (ing1 != 1 && ing1 != 1  && recipe.ingredients[1]==inventory[1])
			ing2 = 1;
		if (ing1 != 2 && ing1 != 2  && recipe.ingredients[1]==inventory[2])
			ing2 = 2;

		if (recipe.ingredients.length > 2) {
			int ing3 = -1;//not found in receipt
			if (ing1 != -1 && ing1 != 0 && ing2 != -1 && ing2 != 0 && recipe.ingredients[2]==inventory[0])
				ing3 = 0;
			if (ing1 != -1 && ing1 != 1 && ing2 != -1 && ing2 != 1 && recipe.ingredients[2]==inventory[1])
				ing3 = 1;
			if (ing1 != -1 && ing1 != 2 && ing2 != -1 && ing2 != 2 && recipe.ingredients[2]==inventory[2])
				ing3 = 2;

			if (ing1 != -1 && ing2 != -1 && ing3 != -1)
				return true;
		} else {
			if (ing1 != -1 && ing2 != -1)
				return true;
		}
		return false;
	}
	
	/**
	 * check wheter you can craft with the ingredients
	 *
	 * @return
	 */
	public ArrayList<Recipe> findMatchingRecipes() {
		ArrayList<Recipe> matchingRecipes = new ArrayList<>(2);
		CollectibleType[] invent = inventory.getContentDef();
		for (Recipe recipe : knownRecipes) {
			if (canCraft(recipe, invent)) {
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
	public SelectionOption confirm(AbstractEntity actor) {
		SelectionOption result = super.confirm(actor);
		if (!getRecipeOrdered().isEmpty()) {
			craft(getRecipeOrdered().get(getSelected().id));
		}
		return result;
	}
}
