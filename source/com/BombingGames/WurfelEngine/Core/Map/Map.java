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

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.Generators.AirGenerator;
import com.badlogic.gdx.Gdx;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *A map stores nine chunks as part of a bigger map. It also contains the entities.
 * @author Benedikt Vogler
 */
public class Map implements Cloneable {
    private static int blocksX, blocksY, blocksZ;
	private static ArrayList<Camera> cameras = new ArrayList<>(2);
	private static Generator defaultGenerator = new AirGenerator();
        
    private final String filename;
    
    /**A list which has all current nine chunk coordinates in it.*/
    private final int[][] coordlist = new int[9][2];
    
	private final Block groundBlock = Block.getInstance(CVar.get("groundBlockID").getValuei());//the representative of the bottom layer (ground) block
    /** Stores the data of the map. Fist dimension is Z. Second X, third Y.  */
    private ArrayList<ArrayList<ArrayList<Block>>> data;
    
    private Generator generator;
    
    /** every entity on the map is stored in this field */
    private ArrayList<AbstractEntity> entityList = new ArrayList<>(20);
    /**
     * holds the metadata of the map
     */
    private final MapMetaData meta;
	private MapIterator mapIterator;
	
	private boolean modified;
	private ArrayList<LinkedWithMap> linkedObjects = new ArrayList<>(3);//camera + minimap + light engine=3 minimum

    
    /**
     * Loads a map using the default generator.
     * @param name if available on disk it will be load
     * @throws java.io.IOException
     * @see #fill(com.BombingGames.WurfelEngine.Core.Map.Generator) 
     */
    public Map(final String name) throws IOException{
        this(name, defaultGenerator);
    }
    
    /**
     * Loads a map. Fill the map with {@link #fill(com.BombingGames.WurfelEngine.Core.Map.Generator) }
     * @param name if available on disk it will load the meta file
     * @param generator the generator used for generating new chunks
     * @throws java.io.IOException thrown if there is no full read/write access to the map file
     * @see #fill(com.BombingGames.WurfelEngine.Core.Map.Generator) 
     */
    public Map(final String name, Generator generator) throws IOException {
        Gdx.app.debug("Map","Map named \""+name+"\" should be loaded");
        this.filename = name;
        meta = new MapMetaData(name);
		if (CVar.get("shouldLoadMap").getValueb()){
			meta.load();
		}
        Chunk.setDimensions(meta);
        
        
        //save chunk size, which are now loaded
        blocksX = Chunk.getBlocksX()*3;
        blocksY = Chunk.getBlocksY()*3;
        blocksZ = Chunk.getBlocksZ();
        data = new ArrayList<>(blocksZ);
		//loop over z
		for (int z = 0; z < blocksZ; z++) {
			ArrayList<ArrayList<Block>> xRow = new ArrayList<>(blocksX);
			data.add(xRow);
			//loop over xrow
			for (int x = 0; x < blocksX; x++) {
				ArrayList<Block> yRow = new ArrayList<>(blocksY);
				xRow.add(yRow);
				
				Block airblock = Block.getInstance(0);
				airblock.setPosition(new Coordinate(x, 0, z));
				yRow.add(0, airblock);//add air cell to have at least one block with a position
			}
		}
		        
        this.generator = generator;
		modified = true;
    }
    
	public void update(float dt){
		//update every block on the map
		for (ArrayList<ArrayList<Block>> z : data) {
			for (ArrayList<Block> x : z) {
				for (Block y : x) {
					y.update(dt);
				}
			}
		}

		//update every entity
		for (int i = 0; i < getEntitys().size(); i++) {
			getEntitys().get(i).update(dt);
			if (getEntitys().get(i).shouldBeDisposed())
				getEntitys().remove(i);
		}
		
		if (modified){
			onModified();
			modified = false;
		}
	}
	
	public void postUpdate(float dt) {
		if (modified){
			onModified();
			modified = false;
		}
	}
		
    /**
     * Should create a new map file.
     * @param mapName file name
     * @throws java.io.IOException 
     */
    public static void createMapFile(final String mapName) throws IOException {
        MapMetaData meta = new MapMetaData(mapName);
        meta.setMapName(mapName);
        meta.write();
    }
    
    /**
     *Fills the map with blocks. If no custom generator is set it will use air.
	 * @param topleft
     * @param allowLoading
     */
    public void fill(Coordinate topleft, boolean allowLoading){
        fill(generator,topleft, allowLoading);
		modified = true;
    }
    
    /**
     * Fills 3x3 chunks the map without overriding the map generator.
     * @param generator the custom generator
	 * @param topleft begginning at this corner
     * @param allowLoading
     */
    public void fill(Generator generator, Coordinate topleft, boolean allowLoading){
        byte chunkpos = 0;
        for (byte y=-1; y < 2; y++)
            for (byte x=-1; x < 2; x++){
                coordlist[chunkpos][0] = x;
                coordlist[chunkpos][1] = y;  
                if (allowLoading)
                    insertChunk(topleft, new Chunk(filename, x, y, generator));
                else 
                    insertChunk(topleft, new Chunk(x, y, generator));
                chunkpos++;
           }
		modified = true;
    }
    /**
     * Fill the data array of the map with Blocks. Also resets the cellOffset.
     */
    public void fillWithAir(){
        Gdx.app.debug("Map","Filling the map with air cells...");
//        for (Block[][] x : data) {
//            for (Block[] y : x) {
//                for (int z = 0; z < y.length; z++) {
//                    y[z] = Block.getInstance(0);
//                }
//            }   
//        }
		
		for (ArrayList<ArrayList<Block>> xRow : data) {
			for (ArrayList<Block> yRow : xRow) {
				for (int y = 0; y < yRow.size(); y++) {
					yRow.set(y, Block.getInstance(0));
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
       
		modified = true;
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
    private static Block[][][] copyBlocks(final Block[][][] array) {
        Block[][][] copy = new Block[array.length][][];
        for (int i = 0; i < array.length; i++) {
            copy[i] = new Block[array[i].length][];
            for (int j = 0; j < array[i].length; j++) {
                copy[i][j] = new Block[array[i][j].length];
                System.arraycopy(
                    array[i][j], 0, copy[i][j], 0, 
                    array[i][j].length
                );
            }
        }
        return copy;
    } 
            
    /**
     * Get the data of the map
     * @return
     */
	public ArrayList<ArrayList<ArrayList<Block>>> getData() {
		return data;
	}
	
	
    
//    /**
//     * Reorganises the map and sets the new middle chunk to param newmiddle.
//     * Move all chunks when loading or creating a new piece of the map
//     *    |0|1|2|
//     *     -------------
//     *    |3|4|5|
//     *     -------------
//     *    |6|7|8|
//     * @param newmiddle newmiddle is 1, 3, 5 or 7
//     */
//    public void setCenter(final int newmiddle){
//        if (CVar.get("chunkSwitchAllowed").getValueb()){
//            Gdx.app.log("Map","ChunkSwitch:"+newmiddle);
//            if (newmiddle==1 || newmiddle==3 || newmiddle==5 || newmiddle==7) {
//
//                //make a chunk of the data
//                Block[][][] dataCopy = copyBlocks(data);
//                
//                for (int pos=0; pos<9; pos++){
//                     //save
//                    if (isMovingOutside(pos, newmiddle)){
//                        try {
//                            copyChunk(dataCopy, pos).save(filename, pos);
//                        } catch (IOException ex) {
//                            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                    
//                    //refresh coordinates
//                    coordlist[pos][0] += (newmiddle == 3 ? -1 : (newmiddle == 5 ? 1 : 0));
//                    coordlist[pos][1] += (newmiddle == 1 ? -1 : (newmiddle == 7 ? 1 : 0));
//
//                    Chunk chunk;
//                    if (isMovingChunkPossible(pos, newmiddle)){
//                        chunk = copyChunk(dataCopy, pos - 4 + newmiddle);
//                    } else {
//                        chunk = new Chunk(
//                            filename,
//                            coordlist[pos][0],
//                            coordlist[pos][1],
//                            generator
//                        );
//                    }
//                    insertChunk((byte) pos,chunk);
//                }
//
//                modified();
//            } else {
//                Gdx.app.log("Map","setCenter was called with center:"+newmiddle);
//            }
//        }
//    }
    
//    /**
//     * checks if the number can be reached by moving the net in a newmiddle
//     * @param pos the position you want to check
//     * @param newmiddle the newmiddle the chunkswitch is made to
//     * @return 
//     */
//     private boolean isMovingChunkPossible(final int pos, final int newmiddle){
//        boolean result = true; 
//        switch (newmiddle){
//            case 1: if ((pos==0) || (pos==1) || (pos==2)) result = false;
//            break;
//            
//            case 3: if ((pos==0) || (pos==3) || (pos==6)) result = false;
//            break;  
//                
//            case 5: if ((pos==2) || (pos==5) || (pos==8)) result = false;
//            break;
//                
//            case 7: if ((pos==6) || (pos==7) || (pos==8)) result = false;
//            break;
//        } 
//        return result;
//    }
     
//     /**
//      * is the chunk moving from the map and overriden?
//      * @param pos
//      * @param newmiddle
//      * @return 
//      */
//    private boolean isMovingOutside(final int pos, final int newmiddle){
//        boolean result = false; 
//        switch (newmiddle){
//            case 1: if ((pos==6) || (pos==7) || (pos==8)) result = true;
//            break;
//            
//            case 3: if ((pos==2) || (pos==5) || (pos==8)) result = true;
//            break;  
//                
//            case 5: if ((pos==0) || (pos==3) || (pos==6)) result = true;
//            break;
//                
//            case 7: if ((pos==0) || (pos==1) || (pos==2)) result = true;
//            break;
//        } 
//        return result;
//    }
     
    /**
     * Get a chunk out of a map (should be a chunk of a part of the field data)
     * @param data The data where you want to read from
     * @param offsetData the offset data
     * @param pos The chunk number where the chunk is located
     */ 
    private Chunk copyChunk(final ArrayList<ArrayList<ArrayList<Block>>> data, final int pos) {
        Chunk chunk = new Chunk();
        //copy the data in two loops and then do an arraycopy
//        for (int x = Chunk.getBlocksX()*(pos % 3);
//                 x < Chunk.getBlocksX()*(pos % 3+1);
//                 x++
//            )
//                for (int y = Chunk.getBlocksY()*Math.abs(pos / 3);
//                         y < Chunk.getBlocksY()*Math.abs(pos / 3+1);
//                         y++
//                    ) {
//                    System.arraycopy(
//						data[x][y],                
//                        0,
//                        chunk.getData()[x-Chunk.getBlocksX()*(pos % 3)][y - Chunk.getBlocksY()*(pos / 3)],
//                        0,
//                        Chunk.getBlocksZ()
//                    );
//                }
        return chunk;
    }

    /**
     * Inserts a chunk in the map.
     * @param pos The position in the grid
     * @param chunk The chunk you want to insert
     */
    private void insertChunk(final Coordinate topleftCorner, final Chunk chunk) {
//        for (int x=0;x < Chunk.getBlocksX(); x++)
//            for (int y=0;y < Chunk.getBlocksY();y++) {
//                System.arraycopy(
//                    chunk.getData()[x][y],
//                    0,
//                    data[x+ Chunk.getBlocksX()*(pos%3)][y+ Chunk.getBlocksY()*Math.abs(pos/3)],
//                    0,
//                    Chunk.getBlocksZ()
//                );
//            }
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
     * Returns a Block without checking the parameters first. Good for debugging and also faster.
	 * O(n)
     * @param x position
     * @param y position
     * @param z position
     * @return the single renderobject you wanted
     */
    public Block getBlock(final int x, final int y, final int z){
		if (z<0)
			return groundBlock;
		
		//find row in Z
		boolean found = false;
		Iterator<ArrayList<ArrayList<Block>>> iterOverZ = data.iterator();
		ArrayList<ArrayList<Block>> xRow = null;
		while (!found && iterOverZ.hasNext()) {
			xRow = iterOverZ.next();
			found = xRow.get(0).get(0).getPosition().getZ()==z;
		}
		
		//still not found, must be over map
		if (!found)
			return Block.getInstance(0); //return air
		
		//find row in x
		found = false;
		Iterator<ArrayList<Block>> iterOverX = xRow.iterator();
		ArrayList<Block> yRow = null;
		while (!found && iterOverX.hasNext()) {
			yRow = iterOverX.next();
			found = yRow.get(0).getPosition().getX()==x;
		}
		
		//still not found, must not loaded
		if (!found)
			return Block.getInstance(0); //return air
		
		//find row in y
		found = false;
		Iterator<Block> iterOverY = yRow.iterator();
		Block block = null;
		while (!found && iterOverY.hasNext()) {
			block = iterOverY.next();
			found = block.getPosition().getY()==y;
		}
		
		if (!found)
			return Block.getInstance(0); //return air
		
        return block;  
    }
    
    /**
     *
     * @param coord
     * @return
     */
    public Block getBlock(final Coordinate coord){
        return getBlock(coord.getX(), coord.getY(), coord.getZ()); 
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
        } else if( x<0 ){
            x = 0;
        }
        
        if (y >= blocksY){
            y = blocksY-1;
        } else if( y < 0 ){
            y = 0;
        }
        
        if (z >= blocksZ){
            z = blocksZ-1;
        } else if( z < 0 ){
            z = 0;
        }
        
        return getBlock(x, y, z);     
    }
    
    /**
     * Get a block at a coordinate but clamp it first.
     * @param coords
     * @return
     */
    public Block getDataClamp(final Coordinate coords) {
        return getBlockClamp(coords.getX(), coords.getY(), coords.getZ());
    }
    
    /**
     * Replace a block. Assume that the map already has been filled at this coordinate.
     * @param block
     */
    public void setData(final Block block) {
		Coordinate coord = block.getPosition();
		//find row in Z
		boolean found = false;
		Iterator<ArrayList<ArrayList<Block>>> iterOverZ = data.iterator();
		ArrayList<ArrayList<Block>> xRow = null;
		while (!found && iterOverZ.hasNext()) {
			xRow = iterOverZ.next();
			found = xRow.get(0).get(0).getPosition().getZ()==coord.getZ();
		}
		
		//still not found, must be over map
		if (!found)
			System.out.println("Tried to set block which is not in memory.");
		else {
			//find row in x
			found = false;
			Iterator<ArrayList<Block>> iterOverX = xRow.iterator();
			ArrayList<Block> yRow = null;
			while (!found && iterOverX.hasNext()) {
				yRow = iterOverX.next();
				found = yRow.get(0).getPosition().getX()==coord.getX();
			}
			
			//still not found, must not loaded
			if (!found)
				System.out.println("Tried to set block which is not in memory.");
			else {

				//find row in y
				found = false;
				int i = 0;
				while (!found && i < yRow.size()) {
					if (yRow.get(i).getPosition().getY()==coord.getY()){
						yRow.set(i, block);
						found=true;
					}
					i++;
				}
			}
		}
		
		
		modified();
    }
        
   /**
     * Set a block with safety checks (clamping to map).
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
        
		data.get(coords[2]).get(coords[0]).set(coords[1], block);
    }
    
    /**
     * Set a block with safety checks (clamping to map).
     * @param coord 
     * @param block
     */
    public void setDataSafe(final Coordinate coord, final Block block) {        
        setDataSafe(new int[]{
            coord.getX(),
            coord.getY(),
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
     * Returns the entityList
     * @return
     */
    public ArrayList<AbstractEntity> getEntitys() {
        return entityList;
    }
	
     /**
     * Find every instance of a special class e.g. find every AbstractCharacter
     * @param <type>
     * @param type
     * @return a list with the entitys
     */
    @SuppressWarnings({"unchecked"})
    public <type extends AbstractEntity> ArrayList<type> getEntitys(final Class<? extends AbstractEntity> type) {
        ArrayList<type> list = new ArrayList<>(30);//defautl size 30

        for (AbstractEntity entity : entityList) {//check every entity
            if (type.isInstance(entity)) {//if the entity is of the wanted type
                list.add((type) entity);//add it to list
            }
        }
        return list;
    }

    /**
     *Returns the degree of the world spin. This changes where the sun rises and falls.
     * @return a number between 0 and 360
     */
    public int getWorldSpinDirection() {
        return CVar.get("worldSpinAngle").getValuei();
    }
    
    
     /**
     * Get every entity on a coord.
     * @param coord
     * @return a list with the entitys
     */
    public ArrayList<AbstractEntity> getEntitysOnCoord(final Coordinate coord) {
        ArrayList<AbstractEntity> list = new ArrayList<>(5);//defautl size 5

        for (AbstractEntity ent : entityList) {
            if (Arrays.equals(
                    ent.getPosition().getCoord().getTriple(),
                    coord.getTriple()
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
    public <type> ArrayList<type> getEntitysOnCoord(final Coordinate coord, final Class<? extends AbstractEntity> type) {
        ArrayList<type> list = new ArrayList<>(5);

        for (AbstractEntity ent : entityList) {
            if (
                Arrays.equals(ent.getPosition().getCoord().getTriple(),coord.getTriple())//on coordinate?
                && type.isInstance(ent)//of tipe of filter?
                ){
                    list.add((type) ent);//add it to list
            } 
        }

        return list;
    }
	
	/**
	 * Get every entity on a chunk
	 * @param pos the chunk position 0-8
	 * @return 
	 */
	public ArrayList<AbstractEntity> getEntitysOnChunk(int pos){
		ArrayList<AbstractEntity> list = new ArrayList<>(10);

        for (AbstractEntity ent : entityList) {
            if (
					ent.getPosition().getX()>pos%3 *Chunk.getGameWidth()//left chunk border
                &&
					ent.getPosition().getX()<(pos%3+1)*Chunk.getGameWidth() //left chunk border
				&&	
					ent.getPosition().getY()>(pos/3)*Chunk.getGameDepth()//top chunk border
				&& 
					ent.getPosition().getY()<(pos/3+1)*Chunk.getGameDepth()//top chunk border
            ){
				list.add(ent);//add it to list
            } 
        }

        return list;
	}
	
		/**
	 * Get every entity on a chunk which should be saved
	 * @param pos the chunk position 0-8
	 * @return 
	 */
	public ArrayList<AbstractEntity> getEntitysOnChunkWhichShouldBeSaved(int pos){
		ArrayList<AbstractEntity> list = new ArrayList<>(10);

        for (AbstractEntity ent : entityList) {
            if (
					ent.isGettingSaved()
				&&
					ent.getPosition().getX()>pos%3 *Chunk.getGameWidth()//left chunk border
                &&
					ent.getPosition().getX()<(pos%3+1)*Chunk.getGameWidth() //left chunk border
				&&	
					ent.getPosition().getY()>(pos/3)*Chunk.getGameDepth()//top chunk border
				&& 
					ent.getPosition().getY()<(pos/3+1)*Chunk.getGameDepth()//top chunk border
            ){
				list.add(ent);//add it to list
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

    /**
     *
     * @return
     */
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
		//commented deep copy because the referals are still pointing to the old objects which causes invisible duplicates.
//		clone.entityList = new ArrayList<>(entityList.size());
//		for (AbstractEntity entity : entityList) {
//			clone.entityList.add((AbstractEntity) entity.clone());
//		}
        return (Map) super.clone();
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
					pos
                );
            } catch (IOException ex) {
                Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }

	/**
	 * set the modified flag to true. usually not manually called.
	 */
	public void modified(){
		this.modified = true;
	}
	
	/**
	 * 
	 * @return returns the modified flag
	 */
	public boolean isModified(){
		return modified;
	}

	/**
	 * called when the map is modified
	 */
	private void onModified() {
		//recalculates the light if requested
		Gdx.app.debug("Map", "modified");
		for (LinkedWithMap object : linkedObjects){
			object.onMapChange();
		}
	}
	
	public void addLinkedObject(LinkedWithMap object){
		linkedObjects.add(object);
	}

	public static void setDefaultGenerator(Generator defaultGenerator) {
		Map.defaultGenerator = defaultGenerator;
	}
	

	public Block getGroundBlock() {
		return groundBlock;
	}

	/**
	 * prints the map to console
	 */
	public void print() {
		for (int z = 0; z < blocksZ; z++) {
			for (int y = 0; y < blocksY; y++) {
				for (int x = 0; x < blocksX; x++) {
					if (getBlock(x, y, z).getId()==0)
						System.out.print("  ");
					else
						System.out.print(getBlock(x, y, z).getId() + " ");
				}
				System.out.print("\n");
			}
				System.out.print("\n\n");
		}
	}
	
	/**
     *
     */
    public void dispose(){
        for (int i = 0; i < entityList.size(); i++) {
			entityList.get(i).dispose();
        }
    }
	
	/**
	 * Get an iteration which can loop throug the map
	 * @param bottomLimitZ bottom limit of the iterations
	 * @param topLimitZ the top limit of the iterations
	 * @return 
	 */
	public MapIterator getIterator(int bottomLimitZ, int topLimitZ){
		if (mapIterator == null)//lazy init
			mapIterator = new MapIterator(this);
		mapIterator.setBottomLimitZ(bottomLimitZ);
		mapIterator.setTopLimitZ(topLimitZ);
		return mapIterator;
	}

	public void loadChunks(int direction, Coordinate center) {
	}

	public void deleteChunks(int direction, Coordinate center) {
	}

    
}