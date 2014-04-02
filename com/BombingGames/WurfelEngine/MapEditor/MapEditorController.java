/*
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
 * * Neither the name of Bombing Games nor Benedikt Vogler nor the names of its contributors 
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

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.WECamera;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 *
 * @author Benedikt Vogler
 */
public class MapEditorController extends Controller {
    private int currentLayer = 0;
    private WECamera camera;
    private Stage stage;



    @Override
    public void init() {
        super.init();
        Gdx.app.log("MapEditorController", "Initializing");
        
        currentLayer = Map.getBlocksZ();
        
        stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        
        TextureAtlas spritesheet = WE.getAsset("com/BombingGames/WurfelEngine/Core/skin/gui.txt");
        
        //add play button
        final Image playbutton = new Image(spritesheet.findRegion("play_button"));
        playbutton.setX(Gdx.graphics.getWidth()-40);
        playbutton.setY(Gdx.graphics.getHeight()-40);
        playbutton.addListener(
            new ClickListener() {
             @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    playbutton.setColor((float) Math.random(), (float) Math.random(), (float) Math.random(), 1);
                    return true;
               }
            }
        );
        stage.addActor(playbutton);
        
         //add play button
        final Image loadbutton = new Image(spritesheet.findRegion("load_button"));
        loadbutton.setX(Gdx.graphics.getWidth()-80);
        loadbutton.setY(Gdx.graphics.getHeight()-40);
        loadbutton.addListener(
            new ClickListener() {
             @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    loadbutton.setColor((float) Math.random(), (float) Math.random(), (float) Math.random(), 1);
                    return true;
               }
            }
        );
        stage.addActor(loadbutton);
        
         //add play button
        final Image savebutton = new Image(spritesheet.findRegion("save_button"));
        savebutton.setX(Gdx.graphics.getWidth()-120);
        savebutton.setY(Gdx.graphics.getHeight()-40);
        savebutton.addListener(
            new ClickListener() {
             @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    savebutton.setColor((float) Math.random(), (float) Math.random(), (float) Math.random(), 1);
                    return true;
               }
            }
        );
        stage.addActor(savebutton);
        
        camera = new WECamera();
        addCamera(camera);
    }
    
   /**
     * Get the value of currentLayer
     *
     * @return the value of currentLayer
     */
    public int getCurrentLayer() {
        return currentLayer;
    }

    /**
     * Set the value of currentLayer
     *
     * @param currentLayer new value of currentLayer
     */
    public void setCurrentLayer(int currentLayer) {
        this.currentLayer = currentLayer;
        
        //clamp
        if (currentLayer<1) this.currentLayer=1;//min is 1
        if (currentLayer >= Map.getBlocksZ()) this.currentLayer=Map.getBlocksZ();
        
        WECamera.setZRenderingLimit(currentLayer);
    }

    public Stage getStage() {
        return stage;
    }
    
    
}
