package com.bombinggames.caveland;

import com.bombinggames.wurfelengine.Core.CVar.CVar;
import com.bombinggames.wurfelengine.Core.CVar.CVarSystem;
import com.bombinggames.wurfelengine.Core.CVar.IntCVar;
import com.bombinggames.wurfelengine.Core.Map.CustomMapCVarRegistration;

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
