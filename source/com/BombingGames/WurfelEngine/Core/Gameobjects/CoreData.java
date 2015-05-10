package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import java.io.Serializable;

/**
 *A small block object hich stores only id and value and is only used for storing in memory. Stores only 8 byte.
 * @author Benedikt Vogler
 */
public class CoreData implements HasID, Serializable {
	private static final long serialVersionUID = 1L;
	
	/**Screen depth of a block/object sprite in pixels. This is the length from the top to the middle border of the block.
     */
    public transient static final int VIEW_DEPTH = 100;
    /**The half (1/2) of VIEW_DEPTH. The short form of: VIEW_DEPTH/2*/
    public transient static final int VIEW_DEPTH2 = VIEW_DEPTH / 2;
    /**A quarter (1/4) of VIEW_DEPTH. The short form of: VIEW_DEPTH/4*/
    public transient static final int VIEW_DEPTH4 = VIEW_DEPTH / 4;
    
    /**
     * The width (x-axis) of the sprite size.
     */
    public transient static final int VIEW_WIDTH = 200;
    /**The half (1/2) of VIEW_WIDTH. The short form of: VIEW_WIDTH/2*/
    public transient static final int VIEW_WIDTH2 = VIEW_WIDTH / 2;
    /**A quarter (1/4) of VIEW_WIDTH. The short form of: VIEW_WIDTH/4*/
    public transient static final int VIEW_WIDTH4 = VIEW_WIDTH / 4;
    
    /**
     * The height (y-axis) of the sprite size.
     */
    public transient static final int VIEW_HEIGHT = 122;
    /**The half (1/2) of VIEW_HEIGHT. The short form of: VIEW_WIDTH/2*/
    public transient static final int VIEW_HEIGHT2 = VIEW_HEIGHT / 2;
    /**A quarter (1/4) of VIEW_HEIGHT. The short form of: VIEW_WIDTH/4*/
    public transient static final int VIEW_HEIGHT4 = VIEW_HEIGHT / 4;
    
    /**
     * The game space dimension size's aequivalent to VIEW_DEPTH or VIEW_WIDTH.
     * Because the x axis is not shortened those two are equal.
     */
    public transient static final int GAME_DIAGLENGTH = VIEW_WIDTH;
    
    /**Half (1/2) of GAME_DIAGLENGTH.
     */
    public transient static final int GAME_DIAGLENGTH2 = VIEW_WIDTH2;
	
	    /**
     * The game spaces dimension in pixel (edge length). 1 game meter ^= 1 GAME_EDGELENGTH
 The value is calculated by VIEW_HEIGHT*sqrt(2) because of the axis shortening.
     */
    public transient static final int GAME_EDGELENGTH = (int) (GAME_DIAGLENGTH / 1.41421356237309504880168872420969807856967187537694807317667973799f);
    
	/**
     * Half (1/2) of GAME_EDGELENGTH.
     */
    public transient static final int GAME_EDGELENGTH2 = GAME_EDGELENGTH/2;
    
	/**
	 * Some magic number which is the factor by what the Z axis is distorted because of the angle pf projection.
	 */
	public transient static final float ZAXISSHORTENING = VIEW_HEIGHT/(float) GAME_EDGELENGTH;
		
    /**the max. amount of different object types*/
    public transient static final int OBJECTTYPESNUM = 124;
      /**the max. amount of different values*/
    public transient static final int VALUESNUM = 64;
	
	/**
	 * the factory for custom blocks
	 */
	private static CustomBlocks customBlocks;
	
		/**
	 * If you want to define custom id's &gt;39
	 *
	 * @param customBlockFactory new value of customBlockFactory
	 */
	public static void setCustomBlockFactory(CustomBlocks customBlockFactory) {
		CoreData.customBlocks = customBlockFactory;
	}

	public static CustomBlocks getFactory() {
		return customBlocks;
	}
	
	private byte id;
	private byte value;
	private byte health = 100;
	private float lightlevel = 1f;//saved here because it saves recalcualtion for every camera

	private CoreData(byte id) {
		this.id = id;
	}
	
	private CoreData(byte id, byte value) {
		this.id = id;
		this.value = value;
	}
	
	/**
	 * Use for creating new objects.
	 * @param id
	 * @return 
	 */
	public static CoreData getInstance(byte id){
		if (id==0) return null;
		return new CoreData(id, (byte) 0);
	}
	
	/**
	 * Use for creating new objects.
	 * @param id
	 * @param value
	 * @return 
	 */
	public static CoreData getInstance(byte id, byte value){
		if (id==0) return null;
		return new CoreData(id, value);
	}
	
	@Override
	public byte getId() {
		return id;
	}

	@Override
	public byte getValue() {
		return value;
	}
	
	public void setValue(byte value) {
		this.value = value;
	}
	
	/**
	 * value between 0-100
	 * @param coord
	 * @param health 
	 */
	public void setHealth(Coordinate coord, byte health){
		this.health = health;
		if (customBlocks != null){
			customBlocks.setHealth(coord, health, id, value);
        }
		if (health <= 0) {
			//make an invalid air instance (should be null)
			this.id = 0;
			this.value = 0;
		}
	}
	
	/**
	 * value between 0-100. This method should only be used for non-bocks.
	 * @param health 
	 */
	public void setHealth(byte health){
		this.health = health;
	}
	
	public byte getHealth(){
		return health;
	}
	
	/**
	 * creates a new RenderBlock instance based on he data
	 * @return 
	 */
	public RenderBlock toBlock(){
		if (id==0)
			return null;
		if (id==9)
			return new Sea(id);
		if (id>9 && customBlocks != null){
            return customBlocks.toRenderBlock(id, value);
        }
		return new RenderBlock(id, value);
	}

	@Override
	public boolean isObstacle() {
		if (id>9 && customBlocks != null){
            return customBlocks.isObstacle(id, value);
        }
		if (id==9)
			return false;
		return id != 0;
	}

	@Override
	public boolean isTransparent() {
		if (id>9 && customBlocks != null){
            return customBlocks.isTransparent(id, value);
        }
		if (id==9)
			return true;
		return false;
	}
	
    /**
     * Check if the block is liquid.
     * @return true if liquid, false if not 
     */
	public boolean isLiquid() {
		if (id>9 && customBlocks != null){
            return customBlocks.isLiquid(id, value);
        }
		if (id==9)
			return true;
		return false;
	}
	
	@Override
	public float getLightlevel() {
		return lightlevel;
	}

	@Override
	public void setLightlevel(float lightlevel) {
		this.lightlevel = lightlevel;
	}
	
	/**
	 * get the name of a combination of id and value
	 * @return 
	 */
	@Override
	public String getName(){
		switch (id) {
			case 0:
				return "air";
			case 1:
				return "grass";
			case 2:
				return "dirt";
			case 3:
				return "???";
			case 4:
				return "???";
			case 5:
				return "???";
			case 6:
				return "???";
			case 7:
				return "???";
			case 8:
				return "sand";
			case 9:
				return "water";								
			default:
				if (id > 9) {
                    if (customBlocks!=null){
                        return customBlocks.getName(id, value);
                    } else {
                        return "no custom blocks";
                    }
                } else {
                    return "engine block not properly defined";
                }
		}
	}

	/**
	 * Creates a new instance of {@link RenderBlock} to check if it has sides. You should prefer the hasSides call to a {@link RenderBlock} object.
	 * @return true if it has sides
	 * @see RenderBlock#hasSides() 
	 */
	public boolean hasSides() {
		RenderBlock block = toBlock();
		if (block==null) return false;
		return block.hasSides();
	}
}
