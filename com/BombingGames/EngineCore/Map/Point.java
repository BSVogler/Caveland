package com.BombingGames.EngineCore.Map;

import com.BombingGames.EngineCore.Controller;
import static com.BombingGames.EngineCore.Gameobjects.AbstractGameObject.SCREEN_DEPTH;
import com.BombingGames.EngineCore.Gameobjects.Block;

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
     * Don't know if finished?
     * @param coordX
     * @param coordY
     * @param coordZ
     * @param relative 
     */
    public Point(int coordX, int coordY, int coordZ, boolean relative) {
        super();
    }
    
    /**
     * This constructor copies the values.
     * @param point 
     */
    public Point(Point point) {
       this.x = point.x;
       this.y = point.y;
       this.setHeight(this.getHeight());
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

    @Override
    public Coordinate getCoordinate() {
        return Controller.findCoordinate((int) getRelX(), (int) getRelY(), false);
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
    @Override
    public Block getBlock(){
        return Controller.getMap().getData(
            getCoordinate()
        );
    }
    

    
    /**
     *
     * @return
     */
    public Block getBlockSafe(){
        return Controller.getMap().getDataSafe(getCoordinate());
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
        return (x >= 0 && x < Map.getGameWidth()
            && y >= 0 && y < Map.getGameDepth());
    }

    @Override
    public Point addVector(float[] vector) {
        this.x += vector[0];
        this.y += vector[1];
        setHeight(getHeight()+ vector[2]*Block.GAME_DIMENSION);
        return this;
    }

    @Override
    public Point addVectorCpy(float[] vector) {
        Point newvec = this.cpy();
        newvec.x += vector[0];
        newvec.y += vector[1];
        newvec.setHeight(newvec.getHeight()+ vector[2]*Block.GAME_DIMENSION);
        return newvec;
    }
}
