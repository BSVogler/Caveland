package com.BombingGames.EngineCore.Gameobjects;

import com.BombingGames.EngineCore.Map.AbstractPosition;

/**
 *An object that knows his own position IsSelfAware.
 * @author Benedikt
 */
public interface IsSelfAware{
   /**
     * Return the coordinates of the SelfAware object.
     * @return the coordinates where the object is located
     */
    public AbstractPosition getPos();
    
    /**
     * Set the coordinates without safety check.
     * @param pos the coordinates you want to set
     */
    public void setPos(AbstractPosition pos);
}