package com.BombingGames.Game.Blocks;

/**
 *
 * @author Benedikt
 */
public abstract class AnimatedBlock extends Block {
    
    private int delta = 0;

    public AnimatedBlock(int id) {
        super(id);
    }

    
    public void updateGFX(){
        delta++;
        if (delta>50)
            setValue(1);
        if (delta>100) {
            setValue(0);
            delta=0;
        }
        
    }

    
}
