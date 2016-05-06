package com.bombinggames.wurfelengine.mapeditor;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;

/**
 * An item for the {@link com.bombinggames.wurfelengine.mapeditor.AbstractPlacableTable}
 *
 * @author Benedikt Vogler
 */
public class PlacableItem extends Stack {
	
	private final Image bgIcon;
	private final Image fgImg;

	/**
	 *
	 * @param drawable
	 * @param result result of a click on it
	 */
	public PlacableItem(TextureRegionDrawable drawable, ClickListener result) {
		//background
		bgIcon = new Image(AbstractGameObject.getSprite('i', (byte) 10, (byte) 0));
		bgIcon.setPosition(50, 0);
		addActor(bgIcon);
		bgIcon.addListener(result);

		//foreground
		fgImg = new Image(drawable);
		addActor(fgImg);
		fgImg.addListener(result);
	}
	
	@Override
	public void setScale(float scaleXY) {
		//super.setScale(scaleXY);
		if (fgImg != null) {
			fgImg.setScale(scaleXY);
		}
	}
}
