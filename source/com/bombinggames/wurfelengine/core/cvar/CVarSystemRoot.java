/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.bombinggames.wurfelengine.core.cvar;

import com.badlogic.gdx.Input;
import java.io.File;

/**
 *
 * @author Benedikt Vogler
 */
public class CVarSystemRoot extends AbstractCVarSystem {

	private CVarSystemMap mapSystem;

	/**
	 *
	 * @param path
	 * @since v1.6.7
	 */
	public CVarSystemRoot(File path) {
		super(path);
		System.out.println("Init Engine CVars…");
		register(new FloatCVar(9.81f), "gravity");
		register(new IntCVar(-40), "worldSpinAngle");
		register(new BooleanCVar(false), "loadPixmap");
		register(new FloatCVar(0.00078125f), "LEazimutSpeed");
		register(new BooleanCVar(false), "LEnormalMapRendering");
		register(new IntCVar(1920), "renderResolutionWidth");
		register(new BooleanCVar(true), "enableLightEngine");
		register(new BooleanCVar(true), "enableFog");
		register(new FloatCVar(0.3f), "fogR");
		register(new FloatCVar(0.4f), "fogG");
		register(new FloatCVar(1.0f), "fogB");
		register(new FloatCVar(2f), "fogOffset");
		register(new FloatCVar(0.17f), "fogFactor");
		register(new BooleanCVar(false), "enableAutoShade");
		register(new BooleanCVar(false), "enableScalePrototype");
		register(new BooleanCVar(true), "enableHSD");
		register(new BooleanCVar(true), "mapChunkSwitch");
		register(new BooleanCVar(true), "mapUseChunks");
		register(new BooleanCVar(false), "DevMode");
		register(new BooleanCVar(false), "DevDebugRendering");
		register(new BooleanCVar(false), "editorVisible");
		register(new IntCVar(2), "groundBlockID");
		register(new BooleanCVar(true), "preventUnloading");
		register(new BooleanCVar(true), "shouldLoadMap");
		register(new BooleanCVar(true), "clearBeforeRendering");
		register(new IntCVar(Input.Keys.F1), "KeyConsole");
		register(new IntCVar(Input.Keys.TAB), "KeySuggestion");
		register(new FloatCVar(1.0f), "music");
		register(new FloatCVar(1.0f), "sound");
		register(new IntCVar(60), "limitFPS");
		register(new BooleanCVar(true), "loadEntities");
		register(new BooleanCVar(false), "enableMinimap");
		register(new FloatCVar(1.0f), "walkingAnimationSpeedCorrection");
		register(new FloatCVar(4.0f), "playerWalkingSpeed");
		register(new FloatCVar(1f), "timeSpeed", CVarFlags.CVAR_VOLATILE);
		register(new FloatCVar(0.001f), "friction");
		register(new FloatCVar(0.03f), "playerfriction");
		register(new IntCVar(6000), "soundDecay");
		register(new IntCVar(4), "controllermacButtonStart");
		register(new IntCVar(5), "controllermacButtonSelect");
		register(new IntCVar(8), "controllermacButtonLB");
		register(new IntCVar(9), "controllermacButtonRB");
		register(new IntCVar(13), "controllermacButtonX");
		register(new IntCVar(12), "controllermacButtonB");
		register(new IntCVar(11), "controllermacButtonA");
		register(new IntCVar(14), "controllermacButtonY");
		register(new IntCVar(1), "controllermacAxisRT");
		register(new IntCVar(2), "controllermacAxisLX");
		register(new IntCVar(3), "controllermacAxisLY");
		register(new IntCVar(3), "controllermacAxisLY");
		register(new IntCVar(0), "controllerwindowsButtonA");
		register(new IntCVar(1), "controllerwindowsButtonB");
		register(new IntCVar(2), "controllerwindowsButtonX");
		register(new IntCVar(3), "controllerwindowsButtonY");
		register(new IntCVar(4), "controllerwindowsButtonLB");
		register(new IntCVar(5), "controllerwindowsButtonRB");
		register(new IntCVar(6), "controllerwindowsButtonSelect");
		register(new IntCVar(7), "controllerwindowsButtonStart");
		register(new IntCVar(0), "controllerwindowsAxisLY");
		register(new IntCVar(1), "controllerwindowsAxisLX");
		register(new IntCVar(3), "controllerwindowsAxisLT");
		register(new IntCVar(4), "controllerwindowsAxisRT");
		register(new IntCVar(4), "controllerlinuxButtonStart");
		register(new IntCVar(5), "controllerlinuxButtonSelect");
		register(new IntCVar(8), "controllerlinuxButtonLB");
		register(new IntCVar(9), "controllerlinuxButtonRB");
		register(new IntCVar(11), "controllerlinuxButtonX");
		register(new IntCVar(12), "controllerlinuxButtonB");
		register(new IntCVar(13), "controllerlinuxButtonA");
		register(new IntCVar(14), "controllerlinuxButtonY");
		register(new IntCVar(1), "controllerlinuxAxisRT");
		register(new IntCVar(2), "controllerlinuxAxisLX");
		register(new IntCVar(3), "controllerlinuxAxisLY");
		register(new IntCVar(3), "controllerlinuxAxisLY");
		register(new IntCVar(0), "resolutionX");
		register(new IntCVar(0), "resolutionY");
		register(new IntCVar(3500), "MaxSprites");
		register(new IntCVar(90), "CameraLeapRadius");
		register(new FloatCVar(0.5f), "ambientOcclusion");
		register(new FloatCVar(200), "MaxDelta");//skip delta if under 5 FPS to prevent glitches
		register(new StringCVar(""),"loadedMap", CVarFlags.CVAR_VOLATILE); 
		register(new StringCVar(""), "lastConsoleCommand");
		register(new IntCVar(20), "undohistorySize");
		register(new IntCVar(500), "mapIndexSpaceSize");
		register(new IntCVar(536870912), "mapMaxMemoryUse");//bytes, 512MB->17,9km^2
		register(new BooleanCVar(false), "showMiniMapChunk");
	}

	public CVarSystemMap getMapCVars() {
		return mapSystem;
	}

	public CVarSystemSave getSaveCVars() {
		if (mapSystem == null) {
			return null;
		}
		return mapSystem.getSaveCVars();
	}
	
	public void setMapCVars(CVarSystemMap mapSystem){
		this.mapSystem = mapSystem;
	}

}
