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

package com.BombingGames.WurfelEngine.Core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import java.io.File;

/**
 *A menu for choosing a map.
 * @author Benedikt Vogler
 */
public class LoadMenu {
    private final static int margin = 100;//the space to the screen corners.
    private Window window;
    private TextField textSearch;
    private Table content;
    private Stage stageRef;

    /**
     * Setups the window.
     * @param view 
     */
    public void viewInit(View view){
        stageRef = view.getStage();
        
        window = new Window("Choose a map", View.getSkin());
        window.setWidth(Gdx.graphics.getWidth()-margin*2);
        window.setHeight(Gdx.graphics.getHeight()-margin*2);
        window.setX(margin);
        window.setY(margin);
        window.setKeepWithinStage(true);
        window.setModal(true);//problem: only affects the stage
        window.setVisible(false);
        window.setMovable(false);

        // The window shall fill the whole window:
        //window.setFillParent(true);

        // This table groups the Search label and the TextField used to gather
        // the search criteria:
        Table search = new Table();
        search.add(new Label("Search", View.getSkin())).spaceRight(10f);

        textSearch = new TextField("Not implemented yet", View.getSkin());

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
        window.add(search).right();
        window.row();

        //rearrangeTable();

        content = new Table(View.getSkin());
        // Prepares the scroll manager:
        ScrollPane scroll = new ScrollPane(content, View.getSkin());

        // Only scroll vertically:
        scroll.setScrollingDisabled(true, false);

        window.add(scroll).fill().expand();
        window.row();
        
        view.getStage().addActor(window);//add the window to the view's stage.
    }
    
    /**
     * Returns if the window is opened.
     * @return 
     */
    public boolean isOpen() {
       return window.isVisible();
    }

    /**
     * Open/close the window
     * @param open 
     */
    public void setOpen(boolean open) {
        if (!window.isVisible()){
            int i=0;
            File wd = WorkingDirectory.getWorkingDirectory("Wurfel Engine");
            for (final File fileEntry : wd.listFiles()) {
                if (fileEntry.isDirectory()) {
                    content.add(fileEntry.getName());
                    
                    //debug
                    content.add(
                        new Button(View.getSkin())
                    );
                    
                    content.row();
                    i++;
                    //listFilesForFolder(fileEntry);
                } else {
                    //System.out.println(fileEntry.getName());
                }
            }
            View.focusInputProcessor(stageRef);
        }else
            View.unfocusInputProcessor();
        window.setVisible(open);
    }
}
