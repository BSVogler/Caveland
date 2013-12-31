package com.BombingGames.WurfelEngine.Game;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.IsSelfAware;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;


/**
 *
 * @author Benedikt Vogler
 */
public class AirLift extends Block implements IsSelfAware {
    private Coordinate coords;

    public AirLift(Coordinate coords, int id) {
        super(id);
        this.coords = coords;
        setObstacle(true);
    }

    @Override
    public void update(float delta) {
        Coordinate topblock = coords.cpy().addVector(0, 0, 1);
        if (topblock.getBlock().getId() != 0)
            topblock.setCellOffsetZ((int) (topblock.getCellOffset()[2]+delta/8f));
    }


    @Override
    public AbstractPosition getPos() {
        return coords;
    }

    @Override
    public void setPos(AbstractPosition pos) {
        coords = pos.getCoord();
    }
}
