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
package com.BombingGames.WurfelEngine.Game;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractCharacter;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.GameplayScreen;
import com.BombingGames.WurfelEngine.Core.Map.Chunk;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.WECamera;
import com.BombingGames.WurfelEngine.WEMain;
import com.BombingGames.WurfelEngine.shooting.Bullet;
import com.BombingGames.WurfelEngine.shooting.Weapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 *The <i>CustomGameController</i> is for the game code. Put engine code into <i>Controller</i>.
 * @author Benedikt
 */
public class GunTestController extends Controller {
    private SpinningWheel spinningWheel;
    private int round = 1;
    private final int roundLength = 15000;
    private int roundTimer;
    private Weapon currentWeapon;
    private boolean gameOver;
    private boolean cooldown = false;
    private Music music;
    private long startingTime;
    private int survivedSeconds;
    
        
    @Override
    public void init(){
        Gdx.app.log("CustomGameController", "Initializing");
        Chunk.setGenerator(2);
        super.init();
        
        gameOver=false;

        
        AbstractCharacter player = (AbstractCharacter) AbstractEntity.getInstance(
                40,
                0,
                Map.getCenter(Map.getGameHeight())
        );
        player.setControls("WASD");
        setPlayer(player);
//        player.setDamageSounds(new Sound[]{
//            (Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/scream1.wav"),
//            (Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/scream2.wav"),
//            (Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/scream3.wav"),
//            (Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/scream4.wav")
//        });
        
        addCamera(
            new WECamera(
                getPlayer(),
                0, //left
                0, //top
                Gdx.graphics.getWidth(), //width 
                Gdx.graphics.getHeight()//height
            )
        );
        Weapon.init();
        Bullet.init();
        
        roundTimer = roundLength;
        spinningWheel = new SpinningWheel(this);
        spinningWheel.add(new Weapon(0, null));
        spinningWheel.add(new Weapon(1, null));
        spinningWheel.add(new Weapon(2, null));
        spinningWheel.add(new Weapon(3, null));
        spinningWheel.add(new Weapon(4, null));
        spinningWheel.add(new Weapon(5, null));
        spinningWheel.add(new Weapon(6, null));
        spinningWheel.add(new Weapon(7, null));
        spinningWheel.spin();
        
        startingTime = System.currentTimeMillis();
        survivedSeconds = 0;
        
        //useLightEngine(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
    }

    
    @Override
    public void update(float delta){
        if (!gameOver){
            float origidelta = delta;
            delta *= getTimespeed();

            //get input and do actions
            Input input = Gdx.input;

            if (!GameplayScreen.msgSystem().isListeningForInput()) {

                boolean running = false;
                if (input.isKeyPressed(Input.Keys.SHIFT_LEFT) && getPlayer().getMana()>0 &&!cooldown){
                    getPlayer().setMana((int) (getPlayer().getMana()-delta));
                    running = true;
                    if (getPlayer().getMana()<=0) cooldown=true;
                }else {
                    getPlayer().setMana((int) (getPlayer().getMana()+delta/2f));
                }

                if (getPlayer().getMana()>100) cooldown=false;


                //walk
                if ("WASD".equals(getPlayer().getControls()))
                    getPlayer().walk(
                        input.isKeyPressed(Input.Keys.W),
                        input.isKeyPressed(Input.Keys.S),
                        input.isKeyPressed(Input.Keys.A),
                        input.isKeyPressed(Input.Keys.D),
                        .25f+(running? 0.5f: 0)
                    );
                if (input.isKeyPressed(Input.Keys.SPACE)) getPlayer().jump();
            }



            roundTimer -= delta;
            if (roundTimer <= 0){
                //reset
                roundTimer = roundLength;
                round++;
                GameplayScreen.msgSystem().add("New Round! Round: "+round, "Warning");
                spinningWheel.spin();

                //spawn an enemy
                GameplayScreen.msgSystem().add("Spawning "+(round-1) +" enemys.", "Warning");
//                for (int i = 0; i < round; i++) {
//                    Coordinate randomPlace = new Coordinate(
//                        (int) (Map.getBlocksX()*Math.random()),
//                        (int) (Map.getBlocksY()*Math.random()),
//                        (float) Map.getGameHeight(),
//                        true);
//                    Enemy enemy = (Enemy) AbstractCharacter.getInstance(14, 0,randomPlace.getPoint());
//                    enemy.setTarget(getPlayer());
//                    enemy.exist();
//                }

            }
            spinningWheel.update(origidelta);

            if (getPlayer().getHealt() <= 0 && !gameOver)
                gameOver();

            if (currentWeapon != null)
                currentWeapon.update(input.isButtonPressed(0), delta);

            super.update(origidelta);
        }else {
            music.stop();
        }
    }

    /**
     * @return the spinningWheel
     */
    public SpinningWheel getSpinningWheel() {
        return spinningWheel;
    }
    
    public void equipWeapon(int id){
        currentWeapon = new Weapon(id, getPlayer());
        currentWeapon.reload();
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }
    
    public void gameOver(){
        gameOver = true;
        ((Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/dead.ogg")).play();
        survivedSeconds =(int) ((System.currentTimeMillis()-startingTime)/1000);
        Gdx.app.error("Game over:", "Time:"+survivedSeconds);
        
        getPlayer().destroy();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Music getMusic() {
        return music;
    }

    public int getRound() {
        return round;
    }

    public int getSurvivedSeconds() {
        return survivedSeconds;
    }
}

