package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;

/**
 *
 * @author Benedikt Vogler
 */
public abstract class AbstractPosition {
    private final int topleftX;//top left chunk x coordinate
    private final int topleftY;//topl left chunk Y coordinate
    private float height;

    /**
     * With custom reference
     * @param topleftX
     * @param topleftY 
     */
    public AbstractPosition(int topleftX, int topleftY) {
        this.topleftX = topleftX;
        this.topleftY = topleftY;
    }

    
    /**
     * With the currently loaded reference
     */
    public AbstractPosition() {
        topleftX = Controller.getMap().getChunkCoords(0)[0];
        topleftY = Controller.getMap().getChunkCoords(0)[1];
    }

    /**
     * Geht the height (z-value) of the coordinate (game dimension).
     * @return
     */
    public float getHeight() {
        return height;
    }

    /**
     * 
     * @param height 
     */
    public void setHeight(float height) {
        this.height = height;
    }
    
   /**
    * 
    * @return 
    */
    protected int getTopleftX() {
        return topleftX;
    }

    /**
     * 
     * @return 
     */
    protected int getTopleftY() {
        return topleftY;
    }
    
     /**
     *Returns the screen x-position where the object is rendered without regarding the camera. It also adds the cell offset.
     * @return
     */
    public abstract int get2DPosX();
    
    /**
     *Returns the screen y-position where the object is rendered without regarding the camera. It also adds the cell offset.
     * @return
     */
    public abstract int get2DPosY();
    
    public abstract Point getPoint();
    
    public abstract Coordinate getCoord();
    
    public abstract Block getBlock(); 
    
    public abstract AbstractPosition cpy(); 
    
    public abstract boolean onLoadedMap();
    
    public abstract AbstractPosition addVector(float[] vector);
    
    public abstract AbstractPosition addVector(float x, float y, float z);
}
