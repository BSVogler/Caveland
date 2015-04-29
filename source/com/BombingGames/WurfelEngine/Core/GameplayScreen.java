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
package com.BombingGames.WurfelEngine.Core;

import static com.BombingGames.WurfelEngine.Core.Controller.getMap;
import com.BombingGames.WurfelEngine.Core.Loading.LoadingScreen;
import com.BombingGames.WurfelEngine.MapEditor.MapEditorController;
import com.BombingGames.WurfelEngine.MapEditor.MapEditorView;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;

/**
 * The GameplayScreen State. This is state where the Wurfel Engine magic happens.
 * @author Benedikt
 */
public class GameplayScreen extends WEScreen { 
  /**
     * Contains the Message System
     */
    
    private GameView view = null;
    private Controller controller = null;
    private MapEditorController editorController;
    private MapEditorView editorView;
	private LoadingScreen loadingScreen;
    
    /**
     * Create the gameplay state. This shows the loading screen.
     * @param controller The controller of this screen.
     * @param view The user view of this screen.
	 * @param loadingScreen
     */
    public GameplayScreen(final Controller controller, final GameView view, LoadingScreen loadingScreen) {
        Gdx.app.log("GameplayScreen", "Initializing");

        Gdx.input.setInputProcessor(null);
        this.loadingScreen = loadingScreen;
        WE.setScreen(loadingScreen);
        
        
        this.controller = controller;
        this.view = view;
    }
             

    /**
     *
     * @return
     */
    public GameView getView() {
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
    public void setView(final GameView view) {
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
    public void renderImpl(float dt) {
		//aply game world speed
        dt *= WE.CVARS.getValueF("timespeed");
		//update data
        controller.update(dt);
		Controller.staticUpdate(dt);
        view.update(dt);
		getMap().postUpdate(dt);//hack to prevent 1-frame lag by too late write access via view update
		//render data
        view.render();
        WE.getEngineView().getStage().draw();
    }

    @Override
    public void resize(final int width, final int height) {
        Gdx.graphics.setTitle("Wurfelengine V" + WE.VERSION + " " + Gdx.graphics.getWidth() + "x"+Gdx.graphics.getHeight());
        view.resize(width, height);
        Gdx.gl.glViewport(0, 0, width,height);
        //WE.getEngineView().getStage().setViewport(width, height);
    }

    @Override
    public void show() {
        WE.getEngineView().resetInputProcessors();
        GameView.classInit();
        this.controller.init();
        this.view.init(controller);
        controller.enter();
        view.enter();
		
		if (loadingScreen!=null && !(boolean) WE.CVARS.get("preventUnloading").getValue()) {
			loadingScreen.dispose();
			loadingScreen=null;
		}
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
		controller.dispose();
		view.dispose();
		WE.getEngineView().disposeMusic();
        Controller.disposeClass();
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