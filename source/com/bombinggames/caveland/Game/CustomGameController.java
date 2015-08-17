package com.bombinggames.caveland.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.Spaceship;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.CVar.BooleanCVar;
import com.bombinggames.wurfelengine.core.CVar.CVar;
import com.bombinggames.wurfelengine.core.CVar.CVarSystem;
import com.bombinggames.wurfelengine.core.CVar.IntCVar;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *The <i>CustomGameController</i> is for the game code. Put engine code into <i>Controller</i>.
 * @author Benedikt
 */
public class CustomGameController extends Controller {
	private Ejira player1;
	private Ejira player2;
	private HashMap<String, ArrayList<AbstractEntity>> caves;
	private Spaceship introSpaceship;
        
    @Override
    public void init(){
        super.init();

		Gdx.app.log("CustomGameController", "Initializing");
       
		// register save cvars
		CVarSystem saveCvars = WE.CVARS.getChildSystem().getChildSystem();
		saveCvars.register(new IntCVar(0), "PlayerLastSaveX", CVar.CVarFlags.CVAR_ARCHIVE);
		saveCvars.register(new IntCVar(0), "PlayerLastSaveY", CVar.CVarFlags.CVAR_ARCHIVE);
		saveCvars.register(new IntCVar(10), "PlayerLastSaveZ", CVar.CVarFlags.CVAR_ARCHIVE);
		saveCvars.register(new BooleanCVar(false), "IntroCutsceneCompleted", CVar.CVarFlags.CVAR_ARCHIVE);
		saveCvars.load();
		
		player1 = new Ejira(1);
		
		player1.spawn(
			new Coordinate(
				saveCvars.getValueI("PlayerLastSaveX"),
				saveCvars.getValueI("PlayerLastSaveY"),
				saveCvars.getValueI("PlayerLastSaveZ")
			).toPoint()
		);
		if (player2!=null && !player2.isSpawned())
			player2.spawn(
				new Coordinate(
					saveCvars.getValueI("PlayerLastSaveX"),
					saveCvars.getValueI("PlayerLastSaveY"),
					saveCvars.getValueI("PlayerLastSaveZ")
				).toPoint()
			);
		
		RenderBlock.setDestructionSound("blockDestroy");
		
		setLightEngine(new CustomLightEngine());
		
		if (!WE.CVARS.getChildSystem().getChildSystem().getValueB("IntroCutsceneCompleted")){
			introSpaceship = (Spaceship) new Spaceship().spawn(new Coordinate(-20, -40, Chunk.getBlocksZ()-1).toPoint());
			introSpaceship.enableCrash(new Coordinate(0, 0, 8));
			introSpaceship.setPassenger(player1);
		}
		
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
//		Spaceship introSpaceship = (Spaceship) new Spaceship().spawn(new Coordinate(14, 69, 0, true).getPoint());
//		introSpaceship.setDimensionZ(3*AbstractGameObject.GAME_EDGELENGTH);
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
		//check if each cave has two portals
		//add onChunkLoad and add this class to map observers
		//each x blocks a new cave beginns, if not already a collection available for it, the nmake new one
	}

	/**
	 * 
	 * @param id 0 is first player, 1 is second
	 * @return 
	 */
	public Ejira getPlayer(int id) {
		if (id==0) {
			return player1;
		} else{
			return player2;
		}
		
	}

	/**
	 * Adds the second player to the game. Will not be spawned until in {@link #onEnter() }.
	 */
	public void activatePlayer2() {
		player2 = new Ejira(2);
		player2.setColor(new Color(0.4f,0.55f,0.55f,1));
	}
}
