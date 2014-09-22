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
package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.Generators.AirGenerator;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *A map stores nine chunks as part of a bigger map. It also contains the entities.
 * @author Benedikt Vogler
 */
public class Map implements Cloneable {
    private static int blocksX, blocksY, blocksZ;
        
    private final String filename;
    
    /**A list which has all current nine chunk coordinates in it.*/
    private final int[][] coordlist = new int[9][2];
    
    /** the map data are the blocks in their cells */
    private Cell[][][] data;
    
    private Generator generator;
    
    /** every entity on the map is stored in this field */
    private final static ArrayList<AbstractEntity> entityList = new ArrayList<>(20);
    /**
     * holds the metadata of the map
     */
    private final MapMetaData meta;
    
    /**
     * Loads a map.
     * @param name if available on disk it will be load
     * @throws java.io.IOException
     * @see #fill(com.BombingGames.WurfelEngine.Core.Map.Generator) 
     */
    public Map(final String name) throws IOException{
        this(name, new AirGenerator());
    }
    
    /**
     * Loads a map. Fill the map with {@link #fill(com.BombingGames.WurfelEngine.Core.Map.Generator) }
     * @param name if available on disk it will be load
     * @param generator the generator used for generating new chunks
     * @throws java.io.IOException thrown if there is no full read/write access to the map file
     * @see #fill(com.BombingGames.WurfelEngine.Core.Map.Generator) 
     */
    public Map(final String name, Generator generator) throws IOException {
        Gdx.app.debug("Map","Map named \""+name+"\" should be loaded");
        this.filename = name;
        meta = new MapMetaData(name);
        Chunk.setDimensions(meta);
        
        
        //save chunk size, which are now loaded
        blocksX = Chunk.getBlocksX()*3;
        blocksY = Chunk.getBlocksY()*3;
        blocksZ = Chunk.getBlocksZ();
        data = new Cell[blocksX][blocksY][blocksZ];//create Array where the data is stored
        
        for (Cell[][] x : data)
            for (Cell[] y : x)
                for (int z = 0; z < y.length; z++) {
                    y[z] = new Cell();
                }
        
        if (generator==null) generator = WE.getCurrentConfig().getChunkGenerator();
        this.generator = generator;
    }
    
    /**
     * Should create a new map file.
     * @param mapName
     * @throws java.io.IOException 
     */
    public static void createMapFile(final String mapName) throws IOException {
        MapMetaData meta = new MapMetaData();
        meta.setFileName(mapName);
        meta.setChunkBlocksX(10);
        meta.setChunkBlocksY(40);
        meta.setChunkBlocksZ(10);
        meta.setMapName(mapName);
        meta.setDescription("No description set");
        meta.write();
    }
    
    /**
     *Fills the map with blocks. If no custom generator is set it will use air.
     * @param allowLoading
     */
    public void fill(boolean allowLoading){
        fill(generator, allowLoading);
    }
    
    /**
     * Fills the map without overriding the map generator.
     * @param generator the custom generator
     * @param allowLoading
     */
    public void fill(Generator generator, boolean allowLoading){
        byte chunkpos = 0;
        for (byte y=-1; y < 2; y++)
            for (byte x=-1; x < 2; x++){
                coordlist[chunkpos][0] = x;
                coordlist[chunkpos][1] = y;  
                if (allowLoading)
                    insertChunk(chunkpos, new Chunk(filename, x, y, generator));
                else 
                    insertChunk(chunkpos, new Chunk(x, y, generator));
                chunkpos++;
           }
    }
    /**
     * Fill the data array of the map with Cells. Also resets the cellOffset.
     */
    public void fillWithAir(){
        Gdx.app.debug("Map","Filling the map with air cells...");
        for (Cell[][] x : data) {
            for (Cell[] y : x) {
                for (int z = 0; z < y.length; z++) {
                    y[z] = new Cell();
                }
            }   
        }
        
//        //Fill the nine chunks
//        int chunkpos = 0;
//        
//        for (byte y=-1; y < 2; y++)
//            for (byte x=-1; x < 2; x++){
//                coordlist[chunkpos][0] = x;
//                coordlist[chunkpos][1] = y;  
//                insertChunk((byte) chunkpos, new Chunk(filename, x, y, generator));
//                chunkpos++;
//        }
       
        Gdx.app.log("Map","...Finished filling the map");
    }
    
     /**
     * Returns the amount of Blocks inside the map in x-direction.
     * @return
     */
    public static int getBlocksX() {
        return blocksX;
    }

    /**
     * Returns the amount of Blocks inside the map in y-direction.
     * @return
     */
    public static int getBlocksY() {
        return blocksY;
    }

    /**
     * Returns the amount of Blocks inside the map in z-direction.
     * @return 
     */
    public static int getBlocksZ() {
        return blocksZ;
    }
    
    
    /**
     * Copies an array with three dimensions. Deep copy until the cells content of cells shallow copy.
     * @param array the data you want to copy
     * @return The copy of the array-
     */
    private static Cell[][][] copyCells(final Cell[][][] array) {
        Cell[][][] copy = new Cell[array.length][][];
        for (int i = 0; i < array.length; i++) {
            copy[i] = new Cell[array[i].length][];
            for (int j = 0; j < array[i].length; j++) {
                copy[i][j] = new Cell[array[i][j].length];
                System.arraycopy(
                    array[i][j], 0, copy[i][j], 0, 
                    array[i][j].length
                );
            }
        }
        return copy;
    } 
    
    /**
     * Copies an array with three dimensions. Deep copy using clone. Probably slower.
     * @param array the data you want to copy
     * @return The copy of the array-
     */
    private static Cell[][][] copyCellsDeeper(final Cell[][][] array) throws CloneNotSupportedException {
        Cell[][][] copy = new Cell[array.length][][];
        for (int x = 0; x < array.length; x++) {
            copy[x] = new Cell[array[x].length][];
            
            for (int y = 0; y < array[x].length; y++) {
                copy[x][y] = new Cell[array[x][y].length];
                for (int z = 0; z < array[x][y].length; z++) {
                    copy[x][y][z] = array[x][y][z].clone();
                }
            }
        }
        return copy;
    } 
        
    /**
     * Get the data of the map
     * @return
     */
    public Cell[][][] getData() {
        return data;
    }
    
    /**
     * Reorganises the map and sets the new middle chunk to param newmiddle.
     * Move all chunks when loading or creating a new piece of the map
     *    |0|1|2|
     *     -------------
     *    |3|4|5|
     *     -------------
     *    |6|7|8|
     * @param newmiddle newmiddle is 1, 3, 5 or 7
     */
    public void setCenter(final int newmiddle){
        if (WE.getCurrentConfig().isChunkSwitchAllowed()){
            Gdx.app.log("Map","ChunkSwitch:"+newmiddle);
            if (newmiddle==1 || newmiddle==3 || newmiddle==5 || newmiddle==7) {

                //make a chunk of the data
                Cell[][][] dataCopy = copyCells(data);
                
                for (int pos=0; pos<9; pos++){
                     //save
                    if (isMovingOutside(pos, newmiddle)){
                        try {
                            copyChunk(dataCopy, pos).save(filename, coordlist[pos][0], coordlist[pos][1]);
                        } catch (IOException ex) {
                            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    //refresh coordinates
                    coordlist[pos][0] += (newmiddle == 3 ? -1 : (newmiddle == 5 ? 1 : 0));
                    coordlist[pos][1] += (newmiddle == 1 ? -1 : (newmiddle == 7 ? 1 : 0));

                    Chunk chunk;
                    if (isMovingChunkPossible(pos, newmiddle)){
                        chunk = copyChunk(dataCopy, pos - 4 + newmiddle);
                    } else {
                        chunk = new Chunk(
                            filename,
                            coordlist[pos][0],
                            coordlist[pos][1],
                            generator
                        );
                    }
                    insertChunk((byte) pos,chunk);
                }

                Controller.requestRecalc();
            } else {
                Gdx.app.log("Map","setCenter was called with center:"+newmiddle);
            }
        }
    }
    
    /**
     * checks if the number can be reached by moving the net in a newmiddle
     * @param pos the position you want to check
     * @param newmiddle the newmiddle the chunkswitch is made to
     * @return 
     */
     private boolean isMovingChunkPossible(final int pos, final int newmiddle){
        boolean result = true; 
        switch (newmiddle){
            case 1: if ((pos==0) || (pos==1) || (pos==2)) result = false;
            break;
            
            case 3: if ((pos==0) || (pos==3) || (pos==6)) result = false;
            break;  
                
            case 5: if ((pos==2) || (pos==5) || (pos==8)) result = false;
            break;
                
            case 7: if ((pos==6) || (pos==7) || (pos==8)) result = false;
            break;
        } 
        return result;
    }
     
     /**
      * is the chunk moving from the map and overriden?
      * @param pos
      * @param newmiddle
      * @return 
      */
    private boolean isMovingOutside(final int pos, final int newmiddle){
        boolean result = false; 
        switch (newmiddle){
            case 1: if ((pos==6) || (pos==7) || (pos==8)) result = true;
            break;
            
            case 3: if ((pos==2) || (pos==5) || (pos==8)) result = true;
            break;  
                
            case 5: if ((pos==0) || (pos==3) || (pos==6)) result = true;
            break;
                
            case 7: if ((pos==0) || (pos==1) || (pos==2)) result = true;
            break;
        } 
        return result;
    }
     
    /**
     * Get a chunk out of a map (should be a chunk of a part of the field data)
     * @param cellData The data where you want to read from
     * @param offsetData the offset data
     * @param pos The chunk number where the chunk is located
     */ 
    private Chunk copyChunk(final Cell[][][] cellData, final int pos) {
        Chunk chunk = new Chunk();
        //copy the data in two loops and then do an arraycopy
        for (int x = Chunk.getBlocksX()*(pos % 3);
                 x < Chunk.getBlocksX()*(pos % 3+1);
                 x++
            )
                for (int y = Chunk.getBlocksY()*Math.abs(pos / 3);
                         y < Chunk.getBlocksY()*Math.abs(pos / 3+1);
                         y++
                    ) {
                    System.arraycopy(
                        cellData[x][y],                
                        0,
                        chunk.getData()[x-Chunk.getBlocksX()*(pos % 3)][y - Chunk.getBlocksY()*(pos / 3)],
                        0,
                        Chunk.getBlocksZ()
                    );
                }
        return chunk;
    }

    /**
     * Inserts a chunk in the map.
     * @param pos The position in the grid
     * @param chunk The chunk you want to insert
     */
    private void insertChunk(final byte pos, final Chunk chunk) {
        for (int x=0;x < Chunk.getBlocksX(); x++)
            for (int y=0;y < Chunk.getBlocksY();y++) {
                System.arraycopy(
                    chunk.getData()[x][y],
                    0,
                    data[x+ Chunk.getBlocksX()*(pos%3)][y+ Chunk.getBlocksY()*Math.abs(pos/3)],
                    0,
                    Chunk.getBlocksZ()
                );
            }
    }
    
   /**
     *Get the coordinates of a chunk. 
     * @param pos 
     * @return the coordinates of the chunk
     */
    public int[] getChunkCoords(final int pos) {
        return coordlist[pos];
    }
   
    
    /**
     * Returns  a Block without checking the parameters first. Good for debugging and also faster.
     * @param x position
     * @param y position
     * @param z position
     * @return the single renderobject you wanted
     */
    public Block getBlock(final int x, final int y, final int z){
        return data[x][y][z].getBlock();  
    }
    
    /**
     *
     * @param coord
     * @return
     */
    public Block getBlock(final Coordinate coord){
        return data[coord.getRelX()][coord.getRelY()][coord.getZ()].getBlock();  
    }
    
     /**
     * Returns a block of the map. Clamps the coodinates.
     * @param x If too high or too low, it takes the highest/deepest value possible
     * @param y If too high or too low, it takes the highest/deepest value possible
     * @param z If too high or too low, it takes the highest/deepest value possible
     * @return A single Block at the wanted coordinates.
     * @see com.BombingGames.WurfelEngine.Core.Map.Map#getDataClamp(com.BombingGames.WurfelEngine.Core.Map.Coordinate)
     */
    public Block getBlockClamp(int x, int y, int z){
        if (x >= blocksX){
            x = blocksX-1;
            Gdx.app.error("Map","X:"+x);
        } else if( x<0 ){
            x = 0;
            Gdx.app.error("Map","X:"+x);
        }
        
        if (y >= blocksY){
            y = blocksY-1;
            Gdx.app.debug("Map","Y:"+y);
        } else if( y < 0 ){
            y = 0;
            Gdx.app.error("Map","Y:"+y);
        }
        
        if (z >= blocksZ){
            z = blocksZ-1;
            Gdx.app.error("Map","Z:"+z+">="+blocksZ);
        } else if( z < 0 ){
            z = 0;
            Gdx.app.error("Map","Z:"+z+">="+blocksZ);
        }
        
        return data[x][y][z].getBlock();    
    }
    
    /**
     * Get a block at a coordinate but clamp it first.
     * @param coords
     * @return
     */
    public Block getDataClamp(final Coordinate coords) {
        return getBlockClamp(coords.getRelX(), coords.getRelY(), coords.getZ());
    }
    
   
    

    /**
     * Set a block at a specific coordinate.
     * @param x position
     * @param y position
     * @param z position
     * @param block  
     */
    public void setData(final int x, final int y, final int z, final Block block){
        data[x][y][z].setBlock(block);
    }
    
    /**
     * Set a block at a specific coordinate.
     * @param coords
     * @param block
     */
    public void setData(final Coordinate coords, final Block block) {
        data[coords.getRelX()][coords.getRelY()][coords.getZ()].setBlock(block);
    }
        
   /**
     * Set a block with safety checks.
     * @param coords
     * @param block
     */
    public void setDataSafe(final int[] coords, final Block block) {       
        if (coords[0] >= blocksX){
            coords[0] = blocksX-1;
        } else if( coords[0]<0 ){
            coords[0] = 0;
        }
        
        if (coords[1] >= blocksY){
            coords[1] = blocksY-1;
        } else if( coords[1] < 0 ){
            coords[1] = 0;
        }
        
        if (coords[2] >= blocksZ){
            coords[2] = blocksZ-1;
        } else if( coords[2] < 0 ){
            coords[2] = 0;
        }
        
        data[coords[0]][coords[1]][coords[2]].setBlock(block);
    }
    
    /**
     * Set a block with safety checks.
     * @param coord 
     * @param block
     */
    public void setDataSafe(final Coordinate coord, final Block block) {        
        setDataSafe(new int[]{
            coord.getRelX(),
            coord.getRelY(),
            coord.getZ()},
            block
        );
    }
    
    /**
     * Set the generator used for generating maps
     * @param generator
     */
    public void setGenerator(Generator generator) {
        this.generator = generator;
    }
    
    /**
     * a method who gives random blocks offset
     * @param numberofblocks the amount of moved blocks
     */
    public void earthquake(final int numberofblocks){
        int[] x = new int[numberofblocks];
        int[] y = new int[numberofblocks];
        int[] z = new int[numberofblocks];
        
        //pick random blocks 
        for (int i=0;i<numberofblocks;i++){
            x[i] = (int) (Math.random()*blocksX-1);
            y[i] = (int) (Math.random()*blocksY-1);
            z[i] = (int) (Math.random()*blocksZ-1);
        }
        
        for (int i=0;i < numberofblocks; i++){
                //cellPos[x[i]][y[i]][z[i]][0] = (float) (Math.random()*Block.SCREEN_DEPTH2);
                //cellPos[x[i]][y[i]][z[i]][1] = (float) (Math.random()*Block.SCREEN_DEPTH2);
                data[x[i]][y[i]][z[i]].setCellOffset(2, (int) (Math.random()*Block.GAME_EDGELENGTH));//vertical shake
            
        }
        Controller.requestRecalc();
    }
    
    /**
     * Returns the entityList
     * @return
     */
    public ArrayList<AbstractEntity> getEntitys() {
        return entityList;
    }

    /**
     *Returns the degree of the world spin. This changes where the sun rises and falls.
     * @return a number between 0 and 360
     */
    public int getWorldSpinDirection() {
        return WE.getCurrentConfig().getWorldSpinAngle();
    }
    
    
    /**
     *
     * @param coord
     * @return
     */
    public int[] getCellOffset(final Coordinate coord) {
        return data[coord.getRelX()][coord.getRelY()][coord.getZ()].getCellOffset();
    }   
    
    /**
     *Set the offset in one cell. 
     * @param coord the cell
     * @param field 0 = X, 1 = y, 2 = z
     * @param value the value you want to set the field
     */
    public void setCelloffset(final Coordinate coord, final int field, final int value){
        data[coord.getRelX()][coord.getRelY()][coord.getZClamp()].setCellOffset(field, value);
    }
    
     /**
     * Find every instance of a special class e.g. find every AbstractCharacter
     * @param <type>
     * @param type
     * @return a list with the entitys
     */
    @SuppressWarnings({"unchecked"})
    public <type extends AbstractEntity> ArrayList<type> getAllEntitysOfType(final Class<? extends AbstractEntity> type) {
        ArrayList<type> list = new ArrayList<>(30);//defautl size 30

        for (AbstractEntity entity : entityList) {//check every entity
            if (type.isInstance(entity)) {//if the entity is of the wanted type
                list.add((type) entity);//add it to list
            }
        }
        return list;
    }
    
     /**
     * Get every entity on a coord.
     * @param coord
     * @return a list with the entitys
     */
    public ArrayList<AbstractEntity> getAllEntitysOnCoord(final Coordinate coord) {
        ArrayList<AbstractEntity> list = new ArrayList<>(5);//defautl size 5

        for (AbstractEntity ent : entityList) {
            if (Arrays.equals(
                    ent.getPos().getCoord().getRel(),
                    coord.getRel()
                )
                ){
                list.add(ent);//add it to list
            } 
        }

        return list;
    }
    
      /**
     * Get every entity on a coord of the wanted type
     * @param <type> the class you want to filter.
     * @param coord the coord where you want to get every entity from
     * @param type the class you want to filter.
     * @return a list with the entitys of the wanted type
     */
    @SuppressWarnings("unchecked")
    public <type> ArrayList<type> getAllEntitysOnCoord(final Coordinate coord, final Class<? extends AbstractEntity> type) {
        ArrayList<type> list = new ArrayList<>(5);

        for (AbstractEntity ent : entityList) {
            if (
                Arrays.equals(ent.getPos().getCoord().getRel(),coord.getRel())//on coordinate?
                && type.isInstance(ent)//of tipe of filter?
                ){
                    list.add((type) ent);//add it to list
            } 
        }

        return list;
    }
    
    /**
     *Returns a coordinate pointing to the absolute center of the map. Height is half the map's height.
     * @return
     */
    public static Point getCenter(){
        return getCenter(Map.getBlocksZ()*Block.GAME_EDGELENGTH/2);
    }
    
    /**
     *Returns a corodinate pointing to the absolute center of the map.
     * @param height You custom height.
     * @return
     */
    public static Point getCenter(final float height){
        return
            new Point(
                Chunk.getGameWidth()*1.5f,
                Chunk.getGameDepth()*1.5f,
                height,
                false
            );
    }
    
    /**
     *The width of the map with three chunks in use
     * @return amount of bluck multiplied by the size in game space.
     */
    public static int getGameWidth(){
        return blocksX*AbstractGameObject.GAME_DIAGLENGTH;
    }
    
    /**
     * The depth of the map in game size
     * @return 
     */
    public static int getGameDepth() {
        return blocksY*AbstractGameObject.GAME_DIAGLENGTH2;
    }

    /**
     * Game size
     * @return 
     */
    public static int getGameHeight(){
        return blocksZ*AbstractGameObject.GAME_EDGELENGTH;
    }

    /**
     * The name of the map on the file.
     * @return 
     */
    public String getFilename() {
        return filename;
    }

    public MapMetaData getMeta() {
        return meta;
    }
    
    
    
    
        
    /**
     *Clones the map. Not yet checked if a valid copy.
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Map clone() throws CloneNotSupportedException{
        Map map = (Map) super.clone();
        map.data = copyCellsDeeper(data);//deep copy of the data
        return map;
    }

    /**
     *
     */
    public static void staticDispose(){
        for (AbstractEntity entity : entityList) {
            entity.dispose();
        }
    }

    /**
     * saves every chunk on the map
     * @return 
     */
    public boolean save() {
        for (int pos=0; pos<9; pos++){
            try {
                Chunk chunk = copyChunk(data, pos);
                chunk.save(
                    filename,
                    coordlist[pos][0],
                    coordlist[pos][1]
                );
            } catch (IOException ex) {
                Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }


    
}