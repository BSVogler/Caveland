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

import com.BombingGames.WurfelEngine.Core.Gameobjects.BlockFactory;
import com.BombingGames.WurfelEngine.Core.Map.Generator;
import com.BombingGames.WurfelEngine.Core.Map.Generators.IslandGenerator;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;


/**
 *The configuration should include most of the game's specific options.
 * @author Benedikt Vogler
 */
public class Configuration {
    private final Generator generator = new IslandGenerator();
    /**
     * Load engine configuration from a file
     */
    public void loadConfigFromFile(){
    }
    
    /**
     * The gravity constant in m/s^2
     * @return default is 9.81m/s^2
     */
    public float getGravity() {
        return 9.81f;
    }

    /**in which direction is the world spinning? This is needed for the light engine.
     * WEST->SOUTH->EAST = 0
      * SOUTH->WEST->NORTH = -90
      * EAST->NORTH->WEST = -180
       *NORTH->EAST->SOUT = -270
     * @return in degrees
     */
    public int getWorldSpinAngle() {
        return -40;
    }

    /**
     *Set if the map should load or generate new chunks when the camera reaches an end of the map.
     * @return
     */
    public boolean isChunkSwitchAllowed() {
        return true;
    }

    /**
     *
     * @return
     */
    public boolean shouldLoadMap() {
        return false;
    }

    /**
     *The map generator
     * @return
     */
    public Generator getChunkGenerator() {
        return generator;
    }

    /**
     *
     * @return
     */
    public float getLEAzimutSpeed() {
        return 1/128f;
    }
    

    /**
     * The virtual render width (resolution).
     * Every resolution smaller than this get's scaled down and every resolution bigger scaled up. 
     * @return
     */
    public int getRenderResolutionWidth() {
        return 1920;
    }
    
   /**
    * clearing the screen is ~5-10% slower than without.
    * @return 
    */
    public boolean clearBeforeRendering(){
        return true;
    }

    /**
     *
     * @return
     */
    public boolean useLightEngine() {
        return true;
    }
    
    /**
     *Should the blocks get rendered with fog?
     * @return
     */
    public boolean useFog() {
        return true;
    }

    /**
     * If no light engine the blocks can be shaded by algorithm. Use this only if you are lazy. You should shade the blocks in this case by hand (pre-lit). This can cut performance in half.
     * @return if it should autoshade it should return true
     */
    public boolean shouldAutoShade() {
        return false;
    }
    
//    /**
//     * Returns a list containing classes of blocks.
//     * @return 
//     */
//    @SuppressWarnings("unchecked")
//    public Class<? extends Block>[] getBlockList(){
//        Class<? extends Block>[] blocklist = new Class[100];
//        //blocklist[31] = ExplosiveBarrel.class;
//        return blocklist;
//    }
//    
//    @SuppressWarnings("unchecked")
//    public void initBlockList(){
//        try {
//            Class<? extends Block>[] blocklist;
//            blocklist = new Class[100];
//            blocklist[31] = (Class<? extends Block>) Class.forName("ExplosiveBarrel");
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    /**
     * If you want to use custom blocks you should override this.
     * @return default is null
     */
    public BlockFactory getBlockFactoy(){
        return null;
    }
    
   /**
     * You can use your own spritesheet. the suffix will be added
     * @return format like "com/BombingGames/WurfelEngine/Core/images/Spritesheet" without suffix
     */
    public String getSpritesheetPath(){
        return "com/BombingGames/WurfelEngine/Core/images/Spritesheet";
    }
    
    /**
     * Add asstes to loading queque. 
     * manager.load("com/BombingGames/WeaponOfChoice/Sounds/melee.wav", Sound.class);
     * @param manager
     */
    public void initLoadingQueque(AssetManager manager){
        
    }
    
    /**
     *
     * @return Get the key which opens the console.
     */
    public int getConsoleKey(){
        return Keys.ENTER;
    }
    
    /**
     *
     * @return True if some depth prototype should be activated.
     */
    public boolean useScalePrototype(){
        return false;
    }
    
    /**
     * 
     * @return The id of the ground block.
     */
    public int groundBlockID(){
        return 2;
    }
}
