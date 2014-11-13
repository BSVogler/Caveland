/*
 * Copyright 2013 Benedikt Vogler.
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
package com.BombingGames.Caveland.Game;

import com.BombingGames.Caveland.GameObjects.CustomPlayer;
import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.PlayerWithWeapon;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.shooting.Bullet;
import com.BombingGames.WurfelEngine.shooting.Weapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 *The <i>CustomGameController</i> is for the game code. Put engine code into <i>Controller</i>.
 * @author Benedikt
 */
public class GunTestController extends Controller {
    private boolean cooldown = false;
    private Music music;
    private boolean sprinting;
	private PlayerWithWeapon player;
    
        
    @Override
    public void init(){
        Gdx.app.log("CustomGameController", "Initializing");
        super.init();
                
        Weapon.init();
        Bullet.init();
        
        player = (PlayerWithWeapon) new CustomPlayer().spawn(Map.getCenter(Map.getGameHeight()));
        player.equipWeapon(1);
        
//        player.setDamageSounds(new Sound[]{
//            (Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/scream1.wav"),
//            (Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/scream2.wav"),
//            (Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/scream3.wav"),
//            (Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/scream4.wav")
//        });

        


    }

    
    @Override
    public void update(float dt){
        float origidelta = dt;
        dt *= CVar.get("gamespeed").getValuef();



//                for (int i = 0; i < round; i++) {
//                    Coordinate randomPlace = new Coordinate(
//                        (int) (Map.getBlocksX()*Math.random()),
//                        (int) (Map.getBlocksY()*Math.random()),
//                        (float) Map.getGameHeight(),
//                        true);
//                    Enemy enemy = (Enemy) AbstractMovableEntity.getInstance(14, 0,randomPlace.getPoint());
//                    enemy.setTarget(getPlayer());
//                    enemy.exist();
//                }


//        if (currentWeapon != null)
//            currentWeapon.update(input.isButtonPressed(0), delta);

        if (sprinting)
            getPlayer().setMana((int) (getPlayer().getMana()-dt));
        else {
            getPlayer().setMana((int) (getPlayer().getMana()+dt/2f));
        }
        if (getPlayer().getMana()>100)
            cooldown=false;
        if (getPlayer().getMana()<=0)
              cooldown=true;
        super.update(origidelta);
    }


    public Music getMusic() {
        return music;
    }
    
    public boolean sprint(){
         sprinting = (getPlayer().getMana()>0 &&!cooldown);
         return sprinting;
    }
	
	public PlayerWithWeapon getPlayer() {
		return player;
	}
}

