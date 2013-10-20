package com.BombingGames.EngineCore.Gameobjects;

import com.BombingGames.EngineCore.Controller;
import com.BombingGames.EngineCore.Map.AbstractPosition;
import com.BombingGames.EngineCore.Map.Coordinate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 *An example for a special block: barrel block which can explode
 * @author Benedikt
 */
public class ExplosiveBarrel extends Block implements IsSelfAware {
    /**Defines the radius of the explosion.*/
    public static final int RADIUS = 3;
    private Coordinate coords;
    private static Sound explosionsound;

    /**
     * Create a explosive barrel.
     * @param id the id of the explosive barrel
     * @param coords  The coordinates where this object get's placed.
     */
    protected ExplosiveBarrel(int id, Coordinate coords){
        super(id);
        if (coords == null) throw new NullPointerException("No coordinates given to ExplosiveBarrel during creation."); 
        this.coords = coords;
        setObstacle(true);
        if (explosionsound == null) explosionsound = Gdx.audio.newSound(Gdx.files.internal("com/BombingGames/Game/Sounds/explosion2.ogg"));
    }
    
    /**
     * Explodes the barrel.
     */
    public void explode(){
        for (int x=-RADIUS; x<RADIUS; x++)
            for (int y=-RADIUS*2; y<RADIUS*2; y++)
                for (int z=-RADIUS; z<RADIUS; z++){
                    //place air
                     if (x*x + (y/2)*(y/2)+ z*z < RADIUS*RADIUS){
                        Controller.getMap().setDataSafe(
                            coords.addVectorCpy(new float[]{x, y, z}).getCoordinate() , Block.getInstance(0)
                        );
                     }
                }
        
         for (int x=-RADIUS; x<RADIUS; x++)
            for (int y=-RADIUS*2; y<RADIUS*2; y++)
                for (int z=-RADIUS; z<RADIUS; z++){
                    
                    //spawn effect
                    if (x*x + (y/2)*(y/2)+ z*z >= RADIUS*RADIUS-4 &&
                        x*x + (y/2)*(y/2)+ z*z <= RADIUS*RADIUS){
                        AbstractEntity.getInstance(
                            41,
                            0,
                            coords.addVectorCpy(new float[]{x, y, z}).getPoint()
                        ).exist();
                    }
                }
         explosionsound.play();
         Controller.requestRecalc();
    }

    @Override
    public Coordinate getPos() {
        return coords;
    }

    @Override
    public void setPos(AbstractPosition pos) {
        this.coords = pos.getCoordinate();
    }
}
