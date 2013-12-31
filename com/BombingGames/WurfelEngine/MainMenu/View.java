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
package com.BombingGames.WurfelEngine.MainMenu;

import com.BombingGames.WurfelEngine.WEMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * The View manages ouput
 * @author Benedikt
 */
public class View {
    private final Sprite lettering;    
    private final Sprite background;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final BitmapFont font;
    private float a =0;
    
    /**
     * Creates a View.
     */
    public View(){
        //load textures
        lettering = new Sprite(new Texture(Gdx.files.internal("com/BombingGames/WurfelEngine/MainMenu/Images/Lettering.png")));
        lettering.setX((Gdx.graphics.getWidth() - lettering.getWidth())/2);
        lettering.setY(50);
        lettering.flip(false, true);
        
        background = new Sprite(new Texture(Gdx.files.internal("com/BombingGames/WurfelEngine/MainMenu/Images/background.png")));
        background.setX((Gdx.graphics.getWidth() - lettering.getWidth())/2);
        background.setY(50);
        background.flip(false, true);
        
        batch = new SpriteBatch();
        
        //set the center to the top left
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        font = new BitmapFont(Gdx.files.internal("com/BombingGames/WurfelEngine/Core/arial.fnt"), true);
        font.setColor(Color.WHITE);
    }

    void update(float delta) {
       a += delta/1000f;
       if (a>1) a=1;
    }
        
    /**
     * renders the scene
     * @param pController 
     */
    public void render(Controller pController){
        //clear & set background to black
        Gdx.gl10.glClearColor( 0f, 0f, 0f, 1f );
        Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
        //update camera and set the projection matrix
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        //Background        
        batch.begin();
        for (int x = 0; x-1 < Gdx.graphics.getWidth()/background.getWidth(); x++) {
            for (int y = 0; y-1 < Gdx.graphics.getHeight()/background.getHeight(); y++) {
                background.setPosition(x*background.getWidth(), y*background.getHeight());
                background.draw(batch);
            }
        }
        batch.end();
        
        batch.begin();
        font.draw(batch, "FPS:"+ Gdx.graphics.getFramesPerSecond(), 20, 20);
        font.draw(batch, Gdx.input.getX()+ ","+Gdx.input.getY(), Gdx.input.getX(), Gdx.input.getY());
        batch.end();
        
        // render the lettering
        batch.begin();
        lettering.setColor(1, 1, 1, a);
        lettering.draw(batch);
        batch.end();
        
        // Draw the menu items
        batch.begin();
        for (MenuItem mI : MainMenuScreen.getController().getMenuItems()) {
            mI.render(batch, camera);
        }
        batch.end();
        
        batch.begin();
        font.draw(batch, "FPS:"+ Gdx.graphics.getFramesPerSecond(), 20, 20);
        font.draw(batch, Gdx.input.getX()+ ","+Gdx.input.getY(), Gdx.input.getX(), Gdx.input.getY());
        batch.end();
        
        font.scale(-0.5f);
        batch.begin();
        font.drawMultiLine(batch, WEMain.getCredits(), 50, 100);
        batch.end();
        font.scale(0.5f);
    }

}

