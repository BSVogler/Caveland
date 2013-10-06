package com.BombingGames.EngineCore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 *The FPS diagramm collects some fps values and creates a diagram and analyzes it.
 * @author Benedikt Vogler
 */
public class FPSdiag {
    private final int[] data = new int[10];
    private float timeSinceUpdate;
    private int field;
    private final int xPos, yPos, width;

    /**
     *
     * @param xPos the position of the diagram
     * @param yPos the position of the diagram
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
        ShapeRenderer shRenderer = view.getShapeRenderer();
        shRenderer.begin(ShapeRenderer.ShapeType.FilledRectangle);
        for (int i = 0; i < data.length; i++) { //render each field in memory
            if (i == field) //highlight current FPS
                shRenderer.setColor(Color.PINK);
            else shRenderer.setColor(Color.WHITE);
            shRenderer.filledRect(xPos+width*i, yPos-data[i], width-1, data[i]);
        }
        shRenderer.end();
        
        //render average
        int avg = getAverage();
        shRenderer.begin(ShapeRenderer.ShapeType.Line);
        shRenderer.setColor(Color.BLUE);
        shRenderer.line(xPos, yPos-avg, xPos+width*data.length, yPos-avg);
        shRenderer.end();
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
        avg /= length;
        return avg;
    }
}
