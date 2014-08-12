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

import com.BombingGames.WurfelEngine.Core.Loading.LoadingScreen;
import com.BombingGames.WurfelEngine.MapEditor.MapEditorController;
import com.BombingGames.WurfelEngine.MapEditor.MapEditorView;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;

/**
 * The GameplayScreen State. This is state where the Wurfel Engine magic happens.
 * @author Benedikt
 */
public class GameplayScreen implements Screen{ 
  /**
     * Contains the Message System
     */
    private static MsgSystem msgSystem;    
    
    private View view = null;
    private Controller controller = null;
    private MapEditorController editorController;
    private MapEditorView editorView;
    private final Configuration config;
    
    /**
     * Create the gameplay state.
     * @param controller The controller of this screen.
     * @param view The user view of this screen.
     * @param config The configuration of the game show in this screen.
     */
    public GameplayScreen(final Controller controller, final View view, final Configuration config) {
        Gdx.app.log("GameplayScreen", "Initializing");

        Gdx.input.setInputProcessor(null);
        
        WE.getInstance().setScreen(new LoadingScreen(config));
        
        msgSystem = new MsgSystem(this);
        
        
        this.controller = controller;
        this.view = view;
        this.config = config;
    }
             

    /**
     * Returns the Message System. Use .add() to add messages to the msgSystem.
     * @return The msgSystem.
     */
    public static MsgSystem msgSystem() {
        return msgSystem;
    }

    /**
     *
     * @return
     */
    public View getView() {
        return view;
    }

    /**
     *
     * @return
     */
    public Controller getController() {
        return controller;
    }

    /**
     *
     * @param view
     */
    public void setView(final View view) {
        this.view = view;
    }

    /**
     *
     * @param controller
     */
    public void setController(final Controller controller) {
        this.controller = controller;
    }
    
    
    
    

    @Override
    public void render(float delta) {
        delta *= 1000;
        controller.update(delta);
        view.update(delta);
        view.render();
        View.getStaticStage().draw();
        msgSystem.update(delta);

        
        if (GameplayScreen.msgSystem().isActive() && Gdx.input.isKeyPressed(Input.Keys.UP))
            GameplayScreen.msgSystem().setText(GameplayScreen.msgSystem().getLastMessage("Console"));
    }

    @Override
    public void resize(final int width, final int height) {
        Gdx.graphics.setTitle("Wurfelengine V" + WE.VERSION + " " + Gdx.graphics.getWidth() + "x"+Gdx.graphics.getHeight());
        view.resize(width, height);
    }

    @Override
    public void show() {
        View.classInit();
        this.controller.init();
        this.view.init(controller);
        controller.enter();
        view.enter();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        msgSystem.dispose();
        Controller.disposeClass();
    }

    /**
     *
     * @return
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * lazy init
     * @return 
     */
    public MapEditorController getEditorController() {
        if (editorController==null){
            editorController = new MapEditorController(
                view,
                controller
            );
        }
        return editorController;
    }

   /**
    * lazy init
    * @return 
    */
    public MapEditorView getEditorView() {
         if (editorView==null){
            editorView = new MapEditorView();
        }
        return editorView;
    }
    
}