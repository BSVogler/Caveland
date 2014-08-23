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

import static com.BombingGames.WurfelEngine.Core.GameView.addInputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 *A view which is not dependend on the currently active game
 * @author Benedikt Vogler
 */
public class View {
    private static SpriteBatch batch;    
    private static ShapeRenderer shapeRenderer;
    private static BitmapFont font;
    private static Stage staticStage;//the stage used for view-independetn things
    private static Skin skin;
    private static Pixmap cursor;
    
    public static void classInit(){
        //set up font
        //font = WurfelEngine.getInstance().manager.get("com/BombingGames/WurfelEngine/EngineCore/arial.fnt"); //load font
        font = new BitmapFont(false);
        //font.scale(2);


        font.setColor(Color.GREEN);
        //font.scale(-0.5f);
        
        //load sprites
        staticStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        addInputProcessor(staticStage);
        skin = new Skin(Gdx.files.internal("com/BombingGames/WurfelEngine/Core/skin/uiskin.json"));
        
                batch = new SpriteBatch();
                        shapeRenderer = new ShapeRenderer();
                                cursor = new Pixmap(Gdx.files.internal("com/BombingGames/WurfelEngine/Core/images/cursor.png"));
    }
    
    
    /**
     * 
     * @return
     */
    public static BitmapFont getFont() {
        return font;
    }
    
        /**
     *y-down
     * @return
     */
    public static SpriteBatch getBatch() {
        return batch;
    }
    
        /**
     * Y-down
     * @return
     */
    public static ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }
    
        
    /**
     *
     * @return a view independent stage
     */
    public static Stage getStaticStage() {
        return staticStage;
    }

    /**
     *
     * @return
     */
    public static Skin getSkin() {
        return skin;
    }

    static Pixmap getCursor() {
        return cursor;
    }
    
}
