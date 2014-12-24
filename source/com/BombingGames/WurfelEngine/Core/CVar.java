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
package com.BombingGames.WurfelEngine.Core;

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
public class CVar {
	/**
	 * true if currently reading. Prevent's saving
	 */
	private static boolean reading;

	/**
	 * @since v1.4.2
	 */
	public static enum CVarFlags {
		CVAR_ARCHIVE, CVAR_VOLATILE // never saved to file
	}
	
	/**
	 * @since v1.4.2
	 */
	private static enum Type {
		b, f, i
	}	
	
	/**global list of all CVars**/
	private static HashMap<String, CVar> cvars = new HashMap<>(50);
	
	private CVarFlags flags;
	private float defaultValue;
	private int valuei;
	private float valuef;
	private boolean valueb;
	private Type type;
	
	/**
	 * initializes engine cvars
	 */
	public static void initEngineVars(){
		register("gravity", 9.81f, CVarFlags.CVAR_ARCHIVE);
		register("worldSpinAngle", -40, CVarFlags.CVAR_ARCHIVE);
		register("LEAzimutSpeed", 0.0078125f, CVarFlags.CVAR_ARCHIVE);
		register("renderResolutionWidth", 1920, CVarFlags.CVAR_ARCHIVE);
		register("enableLightEngine", true, CVarFlags.CVAR_ARCHIVE);
		register("enableFog", true, CVarFlags.CVAR_ARCHIVE);
		register("enableAutoShade", true, CVarFlags.CVAR_ARCHIVE);
		register("enableScalePrototype", false, CVarFlags.CVAR_ARCHIVE);
		register("enableHSD", true, CVarFlags.CVAR_ARCHIVE);
		register("enableChunkSwitch", true, CVarFlags.CVAR_ARCHIVE);
		register("DevMode", true, CVarFlags.CVAR_ARCHIVE);
		register("DevDebugRendering", false, CVarFlags.CVAR_VOLATILE);
		register("groundBlockID", 2, CVarFlags.CVAR_ARCHIVE);
		register("preventUnloading", true, CVarFlags.CVAR_ARCHIVE);
		register("shouldLoadMap", true, CVarFlags.CVAR_ARCHIVE);
		register("clearBeforeRendering", true, CVarFlags.CVAR_ARCHIVE);
		register("KeyConsole", Keys.F1, CVarFlags.CVAR_ARCHIVE);
		register("KeySuggestion", Keys.TAB, CVarFlags.CVAR_ARCHIVE);
		register("music", 1f, CVarFlags.CVAR_ARCHIVE);
		register("sound", 1f, CVarFlags.CVAR_ARCHIVE);
		register("limitFPS",0, CVarFlags.CVAR_ARCHIVE);
		register("loadEntities", true, CVarFlags.CVAR_ARCHIVE);
		register("enableMinimap", false, CVarFlags.CVAR_ARCHIVE);
	}
	
	/**
	 * Registering should only be done by the game or the engine in init phase. Also saves as defaultValue.
	 * @param name indentifier name
	 * @param value the value of the cvar
	 * @param flags
	 * @since v1.4.2
	 */
	public static void register(String name, int value, CVarFlags flags){
		CVar cvar = new CVar();
		cvar.valuei = value;
		cvar.defaultValue = value;
		cvar.flags = flags;
		cvar.type = Type.i;
		cvars.put(name.toLowerCase().intern(), cvar);
	};
	
	/**
	 * Registering should only be done by the game or the engine in init phase. Also saves as defaultValue.
	 * @param name indentifier name. Saved in lower case.
	 * @param value the value of the cvar
	 * @param flags
	 * @since v1.4.2
	 */
	public static void register(String name, float value, CVarFlags flags){
		CVar cvar = new CVar();
		cvar.valuef = value;
		cvar.defaultValue = value;
		cvar.flags = flags;
		cvar.type = Type.f;
		cvars.put(name.toLowerCase().intern(), cvar);
	};
	
	/**
	 * Registering should only be done by the game or the engine in init phase. Also saves as defaultValue.
	 * @param name indentifier name
	 * @param value the value of the cvar
	 * @param flags
	 * @since v1.4.2
	 */
	public static void register(String name, boolean value, CVarFlags flags){
		CVar cvar = new CVar();
		cvar.valueb = value;
		if (value)
			cvar.defaultValue = 1;
		else
			cvar.defaultValue = 0;
		cvar.flags = flags;
		cvar.type = Type.b;
		cvars.put(name.toLowerCase().intern(), cvar);
	};
	
	/**
	 * tries to get the cvar.
	 * @param cvar indentifier name
	 * @return if not found returns null
	 * @since v1.4.2
	 */
	public static CVar get(String cvar){
		return cvars.get(cvar.toLowerCase().intern());
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
	
	public static void save(){
		if (!reading) {
			Writer writer = Gdx.files.absolute(WE.getWorkingDirectory()+"/engine.weconfig").writer(false);

			Iterator<Map.Entry<String, CVar>> it = cvars.entrySet().iterator();
			while (it.hasNext()) {

				Map.Entry<String, CVar> pairs = it.next();
				try {
					//if should be saved and different then default: save
					if (
						pairs.getValue().flags == CVarFlags.CVAR_ARCHIVE
						&& pairs.getValue().getDefaultAsFloat() != pairs.getValue().getValueAsFloat()
					)
						writer.write(pairs.getKey() + " "+pairs.getValue().toString()+"\n");

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

	/**
	 * 
	 * @return return the integer value of the cvar. if not of type i returns init value of int
	 * @since v1.4.2
	 */
	public int getValuei() {
		return valuei;
	}

	/**
	 * 
	 * @return returns the float value of the cvar. if not of type f returns init value of float
	 * @since v1.4.2
	 */
	public float getValuef() {
		return valuef;
	}
	
	/**
	 * 
	 * @return returns the boolean value of the cvar. if not of type b returns init value of boolean
	 * @since v1.4.2
	 */
	public boolean getValueb() {
		return valueb;
	}
	
	/**
	 * can be used for comparing the content.
	 * @return flaot representation of content
	 */
	private float getDefaultAsFloat() {
		return defaultValue;
	}

	/**
	 * can be used for comparing the content.
	 * @return returns the value as a float. true=1, false =0
	 */
	private float getValueAsFloat() {
		if (type==Type.f) return valuef;
		if (type==Type.i) return valuei;
		return valueb ? 1f : 0f;
	}
	
	/**
	 * Saves if should be archived.
	 * @param str 
	 * @since v1.4.2
	 */
	public void setValue(String str) {
		if (str.length()>0) {
			if (type==Type.i){
				valuei = Integer.parseInt(str);
			} else if (type==Type.f){
				valuef = Float.parseFloat(str);
			} else {
				valueb = str.equals("1") || str.equals("true");
			} 
			if (flags == CVarFlags.CVAR_ARCHIVE) save();
		}
	}

	/**
	 * Saves if should be archived.
	 * @param valuei 
	 */
	public void setValuei(int valuei) {
		this.valuei = valuei;
		if (flags == CVarFlags.CVAR_ARCHIVE) save();
	}

	/**
	 * Saves if should be archived.
	 * @param valuef 
	 */
	public void setValuef(float valuef) {
		this.valuef = valuef;
		if (flags == CVarFlags.CVAR_ARCHIVE) save();
	}

	/**
	 * Saves if should be archived.
	 * @param valueb 
	 */
	public void setValueb(boolean valueb) {
		this.valueb = valueb;
	}
	
	/**
	 * Prints content of cvar
	 * @return returns the value
	 */
	@Override
	public String toString() {
		if (type==Type.i) return Integer.toString(getValuei());
		if (type==Type.b) return getValueb() ? "1" : "0";
		return Float.toString(getValuef());
	}
	
}
