/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.bombinggames.caveland.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.bombinggames.caveland.gameobjects.collectibles.CollectibleType;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benedikt Vogler
 */
public class Recipe {
	/**
	 * a list of the ingredient
	 */
	protected final CollectibleType[] ingredients;
	/**
	 * what is the result of the recipe.
	 */
	private final CollectibleType result;
	private final Class<? extends AbstractEntity> resultClass;

	/**
	 * 
	 * @param ingredients
	 * @param name
	 * @param result 
	 * @see #Recipe(CollectibleType[], String, java.lang.Class) 
	 */
	public Recipe(CollectibleType[] ingredients, String name, CollectibleType result) {
		this.ingredients = ingredients;
		this.result = result;
		this.resultClass = null;
	}

	/**
	 * 
	 * @param ingredients
	 * @param name
	 * @param result 
	 * @see #Recipe(CollectibleType[], String, CollectibleType) 
	 */
	public Recipe(CollectibleType[] ingredients, String name, Class<? extends AbstractEntity> result) {
		this.ingredients = ingredients;
		this.result = null;
		this.resultClass = result;
	}

	/**
	 * 
	 * @return can return null if failed to get image
	 */
	public Image getResultImage(){
		if (resultIsCollectible()) {
			return new Image(
				new SpriteDrawable(
					new Sprite(
						AbstractGameObject.getSprite('e', result.getId(),(byte)  0)
					)
				)
			);
		} else {
			try {
				return new Image(
					new SpriteDrawable(
						new Sprite(
							AbstractGameObject.getSprite('e', resultClass.newInstance().getSpriteId(), (byte) 0)
						)
					)
				);
			} catch (InstantiationException | IllegalAccessException ex) {
				Logger.getLogger(CraftingRecipesList.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return null;
	}

	/**
	 *
	 * @param slot
	 * @return
	 */
	public Image getIngredientImage(int slot){
		return new Image(
			new SpriteDrawable(
				new Sprite(
					AbstractGameObject.getSprite('e', ingredients[slot].getId(), (byte) 0)
				)
			)
		);
	}

	/**
	 *
	 * @return
	 */
	public boolean resultIsCollectible(){
		return this.result != null;
	}

	/**
	 * Can be null if not a collectible.
	 * @return
	 */
	public CollectibleType getResultType() {
		return result;
	}

	/**
	 *
	 * @return
	 */
	public Class<? extends AbstractEntity> getResultClass() {
		return resultClass;
	}
		
}
