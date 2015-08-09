/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.MapEditor;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;

/**
 *
 * @author Benedikt Vogler
 */
public class SelectionItem extends Stack {

	public SelectionItem(PlacableTable parent, TextureRegionDrawable drawable, ClickListener result) {
		//background
		Image bgIcon = new Image(AbstractGameObject.getSprite('i', 10, 0));
		
		addActor(bgIcon);
		
		//foreground
		Image fgImg = new Image(drawable);
		addActor(fgImg);
		fgImg.addListener(result);
	}
	
}
