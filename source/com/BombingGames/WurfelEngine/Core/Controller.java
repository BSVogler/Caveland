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
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.LightEngine.LightEngine;
import com.BombingGames.WurfelEngine.Core.Map.Generator;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *A controller manages the map and the game data.
 * @author Benedikt Vogler
 */
public class Controller implements GameManager {
    private static LightEngine lightEngine;
    private static Map map;
    private static DevTools devtools;
    private boolean initalized= false;

    /** The speed of time. 1 = real time;*/
    private float timespeed = 1;
    
    /**
     * This method works like a constructor. Everything is loaded here. You must set your custom map generator, if you want one, before calling this method.
     */
    public void init(){
        init(null);
    }
    
    /**
     * This method works like a constructor. Everything is loaded here. You must set your custom map generator, if you want one, before calling this method.
     * @param generator Set the map generator you want to use.
     */
    public void init(Generator generator){
        Gdx.app.log("Controller", "Initializing");

		if (Controller.devtools == null)
            devtools = new DevTools( 10, 50 );
        
        if (map == null){
            if (!loadMap("default")) {
                Gdx.app.error("Controller", "Map default could not be loaded.");
                try {
                    Map.createMapFile("default");
                    loadMap("default");
                } catch (IOException ex1) {
                    Gdx.app.error("Controller", "Map could not be loaded or created. Wurfel Engine needs access to storage in order to run.");
                    WE.showMainMenu();
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
		
        if (CVar.get("enableLightEngine").getValueb() && Controller.lightEngine == null){
            lightEngine = new LightEngine(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
			getMap().addLinkedObject(lightEngine);
        }
        
        initalized = true;    
    }
        
     /**
     * Main method which is called every refresh.
     * @param dt time since last call
     */
    public void update(float dt) {
        if (devtools!=null) devtools.update(dt);
        
        //aply game world speed
        dt *= timespeed;
        
        if (lightEngine != null) lightEngine.update(dt);
        
        //update every static update method
        AbstractGameObject.updateStaticUpdates(dt);
        
        //update every block on the map
        Block[][][] mapdata = map.getData();
        for (int x=0, maxX=Map.getBlocksX(); x < maxX; x++)
            for (int y=0, maxY = Map.getBlocksY(); y < maxY; y++)
                for (int z=0, maxZ=Map.getBlocksZ(); z < maxZ; z++)
                    mapdata[x][y][z].update(dt, x, y, z);
        
        //update every entity
        for (int i = 0; i < map.getEntitys().size(); i++) {
            map.getEntitys().get(i).update(dt);
            if (map.getEntitys().get(i).shouldBeDisposed())
                map.getEntitys().remove(i);
        }
    }

    /**
     * Tries loading a map.
     * @param name the name of the map
     * @return returns true if the map could be laoded and false if it failed
     */
    public static boolean loadMap(String name) {
        try {
            map = new Map(name);
            map.fill(true);
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
    public static Map getMap() {
        if (map == null)
            throw new NullPointerException("There is no map yet.");
        else return map;
    }

    /**
     *
     * @param map
     */
    public static void setMap(Map map) {
        Gdx.app.debug("Controller", "Map was replaced.");
        Controller.map = map;
        map.modified();
    }
    
    /**
     *
     * @return
     */
    public static LightEngine getLightEngine() {
        return lightEngine;
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
     */
    public static void disposeClass(){
        Gdx.app.debug("ControllerClass", "Disposing.");
        AbstractGameObject.staticDispose();
        Block.staticDispose();
        map.dispose();
		map = null;
        lightEngine = null;
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
        
    }
    
    @Override
    public final void enter() {
        onEnter();
    }
    
    
    /**
     * should get called when you leave the editor
     */
    public void exit(){}

}