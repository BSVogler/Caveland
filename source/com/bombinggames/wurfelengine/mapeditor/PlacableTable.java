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
import com.bombinggames.wurfelengine.core.map.rendering.RenderBlock;
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

	private final SelectionDetails placableGUI;

	private boolean placeBlocks = true;
	private byte selected;
	/**
	 * stores the block drawables
	 */
	private final ArrayList<BlockDrawable> blockDrawables = new ArrayList<>(40);

	/**
	 *
	 * @param colorGUI the linked preview of the selection
	 * @param left
	 */
	public PlacableTable(SelectionDetails colorGUI, boolean left) {
		this.placableGUI = colorGUI;

		setWidth(400);
		setHeight(Gdx.graphics.getHeight()*0.80f);
		setY(10);

		if (left) {
			setX(30);
		} else {
			setX(1480);
		}
	}

	/**
	 *
	 * @param view
	 */
	public void show(GameView view) {
		if (!isVisible()) {
			placableGUI.setVisible(true);
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
				for (byte i = 1; i < RenderBlock.OBJECTTYPESNUM; i++) {//add every possible block
					if (RenderBlock.isSpriteDefined(i,(byte)0) //add defined blocks
						|| !RenderBlock.getName(i, (byte) 0).equals("undefined")) {
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
	 * @param includingSelection including the colro selection gui
	 */
	public void hide(boolean includingSelection) {
		if (hasChildren()) {
			clear();
		}

		if (isVisible()) {
			placableGUI.moveToBorder(placableGUI.getWidth() + 100);
			setVisible(false);
		}

		if (includingSelection) {
			placableGUI.hide();
		}
	}

	/**
	 *
	 * @param view
	 */
	protected void showBlocks(GameView view) {
		placeBlocks = true;
		placableGUI.setMode(placeBlocks);
		clearChildren();
		show(view);
	}

	/**
	 *
	 * @param view
	 */
	protected void showEntities(GameView view) {
		placeBlocks = false;
		placableGUI.setMode(placeBlocks);
		if (placableGUI.getEntity() == null) {//no init value for entity
			placableGUI.setEntity(
				AbstractEntity.getRegisteredEntities().keySet().iterator().next(),
				AbstractEntity.getRegisteredEntities().values().iterator().next()
			);
		}

		clearChildren();
		show(view);
	}

	/**
	 * selects the item //TODO needs more generic method including entities
	 * @param id the id of the listener
	 */
	void selectBlock(byte id) {
		if (id <= getChildren().size) {
			selected = id;
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
		blockDrawables.get(selected).setValue(value);
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
			placableGUI.setEntity(name, entclass);
			if (id <= getChildren().size) {
				for (Actor c : getChildren()) {
					c.setScale(0.5f);
				}
				getChildren().get(id).setScale(0.6f);
			}
		}
	}

	/**
	 * detects a click on the RenderBlock in the list
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
			placableGUI.setId(blockId);
			selectBlock(id);
		}
	}
}
