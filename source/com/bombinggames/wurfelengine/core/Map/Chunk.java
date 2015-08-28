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
package com.bombinggames.wurfelengine.core.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractLogicBlock;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Map.Iterators.DataIterator;
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
	private final static char SIGN_COMMAND = '~';//126 OR 0x7e
	private final static char SIGN_LINEFEED = 0x0A;//10 or 0x0A
	private final static char SIGN_EMTPYLAYER = 'e';//only valid after a command sign
	private final static char SIGN_LOGICBLOCKS = 'l';//only valid after a command sign
	
	/**
	 * the map in which the chunks are used
	 */
	private final Map map;
	
	/**
	 * chunk coordinate
	 */
	private final int coordX, coordY;
	/**
	 * the ids are stored here
	 */
    private final Block data[][][];
	/**
	 * a list containing the logic blocks which point to some blocks in this chunk
	 */
	private final ArrayList<AbstractLogicBlock> logicBlocks = new ArrayList<>(2);
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
    public Chunk(final Map map, final int coordX, final int coordY) {
        this.coordX = coordX;
		this.coordY = coordY;
		this.map = map;
		
		//set chunk dimensions
		blocksX = WE.CVARS.getChildSystem().getValueI("chunkBlocksX");
		blocksY = WE.CVARS.getChildSystem().getValueI("chunkBlocksY");
		blocksZ = WE.CVARS.getChildSystem().getValueI("chunkBlocksZ");
		
		topleft = new Coordinate(coordX*blocksX, coordY*blocksY, 0);
		data = new Block[blocksX][blocksY][blocksZ];
        
        for (int x=0; x < blocksX; x++)
            for (int y=0; y < blocksY; y++)
                for (int z=0; z < blocksZ; z++)
                    data[x][y][z] = null;
		
        resetClipping();
						
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
    public Chunk(final Map map, final File path, final int coordX, final int coordY, final Generator generator){
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
    public Chunk(final Map map, final int coordX, final int coordY, final Generator generator){
        this(map, coordX, coordY);
        fill(coordX, coordY, generator);
    }
	
	/**
	 * Updates the chunk. should be called once per frame.
	 * 
	 * @param dt
	 */
	public void update(float dt){	
		processModification();
		//reset light to zero
		for (Block[][] x : data) {
			for (Block[] y : x) {
				for (Block z : y) {
					if (z!=null)
						z.setLightlevel(0);
				}
			}
		}
		
		for (AbstractLogicBlock logicBlock : logicBlocks) {
			logicBlock.update(dt);
		}
		//check if block at position corespodends to saved, garbage collection
		logicBlocks.removeIf(
			(AbstractLogicBlock lb) -> {
				if (lb.getPosition().getBlock() == null)
					return true;
				return lb.getPosition().getBlock().getId() != lb.getBlock().getId();
			}
		);
	}
	
	/** 
	 * checks if the chunk got modified and if that is the case calls the modification methods
	 */
	public void processModification(){
		if (modified){
			modified = false;
			Controller.getMap().setModified();
			//notify observers that a chunk changed
			for (MapObserver observer : Controller.getMap().getOberservers()){
				observer.onChunkChange(this);
			}
		}
	}
    
    /**
     * Fills the chunk's block using a generator.
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
					Block block = Block.getInstance(
						generator.generate(
							left+x,
							top+y,
							z
						),
						(byte) 0
					);
                    data[x][y][z] = block;
					AbstractEntity[] entities = generator.generateEntities(
						left+x,
						top+y,
						z
					);
					if (entities != null && entities.length>0)
						Controller.getMap().addEntities(entities);
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
			try (ObjectInputStream fis = new ObjectInputStream(new FileInputStream(savepath.file()))) {
				int z = 0;
				int x;
				int y;

				int bChar = fis.read();

				//read a byte for the blocks
				while (bChar != -1) {//read while not eof
					if (bChar == SIGN_COMMAND) {
						bChar = fis.read();
						
						if (bChar == SIGN_EMTPYLAYER) {
							for (x = 0; x < blocksX; x++) {
								for (y = 0; y < blocksY; y++) {
									data[x][y][z] = null;
								}
							}
						}
						
						if (bChar == SIGN_ENTITIES || bChar == SIGN_LOGICBLOCKS)
							break;
					}
					
					if (bChar != SIGN_LINEFEED && bChar != SIGN_ENDCOMMENTS){//not a line break

						//jump over optional comment line
						if (bChar == SIGN_STARTCOMMENTS){
							do {
								bChar = fis.read(); //read until the end of comments
							} while (bChar != SIGN_ENDCOMMENTS);
						}

						//fill layer block by block
						y = 0;
						do {
							x = 0;
							do {
								byte id; 
								if (y==0 && x==0)//already read first one
									 id = (byte) bChar;
								else 
									id = (byte) fis.read();
								if (id > 0) {
									data[x][y][z] = Block.getInstance(id, (byte) fis.read());
								} else {
									data[x][y][z] = null;
								}
								x++;
							} while (x < blocksX);
							y++;
						} while (y < blocksY);
						z++;
					}
					
					//read next line
					bChar = fis.read();
				}
				//ends with a sign for logic or entities or eof
				
				if (bChar == SIGN_LOGICBLOCKS) {
					//load logicblocks
					try {
						//loading entities
						int length = fis.read(); //amount of entities
						Gdx.app.debug("Chunk", "Loading " + length +" logic blocks.");

						AbstractLogicBlock block;
						for (int i = 0; i < length; i++) {
							try {
								block = (AbstractLogicBlock) fis.readObject();
								Controller.getMap().addLogic(block);
								Gdx.app.debug("Chunk", "Loaded entity: "+block.toString());
								//objectIn.close();
							} catch (ClassNotFoundException | InvalidClassException ex) {
								Gdx.app.error("Chunk", "An logicBlock could not be loaded");
								Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					} catch (IOException ex) {
						Gdx.app.error("Chunk","Loading of entities in chunk" +path+"/"+coordX+","+coordY + " failed: "+ex);
					} catch (java.lang.NoClassDefFoundError ex) {
						Gdx.app.error("Chunk","Loading of entities in chunk " +path+"/"+coordX+","+coordY + " failed. Map file corrupt: "+ex);
					}
					
					bChar = fis.read();
					if (bChar == SIGN_COMMAND) {
						bChar = fis.read();
					}
				}
				
				if (bChar==SIGN_ENTITIES){
					if (WE.CVARS.getValueB("loadEntities")) {
						try {
							//loading entities
							int length = fis.read(); //amount of entities
							Gdx.app.debug("Chunk", "Loading " + length +" entities.");

							AbstractEntity object;
							for (int i = 0; i < length; i++) {
								try {
									object = (AbstractEntity) fis.readObject();
									Controller.getMap().addEntities(object);
									Gdx.app.debug("Chunk", "Loaded entity: "+object.getName());
									//objectIn.close();
								} catch (ClassNotFoundException | InvalidClassException ex) {
									Gdx.app.error("Chunk", "An entity could not be loaded");
									Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
								}
							}
						} catch (IOException ex) {
							Gdx.app.error("Chunk","Loading of entities in chunk" +path+"/"+coordX+","+coordY + " failed: "+ex);
						} catch (java.lang.NoClassDefFoundError ex) {
							Gdx.app.error("Chunk","Loading of entities in chunk " +path+"/"+coordX+","+coordY + " failed. Map file corrupt: "+ex);
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
        try (ObjectOutputStream fileOut = new ObjectOutputStream(new FileOutputStream(savepath))) {		
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
					fileOut.write(new byte[]{SIGN_STARTCOMMENTS, (byte) z, SIGN_ENDCOMMENTS});
					if (dirty)
						for (int y = 0; y < blocksY; y++) {
							for (int x = 0; x < blocksX; x++) {
								if (data[x][y][z]==null) {
									fileOut.write(0);
								} else {
									fileOut.write(data[x][y][z].getId());
									fileOut.write(data[x][y][z].getValue());
								}
							}
						}
					else {
						fileOut.write(SIGN_EMTPYLAYER);
					}
				}
				fileOut.flush();
				
				//save logicblocks
				ArrayList<AbstractLogicBlock> logicblocks = map.getLogicBlocksOnChunk(coordX, coordY);
				if (logicblocks.size()>0){
					fileOut.write(new byte[]{SIGN_COMMAND, SIGN_LOGICBLOCKS, (byte) logicblocks.size()});
					for (AbstractLogicBlock lb : logicblocks){
						Gdx.app.debug("Chunk", "Saving block:"+lb.toString());
						try {
							fileOut.writeObject(lb);
						} catch(java.io.NotSerializableException ex){
							Gdx.app.error("Chunk", "Something is not NotSerializable: "+ex.getMessage()+":"+ex.toString());
						}
					}
				}
				
				//save entities
				ArrayList<AbstractEntity> entities = map.getEntitysOnChunkWhichShouldBeSaved(coordX, coordY);
				if (entities.size() > 0) {
					fileOut.write(new byte[]{SIGN_COMMAND, SIGN_ENTITIES, (byte) entities.size()});
					for (AbstractEntity ent : entities){
						Gdx.app.debug("Chunk", "Saving entity:"+ent.getName());
						try {
							fileOut.writeObject(ent);
						} catch(java.io.NotSerializableException ex){
							Gdx.app.error("Chunk", "Something is not NotSerializable: "+ex.getMessage()+":"+ex.toString());
						}
					}
				}
			} catch (IOException ex){
				throw ex;
			}
			fileOut.close();
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
    public Block[][][] getData() {
        return data;
    }

    /**
     *Not scaled.
     * @return
     */
    public static int getViewWidth(){
        return blocksX*Block.VIEW_WIDTH;
    }
    
    /**
     *Not scaled.
     * @return
     */
    public static int getViewDepth() {
        return blocksY*Block.VIEW_DEPTH2;// Divided by 2 because of shifted each second row.
    }
    
    /**
     *x axis
     * @return
     */
    public static int getGameWidth(){
        return blocksX*Block.GAME_DIAGLENGTH;
    }
    
    /**
     *y axis
     * @return
     */
    public static int getGameDepth() {
        return blocksY*Block.GAME_DIAGLENGTH2;
    }
    
        /**
     * The height of the map. z axis
     * @return in game size
     */
    public static int getGameHeight(){
        return blocksZ*Block.GAME_EDGELENGTH;
    }
	
	
	/**
	 * Check if the chunk has the coordinate inside. Only checks x and y.<br>
	 * O(1)
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
		float left = getTopLeftCoordinate().toPoint().getX();
		float top = getTopLeftCoordinate().toPoint().getY();
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
	 * Returns an iterator which iterates over the data in this chunk.
	 * @param startingZ
	 * @param limitZ
	 * @return
	 */
	public DataIterator<Block> getIterator(final int startingZ, final int limitZ){
		return new DataIterator<>(
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
	 * @return not copy safe
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
	public Block getBlock(int x, int y, int z) {
		int xIndex = x-topleft.getX();
		int yIndex = y-topleft.getY();
		return data[xIndex][yIndex][z];
	}
	
	/**
	 * Get the block at the index position
	 * @param x index pos
	 * @param y index pos
	 * @param z index pos
	 * @return 
	 */
	public Block getBlockViaIndex(int x, int y, int z) {
		return data[x][y][z];
	}

	/**
	 * sets a block in the map. if position is under the map does nothing.
	 * @param block no null pointer allowed
	 */
	public void setBlock(RenderBlock block) {
		//get corresponding logic and update
		if (block != null) {
			AbstractLogicBlock logic = block.getBlockData().getLogicInstance(block.getPosition());
			if (logic != null)
				logicBlocks.add(logic);
		}
		
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
	public void setBlock(Coordinate coord, Block block) {
		//get corresponding logic and update
		if (block != null) {
			AbstractLogicBlock logic = block.getLogicInstance(coord);
			if (logic != null)
			logicBlocks.add(block.getLogicInstance(coord));
		}
		
		int xIndex = coord.getX()-topleft.getX();
		int yIndex = coord.getY()-topleft.getY();
		int z = coord.getZ();
		if (z >= 0){
			data[xIndex][yIndex][z] = block;
			modified = true;
		}
	}
	
	public void addLogic(AbstractLogicBlock block) {
		logicBlocks.add(block);
	}
		
	/**
	 * Get the logic to a logicblock.
	 * @param coord 
	 * @return can return null
	 */
	public AbstractLogicBlock getLogic(Coordinate coord) {
		Block block = coord.getBlock();
		if (block != null) {
			for (AbstractLogicBlock logicBlock : logicBlocks) {
				if (logicBlock.getPosition().equals(coord) && logicBlock.getBlock().getId() == block.getId()) {
					return logicBlock;
				}
			}
		}
		return null;
	}

	public ArrayList<AbstractLogicBlock> getLogicBlocks() {
		return logicBlocks;
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

	protected void resetClipping() {
		for (int x=0; x < blocksX; x++)
			for (int y=0; y < blocksY; y++) {
				for (int z=0; z < blocksZ; z++)
					if (data[x][y][z]!=null)
						data[x][y][z].setUnclipped();
			}
	}

}