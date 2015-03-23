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

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.MapEditor.MapEditorView;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.text.NumberFormat;

/**
 * Shows data for developers. Also has some tools like buttons to edito the map.
 * @author Benedikt Vogler
 */
public class DevTools {

    /**
     *The visualised width of every data
     */
    public static final int width=2;
    private final float[] data = new float[100];
    private final int leftOffset, topOffset, maxHeight;
    private float timeStepMin;
    private int field;//the current field number
    private boolean visible = true;
    private StringBuilder memoryText;
    private long freeMemory;
    private long allocatedMemory;
    private long maxMemory;
    private long usedMemory;
    private Image editorbutton;
    private Image editorreversebutton;

    /**
     *
     * @param xPos the position of the diagram from left
     * @param yPos the position of the diagram from top
     */
    public DevTools(final int xPos, final int yPos) {
        this.leftOffset = xPos;
        this.topOffset = yPos;
        maxHeight=150;   
    }
    
    /**
     *Updates the diagramm
     * @param dt
     */
    public void update(float dt){
        timeStepMin += dt;
        if (timeStepMin>50){//update only every t ms
            timeStepMin = 0;
            
            field++;//move to next field
            if (field >= data.length) field = 0; //start over           
            
            data[field] = Gdx.graphics.getDeltaTime();//save delta time
        }
        
        Runtime runtime = Runtime.getRuntime();
        NumberFormat format = NumberFormat.getInstance();

        memoryText = new StringBuilder(100);
        maxMemory = runtime.maxMemory();
        allocatedMemory = runtime.totalMemory();
        freeMemory = runtime.freeMemory();
        usedMemory = allocatedMemory-freeMemory;

        memoryText.append(format.format(usedMemory / 1024));
        memoryText.append("/").append(format.format(allocatedMemory / 1024)).append(" MB");
//        memoryText.append("free: ").append(format.format(freeMemory / 1024));
//        memoryText.append("allocated: ").append(format.format(allocatedMemory / 1024));
//        memoryText.append("max: ").append(format.format(maxMemory / 1024));
//        memoryText.append("total free: ").append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
    }
    
    /**
     *Renders the diagramm
     * @param view
     */
    public void render(final GameView view){
        if (visible){
            
            if (view instanceof MapEditorView) {
                WE.getEngineView().getStage().getActors().removeValue(editorbutton, false);
                WE.getEngineView().getStage().getActors().removeValue(editorreversebutton, false);
            } else {
                showEditorButtons(view);
            }
            
            //draw FPS-String
            view.drawString("FPS: "+ Gdx.graphics.getFramesPerSecond(), 15, 15,true);
            view.drawString("Drawcalls: "+ AbstractGameObject.getDrawCalls(), 15, 30,true);
            
            //draw diagramm
            ShapeRenderer shr = view.getShapeRenderer();
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
            Gdx.gl.glLineWidth(1);
            
            
            shr.begin(ShapeRenderer.ShapeType.Filled);
            //background
            shr.setColor(new Color(0.5f, 0.5f, 0.5f, 0.2f));
			int xPos = leftOffset;
			int yPos = (int) (Gdx.graphics.getHeight()/view.getEqualizationScale()-topOffset);
			
            shr.rect(xPos, yPos, getWidth(), -maxHeight);
            
            //render current field bar
            shr.setColor(new Color(1, 0, 1, 0.8f));
            shr.rect(
                xPos+width*field,
                yPos-maxHeight,
                width,
                getSavedFPS(field)
            );
            
            //render RAM
            shr.setColor(new Color(.2f, 1, .2f, 0.8f));
            shr.rect(
                xPos,
                yPos,
                usedMemory*width*data.length/allocatedMemory,
                -20
            );
            
            shr.setColor(new Color(0.5f, 0.5f, 0.5f, 0.8f));
            shr.rect(
                xPos + usedMemory*width*data.length/allocatedMemory,
                yPos,
                width*data.length - width*data.length*usedMemory/allocatedMemory,
                -20
            );
            
            shr.end();
            
            //render lines
            shr.begin(ShapeRenderer.ShapeType.Line);
            
            //render steps
            shr.setColor(Color.GRAY);
            shr.line(xPos, yPos-maxHeight, xPos+width*data.length, yPos-maxHeight);
            shr.line(xPos, yPos-maxHeight+30, xPos+width*data.length, yPos-maxHeight+30);
            shr.line(xPos, yPos-maxHeight+60, xPos+width*data.length, yPos-maxHeight+60);
            shr.line(xPos, yPos-maxHeight+120, xPos+width*data.length, yPos-maxHeight+120);
            
            //render each FPS field in memory
            for (int i = 0; i < data.length-1; i++) {
                shr.setColor(new Color(0, 0, 1, 0.9f));
                shr.line(
                    xPos+width*i+width/2,
                    yPos+getSavedFPS(i)-maxHeight,
                    xPos+width*(i+1.5f),
                    yPos+getSavedFPS(i+1)-maxHeight
                );
            }
            
            //render average FPS         

            float avg = getAverage();
            if (avg>0) {
                //FPS
               shr.setColor(new Color(1, 0, 1, 0.8f));
                shr.line(
                    xPos,
                    yPos-maxHeight+1/avg,
                    xPos+width*data.length,
                    yPos-maxHeight+1/avg
                );
                
                 //delta values
                shr.setColor(new Color(0, 0.3f, 0.8f, 0.7f));
                shr.line(
                    xPos,
                    yPos-maxHeight+avg*3000,
                    xPos+width*data.length,
                    yPos-maxHeight+avg*3000
                );
                String deltaT = Float.toString(avg*1000);
                if (deltaT.length()>4)
                    view.drawString("d: "+deltaT.substring(0, 5), xPos, (int) (yPos-maxHeight+avg*3000),new Color(0, 0.3f, 0.8f, 0.7f));
                else
                    view.drawString("d: "+deltaT, xPos, (int) (yPos-maxHeight+avg*3000),new Color(0, 0.3f, 0.8f, 0.7f));
            }
            
           

            shr.end(); 
            
            view.drawString(memoryText.toString(), xPos, yPos, true);
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }
    
    /**
     *Get a recorded FPS value. The time between savings is at least the timeStepMin
     * @param pos the array position
     * @return FPS value
     * @see #getTimeStepMin() 
     */
    public int getSavedFPS(int pos){
        if (data[pos]==0)
            return 0;
        else
            return (int) (1/data[pos]);
    }

    /**
     * The minimum time between two FPS values.
     * @return 
     */
    public float getTimeStepMin() {
        return timeStepMin;
    }
    
    /**
     *Returns the average delta time.
     * @return
     */
    public float getAverage(){
        float avg = 0;
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
    * Set the FPSdiag visible. You must nevertheless call render() to let it appear.
    * @param visible 
    */
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    /**
     *
     * @return 
     */
    public int getLeftOffset() {
        return leftOffset;
    }

    /**
     * 
     * @return Y-Up
     */
    public int getTopOffset() {
        return topOffset;
    }

    /**
     * Width of FPS diag.
     * @return in pixels
     */
    public int getWidth() {
        return width*data.length;
    }
    
      /**
     *Adds the buttons to the satage if missing
     * @param view The view which renders the buttons.
     */
    private void showEditorButtons(final GameView view){
        if (editorbutton==null || editorreversebutton==null){    
            TextureAtlas spritesheet = WE.getAsset("com/BombingGames/WurfelEngine/Core/skin/gui.txt");
            
            if (editorbutton==null){
                //add editor button
                editorbutton = new Image(spritesheet.findRegion("editor_button"));
                editorbutton.setX(leftOffset+width+40);
                editorbutton.setY(Gdx.graphics.getHeight()-topOffset);
                editorbutton.addListener(
                    new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            WE.loadEditor(false);
                            return true;
                       }
                    }
                );
            }
			view.getStage().addActor(editorbutton);
            
			if (WE.editorHasMapCopy()){
				if (editorreversebutton==null){
					//add reverse editor button
					editorreversebutton = new Image(spritesheet.findRegion("editorreverse_button"));
					editorreversebutton.setX(leftOffset+width+80);
					editorreversebutton.setY(Gdx.graphics.getHeight()-topOffset);
					editorreversebutton.addListener(
						new ClickListener() {
							@Override
							public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
								WE.loadEditor(true);
								return true;
						   }
						}
					);
				}
				view.getStage().addActor(editorreversebutton);	
			}
		}
    }
	
	/**
	 * disposes the dev tool
	 */
	public void dispose(){
		if (editorbutton!=null) editorbutton.remove();
		if (editorreversebutton!=null) editorreversebutton.remove();
	}
}
