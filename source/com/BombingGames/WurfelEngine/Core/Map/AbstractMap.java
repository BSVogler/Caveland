package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.Generators.AirGenerator;
import com.BombingGames.WurfelEngine.Core.Map.Iterators.MemoryMapIterator;
import com.badlogic.gdx.Gdx;
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
	 * Should create a new map file.
	 * @param mapName file name
	 * @throws java.io.IOException
	 */
	public static void createMapFile(final String mapName) throws IOException {
		MapMetaData meta = new MapMetaData(mapName);
		meta.setMapName(mapName);
		meta.write();
	}
	/** every entity on the map is stored in this field */
	private ArrayList<AbstractEntity> entityList = new ArrayList<>(20);
	private boolean modified = true;
	private ArrayList<LinkedWithMap> linkedObjects = new ArrayList<>(3);//camera + minimap + light engine=3 minimum
	private float gameSpeed;
	private final Block groundBlock = Block.getInstance(CVar.get("groundBlockID").getValuei()); //the representative of the bottom layer (ground) block
	/**
	 * holds the metadata of the map
	 */
	private final MapMetaData meta;
	private Generator generator;
	private final String filename;

	public AbstractMap(final String name, Generator generator) throws IOException {
		this.filename = name;
		this.generator = generator;
		
		meta = new MapMetaData(name);
		if (CVar.get("shouldLoadMap").getValueb()){
			meta.load();
		}
	}
	
	    /**
     *Returns the degree of the world spin. This changes where the sun rises and falls.
     * @return a number between 0 and 360
     */
    public int getWorldSpinDirection() {
        return CVar.get("worldSpinAngle").getValuei();
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
		return getCenter(getBlocksZ() * Block.GAME_EDGELENGTH / 2);
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
	 *
	 * @param coord
	 * @return
	 */
	public abstract Block getBlock(final Coordinate coord);

	/**
	 * Returns a block without checking the parameters first. Good for debugging and also faster.
	 * O(n)
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
	 *
	 * @return
	 */
	public MapMetaData getMeta() {
		return meta;
	}

	/**
	 * saves every chunk on the map
	 * @return
	 */
	public abstract boolean save();

	/**
	 * Replace a block. Assume that the map already has been filled at this coordinate.
	 * @param block
	 */
	public abstract void setBlock(final Block block);

	/**
	 * remove a block from a coord
	 * @param coord
	 */
	public abstract void destroyBlockOnCoord(Coordinate coord);

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
	public String getFilename() {
		return filename;
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
	 * prints the map to console
	 */
	public abstract void printCoords();

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
}
