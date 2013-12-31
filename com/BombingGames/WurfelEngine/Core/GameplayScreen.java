/*
 * Copyright 2013 Benedikt Vogler.
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
package com.BombingGames.WurfelEngine.Core;

import com.BombingGames.WurfelEngine.Core.Loading.LoadingController;
import com.BombingGames.WurfelEngine.Core.Loading.LoadingScreen;
import com.BombingGames.WurfelEngine.WEMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

/**
 * The GameplayScreen State. This is state where the magic happens.
 * @author Benedikt
 */
public class GameplayScreen implements Screen{ 
  /**
     * Contains the Message System
     */
    private static MsgSystem msgSystem;    
    
    private View view = null;
    private Controller controller = null;
    private final LoadingController loadingController;
    
    /**
     * Create the gameplay state.
     * @param controller The controller of this screen.
     * @param view  The view of this screen.
     */
    public GameplayScreen(Controller controller, View view) {
        Gdx.app.log("GameplayScreen", "Initializing");
        msgSystem = new MsgSystem(Gdx.graphics.getWidth()/2, 3*Gdx.graphics.getHeight()/4);

        loadingController = new LoadingController();
        loadingController.init(WEMain.getAssetManager());

        WEMain.getInstance().setScreen(new LoadingScreen(loadingController));
        
        this.controller = controller;
        this.view = view;
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
    
    

    @Override
    public void render(float delta) {
        controller.update(delta*1000);
        view.render();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.graphics.setTitle("Wurfelengine V" + WEMain.VERSION + " " + Gdx.graphics.getWidth() + "x"+Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        this.controller.init();
        this.view.init(controller);

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
    }
    
}