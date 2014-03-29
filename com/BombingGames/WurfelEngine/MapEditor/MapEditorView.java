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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 *
 * @author Benedikt Vogler
 */
public class MapEditorView extends View {

    @Override
    public void init(Controller controller) {
        super.init(controller);
        Gdx.input.setInputProcessor(new InputListener((MapEditorController) controller));
    }

    
    @Override
    public void render() {
        super.render();
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
            getController().getCameras().get(0).move(0, (int) (-delta*speed));
        if (input.isKeyPressed(Input.Keys.S))
            getController().getCameras().get(0).move(0, (int) (delta*speed));
        if (input.isKeyPressed(Input.Keys.A))
            getController().getCameras().get(0).move((int) -(delta*speed*1.414),0);
        if (input.isKeyPressed(Input.Keys.D))
            getController().getCameras().get(0).move((int) (delta*speed*1.414),0);
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
    
}
