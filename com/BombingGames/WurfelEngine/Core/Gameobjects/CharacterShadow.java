package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.LightEngine.PseudoGrey;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.Core.WECamera;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Benedikt Vogler
 */
class CharacterShadow extends AbstractEntity {
    private AbstractCharacter character;

    protected CharacterShadow(int id) {
        super(id);
    }

    @Override
    public void update(float delta) {
    }
    
    public void update(float delta, AbstractCharacter character){
        this.character = character;
        Coordinate tmpPos = character.getPos().getCoord().cpy();
        tmpPos.setZ(tmpPos.getZ());
        while (tmpPos.getZ() > 0 && tmpPos.cpy().addVector(new float[]{0, 0, -1}).getBlockSafe().isTransparent())
            tmpPos.addVector(new float[]{0, 0, -1});
        
        setPos(character.getPos().cpy());
        getPos().setHeight(tmpPos.getHeight());
    }

    @Override
    public void render(View view, WECamera camera, AbstractPosition coords) {
        Color color = PseudoGrey.toColor(
                (character.getPos().getHeight() - getPos().getHeight())/Block.GAME_DIMENSION
                );//make color out of distance from player
        super.render(view, camera, coords,color);
    }
}