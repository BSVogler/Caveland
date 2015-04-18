package com.BombingGames.Caveland.Game;

import com.BombingGames.Caveland.GameObjects.Bausatz;
import com.BombingGames.Caveland.GameObjects.Collectible;
import com.BombingGames.Caveland.GameObjects.TFlint;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
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
	ArrayList<Recipt> receipts= new ArrayList<>(10);
	
	public RecipesList() {
		receipts.add(
			new Recipt(
				new AbstractEntity[]{
					Collectible.create(Collectible.CollectibleType.SULFUR),
					Collectible.create(Collectible.CollectibleType.COAL)
				},
				"TFlint",
				new TFlint()
			)
		);
		
		receipts.add(
			new Recipt(
				new AbstractEntity[]{
					Collectible.create(Collectible.CollectibleType.WOOD),
					Collectible.create(Collectible.CollectibleType.WOOD)
				},
				"Bausatz",
				new Bausatz()
			)
		);
		
		int y = 0;
		for (Recipt receipt : receipts) {
			Image actor = receipt.getImage();
			actor.setPosition(0, y);
			y+=100;
			addActor(actor);
		}
	}
	
	class Recipt {
		AbstractEntity[] ingredients;
		String name;
		AbstractEntity result;

		public Recipt(AbstractEntity[] ingredients, String name, AbstractEntity result) {
			this.ingredients = ingredients;
			this.name = name;
			this.result = result;
		}
		
		public Image getImage(){
			return new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', result.getId(), result.getValue())
					)
				)
			);
		}
		
	}
	
}
