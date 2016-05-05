/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2016 Benedikt Vogler.
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
package com.bombinggames.wurfelengine.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A table showing the registered entities.
 * @author Benedikt Vogler
 */
public class EntityTable extends AbstractPlacableTable {
	private Class<? extends AbstractEntity> entityClass;
	
	public EntityTable() {
		super();
	}

	@Override
	public void show(GameView view) {
		if (!isVisible()) {
			setVisible(true);
		}

		//setScale(5f);
		if (!hasChildren()) {
			byte foundItems = 0;
			//add every registered entity class
			for (Map.Entry<String, Class<? extends AbstractEntity>> entry
				: AbstractEntity.getRegisteredEntities().entrySet()
			) {
				try {
					add(
						new PlacableItem(
							new EntityDrawable(entry.getValue()),
							new EntityListener(entry.getKey(), entry.getValue(), foundItems)
						)
					);
				} catch (InstantiationException | IllegalAccessException ex) {
					Gdx.app.error(this.getClass().getName(), "Please make sure that every registered entity has a construcor without arguments");
					Logger.getLogger(AbstractPlacableTable.class.getName()).log(Level.SEVERE, null, ex);
				}
				foundItems++;
				if (foundItems % 4 == 0) {
					row();//make new row
				}
			}
		}
	}
	
	/**
	 * Trys returning a new instance of a selected entity class.
	 * @return if it fails returns null 
	 */
	public AbstractEntity getEntity(){
		if (entityClass == null) {
			return null;
		}
		try {
			AbstractEntity ent = entityClass.newInstance();
			if (getValue() > -1) {
				ent.setSpriteValue(getValue());
			}
			return ent;
		} catch (InstantiationException | IllegalAccessException ex) {
			Logger.getLogger(CursorInfo.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
		/**
	 *
	 * @param view
	 */
	protected void showEntities(GameView view) {
		if (getEntity() == null) {//no init value for entity
			setEntity(
				AbstractEntity.getRegisteredEntities().keySet().iterator().next(),
				AbstractEntity.getRegisteredEntities().values().iterator().next()
			);
		}

		clearChildren();
		show(view);
	}
	
	/**
	 * Sets the current color to this entity class.
	 * @param name name which gets displayed
	 * @param entclass
	 */
	public void setEntity(String name, Class<? extends AbstractEntity> entclass) {
		entityClass = entclass;
		//label.setText(name);
	}
		
	
	/**
	 * detects a click on an entity in the list
	 */
	private class EntityListener extends ClickListener {

		private final Class<? extends AbstractEntity> entclass;
		private final String name;
		/**
		 * selectionId of this listener
		 */
		private final byte id;
		/**
		 * 
		 * @param name
		 * @param entclass
		 * @param id selectionId of this listener
		 */
		EntityListener(String name, Class<? extends AbstractEntity> entclass, byte id) {
			this.entclass = entclass;
			this.name = name;
			this.id = id;
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			setEntity(name, entclass);
			if (id <= getChildren().size) {
				for (Actor c : getChildren()) {
					c.setScale(0.5f);
				}
				getChildren().get(id).setScale(0.6f);
			}
		}
	}
	
}
