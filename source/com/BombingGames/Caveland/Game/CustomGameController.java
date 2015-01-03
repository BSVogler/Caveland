package com.BombingGames.Caveland.Game;

import com.BombingGames.Caveland.GameObjects.CustomPlayer;
import com.BombingGames.WurfelEngine.Core.Controller;
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
		getMap().setGenerator(new ChunkGenerator());
		
//		new Collectible(Collectible.Def.COAL).spawn(new Coordinate(16, 50, 10, true).getPoint());
//		
//		Lore lore = (Lore) new Lore().spawn(new Coordinate(24, 48, 20, true).getPoint());
//		new Lore().spawn(new Coordinate(25, 47, 10, true).getPoint());
//		Lore lore1 = (Lore) new Lore().spawn(new Coordinate(15, 80, 20, true).getPoint());
//		Lore lore2 = (Lore) new Lore().spawn(new Coordinate(15, 83, 20, true).getPoint());
//		//lore.setPassanger(player);
//		
//		Enemy e1 = (Enemy) new Enemy().spawn(new Coordinate(15, 70, 10, true).getPoint());
//		Enemy e2 = (Enemy) new Enemy().spawn(new Coordinate(14, 71, 10, true).getPoint());
//		e1.setTarget(lore2);
//		e2.setTarget(player);
//		
//		Spaceship spaceship = (Spaceship) new Spaceship().spawn(new Coordinate(14, 69, 0, true).getPoint());
//		spaceship.setDimensionZ(3*AbstractGameObject.GAME_EDGELENGTH);
		//grass test
//		for (int i = 0; i < 500; i++) {
//			new SimpleEntity(44).spawn(
//				new Point((float) (Math.random()*Map.getGameWidth()), (float) (Math.random()*Map.getGameDepth()), 1, true)
//			);
//		}
		//new Vanya().spawn(Map.getCenter());
    }

	@Override
	public void update(float dt) {
		super.update(dt);
//		if (getPlayer(0).getPosition().getCoord().getY()<30 && monstercount<Enemy.getKillcounter()+5){
//			monstercount++;
//			Enemy enemy = (Enemy) new Enemy().spawn(
//				new Coordinate(
//					(int) (Math.random()*Map.getBlocksX()),
//					(int) (Math.random()*30),
//					5
//				).getPoint()
//			);
//			enemy.setTarget(getPlayer(0));
//		}
	}

	public CustomPlayer getPlayer(int id) {
		return player;
	}
}
