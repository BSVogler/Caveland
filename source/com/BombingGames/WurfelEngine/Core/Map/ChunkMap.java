/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
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

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.BombingGames.WurfelEngine.Core.Map.Iterators.MemoryMapIterator;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *A map stores nine chunks as part of a bigger map. It also contains the entities.
 * @author Benedikt Vogler
 */
public class ChunkMap extends AbstractMap implements Cloneable {
	private static int blocksX;
	private static int blocksY;
	private static int blocksZ;
        
    
    /** Stores the data of the map. */
    private ArrayList<Chunk> data;
    
    
	
	
	    /**
     * Copies an array with three dimensions. Deep copy until the cells content of cells shallow copy.
     * @param array the data you want to copy
     * @return The copy of the array-
     */
    private static RenderBlock[][][] copyBlocks(final RenderBlock[][][] array) {
        RenderBlock[][][] copy = new RenderBlock[array.length][][];
        for (int i = 0; i < array.length; i++) {
            copy[i] = new RenderBlock[array[i].length][];
            for (int j = 0; j < array[i].length; j++) {
                copy[i][j] = new RenderBlock[array[i][j].length];
                System.arraycopy(
                    array[i][j], 0, copy[i][j], 0, 
                    array[i][j].length
                );
            }
        }
        return copy;
    } 
	
    /**
     * Loads a map using the default generator.
     * @param name if available on disk it will be load
	 * @param saveslot
     * @throws java.io.IOException
     */
    public ChunkMap(final File name, int saveslot) throws IOException{
        this(name, getDefaultGenerator(), saveslot);
    }
    
    /**
     * Loads a map.
     * @param name if available on disk it will load the meta file
     * @param generator the generator used for generating new chunks
	 * @param saveslot
     * @throws java.io.IOException thrown if there is no full read/write access to the map file
     */
    public ChunkMap(final File name, Generator generator, int saveslot) throws IOException {
		super(name, generator, saveslot);
        Gdx.app.debug("Map","Map named \""+ name +"\", saveslot "+ saveslot +" should be loaded");

        //save chunk size, which are now loaded
        blocksX = Chunk.getBlocksX()*3;
        blocksY = Chunk.getBlocksY()*3;
        blocksZ =  Chunk.getBlocksZ();
        data = new ArrayList<>(9);

		//printCoords();
    }
    
	/**
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt){
		super.update(dt);
		
		//update every block on the map
		for (Chunk chunk : data) {
			chunk.update(dt);
		}
	}
	
	/**
	 * Called after the view update to catch changes caused by the view
	 * @param dt 
	 */
	@Override
	public void postUpdate(float dt) {
		if (WE.CVARS.getValueB("mapChunkSwitch")) {
			//some custom garbage collection, removes chunks
			for (int i = 0; i < data.size(); i++) {
				data.get(i).resetCameraAccesCounter();
			}
		}
		
		//check for modification flag
		for (Chunk chunk : data) {
			chunk.processModification();
		}
		
		modificationCheck();
	}
		
	/**
	 * loads a chunk from disk
	 * @param chunkX
	 * @param chunkY 
	 */
	public void loadChunk(int chunkX, int chunkY){
		//TODO if already there.
		data.add(
			new Chunk(this, getPath(), chunkX, chunkY, getGenerator())
		);
		modified();
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
	@Override
    public CoreData getBlock(final int x, final int y, final int z){
		return getBlock(new Coordinate(this, x, y, z)); 
    }
    
	@Override
    public CoreData getBlock(final Coordinate coord){
		if (coord.getZ() < 0)
			return getGroundBlock();
		Chunk chunk = getChunk(coord);
		if (chunk==null)
			return null;
		else
			return chunk.getBlock(coord.getX(), coord.getY(), coord.getZ());//find chunk in x coord

    }
    
	@Override
    public void setBlock(final RenderBlock block) {
		getChunk(block.getPosition()).setBlock(block);
    }

	@Override
	public void setBlock(Coordinate coord, CoreData block) {
		getChunk(coord).setBlock(coord, block);
	}
	
	/**
	 * get the chunk where the coordinates are on
	 * @param coord
	 * @return 
	 */
	public Chunk getChunk(Coordinate coord){
		for (Chunk chunk : data) {
			int left = chunk.getTopLeftCoordinate().getX();
			int top  = chunk.getTopLeftCoordinate().getY();
			//identify chunk
			if (
				   left <= coord.getX()
				&& coord.getX() < left + Chunk.getBlocksX()
				&& top <= coord.getY()
				&& coord.getY() < top + Chunk.getBlocksY() 
			) {
				return chunk;
			}
		}
		return null;//not found
	}
	
	/**
	 * get the chunk with the given chunk coords. <br>Runtime: O(c) where c = amount of chunks -&gt; O(1)
	 * @param chunkX
	 * @param chunkY
	 * @return if not in memory return null
	 */
	public Chunk getChunk(int chunkX, int chunkY){
		for (Chunk chunk : data) {
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
	 * Get every entity on a chunk.
	 * @param xChunk
	 * @param yChunk
	 * @return 
	 */
	public ArrayList<AbstractEntity> getEntitysOnChunk(final int xChunk, final int yChunk){
		ArrayList<AbstractEntity> list = new ArrayList<>(10);

		//loop over every loaded entity
        for (AbstractEntity ent : getEntitys()) {
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
	 * Get every entity on a chunk which should be saved
	 * @param xChunk
	 * @param yChunk
	 * @return 
	 */
	public ArrayList<AbstractEntity> getEntitysOnChunkWhichShouldBeSaved(final int xChunk, final int yChunk){
		ArrayList<AbstractEntity> list = new ArrayList<>(10);

		//loop over every loaded entity
        for (AbstractEntity ent : getEntitys()) {
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
     * saves every chunk on the map
     * @return 
     */
	@Override
    public boolean save(int saveSlot) {
		for (Chunk chunk : data) {
			try {
                chunk.save(
                    getPath(),
					saveSlot
                );
            } catch (IOException ex) {
                Logger.getLogger(ChunkMap.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
		}
        return true;
    }

	


	/**
	 * prints the map to console
	 */
	@Override
	public void print() {
		MemoryMapIterator iter = getIterator(0, blocksZ-1);
		while (iter.hasNext()){
			//if (!iter.hasNextY() && !iter.hasNextX())
			//	System.out.print("\n\n");
			//if (!iter.hasNsextX())
			//	System.out.print("\n");
			
			CoreData block = iter.next();
			if (block.getId()==0)
				System.out.print("  ");
			else
				System.out.print(block.getId() + " ");
		}
	}
	
	/**
     *
     */
	@Override
    public void dispose(){
		for (Chunk chunk : data) {
			chunk.dispose(getPath());
		}
		super.dispose();
    }

	/**
	 * Returns the amount of Blocks inside the map in x-direction.
	 * @return
	 */
	@Override
	public int getBlocksX() {
		return blocksX;
	}

	/**
	 * Returns the amount of Blocks inside the map in y-direction.
	 * @return
	 */
	@Override
	public int getBlocksY() {
		return blocksY;
	}

	/**
	 * Returns the amount of Blocks inside the map in z-direction.
	 * @return
	 */
	@Override
	public int getBlocksZ() {
		return blocksZ;
	}

}