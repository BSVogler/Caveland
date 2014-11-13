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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benedikt Vogler
 * @since v1.4.2
 */
public class CVar {

	/**
	 * @since v1.4.2
	 */
	public enum CVarFlags {
		CVAR_ARCHIVE, CVAR_VOLATILE // never saved to file
	}
	
	/**
	 * @since v1.4.2
	 */
	private enum Type {
		b, f, i
	}	
	
	/**global list of all CVars**/
	private static HashMap<String, CVar> cvars = new HashMap<>(50);
	
	private CVarFlags flags;
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
		register("groundBlockID", 2, CVarFlags.CVAR_ARCHIVE);
		register("debugObjects", false, CVarFlags.CVAR_ARCHIVE);
		register("preventUnloading", true, CVarFlags.CVAR_ARCHIVE);
		register("shouldLoadMap", true, CVarFlags.CVAR_ARCHIVE);
		register("chunkSwitchAllowed", true, CVarFlags.CVAR_ARCHIVE);
		register("clearBeforeRendering", true, CVarFlags.CVAR_ARCHIVE);
		register("consoleKey", 244, CVarFlags.CVAR_ARCHIVE);//Keys.F1
		register("music", 1f, CVarFlags.CVAR_ARCHIVE);
		register("sound", 1f, CVarFlags.CVAR_ARCHIVE);
		register("gamespeed", 1f, CVarFlags.CVAR_ARCHIVE);
	}
	
	/**
	 * 
	 * @param name indentifier name
	 * @param value
	 * @param flags
	 * @since v1.4.2
	 */
	public static void register(String name, int value, CVarFlags flags){
		CVar cvar = new CVar();
		cvar.valuei = value;
		cvar.flags = flags;
		cvar.type = Type.i;
		cvars.put(name.intern(), cvar);
	};
	
	/**
	 * 
	 * @param name indentifier name
	 * @param value
	 * @param flags
	 * @since v1.4.2
	 */
	public static void register(String name, float value, CVarFlags flags){
		CVar cvar = new CVar();
		cvar.valuef = value;
		cvar.flags = flags;
		cvar.type = Type.f;
		cvars.put(name.intern(), cvar);
	};
	
	/**
	 * 
	 * @param name indentifier name
	 * @param value
	 * @param flags
	 * @since v1.4.2
	 */
	public static void register(String name, boolean value, CVarFlags flags){
		CVar cvar = new CVar();
		cvar.valueb = value;
		cvar.flags = flags;
		cvar.type = Type.b;
		cvars.put(name.intern(), cvar);
	};
	
	/**
	 * tries to get the cvar.
	 * @param cvar indentifier name
	 * @return if not found returns null
	 * @since v1.4.2
	 */
	public static CVar get(String cvar){
		return cvars.get(cvar.intern());
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
	 * 
	 * @param str 
	 * @since v1.4.2
	 */
	public void setValue(String str) {
		if (str.length()>0)
			if (type==Type.i){
				valuei = Integer.parseInt(str);
			} else if (type==Type.f){
				valuef = Float.parseFloat(str);
			} else {
				valueb = str.equals("1") || str.equals("true");
			} 
	}
	
	/**
	 * load CVars from file and overwrite engine cvars
	 * @since v1.4.2
	 */
	public static void loadFromFile(){
		FileHandle sourceFile = new FileHandle(WorkingDirectory.getWorkingDirectory("Wurfel Engine")+"/engine.weconfig");
		try {
			BufferedReader reader = sourceFile.reader(300);
			String line = reader.readLine();
			while (line!=null) {
				StringTokenizer tokenizer = new StringTokenizer(line, " ");
				String datatype = tokenizer.nextToken();
				String name = tokenizer.nextToken();
				String data = tokenizer.nextToken();
				if (null != datatype && CVar.get(name)==null)//only overwrite if not already set
					switch (datatype) {
					case "float":
						register(name, Float.parseFloat(data), CVarFlags.CVAR_ARCHIVE);
						break;
					case "boolean":
						register(name, "1".equals(data), CVarFlags.CVAR_ARCHIVE);
						break;
					case "int":
						register(name, Integer.parseInt(data), CVarFlags.CVAR_ARCHIVE);
						break;
				}
				line = reader.readLine();
			}
			
		} catch (FileNotFoundException ex) {
			Logger.getLogger(CVar.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(CVar.class.getName()).log(Level.SEVERE, null, ex);
		}
	
	}
	
	/**
	 * move config file from internal to wd
	 * @since v1.4.2
	 */
	public static void unpackFile() {
		FileHandle dest = Gdx.files.absolute(WorkingDirectory.getWorkingDirectory("Wurfel Engine")+"/engine.weconfig");
		
		Gdx.files.internal("com/BombingGames/WurfelEngine/Core/CVarDefault.weconfig").copyTo(dest);
	}
	
	
	/**
	 * saves the cvars with the flag to file
	 * @since v1.4.2
	 */
	public static void dispose(){
		
	}
	
}
