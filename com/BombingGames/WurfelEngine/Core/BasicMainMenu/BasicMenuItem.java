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
package com.BombingGames.WurfelEngine.Core.BasicMainMenu;

import com.BombingGames.WurfelEngine.Configuration;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.WEMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *A menu item is an object wich can be placed on a menu.
 * @author Benedikt
 */
public class BasicMenuItem{
    private final Class<Controller> gameController;
    private final Class<View> gameView;
    private int x;
    private int y;
    private final int index;
    private final String text;
    private final int width;
    private final int height = 50;
    private static Sound sound;
    private Configuration config;
    
    /**
     * Create a new menu Item and say which texture it should have.
     * @param index
     * @param label
     * @param gameController Your game controller for this menu item
     * @param gameViews Your game view for this menu item
     * @param config
     */
    @SuppressWarnings("unchecked")
    public BasicMenuItem(int index, String label, Class gameController, Class gameViews, Configuration config) {
        this.gameController = gameController;
        this.gameView = gameViews;
        this.index = index;
        this.text = label;
        this.width= text.length()*20;
        this.config = config;
    }
    
      /**
     * Create a new menu Item which exits the game
     * @param index
     * @param label
     */
    public BasicMenuItem(int index, String label) {
        this.gameController = null;
        this.gameView = null;
        this.index = index;
        this.text = label;
        this.width = text.length()*20;
    }
    
    /**
     *
     * @param camera The camera rendering the MenuItem
     * @param font
     * @param batch
     * @param sr
     */
    public void render(Camera camera, BitmapFont font, SpriteBatch batch, ShapeRenderer sr) {
        this.x = ((Gdx.graphics.getWidth()-50)/2);
        this.y = (Gdx.graphics.getHeight()/2-120+index*80);
        
        sr.setColor(Color.DARK_GRAY.cpy());
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.rect(x, y, width, height);
        sr.end();
        
        font.draw(batch, text, x, y);
    }
    

    /**
     * Check if ithe mouse clicked the menuItem.
     * @return
     */
    public boolean isClicked() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.input.getY();
        
        return (
            Gdx.input.isButtonPressed(Buttons.LEFT) &&
            (mouseX >= x && mouseX <= x + width) &&
            (mouseY >= y && mouseY <= y + height)
        );
    }

    public Class<Controller> getGameController() {
        return gameController;
    }

    public Class<View> getGameView() {
        return gameView;
    }
    
    public void action(){
        if (sound  != null)
            sound.play();
        
        if (text.equalsIgnoreCase("exit")) {
            Gdx.app.exit();
        } else if (text.equalsIgnoreCase("options")) {
            WEMain.getInstance().setScreen(new BasicOptionsScreen());
        }else {
            try {
                Controller c = getGameController().newInstance();

                View v = getGameView().newInstance();
                WEMain.initGame(c,v, config);
                } catch (InstantiationException ex) {
                Logger.getLogger(BasicMenuItem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(BasicMenuItem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void setSound(Sound sound) {
        BasicMenuItem.sound = sound;
    }
}