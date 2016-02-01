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
package com.bombinggames.wurfelengine.core.cvar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Each cvar system manages one file. Cvars get registered first and then
 * overwritten by the local file. If a cvar is in the file but not registered it
 * gets ignored.
 *
 * @author Benedikt Vogler
 */
public abstract class AbstractCVarSystem {

	/**
	 * true if currently reading. Prevents saving
	 */
	private boolean reading;
	/**
	 * path of the cvar file
	 */
	private final File fileSystemPath;
	/**
	 * list of all CVars*
	 */
	private final HashMap<String, CVar> cvars = new HashMap<>(50);

	/**
	 * you have to manually call {@link #load} to load from path.
	 *
	 * @param path path to the .cvar file
	 */
	public AbstractCVarSystem(File path) {
		this.fileSystemPath = path;
	}

	/**
	 *
	 * @param cvar can include a path
	 * @return
	 */
	public CVar get(String cvar) {
		return cvars.get(cvar.toLowerCase());
	}

	/**
	 *
	 * @param cvar
	 * @return
	 */
	public boolean getValueB(String cvar) {
		try {
			return (boolean) cvars.get(cvar.toLowerCase()).getValue();
		} catch (NullPointerException ex) {
			throw new NullPointerException("Cvar \"" + cvar + "\" not defined.");
		}
	}

	/**
	 *
	 * @param cvar
	 * @return
	 */
	public int getValueI(String cvar) {
		try {
			return (int) cvars.get(cvar.toLowerCase()).getValue();
		} catch (NullPointerException ex) {
			throw new NullPointerException("Cvar \"" + cvar + "\" not defined.");
		}
	}

	/**
	 *
	 * @param cvar
	 * @return
	 */
	public float getValueF(String cvar) {
		try {
			return (float) cvars.get(cvar.toLowerCase()).getValue();
		} catch (NullPointerException ex) {
			throw new NullPointerException("Cvar \"" + cvar + "\" not defined.");
		}
	}

	/**
	 *
	 * @param cvar
	 * @return
	 */
	public String getValueS(String cvar) {
		try {
			return (String) cvars.get(cvar.toLowerCase()).getValue();
		} catch (NullPointerException ex) {
			throw new NullPointerException("Cvar \"" + cvar + "\" not defined.");
		}
	}

	/**
	 * load CVars from file and overwrite engine cvars. You must register the
	 * cvars first before the values can be read.
	 *
	 * @since v1.4.2
	 */
	public void load() {
		System.out.println("Loading saved CVars…");
		reading = true;
		FileHandle sourceFile = new FileHandle(fileSystemPath);
		if (sourceFile.exists() && !sourceFile.isDirectory()) {
			try {
				BufferedReader reader = sourceFile.reader(300);
				String line = reader.readLine();
				while (line != null) {
					StringTokenizer tokenizer = new StringTokenizer(line, " ");
					if (tokenizer.hasMoreTokens()) {
						String name = tokenizer.nextToken();
						if (tokenizer.hasMoreTokens()) {
							String data = tokenizer.nextToken();
							if (get(name) != null) {//only overwrite if already registered
								get(name).setValue(data);
								System.out.println("Set CVar " + name + ": " + data);
							}
						}
					}
					line = reader.readLine();
				}

			} catch (FileNotFoundException ex) {
				Logger.getLogger(CVar.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(CVar.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			System.out.println("Custom CVar file " + fileSystemPath + " not found. Creating new one at the same place.");
			try {
				fileSystemPath.createNewFile();
			} catch (IOException ex) {
				System.out.println("Could not create file at " + fileSystemPath + ".");
			}
		}
		reading = false;

	}

	/**
	 * saves the cvars with the flag to file
	 *
	 * @since v1.4.2
	 */
	public void dispose() {
		save();
	}

	/**
	 * saves CVars to file
	 */
	public void save() {
		if (!reading) {
			Writer writer = Gdx.files.absolute(fileSystemPath.getAbsolutePath()).writer(false);

			Iterator<java.util.Map.Entry<String, CVar>> it = cvars.entrySet().iterator();
			while (it.hasNext()) {

				java.util.Map.Entry<String, CVar> pairs = it.next();
				CVar cvar = pairs.getValue();
				try {
					//if should be saved and different then default: save
					if (cvar.flags == CVar.CVarFlags.CVAR_ARCHIVE
						&& !cvar.getDefaultValue().equals(cvar.getValue())
						|| cvar.flags == CVar.CVarFlags.CVAR_ALWAYSSAVE) {
						writer.write(pairs.getKey() + " " + cvar.toString() + "\n");
					}

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
	 *
	 * @param prefix some chars with which the cvar begins.
	 * @return A list containing every cvar starting with the prefix
	 */
	public ArrayList<String> getSuggestions(String prefix) {
		ArrayList<String> resultList = new ArrayList<>(5);
		Iterator<java.util.Map.Entry<String, CVar>> it = cvars.entrySet().iterator();
		while (it.hasNext()) {
			java.util.Map.Entry<String, CVar> cvarEntry = it.next();
			if (cvarEntry.getKey().startsWith(prefix.toLowerCase())) {
				resultList.add(cvarEntry.getKey());
			}
		}
		return resultList;
	}

	/**
	 * Registering should only be done by the game or the engine in init phase.
	 * Also saves as defaultValue. if already registered updates the default and
	 * current value.
	 *
	 * @param cvar
	 * @param name
	 * @param flag
	 * @since v1.4.2
	 */
	public void register(CVar cvar, String name, CVar.CVarFlags flag) {
		cvar.register(name, flag, this);
		//if already registered new value is set
		if (cvars.containsKey(cvar.name)) {
			get(cvar.name).setDefaultValue(cvar.getValue());
		} else {
			cvars.put(cvar.name.toLowerCase(), cvar);
		}
	}

	/**
	 * Registering should only be done by the game or the engine in init phase.
	 * Also saves as defaultValue. if already registered updates the default and
	 * current value.<br>
	 * Uses {@link CVarFlags.CVAR_ARCHIVE}
	 *
	 * @param cvar
	 * @param name
	 * @since v1.4.2
	 */
	public void register(CVar cvar, String name) {
		register(cvar, name, CVar.CVarFlags.CVAR_ARCHIVE);
	}

	/**
	 *
	 * @return
	 */
	public String showAll() {
		return cvars.toString();
	}
}
