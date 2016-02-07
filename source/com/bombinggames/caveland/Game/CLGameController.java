package com.bombinggames.caveland.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Color;
import com.bombinggames.caveland.gameobjects.Ejira;
import com.bombinggames.caveland.gameobjects.Robot;
import com.bombinggames.caveland.gameobjects.Spaceship;
import com.bombinggames.caveland.gameobjects.Vanya;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.cvar.BooleanCVar;
import com.bombinggames.wurfelengine.core.cvar.CVarSystemSave;
import com.bombinggames.wurfelengine.core.cvar.IntCVar;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import java.util.ArrayList;

/**
 * The <i>CLGameController</i> is for the game code. Put engine code into
 * <i>Controller</i>.
 *
 * @author Benedikt
 */
public class CLGameController extends Controller implements Telegraph {

	private Ejira player1;
	private Ejira player2;
	private Spaceship introSpaceship;
	private Vanya tutorialVanya;
	private boolean tutorialEndFight;

	@Override
	public void init() {
		super.init();

		Gdx.app.log("CustomGameController", "Initializing");
		
		setLightEngine(new CustomLightEngine());
		
		player1 = new Ejira(1);
		//RenderBlock.setDestructionSound("blockDestroy");

		mapSetup();
	}
	
	/**
	 * verify that players exist
	 */
	private void spawnPlayers(){
		if (!player1.hasPosition()) {
			player1.spawn(
				new Coordinate(
					WE.getCVarsSave().getValueI("PlayerLastSaveX"),
					WE.getCVarsSave().getValueI("PlayerLastSaveY"),
					WE.getCVarsSave().getValueI("PlayerLastSaveZ")
				).toPoint()
			);
		}
		if (player2 != null && !player2.hasPosition()) {
			player2.spawn(
				new Coordinate(
					WE.getCVarsSave().getValueI("PlayerLastSaveX"),
					WE.getCVarsSave().getValueI("PlayerLastSaveY"),
					WE.getCVarsSave().getValueI("PlayerLastSaveZ")
				).toPoint()
			);
		}
	}

	/**
	 * loads save cvars and check if map is okay
	 */
	private void mapSetup() {
		// register save cvars
		CVarSystemSave saveCvars = WE.getCVarsSave();
		saveCvars.register(new IntCVar(0), "PlayerLastSaveX");
		saveCvars.register(new IntCVar(0), "PlayerLastSaveY");
		saveCvars.register(new IntCVar(10), "PlayerLastSaveZ");
		saveCvars.register(new BooleanCVar(false), "IntroCutsceneCompleted");
		saveCvars.register(new IntCVar(0), "money");
		saveCvars.register(new IntCVar(0), "respawnX");
		saveCvars.register(new IntCVar(0), "respawnY");
		saveCvars.register(new IntCVar(10), "respawnZ");
		saveCvars.load();
		
		spawnPlayers();
		
		if (!WE.getCVarsSave().getValueB("IntroCutsceneCompleted")) {
			introSpaceship = (Spaceship) new Spaceship().spawn(new Coordinate(-30, -80, 30).toPoint());
			introSpaceship.enableCrash(new Coordinate(-5, 4, 7));
			introSpaceship.setIndestructible(true);
			introSpaceship.addContent(player1);
			if (player2 != null)
				introSpaceship.addContent(player2);
		}
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		
		spawnPlayers();
		
		if (tutorialVanya == null){
			ArrayList<Vanya> foundVanya = getPlayer(0).getPosition().getEntitiesNearby(10*Block.GAME_EDGELENGTH, Vanya.class);
			if (foundVanya.isEmpty())
				tutorialVanya = (Vanya) new Vanya().spawn(new Coordinate(-3, 8, 6).toPoint());
			else {
				tutorialVanya = foundVanya.get(0);
			}
		}
		
		if (introSpaceship != null
			&& introSpaceship.isCrashed()
			&& tutorialVanya.getCompletedTutorialStep()==0
		){
			tutorialVanya.setTutorialStep(1);
		}
		
		if (getPlayer(0).getPosition().toCoord().getY() > ChunkGenerator.CAVESBORDER
			||
			player2 != null && getPlayer(0).getPosition().toCoord().getY() > ChunkGenerator.CAVESBORDER) {
			if (tutorialVanya != null) {
				tutorialVanya.setTutorialStep(4);
			}
		}
		
		//jumped over cliff
		if (!tutorialEndFight
			&&
			(
				player1.getPosition().toCoord().isInCube(
					new Coordinate(28, -5, 3),
					new Coordinate(30, 7, Chunk.getBlocksZ())
				)
				||
				(
					player2 != null && player2.getPosition().toCoord().isInCube(
						new Coordinate(28, -5, 3),
						new Coordinate(30, 7, Chunk.getBlocksZ())
					)
				)
			)
		) {
			tutorialEndFight = true;
			if (tutorialVanya != null) {
				tutorialVanya.setTutorialStep(5);
			}
			for (int i = 0; i < 5; i++) {
				new Robot().spawn(
					new Coordinate(
						(int) (34+Math.random()*5),
						(int) (4+Math.random()*5),
						5
				).toPoint());
			}
			
		}
			
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

	/**
	 *
	 * @return 
	 */
	@Override
	public boolean save() {
		if (player1.hasPosition()) {
			Coordinate coord = player1.getPosition().toCoord();
			WE.getCVarsSave().get("PlayerLastSaveX").setValue(coord.getX());
			WE.getCVarsSave().get("PlayerLastSaveY").setValue(coord.getY());
			WE.getCVarsSave().get("PlayerLastSaveZ").setValue(coord.getZ());
		}
		return super.save();
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message == Events.mapReloaded.getId()){
			mapSetup();
			return true;
		}
		return false;
	}
	
}
