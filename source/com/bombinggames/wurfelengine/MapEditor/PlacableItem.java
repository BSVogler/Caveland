package com.bombinggames.wurfelengine.mapeditor;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;

/**
 * An item for the {@link PlacableTable}
 * @author Benedikt Vogler
 */
public class PlacableItem extends Stack {

	/**
	 * 
	 * @param drawable
	 * @param result result of a click on it
	 */
	public PlacableItem(TextureRegionDrawable drawable, ClickListener result) {
		//background
		Image bgIcon = new Image(AbstractGameObject.getSprite('i', (byte) 10,(byte)  0));
		
		addActor(bgIcon);
		bgIcon.addListener(result);
		
		//foreground
		Image fgImg = new Image(drawable);
		addActor(fgImg);
		fgImg.addListener(result);
	}
	
}
