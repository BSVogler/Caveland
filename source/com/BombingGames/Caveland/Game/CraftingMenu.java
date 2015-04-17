package com.BombingGames.Caveland.Game;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 *
 * @author Benedikt Vogler
 */
public class CraftingMenu extends Table {

	public CraftingMenu() {
		addActor(
			new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', 47, 0)
					)
				)
			)
		);
		addActor(
			new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', 25, 5)
					)
				)
			)
		);
	}
	
}
