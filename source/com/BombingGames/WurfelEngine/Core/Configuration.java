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
import com.badlogic.gdx.assets.AssetManager;


/**
 *The configuration should include most of the game's specific options.
 * @author Benedikt Vogler
 */
public class Configuration {
    private final Generator generator = new IslandGenerator();
    
    /**
     *The map generator
     * @return
     */
    public Generator getChunkGenerator() {
        return generator;
    }

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
     * Add asstes to loading queque. <br />
     * e.g. manager.load("com/BombingGames/WeaponOfChoice/Sounds/melee.wav", Sound.class);
     * @param manager
     */
    public void initLoadingQueque(AssetManager manager){
    }
}
