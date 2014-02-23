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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 *The FPS diagramm collects some fps values and creates a diagram and analyzes it.
 * @author Benedikt Vogler
 */
public class FPSdiag {
    private final int[] data = new int[50];
    private float timeSinceUpdate;
    private int field;//the current field number
    private final int xPos, yPos, width;
    private boolean visible = true;

    /**
     *
     * @param xPos the position of the diagram from left
     * @param yPos the position of the diagram (its bottom)
     */
    public FPSdiag(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        width = 4;
    }
    
    /**
     *Updates the diagramm
     * @param delta
     */
    public void update(float delta){
        timeSinceUpdate += delta;
        if (timeSinceUpdate>100){//update only every t ms
            timeSinceUpdate = 0;
            
            field++;//move to next field
            if (field >= data.length) field = 0; //start over           
            
            data[field] = (int) (1/Gdx.graphics.getDeltaTime());//save fps
        }
    }
    
    /**
     *Renders the diagramm
     * @param view
     */
    public void render(View view){
        if (visible){
            ShapeRenderer shRenderer = view.getShapeRenderer();
            Gdx.gl.glEnable(GL10.GL_BLEND);
            Gdx.gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);
            shRenderer.begin(ShapeRenderer.ShapeType.Filled);
            
            for (int i = 0; i < data.length; i++) { //render each field in memory
                if (i == field) //highlight current FPS
                    shRenderer.setColor(new Color(1, 0, 1, 0.8f));
                else
                    shRenderer.setColor(new Color(1, 1, 1, 0.8f));
                shRenderer.rect(xPos+width*i, yPos-data[i], width, data[i]);
            }
            shRenderer.end();

            //render average
            int avg = getAverage();
            shRenderer.begin(ShapeRenderer.ShapeType.Line);
            shRenderer.setColor(new Color(1, 0, 1, 0.8f));
            shRenderer.line(xPos, yPos-avg, xPos+width*data.length, yPos-avg);

            shRenderer.setColor(Color.GRAY);
            shRenderer.line(xPos, yPos, xPos+width*data.length, yPos);
            shRenderer.line(xPos, yPos-30, xPos+width*data.length, yPos-30);
            shRenderer.line(xPos, yPos-60, xPos+width*data.length, yPos-60);
            shRenderer.line(xPos, yPos-120, xPos+width*data.length, yPos-120);
            
            shRenderer.end();
            Gdx.gl.glDisable(GL10.GL_BLEND);
     }
    }
    
    /**
     *
     * @param pos
     * @return
     */
    public int getFPS(int pos){
        return data[pos];
    }
    
    /**
     *Returns the average value.
     * @return
     */
    public int getAverage(){
        int avg = 0;
        int length = 0;
        for (float fps : data) {
            avg += fps;
            if (fps > 0) length ++;//count how many field are filled
        }
        if (length > 0) avg /= length;
        return avg;
    }

    /**
     * Is the diagramm visible?
     * @return 
     */
    public boolean isVisible() {
        return visible;
    }

   /**
    * Set the FPSdiag visible. You must nevertheless call render().
    * @param visible 
    */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
