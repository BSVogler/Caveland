package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;

/**
 *A coordinate is a reference to a specific cell in the map. The coordinate can transfer between relative and absolute coordiantes.
 * Relative coordinates are similar to the currently loaded map array. Absolute coordinates  are indipendent of the current map but to acces them you must have the chunk thet the coordiantes are in the currently loaded chunks.
 * @author Benedikt Vogler
 */
public class Coordinate extends AbstractPosition {
    private int x; //saved as relative
    private int y; //saved as relative
    
    /**
     * Creates a coordiante. You can specify wether the given values are absolute or relative to the map.
     * @param x The x value.
     * @param y The y value.
     * @param z The z value as coordinate.
     * @param relative <b>True</b> when the coordiantes are relative to the currently loaded map. <b>False</b> when they are absolute.
     */
    public Coordinate(int x, int y, int z, final boolean relative) {
        super();
        
        this.x = x;
        this.y = y;
        if (!relative){ //if absolute then make it relative
            this.x -= getTopleftX() * Chunk.getBlocksX();
            this.y -= getTopleftY() * Chunk.getBlocksY();
        }
        
        setHeight(z*Block.GAME_DIMENSION);
    }
    
     /**
     * Creates a coordiante. You can specify wether the given values are absolute or relative to the map.
     * @param x The x value.
     * @param y The y value.
     * @param height The z value as height.
     * @param relative <b>True</b> when the coordiantes are relative to the currently loaded map. <b>False</b> when they are absolute.
     */
    public Coordinate(int x, int y, float height, final boolean relative) {
        super();
        
        this.x = x;
        this.y = y;
        if (!relative){ //if absolute then make it relative
            this.x -= getTopleftX() * Chunk.getBlocksX();
            this.y -= getTopleftY() * Chunk.getBlocksY();
        }
        
        setHeight(height);
    }
    
    /**
     * Creates a new coordinate from an existing coordinate
     * @param coord the Coordinate
     */
    public Coordinate(Coordinate coord) {
        super(coord.getTopleftX(), coord.getTopleftY());
        
        this.x = coord.getRelX();
        this.y = coord.getRelY();
        setHeight(coord.getHeight());
    }
    
    /**
     *
     * @return
     */
    public int getRelX(){
        return x + (getTopleftX()-Controller.getMap().getChunkCoords(0)[0]) * Chunk.getBlocksX();
    }
    /**
     *
     * @return
     */
    public int getRelY(){
        return y + (getTopleftY()-Controller.getMap().getChunkCoords(0)[1]) * Chunk.getBlocksY();
    }
    
    /**
     *
     * @return
     */
    public int getAbsX(){
        return x + getTopleftX() *Chunk.getBlocksX();
    }
    /**
     *
     * @return
     */
    public int getAbsY(){
         return y + getTopleftY() *Chunk.getBlocksY();
    }
    
    /**
     *
     * @return
     */
    public int getZ(){
        return (int) (getHeight()/Block.GAME_DIMENSION);
    }
    
    /**
     *
     * @return
     */
    public int getZSafe(){
        int tmpZ =  (int) (getHeight()/Block.GAME_DIMENSION);
        if (tmpZ >= Map.getBlocksZ())
            return Map.getBlocksZ() -1;
        else if (tmpZ < 0) return 0;
            else return tmpZ;
    }
    
    
   /**
     *
     * @return an array with the offset of the cell
     */
    public int[] getCellOffset(){
        return Controller.getMap().getCellOffset(this);
    }
 
    
    /**
     *Set the coordiantes X component.
     * @param x
     */
    public void setRelX(int x){
        this.x = x;
    }
    
    /**
     *Set the coordiantes Y component.
     * @param y
     */
    public void setRelY(int y){
        this.y = y;
    }
    
    /**
     *Set the coordinates Z component. It will be transversed into a float value (height).
     * @param z
     */
    public void setZ(int z){
        setHeight(z*Block.GAME_DIMENSION);
    }
    

    
    /**
     *Set the vertical offset in the cell, where the coordiante is pointing at.
     * @param height
     */
    public void setCellOffsetZ(int height){
        Controller.getMap().setCelloffset(this, 2, height);
    }
    
    /**
     *
     * @param block
     */
    public void setBlock(Block block){
        Controller.getMap().setData(this, block);
    }
    
    /**
     *
     * @return
     */
    public int[] getRel(){
        return new int[]{getRelX(), getRelY(), getZ()};
    }
    
    /**
     *
     * @return
     */
    public int[] getAbs(){
        return new int[]{getAbsX(), getAbsY(), getZ()};
    }
    
    /**
     * Add a vector to the coordinates. If you just want the result and don't change the coordiantes use addVectorCpy.
     * @param vector
     * @return the new coordiantes which resulted of the addition
     */
    @Override
    public Coordinate addVector(float[] vector) {
        this.x += vector[0];
        this.y += vector[1];
        setHeight(getHeight()+ vector[2]*Block.GAME_DIMENSION);
        return this;
    }
    
     /**
     * Add a vector to the coordinates. If you just want the result and don't change the coordiantes use addVectorCpy.
     * @param x
     * @param y
     * @param z
     * @return the new coordiantes which resulted of the addition
     */
    @Override
    public Coordinate addVector(float x, float y, float z) {
        this.x += x;
        this.y += y;
        setHeight(getHeight()+ z*Block.GAME_DIMENSION);
        return this;
    }
    
    /**
     *
     * @return
     */
    @Override
    public Block getBlock(){
        return Controller.getMap().getBlock(this);
    }
    
    /**
     *
     * @return
     */
    public Block getBlockSafe(){
        return Controller.getMap().getDataSafe(this);
    }
    

    
    /**
     * Has the object an offset (pos vector)?
     * @return when it has offset true, else false
     */
    public boolean hasOffset() {
        return getCellOffset()[0] != 0 || getCellOffset()[1] != 0 || getCellOffset()[2] != 0;
    }
    
   /**
     * The block hides the past block when it has sides and is not transparent (like normal block)
     * @return true when hiding the past Block
     */
    public boolean hidingPastBlock(){
        return (getBlock().hasSides() && ! getBlock().isTransparent() && ! hasOffset());
    }
    
    /** @return a copy of this coordinate */
    @Override
    public Coordinate cpy () {
        return new Coordinate(this);
    }
    
    /**
     * Checks if the coordiantes are accessable with the currently loaded Chunks.
     * @return 
     */
    @Override
    public boolean onLoadedMap(){
        return (getRelX() >= 0 && getRelX() < Map.getBlocksX()
            && getRelY() >= 0 && getRelY() < Map.getBlocksY());
    }
    
    /**
     * Returns the field-id where the coordiantes are inside in relation to the current field. Field id count clockwise, starting with the top with 0.
     * If you want to get the neighbour you can use neighbourSidetoCoords(Coordinate coords, int sideID) with the second parameter found by this function.
     * The numbering of the sides:<br>
     * 7 \ 0 / 1<br>
     * -------<br>
     * 6 | 8 | 2<br>
     * -------<br>
     * 5 / 4 \ 3<br>
     * @param x game-space-coordinates, value in pixels
     * @param y game-space-coordinates, value in pixels
     * @return Returns the fieldnumber of the coordinates. 8 is the field itself.
     * @see com.BombingGames.Game.Gameobjects.AbstractGameObject#neighbourSidetoCoords(com.BombingGames.EngineCore.Map.Coordinate, int)
     */
    public static int getNeighbourSide(float x, float y) {       
        int result = 8;//standard result
        if (x + y <= Block.SCREEN_DEPTH) {
            result = 7;
        }
        if (x - y >= Block.SCREEN_DEPTH) {
            if (result == 7) {
                result = 0;
            } else {
                result = 1;
            }
        }
        if (x + y >= 3 * Block.SCREEN_DEPTH) {
            if (result == 1) {
                result = 2;
            } else {
                result = 3;
            }
        }
        if (-x + y >= Block.SCREEN_DEPTH) {
            if (result == 3) {
                result = 4;
            } else if (result == 7) {
                result = 6;
            } else {
                result = 5;
            }
        }
        return result;
    }

    /**
     * Get the neighbour coordinates of the neighbour of the coords you give.
     * @param neighbourSide the side number of the given coordinates
     * @return The coordinates of the neighbour.
     */
    public Coordinate neighbourSidetoCoords(int neighbourSide) {
        int[] result = new int[3];
        switch (neighbourSide) {
            case 0:
                result[0] = getRelX();
                result[1] = getRelY() - 2;
                break;
            case 1:
                result[0] = getRelX() + (getRelY() % 2 == 1 ? 1 : 0);
                result[1] = getRelY() - 1;
                break;
            case 2:
                result[0] = getRelX() + 1;
                result[1] = getRelY();
                break;
            case 3:
                result[0] = getRelX() + (getRelY() % 2 == 1 ? 1 : 0);
                result[1] = getRelY() + 1;
                break;
            case 4:
                result[0] = getRelX();
                result[1] = getRelY() + 2;
                break;
            case 5:
                result[0] = getRelX() - (getRelY() % 2 == 0 ? 1 : 0);
                result[1] = getRelY() + 1;
                break;
            case 6:
                result[0] = getRelX() - 1;
                result[1] = getRelY();
                break;
            case 7:
                result[0] = getRelX() - (getRelY() % 2 == 0 ? 1 : 0);
                result[1] = getRelY() - 1;
                break;
            default:
                result[0] = getRelX();
                result[1] = getRelY();
        }
        result[2] = getZ();
        return new Coordinate(result[0], result[1], result[2], true);
    }

    protected int getX() {
        return x;
    }

    protected int getY() {
        return y;
    }

    @Override
    public Point getPoint() {
        return new Point(x*Block.SCREEN_WIDTH + (y%2==1 ? Block.SCREEN_WIDTH2 : 0), y*Block.SCREEN_DEPTH, getHeight(), true);
    }

    @Override
    public Coordinate getCoord() {
        return this;
    }
    
    @Override
    public int get2DPosX() {
        int offset = 0;
        if (getZ()>=0)
            offset = (int) (getCellOffset()[0]);
        return getRelX() * Block.SCREEN_WIDTH //x-coordinate multiplied by it's dimension in this direction
               + (getRelY() % 2) * AbstractGameObject.SCREEN_WIDTH2 //offset by y
               + offset;
    }

    @Override
    public int get2DPosY() {
        int offset = 0;
        if (getZ()>=0)
            offset = (int) (getCellOffset()[1] / 2) //add the objects position inside this coordinate
                    - (int) (getCellOffset()[2] / Math.sqrt(2)); //add the objects position inside this coordinate
        return getRelY() * Block.SCREEN_DEPTH2 //y-coordinate * the tile's half size size
               - (int) (getHeight() / Math.sqrt(2)) //take axis shortening into account
               + offset;
    }


}