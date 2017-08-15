package com.bombinggames.caveland.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.bombinggames.caveland.Game.igmenu.IGMenu;
import com.bombinggames.caveland.gameobjects.Ejira;
import com.bombinggames.caveland.gameobjects.GrassBlock;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import static com.bombinggames.wurfelengine.core.Controller.getLightEngine;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.WorkingDirectory;
import com.bombinggames.wurfelengine.core.cvar.CVarSystemSave;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benedikt
 */
public class CLGameView extends GameView {

	/**
	 * -1 = not down, 0=just pressed down, >0 time down
	 */
	private float inventoryDownP1 = -1;
	private int inventoryDownP2;
	private final float[] throwDown = new float[]{-1, -1};
	private float interactDownP1 = -1;
	private float interactDownP2 = -1;
	/**
	 * -1 disable, 0 keyboard only, 1 one or two controller
	 */
	private int coop = -1;
	/**
	 * listener 1 controls player 2
	 */
	private XboxListener controllerListenerB;
	/**
	 * listener 2 controls player 1
	 */
	private XboxListener controllerListenerA;

	/**
	 * contains or may not contain currently active dialogues
	 */
	private final ActionBox[] openDialogue = new ActionBox[2];
	/**
	 * a widget group that can be opened modal.
	 */
	private WidgetGroup modalGroup;

	@Override
	public void init(Controller controller, GameView oldView) {
		super.init(controller, oldView);
		Gdx.app.debug("CustomGameView", "Initializing");

		if (!WE.getCVars().getValueB("ignorePlayer")) {
			try {
				Ejira.loadSheet();
			} catch (FileNotFoundException ex) {
				Logger.getLogger(CLGameView.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		GrassBlock.initGrass();

		//register Sounds
		WE.SOUND.register("turret", "com/bombinggames/caveland/sounds/turret.ogg");
		WE.SOUND.register("jetpack", "com/bombinggames/caveland/sounds/jetpack.wav");
		WE.SOUND.register("step", "com/bombinggames/caveland/sounds/step.wav");
		WE.SOUND.register("urfJump", "com/bombinggames/caveland/sounds/urf_jump.wav");
		WE.SOUND.register("urfHurt", "com/bombinggames/caveland/sounds/urfHurt.wav");
		WE.SOUND.register("loadAttack", "com/bombinggames/caveland/sounds/loadAttack.wav");
		WE.SOUND.register("ha", "com/bombinggames/caveland/sounds/ha.wav");
		WE.SOUND.register("release", "com/bombinggames/caveland/sounds/release.wav");
		WE.SOUND.register("impact", "com/bombinggames/caveland/sounds/impact.wav");
		WE.SOUND.register("robot1destroy", "com/bombinggames/caveland/sounds/robot1destroy.wav");
		WE.SOUND.register("robot1Wobble", "com/bombinggames/caveland/sounds/robot1Wobble.mp3");
		WE.SOUND.register("robotHit", "com/bombinggames/caveland/sounds/robotHit.wav");
		WE.SOUND.register("blockDestroy", "com/bombinggames/caveland/sounds/poch.wav");
		WE.SOUND.register("vanya_jump", "com/bombinggames/caveland/sounds/vanya_jump.wav");
		WE.SOUND.register("wagon", "com/bombinggames/caveland/sounds/wagon.mp3");
		WE.SOUND.register("collect", "com/bombinggames/caveland/sounds/collect.wav");
		WE.SOUND.register("sword", "com/bombinggames/caveland/sounds/sword.wav");
		WE.SOUND.register("hiss", "com/bombinggames/caveland/sounds/hiss.wav");
		WE.SOUND.register("treehit", "com/bombinggames/caveland/sounds/treehit.wav");
		WE.SOUND.register("metallic", "com/bombinggames/caveland/sounds/metallic.wav");
		WE.SOUND.register("construct", "com/bombinggames/caveland/sounds/construct.wav");
		WE.SOUND.register("huhu", "com/bombinggames/caveland/sounds/huhu.wav");
		WE.SOUND.register("interactionFail", "com/bombinggames/caveland/sounds/throwFail.wav");
		WE.SOUND.register("droneLoop", "com/bombinggames/caveland/sounds/droneLoop.mp3");
		WE.SOUND.register("robot2walk", "com/bombinggames/caveland/sounds/robot2walk.mp3");
		WE.SOUND.register("robotScream", "com/bombinggames/caveland/sounds/robotScream.mp3");
		WE.SOUND.register("robotWeep", "com/bombinggames/caveland/sounds/robotWeep.wav");
		WE.SOUND.register("craft", "com/bombinggames/caveland/sounds/craft.wav");
		WE.SOUND.register("moneyPickup", "com/bombinggames/caveland/sounds/moneyPickup.wav");
		WE.SOUND.register("merchantAha", "com/bombinggames/caveland/sounds/merchantAha.mp3");
		WE.SOUND.register("merchantWelcome", "com/bombinggames/caveland/sounds/merchantWelcome.mp3");

		if (coop > -1) {//it is a coop game
			Camera camera0;
			if (WE.getCVars().getValueB("coopVerticalSplitScreen")) {
				camera0 = new CLCamera(
					this,
					0, //left
					0, //top
					Gdx.graphics.getWidth() / 2, //width
					Gdx.graphics.getHeight(),//height
					getPlayer(0)
				);
				camera0.setInternalRenderResolution(WE.getCVars().getValueI("renderResolutionWidth") / 2);
			} else {
				camera0 = new CLCamera(
					this,
					0, //left
					0, //top
					Gdx.graphics.getWidth(), //width
					Gdx.graphics.getHeight() / 2,//height
					getPlayer(0)
				);
			}
			camera0.setZoom(WE.getCVars().getValueF("coopZoom"));
			getPlayer(0).setCamera(camera0);
			addCamera(camera0);

			Camera camera1;
			if (WE.getCVars().getValueB("coopVerticalSplitScreen")) {
				camera1 = new CLCamera(
					this,
					Gdx.graphics.getWidth() / 2,
					0,
					Gdx.graphics.getWidth() / 2,
					Gdx.graphics.getHeight(),
					getPlayer(1)
				);
				camera1.setInternalRenderResolution(WE.getCVars().getValueI("renderResolutionWidth") / 2);
			} else {
				camera1 = new CLCamera(
					this,
					0,
					Gdx.graphics.getHeight() / 2,
					Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight() / 2,
					getPlayer(1)
				);
			}
			camera1.setZoom(WE.getCVars().getValueF("coopZoom"));
			addCamera(camera1);
			getPlayer(1).setCamera(camera1);
		} else {
			//it's a singleplayer game
			Camera camera0 = new CLCamera(
				this,
				0, //left
				0, //top
				Gdx.graphics.getWidth(), //width
				Gdx.graphics.getHeight(),//height
				getPlayer(0)
			);
			camera0.setFullWindow(true);
			getPlayer(0).setCamera(camera0);
			addCamera(camera0);
		}

		WE.SOUND.setMusic("com/bombinggames/caveland/music/overworld.mp3");
	}

	/**
	 * Shortcut method.
	 *
	 * @param id 0 is first player, 1 is second
	 * @return
	 * @see CustomGameController#getPlayer(int)
	 */
	public Ejira getPlayer(int id) {
		return ((CLGameController) getController()).getPlayer(id);
	}

	/**
	 *
	 * @param id player number starting at 0
	 */
	private void toogleCrafting(int id) {
		if (focusOnGame(id)) {
			//open
			CraftingDialogueBox crafting = new CraftingDialogueBox(this, getPlayer(id));
			crafting.register(this, id + 1, getPlayer(id));
		} else //close
		if (openDialogue[id] instanceof CraftingDialogueBox) {
			openDialogue[id].cancel(getPlayer(id));
		}
	}

	@Override
	public void onEnter() {
		WE.getEngineView().addInputProcessor(new MouseKeyboardListener(this)); //alwys listen for keyboard for one player
		//is there a controller?
		if (WE.getCVars().getValueB("enableControllers") && Controllers.getControllers().size > 0) {//checks if active because if not will crash because of a bug in the backend
			//if there is second controller use it for second player
			controllerListenerA = new XboxListener(this, getPlayer(0), 0);
			Controllers.getControllers().get(0).addListener(controllerListenerA);
			//check if there is a second controller
			if (coop > 0 && Controllers.getControllers().size > 1) {
				controllerListenerB = new XboxListener(this, getPlayer(1), 1);
				Controllers.getControllers().get(1).addListener(controllerListenerB);
			}
		}
		//hide cursor
		//Gdx.input.setCursorCatched(true);
		//Gdx.input.setCursorPosition(200, 200);
	}

	@Override
	public void exit() {
		super.exit();
		if (controllerListenerA != null) {
			Controllers.getControllers().get(0).removeListener(controllerListenerA);
		}
		if (controllerListenerB != null) {
			Controllers.getControllers().get(1).removeListener(controllerListenerB);
		}
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		GrassBlock.updateWind(dt);

		//get input and do actions
		Input input = Gdx.input;

		//remove dialogue from list
		if (openDialogue[0] != null && openDialogue[0].closed()) {
			openDialogue[0] = null;
		}
		if (openDialogue[1] != null && openDialogue[1].closed()) {
			openDialogue[1] = null;
		}

		//manual clipping in caves for black areas
//		ArrayList<RenderChunk> cc = getRenderStorage().getData();
//		//check if the chunk is a cave
//		for (RenderChunk renderChunk : cc) {
//			if (renderChunk.getTopLeftCoordinate().getY() > ChunkGenerator.CAVESBORDER) {
//				//iterate over cameracontent
//				DataIterator<RenderBlock> iterator = renderChunk.getIterator(0, Chunk.getBlocksZ()-1);
//				while (iterator.hasNext()) {
//					RenderCell next = iterator.next();
//					if (next != null && next.getBlockData()!=null) {
//						//clip floor
//						if (-1 == ChunkGenerator.insideOutside(next.getPosition())) {
//							next.setClippedTop();
//						}
//						////h
//	//						int iout = ChunkGenerator.insideOutside(next.getPosition());
//	//						if (iout==-1)
//	//							next.setHidden(true);
//	//						else if (iout==0)
//	//							next.getBlockData().setUnclipped();
//					}
//				}
//			}
//		}
		if (WE.getCVars().getValueB("experimentalCameraJoin") && getCameras().size() >= 2) {
			//todo should compare in view space
			if (getPlayer(0).getPosition().distanceTo(getPlayer(1).getPosition()) < RenderCell.GAME_EDGELENGTH * 5) {
				if (!getCameras().get(0).isFullWindow()) {
					getCameras().get(0).setFullWindow(true);
				}
				getCameras().get(0).setCenter((Point) getPlayer(0).getPosition().cpy().lerp(getPlayer(1).getPosition(), 0.5f));
				getCameras().get(0).setInternalRenderResolution(WE.getCVars().getValueI("renderResolutionWidth"));
				getCameras().get(1).setActive(false);
			} else if (getCameras().get(0).isFullWindow()) {
				getCameras().get(0).setFocusEntity(getPlayer(0));
				getCameras().get(1).setFocusEntity(getPlayer(1));
				getCameras().get(1).setActive(true);
				getCameras().get(0).setScreenSize(
					Gdx.graphics.getWidth() / 2,
					Gdx.graphics.getHeight()
				);
				getCameras().get(0).setInternalRenderResolution(WE.getCVars().getValueI("renderResolutionWidth") / 2);
			}
		}

		if (focusOnGame(0)) {
			//walk
			if (getPlayer(0) != null) {
				getPlayer(0).walk(
					input.isKeyPressed(Input.Keys.W),
					input.isKeyPressed(Input.Keys.S),
					input.isKeyPressed(Input.Keys.A),
					input.isKeyPressed(Input.Keys.D),
					WE.getCVars().getValueF("playerWalkingSpeed") * (WE.getCVars().getValueB("devmode") && input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 1.5f : 1),
					dt
				);
			} else {
				//update camera position
				Camera camera = getCameras().get(0);
				camera.move(
					(input.isKeyPressed(Input.Keys.D) ? 3 : 0)
					- (input.isKeyPressed(Input.Keys.A) ? 3 : 0),
					+(input.isKeyPressed(Input.Keys.W) ? 3 : 0)
					- (input.isKeyPressed(Input.Keys.S) ? 3 : 0)
				);
			}
		}

		if (focusOnGame(1)) {
			//update player2
			if (getPlayer(1) != null) {
				getPlayer(1).walk(
					input.isKeyPressed(Input.Keys.UP),
					input.isKeyPressed(Input.Keys.DOWN),
					input.isKeyPressed(Input.Keys.LEFT),
					input.isKeyPressed(Input.Keys.RIGHT),
					WE.getCVars().getValueF("playerWalkingSpeed"),
					dt
				);
			}
		}

		if (controllerListenerA != null) {//first controller used
			if (controllerListenerA.speed > 0) {
				getPlayer(controllerListenerA.player.getPlayerNumber() - 1).setSpeedHorizontal((WE.getCVars().getValueF("playerWalkingSpeed") * controllerListenerA.speed)
				);
			}
		}

		if (controllerListenerB != null) {//second controller used
			if (controllerListenerB.speed > 0) {
				getPlayer(controllerListenerB.player.getPlayerNumber() - 1).setSpeedHorizontal((WE.getCVars().getValueF("playerWalkingSpeed") * controllerListenerB.speed)
				);
			}
		}

		if (interactDownP1 == 0) {
			getPlayer(0).interactWithNearestThing(this);
		}
		if (coop > -1) {
			if (interactDownP2 == 0) {
				getPlayer(1).interactWithNearestThing(this);
			}
		}

		if (inventoryDownP1 == 0) {
			getPlayer(0).useItem(this);
		}

		if (coop > -1) {
			if (inventoryDownP2 == 0) {
				getPlayer(1).useItem(this);
			}
		}

		if (throwDown[0] == 0) {
			getPlayer(0).prepareThrow();
		}
		if (coop > -1 && throwDown[1] == 0) {
			getPlayer(1).prepareThrow();
		}

		if (throwDown[0] >= WE.getCVars().getValueF("playerItemDropTime")) {
			getPlayer(0).dropItem();
			throwDown[0] = -1;
		}

		if (throwDown[1] >= WE.getCVars().getValueF("playerItemDropTime")) {
			getPlayer(1).dropItem();
			throwDown[1] = -1;
		}

		if (inventoryDownP1 > -1) {
			inventoryDownP1 += dt;
		}
		if (inventoryDownP2 > -1) {
			inventoryDownP2 += dt;
		}
		if (throwDown[0] > -1) {
			throwDown[0] += dt;
		}
		if (throwDown[1] > -1) {
			throwDown[1] += dt;
		}
		if (interactDownP1 > -1) {
			interactDownP1 += dt;
		}
		if (interactDownP2 > -1) {
			interactDownP2 += dt;
		}

	}

	@Override
	public void render() {
		super.render();
		//Draw HUD
		setShader(getShader());
		getProjectionSpaceSpriteBatch().begin();
		getPlayer(0).getInventory().drawHUD(this, getCameras().get(0));
		if (coop > -1) {
			getPlayer(1).getInventory().drawHUD(this, getCameras().get(1));
		}
		getProjectionSpaceSpriteBatch().end();

		useDefaultShader();
		//getSpriteBatch().setColor(Color.WHITE.cpy());
		getProjectionSpaceSpriteBatch().begin();
		if (coop > -1) {
			PlayerCompass pC = new PlayerCompass();
			pC.drawHUD(getPlayer(1), this, getCameras().get(0));
			PlayerCompass pC2 = new PlayerCompass();
			pC2.drawHUD(getPlayer(0), this, getCameras().get(1));
		}
		CVarSystemSave saveCvars = Controller.getMap().getSaveCVars();
		drawString(
			"Money: " + saveCvars.getValueI("money"),
			Gdx.graphics.getWidth() / 2 - 50,
			Gdx.graphics.getHeight() - 100,
			Color.WHITE.cpy()
		);

		if (getPlayer(0).hasPosition() && getPlayer(0).getPosition().toCoord().getY() > ChunkGenerator.GENERATORBORDER) {
			drawString(
				"Cave Level: " + ChunkGenerator.getCaveNumber(getPlayer(0).getPosition().toCoord()),
				50,
				50,
				Color.WHITE.cpy()
			);
		}
		getProjectionSpaceSpriteBatch().end();
	}

	/**
	 *
	 * @param flag -1 disable, 0 keyboard only, 1 one or two controller
	 */
	public void enableCoop(int flag) {
		coop = flag;
	}

	private class XboxListener implements ControllerListener {

		private final Ejira player;
		/**
		 * speed of one player
		 */
		protected float speed = -1;
		/**
		 * starting at 0
		 */
		private final int id;
		private final CLGameView parent;
		private float oldRTvalue = -1;
		private final String OS;

		/**
		 *
		 * @param parent
		 * @param controllable
		 * @param id starting with 0
		 */
		XboxListener(CLGameView parent, Ejira controllable, int id) {
			this.player = controllable;
			this.id = id;
			this.parent = parent;
			OS = WorkingDirectory.getPlatform().toString();
		}

		@Override
		public void connected(com.badlogic.gdx.controllers.Controller controller) {
		}

		@Override
		public void disconnected(com.badlogic.gdx.controllers.Controller controller) {
		}

		@Override
		public boolean buttonDown(com.badlogic.gdx.controllers.Controller controller, int buttonCode) {
			if (buttonCode == WE.getCVars().getValueI("controller" + OS + "ButtonB")) {//B
				if (parent.openDialogue[id] != null) {
					parent.toogleCrafting(id);
				} else {
					player.jump();
				}
			}

			if (buttonCode == WE.getCVars().getValueI("controller" + OS + "ButtonA")) { //A
				if (parent.openDialogue[id] != null) {
					parent.openDialogue[id].confirm(parent.getPlayer(id));
				} else {
					player.attack();
				}
			}

			if (buttonCode == WE.getCVars().getValueI("controller" + OS + "ButtonX")) {//X
				if (id == 0) {
					parent.throwDown[0] = 0;
				} else {
					parent.throwDown[1] = 0;
				}
			}

			if (buttonCode == WE.getCVars().getValueI("controller" + OS + "ButtonY")) { //14=Y
				if (id == 0) {
					parent.inventoryDownP1 = 0;
				} else {
					parent.inventoryDownP2 = 0;
				}
			}

			if (buttonCode == WE.getCVars().getValueI("controller" + OS + "ButtonLB")) {//LB
				player.getInventory().switchItems(true);
			}

			if (buttonCode == WE.getCVars().getValueI("controller" + OS + "ButtonRB")) {//RB
				player.getInventory().switchItems(false);
			}

			if (buttonCode == WE.getCVars().getValueI("controller" + OS + "ButtonSelect")) { //Select
				parent.toogleCrafting(id);
			}

			if (buttonCode == WE.getCVars().getValueI("controller" + OS + "ButtonStart")) {
				WE.showMainMenu();
			}
			return false;
		}

		@Override
		public boolean buttonUp(com.badlogic.gdx.controllers.Controller controller, int buttonCode) {
			if (buttonCode == WE.getCVars().getValueI("controller" + OS + "ButtonX")) {
				if (id == 0) {
					parent.throwDown[0] = -1;
				} else {
					parent.throwDown[1] = -1;
				}
				if (throwDown[player.getPlayerNumber() - 1] >= WE.getCVars().getValueF("playerItemDropTime")) {
					player.throwItem();
				}
			}

			if (buttonCode == WE.getCVars().getValueI("controller" + OS + "ButtonY")) {
				if (id == 0) {
					parent.inventoryDownP1 = -1;
				} else {
					parent.inventoryDownP2 = -1;
				}
			}

			if (buttonCode == WE.getCVars().getValueI("controller" + OS + "ButtonA")) {
				player.attackLoadingStopped();
			}

			return true;
		}

		@Override
		public boolean axisMoved(com.badlogic.gdx.controllers.Controller controller, int axisCode, float value) {
			if (axisCode == WE.getCVars().getValueI("controller" + OS + "AxisRT")) {//RT
				//button down
				if (oldRTvalue < -0.75f && value > -0.75f) {
					if (id == 0) {
						parent.interactDownP1 = 0;
					} else {
						parent.interactDownP2 = 0;
					}
				}
				//button up
				if (oldRTvalue > 0.75f && value < -0.75f) {
					if (id == 0) {
						parent.interactDownP1 = -1;
					} else {
						parent.interactDownP2 = -1;
					}
				}
				oldRTvalue = value;
			} else {
				float xDeflec = controller.getAxis(WE.getCVars().getValueI("controller" + OS + "AxisLX"));
				float yDeflec = controller.getAxis(WE.getCVars().getValueI("controller" + OS + "AxisLY"));
				speed = (float) Math.sqrt(
					xDeflec * xDeflec
					+ yDeflec * yDeflec
				);

				if (speed > 0.1f) { //move only if stick is a bit moved
					player.setMovement(new Vector3(
						xDeflec,
						yDeflec,
						player.getMovement().z
					)
					);
				}

				if (speed < 0.2f) {//if moving to little only set orientation
					speed = 0;
				}

				player.setSpeedHorizontal(
					(WE.getCVars().getValueF("playerWalkingSpeed") * speed)
				);
			}
			return false;
		}

		@Override
		public boolean povMoved(com.badlogic.gdx.controllers.Controller controller, int povCode, PovDirection value) {
			return false;
		}

		@Override
		public boolean xSliderMoved(com.badlogic.gdx.controllers.Controller controller, int sliderCode, boolean value) {
			return false;
		}

		@Override
		public boolean ySliderMoved(com.badlogic.gdx.controllers.Controller controller, int sliderCode, boolean value) {
			return false;
		}

		@Override
		public boolean accelerometerMoved(com.badlogic.gdx.controllers.Controller controller, int accelerometerCode, Vector3 value) {
			return false;
		}
	}

	private class MouseKeyboardListener implements InputProcessor {

		private final CLGameView parent;

		MouseKeyboardListener(CLGameView parent) {
			this.parent = parent;
		}

		@Override
		public boolean keyDown(int keycode) {
			if (!WE.getConsole().isActive()) {
				if (focusOnGame(0)) {
					//use inventory
					if (keycode == Input.Keys.E) {
						inventoryDownP1 = 0;//register on down
					}

					//interact
					if (keycode == Input.Keys.F) {
						interactDownP1 = 0;//register on down
					}

					if (keycode == Input.Keys.C) {
						toogleCrafting(0);
					}

					if (keycode == Input.Keys.N) {
						getPlayer(0).attack();
					}

					if (keycode == Input.Keys.M) {
						throwDown[0] = 0;
					}

					if (keycode == Input.Keys.NUM_1) {
						getPlayer(0).getInventory().switchItems(true);
					}

					if (keycode == Input.Keys.NUM_2) {
						getPlayer(0).getInventory().switchItems(false);
					}

					if (keycode == Input.Keys.SPACE) {
						getPlayer(0).jump();
					}

					if (keycode == Input.Keys.N) {
						getPlayer(0).attack();
					}
				}

				if (openDialogue[0] != null) {
					if (keycode == Input.Keys.W) {
						openDialogue[0].up(getPlayer(0));
					}

					if (keycode == Input.Keys.S) {
						openDialogue[0].down(getPlayer(0));
					}
				}

				if (openDialogue[1] != null) {
					if (keycode == Input.Keys.UP) {
						openDialogue[1].up(getPlayer(1));
					}

					if (keycode == Input.Keys.DOWN) {
						openDialogue[1].down(getPlayer(1));
					}
				}

				//coop controlls
				if (coop == 0) {

					//p2
					if (focusOnGame(1)) {
						if (keycode == Input.Keys.NUMPAD_0) {
							getPlayer(1).jump();
						}

						if (keycode == Input.Keys.NUMPAD_1) {
							getPlayer(1).attack();
						}

						if (keycode == Input.Keys.NUMPAD_2) {
							throwDown[1] = 0;
						}

						if (keycode == Input.Keys.NUMPAD_3) {
							inventoryDownP2 = 0;//register on down
						}

						if (keycode == Input.Keys.NUMPAD_4) {
							inventoryDownP2 = 0;
						}

						if (keycode == Input.Keys.NUMPAD_5) {
							interactDownP2 = 0;//context
						}

						if (keycode == Input.Keys.NUMPAD_6) {
							toogleCrafting(1);//craft
						}

						if (keycode == Input.Keys.NUMPAD_7) {
							getPlayer(1).getInventory().switchItems(true);
						}

						if (keycode == Input.Keys.NUMPAD_8) {
							getPlayer(1).getInventory().switchItems(false);
						}
					}
				}

				//editor
				if (keycode == Input.Keys.G) {
					WE.startEditor();
				}

				//reset zoom
				if (keycode == Input.Keys.Z) {
					getCameras().get(0).setZoom(1);
					WE.getConsole().add("Zoom reset");
				}

				//show/hide light engine
				if (keycode == Input.Keys.L) {
					if (getLightEngine() != null) {
						getLightEngine().setDebug(!getLightEngine().isInDebug());
					}
				}

				if (keycode == Input.Keys.ESCAPE) {
					if (modalGroup == null) {
						setModal(new IGMenu(this.parent));
						pauseTime();
					} else {
						setModal(null);
						continueTime();
					}
				}
			}

			return true;
		}

		@Override
		public boolean keyUp(int keycode) {
			if (keycode == Input.Keys.N) {
				if (focusOnGame(0)) {
					getPlayer(0).attackLoadingStopped();
				}
				if (openDialogue[0] != null) {
					openDialogue[0].confirm(getPlayer(0));
				}
			}
			if (keycode == Input.Keys.M) {
				if (focusOnGame(0)) {
					getPlayer(0).throwItem();
					throwDown[0] = -1;
				}
				if (openDialogue[0] != null) {
					openDialogue[0].cancel(getPlayer(0));
				}
			}
			if (coop == 0) {
				if (keycode == Input.Keys.NUMPAD_1) {
					if (focusOnGame(1)) {
						getPlayer(1).attackLoadingStopped();
					}
					if (openDialogue[1] != null) {
						openDialogue[1].confirm(getPlayer(1));
					}
				}
				if (keycode == Input.Keys.NUMPAD_2) {
					if (focusOnGame(1)) {
						getPlayer(1).throwItem();
						throwDown[1] = -1;
					}
					if (openDialogue[1] != null) {
						openDialogue[1].cancel(getPlayer(1));
					}
				}
			}

			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			if (focusOnGame(0)) {
				if (button == Buttons.LEFT) {
					getPlayer(0).attack();
				}

				if (button == Buttons.RIGHT) {
					throwDown[0] = 0;
				}
			}
			return true;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			if (button == Buttons.LEFT) {
				if (openDialogue[0] != null) {
					openDialogue[0].confirm(getPlayer(0));
				}
				if (focusOnGame(0)) {
					getPlayer(0).attackLoadingStopped();
				}
			}
			if (button == Buttons.RIGHT) {
				if (focusOnGame(0)) {
					getPlayer(0).throwItem();
					throwDown[0] = -1;
				}
				if (openDialogue[0] != null) {
					openDialogue[0].cancel(getPlayer(0));
				}
			}
			return true;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			getCameras().get(0).setZoom(getCameras().get(0).getZoom() - amount / 100f);

			WE.getConsole().add("Zoom: " + getCameras().get(0).getZoom() + "\n");
			return true;
		}
	}

	/**
	 * is the focus of the player on the game or is it redirected by some popup?
	 *
	 * @param playerId starts with 0
	 * @return
	 */
	private boolean focusOnGame(int playerId) {
		return !WE.getConsole().isActive()
			&& openDialogue[playerId] == null
			&& modalGroup == null;
	}

	/**
	 * Set an actionbox to a modal dialogue, so that the input gets redirected
	 * to the dialoge and not the character. The modality is only valid for one
	 * player.
	 *
	 * @param actionBox can be null
	 * @param playerNumber starts with 1
	 */
	public void setModalDialogue(ActionBox actionBox, int playerNumber) {
		this.openDialogue[playerNumber - 1] = actionBox;
		if (actionBox != null) {
			if (coop == -1) {
				actionBox.setPosition(
					getStage().getWidth() / 2 - actionBox.getWindow().getWidth() / 2,
					getStage().getHeight() / 5
				);
			} else if (playerNumber == 1) {
				actionBox.setPosition(
					getStage().getWidth() / 4 - actionBox.getWindow().getWidth() / 2,
					getStage().getHeight() / 5
				);
			} else {
				actionBox.setPosition(
					getStage().getWidth() * 3 / 4 - actionBox.getWindow().getWidth() / 2,
					getStage().getHeight() / 5
				);
			}
			getStage().addActor(actionBox);
		}
	}

	/**
	 * Set a global modal widget. Both inputs get redirected there. Adds the
	 * widget to the stage.
	 *
	 * @param group can be null
	 */
	public void setModal(WidgetGroup group) {
		if (group == null && modalGroup != null) {
			this.modalGroup.remove();
		}
		this.modalGroup = group;
		if (this.modalGroup != null) {
			getStage().addActor(modalGroup);
			modalGroup.setPosition(
				getStage().getWidth() / 2 - modalGroup.getWidth() / 2,
				getStage().getHeight() / 2 - modalGroup.getHeight() / 2
			);
		}
	}

	/**
	 *
	 */
	public void pauseTime() {
		WE.getCVars().get("timespeed").setValue(0f);
	}

	/**
	 *
	 */
	public void continueTime() {
		WE.getCVars().get("timespeed").setValue(1f);
	}
}
