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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Benedikt Vogler
 */
public class BlockTable extends AbstractPlacableTable {

	private byte selectionId;
	/**
	 * stores the block drawables
	 */
	private final ArrayList<BlockDrawable> blockDrawables = new ArrayList<>(40);
	private BlockDrawable selectedDrawable;

	@Override
	public void show(GameView view) {
		if (!isVisible()) {
			setVisible(true);
		}

		//setScale(5f);
		if (!hasChildren()) {
			byte foundItems = 0;
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
				if (RenderCell.isSpriteDefined(i, (byte) 0) //add defined blocks
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
			if (selectedDrawable == null) {
				selectedDrawable = blockDrawables.get(0);
			}
		}
	}

	private void setIdSelection(byte id) {
		this.selectionId = id;
	}

	@Override
	void selectItem(byte pos) {
		super.selectItem(pos);
		selectedDrawable = blockDrawables.get(pos);
	}

	@Override
	void setValue(byte value) {
		super.setValue(value);
		selectedDrawable.setValue(value);
	}

	/**
	 *
	 * @return
	 */
	public byte getIdOfSelection() {
		return selectionId;
	}

	/**
	 * Returns the data of the selected block.
	 * @return id, value and 100 health
	 */
	public int getSelectedBlock() {
		return selectionId+(getValue() << 8)+(100<<16);
	}

	/**
	 * Select a block. Will be highlighted in the table.
	 * @param blockId id of block data
	 * @param blockValue value of block data
	 */
	public void select(byte blockId, byte blockValue) {
		selectionId = blockId;
		byte i = 0;
		Iterator<BlockDrawable> iter = blockDrawables.iterator();
		while (iter.hasNext()) {
			if (iter.next().getRenderBlock().getId() == blockId) {
				selectItem(i);
				break;
			}
			i++;
		}
		setValue(selectedDrawable.getValue());
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
			BlockTable.this.setIdSelection(blockId);
			selectItem(id);
		}
	}

}
