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

package com.bombinggames.wurfelengine.MapEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import java.util.Map;

/**
 * A table containing all blocks where you can choose your block.
 * @author Benedikt Vogler
 */
public class PlacableTable extends Table {
	private final PlacableGUI placableGUI;
	
	private PlaceMode mode = PlaceMode.Blocks;
	
	/**
     * 
     * @param colorGUI the linked preview of the selection
	 * @param left
     */
    public PlacableTable(PlacableGUI colorGUI, boolean left) {
        this.placableGUI = colorGUI;
        
		setWidth(400);
		setHeight(Gdx.graphics.getHeight()-100);
		setY(10);
		
		if (left) {
			setX(0);
		} else {
			setX(1580);
		}
    }

    /**
     *
     */
    public void show(){
		if (!isVisible()) {
			placableGUI.setVisible(true);
			//placableGUI.moveToCenter(getWidth());//moving to center not needed with new reduced table
			setVisible(true);
		}
		
        if (!hasChildren()){
			if (!hasChildren()){
				int foundItems = 0;
				if (mode == PlaceMode.Blocks) {//add blocks
					for (byte i = 1; i < Block.OBJECTTYPESNUM; i++) {//add every possible block
						//table.add(new Label(Block.getInstance(i).getName()+" (" +i + ")" , WE.getEngineView().getSkin()));
						if (RenderBlock.isSpriteDefined(Block.getInstance(i))) {
							BlockDrawable dbl = new BlockDrawable(i);
							ImageButton button = new ImageButton(dbl);
							button.addListener(new BlockListener(i));
							add(button);
							foundItems++;
							if (foundItems % 4 == 0)
								row();//make new row
						}
					}
				} else {//add entities
					for (
						Map.Entry<String, Class<? extends AbstractEntity>> entry
						: AbstractEntity.getRegisteredEntities().entrySet()
					) {//add every registered entity class
						//add(new Label(entry.getKey(), WE.getEngineView().getSkin()));
						Drawable dbl = new EntityDrawable(entry.getValue());
						ImageButton button = new ImageButton(dbl);
						button.addListener(new EntityListener(entry.getKey(), entry.getValue(), button));
						add(button);

						foundItems++;
						if (foundItems % 4 == 0)
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
    public void hide(boolean includingSelection){
        if (hasChildren()){
            clear();
        }
		
		if (isVisible()){
			placableGUI.moveToBorder(placableGUI.getWidth()+100);
			setVisible(false);
		}
		
		if (includingSelection)
			placableGUI.hide();
    }
	
	/**
	 *
	 */
	protected void showBlocks() {
		mode = PlaceMode.Blocks;
		placableGUI.setMode(mode);
//		if (table !=null)
		clearChildren();
		show();
	}

	/**
	 *
	 */
	protected void showEntities() {
		mode = PlaceMode.Entities;
		placableGUI.setMode(mode);
		if (placableGUI.getEntity()==null)//no init value for entity
			placableGUI.setEntity(
				AbstractEntity.getRegisteredEntities().keySet().iterator().next(),
				AbstractEntity.getRegisteredEntities().values().iterator().next()
			);
	
		clearChildren();
		show();
	}

	/**
	 * detects a click on the RenderBlock in the list
	 */
    private class BlockListener extends ClickListener {
        private final byte id;
		
		BlockListener(byte id){
            this.id = id;
        }
                
        @Override
        public void clicked(InputEvent event, float x, float y) {
			if (id==0)
				placableGUI.setBlock(null);
			else
				placableGUI.setBlock(Block.getInstance(id));
        }
     }
	
	/**
	 * detects a click on an entity in the list
	 */
	private class EntityListener extends ClickListener {
        private final Class<? extends AbstractEntity> entclass;
		private final String name;
        
		EntityListener(String name, Class<? extends AbstractEntity> entclass, Button parent){
            this.entclass = entclass;
			this.name = name;
        }
                
        @Override
        public void clicked(InputEvent event, float x, float y) {
            placableGUI.setEntity(name, entclass);
        };
     }
    
}
