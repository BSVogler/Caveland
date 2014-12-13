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
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.Generators.AirGenerator;
import com.badlogic.gdx.Gdx;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *A map stores nine chunks as part of a bigger map. It also contains the entities.
 * @author Benedikt Vogler
 */
public class Map implements Cloneable{
    private static int blocksX, blocksY, blocksZ;
	private static Generator defaultGenerator = new AirGenerator();
        
    private final String filename;
    
	private final Block groundBlock = Block.getInstance(CVar.get("groundBlockID").getValuei());//the representative of the bottom layer (ground) block
    /** Stores the data of the map. */
    private ArrayList<Chunk> data;
    
    private Generator generator;
    
    /** every entity on the map is stored in this field */
    private ArrayList<AbstractEntity> entityList = new ArrayList<>(20);
    /**
     * holds the metadata of the map
     */
    private final MapMetaData meta;
	
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
        data = new ArrayList<>(9);

		//printCoords();
		        
        this.generator = generator;
		modified = true;
    }
    
	public void update(float dt){
		//update every block on the map
		for (Chunk chunk : data) {
			chunk.update(dt);
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
		if (CVar.get("enableChunkSwitch").getValueb()) {
			//some custom garbage collection
			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).shouldBeRemoved())
					data.remove(i);
				else data.get(i).resetCameraAccesCounter();
			}
		}
		
		//check for modification flag
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
	 * loads a chunk from disk
	 * @param chunkX
	 * @param chunkY 
	 */
	public void loadChunk(int chunkX, int chunkY){
		//TODO if already there.
		data.add(new Chunk(filename, chunkX, chunkY, generator));
		modified();
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
	public ArrayList<Chunk> getData() {
		return data;
	}
     
    /**
     * Returns a block without checking the parameters first. Good for debugging and also faster.
	 * O(n)
     * @param x coordinate
     * @param y coordinate
     * @param z coordinate
     * @return the single block you wanted
     */
    public Block getBlock(final int x, final int y, final int z){
		if (z<0)
			return groundBlock;
		Chunk chunk = getChunk(new Coordinate(x, y, z));
		if (chunk==null)
			return null;
		else
			return chunk.getBlock(x, y, z);//find chunk in x coord
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
		getChunk(block.getPosition()).setBlock(block);
    }
	
	/**
	 * get the chunk where the coordinates are on
	 * @param coord
	 * @return 
	 */
	public Chunk getChunk(Coordinate coord){
		for (Chunk chunk : data) {
			int left = chunk.getData()[0][0][0].getPosition().getX();
			int top = chunk.getData()[0][0][0].getPosition().getY();
			//identify chunk
			if (
				   left <= coord.getX()
				&& left + Chunk.getBlocksX() > coord.getX()
				&& top <= coord.getY()
				&& top + Chunk.getBlocksY() > coord.getY()
			) {
				return chunk;
			}
		}
		return null;//not found
	}
	
	/**
	 * get the chunkwith the chunk coords
	 * @param chunkX
	 * @param chunkY
	 * @return 
	 */
	public Chunk getChunk(int chunkX, int chunkY){
		for (Chunk chunk : data) {
			//identify chunk
			if (
				   chunkX == chunk.getChunkX()
				&& chunkY == chunk.getChunkY()
			) {
				return chunk;
			}
		}
		return null;//not found
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
            if ( ent.getPosition().getCoord().equals(coord) ){
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
                ent.getPosition().getCoord().getVector().equals(coord.getVector())//on coordinate?
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
	 * @param xChunk
	 * @param yChunk
	 * @return 
	 */
	public ArrayList<AbstractEntity> getEntitysOnChunkWhichShouldBeSaved(final int xChunk, final int yChunk){
		ArrayList<AbstractEntity> list = new ArrayList<>(10);

		//loop over every loaded entity
        for (AbstractEntity ent : entityList) {
            if (
					ent.isGettingSaved() //save only entities which are flagged
				&&
					ent.getPosition().getX() > xChunk*Chunk.getGameWidth()//left chunk border
                &&
					ent.getPosition().getX() < (xChunk+1)*Chunk.getGameWidth() //left chunk border
				&&	
					ent.getPosition().getY() > (yChunk)*Chunk.getGameDepth()//top chunk border
				&& 
					ent.getPosition().getY() < (yChunk+1)*Chunk.getGameDepth()//top chunk border
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
     *Returns a coordinate pointing to middle of a 3x3 chunk map.
     * @param height You custom height.
     * @return
     */
    public static Point getCenter(final float height){
        return
            new Point(
                Chunk.getGameWidth()/2,
                Chunk.getGameDepth()/2,
                height
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
		for (Chunk chunk : data) {
			try {
                chunk.save(
                    filename
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

	public ArrayList<LinkedWithMap> getLinkedObjects() {
		return linkedObjects;
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
		MapIterator iter = getIterator(blocksZ-1);
		while (iter.hasNext()){
			//if (!iter.hasNextY() && !iter.hasNextX())
			//	System.out.print("\n\n");
			//if (!iter.hasNsextX())
			//	System.out.print("\n");
			
			Block block = iter.next();
			if (block.getId()==0)
				System.out.print("  ");
			else
				System.out.print(block.getId() + " ");
		}
	}
	
		/**
	 * prints the map to console
	 */
	public final void printCoords() {
		MapIterator iter = getIterator(blocksZ-1);
		while (iter.hasNext()){
		//	if (!iter.hasNextY() && !iter.hasNextX())
		//		System.out.print("\n\n");
		//	if (!iter.hasNextX())
		//		System.out.print("\n");
			
			Block block = iter.next();
			System.out.print(block.getPosition()+" ");
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
	 * @param topLimitZ the top limit of the iterations
	 * @return 
	 */
	public MapIterator getIterator(int topLimitZ){
		MapIterator mapIterator = new MapIterator();
		mapIterator.setTopLimitZ(topLimitZ);
		return mapIterator;
	}

}