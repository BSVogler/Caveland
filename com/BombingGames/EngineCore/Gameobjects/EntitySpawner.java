package com.BombingGames.EngineCore.Gameobjects;

import com.BombingGames.EngineCore.Controller;
import com.BombingGames.EngineCore.GameplayScreen;
import com.BombingGames.EngineCore.Map.AbstractPosition;
import com.BombingGames.EngineCore.Map.Coordinate;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *The entitty spawner spawns an entity when a character steps on it.
 * @author Benedikt Vogler
 */
public class EntitySpawner extends Block implements IsSelfAware {
    private Coordinate coords;//this field is needed because of it is selfAware
    private boolean up = true;

    /**
     *
     * @param id
     * @param coords
     */
    protected EntitySpawner(int id, Coordinate coords){
        super(id);
        if (coords == null) throw new NullPointerException("No coordinates given to EntitySpawner during creation."); 
        this.coords = coords;
        setObstacle(true);
    }
    

    @Override
    public void update(float delta) {
        int[] coordsOnTop = coords.addVectorCpy(new float[]{0, 0, 1}).getCoordinate().getRel();
        
        //get every character
        ArrayList<AbstractCharacter> entitylist;
        entitylist = Controller.getMap().getAllEntitysOfType(AbstractCharacter.class);
        
        //check every character if standing on top
        int i = 0;
        while (i < entitylist.size() && !Arrays.equals( entitylist.get(i).getPos().getCoordinate().getRel(), coordsOnTop)){
            i++;
        }
        
        if (i < entitylist.size() && Arrays.equals(entitylist.get(i).getPos().getCoordinate().getRel(), coordsOnTop)) {
            if (up) trigger();
            up = false;
        } else {
            up = true;
        }
    }

    @Override
    public AbstractPosition getPos() {
        return coords;
    }


    private void trigger() {
        GameplayScreen.msgSystem().add("You are standing on: " + coords.getRelX() +"," + coords.getRelY() +","+ coords.getZ(), "System");
        AbstractEntity.getInstance(41, 0, coords.addVectorCpy(new float[]{0, 2, 1}).getPoint()).exist();
    }

    @Override
    public void setPos(AbstractPosition pos) {
        coords = pos.getCoordinate();
    }
}
