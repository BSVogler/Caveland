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
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.cvar.CVarSystemMap;
import com.bombinggames.wurfelengine.core.cvar.CVarSystemSave;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.map.rendering.RenderBlock;
import com.bombinggames.wurfelengine.core.map.Generators.AirGenerator;
import com.bombinggames.wurfelengine.core.map.Iterators.MemoryMapIterator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A map stores nine chunks as part of a bigger map. It also contains the
 * entities.
 *
 * @author Benedikt Vogler
 */
public class Map implements Cloneable, IndexedGraph<PfNode> {

	private static Generator defaultGenerator = new AirGenerator();

	/**
	 *
	 */
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
	 *
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
	 * every entity on the map is stored in this field
	 */
	private final ArrayList<AbstractEntity> entityList = new ArrayList<>(40);
	private boolean modified = true;
	/**
	 * observer pattern
	 */
	private Generator generator;
	private final File directory;
	private int activeSaveSlot;

	/**
	 * Stores the data of the map.
	 */
	private final ArrayList<Chunk> data = new ArrayList<>(40);
	
	private final ArrayList<ChunkLoader> loadingRunnables = new ArrayList<>(9);

	/**
	 * Loads a map using the default generator.
	 *
	 * @param name if available on disk it will be load
	 * @param saveslot
	 * @throws java.io.IOException
	 */
	public Map(final File name, int saveslot) throws IOException {
		this(name, getDefaultGenerator(), saveslot);
	}

	/**
	 * Loads a map.
	 *
	 * @param name if available on disk it will load the meta file
	 * @param generator the generator used for generating new chunks
	 * @param saveSlot
	 * @throws java.io.IOException thrown if there is no full read/write access
	 * to the map file
	 */
	public Map(final File name, Generator generator, int saveSlot) throws IOException {
		this.directory = name;
		WE.getCVars().get("loadedMap").setValue(name.getName());
		this.generator = generator;
		CVarSystemMap mapCVars = new CVarSystemMap(new File(directory + "/meta.wecvar"));

		WE.getCVars().setMapCVars(mapCVars);
		mapCVars.load();

		if (!hasSaveSlot(saveSlot)) {
			createSaveSlot(saveSlot);
		}
		useSaveSlot(saveSlot);

		Gdx.app.debug("Map", "Map named \"" + name + "\", saveslot " + saveSlot + " should be loaded");
	}

	/**
	 * Updates Ïmostly the entities.
	 *
	 * @param dt time in ms
	 */
	public void update(float dt) {
		dt *= WE.getCVars().getValueF("timespeed");//aplly game speed

		//add parralell loaded chunks serial to avoid conflicts
		for (int i = 0; i < loadingRunnables.size(); i++) {
			ChunkLoader runnable = loadingRunnables.get(i);
			if (runnable.getChunk() != null) {
				data.add(runnable.getChunk());
				addEntities(runnable.getChunk().retrieveEntities());
				setModified();
				loadingRunnables.remove(i);
			}
		}
		
		//update chunks
		 for (Chunk chunk : data) {
			chunk.update(dt);
		}

		//update every entity
		//old style for loop because allows modification during loop
		float rawDelta = Gdx.graphics.getRawDeltaTime() * 1000f;
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
		entityList.removeIf((AbstractEntity entity) -> !entity.hasPosition());
	}

	/**
	 * Called after the view update to catch changes caused by the view
	 *
	 * @param dt
	 */
	public void postUpdate(float dt) {
		//check for modification flag
		for (Chunk chunk : data) {
			chunk.processModification();
		}

		modificationCheck();
	}

	/**
	 * loads a chunk from disk if not already loaded.
	 *
	 * @param chunkX
	 * @param chunkY
	 */
	public void loadChunk(int chunkX, int chunkY) {
		if (getChunk(chunkX, chunkY) == null) {
			if (!isLoading(chunkX, chunkY)) {
				ChunkLoader cl = new ChunkLoader(this, getPath(), chunkX, chunkY, getGenerator());
				loadingRunnables.add(cl);
				Thread thread = new Thread(cl, "loadChunk "+chunkX+","+chunkY);
				thread.start();
			}
		}
	}

	/**
	 * loads a chunk from disk if not already loaded.
	 *
	 * @param coord
	 */
	public void loadChunk(Coordinate coord) {
		loadChunk(coord.getChunkX(), coord.getChunkY());
	}
	/**
	 * Get the data of the map
	 *
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
	public Block getBlock(final int x, final int y, final int z) {
		return getBlock(new Coordinate(x, y, z));
	}

	/**
	 * If the block can not be found returns null pointer.
	 *
	 * @param coord
	 * @return
	 */
	public Block getBlock(final Coordinate coord) {
		if (coord.getZ() < 0) {
			return getNewGroundBlockInstance();
		}
		Chunk chunk = getChunk(coord);
		if (chunk == null) {
			return null;
		} else {
			return chunk.getBlock(coord.getX(), coord.getY(), coord.getZ());//find chunk in x coord
		}
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
	 * Set a block at this coordinate. This creates a logic instance if the
	 * block if it has a logic.
	 *
	 * @param coord
	 * @param block
	 * @see
	 * #setBlock(com.bombinggames.wurfelengine.Core.Gameobjects.RenderBlock)
	 */
	public void setBlock(Coordinate coord, Block block) {
		Chunk chunk = getChunk(coord);
		if (chunk != null) {
			chunk.setBlock(coord, block);
		}
	}

	/**
	 *
	 * @param coord
	 * @param value
	 */
	public void setValue(Coordinate coord, byte value) {
		getChunk(coord).setValue(coord, value);
	}

	/**
	 * get the chunk where the coordinates are on
	 *
	 * @param coord not altered
	 * @return can return null if not loaded
	 */
	public Chunk getChunk(final Coordinate coord) {
		//checks every chunk in memory
		for (Chunk chunk : data) {
			int left = chunk.getTopLeftCoordinate().getX();
			int top = chunk.getTopLeftCoordinate().getY();
			//check if coordinates are inside the chunk
			if (left <= coord.getX()
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
	 * get the chunk with the given chunk coords. <br>Runtime: O(c)  c:
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
	 *
	 * @param xChunk
	 * @param yChunk
	 * @return
	 */
	public ArrayList<AbstractEntity> getEntitiesOnChunk(final int xChunk, final int yChunk) {
		ArrayList<AbstractEntity> list = new ArrayList<>(10);

		//loop over every loaded entity
		for (AbstractEntity ent : getEntities()) {
            if (
					ent.hasPosition()
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
	 *
	 * @param xChunk
	 * @param yChunk
	 * @return
	 */
	public ArrayList<AbstractEntity> getEntitiesOnChunkSavedOnly(final int xChunk, final int yChunk) {
		ArrayList<AbstractEntity> list = new ArrayList<>(10);

		//loop over every loaded entity
		for (AbstractEntity ent : getEntities()) {
            if (
					ent.isGettingSaved() && ent.hasPosition() //save only entities which are flagged
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
	 * save every chunk using the current active save slot. Saves position of
	 * the sun and moon at origin.
	 *
	 * @return
	 */
	public boolean save() {
		WE.getCVarsSave().get("LEsunAzimuth").setValue(Controller.getLightEngine().getSun(new Coordinate(0, 0, 0)).getAzimuth());
		WE.getCVarsSave().get("LEmoonAzimuth").setValue(Controller.getLightEngine().getMoon(new Coordinate(0, 0, 0)).getAzimuth());
		return save(activeSaveSlot);
	}

	/**
	 * prints the map to console
	 */
	public void print() {
		MemoryMapIterator iter = getIterator(0, Chunk.getBlocksZ() - 1);
		while (iter.hasNext()) {
			//if (!iter.hasNextY() && !iter.hasNextX())
			//	System.out.print("\n\n");
			//if (!iter.hasNsextX())
			//	System.out.print("\n");

			Block block = iter.next();
			if (block == null) {
				System.out.print("  ");
			} else {
				System.out.print(block.getId() + " ");
			}
		}
	}

	/**
	 * disposes every chunk
	 *
	 * @param save
	 */
	public void dispose(boolean save) {
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
	 *
	 * @param coord
	 * @return
	 */
	public AbstractBlockLogicExtension getLogic(Coordinate coord) {
		Chunk chunk = getChunk(coord);
		if (chunk == null) {
			return null;
		} else {
			return chunk.getLogic(coord);
		}
	}

	/**
	 * Add a logicblock to the map.
	 *
	 * @param block
	 */
	public void addLogic(AbstractBlockLogicExtension block) {
		Chunk chunk = getChunk(block.getPosition());
		chunk.addLogic(block);
	}

	/**
	 * uses a specific save slot for loading and saving the map. Loads the save
	 * cvars.
	 *
	 * @param slot
	 */
	public void useSaveSlot(int slot) {
		this.activeSaveSlot = slot;
		WE.getCVarsMap().get("currentSaveSlot").setValue(slot);
		//load save cvars
		WE.getCVarsMap().setSaveCVars(
			new CVarSystemSave(
				new File(directory + "/save" + activeSaveSlot + "/meta.wecvar")
			)
		);
		WE.getCVarsSave().load();
	}

	/**
	 * Uses a new save slot as the save slot
	 *
	 * @return the new save slot number
	 */
	public int newSaveSlot() {
		useSaveSlot(getSavesCount());
		createSaveSlot(activeSaveSlot);
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

	/**
	 *
	 * @param slot
	 */
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
			MessageManager.getInstance().dispatchMessage(Events.mapChanged.getId());
			modified = false;
		}
	}

	/**
	 *
	 * @return
	 */
	public Generator getGenerator() {
		return generator;
	}

	/**
	 *
	 * @return
	 */
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
	 *
	 * @return
	 */
	public File getPath() {
		return directory;
	}

	/**
	 * Creates a new instance of the groundblock.
	 *
	 * @return
	 */
	public Block getNewGroundBlockInstance() {
		return Block.getInstance((byte) WE.getCVars().getValueI("groundBlockID")); //the representative of the bottom layer (ground) block
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
		return getCenter(Chunk.getBlocksZ() * Block.GAME_EDGELENGTH / 2);
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
	public ArrayList<AbstractEntity> getEntities() {
		return entityList;
	}

	/**
	 * Adds entities.
	 *
	 * @param ent entities should be already spawned
	 */
	public void addEntities(AbstractEntity... ent) {
		//remove duplicates
		for (AbstractEntity e : ent) {
			entityList.remove(e);
		}
		entityList.addAll(Arrays.asList(ent));
	}
	
	/**
	 * Adds entities.
	 *
	 * @param ent entities should be already spawned
	 */
	public void addEntities(Collection<AbstractEntity> ent) {
		//remove duplicates
		for (AbstractEntity e : ent) {
			entityList.remove(e);
		}
		entityList.addAll(ent);
	}
	

	/**
	 * Disposes every entity on the map and clears the list.
	 */
	public void disposeEntities() {
		entityList.forEach((AbstractEntity e) -> e.dispose());
		entityList.clear();
	}

	/**
	 * Find every instance of a special class. E.g. find every
	 * <i>AbstractCharacter</i>. They must be spawned to appear in the results.
	 *
	 * @param <type> the class you want to filter.
	 * @param filter the class you want to filter.
	 * @return a list with the entitys
	 */
	@SuppressWarnings(value = {"unchecked"})
	public <type extends AbstractEntity> ArrayList<type> getEntitys(final Class<type> filter) {
		ArrayList<type> result = new ArrayList<>(30); //default size 30
		if (filter == null) {
			throw new IllegalArgumentException();
		}
		for (AbstractEntity entity : entityList) {
			if (entity.hasPosition() && filter.isInstance(entity)) {
				result.add((type) entity);
			}
		}
		return result;
	}

	/**
	 * Get every entity on a coord.
	 *
	 * @param coord
	 * @return a list with the entitys
	 */
	public ArrayList<AbstractEntity> getEntitysOnCoord(final Coordinate coord) {
		ArrayList<AbstractEntity> result = new ArrayList<>(5);//default size 5

		for (AbstractEntity ent : entityList) {
			if (ent.getPosition() != null && ent.getPosition().toCoord().equals(coord)) {
				result.add(ent);
			}
		}

		return result;
	}

	/**
	 * Get every entity on a coord of the wanted type
	 *
	 * @param <type> the class you want to filter.
	 * @param coord the coord where you want to get every entity from
	 * @param filter the class you want to filter.
	 * @return a list with the entitys of the wanted type
	 */
	@SuppressWarnings("unchecked")
	public <type> ArrayList<type> getEntitysOnCoord(final Coordinate coord, final Class<? extends AbstractEntity> filter) {
		ArrayList<type> result = new ArrayList<>(5);

		for (AbstractEntity ent : entityList) {
			if (ent.hasPosition()
				&& ent.getPosition().toCoord().equals(coord)//on coordinate?
				&& filter.isInstance(ent)//of tipe of filter?
				) {
				result.add((type) ent);//add it to list
			}
		}

		return result;
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

	@Override
	public Array<Connection<PfNode>> getConnections(PfNode fromNode) {
		return fromNode.getConnections();

	}

	/**
	 *
	 * @param start
	 * @param goal
	 * @return
	 */
	public DefaultGraphPath<PfNode> findPath(Coordinate start, Coordinate goal) {
		IndexedAStarPathFinder<PfNode> pathFinder;
		pathFinder = new IndexedAStarPathFinder<>(this, true);

		DefaultGraphPath<PfNode> path = new DefaultGraphPath<>();
		path.clear();
		Heuristic<PfNode> heuristic = new ManhattanDistanceHeuristic();

		boolean found = pathFinder.searchNodePath(
			new PfNode(start),
			new PfNode(goal),
			heuristic,
			path
		);

		return path;
	}

	@Override
	public int getNodeCount() {
		return Chunk.getBlocksX() * Chunk.getBlocksY();
	}

	public boolean isLoading(int chunkX, int chunkY) {
		for (ChunkLoader lR : loadingRunnables) {
			if (lR.getCoordX() == chunkX && lR.getCoordY() == chunkY)
				return true;
		}
		return false;
	}

	private static class ManhattanDistanceHeuristic implements Heuristic<PfNode> {

		@Override
		public float estimate(PfNode node, PfNode endNode) {
			return Math.abs(endNode.getX() - node.getX()) + Math.abs(endNode.getY() - node.getY());
		}
	}

	private static class EuklideanDistanceHeuristic implements Heuristic<PfNode> {

		@Override
		public float estimate(PfNode node, PfNode endNode) {
			return node.distanceTo(endNode);
		}
	}
}
