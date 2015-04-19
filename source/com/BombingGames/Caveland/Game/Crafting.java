package com.BombingGames.Caveland.Game;

import com.BombingGames.Caveland.GameObjects.Collectible;
import com.BombingGames.Caveland.GameObjects.Collectible.CollectibleType;
import com.BombingGames.Caveland.GameObjects.Inventory;
import com.BombingGames.Caveland.GameObjects.TFlint;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;

/**
 *Shows a HUD for crafting via inventory
 * @author Benedikt Vogler
 */
public class Crafting extends Table {
	private AbstractEntity result;
	private final Inventory inventory;

	public Crafting(Inventory inventory) {
		this.inventory = inventory;
		this.result = new TFlint();
		
		if (canCraft()) {
			//A
			Image a = new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', inventory.getCollectible(CollectibleType.SULFUR).getId(), 0)
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
						AbstractGameObject.getSprite('e', inventory.getCollectible(CollectibleType.COAL).getId(), 0)
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
						AbstractGameObject.getSprite('e', result.getId(), result.getValue())
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
	public boolean canCraft(){
		Array<CollectibleType> tmp = new Array<>(inventory.getContentDef());
		return tmp.contains(CollectibleType.COAL, false) && tmp.contains(CollectibleType.SULFUR, false);
	}
	
	/**
	 * removes the items from inventory
	 */
	public void craft(){
		if (canCraft()){
			Collectible a = inventory.fetchCollectible(CollectibleType.COAL);
			Collectible b = inventory.fetchCollectible(CollectibleType.SULFUR);
			if (a!=null && b!=null) {
				inventory.add(Collectible.create(CollectibleType.EXPLOSIVES));
			}
		}
	}
}
