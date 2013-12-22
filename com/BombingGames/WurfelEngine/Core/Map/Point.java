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
            this.x = posX - getReferenceX() * Chunk.getBlocksX();
            this.y = posY - getReferenceY() * Chunk.getBlocksY();
        }
        setHeight(height);
    }
    
    /**
     * This constructor copies the values.
     * @param point the source of the copy
     */
    public Point(Point point) {
       super(point.getReferenceX(), point.getReferenceY());
       this.x = point.x;
       this.y = point.y;
       this.setHeight(point.getHeight());
    }
    
    
    
    @Override
    public Point getPoint() {
       return this;
    }
    
    @Override
    public Coordinate getCoord() {
        return toCoord(this, false);
    }
    
        /**
     *
     * @return
     */
    public float[] getRel(){
        return new float[]{getRelX(), getRelY(), getHeight()};
    }

    public float getRelX() {
        return x + (getReferenceX()-Controller.getMap().getChunkCoords(0)[0]) * Chunk.getGameWidth();
    }
    
    public float getRelY() {
        return y + (getReferenceY()-Controller.getMap().getChunkCoords(0)[1]) * Chunk.getGameDepth();
    }
    
            /**
     *
     * @return
     */
    public float[] getAbs(){
        return new float[]{getAbsX(), getAbsY(), getHeight()};
    }

    public float getAbsX() {
        return x + getReferenceX() *Chunk.getGameWidth();
    }
    
    public float getAbsY() {
        return y + getReferenceY() *Chunk.getGameDepth();
    }
    
    
      /**
     *
     * @return
     */
    @Override
    public Block getBlock(){
        if (onLoadedMap())
                    return getCoord().getBlock();
        else return null;
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
    
        /**
     * Game position to game coordinate
     * @param pos the position on the map
     * @param depthCheck when true the coordiantes are checked with depth, use this for "screen to coords". This is only possible if the position are on the map.
     * @return 
     */
    public static Coordinate toCoord(Point pos, boolean depthCheck){
        //find out where the position is (basic)
        Coordinate coords = new Coordinate(
            (int) (pos.getRelX()) / Block.GAME_DIAGSIZE,
            (int) (pos.getRelY()) / Block.GAME_DIAGSIZE*2,
            pos.getHeight(),
            true
        );
       
        //find the specific coordinate (detail)
        Coordinate specificCoords = coords.neighbourSidetoCoords(
            Coordinate.getNeighbourSide(
                pos.getRelX() % Block.GAME_DIAGSIZE,
                pos.getRelY() % (Block.GAME_DIAGSIZE)
            )
        );
        coords.setRelX(specificCoords.getRelX());
        coords.setRelY(specificCoords.getRelY());
        
        //trace ray down if wanted
        if (depthCheck && pos.onLoadedMap()) {
            coords.setRelY(coords.getRelY() + (depthCheck? coords.getZ()*2 : 0));
            //if selection is not found by that specify it
            if (coords.getBlock().isHidden()){
                //trace ray down to bottom. for each step 2 y and 1 z down
                do {
                    coords.setRelY(coords.getRelY()-2);
                    coords.setZ(coords.getZ()-1);
                } while (coords.getBlock().isHidden() && coords.getZ()>0);
            }
        }
        
        return coords;
    }
}
