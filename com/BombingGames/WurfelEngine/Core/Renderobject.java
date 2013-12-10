package com.BombingGames.WurfelEngine.Core;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;

/**
 *Saves the information for the rendering. This class is only used in the rendering process.
 * @author Benedikt
 */
public class Renderobject {
    private final AbstractPosition pos;
    private final int depth;
    private final AbstractGameObject content;

     /**
         * Create an Renderobject with a regular Block in the map
         * @param object 
         * @param pos The coordinates where the object should be rendered
         */
    protected Renderobject(AbstractGameObject object, AbstractPosition pos) {
        this.pos = pos;
        this.depth = object.getDepth(pos);
        content = object;
    }


    /**
     * 
     * @return
     */
    public int getDepth() {
        return depth;
    }

    /**
     * 
     * @return
     */
    public AbstractPosition getCoords() {
        return pos;
    }

    /**
     *
     * @return
     */
    public AbstractGameObject getObject() {
        return content;
    }

    
}