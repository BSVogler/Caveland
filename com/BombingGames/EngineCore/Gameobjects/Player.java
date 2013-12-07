package com.BombingGames.EngineCore.Gameobjects;

import com.BombingGames.EngineCore.Map.Point;
import com.BombingGames.WurfelEngine;
import com.badlogic.gdx.Gdx;


/**
 *The Player is a character who can walk.
 * @author Benedikt
 */
public class Player extends AbstractCharacter{   
    /**
     * Creates a player. The parameters are for the lower half of the player. The constructor automatically creates a block on top of it.
     * @param id 
     * @param point 
     * @see com.BombingGames.Game.Gameobjects.Block#getInstance(int) 
     */
    public Player(int id, Point point) {
        super(id, 1, point);
        Gdx.app.debug("Player", "Creating player");
        setFallingSound((com.badlogic.gdx.backends.openal.Ogg.Sound)
            WurfelEngine.getInstance().manager.get("com/BombingGames/Game/Sounds/wind.ogg"));
        setRunningSound(
            (com.badlogic.gdx.backends.openal.Ogg.Sound)
                WurfelEngine.getInstance().manager.get("com/BombingGames/Game/Sounds/victorcenusa_running.ogg"));
        setJumpingSound((com.badlogic.gdx.backends.openal.Wav.Sound)
            WurfelEngine.getInstance().manager.get("com/BombingGames/Game/Sounds/jump_man.wav"));
        setLandingSound((com.badlogic.gdx.backends.openal.Wav.Sound)
            WurfelEngine.getInstance().manager.get("com/BombingGames/Game/Sounds/landing.wav"));
        
        setTransparent(true);
        setObstacle(true);
        setDimensionZ(2);
    }   

    /**
     * Jumps the player
     */
    @Override
    public void jump() {
        super.jump(5);
    }
}