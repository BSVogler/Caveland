package com.bombinggames.caveland;

import com.bombinggames.wurfelengine.core.CVar.CVar;
import com.bombinggames.wurfelengine.core.CVar.CVarSystem;
import com.bombinggames.wurfelengine.core.CVar.IntCVar;
import com.bombinggames.wurfelengine.core.Map.CustomMapCVarRegistration;

/**
 *
 * @author Benedikt Vogler
 */
public class CavelandMapCVars implements CustomMapCVarRegistration{

	/**
	 *
	 * @param system
	 */
	@Override
	public void register(CVarSystem system) {
		system.register( new IntCVar(1), "groundBlockID", CVar.CVarFlags.CVAR_ARCHIVE);
	}
	
}
