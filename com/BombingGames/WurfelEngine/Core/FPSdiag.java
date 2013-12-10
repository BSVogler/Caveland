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
    private final int[] data = new int[10];
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
        width = 12;
    }
    
    /**
     *Updates the diagramm
     * @param delta
     */
    public void update(float delta){
        timeSinceUpdate += delta;
        if (timeSinceUpdate>1000){//update only every second
            timeSinceUpdate = 0;
            
            field++;//move to next field
            if (field >= data.length) field = 0; //start over           
            
            data[field] = Gdx.graphics.getFramesPerSecond();//save fps
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
                shRenderer.rect(xPos+width*i, yPos-data[i], width-1, data[i]);
            }
            shRenderer.end();

            //render average
            int avg = getAverage();
            shRenderer.begin(ShapeRenderer.ShapeType.Line);
            shRenderer.setColor(Color.BLUE);
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
