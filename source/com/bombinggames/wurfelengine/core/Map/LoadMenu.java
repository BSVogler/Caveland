/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
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

package com.bombinggames.wurfelengine.core.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.WorkingDirectory;
import java.io.File;

/**
 *A menu for choosing a map.
 * @author Benedikt Vogler
 */
public class LoadMenu extends Window {
    private final static int margin = 100;//the space to the screen corners.
    private TextField textSearch;
    private Table content;
    private ScrollPane scroll;
    private Stage stageRef;
    private boolean initialized = false;
    private LoadMenuListener listener;

        /**
     * Setups the window.
     */
    public LoadMenu() {
        super("Choose a map", WE.getEngineView().getSkin());
    }

    /**
     * 
     * @param view 
     */
    public void init(GameView view){
        setWidth(Gdx.graphics.getWidth()-margin*2);
        setHeight(Gdx.graphics.getHeight()-margin*2);
        setX(margin);
        setY(margin);
        setKeepWithinStage(true);
        setModal(true);//problem: only affects the stage
        setVisible(false);
        setMovable(true);
        stageRef = view.getStage();
        // The window shall fill the whole window:
        //window.setFillParent(true);

        // This table groups the Search label and the TextField used to gather
        // the search criteria:
        Table search = new Table();
        search.add(new Label("Search", WE.getEngineView().getSkin())).spaceRight(10f);

        textSearch = new TextField("Not implemented yet", WE.getEngineView().getSkin());

        // This event waits untilk the RETURN key is pressed to reorganize the
        // intens inside the grid:
        textSearch.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                //if (keycode 1== Input.Keys.ENTER)
                    //rearrangeTable();

                // Gdx.app.log("KEY", String.valueOf(keycode));

                return super.keyDown(event, keycode);
            }
        });

        search.add(textSearch).minSize(400f, 15f);

        // The search field will be aligned at the right of the window:
        add(search).right();
        row();

        //rearrangeTable();

        content = new Table(WE.getEngineView().getSkin());
        // Prepares the scroll manager:
        scroll = new ScrollPane(content, WE.getEngineView().getSkin());

        // Only scroll vertically:
        scroll.setScrollingDisabled(true, false);

        add(scroll).fill().expand();
        row();
        
        stageRef.addActor(this);//add the window to the view's stage.
        initialized=true;
    }
    
    /**
     * Open/close the window
     * @param view if not intialized it initializes it. can be null if definetly initialized.
     * @param open should be open or closed?
     */
    public void setOpen(GameView view, boolean open) {
        if (initialized || open){//if initialized or should be opened
            if (!initialized) init(view);
            if (!isVisible()){//opening
                int i=0;
                File mapsFolder = WorkingDirectory.getMapsFolder();
                for (final File fileEntry : mapsFolder.listFiles()) {
                    if (fileEntry.isDirectory()) {
                        content.add(new MapButton(fileEntry.getName()));

                        content.row();
                        i++;
                        //listFilesForFolder(fileEntry);
                    } else {
                        //System.out.println(fileEntry.getName());
                    }
                }
                listener = new LoadMenuListener(this);
                stageRef.addListener(listener);
                WE.getEngineView().focusInputProcessor(stageRef);
            }else{ //closing
                clear();
                clearListeners();
                scroll.clearListeners();
                stageRef.removeListener(listener);
                initialized=false;
                WE.getEngineView().unfocusInputProcessor();
            }
            setVisible(open);
        }
    }
    
	/**
	 *
	 */
	public void close(){
        setOpen(null, false);
    }
    
    private static class LoadMenuListener extends InputListener{
        private final LoadMenu parent;
        
        protected LoadMenuListener(LoadMenu parent) {
            this.parent = parent;
        }

        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            if (keycode == Input.Keys.ESCAPE){
                parent.close();
            }
            return true;
        }
    }
}

