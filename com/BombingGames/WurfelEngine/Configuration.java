/*
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
 * * Neither the name of Bombing Games nor Benedikt Vogler nor the names of its contributors 
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

package com.BombingGames.WurfelEngine;

//import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.BlockFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

//import com.BombingGames.WurfelEngine.Core.Gameobjects.ExplosiveBarrel;

/**
 *The configuration should include most of the game's specific options.
 * @author Benedikt Vogler
 */
public class Configuration {
    private final boolean editor = false;
        /**
     * The gravity constant in m/s^2
     */
    private final float gravity = 9.81f;
    
    /**in which direction is the world spinning? This is needed for the light engine.
     * WEST->SOUTH->EAST = 0
      * SOUTH->WEST->NORTH = -90
      * EAST->NORTH->WEST = -180
       *NORTH->EAST->SOUT = -270
       **/
    private final int worldSpinAngle = -40;
    
    /**
     *Set if the map should load or generate new chunks when the camera reaches an end of the map.
     */
    private final boolean allowChunkSwitch = true;
    
    private final boolean loadMap = false;
    
    /**The number of the mapgenerator used.*/
    private final int chunkgenerator = 1;
    
    private final float leAzimutSpeed = 1/64f;

    /**
     * The virtual render width (resolution).
     * Every resolution smaller than this get's scaled down and every resolution bigger scaled up. 
     */
    private final int renderResolutionWidth = 1920;
    
    private final boolean useLightEngine = true;
    
    /**
     * If no light engine the blocks can be shaded by algorithm. Use this only if you are lazy. YOu should shade the blokcs in this case by hand. This can cut performance in half.
     */
    private final boolean autoshade = false;
    
    
    /**
     *
     * @return
     */
    public boolean isEditor() {
        return editor;
    }

    /**
     *
     * @return
     */
    public float getGravity() {
        return gravity;
    }

    /**
     *
     * @return
     */
    public int getWorldSpinAngle() {
        return worldSpinAngle;
    }

    /**
     *
     * @return
     */
    public boolean ChunkSwitchAllowed() {
        return allowChunkSwitch;
    }

    /**
     *
     * @return
     */
    public boolean shouldLoadMap() {
        return loadMap;
    }

    /**
     *
     * @return
     */
    public int getChunkGenerator() {
        return chunkgenerator;
    }

    /**
     *
     * @return
     */
    public float getLEAzimutSpeed() {
        return leAzimutSpeed;
    }
    
    
    /**
     * Load engine configuration from a file
     */
    public void loadfromFile(){
    }

    public int getRenderResolutionWidth() {
        return renderResolutionWidth;
    }

    public boolean useLightEngine() {
        return useLightEngine;
    }

    public boolean shouldAutoShade() {
        return autoshade;
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
     * default returns null. If you want to use custom blocks you should override this.
     * @return null
     */
    public BlockFactory getBlockFactoy(){
        return null;
    }
    
}
