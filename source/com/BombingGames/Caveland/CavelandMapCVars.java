package com.BombingGames.Caveland;

import com.BombingGames.WurfelEngine.Core.CVar.CVar;
import com.BombingGames.WurfelEngine.Core.CVar.CVarSystem;
import com.BombingGames.WurfelEngine.Core.CVar.IntCVar;
import com.BombingGames.WurfelEngine.Core.Map.CustomMapCVarRegistration;

/**
 *
 * @author Benedikt Vogler
 */
public class CavelandMapCVars implements CustomMapCVarRegistration{

	@Override
	public void register(CVarSystem system) {
		system.register( new IntCVar(1), "groundBlockID", CVar.CVarFlags.CVAR_ARCHIVE);
	}
	
}
