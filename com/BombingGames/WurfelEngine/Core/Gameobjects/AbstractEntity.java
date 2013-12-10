package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Point;

/**
 *An entity is a game object wich is self aware that means it knows it's position.
 * @author Benedikt
 */
public abstract class AbstractEntity extends AbstractGameObject implements IsSelfAware {
       /**
     *
     */
    public static final char CATEGORY = 'e';
   
    /**Containts the names of the objects. index=id*/
    public static final String[] NAMELIST = new String[OBJECTTYPESCOUNT]; 
    
    /** A list containing the offset of the objects. */
    public static final int[][][] OFFSET = new int[OBJECTTYPESCOUNT][VALUESCOUNT][2];
    
    private Point point;//the position in the map-grid
   
    static {
        NAMELIST[40] = "player";
        OFFSET[40][0][0] = 54-80;
        OFFSET[40][0][1] = 37-40;
        OFFSET[40][1][0] = 55-80;
        OFFSET[40][1][1] = 38-40;
        OFFSET[40][2][0] = 53-80;
        OFFSET[40][2][1] = 35-40;
        OFFSET[40][3][0] = 46-80;
        OFFSET[40][3][1] = 33-40;
        OFFSET[40][4][0] = 53-80;
        OFFSET[40][4][1] = 35-40;
        OFFSET[40][5][0] = 64-80;
        OFFSET[40][5][1] = 33-40;
        OFFSET[40][6][0] = 53-80;
        OFFSET[40][6][1] = 33-40;
        OFFSET[40][7][0] = 46-80;
        OFFSET[40][7][1] = 33-40;
        NAMELIST[41] = "smoke test";
        NAMELIST[42] = "character shadow";
        OFFSET[42][0][0] = -80;
        OFFSET[42][0][1] = 40;
    }
    
    private boolean destroy;
   
    /**
     * Create an abstractEntity. You should use Block.getInstance(int) 
     * @param id 
     * @see com.BombingGames.Game.Gameobjects.Block#getInstance(int) 
     */
    protected AbstractEntity(int id){
        super(id,0);
    }
    
    /**
     * Create an entity through this factory method..
     * @param id the object id of the entity.
     * @param value The value at start.
     * @param point The coordiantes where you place it.
     * @return the entity.
     */
    public static AbstractEntity getInstance(int id, int value, Point point){
        AbstractEntity entity;
        //define the default SideSprites
        switch (id){
            case 40:
                    entity = new Player(id, point);
                    break;
            case 41: //explosion
                    entity = new AnimatedEntity(
                                id,
                                value,
                                new int[]{700,2000},
                                true,
                                false
                            );
                    break;
            case 42: entity = new CharacterShadow(id);
                    break;
             
            default: entity = new SimpleEntity(id);
        }
        
        entity.setPos(point);
        entity.setValue(value);
        return entity;
    }
    
    @Override
    public int getDepth(AbstractPosition pos){
        return (int) (
            pos.getPoint().getRelY()//Y
            
            + pos.getHeight()/Math.sqrt(2)//Z
            + (getDimensionZ() - 1) * GAME_DIMENSION/6/Math.sqrt(2)
        );
    }
    
    //IsSelfAware implementation
    @Override
    public Point getPos() {
        return point;
    }

    @Override
    public void setPos(AbstractPosition pos) {
        this.point = pos.getPoint();
    }
    
    /**
     * 
     * @param height 
     */
    public void setHeight(float height) {
        point.setHeight(height);
    }
    
  
    /**
     * Is the entity laying/standing on the ground?
     * @return true when on the ground
     */
    public boolean onGround(){
        if (getPos().getHeight() <= 0) return true; //if entity is under the map
        
        //check if one pixel deeper is on ground.
        int z = (int) ((getPos().getHeight()-1)/GAME_DIMENSION);
        if (z > Map.getBlocksZ()-1) z = Map.getBlocksZ()-1;
        
        return new Coordinate(point.getCoord().getRelX(), point.getCoord().getRelY(), z, true).getBlock().isObstacle();
    }
    
    /**
     * add this entity to the map-> let it exist
     */
    public void exist(){
        Controller.getMap().getEntitys().add(this);
    }
  
    /**
     *
     * @return
     */
    @Override
    public char getCategory() {
        return CATEGORY;
    } 
    
    @Override
    public String getName() {
        return NAMELIST[getId()];
    }
    
    /**
     *The offset is the offset of the sprite image.
     * @return
     */
    @Override
    public int getOffsetX() {
        return OFFSET[getId()][getValue()][0];
    }

    /**
     *The offset is the offset of the sprite image.
     * @return
     */
    @Override
    public int getOffsetY() {
        return OFFSET[getId()][getValue()][1];
    } 
    
   /**
     * Deletes the object from the map. The opposite to exist();
     */
    public void destroy(){
        destroy=true;
    }

    /**
     *
     * @return
     */
    public boolean shouldBeDestroyed() {
        return destroy;
    }
}