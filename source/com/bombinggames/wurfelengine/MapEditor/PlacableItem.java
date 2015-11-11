package com.bombinggames.wurfelengine.MapEditor;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;

/**
 * An item for the {@link PlacableTable}
 * @author Benedikt Vogler
 */
public class PlacableItem extends Stack {

	/**
	 * 
	 * @param parent
	 * @param drawable
	 * @param result result of a click on it
	 */
	public PlacableItem(PlacableTable parent, TextureRegionDrawable drawable, ClickListener result) {
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
