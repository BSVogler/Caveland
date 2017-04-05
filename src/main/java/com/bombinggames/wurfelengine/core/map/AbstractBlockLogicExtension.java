package com.bombinggames.wurfelengine.core.map;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the game logic for a block. The instances are not saved in the map
 * save file; therfore, every data saved in the fields are lost after
 * quitting.<br>
 * Points to a {@link Coordinate} in the map. If the content of the coordinate
 * changes it will be removed via
 * {@link  com.bombinggames.wurfelengine.core.map.Map}. Check if is about to be
 * removed via {@link #isValid() }.<br> If you want to save information in the
 * save file you have to use and spawn an {@link com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity}.
 *
 * @author Benedikt Vogler
 */
public abstract class AbstractBlockLogicExtension {

	private static final long serialVersionUID = 2L;
	private static final HashMap<Byte, Class<? extends AbstractBlockLogicExtension>> LOGICREGISTER = new HashMap<>(20);

	public static void registerClass(byte id, Class<? extends AbstractBlockLogicExtension> aClass){
		LOGICREGISTER.put(id, aClass);
}
	/**
	 * 
	 * @param blockId the block at the position
	 * @param value
	 * @param coord the position where the logic block is placed
	 * @return 
	 */
 	public static AbstractBlockLogicExtension newLogicInstance(byte blockId, byte value, Coordinate coord) {
		if (coord == null) {
			throw new NullPointerException();
		}
		try {
			Class<? extends AbstractBlockLogicExtension> aClass = LOGICREGISTER.get(blockId);
			if (aClass!=null) {
				AbstractBlockLogicExtension instance = aClass.newInstance();
				instance.id = blockId;
				instance.coord = coord;
				instance.setValue(value);
				instance.setCoord(coord);
				return instance;
			}
		} catch (InstantiationException | IllegalAccessException ex) {
			Logger.getLogger(AbstractBlockLogicExtension.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public static boolean isRegistered(byte blockId) {
		return LOGICREGISTER.containsKey(blockId);
	}
	
	/**
	 * pointer to the according coordinate
	 */
	private Coordinate coord;
	/**
	 * Is only used for validity check.
	 */
	private byte id;

	/**
	 * Called when spawned. Should not access the map because during map
	 * creating this method is called and the map still empty. Also entities can not be spawned here.
	 *

	 */
	public AbstractBlockLogicExtension() {
	}

	/**
	 * This method must be named "getPosition" so that this method can implement
	 * other interfaces using this API signature
	 *
	 * @return not copy safe. never null
	 */
	public Coordinate getPosition() {
		return coord;
	}

	/**
	 * A logicblock is still valid if the pointer shows to a block with the same
	 * id as during creation.
	 *
	 * @return false if should be deleted
	 */
	public boolean isValid() {
		return coord.getBlockId() == id;
	}

	/**
	 *
	 * @param dt
	 */
	public abstract void update(float dt);

	/**
	 * called when removed
	 */
	public abstract void dispose();

	/**
	 * 
	 * @param value 
	 */
	public void setValue(byte value) {
	}

	private void setCoord(Coordinate coord) {
		this.coord = coord;
	}

}
