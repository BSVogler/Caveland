/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2014 Benedikt Vogler.
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
package com.BombingGames.WurfelEngine.Core.CVar;

import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedReader;
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
 *CVars start with a small letter and are CamelCase.
 * @author Benedikt Vogler
 * @since v1.4.2
 */
public abstract class CVar {
	/**
	 * true if currently reading. Prevents saving
	 */
	private static boolean reading;

	/**
	 * @since v1.4.2
	 */
	public static enum CVarFlags {
		/**
		 * does save changes to file
		*/
		CVAR_ARCHIVE, 
		/**
		 * never saved to file
		 */
		 CVAR_VOLATILE
	}
	

	
	/**global list of all CVars**/
	private static HashMap<String, CVar> cvars = new HashMap<>(50);
	
	protected CVarFlags flags;
	protected String name;
	
	/**
	 * initializes engine cvars
	 */
	public static void initEngineVars(){
		new FloatCVar(9.81f).register("gravity", CVarFlags.CVAR_ARCHIVE);
		new IntCVar(-40).register("worldSpinAngle", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(false).register("loadPixmap", CVarFlags.CVAR_ARCHIVE);
		new FloatCVar(0.00078125f).register("LEazimutSpeed", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(true).register("LEnormalMapRendering", CVarFlags.CVAR_ARCHIVE);
		new IntCVar(1920).register("renderResolutionWidth", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(true).register("enableLightEngine", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(true).register("enableFog", CVarFlags.CVAR_ARCHIVE);
		new FloatCVar(0.3f).register("fogR", CVarFlags.CVAR_ARCHIVE);
		new FloatCVar(0.4f).register("fogG", CVarFlags.CVAR_ARCHIVE);
		new FloatCVar(1.0f).register("fogB", CVarFlags.CVAR_ARCHIVE);
		new FloatCVar(2f).register("fogOffset", CVarFlags.CVAR_ARCHIVE);
		new FloatCVar(0.17f).register("fogFactor", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(false).register("enableAutoShade", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(false).register("enableScalePrototype", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(true).register("enableHSD", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(true).register("mapChunkSwitch", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(true).register("mapUseChunks", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(false).register("DevMode", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(false).register("DevDebugRendering", CVarFlags.CVAR_ARCHIVE);
		new IntCVar(2).register("groundBlockID", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(true).register("preventUnloading", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(true).register("shouldLoadMap", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(true).register("clearBeforeRendering", CVarFlags.CVAR_ARCHIVE);
		new IntCVar(Keys.F1).register("KeyConsole", CVarFlags.CVAR_ARCHIVE);
		new IntCVar(Keys.TAB).register("KeySuggestion", CVarFlags.CVAR_ARCHIVE);
		new FloatCVar(1.0f).register("music", CVarFlags.CVAR_ARCHIVE);
		new FloatCVar(1.0f).register("sound", CVarFlags.CVAR_ARCHIVE);
		new IntCVar(60).register("limitFPS", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(true).register("loadEntities", CVarFlags.CVAR_ARCHIVE);
		new BooleanCVar(false).register("enableMinimap", CVarFlags.CVAR_ARCHIVE);
		new FloatCVar(1.0f).register("walkingAnimationSpeedCorrection", CVarFlags.CVAR_ARCHIVE);
		new FloatCVar(4.0f).register("playerWalkingSpeed", CVarFlags.CVAR_ARCHIVE);
		new FloatCVar(1f).register("timeSpeed", CVarFlags.CVAR_VOLATILE);
		new FloatCVar(0.001f).register("friction", CVarFlags.CVAR_ARCHIVE);
		new FloatCVar(0.03f).register("playerfriction", CVarFlags.CVAR_ARCHIVE);
		new IntCVar(6000).register("soundDecay", CVarFlags.CVAR_ARCHIVE);
		
	}
	
	public abstract Object getValue();
	public abstract void setValue(Object value);
	public abstract Object getDefaultValue();
	protected abstract void setDefaultValue(Object value);
	
	public String getName(){
		return name;
	}
	
	@Override
	public abstract String toString();
	
	/**
	 * Registering should only be done by the game or the engine in init phase. Also saves as defaultValue.
	 * if already registered updates the default and current value.
	 * @param name
	 * @param flag
	 * @since v1.4.2
	 */
	public void register(String name, CVarFlags flag){
		this.name = name;
		this.flags = flag;
		//if already registered new value is set
		if (cvars.containsKey(name))
			cvars.get(name).setDefaultValue(this.getValue());
		else
			cvars.put(name.toLowerCase(), this);
	}
	
	
	/**
	 * tries to get the cvar.
	 * @param cvar indentifier name
	 * @return if not found returns null
	 * @since v1.4.2
	 */
	public static CVar get(String cvar){
		return cvars.get(cvar.toLowerCase());
	}
	
	public static boolean getValueB(String cvar){
		return (boolean) cvars.get(cvar.toLowerCase()).getValue();
	}
	
	public static int getValueI(String cvar){
		return (int) cvars.get(cvar.toLowerCase()).getValue();
	}
	
	public static float getValueF(String cvar){
		return (float) cvars.get(cvar.toLowerCase()).getValue();
	}
	
	public static String getValueS(String cvar){
		return (String) cvars.get(cvar.toLowerCase()).getValue();
	}
	
	
	/**
	 * load CVars from file and overwrite engine cvars
	 * @since v1.4.2
	 */
	public static void loadFromFile(){
		reading = true;
		FileHandle sourceFile = new FileHandle(WE.getWorkingDirectory()+"/engine.weconfig");
		if (sourceFile.exists()) {
			try {
				BufferedReader reader = sourceFile.reader(300);
				String line = reader.readLine();
				while (line!=null) {
					StringTokenizer tokenizer = new StringTokenizer(line, " ");
					String name = tokenizer.nextToken();
					String data = tokenizer.nextToken();
					if (CVar.get(name)!=null){//only overwrite if already set
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
		} else System.out.println("Custom CVar file not found.");
		reading = false;
	
	}
	
	/**
	 * saves the cvars with the flag to file
	 * @since v1.4.2
	 */
	public static void dispose(){
		save();
	}
	
	/**
	 * saves CVars to file
	 */
	public static void save(){
		if (!reading) {
			Writer writer = Gdx.files.absolute(WE.getWorkingDirectory()+"/engine.weconfig").writer(false);

			Iterator<Map.Entry<String, CVar>> it = cvars.entrySet().iterator();
			while (it.hasNext()) {

				Map.Entry<String, CVar> pairs = it.next();
				CVar cvar = pairs.getValue();
				try {
					//if should be saved and different then default: save
					if (
						cvar.flags == CVarFlags.CVAR_ARCHIVE
						&& cvar.getDefaultValue() != cvar.getValue()
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
	public static ArrayList<String> getSuggestions(String prefix){
		ArrayList<String> resultList = new ArrayList<>(1);
		Iterator<Map.Entry<String, CVar>> it = cvars.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, CVar> cvarEntry = it.next();
			if (cvarEntry.getKey().startsWith(prefix.toLowerCase()))
				resultList.add(cvarEntry.getKey());
		}
		return resultList;
	}
}
