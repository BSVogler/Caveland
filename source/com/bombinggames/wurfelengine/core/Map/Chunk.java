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
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Gameobjects.Side;
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
	private final static char SIGN_COMMAND = '~';//126 OR 0x7e
	private final static char SIGN_EMTPYLAYER = 'e';//only valid after a command sign
	private final static char SIGN_ENDBLOCKS = 'b';//only valid after a command sign

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
	 * A list containing the logic blocks. Each logic block points to some block in this chunk.
	 */
	private final ArrayList<AbstractBlockLogicExtension> logicBlocks = new ArrayList<>(2);
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
		blocksX = WE.getCvars().getChildSystem().getValueI("chunkBlocksX");
		blocksY = WE.getCvars().getChildSystem().getValueI("chunkBlocksY");
		blocksZ = WE.getCvars().getChildSystem().getValueI("chunkBlocksZ");

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
		if (WE.getCvars().getValueB("shouldLoadMap")){
			if (!load(path, map.getCurrentSaveSlot(), coordX, coordY)) {
				fill(generator);
			}
		} else fill(generator);
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
        fill(generator);
    }

	/**
	 * Updates the chunk. should be called once per frame.
	 *
	 * @param dt time since last frame in game time
	 */
	public void update(float dt){
		processModification();
		//reset light to normal level
		int maxZ = Chunk.getBlocksZ();
		for (Block[][] x : data) {
			for (Block[] y : x) {
				for (int z = 0; z < y.length; z++) {
					if (y[z] != null) {
						y[z].setLightlevel(1.0f);

						if (
							z < maxZ-2
							&& (
								y[z+1] == null
								|| y[z+1].isTransparent()
							)
						){
							//two block above is a block casting shadows
							if (y[z+2] != null
								&& !y[z+2].isTransparent()
								) {
									y[z].setLightlevel(0.8f, Side.TOP);
								} else if (
									z < maxZ-3
									&& (
										y[z+2] == null
										|| y[z+2].isTransparent()
									)
									&& y[z+3] != null
									&& !y[z+3].isTransparent()
								) {
									y[z].setLightlevel(0.9f, Side.TOP);
								}
						}
					}
				}
			}
		}

		for (AbstractBlockLogicExtension logicBlock : logicBlocks) {
			if (logicBlock.isValid())
				logicBlock.update(dt);
		}
		//check if block at position corespodends to saved, garbage collection
		logicBlocks.removeIf((AbstractBlockLogicExtension lb) -> {
			boolean remove = !lb.isValid();
			if (remove) {
				lb.dispose();
			}
			return remove;
		});
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
     * @param generator
     */
    public void fill(final Generator generator){
		int left = blocksX*coordX;
		int top = blocksY*coordY;
        for (int x = 0; x < blocksX; x++)
            for (int y = 0; y < blocksY; y++)
                for (int z = 0; z < blocksZ; z++){
                    data[x][y][z] = generator.generate(
						left+x,
						top+y,
						z
					);
					if (data[x][y][z] != null) {
						AbstractBlockLogicExtension logic = data[x][y][z].createLogicInstance(
							new Coordinate(coordX*blocksX+x, coordY*blocksY+y, z)
						);
						if (logic != null)
							logicBlocks.add(logic);
					}
					
					generator.spawnEntities(
						left+x,
						top+y,
						z
					);
				}
		modified = true;
    }

	/**
	 * copies  something
	 * @param path
	 * @param saveSlot
	 * @param coordX
	 * @param coordY
	 * @return
	 */
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
	 * @param fis
	 * @return -1 if eof, if sucessuf read then {@link #SIGN_ENDBLOCKS}
	 * @throws IOException
	 */
	private byte loadBlocks(FileInputStream fis) throws IOException{
		int z = 0;
		int x = 0;
		int y = 0;
		byte id = -1;//undefined

		byte bChar;
		boolean command = false;
		//read a byte for the blocks
		do {
			bChar = (byte) fis.read();//read while not eof
			if (bChar == -1) return bChar;
			boolean skip = false;

			if (bChar == SIGN_COMMAND) {
				skip = true;
				command = true;
			} else {
				if (command) {
					if (bChar == SIGN_EMTPYLAYER) {
						for (x = 0; x < blocksX; x++) {
							for (y = 0; y < blocksY; y++) {
								data[x][y][z] = null;
							}
						}
						skip = true;
					}

					if (bChar == SIGN_ENDBLOCKS || bChar==-1)
						return bChar;

					command = false;
				}
			}


			if (bChar != SIGN_COMMAND && skip == false) {
				try {
					//fill layer block by block
					if (id == -1) {

						id = bChar;
						if (id == 0) {
							data[x][y][z] = null;
							id = -1;
							x++;
							if (x == blocksX) {
								y++;
								x=0;
							}
							if (y == blocksY) {
								x=0;
								y=0;
								z++;
							}
						}
					} else {
						data[x][y][z] = Block.getInstance(id, bChar);
						//if has logicblock then add logicblock
						if (data[x][y][z] != null) {
							AbstractBlockLogicExtension logic = data[x][y][z].createLogicInstance(
								new Coordinate(coordX*blocksX+x, coordY*blocksY+y, z)
							);
							if (logic != null)
								logicBlocks.add(logic);
						}
						id = -1;
						x++;
						if (x == blocksX) {
							y++;
							x=0;
						}
						if (y == blocksY) {
							x=0;
							y=0;
							z++;
						}
					}
				} catch (ArrayIndexOutOfBoundsException ex){
					Gdx.app.error("Chunk", "too much blocks loaded:"+x+","+y+","+z+". Map file corrrupt?");
				}
			}
		} while (bChar != -1);
		return bChar;
	}


	private void loadEntities(FileInputStream fis, File path){
		//ends with a sign for logic or entities or eof
		try (ObjectInputStream ois = new ObjectInputStream(fis)) {
			byte bChar = ois.readByte();
			if (bChar == SIGN_COMMAND)
				bChar = ois.readByte();

			if (bChar==SIGN_ENTITIES){
				if (WE.getCvars().getValueB("loadEntities")) {
					try {
						//loading entities
						byte length = ois.readByte(); //amount of entities
						Gdx.app.debug("Chunk", "Loading " + length +" entities.");

						AbstractEntity object;
						for (int i = 0; i < length; i++) {
							try {
								object = (AbstractEntity) ois.readObject();
								Controller.getMap().addEntities(object);
								Gdx.app.debug("Chunk", "Loaded entity: "+object.getName());
								//objectIn.close();
							} catch (ClassNotFoundException | InvalidClassException ex) {
								Gdx.app.error("Chunk", "An entity could not be loaded: "+ex.getMessage());
							}
						}
					} catch (IOException ex) {
						Gdx.app.error("Chunk","Loading of entities in chunk" +path+"/"+coordX+","+coordY + " failed: "+ex);
					} catch (java.lang.NoClassDefFoundError ex) {
						Gdx.app.error("Chunk","Loading of entities in chunk " +path+"/"+coordX+","+coordY + " failed. Map file corrupt: "+ex);
					}
				}
			}
		} catch (IOException ex) {
			Gdx.app.error("Chunk","Loading of chunk" +path+"/"+coordX+","+coordY + " failed: "+ex);
		} catch (StringIndexOutOfBoundsException | NumberFormatException ex) {
			Gdx.app.error("Chunk","Loading of chunk " +path+"/"+coordX+","+coordY + " failed. Map file corrupt: "+ex);
		} catch (ArrayIndexOutOfBoundsException ex){
			Gdx.app.error("Chunk","Loading of chunk " +path+"/"+coordX+","+coordY + " failed. Chunk or meta file corrupt: "+ex);
		}
	}

    /**
     * Tries to load a chunk from disk.
     */
    private boolean load(final File path, int saveSlot, int coordX, int coordY) {

		//FileHandle path = Gdx.files.internal("/map/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX);
		FileHandle savepath = Gdx.files.absolute(path+"/save"+saveSlot+"/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX);

		if (savepath.exists()) {
			Gdx.app.debug("Chunk","Loading Chunk: "+ coordX + ", "+ coordY);
			//Reading map files test
			try {
				FileInputStream fis = new FileInputStream(savepath.file());

				byte bChar = loadBlocks(fis);
				if (bChar == SIGN_ENDBLOCKS)
					System.out.println("loaded block sucessfull");

				if (fis.available() > 0) {//not eof
					loadEntities(fis, path);
				}

				modified = true;
				return true;

			} catch (IOException ex){
				Gdx.app.error("Chunk","Loading of chunk " +path+"/"+coordX+","+coordY + " failed. Chunk or meta file corrupt: "+ex);
			}
		} else {
			Gdx.app.log("Chunk",savepath+" could not be found on disk. Trying to restore chunk.");
			if (restoreFromRoot(path, saveSlot, coordX, coordY))
				load(path, saveSlot, coordX, coordY);
		}

        return false;
    }


    /**
     * Save this chunk on storage.
     * @param path the map name on storage
	 * @param saveSlot

     * @return
     * @throws java.io.IOException
     */
    public boolean save(File path, int saveSlot) throws IOException {
        if (path == null) return false;
        Gdx.app.log("Chunk","Saving "+coordX + ","+ coordY +".");
		File savepath = new File(path + "/save" + saveSlot + "/chunk" + coordX + "," + coordY + "." + CHUNKFILESUFFIX);

        savepath.createNewFile();

		FileOutputStream fos = new FileOutputStream(savepath);
		for (byte z = 0; z < blocksZ; z++) {
			//check if layer is empty
			boolean dirty = false;
			for (int x = 0; x < blocksX; x++) {
				for (int y = 0; y < blocksY; y++) {
					if (data[x][y][z] != null) {
						dirty = true;
					}
				}
			}
			if (dirty) {
				for (int y = 0; y < blocksY; y++) {
					for (int x = 0; x < blocksX; x++) {
						if (data[x][y][z] == null) {
							fos.write(0);
						} else {
							fos.write(new byte[]{data[x][y][z].getId(), data[x][y][z].getValue()});
						}
					}
				}
			} else {
				fos.write(new byte[]{SIGN_COMMAND, SIGN_EMTPYLAYER});
			}
		}
		fos.write(new byte[]{SIGN_COMMAND, SIGN_ENDBLOCKS});
		fos.flush();

		ArrayList<AbstractEntity> entities = map.getEntitysOnChunkWhichShouldBeSaved(coordX, coordY);

		if (entities.size() > 0){
			try (ObjectOutputStream fileOut = new ObjectOutputStream(fos)) {
				//save entities
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
				fileOut.close();
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
		if (z >= Chunk.blocksZ) return null;
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
		int xIndex = block.getPosition().getX()-topleft.getX();
		int yIndex = block.getPosition().getY()-topleft.getY();
		int z = block.getPosition().getZ();
		if (z >= 0){
			data[xIndex][yIndex][z] = block.toStorageBlock();
			modified = true;
		}
		
		//get corresponding logic and update
		if (block.getBlockData() != null) {
			AbstractBlockLogicExtension logic = block.getBlockData().createLogicInstance(block.getPosition());
			if (logic != null)
				logicBlocks.add(logic);
		}
	}

	/**
	 * Almost lowest level method to set a block in the map. If the block has logic a new logicinstance will be created.
	 * @param coord The position where you insert the block. Must be inside the bounds of the chunk.
	 * @param block
	 */
	public void setBlock(Coordinate coord, Block block) {
		int xIndex = coord.getX()-topleft.getX();
		int yIndex = coord.getY()-topleft.getY();
		int z = coord.getZ();
		if (z >= 0){
			data[xIndex][yIndex][z] = block;
			modified = true;
		}
		
		//get corresponding logic and update
		if (block != null) {
			//create new instance
			AbstractBlockLogicExtension logic = block.createLogicInstance(coord);
			if (logic != null)
				logicBlocks.add(logic);
		}
	}
	
	public void setValue(Coordinate coord, byte value) {
		int xIndex = coord.getX()-topleft.getX();
		int yIndex = coord.getY()-topleft.getY();
		int z = coord.getZ();
		if (z >= 0){
			if (data[xIndex][yIndex][z].getValue() != value) {
				data[xIndex][yIndex][z].setValue(value);
				modified = true;
			}
		}
	}

	protected void addLogic(AbstractBlockLogicExtension block) {
		logicBlocks.add(block);
	}

	/**
	 * Get the logic to a logicblock.
	 * @param coord
	 * @return can return null
	 */
	public AbstractBlockLogicExtension getLogic(Coordinate coord) {
		Block block = coord.getBlock();
		if (block != null) {
			//find the logicBlock
			for (AbstractBlockLogicExtension logicBlock : logicBlocks) {
				if (logicBlock.getPosition().equals(coord) && logicBlock.isValid()) {
					return logicBlock;
				}
			}
		}
		return null;
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