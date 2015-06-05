package com.bombinggames.caveland.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.GameObjects.CustomPlayer;
import com.bombinggames.wurfelengine.Core.Camera;
import com.bombinggames.wurfelengine.Core.Controller;
import static com.bombinggames.wurfelengine.Core.Controller.getLightEngine;
import com.bombinggames.wurfelengine.Core.GameView;
import com.bombinggames.wurfelengine.Core.WorkingDirectory;
import com.bombinggames.wurfelengine.WE;


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
	 * -1 disable, 0 keyboard only, 1 one controller, 2 two controllers
	 */
	private int coop = -1;
	private XboxListener controllerListener1;
	private XboxListener controllerListener2;
	
	private ActionBox[] openDialogue = new ActionBox[2];
	
    @Override
    public void init(Controller controller) {
        super.init(controller);
        Gdx.app.debug("CustomGameView", "Initializing");
		
		CustomPlayer.loadSheet();
		
		//register Sounds
		Controller.getSoundEngine().register("jetpack", "com/bombinggames/Caveland/sounds/jetpack.wav");
		Controller.getSoundEngine().register("step", "com/bombinggames/Caveland/sounds/step.wav");
		Controller.getSoundEngine().register("urfJump", "com/bombinggames/Caveland/sounds/urf_jump.wav");
		Controller.getSoundEngine().register("urfHurt", "com/bombinggames/Caveland/sounds/urfHurt.wav");
		Controller.getSoundEngine().register("loadAttack", "com/bombinggames/Caveland/sounds/loadAttack.wav");
		Controller.getSoundEngine().register("ha", "com/bombinggames/Caveland/sounds/ha.wav");
		Controller.getSoundEngine().register("release", "com/bombinggames/Caveland/sounds/release.wav");
		Controller.getSoundEngine().register("impact", "com/bombinggames/Caveland/sounds/impact.wav");
		Controller.getSoundEngine().register("robot1destroy", "com/bombinggames/Caveland/sounds/robot1destroy.wav");
		Controller.getSoundEngine().register("robot1Wobble", "com/bombinggames/Caveland/sounds/robot1Wobble.mp3");
		Controller.getSoundEngine().register("robotHit", "com/bombinggames/Caveland/sounds/robotHit.wav");
		Controller.getSoundEngine().register("blockDestroy", "com/bombinggames/Caveland/sounds/poch.wav");
		Controller.getSoundEngine().register("vanya_jump", "com/bombinggames/Caveland/sounds/vanya_jump.wav");
		Controller.getSoundEngine().register("wagon", "com/bombinggames/Caveland/sounds/wagon.mp3");
		Controller.getSoundEngine().register("collect", "com/bombinggames/Caveland/sounds/collect.wav");
		Controller.getSoundEngine().register("sword", "com/bombinggames/Caveland/sounds/sword.wav");
		Controller.getSoundEngine().register("hiss", "com/bombinggames/Caveland/sounds/hiss.wav");
		Controller.getSoundEngine().register("treehit", "com/bombinggames/Caveland/sounds/treehit.wav");
		
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
			
			//if there is second controller use it for second player
			if (Controllers.getControllers().size > 1){
				controllerListener2 = new XboxListener(this, getPlayer(1), 1);
				Controllers.getControllers().get(1).addListener(controllerListener2);
			}
		} else {
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
		
		
		WE.getEngineView().setMusic("com/bombinggames/Caveland/music/overworld.mp3");
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
	public CustomPlayer getPlayer(int id){
		return ((CustomGameController) getController()).getPlayer(id);
	}
	
	private void toogleCrafting(int id) {
		if (openDialogue[id]==null) {
			openDialogue[id] = new Crafting(getStage(), getPlayer(id).getInventory());
			openDialogue[id].setBounds(getStage().getWidth()/2-200, getStage().getHeight()/2+200, 400, 400);
			getStage().addActor(openDialogue[id]);
		} else {
			openDialogue[id].remove();
			openDialogue[id]=null;
		}
	}
	
	@Override
    public void onEnter() {
        WE.getEngineView().addInputProcessor(new MouseKeyboardListener());
		int playerId = 0;
		if (coop==1) playerId = 1;
		if (Controllers.getControllers().size > 0){
			controllerListener1 = new XboxListener(this,getPlayer(playerId),playerId);
			Controllers.getControllers().get(0).addListener(controllerListener1);
		}
		
		//hide cursor
		//Gdx.input.setCursorCatched(true);
		//Gdx.input.setCursorPosition(200, 200);
    }

	@Override
	public void exit() {
		super.exit();
		if (controllerListener1 !=null)
			Controllers.getControllers().get(0).removeListener(controllerListener1);
		if (controllerListener2 !=null)
			Controllers.getControllers().get(1).removeListener(controllerListener2);
	}
	
    @Override
    public void update(float dt) {
        super.update(dt);
        //get input and do actions
        Input input = Gdx.input;
        
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
				- (input.isKeyPressed(Input.Keys.W)? 3: 0)
				+ (input.isKeyPressed(Input.Keys.S)? 3: 0)
			);
		}
		
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

		if (coop !=1) {//P1 controlled by keyboard
			if (XboxListener.speed[0] > 0){
				getPlayer(0).setSpeedHorizontal(
					(WE.CVARS.getValueF("playerWalkingSpeed")*XboxListener.speed[0])
				);
			}
		}
		
		if (coop > 0) {
			if (XboxListener.speed[1] > 0){
				getPlayer(1).setSpeedHorizontal(
					(WE.CVARS.getValueF("playerWalkingSpeed")*XboxListener.speed[1])
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
			getPlayer(0).useItem();
		}
		
		if (coop>-1)
			if (inventoryDownP2==0){
				getPlayer(1).useItem();
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
		getBatch().setShader(getShader());
		getBatch().begin();
			getPlayer(0).getInventory().render(this, getCameras().get(0));
			if (coop > -1)
				getPlayer(1).getInventory().render(this,getCameras().get(1));
		getBatch().end();
	}

	/**
	 * 
	 * @param flag -1 disable, 0 keyboard only, 1 one controller, 2 two controllers
	 */
	public void enableCoop(int flag) {
		coop = flag;
	}

	private static class XboxListener implements ControllerListener {
		private final CustomPlayer player;
		/**
		 * speed of one player
		 */
		public static float[] speed = new float[]{-1,-1};
		private int id;
		private final CustomGameView parent;
		private float oldRTvalue = -1;
		private String OS;

		XboxListener(CustomGameView parent, CustomPlayer controllable, int id) {
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
					parent.openDialogue[id].confirm();
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
				speed[id] = (float) Math.sqrt(
					 xDeflec*xDeflec
					+yDeflec*yDeflec
				);

				if (speed[id]>0.1f)  //move only if stick is a bit moved
					player.setMovement(new Vector3(
							xDeflec,
							yDeflec,
							player.getMovement().z
						)
					);

				if (speed[id] < 0.2f){//if moving to little only set orientation
					speed[id] = 0;
				}

				player.setSpeedHorizontal(
					(WE.CVARS.getValueF("playerWalkingSpeed")*XboxListener.speed[id])
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

		MouseKeyboardListener() {
		}

        @Override
        public boolean keyDown(int keycode) {
            if (!WE.getConsole().isActive()) {
				//use inventory
				if (keycode == Input.Keys.E){
					inventoryDownP1 = 0;//register on down
				}

				//interact
				if (keycode == Input.Keys.F){
					 useDownP1 = 0;//register on down
				}
				
				//editor
				if (keycode == Input.Keys.G){
					 WE.loadEditor(false);
				}
				
				if (keycode == Input.Keys.C) //Select
					toogleCrafting(0);
				

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

                 if (keycode == Input.Keys.ESCAPE)// Gdx.app.exit();
                     WE.showMainMenu();
				 
				if (keycode==Input.Keys.NUM_1)
					getPlayer(0).getInventory().switchItems(true);
			
				if (keycode==Input.Keys.NUM_2)
					getPlayer(0).getInventory().switchItems(false);
				
				if (keycode==Input.Keys.SPACE)
					getPlayer(0).jump();
				
				if (keycode==Input.Keys.TAB)
					if (getOrientation()==0)
						setOrientation(2);
					else 
						setOrientation(0);
				
				//coop controlls
				if (coop==0){
					//p1
					if (keycode == Input.Keys.N) {
						getPlayer(0).attack();
					}
					
					if (keycode == Input.Keys.M) {
						throwDownP1 = 0;
					}
					
					//p2
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
            
            return true;            
        }

        @Override
        public boolean keyUp(int keycode) {
			if (coop==0){
				if (keycode == Input.Keys.N){
					getPlayer(0).attackLoadingStopped();
				}
				
				if (keycode == Input.Keys.M){
					getPlayer(0).throwItem();
				}
				
				if (keycode==Input.Keys.NUMPAD_1){
					getPlayer(1).attackLoadingStopped();
				}
				
				if (keycode==Input.Keys.NUMPAD_2){
					getPlayer(1).throwItem();
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
				if (openDialogue[0] !=null)
					openDialogue[0].confirm();
				else {
					getPlayer(0).attack();
				}
			
			if (button ==Buttons.RIGHT) {
				if (openDialogue[0] ==null) {
					throwDownP1 = 0;
				}
			}
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			if (button ==Buttons.LEFT)
				getPlayer(0).attackLoadingStopped();
			if (button ==Buttons.RIGHT) {
				if (openDialogue[0] ==null) {
					getPlayer(0).throwItem();
					throwDownP1 = -1;
				} else {
					toogleCrafting(0);
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
}