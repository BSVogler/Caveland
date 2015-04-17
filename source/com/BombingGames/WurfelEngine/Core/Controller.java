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

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.BombingGames.WurfelEngine.Core.LightEngine.LightEngine;
import com.BombingGames.WurfelEngine.Core.Map.AbstractMap;
import com.BombingGames.WurfelEngine.Core.Map.ChunkMap;
import com.BombingGames.WurfelEngine.Core.Map.CompleteMap;
import com.BombingGames.WurfelEngine.Core.Map.LinkedWithMap;
import com.BombingGames.WurfelEngine.Core.SoundEngine.SoundEngine;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import java.io.File;
import java.io.IOException;

/**
 *A controller manages the map and the game data.
 * @author Benedikt Vogler
 */
public class Controller implements GameManager {
    private static LightEngine lightEngine;
	private static SoundEngine soundEngine;
    private static AbstractMap map;
	
	/**
	 * update every static update method
	 * @param dt 
	 */
	public static void staticUpdate(float dt){
		if (lightEngine != null) lightEngine.update(dt);
		if (soundEngine != null) soundEngine.update(dt);
		map.update(dt);
		map.modificationCheck();
	}

    /**
     * Tries loading a map.
	 * @param path
	 * @param saveslot this saveslot will become the active
     * @return returns true if the map could be loaded and false if it failed
     */
    public static boolean loadMap(File path, int saveslot) {
		if (map != null)
			map.dispose();
        try {
			Iterable<LinkedWithMap> linked = null;
			if (map != null) {//if loading another map save linked objects
				linked = map.getLinkedObjects();
			}
				
			if (WE.CVARS.getValueB("mapUseChunks"))
				map = new ChunkMap(path, saveslot);
			else
				map = new CompleteMap(path, saveslot);

			if (linked != null) {
				for (LinkedWithMap linkedObj : linked) {
					map.addLinkedObject(linkedObj);
				}
			}
			
            return true;
        } catch (IOException ex) {
            WE.getConsole().add(ex.getMessage(), "Warning");
            return false;
        }
    }
    
    /**
     * Returns the currently loaded map.
     * @return the map
     */
    public static AbstractMap getMap() {
        if (map == null)
            throw new NullPointerException("There is no map yet.");
        else return map;
    }

    /**
     *
     * @param map
     */
    public static void setMap(AbstractMap map) {
        Gdx.app.debug("Controller", "Map was replaced.");
        Controller.map = map;
        map.modified();
    }
    
    /**
     * The light engine doing the lighting.
     * @return can return null
     */
    public static LightEngine getLightEngine() {
        return lightEngine;
    }
	
	/**
     *The sound engine managing the sfx.
     * @return
     */
    public static SoundEngine getSoundEngine() {
        return soundEngine;
    }
	
	
    private DevTools devtools;
    private boolean initalized= false;
	private int saveSlot;

	/**
	 * uses a specific save slot for loading and saving the map. Can be called before calling init().
	 * @param slot 
	 */
	public void useSaveSlot(int slot){
		this.saveSlot = slot;
		if (map!=null) map.useSaveSlot(slot);
	}

	/**
	 * get the savee slot used for loading and saving the map.
	 * @return 
	 */
	public int getSaveSlot() {
		if (map!=null)
			return map.getCurrentSaveSlot();
		else return saveSlot;
	}
	
	/**
	 * Uses a new save slot as the save slot
	 * @return the new save slot number
	 */
	public int newSaveSlot() {
		saveSlot = AbstractMap.newSaveSlot(new File(WorkingDirectory.getMapsFolder()+"/default/"));
		if (map != null) map.useSaveSlot(saveSlot);
		return saveSlot;
	}
	
    /**
     * This method works like a constructor. Everything is loaded here. You must set your custom map generator, if you want one, before calling this method.
     */
    public void init(){
        init(saveSlot);
    }
    
    /**
     * This method works like a constructor. Everything is loaded here. You must set your custom map generator, if you want one, before calling this method.
	 * @param saveslot
     */
    public void init(int saveslot){
        Gdx.app.log("Controller", "Initializing");

		if (devtools == null && WE.CVARS.getValueB("DevMode"))
            devtools = new DevTools( 10, 50 );
        if (map == null){
            if (!loadMap(new File(WorkingDirectory.getMapsFolder()+"/default"), saveslot)) {
                Gdx.app.error("Controller", "Map default could not be loaded.");
//                try {
                    //ChunkMap.createMapFile("default");
                    loadMap(new File(WorkingDirectory.getMapsFolder()+"/default"), saveslot);
//                } catch (IOException ex1) {
//                    Gdx.app.error("Controller", "Map could not be loaded or created. Wurfel Engine needs access to storage in order to run.");
//                    WE.showMainMenu();
//                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex1);
//                }
            }
        }
		
        if (WE.CVARS.getValueB("enableLightEngine") && Controller.lightEngine == null){
            lightEngine = new LightEngine(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
			getMap().addLinkedObject(lightEngine);
        }
		
		//only initialize static variable once
		if (Controller.soundEngine == null)
			soundEngine = new SoundEngine();
		
        initalized = true;    
    }
	
        
     /**
     * Main method which is called every refresh.
     * @param dt time since last call
     */
    public void update(float dt) {
		if (WE.CVARS.getValueB("DevMode")) {
			if (devtools == null ) {
				devtools = new DevTools(10, 50);
			}
			devtools.update(Gdx.graphics.getRawDeltaTime()*1000f);
		} else {
			if (devtools != null ) {
				devtools.dispose();
				devtools = null;
			}
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
    public void onEnter(){
		WE.CVARS.get("timespeed").setValue(1f);
    }
    
    @Override
    public final void enter() {
        onEnter();
    }
    
    
	@Override
    public void exit(){
	}

	@Override
	public void dispose() {
	}
	
	    /**
     *Disposes static stuff.
     */
    public static void disposeClass(){
        Gdx.app.debug("ControllerClass", "Disposing.");
        AbstractGameObject.staticDispose();
        RenderBlock.staticDispose();
        map.dispose();
		map = null;
        lightEngine = null;
		soundEngine.dispose();
		soundEngine = null;
    }

}