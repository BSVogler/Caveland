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

package com.bombinggames.wurfelengine.MapEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Map.Chunk;

/**
 *A bar which schows the current fitlering level.
 * @author Benedikt Vogler
 */
public class Navigation {
    
    /**
     *
     * @param view
     */
    protected void render(EditorView view){
            //draw layer navigation  on right side
            ShapeRenderer sh = WE.getEngineView().getShapeRenderer();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
            Gdx.gl.glLineWidth(3);

            sh.begin(ShapeRenderer.ShapeType.Line);

            int rightborder = Gdx.graphics.getWidth();
            int topBorder = Gdx.graphics.getHeight();
            int steps = topBorder/(Chunk.getBlocksZ()+1);

            for (int i = 1; i < Chunk.getBlocksZ()+1; i++) {
                if (view.getCameras().get(0).getZRenderingLimit() == i )
                    sh.setColor(Color.LIGHT_GRAY.cpy().sub(0, 0, 0,0.1f));
                else 
                    sh.setColor(Color.GRAY.cpy().sub(0, 0, 0,0.5f));
                sh.line(
                    rightborder,
                    i*steps,
                    rightborder-50- ( view.getCameras().get(0).getZRenderingLimit() == i ?40:0),
                    i*steps
                );

                //"shadow"
                sh.setColor(Color.DARK_GRAY.cpy().sub(0, 0, 0,0.5f));
                sh.line(
                    rightborder,
                    i*steps+3,
                    rightborder-50- ( view.getCameras().get(0).getZRenderingLimit() == i ?40:0),
                    i*steps+3
                ); 
            }

            sh.end();
            Gdx.gl.glLineWidth(1);
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
}
