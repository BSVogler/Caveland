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

import com.BombingGames.Caveland.GameObjects.Collectible;
import com.BombingGames.Caveland.GameObjects.CustomPlayer;
import com.BombingGames.Caveland.GameObjects.Enemy;
import com.BombingGames.Caveland.GameObjects.Lore;
import com.BombingGames.Caveland.GameObjects.Spaceship;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Generators.AirGenerator;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.badlogic.gdx.Gdx;

/**
 *The <i>CustomGameController</i> is for the game code. Put engine code into <i>Controller</i>.
 * @author Benedikt
 */
public class CustomGameController extends Controller {
	private int monstercount =0;
	private CustomPlayer player;
        
    @Override
    public void init(){
        Gdx.app.log("CustomGameController", "Initializing");
        super.init(new AirGenerator());

        player = (CustomPlayer) new CustomPlayer().spawn(Map.getCenter(Map.getGameHeight()));
		new Collectible(Collectible.Def.COAL).spawn(new Coordinate(16, 50, 10, true).getPoint());
		
		Lore lore = (Lore) new Lore().spawn(new Coordinate(24, 48, 20, true).getPoint());
		new Lore().spawn(new Coordinate(25, 47, 10, true).getPoint());
		Lore lore1 = (Lore) new Lore().spawn(new Coordinate(15, 80, 20, true).getPoint());
		Lore lore2 = (Lore) new Lore().spawn(new Coordinate(15, 83, 20, true).getPoint());
		//lore.setPassanger(player);
		
		Enemy e1 = (Enemy) new Enemy().spawn(new Coordinate(15, 70, 10, true).getPoint());
		Enemy e2 = (Enemy) new Enemy().spawn(new Coordinate(14, 71, 10, true).getPoint());
		e1.setTarget(lore2);
		e2.setTarget(player);
		
		Spaceship spaceship = (Spaceship) new Spaceship().spawn(new Coordinate(14, 69, 0, true).getPoint());
		spaceship.setDimensionZ(3*AbstractGameObject.GAME_EDGELENGTH);
		//grass test
//		for (int i = 0; i < 500; i++) {
//			new SimpleEntity(44).spawn(
//				new Point((float) (Math.random()*Map.getGameWidth()), (float) (Math.random()*Map.getGameDepth()), 1, true)
//			);
//		}
		//new Vanya(Map.getCenter()).spawn();
    }

	@Override
	public void update(float delta) {
		super.update(delta);
		if (getPlayer(0).getPosition().getCoord().getRelY()<30 && monstercount<Enemy.getKillcounter()+5){
			monstercount++;
			Enemy enemy = (Enemy) new Enemy().spawn(
				new Coordinate(
					(int) (Math.random()*Map.getBlocksX()),
					(int) (Math.random()*30),
					5,
					true
				).getPoint()
			);
			enemy.setTarget(getPlayer(0));
		}
	}

	public CustomPlayer getPlayer(int id) {
		return player;
	}
	

    
}
