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
package com.bombinggames.wurfelengine.core;

import com.badlogic.gdx.Gdx;
import com.bombinggames.wurfelengine.Command;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.Gameobjects.Cursor;
import com.bombinggames.wurfelengine.core.Gameobjects.EntityShadow;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.LightEngine.LightEngine;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Map;
import com.bombinggames.wurfelengine.core.Map.MapObserver;
import com.bombinggames.wurfelengine.core.Map.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A controller manages the map and the game data.
 *
 * @author Benedikt Vogler
 */
public class Controller implements GameManager, MapObserver {

	private static LightEngine lightEngine;
	private static Map map;

	/**
	 * update every static update method
	 *
	 * @param dt
	 */
	public static void staticUpdate(float dt) {
		if (lightEngine != null) {
			lightEngine.update(dt);
		}
		map.update(dt);
		map.modificationCheck();
	}

	/**
	 * Tries loading a map.
	 *
	 * @param path
	 * @param saveslot this saveslot will become the active
	 * @return returns true if the map could be loaded and false if it failed
	 */
	public static boolean loadMap(File path, int saveslot) {
		ArrayList<MapObserver> linked = null;
		if (map != null) {
			map.dispose(false);
			//if loading another map, save linked objects
			linked = map.getOberservers();
		}
		try {
			map = new Map(path, saveslot);
			if (linked != null) {
				map.setObservers(linked);
			}
			map.onReload();

			return true;
		} catch (IOException ex) {
			WE.getConsole().add(ex.getMessage(), "Warning");
			return false;
		}
	}

	/**
	 * Returns the currently loaded map.
	 *
	 * @return the map
	 */
	public static Map getMap() {
		if (map == null) {
			throw new NullPointerException("There is no map yet.");
		} else {
			return map;
		}
	}

	/**
	 *
	 * @param map
	 */
	public static void setMap(Map map) {
		Gdx.app.debug("Controller", "Map was replaced.");
		Controller.map = map;
		map.setModified();
	}

	/**
	 * The light engine doing the lighting.
	 *
	 * @return can return null
	 */
	public static LightEngine getLightEngine() {
		return lightEngine;
	}

	public static void setLightEngine(LightEngine le) {
		lightEngine = le;
		getMap().getOberservers().add(lightEngine);
	}

	/**
	 * Disposes static stuff.
	 */
	public static void staticDispose() {
		Gdx.app.debug("ControllerClass", "Disposing.");
		AbstractGameObject.staticDispose();
		RenderBlock.staticDispose();
		map.dispose(false);
		map = null;
		lightEngine = null;
	}

	private DevTools devtools;
	private boolean initalized = false;
	private int saveSlot;
	private String mapName = "default";
	private final Cursor cursor = new Cursor();
	private ArrayList<AbstractEntity> selectedEntities = new ArrayList<>(4);
	private final Command[] commandHistory = new Command[WE.getCvars().getValueI("historySize")];
	private int lastCommandPos = -1;


	public void executeCommand(Command cmd){
		//shift commands back if full
		if (lastCommandPos >= commandHistory.length-1) {
			for (int i = 0; i < commandHistory.length-1; i++) {
				commandHistory[i]=commandHistory[i+1];
			}
		} else {
			lastCommandPos++;
		}
		
		commandHistory[lastCommandPos] = cmd;
		
		commandHistory[lastCommandPos].execute();
		//empty in front
		for (int i = lastCommandPos+1; i < commandHistory.length; i++) {
			commandHistory[i]=null;
		}
	}
	
	public void undoCommand(){
		if (lastCommandPos >= 0 && lastCommandPos < commandHistory.length-1){
			commandHistory[lastCommandPos].undo();
			lastCommandPos--;
		}
	}
	
	public void redoCommand(){
		if (lastCommandPos >= -1 && lastCommandPos < commandHistory.length-1 && commandHistory[lastCommandPos+1] != null){
			lastCommandPos++;
			commandHistory[lastCommandPos].execute();
		}
	}
	
	/**
	 * uses a specific save slot for loading and saving the map. Can be called
	 * before calling init().
	 *
	 * @param slot
	 */
	public void useSaveSlot(int slot) {
		this.saveSlot = slot;
		if (map != null) {
			map.useSaveSlot(slot);
		}
	}

	/**
	 * Set the map name which is loaded then. Should be called before the
	 * {@link #init()}
	 *
	 * @param mapName
	 */
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	/**
	 * get the savee slot used for loading and saving the map.
	 *
	 * @return
	 */
	public int getSaveSlot() {
		if (map != null) {
			return map.getCurrentSaveSlot();
		} else {
			return saveSlot;
		}
	}

	/**
	 * Uses a new save slot as the save slot
	 *
	 * @return the new save slot number
	 */
	public int newSaveSlot() {
		saveSlot = Map.newSaveSlot(new File(WorkingDirectory.getMapsFolder() + "/" + mapName + "/"));
		if (map != null) {
			map.useSaveSlot(saveSlot);
		}
		return saveSlot;
	}

	/**
	 * This method works like a constructor. Everything is loaded here. You must
	 * set your custom map generator, if you want one, before calling this
	 * method.
	 */
	public void init() {
		init(saveSlot, mapName);
	}

	/**
	 * This method works like a constructor. Everything is loaded here. You must
	 * set your custom map generator, if you want one, before calling this
	 * method.
	 *
	 * @param saveslot
	 * @param mapName the value of mapName
	 */
	public void init(int saveslot, String mapName) {
		Gdx.app.log("Controller", "Initializing");

		if (devtools == null && WE.getCvars().getValueB("DevMode")) {
			devtools = new DevTools(10, 50);
		}
		if (map == null) {
			if (!loadMap(new File(WorkingDirectory.getMapsFolder() + "/" + mapName), saveslot)) {
				Gdx.app.error("Controller", "Map " + mapName + "could not be loaded.");
//                try {
//                    Map.createMapFile("default");
//                    loadMap(new File(WorkingDirectory.getMapsFolder()+"/default"), saveslot);
//                } catch (IOException ex1) {
//                    Gdx.app.error("Controller", "Map could not be loaded or created. Wurfel Engine needs access to storage in order to run.");
				WE.showMainMenu();
				return;
				// Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex1);
				// }
			}
			//add controller to observers
			if (!map.getOberservers().contains(this)) {
				map.getOberservers().add(this);
			}
		}

		//create default light engine
		if (WE.getCvars().getValueB("enableLightEngine") && Controller.lightEngine == null) {
			lightEngine = new LightEngine(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
			getMap().getOberservers().add(lightEngine);
		}

		initalized = true;
	}

	/**
	 * Main method which is called every refresh.
	 *
	 * @param dt time since last call
	 */
	public void update(float dt) {
		if (WE.getCvars().getValueB("DevMode")) {
			if (devtools == null) {
				devtools = new DevTools(10, 50);
			}
			devtools.update(Gdx.graphics.getRawDeltaTime() * 1000f);
		} else {
			devtools = null;
		}

		if (!cursor.hasPosition()) {
			cursor.spawn(
				new Point(0, 0, Chunk.getBlocksZ() - 1)
			);
		}
	}

	/**
	 *
	 * @return
	 */
	public DevTools getDevTools() {
		return devtools;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public boolean isInitalized() {
		return initalized;
	}

	@Override
	public void onEnter() {
		WE.getCvars().get("timespeed").setValue(1f);
	}

	@Override
	public final void enter() {
		onEnter();
	}

	@Override
	public void exit() {
		Gdx.app.debug("Controller", "exited");
	}

	/**
	 * saves the map and the complete game state
	 *
	 * @return
	 */
	public boolean save() {
		return Controller.getMap().save();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void onMapChange() {
	}

	@Override
	public void onChunkChange(Chunk chunk) {
	}

	@Override
	public void onMapReload() {
	}

	/**
	 * @return selected entities but map editor entities
	 */
	public ArrayList<AbstractEntity> getSelectedEntities() {
		selectedEntities.removeIf(ent -> !ent.hasPosition());
		return selectedEntities;
	}

	/**
	 * filter map editor entities
	 *
	 * @param newSel
	 */
	public void setSelectedEnt(ArrayList<AbstractEntity> newSel) {
		this.selectedEntities = newSel;
		selectedEntities.remove(cursor);
		selectedEntities.removeIf(ent
			-> ent.getName().equals("normal")
			|| ent instanceof EntityShadow
			|| !ent.hasPosition()
		);
	}

	/**
	 * Get the entity laying under the cursor.
	 *
	 * @return
	 */
	public Cursor getCursor() {
		return cursor;
	}
}
