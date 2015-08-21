package com.bombinggames.caveland.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import static com.bombinggames.wurfelengine.core.Controller.getLightEngine;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Iterators.DataIterator;
import com.bombinggames.wurfelengine.core.WorkingDirectory;


/**
 *
 * @author Benedikt
 */
public class CustomGameView extends GameView{
	/**
	 * -1 = not down, 0=just pressed down, >0 time down
	 */
	private float inventoryDownP1 =-1;
	private int inventoryDownP2;
	private float throwDownP1 =-1;
	private float throwDownP2 =-1;
	private float useDownP1 =-1;
	private float useDownP2 =-1;
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
	
    @Override
    public void init(Controller controller) {
        super.init(controller);
        Gdx.app.debug("CustomGameView", "Initializing");
		
		if (!WE.CVARS.getValueB("ignorePlayer"))
			Ejira.loadSheet();
		
		//register Sounds
		WE.getEngineView().getSoundEngine().register("jetpack", "com/bombinggames/caveland/sounds/jetpack.wav");
		WE.getEngineView().getSoundEngine().register("step", "com/bombinggames/caveland/sounds/step.wav");
		WE.getEngineView().getSoundEngine().register("urfJump", "com/bombinggames/caveland/sounds/urf_jump.wav");
		WE.getEngineView().getSoundEngine().register("urfHurt", "com/bombinggames/caveland/sounds/urfHurt.wav");
		WE.getEngineView().getSoundEngine().register("loadAttack", "com/bombinggames/caveland/sounds/loadAttack.wav");
		WE.getEngineView().getSoundEngine().register("ha", "com/bombinggames/caveland/sounds/ha.wav");
		WE.getEngineView().getSoundEngine().register("release", "com/bombinggames/caveland/sounds/release.wav");
		WE.getEngineView().getSoundEngine().register("impact", "com/bombinggames/caveland/sounds/impact.wav");
		WE.getEngineView().getSoundEngine().register("robot1destroy", "com/bombinggames/caveland/sounds/robot1destroy.wav");
		WE.getEngineView().getSoundEngine().register("robot1Wobble", "com/bombinggames/caveland/sounds/robot1Wobble.mp3");
		WE.getEngineView().getSoundEngine().register("robotHit", "com/bombinggames/caveland/sounds/robotHit.wav");
		WE.getEngineView().getSoundEngine().register("blockDestroy", "com/bombinggames/caveland/sounds/poch.wav");
		WE.getEngineView().getSoundEngine().register("vanya_jump", "com/bombinggames/caveland/sounds/vanya_jump.wav");
		WE.getEngineView().getSoundEngine().register("wagon", "com/bombinggames/caveland/sounds/wagon.mp3");
		WE.getEngineView().getSoundEngine().register("collect", "com/bombinggames/caveland/sounds/collect.wav");
		WE.getEngineView().getSoundEngine().register("sword", "com/bombinggames/caveland/sounds/sword.wav");
		WE.getEngineView().getSoundEngine().register("hiss", "com/bombinggames/caveland/sounds/hiss.wav");
		WE.getEngineView().getSoundEngine().register("treehit", "com/bombinggames/caveland/sounds/treehit.wav");
		WE.getEngineView().getSoundEngine().register("metallic", "com/bombinggames/caveland/sounds/metallic.wav");
		WE.getEngineView().getSoundEngine().register("construct", "com/bombinggames/caveland/sounds/construct.wav");
		
		if (coop >- 1){//it is a coop game
			Camera camera0;
			if (WE.CVARS.getValueB("coopVerticalSplitScreen")) {
				camera0  = new Camera(
					getPlayer(0),
					0, //left
					0, //top
					Gdx.graphics.getWidth()/2, //width
					Gdx.graphics.getHeight(),//height
					this
				);
				camera0.setInternalRenderResolution( WE.CVARS.getValueI("renderResolutionWidth")/2);
			} else {
				camera0  = new Camera(
					getPlayer(0),
					0, //left
					0, //top
					Gdx.graphics.getWidth(), //width
					Gdx.graphics.getHeight()/2,//height
					this
				);
			}
			getPlayer(0).setCamera(camera0);
			addCamera(camera0);
			
			Camera camera1;
			if (WE.CVARS.getValueB("coopVerticalSplitScreen")) {
				camera1 = new Camera(
					getPlayer(1),
					Gdx.graphics.getWidth()/2,
					0,
					Gdx.graphics.getWidth()/2,
					Gdx.graphics.getHeight(),
					this
				);
				camera1.setInternalRenderResolution( WE.CVARS.getValueI("renderResolutionWidth")/2);
			} else {
				camera1 = new Camera(
					getPlayer(1),
					0,
					Gdx.graphics.getHeight()/2,
					Gdx.graphics.getWidth(),
					Gdx.graphics.getHeight()/2,
					this
				);
			}
			addCamera(camera1);
			getPlayer(1).setCamera(camera1);
		} else {
			//it's a singleplayer game
			Camera camera0  = new Camera(
				getPlayer(0),
				0, //left
				0, //top
				Gdx.graphics.getWidth(), //width
				Gdx.graphics.getHeight(),//height
				this
			);
			camera0.setFullWindow(true);
			getPlayer(0).setCamera(camera0);
			addCamera(camera0);
		}
		
		WE.getEngineView().setMusic("com/bombinggames/caveland/music/overworld.mp3");
		WE.getEngineView().setMusicLoudness(WE.CVARS.getValueF("music"));
        
//        controller.setMinimap(
//            new Minimap(controller, getCameras().get(0), Gdx.graphics.getWidth() - 400,Gdx.graphics.getHeight()-10)
//        );
    }

	/**
	 * 
	 * @param id 0 is first player, 1 is second
	 * @return 
	 */
	public Ejira getPlayer(int id){
		return ((CustomGameController) getController()).getPlayer(id);
	}
	
	/**
	 * 
	 * @param id player number starting at 0
	 */
	private void toogleCrafting(int id) {
		if (openDialogue[id]==null) {
			//open
			Crafting crafting = new Crafting(this, getPlayer(id));
			crafting.register(this, id+1);
		} else {
			//close
			openDialogue[id].cancel(this, getPlayer(id));
		}
	}
	
	@Override
    public void onEnter() {
        WE.getEngineView().addInputProcessor(new MouseKeyboardListener(this)); //alwys listen for keyboard
		
		//is there a controller?
		if (Controllers.getControllers().size > 0){
			//if there is second controller use it for second player
			int playerId=0;
			if (coop>0) playerId=1;
			controllerListenerA = new XboxListener(this,getPlayer(playerId), playerId);
			Controllers.getControllers().get(0).addListener(controllerListenerA);
			if (coop>0) {
				//check if there is a second controller
				if (Controllers.getControllers().size > 1){
					controllerListenerB = new XboxListener(this, getPlayer(1), 1);
					Controllers.getControllers().get(1).addListener(controllerListenerB);
				}
			}
		}
		//hide cursor
		//Gdx.input.setCursorCatched(true);
		//Gdx.input.setCursorPosition(200, 200);
    }

	@Override
	public void exit() {
		super.exit();
		if (controllerListenerA !=null)
			Controllers.getControllers().get(0).removeListener(controllerListenerA);
		if (controllerListenerB !=null)
			Controllers.getControllers().get(1).removeListener(controllerListenerB);
	}
	
    @Override
    public void update(float dt) {
        super.update(dt);
		//get input and do actions
		Input input = Gdx.input;

		
		//manual clipping in caves for black areas
		for (Camera camera : getCameras()) {
			RenderBlock[][][] cc = camera.getCameraContent();
			//if bottom is a cave
			if (cc[cc.length-1][cc[0].length-1][0].getPosition().getY() > ChunkGenerator.CAVESBORDER){
				DataIterator<RenderBlock> iterator = new DataIterator<>(cc, 0, Chunk.getBlocksZ());
				while (iterator.hasNext()) {
					RenderBlock next = iterator.next();
					if (next!=null) {
						//clip floor
						if (iterator.getCurrentIndex()[2]==0)
							next.getBlockData().setClippedTop();
						////h
//						int iout = ChunkGenerator.insideOutside(next.getPosition());
//						if (iout==-1)
//							next.setHidden(true);
//						else if (iout==0)
//							next.getBlockData().setUnclipped();
					}
				}
			}
		}
		
		if (focusOnGame(0)) {
			//walk
			if (getPlayer(0) != null){
				getPlayer(0).walk(
					input.isKeyPressed(Input.Keys.W),
					input.isKeyPressed(Input.Keys.S),
					input.isKeyPressed(Input.Keys.A),
					input.isKeyPressed(Input.Keys.D),
					WE.CVARS.getValueF("playerWalkingSpeed")*(input.isKeyPressed(Input.Keys.SHIFT_LEFT)? 1.5f: 1),
					dt
				);
			} else {
				//update camera position
				Camera camera = getCameras().get(0);
				camera.move(
					(input.isKeyPressed(Input.Keys.D)? 3: 0)
					- (input.isKeyPressed(Input.Keys.A)? 3: 0),
					+ (input.isKeyPressed(Input.Keys.W)? 3: 0)
					- (input.isKeyPressed(Input.Keys.S)? 3: 0)
				);
			}
		}
		
		if (focusOnGame(1)) {
			//update player2
			if (getPlayer(1) != null){
				getPlayer(1).walk(
					input.isKeyPressed(Input.Keys.UP),
					input.isKeyPressed(Input.Keys.DOWN),
					input.isKeyPressed(Input.Keys.LEFT),
					input.isKeyPressed(Input.Keys.RIGHT),
					WE.CVARS.getValueF("playerWalkingSpeed"),
					dt
				);
			}
		}

		if (controllerListenerA != null) {//first controlelr used
			if (controllerListenerA.speed > 0){
				getPlayer(controllerListenerA.player.getPlayerNumber()-1).setSpeedHorizontal((WE.CVARS.getValueF("playerWalkingSpeed")*controllerListenerA.speed)
				);
			}
		}
		
		if (controllerListenerB != null) {//second controller used
			if (controllerListenerB.speed > 0){
				getPlayer(controllerListenerA.player.getPlayerNumber()-1).setSpeedHorizontal((WE.CVARS.getValueF("playerWalkingSpeed")*controllerListenerB.speed)
				);
			}
		}
		
		if (useDownP1==0){
			getPlayer(0).interactWithNearestThing(this);
			//ArrayList<Lore> loren = getPlayer(0).getPosition().getEntitiesNearby(200, Lore.class);
			//if (loren.size()>0)
			//	getPlayer(0).getInventory().addAll(loren.get(0).getContent());
		}
		if (coop>-1)
			if (useDownP2==0){
				getPlayer(1).interactWithNearestThing(this);
				//ArrayList<Lore> loren = getPlayer(0).getPosition().getEntitiesNearby(200, Lore.class);
				//if (loren.size()>0)
				//	getPlayer(0).getInventory().addAll(loren.get(0).getContent());
			}
		
		if (inventoryDownP1==0){
			getPlayer(0).useItem(this);
		}
		
		if (coop>-1)
			if (inventoryDownP2==0){
				getPlayer(1).useItem(this);
			}
		
		if (throwDownP1==0){
			getPlayer(0).prepareThrow();
		}
		if (throwDownP1>=600){
			getPlayer(0).dropItem();
			throwDownP1=-1;
		}
		
		if (throwDownP2>=600){
			getPlayer(1).dropItem();
			throwDownP2=-1;
		}
		
		
		if (coop>-1)
			if (throwDownP2==0){
				getPlayer(1).prepareThrow();
			}
		
		if (inventoryDownP1>-1)
			inventoryDownP1+=dt;
		if (inventoryDownP2>-1)
			inventoryDownP2+=dt;
		if (throwDownP1 > -1)
			throwDownP1 += dt;
		if (throwDownP2 > -1)
			throwDownP2 += dt;
		if (useDownP1>-1)
			useDownP1+=dt;
		if (useDownP2>-1)
			useDownP2+=dt;

	}

	@Override
	public void render() {
		super.render();
		getSpriteBatch().setShader(getShader());
		getSpriteBatch().begin();
			getPlayer(0).getInventory().render(this, getCameras().get(0));
			if (coop > -1)
				getPlayer(1).getInventory().render(this,getCameras().get(1));
		getSpriteBatch().end();
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
		protected float speed =-1;
		private final int id;
		private final CustomGameView parent;
		private float oldRTvalue = -1;
		private final String OS;

		/**
		 * 
		 * @param parent
		 * @param controllable
		 * @param id  starting with 0
		 */
		XboxListener(CustomGameView parent, Ejira controllable, int id) {
			this.player = controllable;
			this.id=id;
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
			if (buttonCode == WE.CVARS.getValueI("controller"+OS+"ButtonX")) {//A
				player.jump();
			}
			
			if (buttonCode == WE.CVARS.getValueI("controller"+OS+"ButtonA")) { //X
				if (parent.openDialogue[id] !=null)
					parent.openDialogue[id].confirm(parent, parent.getPlayer(id));
				else
					player.attack();
			}
			
			if (buttonCode == WE.CVARS.getValueI("controller"+OS+"ButtonB")){//B
				if (parent.openDialogue[id] !=null)
					parent.toogleCrafting(id);
				else {
					if (id==0)
						parent.throwDownP1 = 0;
					else
						parent.throwDownP2 = 0;
				}
			}
			
			if (buttonCode == WE.CVARS.getValueI("controller"+OS+"ButtonY")) //14=Y
				if (id==0)
					parent.inventoryDownP1 = 0;
				else
					parent.inventoryDownP2 = 0;
			
			if (buttonCode == WE.CVARS.getValueI("controller"+OS+"ButtonLB"))//LB
				player.getInventory().switchItems(true);
			
			if (buttonCode == WE.CVARS.getValueI("controller"+OS+"ButtonRB"))//RB
				player.getInventory().switchItems(false);
			
			if (buttonCode == WE.CVARS.getValueI("controller"+OS+"ButtonSelect")) //Select
				parent.toogleCrafting(id);
			
			if (buttonCode == WE.CVARS.getValueI("controller"+OS+"ButtonStart"))
                WE.showMainMenu();
			return false;
		}

		@Override
		public boolean buttonUp(com.badlogic.gdx.controllers.Controller controller, int buttonCode) {
			if (buttonCode==WE.CVARS.getValueI("controller"+OS+"ButtonB")){
				if (id==0) {
					parent.throwDownP1 = -1;
				} else
					parent.throwDownP2 = -1;
				player.throwItem();
			}
			
			if (buttonCode==WE.CVARS.getValueI("controller"+OS+"ButtonY"))
				if (id==0)
					parent.inventoryDownP1 = -1;
				else
					parent.inventoryDownP2 = -1;
			
			if (buttonCode==WE.CVARS.getValueI("controller"+OS+"ButtonA"))
				player.attackLoadingStopped();
			
			return true;
		}

		@Override
		public boolean axisMoved(com.badlogic.gdx.controllers.Controller controller, int axisCode, float value) {
			if (axisCode == WE.CVARS.getValueI("controller"+OS+"AxisRT")) {//RT
				//button down
				if (oldRTvalue < -0.75f && value>-0.75f) {
					if (id==0)
						parent.useDownP1 = 0;
					else
						parent.useDownP2 = 0;
				}
				//button up
				if (oldRTvalue > 0.75f && value<-0.75f) {
					if (id==0)
						parent.useDownP1 = -1;
					else
						parent.useDownP2 = -1;
				}
				oldRTvalue=value;
			} else {
			
				float xDeflec = controller.getAxis(WE.CVARS.getValueI("controller"+OS+"AxisLX"));
				float yDeflec = controller.getAxis(WE.CVARS.getValueI("controller"+OS+"AxisLY"));
				speed = (float) Math.sqrt(
					 xDeflec*xDeflec
					+yDeflec*yDeflec
				);

				if (speed > 0.1f)  //move only if stick is a bit moved
					player.setMovement(new Vector3(
							xDeflec,
							yDeflec,
							player.getMovement().z
						)
					);

				if (speed < 0.2f){//if moving to little only set orientation
					speed = 0;
				}

				player.setSpeedHorizontal(
					(WE.CVARS.getValueF("playerWalkingSpeed")*speed)
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
		private final CustomGameView parent;

		MouseKeyboardListener(CustomGameView parent) {
			this.parent = parent;
		}

        @Override
        public boolean keyDown(int keycode) {
			if (!WE.getConsole().isActive()) {
				if (focusOnGame(0)) {
					//use inventory
					if (keycode == Input.Keys.E){
						inventoryDownP1 = 0;//register on down
					}

					//interact
					if (keycode == Input.Keys.F){
						 useDownP1 = 0;//register on down
					}

					if (keycode == Input.Keys.C) //Select
						toogleCrafting(0);
					
					if (keycode==Input.Keys.NUM_1)
						getPlayer(0).getInventory().switchItems(true);

					if (keycode==Input.Keys.NUM_2)
						getPlayer(0).getInventory().switchItems(false);

					if (keycode==Input.Keys.SPACE)
						getPlayer(0).jump();
				}
			
				if (openDialogue[0]!=null) {
					if (keycode==Input.Keys.W) {
						openDialogue[0].up();
					}
					
					if (keycode==Input.Keys.S) {
						openDialogue[0].down();
					}
				}
				
				if (openDialogue[1]!=null) {
					if (keycode==Input.Keys.UP) {
						openDialogue[1].up();
					}
					
					if (keycode==Input.Keys.DOWN) {
						openDialogue[1].down();
					}
				}

				//editor
				if (keycode == Input.Keys.G){
					 WE.loadEditor(false);
				}
				

				//pause
				//time is set 0 but the game keeps running
				  if (keycode == Input.Keys.P) {
					WE.CVARS.get("gamespeed").setValue(0);
				 } 

                //reset zoom
                if (keycode == Input.Keys.Z) {
                     getCameras().get(0).setZoom(1);
                     WE.getConsole().add("Zoom reset");
				}  
				 

                 //show/hide light engine
                 if (keycode == Input.Keys.L) {
                     if (getLightEngine() != null) getLightEngine().setDebug(!getLightEngine().isInDebug());
                  } 

                 if (keycode == Input.Keys.ESCAPE)
                     WE.showMainMenu();
				 
				//coop controlls
				if (coop==0){
					//p1
					if (focusOnGame(0)) {
						if (keycode == Input.Keys.N) {
							getPlayer(0).attack();
						}

						if (keycode == Input.Keys.M) {
							throwDownP1 = 0;
						}
					}
					
					//p2
					if (focusOnGame(1)) {
						if (keycode == Input.Keys.NUMPAD_0) {
							getPlayer(1).jump();
						}

						if (keycode == Input.Keys.NUMPAD_1) {
							getPlayer(1).attack();
						}

						if (keycode == Input.Keys.NUMPAD_2) {
							throwDownP2=0;
						}

						if (keycode == Input.Keys.NUMPAD_3) {
							inventoryDownP2 = 0;//register on down
						}

						if (keycode==Input.Keys.NUMPAD_4)
							getPlayer(1).getInventory().switchItems(true);

						if (keycode==Input.Keys.NUMPAD_5){
							getPlayer(1).getInventory().switchItems(false);
						}

						if (keycode==Input.Keys.NUMPAD_6){
							useDownP2 =0;
						}
					}
				}
            }
            
            return true;            
        }

        @Override
        public boolean keyUp(int keycode) {
			if (coop==0){
				if (focusOnGame(0)) {
					if (keycode == Input.Keys.N){
						getPlayer(0).attackLoadingStopped();
					}

					if (keycode == Input.Keys.M){
						getPlayer(0).throwItem();
					}
				}
				
				if (focusOnGame(0)) {
					if (keycode==Input.Keys.NUMPAD_1){
						getPlayer(1).attackLoadingStopped();
					}

					if (keycode==Input.Keys.NUMPAD_2){
						getPlayer(1).throwItem();
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
			if (button ==Buttons.LEFT)
				if (openDialogue[0] ==null)
					getPlayer(0).attack();
			
			if (button ==Buttons.RIGHT) {
				if (openDialogue[0] ==null) {
					throwDownP1 = 0;
				}
			}
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			if (button ==Buttons.LEFT) {
				if (openDialogue[0] !=null)
					openDialogue[0].confirm(parent, getPlayer(0));
				getPlayer(0).attackLoadingStopped();
			}
			if (button == Buttons.RIGHT) {
				if (openDialogue[0] ==null) {
					getPlayer(0).throwItem();
					throwDownP1 = -1;
				} else {
					openDialogue[0].cancel(parent, getPlayer(0));
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
            getCameras().get(0).setZoom(getCameras().get(0).getZoom() - amount/100f);
            
            WE.getConsole().add("Zoom: " + getCameras().get(0).getZoom()+"\n");   
            return true;
        }
    }
	
	private boolean focusOnGame(int playerId){
		return !WE.getConsole().isActive() && openDialogue[playerId]==null;
	}
	
	/**
	 * 
	 * @param actionBox can be null
	 * @param playerNumber starts with 1
	 */
	public void setModalDialogue(ActionBox actionBox, int playerNumber){
		this.openDialogue[playerNumber-1] = actionBox;
		if (actionBox != null) {
			if (coop==-1)
				actionBox.setPosition(getStage().getWidth() / 2 - actionBox.getWindow().getWidth()/2, getStage().getHeight() / 2);
			else {
				if (playerNumber==1){
					actionBox.setPosition(getStage().getWidth() / 4 - actionBox.getWindow().getWidth()/2, getStage().getHeight() / 2);
				} else {
					actionBox.setPosition(getStage().getWidth()*3 / 4 - actionBox.getWindow().getWidth()/2, getStage().getHeight() / 2);
				}
			}
			getStage().addActor(actionBox);
		}
	}
}