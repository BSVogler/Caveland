package com.bombinggames.caveland;

import com.bombinggames.wurfelengine.Core2.CVar.CVar;
import com.bombinggames.wurfelengine.Core2.CVar.CVarSystem;
import com.bombinggames.wurfelengine.Core2.CVar.IntCVar;
import com.bombinggames.wurfelengine.Core2.Map.CustomMapCVarRegistration;

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
