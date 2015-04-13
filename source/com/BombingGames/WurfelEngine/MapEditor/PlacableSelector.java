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

package com.BombingGames.WurfelEngine.MapEditor;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import java.util.Map;

/**
 * A table containing all blocks where you can choose your block.
 * @author Benedikt Vogler
 */
public class PlacableSelector extends Table {
    private Table table;
    private ScrollPane scroll; 
	private final PlacableGUI placableGUI;
	private float lastPosition;
	
	private PlaceMode mode = PlaceMode.Blocks;
	
	/**
     *
     * @param colorGUI the linked preview of the selection
	 * @param left
     */
    public PlacableSelector(PlacableGUI colorGUI, boolean left) {
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
			placableGUI.moveToCenter(getWidth());
			setVisible(true);
		}
		
        if (!hasChildren()){
            table = new Table();
            table.pad(10).defaults().expandX().space(4);

            scroll = new ScrollPane(table, WE.getEngineView().getSkin());
            add(scroll).expand().fill();
			
			if (mode == PlaceMode.Blocks) {
				if (!table.hasChildren()){//add blocks
					for (byte i = 1; i < AbstractGameObject.OBJECTTYPESNUM; i++) {
						table.row();
						table.add(new Label(Integer.toString(i), WE.getEngineView().getSkin())).expandX().fillX();
						
						BlockDrawable dbl = new BlockDrawable(i);
						ImageButton button = new ImageButton(dbl);
						dbl.setX(50);
						button.addListener(new BlockListener(i, button));
						//button.setStyle(style);
						table.add(button);

						table.add(new Label(new RenderBlock(i, (byte) 0).getName(), WE.getEngineView().getSkin()));
					}
				}
			} else {//add entities
				if (!table.hasChildren()){
					for (
						Map.Entry<String, Class<? extends AbstractEntity>> entry
						: AbstractEntity.getRegisteredEntities().entrySet()
					) {
						table.row();
						//table.add(new Label(Integer.toString(i), WE.getEngineView().getSkin())).expandX().fillX();

						Drawable dbl = new EntityDrawable(entry.getValue());
						Button button = new Button(dbl);
						button.addListener(new EntityListener(entry.getKey(), entry.getValue(), button));
						//button.setStyle(style);
						table.add(button);

						table.add(new Label(entry.getKey(), WE.getEngineView().getSkin()));
					}
				}
			}
			
			scroll.setForceScroll(false, true);
			scroll.setScrollBarPositions(false, true);
			scroll.setScrollX(lastPosition);
        }
    }
    
    /**
     *
	 * @param includingSelection including the colro selection gui
     */
    public void hide(boolean includingSelection){
        if (hasChildren()){
			lastPosition = scroll.getScrollX();
            scroll.clearListeners();
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
		if (table !=null)
			table.clearChildren();
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
		if (table !=null)
			table.clearChildren();
		show();
	}

	/**
	 * detects a click on the RenderBlock in the list
	 */
    private class BlockListener extends ClickListener {
        private byte id;
        private Button parent; 
        
		BlockListener(byte id, Button parent){
            this.id = id;
            this.parent = parent;
        }
                
        @Override
        public void clicked(InputEvent event, float x, float y) {
			if (id==0)
				placableGUI.setBlock(null);
			else
				placableGUI.setBlock(CoreData.getInstance(id));
        };
     }
	
	/**
	 * detects a click on an entity in the list
	 */
	private class EntityListener extends ClickListener {
        private Class<? extends AbstractEntity> entclass;
        private Button parent; 
		private final String name;
        
		EntityListener(String name, Class<? extends AbstractEntity> entclass, Button parent){
            this.entclass = entclass;
			this.name = name;
            this.parent = parent;
        }
                
        @Override
        public void clicked(InputEvent event, float x, float y) {
            placableGUI.setEntity(name, entclass);
        };
     }
    
}
