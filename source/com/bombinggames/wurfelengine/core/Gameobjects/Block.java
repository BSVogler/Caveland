package com.bombinggames.wurfelengine.core.Gameobjects;

import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.io.Serializable;

/**
 *A small block object hich stores only id and value and is only used for storing in memory. Stores only 8 byte.
 * @author Benedikt Vogler
 */
public class Block implements HasID, Serializable {
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
		Block.customBlocks = customBlockFactory;
	}

	public static CustomBlocks getFactory() {
		return customBlocks;
	}
	
	private byte id;
	private byte value;
	private byte health = 100;
	/**
	 *  each side has RGB color stored as 10bit float. Obtained by dividing bits by fraction /1023.
	 */
	private int colorLeft = (55<<16)+(55<<8)+55;
	private int colorTop = (55<<16)+(55<<8)+55;;
	private int colorRight = (55<<16)+(55<<8)+55;
	/**
	 * byte 0: left side, byte 1: top side, byte 2: right side.<br>In each byte bit order: <br>
	 * 7 \ 0 / 1<br>
	 * -------<br>
	 * 6 | - | 2<br>
	 * -------<br>
	 * 5 / 4 \ 3<br>
	 */
	private int aoFlags;
	private byte clipping;

	private Block(byte id) {
		this.id = id;
	}
	
	private Block(byte id, byte value) {
		this.id = id;
		this.value = value;
	}
	
	/**
	 * Use for creating new objects.
	 * @param id
	 * @return 
	 */
	public static Block getInstance(byte id){
		if (id==0) return null;
		return new Block(id, (byte) 0);
	}
	
	/**
	 * Use for creating new objects.
	 * @param id
	 * @param value
	 * @return 
	 */
	public static Block getInstance(byte id, byte value) {
		if (id==0) return null;
		if (id>OBJECTTYPESNUM) return null;
		if (value>VALUESNUM) return null;
		return new Block(id, value);
	}
	
	@Override
	public byte getId() {
		return id;
	}

	@Override
	public byte getValue() {
		return value;
	}
	
	@Override
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
		
		if (id>9 && customBlocks != null)
           return customBlocks.toRenderBlock(this);
        
		return new RenderBlock(this);
	}

	@Override
	public boolean isObstacle() {
		if (id>9 && customBlocks != null){
            return customBlocks.isObstacle(id, value);
        }
		if (id==9)
			return false;
		if (id==0) return false;
		return true;
	}

	@Override
	public boolean isTransparent() {
		if (id==9)
			return true;
		if (id>9 && customBlocks != null){
            return customBlocks.isTransparent(id, value);
        }
		return false;
	}
	
    /**
     * Check if the block is liquid.
     * @return true if liquid, false if not 
     */
	@Override
	public boolean isLiquid() {
		if (id>9 && customBlocks != null){
            return customBlocks.isLiquid(id, value);
        }
		if (id==9)
			return true;
		return false;
	}
	
	/**
	 * get the average brightness above all the sides
	 * @return 
	 */
	@Override
	public float getLightlevelR() {
		return (getLightlevelR(Side.LEFT)+getLightlevelR(Side.TOP)+getLightlevelR(Side.RIGHT))/3f;
	}
	
	@Override
	public float getLightlevelG() {
		return (getLightlevelG(Side.LEFT)+getLightlevelG(Side.TOP)+getLightlevelG(Side.RIGHT))/3f;
	}
	
	@Override
	public float getLightlevelB() {
		return (getLightlevelB(Side.LEFT)+getLightlevelB(Side.TOP)+getLightlevelB(Side.RIGHT))/3f;
	}
	
	/**
	 * 
	 * @param side
	 * @return 
	 */
	public float getLightlevelR(Side side){
		if (side==Side.LEFT)
			return ((colorLeft >> 20) & 0x3FF)/1023f;
		else if (side==Side.TOP)
			return ((colorTop >> 20) & 0x3FF)/1023f;
		else
			return ((colorRight >> 20) & 0x3FF)/1023f;
	}
	
	/**
	 * 
	 * @param side
	 * @return 
	 */
	public float getLightlevelG(Side side){
		if (side==Side.LEFT)
			return ((colorLeft >> 10) & 0x3FF)/1023f;
		else if (side==Side.TOP)
			return ((colorTop >> 10) & 0x3FF)/1023f;
		else
			return ((colorRight >> 10) & 0x3FF)/1023f;
	}
	
	/**
	 * 
	 * @param side
	 * @return 
	 */
	public float getLightlevelB(Side side){
		if (side==Side.LEFT)
			return (colorLeft & 0x3FF)/1023f;
		else if (side==Side.TOP)
			return (colorTop & 0x3FF)/1023f;
		else
			return (colorRight & 0x3FF)/1023f;
	}

	/**
	 * stores the lightlevel overriding each side
	 * @param lightlevel 
	 */
	@Override
	public void setLightlevel(float lightlevel) {
		this.colorLeft =  ((int) (lightlevel*1023)<<20)+((int) (lightlevel*1023)<<10)+(int) (lightlevel*1023);//RGB
		this.colorTop =   colorLeft;
		this.colorRight = colorLeft;
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

	@Override
	public boolean hasSides() {
		if (id==0) return false;
		if (id>9 && customBlocks != null){
			return customBlocks.hasSides(id, value);
		}
		return true;
	}

	public boolean hidingPastBlock() {
		return hasSides() && !isTransparent();
	}

	/**
	 * 
	 * @param side 
	 */
	public void setAOFlagTrue(int side) {
		this.aoFlags |= 1 << side;//set n'th bit to true via OR operator
	}
	
	public void setAOFlagFalse(int side){
		this.aoFlags &= ~(1 << side);//set n'th bit to false via AND operator
	}

	/**
	 * byte 0: left side, byte 1: top side, byte 2: right side.<br>In each byte bit order: <br>
	 * 7 \ 0 / 1<br>
	 * -------<br>
	 * 6 | - | 2<br>
	 * -------<br>
	 * 5 / 4 \ 3<br>
	 * @return 
	 */
	public int getAOFlags() {
		return aoFlags;
	}

	public void setAoFlags(int aoFlags) {
		this.aoFlags = aoFlags;
	}
	
	/**
	 * a block is only clipped if every side is clipped
	 * @return 
	 */
	public byte getClipping() {
		return clipping;
	}
	
	/**
	 * a block is only clipped if every side is clipped
	 * @return 
	 */
	public boolean isClipped() {
		return clipping == 0b111;
	}
	
	public void setClippedLeft(){
		clipping |= 1;
	}
	
	public void setClippedTop(){
		clipping |= 1 << 1;
	}
	
	public void setClippedRight(){
		clipping |= 1 << 2;
	}

	public void setUnclipped() {
		clipping=(byte)0;
	}
}
