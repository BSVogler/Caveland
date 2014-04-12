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
import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.Core.WECamera;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 *
 * @author Benedikt Vogler
 */
public class MapEditorView extends View {
    private MapEditorController controller;
    private Stage stage;
    private WECamera camera;

    @Override
    public void init(Controller controller) {
        super.init(controller);
        this.controller = (MapEditorController) controller;
        
        stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
               
        View.addInputProcessor(new InputListener(this.controller));
        View.addInputProcessor(stage);
        
        
        camera = new WECamera();
        addCamera(camera);
        
        

        
        TextureAtlas spritesheet = WE.getAsset("com/BombingGames/WurfelEngine/Core/skin/gui.txt");
        
        //add play button
        final Image playbutton = new Image(spritesheet.findRegion("play_button"));
        playbutton.setX(Gdx.graphics.getWidth()-40);
        playbutton.setY(Gdx.graphics.getHeight()-40);
        playbutton.addListener(new PlayButton(controller, false));
        stage.addActor(playbutton);
        
         //add load button
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
        
         //add save button
        final Image savebutton = new Image(spritesheet.findRegion("save_button"));
        savebutton.setX(Gdx.graphics.getWidth()-120);
        savebutton.setY(Gdx.graphics.getHeight()-40);
        savebutton.addListener(new PlayButton(controller,false));
        stage.addActor(savebutton);
        
        //add replaybutton
        final Image replaybutton = new Image(spritesheet.findRegion("replay_button"));
        replaybutton.setX(Gdx.graphics.getWidth()-160);
        replaybutton.setY(Gdx.graphics.getHeight()-40);
        replaybutton.addListener(new PlayButton(controller, true));
        stage.addActor(replaybutton);
    }

    
    @Override
    public void render() {
        super.render();
        stage.draw();
        
        ShapeRenderer sh = getShapeRenderer();
        Gdx.gl.glEnable(GL10.GL_BLEND);
        Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glLineWidth(3);

        
        sh.begin(ShapeRenderer.ShapeType.Line);
        
        int rightborder = Gdx.graphics.getWidth();
        int bottomborder = Gdx.graphics.getHeight();
        int steps = bottomborder/(Map.getBlocksZ()+1);
        
        for (int i = 1; i < Map.getBlocksZ()+1; i++) {
            if (((MapEditorController) getController()).getCurrentLayer() == i )
                sh.setColor(Color.LIGHT_GRAY.cpy().sub(0, 0, 0,0.1f));
            else 
                sh.setColor(Color.GRAY.cpy().sub(0, 0, 0,0.5f));
            sh.line(
                rightborder,
                bottomborder-i*steps,
                rightborder-50- ( ((MapEditorController) getController()).getCurrentLayer() == i ?40:0),
                bottomborder-i*steps
            );
            
            //"shadow"
            sh.setColor(Color.DARK_GRAY.cpy().sub(0, 0, 0,0.5f));
            sh.line(
                rightborder,
                bottomborder-i*steps+3,
                rightborder-50- ( ((MapEditorController) getController()).getCurrentLayer() == i ?40:0),
                bottomborder-i*steps+3
            ); 
        }
        
        sh.end();
        Gdx.gl.glDisable(GL10.GL_BLEND);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        Input input = Gdx.input;
        int speed;
        
        if (input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            speed = 1600;
        else speed = 800;
        
        if (input.isKeyPressed(Input.Keys.W))
            camera.move(0, (int) (-delta*speed));
        if (input.isKeyPressed(Input.Keys.S))
            camera.move(0, (int) (delta*speed));
        if (input.isKeyPressed(Input.Keys.A))
            camera.move((int) -(delta*speed*1.414),0);
        if (input.isKeyPressed(Input.Keys.D))
            camera.move((int) (delta*speed*1.414),0);
    }

    
    
    private static class InputListener implements InputProcessor {
        private final MapEditorController controller;

        InputListener(MapEditorController controller) {
            this.controller = controller;
        }
        


        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            controller.setCurrentLayer(controller.getCurrentLayer()-amount);
            return true;
        }
    }
    
    private static class PlayButton extends ClickListener{
        private final MapEditorController controller;
        private final boolean replay;
        
        private PlayButton(Controller controller, boolean replay) {
            this.controller = (MapEditorController) controller;
            this.replay = replay;
        }
        
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (replay)
                    WE.switchSetupWithInit(controller.getGameplayController(), controller.getGameplayView());
                else
                    WE.switchSetup(controller.getGameplayController(), controller.getGameplayView());
            return true;
        }
    }
}
