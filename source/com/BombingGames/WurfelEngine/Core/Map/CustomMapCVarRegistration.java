package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.CVar.CVarSystem;

/**
 * An interface to register custom cvars for the map cvar system. They msut be registered before being updated by loading from file.
 * @author Benedikt Vogler
 */
public interface CustomMapCVarRegistration {
	public void register(CVarSystem styem);
}
