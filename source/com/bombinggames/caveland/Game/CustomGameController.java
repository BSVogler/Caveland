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
 * The <i>CustomGameController</i> is for the game code. Put engine code into
 * <i>Controller</i>.
 *
 * @author Benedikt
 */
public class CustomGameController extends Controller {

	private Ejira player1;
	private Ejira player2;
	private HashMap<String, ArrayList<AbstractEntity>> caves;
	private Spaceship introSpaceship;

	@Override
	public void init() {
		super.init();

		Gdx.app.log("CustomGameController", "Initializing");
		
		setLightEngine(new CustomLightEngine());
		
		player1 = new Ejira(1);
		RenderBlock.setDestructionSound("blockDestroy");

		mapSetup();
		//grass test
//		for (int i = 0; i < 500; i++) {
//			new SimpleEntity(44).spawn(
//				new Point((float) (Math.random()*Map.getGameWidth()), (float) (Math.random()*Map.getGameDepth()), 1, true)
//			);
//		}
	}
	
	/**
	 * verify that players exist
	 */
	private void spawnPlayers(){
		if (!player1.isSpawned()) {
			player1.spawn(
				new Coordinate(
					WE.CVARS.getChildSystem().getChildSystem().getValueI("PlayerLastSaveX"),
					WE.CVARS.getChildSystem().getChildSystem().getValueI("PlayerLastSaveY"),
					WE.CVARS.getChildSystem().getChildSystem().getValueI("PlayerLastSaveZ")
				).toPoint()
			);
		}
		if (player2 != null && !player2.isSpawned()) {
			player2.spawn(
				new Coordinate(
					WE.CVARS.getChildSystem().getChildSystem().getValueI("PlayerLastSaveX"),
					WE.CVARS.getChildSystem().getChildSystem().getValueI("PlayerLastSaveY"),
					WE.CVARS.getChildSystem().getChildSystem().getValueI("PlayerLastSaveZ")
				).toPoint()
			);
		}
	}

	@Override
	public void onMapReload() {
		super.onMapReload();
		Gdx.app.log("CustomGameController", "onMapLoad");
		mapSetup();
	}
	
	/**
	 * loads save cvars and check if map is okay
	 */
	private void mapSetup() {
		// register save cvars
		CVarSystem saveCvars = WE.CVARS.getChildSystem().getChildSystem();
		saveCvars.register(new IntCVar(0), "PlayerLastSaveX", CVar.CVarFlags.CVAR_ARCHIVE);
		saveCvars.register(new IntCVar(0), "PlayerLastSaveY", CVar.CVarFlags.CVAR_ARCHIVE);
		saveCvars.register(new IntCVar(10), "PlayerLastSaveZ", CVar.CVarFlags.CVAR_ARCHIVE);
		saveCvars.register(new BooleanCVar(false), "IntroCutsceneCompleted", CVar.CVarFlags.CVAR_ARCHIVE);
		saveCvars.load();
		
		spawnPlayers();
		
		if (!WE.CVARS.getChildSystem().getChildSystem().getValueB("IntroCutsceneCompleted")) {
			introSpaceship = (Spaceship) new Spaceship().spawn(new Coordinate(-20, -40, Chunk.getBlocksZ() - 1).toPoint());
			introSpaceship.enableCrash(new Coordinate(0, 0, 8));
			introSpaceship.setPassenger(player1);
		}
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		
		spawnPlayers();
	}

	/**
	 *
	 * @param id 0 is first player, 1 is second
	 * @return
	 */
	public Ejira getPlayer(int id) {
		if (id == 0) {
			return player1;
		} else {
			return player2;
		}
	}

	/**
	 * Adds the second player to the game. Will not be spawned until in {@link #onEnter()
	 * }.
	 */
	public void activatePlayer2() {
		player2 = new Ejira(2);
		player2.setColor(new Color(0.4f, 0.55f, 0.55f, 1));
	}
}
