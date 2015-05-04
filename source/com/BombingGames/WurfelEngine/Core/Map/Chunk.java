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

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.BombingGames.WurfelEngine.Core.Map.Iterators.DataIterator;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Chunk is filled with many Blocks and is a part of the map.
 * @author Benedikt
 */
public class Chunk {
    /**The suffix of a chunk files.*/
    protected static final String CHUNKFILESUFFIX = "wec";

	private static int blocksX = 10;
    private static int blocksY = 40;//blocksY must be even number
    private static int blocksZ = 10;
	
	/**
	 * save file stuff
	 */
	private final static char SIGN_ENTITIES = '|';//124 OR 0x7c
	private final static char SIGN_STARTCOMMENTS = '{';//123 OR 0x7b
	private final static char SIGN_ENDCOMMENTS = '}';//125 OR 0x7d
	private final static char SIGN_EMPTYLAYER = '~';//126 OR 0x7e
	private final static char SIGN_LINEFEED = 0x0A;//10 or 0x0A
	
	/**
	 * the map in which the chunks are used
	 */
	private final ChunkMap map;
	
	/**
	 * chunk coordinate
	 */
	private final int coordX, coordY;
	/**
	 * the ids are stored here
	 */
    private final CoreData data[][][];
	private boolean modified;
	/**
	 * How many cameras are pointing at this chunk? If &lt;= 0 delete from memory.
	 */
	private int cameraAccessCounter = 0;
	private Coordinate topleft;
  
    /**
     * Creates a Chunk filled with empty cells (likely air).
	 * @param map
	 * @param coordX
	 * @param coordY
     */
    public Chunk(final ChunkMap map, final int coordX, final int coordY) {
        this.coordX = coordX;
		this.coordY = coordY;
		this.map = map;
		
		//set chunk dimensions
		blocksX = map.getCVars().getValueI("chunkBlocksX");
		blocksY = map.getCVars().getValueI("chunkBlocksY");
		blocksZ = map.getCVars().getValueI("chunkBlocksZ");
		
		topleft = new Coordinate(map, coordX*blocksX, coordY*blocksY, 0);
		data = new CoreData[blocksX][blocksY][blocksZ];
        
        for (int x=0; x < blocksX; x++)
            for (int y=0; y < blocksY; y++)
                for (int z=0; z < blocksZ; z++)
                    data[x][y][z] = null;
		modified = true;
    }
    
    /**
    *Creates a chunk by trying to load and if this fails it generates a new one.
	 * @param map
    * @param coordX the chunk coordinate
    * @param coordY the chunk coordinate
     * @param path filename
     * @param generator
    */
    public Chunk(final ChunkMap map, final File path, final int coordX, final int coordY, final Generator generator){
        this(map, coordX,coordY);
		if (WE.CVARS.getValueB("shouldLoadMap")){
			if (!load(path, map.getCurrentSaveSlot(), coordX, coordY))
				fill(coordX, coordY, generator);
		} else fill(coordX, coordY, generator);
		increaseCameraHandleCounter();
    }
    
    /**
    *Creates a chunk by generating a new one.
	 * @param map
    * @param coordX the chunk coordinate
    * @param coordY the chunk coordinate
    * @param generator
    */
    public Chunk(final ChunkMap map, final int coordX, final int coordY, final Generator generator){
        this(map, coordX, coordY);
        fill(coordX, coordY, generator);
    }
	
	/**
	 *
	 * @param dt
	 */
	public void update(float dt){
//		for (StorageBlock[][] x : data) {
//			for (StorageBlock[] y : x) {
//				for (StorageBlock z : y) {
//					if (z!=null)
//						z.update(dt);
//				}
//			}
//		}
		
		processModification();
	}
	
	/** 
	 */
	public void processModification(){
		if (modified){
			modified = false;
			Controller.getMap().modified();
			for (LinkedWithMap object : Controller.getMap().getLinkedObjects()){
				object.onChunkChange(this);
			}
		}
	}
    
    /**
     * Fills the chunk using a generator.
     * @param chunkCoordX
     * @param chunkCoordY
     * @param generator 
     */
    private void fill(final int chunkCoordX, final int chunkCoordY, final Generator generator){
		int left = blocksX*chunkCoordX;
		int top = blocksY*chunkCoordY;
        for (int x = 0; x < blocksX; x++)
            for (int y = 0; y < blocksY; y++)
                for (int z = 0; z < blocksZ; z++){
					CoreData block = CoreData.getInstance(
						generator.generate(
							left+x,
							top+y,
							z
						),
						(byte) 0
					);
                    data[x][y][z] = block;
				}
		modified = true;
    }
    
    /**
     * Tries to load a chunk from disk.
     */
    private boolean load(final File path, int saveSlot, int coordX, int coordY){

		//FileHandle path = Gdx.files.internal("/map/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX);
		FileHandle savepath = Gdx.files.absolute(path+"/save"+saveSlot+"/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX);

		if (savepath.exists()) {
			Gdx.app.debug("Chunk","Loading Chunk: "+ coordX + ", "+ coordY);
			//Reading map files test
			try (FileInputStream fis = new FileInputStream(savepath.file())) {
				int z = 0;
				int x;
				int y;

				int bufChar = fis.read();

				//read a byte
				while (bufChar != -1 && bufChar != SIGN_ENTITIES) {//read while not eof and not at entity part
					if (bufChar == SIGN_LINEFEED) //skip line breaks
						bufChar = fis.read();

					if (bufChar != SIGN_LINEFEED){//not a line break

						//jump over optional comment line
						if (bufChar == SIGN_STARTCOMMENTS){
							bufChar = fis.read();
							while (bufChar != SIGN_ENDCOMMENTS){
								bufChar = fis.read();
							}
							bufChar = fis.read();
							if (bufChar== SIGN_LINEFEED)//if following is a line break also skip it again
								bufChar = fis.read();
						}

						//if layer is empty, fill with air
						if (bufChar == SIGN_EMPTYLAYER ){
							for (x = 0; x < blocksX; x++) {
								for (y = 0; y < blocksY; y++) {
									data[x][y][z] = null;
								}
							}
						} else {
							//fill layer block by block
							y = 0;
							do {
								x = 0;
								do {

									byte id; 
									if (y==0 && x==0)//already got first one
										 id = (byte) bufChar;
									else 
										id = (byte) fis.read();
									byte value = (byte) fis.read();
									if (id > 0) {
										data[x][y][z] = CoreData.getInstance(id, value);
									} else 
										data[x][y][z] =null;
									x++;
								} while (x < blocksX);
								y++;
							} while (y < blocksY);
						}
						z++;
					}
					//read next line
					bufChar = fis.read();
				}

				if (WE.CVARS.getValueB("loadEntities")) {
					//loading entities
					if (bufChar==SIGN_ENTITIES){
						int length = fis.read(); //amount of entities
						Gdx.app.debug("Chunk", "Loading " + length+" entities");

						AbstractEntity object;
						for (int i = 0; i < length; i++) {
							try {
								ObjectInputStream objectIn = new ObjectInputStream(fis);
								object = (AbstractEntity) objectIn.readObject();
								Controller.getMap().getEntitys().add(object);
								Gdx.app.debug("Chunk", "Loaded entity: "+object.getName());
								//objectIn.close();
							} catch (ClassNotFoundException | InvalidClassException ex) {
								Gdx.app.error("Chunk", "An entity could not be loaded");
								Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					}
				}
				modified = true;
				return true;
			} catch (IOException ex) {
				Gdx.app.error("Chunk","Loading of chunk" +path+"/"+coordX+","+coordY + " failed: "+ex);
			} catch (StringIndexOutOfBoundsException | NumberFormatException ex) {
				Gdx.app.error("Chunk","Loading of chunk " +path+"/"+coordX+","+coordY + " failed. Map file corrupt: "+ex);
			} catch (ArrayIndexOutOfBoundsException ex){
				Gdx.app.error("Chunk","Loading of chunk " +path+"/"+coordX+","+coordY + " failed. Chunk or meta file corrupt: "+ex);
			}
		} else {
			Gdx.app.log("Chunk",savepath+" could not be found on disk. Trying to restore chunk.");
			if (restoreFromRoot(path, saveSlot, coordX, coordY))
				load(path, saveSlot, coordX, coordY);
		}
		
        return false;
    }
	
	public boolean restoreFromRoot(final File path, int saveSlot, int coordX, int coordY){
		FileHandle chunkInRoot = Gdx.files.absolute(path+"/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX);
		if (chunkInRoot.exists() && !chunkInRoot.isDirectory()){
			chunkInRoot.copyTo(Gdx.files.absolute(path+"/save"+saveSlot+"/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX));
			load(path, saveSlot, coordX, coordY);
		} else {
			Gdx.app.log("Chunk","Restoring:" + chunkInRoot +" failed.");
			return false;
		}
		return true;
	}
    
	
    /**
     * 
     * @param path the map name on disk
	 * @param saveSlot

     * @return 
     * @throws java.io.IOException 
     */
    public boolean save(File path, int saveSlot) throws IOException {
        if (path == null) return false;
        Gdx.app.log("Chunk","Saving "+coordX + ","+ coordY +".");
        File savepath = new File(path+"/save"+saveSlot+"/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX);
        
        savepath.createNewFile();
        try (FileOutputStream fileOut = new FileOutputStream(savepath)) {		
			try {
				for (int z = 0; z < blocksZ; z++) {
					//check if layer is empty
					boolean dirty = false;
					for (int x = 0; x < blocksX; x++) {
						for (int y = 0; y < blocksY; y++) {
							if (data[x][y][z] != null)
								dirty=true;
						}
					}
					fileOut.write(SIGN_STARTCOMMENTS);
					fileOut.write(z);
					fileOut.write(SIGN_ENDCOMMENTS);
					if (dirty)
						for (int y = 0; y < blocksY; y++) {
							for (int x = 0; x < blocksX; x++) {
								if (data[x][y][z]==null) {
									fileOut.write(0);
									fileOut.write(0);
								} else {
									fileOut.write(data[x][y][z].getId());
									fileOut.write(data[x][y][z].getValue());
								}
							}
						}
					else {
						fileOut.write(SIGN_EMPTYLAYER);
					}
				}
				
				//save entities
				ArrayList<AbstractEntity> entities = map.getEntitysOnChunkWhichShouldBeSaved(coordX, coordY);
				if (entities.size() > 0) {
					fileOut.write(SIGN_ENTITIES);
					fileOut.write(entities.size());
					try (ObjectOutputStream outStream = new ObjectOutputStream(fileOut)) {
						for (AbstractEntity ent : entities){
							Gdx.app.debug("Chunk", "Saving entity:"+ent.getName());
							try {
								outStream.writeObject(ent);
							} catch(java.io.NotSerializableException ex){
								Gdx.app.error("Chunk", "Something is not NotSerializable: "+ex.getMessage()+":"+ex.toString());
							}
						}
						outStream.close();
					}
				}
			} catch (IOException ex){
				throw ex;
			}
		}
		return true;
    }
        /**
     * The amount of blocks in X direction
     * @return 
     */
    public static int getBlocksX() {
        return blocksX;
    }

    /**
     * The amount of blocks in Y direction
     * @return 
     */
    public static int getBlocksY() {
        return blocksY;
    }

   /**
     * The amount of blocks in Z direction
     * @return 
     */
    public static int getBlocksZ() {
        return blocksZ;
    }
    

    /**
     * Returns the data of the chunk
     * @return 
     */
    public CoreData[][][] getData() {
        return data;
    }

    /**
     *Not scaled.
     * @return
     */
    public static int getViewWidth(){
        return blocksX*AbstractGameObject.VIEW_WIDTH;
    }
    
    /**
     *Not scaled.
     * @return
     */
    public static int getViewDepth() {
        return blocksY*AbstractGameObject.VIEW_DEPTH2;// Divided by 2 because of shifted each second row.
    }
    
    /**
     *x axis
     * @return
     */
    public static int getGameWidth(){
        return blocksX*AbstractGameObject.GAME_DIAGLENGTH;
    }
    
    /**
     *y axis
     * @return
     */
    public static int getGameDepth() {
        return blocksY*AbstractGameObject.GAME_DIAGLENGTH2;
    }
    
        /**
     * The height of the map. z axis
     * @return in game size
     */
    public static int getGameHeight(){
        return blocksZ*AbstractGameObject.GAME_EDGELENGTH;
    }
	
	
	/**
	 * Check if the coordinate has the coordinate inside. O(1)
	 * @param coord the coordinate to be checked
	 * @return true if coord is inside.
	 */
	public boolean hasCoord(Coordinate coord){
		int x = coord.getX();
		int y = coord.getY();
		int left = topleft.getX();
		int top = topleft.getY();
		return (   x >= left
				&& x <  left + blocksX
				&& y >= top
				&& y <  top + blocksY
		);
	}
	
		/**
	 * Check if the coordinate has the coordinate inside.
	 * @param point the coordinate to be checked
	 * @return true if coord is inside.
	 */
	public boolean hasPoint(Point point){
		float x = point.getX();
		float y = point.getY();
		float left = getTopLeftCoordinate().getPoint().getX();
		float top = getTopLeftCoordinate().getPoint().getY();
		return (x >= left
				&& x < left + getGameWidth()
				&& y >= top
				&& y < top + getGameDepth()
		);
	}
	
	/**
	 * print the chunk to console
	 * @return 
	 */
	@Override
	public String toString() {
		String strg = null;
		for (int z = 0; z < blocksZ; z++) {
			for (int y = 0; y < blocksY; y++) {
				for (int x = 0; x < blocksX; x++) {
					if (data[x][y][z].getId()==0)
						strg += "  ";
					else
						strg += data[x][y][z].getId() + " ";
				}
				strg += "\n";
			}
			strg += "\n\n";
		}
		return strg;
	}
	
	/**
	 *
	 * @param startingZ
	 * @param limitZ
	 * @return
	 */
	public DataIterator getIterator(final int startingZ, final int limitZ){
		return new DataIterator(
			data,
			startingZ,
			limitZ
		);
	}

	/**
	 * Get the chunk coordinate of this chunk.
	 * @return
	 */
	public int getChunkX() {
		return coordX;
	}

	/**
	 * Get the chunk coordinate of this chunk.
	 * @return
	 */
	public int getChunkY() {
		return coordY;
	}
	
	/**
	 *
	 * @return
	 */
	public Coordinate getTopLeftCoordinate(){
		return topleft;
	}

	/**
	 * 
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return can be null
	 */
	public CoreData getBlock(int x, int y, int z) {
		int xIndex = x-topleft.getX();
		int yIndex = y-topleft.getY();
		return data[xIndex][yIndex][z];
	}

	/**
	 * sets a block in the map. if position is under the map does nothing.
	 * @param block no null pointer allowed
	 */
	public void setBlock(RenderBlock block) {
		int xIndex = block.getPosition().getX()-topleft.getX();
		int yIndex = block.getPosition().getY()-topleft.getY();
		int z = block.getPosition().getZ();
		if (z >= 0){
			data[xIndex][yIndex][z] = block.toStorageBlock();
			modified = true;
		}
	}
	
	/**
	 * 
	 * @param coord
	 * @param block 
	 */
	public void setBlock(Coordinate coord, CoreData block) {
		int xIndex = coord.getX()-topleft.getX();
		int yIndex = coord.getY()-topleft.getY();
		int z = coord.getZ();
		if (z >= 0){
			data[xIndex][yIndex][z] = block;
			modified = true;
		}
	}
	
	/**
	 * Set that no camera is accessing this chunk.
	 */
	public void resetCameraAccesCounter(){
		cameraAccessCounter=0;
	}
	
	/**
	 *
	 */
	public final void increaseCameraHandleCounter(){
		cameraAccessCounter++;
	}

	/**
	 * Can this can be removed from memory?
	 * @return true if no camera is rendering this chunk
	 */
	boolean shouldBeRemoved() {
		return cameraAccessCounter <= 0;
	}
	
	/**
	 * disposes the chunk
	 * @param path if null, does not save the file
	 */
	public void dispose(File path){
		//try saving
		if (path != null) {
			try {
				save(path, map.getCurrentSaveSlot());
			} catch (IOException ex) {
				Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		//remove entities on this chunk from map
		ArrayList<AbstractEntity> entities = map.getEntitysOnChunk(coordX, coordY);
		for (AbstractEntity ent : entities) {
			ent.disposeFromMap();
		}
	}

}