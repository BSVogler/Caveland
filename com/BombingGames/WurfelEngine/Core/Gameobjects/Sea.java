package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;

/**
 *A Sea Block which has a "waves" effect.
 * @author Benedikt Vogler
 */
public class Sea extends Block implements IsSelfAware{
    public static final int WAVE_AMPLITUDE = AbstractGameObject.GAME_DIMENSION-10;
    private static final float wavespeed = 1/700f; //the smaller the slower
    private static float currentX = 0;
    private final int waveWidth = Map.getBlocksX()/7;
    
    private float startvalue;
    
    private Coordinate coords;
        
    /**
     *
     * @param id
     * @param coords
     */
    public Sea(int id, Coordinate coords) {
        super(id);
        setTransparent(true);
        
        if (coords == null) throw new NullPointerException("No coordinates given to Sea-Block during creation."); 
        
        this.coords = coords;
        startvalue = (float) (coords.getCellOffset()[2] + Math.random()*WAVE_AMPLITUDE - WAVE_AMPLITUDE);
       
    }

    @Override
    public Coordinate getPos() {
        return coords;
    }

    @Override
    public void setPos(AbstractPosition pos) {
        this.coords = pos.getCoord();
    }

    @Override
    public void update(float delta) {
        coords.setCellOffsetZ(
            (int) (startvalue +
                Math.sin(
                    (currentX-coords.getRelX()-coords.getRelY())
                        * Math.PI/waveWidth
                )*WAVE_AMPLITUDE));
    }
    
    /**
     *
     * @param delta
     */
    public static void staticUpdate(float delta){
        currentX += delta*wavespeed;
    }

}