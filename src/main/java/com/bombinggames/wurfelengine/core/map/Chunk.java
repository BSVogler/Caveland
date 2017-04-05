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
package com.bombinggames.wurfelengine.core.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.files.FileHandle;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import com.bombinggames.wurfelengine.core.map.rendering.RenderChunk;
import com.bombinggames.wurfelengine.core.map.rendering.RenderStorage;
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
public class Chunk implements Telegraph {
    /**The suffix of a chunk files.*/
    protected static final String CHUNKFILESUFFIX = "wec";

	/**
	 * dimensions of the chunk in X
	*/
	private static int blocksX = 10;
	/**
	 * dimensions of the chunk in Y
	*/
    private static int blocksY = 40;//blocksY must be even number
	/**
	 * dimensions of the chunk in Z
	*/
    private static int blocksZ = 10;

	/**
	 * special signs for the save file
	 */
	private final static char SIGN_ENTITIES = '|';//124 OR 0x7c
	private final static char SIGN_COMMAND = '~';//126 OR 0x7e
	private final static char SIGN_EMTPYLAYER = 'e';//only valid after a command sign
	private final static char SIGN_ENDBLOCKS = 'b';//only valid after a command sign

	/**
	 * The amount of blocks in X direction
	 *
	 * @return
	 */
	public static int getBlocksX() {
		return blocksX;
	}

	/**
	 * The amount of blocks in Y direction
	 *
	 * @return
	 */
	public static int getBlocksY() {
		return blocksY;
	}

	/**
	 * The amount of blocks in Z direction
	 *
	 * @return
	 */
	public static int getBlocksZ() {
		return blocksZ;
	}
	
	 /**
     *Not scaled.
     * @return
     */
    public static int getViewWidth(){
        return blocksX*RenderCell.VIEW_WIDTH;
    }

    /**
     *Not scaled.
     * @return
     */
    public static int getViewDepth() {
        return blocksY*RenderCell.VIEW_DEPTH2;// Divided by 2 because of shifted each second row.
    }

    /**
     *x axis
     * @return
     */
    public static int getGameWidth(){
        return blocksX*RenderCell.GAME_DIAGLENGTH;
    }

    /**
     *y axis
     * @return
     */
    public static int getGameDepth() {
        return blocksY*RenderCell.GAME_DIAGLENGTH2;
    }

        /**
     * The height of the map. z axis
     * @return in game size
     */
    public static int getGameHeight(){
        return blocksZ*RenderCell.GAME_EDGELENGTH;
    }

	/**
	 * chunk coordinate
	 */
	private final int chunkX, chunkY;
	
	/**
	 * the ids are stored here. A block is defined by three fields. In the last dimension first is id, second value, third is health.
	 */
    private final byte data[][][];
	
	/**
	 * A list containing the logic blocks to be updated. Each logic block object points to some block inside this chunk.
	 */
	private final ArrayList<AbstractBlockLogicExtension> logicBlocks = new ArrayList<>(4);
	private boolean modified;

	/**
	 * contains the entities on this chunk
	 */
	private ArrayList<AbstractEntity> entitiesinSaveFile;
	private int topleftX;
	private int topleftY;

    /**
     * Creates a Chunk filled with empty cells (likely air).
	 * @param map
	 * @param coordX
	 * @param coordY
     */
    public Chunk(final Map map, final int coordX, final int coordY) {
        this.chunkX = coordX;
		this.chunkY = coordY;

		//set chunk dimensions
		blocksX = map.getCVars().getValueI("chunkBlocksX");
		blocksY = map.getCVars().getValueI("chunkBlocksY");
		blocksZ = map.getCVars().getValueI("chunkBlocksZ");

		topleftX = coordX*blocksX;
		topleftY = coordY*blocksY;
		data = new byte[blocksX][blocksY][blocksZ*3];

       for (int x = 0; x < blocksX; x++) {
			for (int y = 0; y < blocksY; y++) {
				for (int z = 0; z < blocksZ*3; z++) {
					if (z % 3 == 2) {//every third is health
						data[x][y][z] = 100;
					} else {
						data[x][y][z] = 0;
					}
				}
			}
		}
		
		modified = true;
    }

	/**
	 * Creates a chunk by trying to load and if this fails it generates a new
	 * one.
	 *
	 * @param map
	 * @param coordX the chunk coordinate
	 * @param coordY the chunk coordinate
	 * @param path filename, can be null to skip file loading
	 * @param generator used for generating if laoding fails
	 */
	public Chunk(final Map map, final File path, final int coordX, final int coordY, final Generator generator) {
		this(map, coordX, coordY);
		if (path != null && WE.getCVars().getValueB("shouldLoadMap")) {
			if (!load(path, map.getCurrentSaveSlot(), coordX, coordY)) {
				fill(generator);
			}
		} else {
			fill(generator);
		}
	}

	/**
	 * Updates the chunk. should be called once per frame.
	 *
	 * @param dt time since last frame in game time
	 */
	public void update(float dt) {
		processModification();

		//update logicblocks
		for (AbstractBlockLogicExtension logicBlock : logicBlocks) {
			if (logicBlock.isValid()) {
				logicBlock.update(dt);
			}
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
	 * checks if the chunk got modified and if that is the case calls the
	 * modification methods
	 */
	public void processModification() {
		if (modified) {
			modified = false;

			Controller.getMap().setModified();
			//notify observers that a chunk changed
			MessageManager.getInstance().dispatchMessage(this, Events.chunkChanged.getId(), this);
		}
	}

   /**
	 * Fills the chunk's block using a generator.
	 *
	 * @param generator
	 */
	public void fill(final Generator generator) {
		int left = blocksX * chunkX;
		int top = blocksY * chunkY;
		for (int x = 0; x < blocksX; x++) {
			for (int y = 0; y < blocksY; y++) {
				for (int z = 0; z < blocksZ * 3; z += 3) {
					int generated = generator.generate(
						left + x,
						top + y,
						z
					);
					data[x][y][z] = (byte) (generated&255);
					data[x][y][z + 1] = (byte) ((generated>>8)&255);
					data[x][y][z + 2] = 100;//health
					if (data[x][y][z] != 0) {
						AbstractBlockLogicExtension logic = RenderCell.createLogicInstance(data[x][y][z],
							data[x][y][z + 1],
							new Coordinate(left + x, top + y, z)
						);
						if (logic != null) {
							logicBlocks.add(logic);
						}
					}

					generator.spawnEntities(
						left + x,
						top + y,
						z
					);
				}
			}
		}
		modified = true;
	}

	/**
	 * copies something
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
		byte[][][] data = this.data;
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
				command = true;
			} else {
				if (command) {
					command = false;
					if (bChar == SIGN_EMTPYLAYER) {
						for (x = 0; x < blocksX; x++) {
							for (y = 0; y < blocksY; y++) {
								data[x][y][z * 3] = 0;//id
								data[x][y][z * 3 + 1] = 0;//value
								data[x][y][z * 3 + 2] = 100;//health
							}
						}
						skip = true;
					}

					if (bChar == SIGN_ENDBLOCKS || bChar == -1) {
						return bChar;
					}
				}

				if (!skip) {
					try {
						//fill layer block by block
						if (id == -1) {
							id = bChar;
							
							if (id == 0) {
								data[x][y][z * 3] = id;
								data[x][y][z * 3 + 1] = 0;//value
								data[x][y][z * 3 + 2] = 100;//health
								id = -1;
								x++;
								if (x == blocksX) {
									y++;
									x = 0;
								}
								if (y == blocksY) {
									x = 0;
									y = 0;
									z++;
								}
							}
						} else {
							data[x][y][z * 3] = id;
							data[x][y][z * 3 + 1] = bChar;
							data[x][y][z * 3 + 2] = 100;//health
							//if has logicblock then add logicblock
							if (id != 0) {
								if (RenderCell.hasLogic(id, bChar)) {
									AbstractBlockLogicExtension logic = RenderCell.createLogicInstance(
										id,
										bChar,
										new Coordinate(
											chunkX * blocksX + x,
											chunkY * blocksY + y,
											z
										)
									);
									logicBlocks.add(logic);
								}
							}
							x++;
							if (x == blocksX) {
								y++;
								x = 0;
							}
							if (y == blocksY) {
								x = 0;
								y = 0;
								z++;
							}
							id = -1;
						}
					} catch (ArrayIndexOutOfBoundsException ex) {
						Gdx.app.error("Chunk", "too much blocks loaded:" + x + "," + y + "," + z + ". Map file corrrupt?");
					}
				}
			}
		} while (bChar != -1);
		return bChar;
	}

	/**
	 * fills entitie cache
	 *
	 * @param fis
	 * @param path
	 */
	private void loadEntities(FileInputStream fis, File path) {
		//ends with a sign for logic or entitiesinSaveFile or eof
		try (ObjectInputStream ois = new ObjectInputStream(fis)) {
			byte bChar = ois.readByte();
			if (bChar == SIGN_COMMAND) {
				bChar = ois.readByte();
			}

			if (bChar == SIGN_ENTITIES && WE.getCVars().getValueB("loadEntities")) {
				try {
					//loading entitiesinSaveFile
					byte entCount = ois.readByte(); //amount of entities
					if (entCount > 0 && entCount < 10000) {//upper limit
						Gdx.app.debug("Chunk", "Loading " + entCount + " entities.");
						entitiesinSaveFile = new ArrayList<>(entCount);

						AbstractEntity ent;
						for (int i = 0; i < entCount; i++) {
							try {
								ent = (AbstractEntity) ois.readObject();
								entitiesinSaveFile.add(ent);
								Gdx.app.debug("Chunk", "Loaded entity: " + ent.getName());
							} catch (ClassNotFoundException | InvalidClassException ex) {
								Gdx.app.error("Chunk", "An entity could not be loaded: " + ex.getMessage());
							}
						}
					} else if (entCount < 0) {
						Gdx.app.error("Chunk", "Loading of entities in chunk" + path + "/" + chunkX + "," + chunkY + " failed. File is corrupt.");
					}
					ois.close();
				} catch (IOException ex) {
					Gdx.app.error("Chunk", "Loading of entities in chunk" + path + "/" + chunkX + "," + chunkY + " failed: " + ex);
				} catch (java.lang.NoClassDefFoundError ex) {
					Gdx.app.error("Chunk", "Loading of entities in chunk " + path + "/" + chunkX + "," + chunkY + " failed. Map file corrupt: " + ex);
				}
			}
		} catch (IOException ex) {
			Gdx.app.error("Chunk", "Loading of chunk" + path + "/" + chunkX + "," + chunkY + " failed: " + ex);
		} catch (StringIndexOutOfBoundsException | NumberFormatException ex) {
			Gdx.app.error("Chunk", "Loading of chunk " + path + "/" + chunkX + "," + chunkY + " failed. Map file corrupt: " + ex);
		} catch (ArrayIndexOutOfBoundsException ex) {
			Gdx.app.error("Chunk", "Loading of chunk " + path + "/" + chunkX + "," + chunkY + " failed. Chunk or meta file corrupt: " + ex);
		}
	}

    /**
     * Tries to load a chunk from disk.
     */
    private boolean load(final File path, int saveSlot, int coordX, int coordY) {

		//FileHandle path = Gdx.files.internal("/map/chunk"+coordX+","+chunkY+"."+CHUNKFILESUFFIX);
		FileHandle savepath = Gdx.files.absolute(path+"/save"+saveSlot+"/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX);

		if (savepath.exists()) {
			Gdx.app.debug("Chunk","Loading Chunk: "+ coordX + ", "+ coordY);
			//Reading map files test
			try {
				FileInputStream fis = new FileInputStream(savepath.file());

				byte bChar = loadBlocks(fis);
				//if (bChar == SIGN_ENDBLOCKS)
					//Gdx.app.debug("Chunk","Loaded blocks sucessfull");

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
	 * Returns entitiesinSaveFile spawned on this chunk. Can only called once.
	 * @return list of entitiesinSaveFile on this chunk, can be null if empty
	 */
	public ArrayList<AbstractEntity> retrieveEntities() {
		ArrayList<AbstractEntity> tmp = entitiesinSaveFile;
		entitiesinSaveFile = null;//clear this reference to help gc
		return tmp;
	}


    /**
     * Save this chunk on storage.
	 * @param map the map of which this chunk is a part of
     * @param path the map name on storage
	 * @param saveSlot

     * @return
     * @throws java.io.IOException
     */
    public boolean save(Map map, File path, int saveSlot) throws IOException {
        if (path == null) return false;
        Gdx.app.log("Chunk","Saving "+chunkX + ","+ chunkY +".");
		File savepath = new File(path + "/save" + saveSlot + "/chunk" + chunkX + "," + chunkY + "." + CHUNKFILESUFFIX);

        savepath.createNewFile();

		FileOutputStream fos = new FileOutputStream(savepath);
		for (byte z = 0; z < blocksZ; z++) {
			//check if layer is empty
			boolean dirty = false;
			for (int x = 0; x < blocksX; x++) {
				for (int y = 0; y < blocksY; y++) {
					if (data[x][y][z*3] != 0) {
						dirty = true;
					}
				}
			}
			if (dirty) {
				for (int y = 0; y < blocksY; y++) {
					for (int x = 0; x < blocksX; x++) {
						if (data[x][y][z * 3] == 0) {
							fos.write(0);//value would be redundand
						} else {
							fos.write(new byte[]{data[x][y][z * 3], data[x][y][z * 3 + 1]});
						}
					}
				}
			} else {
				fos.write(new byte[]{SIGN_COMMAND, SIGN_EMTPYLAYER});
			}
		}
		fos.write(new byte[]{SIGN_COMMAND, SIGN_ENDBLOCKS});
		fos.flush();

		ArrayList<AbstractEntity> entities = map.getEntitiesOnChunkSavedOnly(chunkX, chunkY);

		if (entities.size() > 0){
			try (ObjectOutputStream fileOut = new ObjectOutputStream(fos)) {
				//save entitiesinSaveFile
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
     * Returns the data of the chunk
     * @return
     */
    public byte[][][] getData() {
        return data;
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
		int left = topleftX;
		int top = topleftY;
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
		float top = topleftY;
		float left = topleftX * RenderCell.GAME_DIAGLENGTH + (top % 2 != 0 ? RenderCell.VIEW_WIDTH2 : 0);
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
		for (int z = 0; z < blocksZ*3; z+=3) {
			for (int y = 0; y < blocksY; y++) {
				for (int x = 0; x < blocksX; x++) {
					if (data[x][y][z]==0)
						strg += "  ";
					else
						strg += data[x][y][z] + " ";
				}
				strg += "\n";
			}
			strg += "\n\n";
		}
		return strg;
	}

	/**
	 * Get the chunk coordinate of this chunk.
	 * @return
	 */
	public int getChunkX() {
		return chunkX;
	}

	/**
	 * Get the chunk coordinate of this chunk.
	 * @return
	 */
	public int getChunkY() {
		return chunkY;
	}

	/**
	 *
	 * @return not copy safe
	 */
	public int getTopLeftCoordinateX(){
		return topleftX;
	}
	
	/**
	 *
	 * @return not copy safe
	 */
	public int getTopLeftCoordinateY(){
		return topleftY;
	}

	/**
	 * sets a block in the map. if position is under the map does nothing.
	 * @param rblock no null pointer allowed
	 */
	public void setBlock(RenderCell rblock) {
		int xIndex = rblock.getPosition().getX()-topleftX;
		int yIndex = rblock.getPosition().getY()-topleftY;
		int z = rblock.getPosition().getZ()*3;
		if (z >= 0){
			data[xIndex][yIndex][z] = rblock.getId();
			data[xIndex][yIndex][z+1] = rblock.getValue();
			data[xIndex][yIndex][z+2] = rblock.getHealth();
			modified = true;
		}
		
		//get corresponding logic and update
		if (rblock.getId() != 0) {
			AbstractBlockLogicExtension logic = RenderCell.createLogicInstance(rblock.getId(), rblock.getValue(), rblock.getPosition());
			if (logic != null)
				logicBlocks.add(logic);
		}
	}

	/**
	 * Almost lowest level method to set a block in the map. If the block has
	 * logic a new logicinstance will be created.
	 *
	 * @param coord The position where you insert the block. Must be inside the
	 * bounds of the chunk.
	 * @param id
	 * @param value
	 * @param health
	 */
	public void setBlock(Coordinate coord, byte id, byte value, byte health) {
		int xIndex = coord.getX() - topleftX;
		int yIndex = coord.getY() - topleftY;
		int z = coord.getZ()*3;
		if (z >= 0){
			data[xIndex][yIndex][z] = id;
			data[xIndex][yIndex][z+1] = value;
			data[xIndex][yIndex][z+2] = health;
			modified = true;
		}
		
		//get corresponding logic and update
		if (id != 0) {
			AbstractBlockLogicExtension logic = RenderCell.createLogicInstance(id, value, coord);
			if (logic != null)
				logicBlocks.add(logic);
		}
	}
	
	/**
	 * Almost lowest level method to set a block in the map. If the block has
	 * logic a new logicinstance will be created.
	 * Sets health to 100.
	 * @param coord
	 * @param id
	 * @param value 
	 */
	public void setBlock(Coordinate coord, byte id, byte value) {
		int xIndex = coord.getX() - topleftX;
		int yIndex = coord.getY() - topleftY;
		int z = coord.getZ()*3;
		if (z >= 0){
			data[xIndex][yIndex][z] = id;
			data[xIndex][yIndex][z+1] = value;
			data[xIndex][yIndex][z+2] = 100;
			modified = true;
		}
		
		//get corresponding logic and update
		if (id != 0) {
			AbstractBlockLogicExtension logic = RenderCell.createLogicInstance(id, value, coord);
			if (logic != null)
				logicBlocks.add(logic);
		}
	}
	
	/**
	 * Almost lowest level method to set a block in the map. If the block has
	 * logic a new logicinstance will be created.
	 * Health set to 100 and value set to 0
	 * @param coord
	 * @param id 
	 */
		public void setBlock(Coordinate coord, byte id) {
		int xIndex = coord.getX() - topleftX;
		int yIndex = coord.getY() - topleftY;
		int z = coord.getZ()*3;
		if (z >= 0){
			data[xIndex][yIndex][z] = id;
			data[xIndex][yIndex][z+1] = 0;
			data[xIndex][yIndex][z+2] = 100;
			modified = true;
		}
		
		//get corresponding logic and update
		if (id != 0) {
			AbstractBlockLogicExtension logic = RenderCell.createLogicInstance(id, (byte) 0, coord);
			if (logic != null)
				logicBlocks.add(logic);
		}
	}
	
	/**
	 *
	 * @param coord
	 * @param value
	 */
	public void setValue(Coordinate coord, byte value) {
		int xIndex = coord.getX() - topleftX;
		int yIndex = coord.getY() - topleftY;
		int z = coord.getZ()*3;
		if (z >= 0) {
			//check if actually changed
			if (data[xIndex][yIndex][z+1] != value) {
				data[xIndex][yIndex][z+1] = value;
				modified = true;
			}
		}
	}
	
	/**
	 * Set health of a cell.
	 * @param coord
	 * @param health 0-100.
	 */
	public void setHealth(Coordinate coord, byte health) {
		MessageManager.getInstance().dispatchMessage(Events.blockDamaged.getId(), coord);
		int xIndex = coord.getX() - topleftX;
		int yIndex = coord.getY() - topleftY;
		int z = coord.getZ()*3;
		if (z >= 0) {
			if (data[xIndex][yIndex][z+2] != health) {
				data[xIndex][yIndex][z+2] = health;
				modified = true;
			}
		}
	}

	/**
	 *
	 * @param block
	 */
	protected void addLogic(AbstractBlockLogicExtension block) {
		logicBlocks.add(block);
	}

	/**
	 * Get the logic to a logicblock.
	 *
	 * @param coord
	 * @return can return null
	 */
	public AbstractBlockLogicExtension getLogic(Coordinate coord) {
		if (coord.getBlockId() != 0) {
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
	 * disposes the chunk
	 *
	 * @param map
	 * @param path if null, does not save the file
	 */
	public void dispose(Map map, File path) {
		//try saving
		if (path != null) {
			try {
				save(map, path, map.getCurrentSaveSlot());
			} catch (IOException ex) {
				Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		//remove entitiesinSaveFile on this chunk from map
		ArrayList<AbstractEntity> entities = map.getEntitiesOnChunk(chunkX, chunkY);
		for (AbstractEntity ent : entities) {
			ent.removeFromMap();
		}
	}

	/**
	 *
	 * @param storage
	 * @return
	 */
	public RenderChunk getRenderChunk(RenderStorage storage) {
		return storage.getChunk(chunkX, chunkY);
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}

	/*
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return can be null
	 */

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */

	public byte getBlockId(int x, int y, int z) {
		if (z >= Chunk.blocksZ) {
			return 0;
		}
		int xIndex = x - topleftX;
		int yIndex = y - topleftY;
		return data[xIndex][yIndex][z * 3];
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public byte getBlockValue(int x, int y, int z) {
		if (z >= Chunk.blocksZ) {
			return 0;
		}
		int xIndex = x - topleftX;
		int yIndex = y - topleftY;
		return data[xIndex][yIndex][z * 3 + 1];
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public byte getHealth(int x, int y, int z) {
		if (z >= Chunk.blocksZ) {
			return 0;
		}
		int xIndex = x - topleftX;
		int yIndex = y - topleftY;
		return data[xIndex][yIndex][z * 3 + 2];
	}

	/**
	 * Get the block data at this coordinate.
	 *
	 * @param x global coordinates
	 * @param y global coordinates
	 * @param z global coordinates
	 * @return first byte id, second value, third is health.
	 */
	public int getBlock(int x, int y, int z) {
		if (z >= Chunk.blocksZ) {
			return 0;
		}
		int xIndex = x - topleftX;
		int yIndex = y - topleftY;
		return data[xIndex][yIndex][z * 3] + (data[xIndex][yIndex][z * 3 + 1] << 8) + (data[xIndex][yIndex][z * 3 + 2] << 16);
	}

	/**
	 * Get the block data at this index position.
	 * @param x only valid index
	 * @param y only valid index
	 * @param z only valid index
	 * @return first byte id, second value, third is health.
	 */
	public int getBlockByIndex(int x, int y, int z) {
		if (z >= Chunk.blocksZ) {
			return 0;
		}
		return data[x][y][z * 3] + (data[x][y][z * 3 + 1] << 8) + (data[x][y][z * 3 + 2] << 16);
	}
}