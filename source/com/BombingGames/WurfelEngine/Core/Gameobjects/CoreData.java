package com.BombingGames.WurfelEngine.Core.Gameobjects;

/**
 *A small block object hich stores only id and value and is only used for storing in memory. Stores only 8 byte.
 * @author Benedikt Vogler
 */
public class CoreData implements HasID {
	private static BlockFactory customBlockFactory;
	
		/**
	 * If you want to define custo id's >39
	 *
	 * @param customBlockFactory new value of customBlockFactory
	 */
	public static void setCustomBlockFactory(BlockFactory customBlockFactory) {
		CoreData.customBlockFactory = customBlockFactory;
	}
	
	
	private int identifier;
	private float lightlevel = 1f;//saved here because it saves recalcualtion for every camera


	public CoreData(int id) {
		this.identifier = id;
	}
	
	public CoreData(int id, int value) {
		this.identifier = (value<<8)+id;
	}
	
	@Override
	public int getId() {
		return identifier%256;
	}

	@Override
	public int getValue() {
		return (identifier>>8)%256;
	}
	
	/**
	 * creates a new RenderBlock instance based on he data
	 * @return 
	 */
	public RenderBlock toBlock(){
		return new RenderBlock(identifier, getValue());
	}

	@Override
	public boolean isObstacle() {
		//todo
		return getId() != 0;
	}

	@Override
	public boolean isTransparent() {
		//todo
		return false;
	}
	
    /**
     * Check if the block is liquid.
     * @return true if liquid, false if not 
     */
	public boolean isLiquid() {
		if (getId()==9) return true;
		return false;
	}
	
    /**
     * Is the block a true block with sides or represents it another thing like a flower?
     * @return 
     */
	public boolean hasSides() {
		int id = getId();
		if (id==34 ||id==35 || id==36) return false;
//todo
		return true;
	}

	@Override
	public float getLightlevel() {
		return lightlevel;
	}

	@Override
	public void setLightlevel(float lightlevel) {
		this.lightlevel = lightlevel;
	}
	
	public static boolean hasSides(int id, int value){
		return new CoreData(id, value).hasSides();
	}

	public void setValue(int value) {
		this.identifier = (value<<8)+getValue();
	}
	
	/**
	 * get the name of a combination of id and value
	 * @param id
	 * @param value
	 * @return 
	 */
	public static String getName(final int id, final int value){
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
			case 10:
				return "???";
			case 34:
				return "flower";
			case 35:
				return "bush";
			case 36:
				return "tree";
								
			default:
				if (id > 39) {
                    if (customBlockFactory!=null){
                        return customBlockFactory.getName(id, value);
                    } else {
                        return "no custom blocks";
                    }
                } else {
                    return "engine block not properly defined";
                }
		}
	}
}
