/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2014 Benedikt Vogler.
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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A table containing all blocks where you can choose your block.
 *
 * @author Benedikt Vogler
 */
public class PlacableTable extends Table {

	private boolean placeBlocks = true;
	/**
	 * list position
	 */
	private byte selected;
	private byte id;
	private byte value;
	private Class<? extends AbstractEntity> entityClass;
	/**
	 * stores the block drawables
	 */
	private final ArrayList<BlockDrawable> blockDrawables = new ArrayList<>(40);
	
	private Toolbar parent;

	/**
	 *
	 * @param parent
	 */
	public PlacableTable(Toolbar parent) {
		setWidth(400);
		setHeight(Gdx.graphics.getHeight() * 0.80f);
		setY(10);
		setX(30);
		this.parent = parent;
	}

	/**
	 *
	 * @param view
	 */
	public void show(GameView view) {
		if (!isVisible()) {
			setVisible(true);
		}

		//setScale(5f);
		if (!hasChildren()) {
			byte foundItems = 0;
			if (placeBlocks) {//add blocks
				blockDrawables.clear();
				//add air
				BlockDrawable blockDrawable = new BlockDrawable((byte) 0, (byte) 0, 0.35f);
				blockDrawables.add(blockDrawable);
				add(
					new PlacableItem(
						blockDrawable,
						new BlockListener((byte) 0, (byte) 0)
					)
				);
				foundItems++;
				//add rest
				for (byte i = 1; i < RenderCell.OBJECTTYPESNUM; i++) {//add every possible block
					if (RenderCell.isSpriteDefined(i,(byte)0) //add defined blocks
						|| !RenderCell.getName(i, (byte) 0).equals("undefined")) {
						blockDrawable = new BlockDrawable(i, (byte) 0, 0.35f);
						blockDrawables.add(blockDrawable);
						add(
							new PlacableItem(
								blockDrawable,
								new BlockListener(foundItems, i)
							)
						);
						foundItems++;
						if (foundItems % 4 == 0) {
							row();//make new row
						}
					}
				}
			} else {
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
						Logger.getLogger(PlacableTable.class.getName()).log(Level.SEVERE, null, ex);
					}
					foundItems++;
					if (foundItems % 4 == 0) {
						row();//make new row
					}
				}
			}
		}
	}

	/**
	 *
	 */
	public void hide() {
		if (hasChildren()) {
			clear();
		}

		if (isVisible()) {
			setVisible(false);
		}
	}

	/**
	 *
	 * @param view
	 */
	protected void showBlocks(GameView view) {
		placeBlocks = true;
		clearChildren();
		show(view);
	}

	/**
	 *
	 * @param view
	 */
	protected void showEntities(GameView view) {
		placeBlocks = false;
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
	 * selects the item //TODO needs more generic method including entities
	 * @param pos the pos of the listener
	 */
	void selectBlock(byte pos) {
		if (pos <= getChildren().size) {
			selected = pos;
			for (Actor c : getChildren()) {
				c.setScale(0.35f);
			}
			getChildren().get(selected).setScale(0.4f);
		}
	}

	/**
	 * sets the value of the selected
	 * @param value 
	 */
	void setValue(byte value) {
		this.value = value;
		blockDrawables.get(selected).setValue(value);
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
			if (value > -1) {
				ent.setSpriteValue(value);
			}
			return ent;
		} catch (InstantiationException | IllegalAccessException ex) {
			Logger.getLogger(CursorInfo.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
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
	
	private void setId(byte id) {
		this.id = id;
	}
	/**
	 *
	 * @return
	 */
	public byte getId() {
		return id;
	}
		
	/**
	 *
	 * @return
	 */
	public byte getValue() {
		return value;
	}
	
	public int getBlock(){
		return (value<<8)+id;
	}

	void select(byte blockId, byte blockValue) {
//		for (Actor ch : getChildren()) {
//			((PlacableItem) ch).getget(id);
//		}
//		selectBlock(id);
	}

	/**
	 * detects a click on an entity in the list
	 */
	private class EntityListener extends ClickListener {

		private final Class<? extends AbstractEntity> entclass;
		private final String name;
		/**
		 * id of this listener
		 */
		private final byte id;

		/**
		 * 
		 * @param name
		 * @param entclass
		 * @param id id of this listener
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

	/**
	 * detects a click on the RenderCell in the list
	 */
	private class BlockListener extends ClickListener {

		/**
		 * id of represented block
		 */
		private final byte blockId;
		private final byte id;

		/**
		 * 
		 * @param id id of the listener
		 * @param blockId representing block id
		 */
		BlockListener(byte id, byte blockId) {
			this.blockId = blockId;
			this.id = id;
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			PlacableTable.this.setId(blockId);
			selectBlock(id);
		}
	}
}
