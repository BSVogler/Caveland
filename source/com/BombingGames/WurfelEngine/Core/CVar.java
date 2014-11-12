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
 */
public class CVar {
	
	public enum CVarFlags {
		CVAR_ARCHIVE, CVAR_VOLATILE // never saved to file
	}	
	
	/**global list of all CVars**/
	private static HashMap<String, CVar> cvars = new HashMap<>(50);
	
	private CVarFlags flags;
	private int valuei;
	private float valuef;
	private boolean valueb;
	
	public static void register(String name, int value, CVarFlags flags){
		CVar cvar = new CVar();
		cvar.valuei = value;
		cvar.flags = flags;
		cvars.put(name.intern(), cvar);
	};
	
	public static void register(String name, float value, CVarFlags flags){
		CVar cvar = new CVar();
		cvar.valuef = value;
		cvar.flags = flags;
		cvars.put(name.intern(), cvar);
	};
	
	public static void register(String name, boolean value, CVarFlags flags){
		CVar cvar = new CVar();
		cvar.valueb = value;
		cvar.flags = flags;
		cvars.put(name.intern(), cvar);
	};
	
	
	public static CVar get(String cvar){
		return cvars.get(cvar.intern());
	}

	public int getValuei() {
		return valuei;
	}

	public float getValuef() {
		return valuef;
	}
	
	public boolean getValueb() {
		return valueb;
	}
	
	
	/**
	 * load CVars from file 
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
				if (null != datatype)
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
	 * @param src
	 */
	public static void unpackFile() {
		FileHandle dest = Gdx.files.absolute(WorkingDirectory.getWorkingDirectory("Wurfel Engine")+"/engine.weconfig");
		
		Gdx.files.internal("com/BombingGames/WurfelEngine/Core/CVarDefault.weconfig").copyTo(dest);
	}
	
}
