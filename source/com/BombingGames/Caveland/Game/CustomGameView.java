package com.BombingGames.Caveland.Game;

import com.BombingGames.Caveland.GameObjects.CustomPlayer;
import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import static com.BombingGames.WurfelEngine.Core.Controller.getLightEngine;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Controllable;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


/**
 *
 * @author Benedikt
 */
public class CustomGameView extends GameView{
	/**
	 * -1 = not down, 0=just pressed down, >0 time down
	 */
	private float inventoryDown =-1;
	private float useDown =-1;
	/**
	 * -1 disable, 0 keyboard only, 1 one controller, 2 two controllers
	 */
	private int coop = -1;
	
	
    @Override
    public void init(Controller controller) {
        super.init(controller);
        Gdx.app.debug("CustomGameView", "Initializing");
		
		CustomPlayer.loadSheet();
		
		//register Sounds
		Controller.getSoundEngine().register("jetpack", "com/BombingGames/Caveland/sounds/jetpack.wav");
		Controller.getSoundEngine().register("step", "com/BombingGames/Caveland/sounds/step.wav");
		Controller.getSoundEngine().register("urfJump", "com/BombingGames/Caveland/sounds/urf_jump.wav");
		Controller.getSoundEngine().register("loadAttack", "com/BombingGames/Caveland/sounds/loadAttack.wav");
		Controller.getSoundEngine().register("ha", "com/BombingGames/Caveland/sounds/ha.wav");
		Controller.getSoundEngine().register("attack", "com/BombingGames/Caveland/sounds/attack.wav");
		Controller.getSoundEngine().register("impact", "com/BombingGames/Caveland/sounds/impact.wav");
		Controller.getSoundEngine().register("robot1destroy", "com/BombingGames/Caveland/sounds/robot1destroy.wav");
		Controller.getSoundEngine().register("blockDestroy", "com/BombingGames/Caveland/sounds/poch.wav");
		Controller.getSoundEngine().register("vanya_jump", "com/BombingGames/Caveland/sounds/vanya_jump.wav");
		Controller.getSoundEngine().register("wagon", "com/BombingGames/Caveland/sounds/wagon.mp3");
		
		if (coop >- 1){//coop
			((CustomGameController) controller).addPlayer2();
			Camera camera0  = new Camera(
				getPlayer(0),
				0, //left
				0, //top
				Gdx.graphics.getWidth(), //width
				Gdx.graphics.getHeight()/2,//height
				this
			);
			getPlayer(0).setCamera(camera0);
			addCamera(camera0);
			
			Camera camera1 = new Camera(
				getPlayer(1),
				0,
				Gdx.graphics.getHeight()/2,
				Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight()/2,
				this
			);
			addCamera(camera1);
			getPlayer(1).setCamera(camera1);
			
			//if there is second controller use it for second player
			if (Controllers.getControllers().size > 1)
				Controllers.getControllers().get(1).addListener(new XboxListener(this, getPlayer(1), 1));
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
		
		
		WE.getEngineView().setMusic("com/BombingGames/Caveland/music/overworld.mp3");
		WE.getEngineView().setMusicLoudness(CVar.get("music").getValuef());
        
        
//        controller.setMinimap(
//            new Minimap(controller, getCameras().get(0), Gdx.graphics.getWidth() - 400,Gdx.graphics.getHeight()-10)
//        );
    }

	public CustomPlayer getPlayer(int id){
		return ((CustomGameController) getController()).getPlayer(id);
	}
	
	@Override
    public void onEnter() {
        WE.getEngineView().addInputProcessor(new InputListener(this));
		if (Controllers.getControllers().size > 0){
			Controllers.getControllers().get(0).addListener(new XboxListener(this,getPlayer(0),0));
		}
		//hide cursor
		//Gdx.input.setCursorCatched(true);
		//Gdx.input.setCursorPosition(200, 200);
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
				CVar.get("playerWalkingSpeed").getValuef()*(input.isKeyPressed(Input.Keys.SHIFT_LEFT)? 1.5f: 1),
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

		if (XboxListener.speed[0]!=-1){
		Vector2 direction = getPlayer(0).getOrientation();
			getPlayer(0).setHorMovement(
				new Vector2(direction.x, direction.y).scl(
					(CVar.get("playerWalkingSpeed").getValuef()*XboxListener.speed[0])
				)
			);
		}

		if (inventoryDown>-1)
			inventoryDown+=dt;

		if (useDown==0){
			getPlayer(0).getNearestInteractable().interact(getPlayer(0), this);
			//ArrayList<Lore> loren = getPlayer(0).getPosition().getEntitiesNearby(200, Lore.class);
			//if (loren.size()>0)
			//	getPlayer(0).getInventory().addAll(loren.get(0).getContent());
		}
		useDown+=dt;
	}

	@Override
	public void render() {
		super.render();
		getBatch().begin();
		getPlayer(0).getInventory().render(this);
		getPlayer(1).getInventory().render(this);
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
		private final Controllable controllable;
		public static float[] speed = new float[]{-1,-1};
		private int id;
		private final CustomGameView parent;

		XboxListener(CustomGameView parent, Controllable controllable, int id) {
			this.controllable = controllable;
			this.id=id;
			this.parent = parent;
		}

		@Override
		public void connected(com.badlogic.gdx.controllers.Controller controller) {
		}

		@Override
		public void disconnected(com.badlogic.gdx.controllers.Controller controller) {
		}

		@Override
		public boolean buttonDown(com.badlogic.gdx.controllers.Controller controller, int buttonCode) {
			if (buttonCode==13)//A
				controllable.jump();
			
			if (buttonCode==11) //X
				((CustomPlayer) controllable).attack(500);
			
			if (buttonCode==12)//B
				((CustomPlayer) controllable).throwItem();
			
			if (buttonCode==14) //14=Y
				parent.inventoryDown = 0;
			
			if (buttonCode==8)//???
				((CustomPlayer) controllable).getInventory().switchItems(true);
			
			if (buttonCode==9)//???
				((CustomPlayer) controllable).getInventory().switchItems(false);
			
			if (buttonCode==15)//???
				parent.useDown = 0;
			return false;
		}

		@Override
		public boolean buttonUp(com.badlogic.gdx.controllers.Controller controller, int buttonCode) {
			if (buttonCode==14){ //14=Y
				parent.inventoryDown = -1;
			}
			if (buttonCode==11) //X
				((CustomPlayer) controllable).loadAttack();
			
			return true;
		}

		@Override
		public boolean axisMoved(com.badlogic.gdx.controllers.Controller controller, int axisCode, float value) {
				speed[id] = 
					(Math.abs(controller.getAxis(2))
				   +Math.abs(controller.getAxis(3)))/2;
				if (speed[id] < 0.2f){
					if (speed[id] > 0.1f) {
						controllable.setMovement(
							new Vector3(
								controller.getAxis(2),
								controller.getAxis(3),
								controllable.getMovement().z
							)
						);
					}
					speed[id] = 0;
				} else {
					controllable.setMovement(
						new Vector3(
							controller.getAxis(2),
							controller.getAxis(3),
							controllable.getMovement().z
						)
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
    
    private class InputListener implements InputProcessor {
		CustomGameView parent;

		InputListener(CustomGameView parent) {
			this.parent = parent;
		}
		
		
	
		

        @Override
        public boolean keyDown(int keycode) {
            if (!WE.getConsole().isActive()) {
                //toggle minimap
                 if (keycode == Input.Keys.M && getMinimap() != null){
                     getMinimap().toggleVisibility();
                 }

                 //toggle eathquake
                 if (keycode == Input.Keys.E){ //((ExplosiveBarrel)(getMapData(Chunk.getBlocksX()+5, Chunk.getBlocksY()+5, 3))).explode();
					 parent.inventoryDown = 0;//register on down
                 }
				 
				 //toggle eathquake
                 if (keycode == Input.Keys.F){
					 parent.useDown = 0;//register on down
                 }

                 //pause
                 //time is set 0 but the game keeps running
                   if (keycode == Input.Keys.P) {
                     //CVar.get("gamespeed").setValue("0");;
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
					parent.getPlayer(0).getInventory().switchItems(true);
			
				if (keycode==Input.Keys.NUM_2)
					parent.getPlayer(0).getInventory().switchItems(false);
				
				if (keycode==Input.Keys.SPACE)
					parent.getPlayer(0).jump();
				
				if (keycode==Input.Keys.TAB)
					if (parent.getOrientation()==0)
						parent.setOrientation(2);
					else 
						parent.setOrientation(0);
				
            }
            
            return true;            
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			if (button ==Buttons.RIGHT)getPlayer(0).throwItem();
			if (button ==Buttons.LEFT) getPlayer(0).attack(500);
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			if (button ==Buttons.LEFT) getPlayer(0).loadAttack();
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
            
            WE.getConsole().add("Zoom: " + getCameras().get(0).getZoom());   
            return true;
        }
    }
}