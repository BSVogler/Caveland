package com.bombinggames.caveland.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.Robot;
import com.bombinggames.caveland.GameObjects.Spaceship;
import com.bombinggames.caveland.GameObjects.Vanya;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.CVar.BooleanCVar;
import com.bombinggames.wurfelengine.core.CVar.CVarSystem;
import com.bombinggames.wurfelengine.core.CVar.IntCVar;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 * The <i>CLGameController</i> is for the game code. Put engine code into
 * <i>Controller</i>.
 *
 * @author Benedikt
 */
public class CLGameController extends Controller {

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
					WE.getCvars().getChildSystem().getChildSystem().getValueI("PlayerLastSaveX"),
					WE.getCvars().getChildSystem().getChildSystem().getValueI("PlayerLastSaveY"),
					WE.getCvars().getChildSystem().getChildSystem().getValueI("PlayerLastSaveZ")
				).toPoint()
			);
		}
		if (player2 != null && !player2.hasPosition()) {
			player2.spawn(
				new Coordinate(
					WE.getCvars().getChildSystem().getChildSystem().getValueI("PlayerLastSaveX"),
					WE.getCvars().getChildSystem().getChildSystem().getValueI("PlayerLastSaveY"),
					WE.getCvars().getChildSystem().getChildSystem().getValueI("PlayerLastSaveZ")
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
		CVarSystem saveCvars = WE.getCvars().getChildSystem().getChildSystem();
		saveCvars.register(new IntCVar(0), "PlayerLastSaveX");
		saveCvars.register(new IntCVar(0), "PlayerLastSaveY");
		saveCvars.register(new IntCVar(10), "PlayerLastSaveZ");
		saveCvars.register(new BooleanCVar(false), "IntroCutsceneCompleted");
		saveCvars.register(new IntCVar(0), "money");
		saveCvars.load();
		
		spawnPlayers();
		
		if (!WE.getCvars().getChildSystem().getChildSystem().getValueB("IntroCutsceneCompleted")) {
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
			WE.getCvars().getChildSystem().getChildSystem().get("PlayerLastSaveX").setValue(coord.getX());
			WE.getCvars().getChildSystem().getChildSystem().get("PlayerLastSaveY").setValue(coord.getY());
			WE.getCvars().getChildSystem().getChildSystem().get("PlayerLastSaveZ").setValue(coord.getZ());
		}
		return super.save();
	}
}
