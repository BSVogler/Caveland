package com.bombinggames.caveland;

import com.bombinggames.wurfelengine.core.cvar.CVarFlags;
import com.bombinggames.wurfelengine.core.cvar.CVarSystemMap;
import com.bombinggames.wurfelengine.core.cvar.IntCVar;
import com.bombinggames.wurfelengine.core.map.CustomMapCVarRegistration;

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
	public void register(CVarSystemMap system) {
		system.register(new IntCVar(1), "groundBlockID", CVarFlags.ARCHIVE);
	}
	
}
