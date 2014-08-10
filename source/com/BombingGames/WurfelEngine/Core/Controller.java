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

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractCharacter;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.LightEngine.LightEngine;
import com.BombingGames.WurfelEngine.Core.Map.Cell;
import com.BombingGames.WurfelEngine.Core.Map.Generator;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Minimap;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *A controller manages the map and the game data.
 * @author Benedikt Vogler
 */
public class Controller implements Manager {
    private static LightEngine lightEngine;
    private static Map map;
    private static boolean recalcRequested;
    private static DevTools devtools;
    private boolean initalized= false;

    
    private Minimap minimap;
    /** The speed of time. 1 = real time;*/
    private float timespeed = 1;
    private AbstractCharacter player;  
    
    private LoadMenu loadMenu;
    
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
        if (Controller.devtools == null) devtools = new DevTools(this, 10,Gdx.graphics.getHeight()-50);
        if (map == null){
            map = new Map("no name set", generator);
            map.fill();
            requestRecalc();
        }
        
        if (WE.getCurrentConfig().useLightEngine() && Controller.lightEngine == null){
            Controller.lightEngine = new LightEngine(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        }
        
        initalized = true;    
    }
        
     /**
     * Main method which is called every refresh.
     * @param delta time since last call
     */
    public void update(float delta) {
        if (devtools!=null) devtools.update(delta);
        
        //aply game world speed
        delta *= timespeed;
        
        if (lightEngine != null) lightEngine.update(delta);
        
         //update the log
        GameplayScreen.msgSystem().update(delta);
        
        
        //update every static update method
        AbstractGameObject.updateStaticUpdates(delta);
        
        //update every block on the map
        Cell[][][] mapdata = map.getData();
        for (int x=0; x < Map.getBlocksX(); x++)
            for (int y=0; y < Map.getBlocksY(); y++)
                for (int z=0; z < Map.getBlocksZ(); z++)
                    mapdata[x][y][z].getBlock().update(delta);
        
        //update every entity
        for (int i = 0; i < map.getEntitys().size(); i++) {
            map.getEntitys().get(i).update(delta);
            if (map.getEntitys().get(i).shouldBeDisposed())
                map.getEntitys().remove(i);
        }
        
                
        //recalculates the light if requested
        if (recalcRequested) {
            Camera.raytracing();
            LightEngine.calcSimpleLight();
            if (minimap != null)minimap.buildMinimap();
            recalcRequested = false;
        }
    }

    
     /**
     * Informs the map that a recalc is requested. It will do it in the next update. This method  to limit update calls to to per frame
     */
    public static void requestRecalc(){
        //Gdx.app.debug("Controller", "A recalc was requested.");
        recalcRequested = true;
    }
    
    /**
     * Creates a new Map using it's generator. Does a recalc.
     */
    public static void newMap(){
        map = new Map("nonameset");
        map.fill();
        requestRecalc();
    }
    
    /**
     * Creates a new Map.
     * @param generator using this generator
     */
    public static void newMap(Generator generator){
        map = new Map("nonameset");
        map.fill(generator);
        requestRecalc();
    }
    
    /**
     * Tries loading a map.
     * @param name the name of the map
     */
    public static void loadMap(String name){
        map = new Map(name);
        map.fill();
        requestRecalc();
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
        requestRecalc();
    }
    
    /**
     * Returns the player.
     * @return the player. If no player returns null.
     */
    public AbstractCharacter getPlayer() {
        return player;
    }

   /**
     * Sets a player 
     * @param player 
     */
    public void setPlayer(AbstractCharacter player) {
        if (player==null)
            throw new NullPointerException("No player was passed.");
        else {
            this.player = player;
            player.exist();
        }
    }   
    
    /**
     * Returns the minimap.
     * @return 
     */
    public Minimap getMinimap() {
        return minimap;
    }
    
   

    /**
     * Set the minimap and "builds it"
     * @param minimap
     */
    public void setMinimap(final Minimap minimap) {
        this.minimap = minimap;
        minimap.buildMinimap();
    }

    /**
     *
     * @return
     */
    public static LightEngine getLightengine() {
        return lightEngine;
    }

    /**
     *
     * @return
     */
    public float getTimespeed() {
        return timespeed;
    }

    /**
     *
     */
    public void setTimespeed() {
        JFrame frame = new JFrame("InputDialog Example #2");
        try {
            this.timespeed = Float.parseFloat(JOptionPane.showInputDialog(frame, "Use dot as separator.", "Set the speed of time", JOptionPane.QUESTION_MESSAGE));
        } catch(NumberFormatException e) {
            this.timespeed = 1;
            Gdx.app.error("JFrame", "Invalid nubmer entered: "+e.toString());
        } catch(NullPointerException e){
            Gdx.app.debug("JFrame", "Canceled: "+e.toString());
        }
    }
    
    /**
     *
     * @param timespeed
     */
    public void setTimespeed(float timespeed) {
        this.timespeed = timespeed;
    }

    /**
     *
     * @return
     */
    public DevTools getDevTools() {
        return devtools;
    }
    

    /**
     *Get a menu which can be used for loading maps.
     * @return
     */
    public LoadMenu getLoadMenu() {
        if (loadMenu==null) loadMenu = new LoadMenu();//lazy init
        return loadMenu;
    }

    /**
     *
     */
    public static void disposeClass(){
        Gdx.app.debug("ControllerClass", "Disposing.");
        AbstractGameObject.staticDispose();
        Block.staticDispose();
        Map.staticDispose();
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
    
        /**
     * should be called when entered
     */
    @Override
    public void enter(){}
    
    /**
     * should get called when you leave the editor
     */
    public void exit(){}

    

}