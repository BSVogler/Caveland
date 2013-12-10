package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;

/**
 *
 * @author Benedikt Vogler
 */
public class Point extends AbstractPosition {
    private float x;
    private float y;

    /**
     * 
     * @param posX
     * @param posY
     * @param height
     * @param relative 
     */
    public Point(float posX, float posY, float height, boolean relative) {
        super();
         if (relative){
            this.x = posX;
            this.y = posY;
        } else {
            this.x = posX - getTopleftX() * Chunk.getBlocksX();
            this.y = posY - getTopleftY() * Chunk.getBlocksY();
        }
        setHeight(height);
    }
    
    /**
     * This constructor copies the values.
     * @param point the source of the copy
     */
    public Point(Point point) {
       super(point.getTopleftX(), point.getTopleftY());
       this.x = point.x;
       this.y = point.y;
       this.setHeight(point.getHeight());
    }
    
    
    
    @Override
    public Point getPoint() {
       return this;
    }
    
        /**
     *
     * @return
     */
    public float[] getRel(){
        return new float[]{getRelX(), getRelY(), getHeight()};
    }

    public float getRelX() {
        return x + (getTopleftX()-Controller.getMap().getChunkCoords(0)[0]) * Chunk.getGameWidth();
    }
    
    public float getRelY() {
        return y + (getTopleftY()-Controller.getMap().getChunkCoords(0)[1]) * Chunk.getGameDepth();
    }
    
            /**
     *
     * @return
     */
    public float[] getAbs(){
        return new float[]{getAbsX(), getAbsY(), getHeight()};
    }

    public float getAbsX() {
        return x + getTopleftX() *Chunk.getGameWidth();
    }
    
    public float getAbsY() {
        return y + getTopleftY() *Chunk.getGameDepth();
    }
    
    @Override
    public Coordinate getCoord() {
        return Controller.findCoordinate(this, false);
    }
    
    
      /**
     *
     * @return
     */
    @Override
    public Block getBlock(){
        return getCoord().getBlock();
    }
    

    
    /**
     *
     * @return
     */
    public Block getBlockSafe(){
        Coordinate coord = getCoord();
        if (coord.getZ() >= Chunk.getGameHeight())
            return Block.getInstance(0);
        else
            return Controller.getMap().getDataSafe(coord);
    }

    @Override
    public Point cpy() {
        return new Point(this);
    }

    @Override
    public int get2DPosX() {
        return (int) (getRelX()); //x-coordinate multiplied by it's dimension in this direction
    }

    @Override
    public int get2DPosY() {
        return (int) (getRelY() / 2) //add the objects position inside this coordinate
               - (int) (getHeight() / Math.sqrt(2)); //take z-axis shortening into account
    }

    @Override
    public boolean onLoadedMap() {
        return (getRelX() >= 0 && getRelX() < Map.getGameWidth()
            && getRelY() >= 0 && getRelY() < Map.getGameDepth());
    }

    @Override
    public Point addVector(float[] vector) {
        this.x += vector[0];
        this.y += vector[1];
        setHeight(getHeight()+ vector[2]*Block.GAME_DIMENSION);
        return this;
    }

    @Override
    public AbstractPosition addVector(float x, float y, float z) {
        this.x += x;
        this.y += y;
        setHeight(getHeight()+ z*Block.GAME_DIMENSION);
        return this;
    }
}
