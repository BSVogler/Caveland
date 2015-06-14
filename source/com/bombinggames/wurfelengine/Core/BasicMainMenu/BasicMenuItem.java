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
package com.bombinggames.wurfelengine.core.BasicMainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Loading.LoadingScreen;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *A menu item is an object wich can be placed on a menu.
 * @author Benedikt
 */
public class BasicMenuItem {
    private static Sound sound;
    private static int highlight =0;
    private final Class<? extends Controller> gameController;
    private final Class<? extends GameView> gameView;
    private int x;
    private int y;
    private final int index;
    private final String label;
    private final int width;
    private final int height = 50;
    
    /**
     * Create a new menu Item which can launch a game.
     * @param index the index of the button
     * @param label the string displayed by the button. If "exit" or "options" they change their behaviour.
     * @param gameController Your game controller class for this menu item
     * @param gameView Your game view class for this menu item
     */
    public BasicMenuItem(int index, String label, Class<? extends Controller> gameController, Class<? extends GameView> gameView) {
        this.gameController = gameController;
        this.gameView = gameView;
        this.index = index;
        this.label = label;
        this.width= this.label.length()*20;
    }
    
      /**
     * Create a new menu Item which does something specific like exiting or showing the option screen.
     * @param index the index of the button
     * @param label the string displayed by the button. If "exit" or "options" they cahnge they behaviour.
     */
    public BasicMenuItem(int index, String label) {
        this.gameController = null;
        this.gameView = null;
        this.index = index;
        this.label = label;
        this.width = this.label.length()*20;
    }
    
    /**
     *Renders the menu item.
     * @param font
     * @param batch
     * @param sr
     */
    public void render(BitmapFont font, SpriteBatch batch, ShapeRenderer sr) {
        this.x = ((Gdx.graphics.getWidth()-50)/2);
        this.y = (Gdx.graphics.getHeight()/2+120-index*80);
        
        if (highlight==index)
            sr.setColor(Color.LIGHT_GRAY.cpy());
        else 
            sr.setColor(Color.DARK_GRAY.cpy());
		batch.end();
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.rect(x, y, width, height);
        sr.end();
		batch.begin();
        font.draw(batch, label, x, y);
    }
    

    /**
     * Check if the mouse clicked the menuItem.
     * @return
     */
    public boolean isClicked() {
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        
        return (
            Gdx.input.isButtonPressed(Buttons.LEFT) &&
            (mouseX >= x && mouseX <= x + width) &&
            (mouseY >= y && mouseY <= y + height)
        );
    }

    /**
     *
     * @return
     */
    public Class<? extends Controller> getGameController() {
        return gameController;
    }

    /**
     *
     * @return
     */
    public Class<? extends GameView> getGameView() {
        return gameView;
    }
    
    /**
     *
     */
    public void action(){
        if (sound  != null)
            sound.play();
        
        if (label.equalsIgnoreCase("exit")) {
            Gdx.app.exit();
        } else if (label.equalsIgnoreCase("options")) {
            WE.setScreen(new BasicOptionsScreen());
        }else {
            try {
                Controller c = getGameController().newInstance();
                GameView v = getGameView().newInstance();
                WE.initAndStartGame(c,v, new LoadingScreen());
            } catch (InstantiationException ex) {
                    Gdx.app.error("BasicMenuItem", "Failed intitalizing game by creating new instances of a class.");
                    Logger.getLogger(BasicMenuItem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                    Gdx.app.error("BasicMenuItem", "Failed intitalizing game.");
                    Logger.getLogger(BasicMenuItem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *
     * @param sound
     */
    public static void setSound(Sound sound) {
        BasicMenuItem.sound = sound;
    }

    /**
     *
     * @return
     */
    public static int getHighlight() {
        return highlight;
    }

    /**
     *
     * @param highlight
     */
    public static void setHighlight(int highlight) {
        BasicMenuItem.highlight = highlight;
    }
    
    
}