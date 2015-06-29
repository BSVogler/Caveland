package com.bombinggames.wurfelengine.core.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.CVar.CVarSystem;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Map.Generators.AirGenerator;
import com.bombinggames.wurfelengine.core.Map.Iterators.MemoryMapIterator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * If a class wants to be notified if a change on the map happens it must register as an {@link com.bombinggames.wurfelengine.core.Map.MapObserver} in the {@link #getOberserverList() } first.
 * @author Benedikt Vogler
 */
public abstract class AbstractMap implements Cloneable {

	private static Generator defaultGenerator = new AirGenerator();
	public final static Integer MAPVERSION = 3;

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
	private final ArrayList<AbstractEntity> entityList = new ArrayList<>(20);
	private boolean modified = true;
	/**
	 * observer pattern
	 */
	private final ArrayList<MapObserver> observers = new ArrayList<>(2);//camera + light engine=2 minimum
	private float gameSpeed;
	private final Block groundBlock = Block.getInstance((byte) WE.CVARS.getValueI("groundBlockID")); //the representative of the bottom layer (ground) block
	private Generator generator;
	private final File directory;
	private int activeSaveSlot;

	/**
	 *
	 * @param directory the directory where the map lays in
	 * @param generator
	 * @param saveSlot the used saveslot
	 * @throws IOException
	 */
	public AbstractMap(final File directory, Generator generator, int saveSlot) throws IOException {
		this.directory = directory;
		this.generator = generator;
		CVarSystem cvars = CVarSystem.getInstanceMapSystem(new File(directory + "/meta.wecvar"));
		
		WE.CVARS.setChildSystem(cvars);

		cvars.load();

		if (!hasSaveSlot(saveSlot)) {
			createSaveSlot(saveSlot);
		}
		useSaveSlot(saveSlot);
	}

	/**
	 *
	 * @param dt
	 */
	public void update(float dt) {
		dt *= gameSpeed;//aplly game speed

		//update every entity
		for (int i = 0; i < getEntitys().size(); i++) {
			AbstractEntity entity = getEntitys().get(i);
			if (entity.isInMemoryArea()) {//only update entities in memory
				entity.update(dt);
			}
		}

		//remove not spawned objects from list
		getEntitys().removeIf(
			(AbstractEntity entity) -> !entity.isSpawned()
		);
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
	 * Returns the entityList
	 *
	 * @return
	 */
	public ArrayList<AbstractEntity> getEntitys() {
		return entityList;
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
		ArrayList<AbstractEntity> list = new ArrayList<>(5);//defautl size 5

		for (AbstractEntity ent : entityList) {
			if (ent.getPosition().toCoord().equals(coord)) {
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
	 * True if some block has changed in loaded chunks.
	 *
	 * @return returns the modified flag
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * If the block can not be found returns null pointer.
	 *
	 * @param coord
	 * @return
	 */
	public abstract Block getBlock(final Coordinate coord);

	/**
	 * Returns a block without checking the parameters first. Good for debugging
	 * and also faster. O(n)
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return the single block you wanted
	 */
	public abstract Block getBlock(final int x, final int y, final int z);

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
	public ArrayList<MapObserver> getOberserverList() {
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
	protected void onModified() {
		//recalculates the light if requested
		Gdx.app.debug("Map", "onModified");
		for (MapObserver observer : observers) {
			observer.onMapChange();
		}
	}

	/**
	 * set the modified flag to true. usually not manually called.
	 */
	public void modified() {
		this.modified = true;
	}

	/**
	 *
	 * @return
	 */
	public float getGameSpeed() {
		return gameSpeed;
	}

	/**
	 * saves every chunk on the map
	 *
	 * @param saveSlot
	 * @return
	 */
	public abstract boolean save(int saveSlot);

	/**
	 * Replace a block. Assume that the map already has been filled at this
	 * coordinate.
	 *
	 * @param block no null pointer
	 * @see
	 * #setBlock(com.bombinggames.wurfelengine.Core.Gameobjects.RenderBlock)
	 */
	public abstract void setBlock(final RenderBlock block);

	/**
	 * Replace a block. Assume that the map already has been filled at this
	 * coordinate.
	 *
	 * @param coord
	 * @param block
	 * @see
	 * #setBlock(com.bombinggames.wurfelengine.Core.Gameobjects.RenderBlock)
	 */
	public abstract void setBlock(Coordinate coord, Block block);

	/**
	 * Set the speed of the world.
	 *
	 * @param gameSpeed
	 */
	public void setGameSpeed(float gameSpeed) {
		this.gameSpeed = gameSpeed;
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
	 *
	 */
	public void dispose() {
		for (int i = 0; i < entityList.size(); i++) {
			entityList.get(i).dispose();
		}
	}

	;

	/**
	 * The name of the map on the file.
	 * @return
	 */
	public File getPath() {
		return directory;
	}

	/**
	 * Returns the amount of Blocks inside the map in x-direction.
	 *
	 * @return
	 */
	public abstract int getBlocksX();

	/**
	 * Returns the amount of Blocks inside the map in y-direction.
	 *
	 * @return
	 */
	public abstract int getBlocksY();

	/**
	 * Returns the amount of Blocks inside the map in z-direction.
	 *
	 * @return
	 */
	public abstract int getBlocksZ();

	/**
	 * prints the map to console
	 */
	public abstract void print();

	/**
	 * Called after the view update to catch changes caused by the view
	 *
	 * @param dt
	 */
	public abstract void postUpdate(float dt);

	/**
	 * Clones the map. Not yet checked if a valid copy.
	 *
	 * @return
	 * @throws CloneNotSupportedException
	 */
	@Override
	public AbstractMap clone() throws CloneNotSupportedException {
		//commented deep copy because the referals are still pointing to the old objects which causes invisible duplicates.
//		clone.entityList = new ArrayList<>(entityList.size());
//		for (AbstractEntity entity : entityList) {
//			clone.entityList.add((AbstractEntity) entity.clone());
//		}
		return (AbstractMap) super.clone();
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
}