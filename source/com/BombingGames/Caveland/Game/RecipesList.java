package com.bombinggames.caveland.Game;

import com.bombinggames.caveland.GameObjects.Collectible.CollectibleType;
import com.bombinggames.wurfelengine.Core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.WE;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class RecipesList extends Table {
	ArrayList<Recipe> receipts= new ArrayList<>(10);
	
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
				"Bausatz",
				CollectibleType.TOOLKIT
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
	
	class Recipe {
		CollectibleType[] ingredients;
		String name;
		CollectibleType result;

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
