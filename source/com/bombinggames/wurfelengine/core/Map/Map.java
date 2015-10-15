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
import com.bombinggames.wurfelengine.core.CVar.CVarSystem;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Map.Generators.AirGenerator;
import com.bombinggames.wurfelengine.core.Map.Iterators.DataIterator;
import com.bombinggames.wurfelengine.core.Map.Iterators.MemoryMapIterator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A map stores nine chunks as part of a bigger map. It also contains the
 * entities.
 *
 * @author Benedikt Vogler
 */
public class Map implements Cloneable {

	private static int blocksX;
	private static int blocksY;
	private static int blocksZ;

	private static Generator defaultGenerator = new AirGenerator();
	public final static Integer MAPVERSION = 4;

	/**
	 *
	 * @param generator
	 */
	public static void setDefaultGenerator(Generator generator) {
		defaultGenerator = generator;
	}

	/**
	 * Get the default set generator.
	 *
	 * @return
	 * @see #setDefaultGenerator(Generator)
	 */
	public static Generator getDefaultGenerator() {
		return defaultGenerator;
	}

	/**
	 *
	 * @param path the directory of the map
	 * @return
	 */
	public static int newSaveSlot(File path) {
		int slot = getSavesCount(path);
		createSaveSlot(path, slot);
		return slot;
	}

	/**
	 *
	 * @param path the directory of the map
	 * @param slot
	 */
	public static void createSaveSlot(File path, int slot) {
		FileHandle pathHandle = Gdx.files.absolute(path + "/save" + slot + "/");
		if (!pathHandle.exists()) {
			pathHandle.mkdirs();
		}
		//copy from map folder root
		FileHandle root = Gdx.files.absolute(path.getAbsolutePath());
		FileHandle[] childen = root.list();
		for (FileHandle file : childen) {
			if (!file.isDirectory()) {
				file.copyTo(pathHandle);
			}
		}
	}

	/**
	 * Get the amount of save files for this map.
	 * @param path
	 * @return 
	 */
	public static int getSavesCount(File path) {
		FileHandle children = Gdx.files.absolute(path.getAbsolutePath());
		int i = 0;
		while (children.child("save" + i).exists()) {
			i++;
		}
		return i;
	}

	/**
	 * Copies an array with three dimensions. Deep copy until the cells content
	 * of cells shallow copy.
	 *
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
	 * every entity on the map is stored in this field
	 */
	private final ArrayList<AbstractEntity> entityList = new ArrayList<>(20);
	private boolean modified = true;
	/**
	 * observer pattern
	 */
	private final ArrayList<MapObserver> observers = new ArrayList<>(2);//camera + light engine=2 minimum
	private final Block groundBlock = Block.getInstance((byte) WE.CVARS.getValueI("groundBlockID")); //the representative of the bottom layer (ground) block
	private Generator generator;
	private final File directory;
	private int activeSaveSlot;


    /** Stores the data of the map. */
    private ArrayList<Chunk> data;

    /**
     * Loads a map using the default generator.
     * @param name if available on disk it will be load
	 * @param saveslot
     * @throws java.io.IOException
     */
    public Map(final File name, int saveslot) throws IOException{
        this(name, getDefaultGenerator(), saveslot);
    }

    /**
     * Loads a map.
     * @param name if available on disk it will load the meta file
     * @param generator the generator used for generating new chunks
	 * @param saveSlot
     * @throws java.io.IOException thrown if there is no full read/write access to the map file
     */
    public Map(final File name, Generator generator, int saveSlot) throws IOException {
		this.directory = name;
		this.generator = generator;
		CVarSystem cvars = CVarSystem.getInstanceMapSystem(new File(directory + "/meta.wecvar"));

		WE.CVARS.setChildSystem(cvars);

		cvars.load();

		if (!hasSaveSlot(saveSlot)) {
			createSaveSlot(saveSlot);
		}
		useSaveSlot(saveSlot);

        Gdx.app.debug("Map","Map named \""+ name +"\", saveslot "+ saveSlot +" should be loaded");

        //save chunk size, which are now loaded
        blocksX = Chunk.getBlocksX()*3;
        blocksY = Chunk.getBlocksY()*3;
        blocksZ =  Chunk.getBlocksZ();
        data = new ArrayList<>(9);

		//printCoords();
    }

	/**
	 *Updates mostly the entities.
	 * @param dt
	 */
	public void update(float dt) {
		dt *= WE.CVARS.getValueF("timespeed");//aplly game speed

	//update every block on the map
		for (Chunk chunk : data) {
			chunk.update(dt);
		}
		//update every entity
		//old style for loop because allows modification during loop
		float rawDelta = Gdx.graphics.getRawDeltaTime();
		for (int i = 0; i < entityList.size(); i++) {
			AbstractEntity entity = entityList.get(i);
			if (!entity.isInMemoryArea()) {
				entity.requestChunk();
			}
			if (entity.useRawDelta()) {
				entity.update(rawDelta);
			} else {
				entity.update(dt);
			}
		}

		//remove not spawned objects from list
		entityList.removeIf((AbstractEntity entity) -> !entity.isSpawned());
	}
	
	/**
	 * Called after the view update to catch changes caused by the view
	 * @param dt
	 */
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
		setModified();
	}

	/**
	 * performs a simple viewFrustum check by looking at the direct neighbours.
	 * @param camera the camera which is used for the limits. Gets stored globally so only one camera can be used. Calling this method more then once ith different cameras overwrites the result.
	 * @param chunkX chunk coordinate
	 * @param chunkY chunk coordinate
	 */
	public void hiddenSurfaceDetection(final Camera camera, final int chunkX, final int chunkY) {
		Gdx.app.debug("ChunkMap", "HSD for chunk " + chunkX + "," + chunkY);
		Chunk chunk = getChunk(chunkX, chunkY);
		Block[][][] chunkData = chunk.getData();

		chunk.resetClipping();

		//loop over floor for ground level
		//DataIterator floorIterator = chunk.getIterator(0, 0);
//		while (floorIterator.hasNext()) {
//			if (((Block) floorIterator.next()).hidingPastBlock())
//				chunk.getBlock(
//					floorIterator.getCurrentIndex()[0],
//					floorIterator.getCurrentIndex()[1],
//					chunkY)setClippedTop(
//					floorIterator.getCurrentIndex()[0],
//					floorIterator.getCurrentIndex()[1],
//					-1
//				);
//		}

		//iterate over chunk
		DataIterator<Block> dataIter = new DataIterator<>(
			chunkData,
			0,
			camera.getZRenderingLimit()-1
		);

		while (dataIter.hasNext()) {
			Block current = dataIter.next();//next is the current block

			if (current != null) {
				//calculate index position relative to camera border
				final int x = dataIter.getCurrentIndex()[0];
				final int y = dataIter.getCurrentIndex()[1];
				final int z = dataIter.getCurrentIndex()[2];

				Block neighbour;
				//left side
				//get neighbour block
				if (y % 2 == 0) {//next row is shifted right
					neighbour = getIndex(chunk, x-1, y+1, z);
				} else
					neighbour = getIndex(chunk, x, y+1, z);

				if (neighbour!= null
					&& (neighbour.hidingPastBlock() || (neighbour.isLiquid() && current.isLiquid()))
				) {
					current.setClippedLeft();
				}

				//right side
				//get neighbour block
				if (y % 2 == 0)//next row is shifted right
					neighbour = getIndex(chunk, x, y+1, z);
				else {
					neighbour = getIndex(chunk, x+1, y+1, z);
				}

				if (neighbour!= null
					&& (neighbour.hidingPastBlock() || (neighbour.isLiquid() && current.isLiquid()))
				) {
					current.setClippedRight();
				}

				//check top
				if (z < getBlocksZ()-1) {
					neighbour = getIndex(chunk, x, y+2, z+1);
					if (
						(
							chunkData[x][y][z+1] != null
							&& (
								chunkData[x][y][z+1].hidingPastBlock()
								|| chunkData[x][y][z+1].isLiquid() && current.isLiquid()
							)
						)
						||
						(
							neighbour != null
							&& (
								neighbour.hidingPastBlock()
								|| neighbour.isLiquid() && current.isLiquid()
							)
						)
					) {
						current.setClippedTop();
					}
				}
			}
		}
	}

	/**
	 * Helper function. Gets a block at an index. can be outside of this chunk
	 * @param chunk
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private Block getIndex(Chunk chunk, int x, int y, int z){
		if (x < 0 || y >= Chunk.getBlocksY() || x >= Chunk.getBlocksX())//index outside current chunk
			return getBlock(
				chunk.getTopLeftCoordinate().getX()+x,
				chunk.getTopLeftCoordinate().getY()+y,
				z
			);
		else
			return chunk.getBlockViaIndex(x, y, z);
	}

    /**
     * Get the data of the map
     * @return
     */
	public ArrayList<Chunk> getData() {
		return data;
	}

   	/**
	 * Returns a block without checking the parameters first. Good for debugging
	 * and also faster. O(n)
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return the single block you wanted
	 */
    public Block getBlock(final int x, final int y, final int z){
		return getBlock(new Coordinate(x, y, z));
    }

    /**
	 * If the block can not be found returns null pointer.
	 *
	 * @param coord
	 * @return
	 */
    public Block getBlock(final Coordinate coord){
		if (coord.getZ() < 0)
			return getGroundBlock();
		Chunk chunk = getChunk(coord);
		if (chunk==null)
			return null;
		else
			return chunk.getBlock(coord.getX(), coord.getY(), coord.getZ());//find chunk in x coord

    }

	/**
	 * Replace a block. Assume that the map already has been filled at this
	 * coordinate.
	 *
	 * @param block no null pointer
	 * @see
	 * #setBlock(com.bombinggames.wurfelengine.Core.Gameobjects.RenderBlock)
	 */
    public void setBlock(final RenderBlock block) {
		getChunk(block.getPosition()).setBlock(block);
    }

		/**
	 * Set a block at this coordinate. This creates a logic instance if the block if it has a logic.
	 *
	 * @param coord
	 * @param block
	 * @see
	 * #setBlock(com.bombinggames.wurfelengine.Core.Gameobjects.RenderBlock)
	 */
	public void setBlock(Coordinate coord, Block block) {
		getChunk(coord).setBlock(coord, block);
	}

	/**
	 * get the chunk where the coordinates are on
	 * @param coord
	 * @return can return null if not loaded
	 */
	public Chunk getChunk(Coordinate coord){
		//checks every chunk in memory
		for (Chunk chunk : data) {
			int left = chunk.getTopLeftCoordinate().getX();
			int top  = chunk.getTopLeftCoordinate().getY();
			//check if coordinates are inside the chunk
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
	 * get the chunk with the given chunk coords. <br>Runtime: O(c) where c =
	 * amount of chunks -&gt; O(1)
	 *
	 * @param chunkX
	 * @param chunkY
	 * @return if not in memory return null
	 */
	public Chunk getChunk(int chunkX, int chunkY) {
		for (Chunk chunk : data) {
			if (chunkX == chunk.getChunkX()
				&& chunkY == chunk.getChunkY()) {
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
				&& ent.isSpawned()
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
					ent.isGettingSaved() && ent.isSpawned() //save only entities which are flagged
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
	 *
	 * @param saveSlot
	 * @return
	 */
    public boolean save(int saveSlot) {
		for (Chunk chunk : data) {
			try {
                chunk.save(
                    getPath(),
					saveSlot
                );
            } catch (IOException ex) {
                Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
		}
        return true;
    }


	/**
	 * prints the map to console
	 */
	public void print() {
		MemoryMapIterator iter = getIterator(0, blocksZ-1);
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
     *
	 * @param save
     */
    public void dispose(boolean save){
		for (Chunk chunk : data) {
			if (save) {
				chunk.dispose(getPath());
			} else {
				chunk.dispose(null);
			}
		}
		disposeEntities();
    }

	/**
	 * Returns the amount of Blocks inside the map in x-direction.
	 * @return
	 */
	public int getBlocksX() {
		return blocksX;
	}

	/**
	 * Returns the amount of Blocks inside the map in y-direction.
	 * @return
	 */
	public int getBlocksY() {
		return blocksY;
	}

	/**
	 * Returns the amount of Blocks inside the map in z-direction.
	 * @return
	 */
	public int getBlocksZ() {
		return blocksZ;
	}

	public AbstractBlockLogicExtension getLogic(Coordinate coord) {
		Chunk chunk = getChunk(coord);
		if (chunk==null) return null;
		else return chunk.getLogic(coord);
	}

	/**
	 * Add a logicblock to the map.
	 * @param block 
	 */
	public void addLogic(AbstractBlockLogicExtension block) {
		Chunk chunk = getChunk(block.getPosition());
		chunk.addLogic(block);
	}

	/**
	 * uses a specific save slot for loading and saving the map
	 *
	 * @param slot
	 */
	public void useSaveSlot(int slot) {
		this.activeSaveSlot = slot;
		WE.CVARS.getChildSystem().setChildSystem(
			CVarSystem.getInstanceSaveSystem(
				new File(directory + "/save" + activeSaveSlot + "/meta.wecvar")
			)
		);
	}

	/**
	 * Uses a new save slot as the save slot
	 *
	 * @return the new save slot number
	 */
	public int newSaveSlot() {
		activeSaveSlot = getSavesCount();
		createSaveSlot(activeSaveSlot);
		WE.CVARS.getChildSystem().setChildSystem(
			CVarSystem.getInstanceSaveSystem(
				new File(directory + "/save" + activeSaveSlot + "/meta.wecvar")
			)
		);
		return activeSaveSlot;
	}

	/**
	 * Check if a save slot exists.
	 *
	 * @param saveSlot
	 * @return
	 */
	public boolean hasSaveSlot(int saveSlot) {
		FileHandle path = Gdx.files.absolute(directory + "/save" + saveSlot);
		return path.exists();
	}

	public void createSaveSlot(int slot) {
		createSaveSlot(directory, slot);
	}

	/**
	 * checks a map for the amount of save files
	 *
	 * @return the amount of saves for this map
	 */
	public int getSavesCount() {
		return getSavesCount(directory);
	}

		/**
	 * should be executed after the update method
	 */
	public void modificationCheck() {
		if (modified) {
			onModified();
			modified = false;
		}
	}

	public Generator getGenerator() {
		return generator;
	}

	public int getCurrentSaveSlot() {
		return activeSaveSlot;
	}

	/**
	 * Set the generator used for generating maps
	 *
	 * @param generator
	 */
	public void setGenerator(Generator generator) {
		this.generator = generator;
	}



	/**
	 * The name of the map on the file.
	 * @return
	 */
	public File getPath() {
		return directory;
	}


	/**
	 *
	 * @return
	 */
	public Block getGroundBlock() {
		return groundBlock;
	}

	/**
	 *
	 * @return reference to list containing the observers
	 */
	public ArrayList<MapObserver> getOberservers() {
		return observers;
	}

	/**
	 * Get an iteration which can loop throug the map
	 *
	 * @param startLimit the starting level
	 * @param topLimitZ the top limit of the iterations
	 * @return
	 */
	public MemoryMapIterator getIterator(int startLimit, int topLimitZ) {
		MemoryMapIterator mapIterator = new MemoryMapIterator(this, startLimit);
		mapIterator.setTopLimitZ(topLimitZ);
		return mapIterator;
	}

	/**
	 * called when the map is modified
	 */
	private void onModified() {
		//recalculates the light if requested
		Gdx.app.debug("Map", "onModified");
		for (MapObserver observer : observers) {
			observer.onMapChange();
		}
	}

	/**
	 * called when the map reloaded. Ideally should find this out by itself.
	 */
	public void onReload(){
		//recalculates the light if requested
		Gdx.app.debug("Map", "onReload");
		for (MapObserver observer : observers) {
			observer.onMapReload();
		}
	}

	/**
	 * set the modified flag to true. usually not manually called.
	 */
	public void setModified() {
		this.modified = true;
	}


	/**
	 * Returns a coordinate pointing to the absolute center of the map. Height
	 * is half the map's height.
	 *
	 * @return
	 */
	public Point getCenter() {
		return getCenter(getBlocksZ() * Block.GAME_EDGELENGTH / 2);
	}

	/**
	 * Returns a coordinate pointing to middle of a 3x3 chunk map.
	 *
	 * @param height You custom height.
	 * @return
	 */
	public Point getCenter(final float height) {
		return new Point(
			Chunk.getGameWidth() / 2,
			Chunk.getGameDepth() / 2,
			height
		);
	}

	/**
	 * Returns a copy of the entityList.
	 *
	 * @return every item on the map
	 */
	public AbstractEntity[] getEntitys() {
		return entityList.toArray(new AbstractEntity[]{});//it is a bit stupid to create a new array which will not be used but wihtout you get Object[] which can not be casted to Interactable[].
	}

	/**
	 * Adds entities.
	 * @param ent entities should be already spawned
	 */
	public void addEntities(AbstractEntity... ent){
		entityList.addAll(Arrays.asList(ent));
	}

	/**
	 *Disposes every entity on the map and clears the list.
	 */
	public void disposeEntities(){
		for (int i = 0; i < Controller.getMap().getEntitys().length; i++) {
			entityList.get(i).dispose();
		}
		entityList.clear();
	}

	/**
	 * Find every instance of a special class. E.g. find every
	 * <i>AbstractCharacter</i>. They must be spawned to appear in the results.
	 *
	 * @param <type>
	 * @param type
	 * @return a list with the entitys
	 */
	@SuppressWarnings(value = {"unchecked"})
	public <type extends AbstractEntity> ArrayList<type> getEntitys(final Class<type> type) {
		ArrayList<type> list = new ArrayList<>(30); //defautl size 30
		for (AbstractEntity entity : entityList) {
			//check every entity
			if (entity.isSpawned() && type.isInstance(entity)) {
				//if the entity is of the wanted type
				list.add((type) entity); //add it to list
			}
		}
		return list;
	}

	/**
	 * Get every entity on a coord.
	 *
	 * @param coord
	 * @return a list with the entitys
	 */
	public ArrayList<AbstractEntity> getEntitysOnCoord(final Coordinate coord) {
		ArrayList<AbstractEntity> list = new ArrayList<>(5);//default size 5

		for (AbstractEntity ent : entityList) {
			if (ent.getPosition() != null && ent.getPosition().toCoord().equals(coord)) {
				list.add(ent);//add it to list
			}
		}

		return list;
	}

	/**
	 * Get every entity on a coord of the wanted type
	 *
	 * @param <type> the class you want to filter.
	 * @param coord the coord where you want to get every entity from
	 * @param type the class you want to filter.
	 * @return a list with the entitys of the wanted type
	 */
	@SuppressWarnings("unchecked")
	public <type> ArrayList<type> getEntitysOnCoord(final Coordinate coord, final Class<? extends AbstractEntity> type) {
		ArrayList<type> list = new ArrayList<>(5);

		for (AbstractEntity ent : entityList) {
			if (ent.isSpawned()
				&& ent.getPosition().toCoord().getVector().equals(coord.getVector())//on coordinate?
				&& type.isInstance(ent)//of tipe of filter?
				) {
				list.add((type) ent);//add it to list
			}
		}

		return list;
	}

	/**
	 * The width of the map with three chunks in use
	 *
	 * @return amount of bluck multiplied by the size in game space.
	 */
	public int getGameWidth() {
		return getBlocksX() * Block.GAME_DIAGLENGTH;
	}

	/**
	 * The depth of the map in game size
	 *
	 * @return
	 */
	public int getGameDepth() {
		return getBlocksY() * Block.GAME_DIAGLENGTH2;
	}

	/**
	 * Game size
	 *
	 * @return
	 */
	public int getGameHeight() {
		return getBlocksZ() * Block.GAME_EDGELENGTH;
	}

	/**
	 * True if some block has changed in loaded chunks.
	 *
	 * @return returns the modified flag
	 */
	public boolean isModified() {
		return modified;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}