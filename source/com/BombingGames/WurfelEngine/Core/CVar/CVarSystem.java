/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
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
package com.BombingGames.WurfelEngine.Core.CVar;

import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benedikt Vogler
 */
public class CVarSystem {
		/**
	 * true if currently reading. Prevents saving
	 */
	private boolean reading;
	private File path;
	/**global list of all CVars**/
	private HashMap<String, CVar> cvars = new HashMap<>(50);

	/**
	 * you have to manually call {@link #load} to load from path.
	 * @param path path to the .cvar file
	 */
	public CVarSystem(File path) {
		this.path = path;
	}

	public CVar get(String cvar){
		return cvars.get(cvar.toLowerCase());
	}
	
	public boolean getValueB(String cvar){
		try {
			return (boolean) cvars.get(cvar.toLowerCase()).getValue();
		} catch(NullPointerException ex){
			throw new NullPointerException("Cvar \""+cvar+"\" not defined.");
		}
	}
	
	public int getValueI(String cvar){
		try {
			return (int) cvars.get(cvar.toLowerCase()).getValue();
		} catch(NullPointerException ex){
			throw new NullPointerException("Cvar \""+cvar+"\" not defined.");
		}
	}
	
	public float getValueF(String cvar){
		try {
			return (float) cvars.get(cvar.toLowerCase()).getValue();
		} catch(NullPointerException ex){
			throw new NullPointerException("Cvar \""+cvar+"\" not defined.");
		}
	}
	
	public String getValueS(String cvar){
		try {
			return (String) cvars.get(cvar.toLowerCase()).getValue();
		} catch(NullPointerException ex){
			throw new NullPointerException("Cvar \""+cvar+"\" not defined.");
		}
	}
	
	/**
	 * load CVars from file and overwrite engine cvars. You must register the cvars first.
	 * @since v1.4.2
	 */
	public void load(){
		reading = true;
		FileHandle sourceFile = new FileHandle(path);
		if (sourceFile.exists() && !sourceFile.isDirectory()) {
			try {
				BufferedReader reader = sourceFile.reader(300);
				String line = reader.readLine();
				while (line!=null) {
					StringTokenizer tokenizer = new StringTokenizer(line, " ");
					String name = tokenizer.nextToken();
					String data = tokenizer.nextToken();
					if (get(name)!=null){//only overwrite if already registered
						get(name).setValue(data);
						System.out.println("Set CVar "+name+": "+data);
					}
					line = reader.readLine();
				}

			} catch (FileNotFoundException ex) {
				Logger.getLogger(CVar.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(CVar.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			System.out.println("Custom CVar file "+path+" not found. Creating new one at the same place.");
			try {
				path.createNewFile();
			} catch (IOException ex) {
				System.out.println("Could not create file at "+path+".");
			}
		}
		reading = false;
	
	}
	
		/**
	 * saves the cvars with the flag to file
	 * @since v1.4.2
	 */
	public void dispose(){
		save();
	}
	
	/**
	 * saves CVars to file
	 */
	public void save(){
		if (!reading) {
			Writer writer = Gdx.files.absolute(path.getAbsolutePath()).writer(false);

			Iterator<Map.Entry<String, CVar>> it = cvars.entrySet().iterator();
			while (it.hasNext()) {

				Map.Entry<String, CVar> pairs = it.next();
				CVar cvar = pairs.getValue();
				try {
					//if should be saved and different then default: save
					if (
						cvar.flags == CVar.CVarFlags.CVAR_ARCHIVE
						&& !cvar.getDefaultValue().equals(cvar.getValue())
					)
						writer.write(pairs.getKey() + " "+cvar.toString()+"\n");

				} catch (IOException ex) {
					Logger.getLogger(CVar.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			try {
				writer.close();
			} catch (IOException ex) {
				Logger.getLogger(CVar.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	/**
	 * Good use is auto-complete suggestions.
	 * @param prefix some chars with which the cvar begins.
	 * @return A list containing every cvar starting with the prefix
	 */
	public ArrayList<String> getSuggestions(String prefix){
		ArrayList<String> resultList = new ArrayList<>(1);
		Iterator<Map.Entry<String, CVar>> it = cvars.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, CVar> cvarEntry = it.next();
			if (cvarEntry.getKey().startsWith(prefix.toLowerCase()))
				resultList.add(cvarEntry.getKey());
		}
		return resultList;
	}
	
	/**
	 * Registering should only be done by the game or the engine in init phase. Also saves as defaultValue.
	 * if already registered updates the default and current value.
	 * @param cvar
	 * @param name
	 * @param flag
	 * @since v1.4.2
	 */
	public void register(CVar cvar, String name, CVar.CVarFlags flag){
		cvar.register(name, flag, this);
		//if already registered new value is set
		if (cvars.containsKey(cvar.name))
			get(cvar.name).setDefaultValue(cvar.getValue());
		else
			cvars.put(cvar.name.toLowerCase(), cvar);
	}
	
	/*
	 * initializes engine cvars
	 */
	public void initEngineCVars(){
		WE.CVARS.register(new FloatCVar(9.81f), "gravity", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(-40), "worldSpinAngle", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(false), "loadPixmap", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(0.00078125f), "LEazimutSpeed", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(false), "LEnormalMapRendering", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(1920), "renderResolutionWidth", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(true), "enableLightEngine", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(true), "enableFog", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(0.3f), "fogR", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(0.4f), "fogG", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(1.0f), "fogB", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(2f), "fogOffset", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(0.17f), "fogFactor", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(false), "enableAutoShade", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(false), "enableScalePrototype", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(true), "enableHSD", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(true), "mapChunkSwitch", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(true), "mapUseChunks", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(false), "DevMode", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(false), "DevDebugRendering", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(2), "groundBlockID", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(true), "preventUnloading", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(true), "shouldLoadMap", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(true), "clearBeforeRendering", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(Input.Keys.F1), "KeyConsole", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(Input.Keys.TAB), "KeySuggestion", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(1.0f), "music", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(1.0f), "sound", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(60), "limitFPS", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(true), "loadEntities", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(false), "enableMinimap", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(1.0f), "walkingAnimationSpeedCorrection", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(4.0f), "playerWalkingSpeed", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(1f), "timeSpeed", CVar.CVarFlags.CVAR_VOLATILE);
		WE.CVARS.register(new FloatCVar(0.001f), "friction", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(0.03f), "playerfriction", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(6000), "soundDecay", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(4), "controllermacButtonStart", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(5), "controllermacButtonSelect", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(8), "controllermacButtonLB", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(9), "controllermacButtonRB", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(11), "controllermacButtonX", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(12), "controllermacButtonB", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(13), "controllermacButtonA", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(14), "controllermacButtonY", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(1), "controllermacAxisRT", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(2), "controllermacAxisLX", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(3), "controllermacAxisLY", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(3), "controllermacAxisLY", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(0), "controllerwindowsButtonA", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(1), "controllerwindowsButtonB", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(2), "controllerwindowsButtonX", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(3), "controllerwindowsButtonY", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(4), "controllerwindowsButtonLB", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(5), "controllerwindowsButtonRB", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(6), "controllerwindowsButtonSelect", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(7), "controllerwindowsButtonStart", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(0), "controllerwindowsAxisLY", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(1), "controllerwindowsAxisLX", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(3), "controllerwindowsAxisLT", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(4), "controllerwindowsAxisRT", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(4), "controllerlinuxButtonStart", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(5), "controllerlinuxButtonSelect", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(8), "controllerlinuxButtonLB", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(9), "controllerlinuxButtonRB", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(11), "controllerlinuxButtonX", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(12), "controllerlinuxButtonB", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(13), "controllerlinuxButtonA", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(14), "controllerlinuxButtonY", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(1), "controllerlinuxAxisRT", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(2), "controllerlinuxAxisLX", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(3), "controllerlinuxAxisLY", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(3), "controllerlinuxAxisLY", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new IntCVar(3500), "MaxSprites", CVar.CVarFlags.CVAR_ARCHIVE);
	}
}
