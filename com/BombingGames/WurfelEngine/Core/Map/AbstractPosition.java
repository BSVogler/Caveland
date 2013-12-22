package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;

/**
 *
 * @author Benedikt Vogler
 */
public abstract class AbstractPosition {
    private final int referenceX;//top left chunk x coordinate
    private final int referenceY;//top left chunk Y coordinate
    private float height;

    /**
     * With custom reference
     * @param topleftX the chunk's X coordinate of the chunk at the top left
     * @param topleftY the chunk's Y coordinate of the chunk at the top left 
     */
    public AbstractPosition(int topleftX, int topleftY) {
        this.referenceX = topleftX;
        this.referenceY = topleftY;
    }

    
    /**
     * With the currently loaded top left chunk.
     */
    public AbstractPosition() {
        referenceX = Controller.getMap().getChunkCoords(0)[0];
        referenceY = Controller.getMap().getChunkCoords(0)[1];
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
        return referenceX;
    }

    /**
     * 
     * @return 
     */
    protected int getTopleftY() {
        return referenceY;
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
    
    /**
     * Get the block at the position. If the coordiante is outside the map return null.
     * @return 
     */
    public abstract Block getBlock(); 
    
    public abstract AbstractPosition cpy(); 
    
    /**
     * Checks if the position is on the chunks currently in memory.
     * @return 
     */
    public abstract boolean onLoadedMap();
    
    public abstract AbstractPosition addVector(float[] vector);
    
    public abstract AbstractPosition addVector(float x, float y, float z);
}
