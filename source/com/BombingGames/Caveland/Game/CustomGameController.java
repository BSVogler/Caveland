package com.BombingGames.Caveland.Game;

import com.BombingGames.Caveland.GameObjects.CustomBlockDestructionAction;
import com.BombingGames.Caveland.GameObjects.CustomPlayer;
import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Generators.AirGenerator;
import com.badlogic.gdx.Gdx;

/**
 *The <i>CustomGameController</i> is for the game code. Put engine code into <i>Controller</i>.
 * @author Benedikt
 */
public class CustomGameController extends Controller {
	private CustomPlayer player1;
	private CustomPlayer player2;
        
    @Override
    public void init(){
        Gdx.app.log("CustomGameController", "Initializing");
        super.init(new AirGenerator());

        player1 = new CustomPlayer();
		getMap().setGenerator(new ChunkGenerator());
		Block.setDestructionSound("blockDestroy");
		Block.setDestructionAction(new CustomBlockDestructionAction());
		
//		new Collectible(Collectible.Def.COAL).spawn(new Coordinate(16, 50, 10, true).getPoint());
//		
//		Lore lore = (Lore) new Lore().spawn(new Coordinate(24, 48, 20, true).getPoint());
//		new Lore().spawn(new Coordinate(25, 47, 10, true).getPoint());
//		Lore lore1 = (Lore) new Lore().spawn(new Coordinate(15, 80, 20, true).getPoint());
//		Lore lore2 = (Lore) new Lore().spawn(new Coordinate(15, 83, 20, true).getPoint());
//		//lore.setPassanger(player1);
//		
//		Enemy e1 = (Enemy) new Enemy().spawn(new Coordinate(15, 70, 10, true).getPoint());
//		Enemy e2 = (Enemy) new Enemy().spawn(new Coordinate(14, 71, 10, true).getPoint());
//		e1.setTarget(lore2);
//		e2.setTarget(player1);
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
	public void onEnter() {
		super.onEnter();
		if (!player1.spawned())
			player1.spawn(
				new Coordinate(
					CVar.get("PlayerLastSaveX").getValuei(),
					CVar.get("PlayerLastSaveY").getValuei(),
					CVar.get("PlayerLastSaveZ").getValuei()
				).getPoint()
			);
		if (player2!=null && !player2.spawned())
			player2.spawn(
				new Coordinate(
					CVar.get("PlayerLastSaveX").getValuei(),
					CVar.get("PlayerLastSaveY").getValuei(),
					CVar.get("PlayerLastSaveZ").getValuei()
				).getPoint()
			);
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

	/**
	 * 
	 * @param id 0 is first player, 1 is second
	 * @return 
	 */
	public CustomPlayer getPlayer(int id) {
		if (id==0)
			return player1;
		else
			return player2;
		
	}

	/**
	 * Adds the second player to the game. Will not be spawned until in {@link #onEnter() }.
	 */
	protected void addPlayer2() {
		player2 = new CustomPlayer();
	}
}
