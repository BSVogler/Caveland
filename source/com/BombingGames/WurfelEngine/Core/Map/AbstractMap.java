package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.CVar.CVar;
import com.BombingGames.WurfelEngine.Core.CVar.CVarSystem;
import com.BombingGames.WurfelEngine.Core.CVar.IntCVar;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.BombingGames.WurfelEngine.Core.Map.Generators.AirGenerator;
import com.BombingGames.WurfelEngine.Core.Map.Iterators.MemoryMapIterator;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public abstract class AbstractMap implements Cloneable {
	private static Generator defaultGenerator = new AirGenerator();
	
	/**
	 *
	 * @param generator
	 */
	public static void setDefaultGenerator(Generator generator) {
		defaultGenerator = generator;
	}
	
	/**
	 * Get the default set generator.
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
	public static void createSaveSlot(File path, int slot){
		FileHandle pathHandle = Gdx.files.absolute(path+"/save"+slot+"/");
        if (!pathHandle.exists()){
			pathHandle.mkdirs();
		}
		//copy from map folder root
		FileHandle root = Gdx.files.absolute(path.getAbsolutePath());
		FileHandle[] childen = root.list();
		for (FileHandle file : childen) {
			if (!file.isDirectory()){
				file.copyTo(pathHandle);
			}
		}
	}
	
	public static int getSavesCount(File path) {
		FileHandle children = Gdx.files.absolute(path.getAbsolutePath());
		int i = 0;
		while (children.child("save"+i).exists()) {			
			i++;
		}
		return i;
	}

	private static CustomMapCVarRegistration customRegistration;
	
	/**
	 * Set a custom registration of cvars before they are loaded.
	 * @param mapcvars 
	 */
	public static void setCustomMapCVarRegistration(CustomMapCVarRegistration mapcvars) {
		customRegistration = mapcvars;
	}
	
	/** every entity on the map is stored in this field */
	private ArrayList<AbstractEntity> entityList = new ArrayList<>(20);
	private boolean modified = true;
	private ArrayList<LinkedWithMap> linkedObjects = new ArrayList<>(3);//camera + minimap + light engine=3 minimum
	private float gameSpeed;
	private final CoreData groundBlock = CoreData.getInstance((byte) WE.CVARS.getValueI("groundBlockID")); //the representative of the bottom layer (ground) block
	private Generator generator;
	private final File directory;
	private int activeSaveSlot;
	/**
	 * cvar system for the map. SHould in most cases read only because they are not save file independant.
	 */
	private CVarSystem cvars;
	private CVarSystem saveCVars; 

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
		cvars = new CVarSystem(new File(directory+"/meta.wecvar"));
		//engine cvar registration
		cvars.register( new IntCVar(1), "groundBlockID", CVar.CVarFlags.CVAR_ARCHIVE);
		cvars.register( new IntCVar(10), "chunkBlocksX", CVar.CVarFlags.CVAR_ARCHIVE);
		cvars.register( new IntCVar(40), "chunkBlocksY", CVar.CVarFlags.CVAR_ARCHIVE);
		cvars.register( new IntCVar(10), "chunkBlocksZ", CVar.CVarFlags.CVAR_ARCHIVE);
		
		//custom registration of cvars
		if (customRegistration!=null)
			customRegistration.register(cvars);
		
		cvars.load();
		
		if (!hasSaveSlot(saveSlot))
			createSaveSlot(saveSlot);
		activeSaveSlot = saveSlot;
		saveCVars = new CVarSystem(new File(directory+"/save"+activeSaveSlot+"/meta.wecvar"));
	}
	
	/**
	 * should be set if you want to have custom. Loads cvars from file.
	 * @param cvarSystem 
	 */
	public void setCVarSystem(CVarSystem cvarSystem){
		cvars = cvarSystem;
		cvars.load();
	}
	
	    /**
     *Returns the degree of the world spin. This changes where the sun rises and falls.
     * @return a number between 0 and 360
     */
    public int getWorldSpinDirection() {
        return WE.CVARS.getValueI("worldSpinAngle");
    }

	
	
	/**
	 *
	 * @param object
	 */
	public void addLinkedObject(LinkedWithMap object) {
		linkedObjects.add(object);
	}

	/**
	 *Returns a coordinate pointing to the absolute center of the map. Height is half the map's height.
	 * @return
	 */
	public Point getCenter() {
		return getCenter(getBlocksZ() * RenderBlock.GAME_EDGELENGTH / 2);
	}


	/**
	 *Returns a coordinate pointing to middle of a 3x3 chunk map.
	 * @param height You custom height.
	 * @return
	 */
	public Point getCenter(final float height) {
		return new Point(
			this,
			Chunk.getGameWidth() / 2,
			Chunk.getGameDepth() / 2,
			height
		);
	}

	/**
	 * Returns the entityList
	 * @return
	 */
	public ArrayList<AbstractEntity> getEntitys() {
		return entityList;
	}
	
	/**
	 *The width of the map with three chunks in use
	 * @return amount of bluck multiplied by the size in game space.
	 */
	public int getGameWidth() {
		return getBlocksX() * AbstractGameObject.GAME_DIAGLENGTH;
	}
	
	/**
	 * The depth of the map in game size
	 * @return
	 */
	public int getGameDepth() {
		return getBlocksY() * AbstractGameObject.GAME_DIAGLENGTH2;
	}

	/**
	 * Game size
	 * @return
	 */
	public int getGameHeight() {
		return getBlocksZ() * AbstractGameObject.GAME_EDGELENGTH;
	}



	/**
	 * Find every instance of a special class. E.g. find every <i>AbstractCharacter</i>.
	 * @param <type>
	 * @param type
	 * @return a list with the entitys
	 */
	@SuppressWarnings(value = {"unchecked"})
	public <type extends AbstractEntity> ArrayList<type> getEntitys(final Class<type> type) {
		ArrayList<type> list = new ArrayList<>(30); //defautl size 30
		for (AbstractEntity entity : entityList) {
			//check every entity
			if (type.isInstance(entity)) {
				//if the entity is of the wanted type
				list.add((type) entity); //add it to list
			}
		}
		return list;
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
	 * True if some block has changed in loaded chunks.
	 * @return returns the modified flag
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * If the block can not be found returns null pointer.
	 * @param coord
	 * @return
	 */
	public abstract CoreData getBlock(final Coordinate coord);

	/**
	 * Returns a block without checking the parameters first. Good for debugging and also faster.
	 * O(n)
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return the single block you wanted
	 */
	public abstract CoreData getBlock(final int x, final int y, final int z);

	/**
	 *
	 * @return
	 */
	public CoreData getGroundBlock() {
		return groundBlock;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<LinkedWithMap> getLinkedObjects() {
		return linkedObjects;
	}
	
	/**
	 * Get an iteration which can loop throug the map
	 * @param startLimit the starting level
	 * @param topLimitZ the top limit of the iterations
	 * @return 
	 */
	public MemoryMapIterator getIterator(int startLimit, int topLimitZ){
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
		for (LinkedWithMap object : linkedObjects) {
			object.onMapChange();
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
	 * @param saveSlot
	 * @return
	 */
	public abstract boolean save(int saveSlot);

	/**
	 * Replace a block. Assume that the map already has been filled at this coordinate.
	 * @param block no null pointer
	 * @see #setBlock(com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock) 
	 */
	public abstract void setBlock(final RenderBlock block);

	/**
	 * Replace a block. Assume that the map already has been filled at this coordinate.
	 * @param coord
	 * @param block
	 * @see #setBlock(com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock) 
	 */
	public abstract void setBlock(Coordinate coord, CoreData block);

	/**
	 * Set the speed of the world.
	 * @param gameSpeed
	 */
	public void setGameSpeed(float gameSpeed) {
		this.gameSpeed = gameSpeed;
	}

	/**
	 * Set the generator used for generating maps
	 * @param generator
	 */
	public void setGenerator(Generator generator) {
		this.generator = generator;
	}

	/**
	 *
	 */
	public void dispose(){
	    for (int i = 0; i < entityList.size(); i++) {
			entityList.get(i).dispose();
        }
	};

	/**
	 * The name of the map on the file.
	 * @return
	 */
	public File getPath() {
		return directory;
	}

	/**
	 * Returns the amount of Blocks inside the map in x-direction.
	 * @return
	 */
	public abstract int getBlocksX();

	/**
	 * Returns the amount of Blocks inside the map in y-direction.
	 * @return
	 */
	public abstract int getBlocksY();

	/**
	 * Returns the amount of Blocks inside the map in z-direction.
	 * @return
	 */
	public abstract int getBlocksZ();

	/**
	 * prints the map to console
	 */
	public abstract void print();

	/**
	 * Called after the view update to catch changes caused by the view
	 * @param dt
	 */
	public abstract void postUpdate(float dt);
	
	    /**
     *Clones the map. Not yet checked if a valid copy.
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public AbstractMap clone() throws CloneNotSupportedException{
		//commented deep copy because the referals are still pointing to the old objects which causes invisible duplicates.
//		clone.entityList = new ArrayList<>(entityList.size());
//		for (AbstractEntity entity : entityList) {
//			clone.entityList.add((AbstractEntity) entity.clone());
//		}
        return (AbstractMap) super.clone();
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
			if (entity.isInMemoryArea())//only update entities in memory
				entity.update(dt);
			//else entity.dispose();//dispose entities outside of memory area
			if (entity.shouldBeDisposedFromMap())
				getEntitys().remove(i);
		}

	};
	
	/**
	 * should be executed after the update method
	 */
	public void modificationCheck(){
		if (modified){
			onModified();
			modified = false;
		}
	}

	public Generator getGenerator() {
		return generator;
	}
	
	public int getCurrentSaveSlot(){
		return activeSaveSlot;
	}
	
	/**
	 * uses a specific save slot for loading and saving the map
	 * @param slot 
	 */
	public void useSaveSlot(int slot){
		this.activeSaveSlot = slot;
		saveCVars = new CVarSystem(new File(directory+"/save"+activeSaveSlot+"/meta.wecvar"));
	}
	
	/**
	 * Uses a new save slot as the save slot
	 * @return the new save slot number
	 */
	public int newSaveSlot() {
		activeSaveSlot = getSavesCount();
		createSaveSlot(activeSaveSlot);
		saveCVars = new CVarSystem(new File(directory+"/save"+activeSaveSlot+"/meta.wecvar"));
		return activeSaveSlot;
	}
	
	/**
	 * Check if a save slot exists.
	 * @param saveSlot
	 * @return 
	 */
	public boolean hasSaveSlot(int saveSlot) {
		FileHandle path = Gdx.files.absolute(directory+"/save"+saveSlot);
		return path.exists();
	}
	
	public void createSaveSlot(int slot){
		createSaveSlot(directory, slot);
	}
	
		
	/**
	 * checks a map for the amount of save files
	 * @return the amount of saves for this map
	 */
	public int getSavesCount() {
		return getSavesCount(directory);
	}
	
	/**
	 * in regular case only read operations should be performed on the cvars in here
	 * @return 
	 */
	public CVarSystem getCVars() {
		return cvars;
	}
	
	/**
	 * save dependant Cvars. They are not loaded from disk unitl you do that.
	 * @return 
	 */
	public CVarSystem getSaveCVars(){
		return saveCVars;
	}
}
