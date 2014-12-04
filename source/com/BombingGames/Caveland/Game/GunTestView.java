package com.BombingGames.Caveland.Game;

import com.BombingGames.Caveland.GameObjects.CustomPlayer;
import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Controllable;
import com.BombingGames.WurfelEngine.Core.Gameobjects.PlayerWithWeapon;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.BombingGames.WurfelEngine.shooting.Weapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


/**
 *
 * @author Benedikt
 */
public class GunTestView extends GameView{
    private GunTestController controller;
    private InputListener ip;
    
    @Override
    public void init(Controller controller) {
        super.init(controller);
        this.controller = (GunTestController) controller;
        ip = new InputListener();
        WE.getEngineView().addInputProcessor(ip);
        
        Camera camera = new Camera(
            getPlayer(),
            0, //left
            0, //top
            Gdx.graphics.getWidth(), //width
            Gdx.graphics.getHeight(),//height
			this
        );
        addCamera(camera);
        ((PlayerWithWeapon) getPlayer()).setCamera(camera);
    }
    

    @Override
    public void update(float dt){
        super.update(dt);
        Input input = Gdx.input;
        
        boolean sprint = false;
        if (input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            sprint = controller.sprint();
        }
        
        if (ip.isLftbtndown())
            ((PlayerWithWeapon) getPlayer()).getWeapon().shoot();

        //walks
        getPlayer().walk(
            input.isKeyPressed(Input.Keys.W),
            input.isKeyPressed(Input.Keys.S),
            input.isKeyPressed(Input.Keys.A),
            input.isKeyPressed(Input.Keys.D),
            .25f+(sprint? 0.5f: 0)
        );
        if (input.isKeyPressed(Input.Keys.SPACE))
            getPlayer().jump();
        CustomPlayer player = (CustomPlayer) (getPlayer());
        if (input.isKeyPressed(Input.Keys.Q))
            player.setAimHeight(player.getAimHeight()- dt*0.001f);
        if (input.isKeyPressed(Input.Keys.E))
            player.setAimHeight(player.getAimHeight()+ dt*0.001f);
    }

	public Controllable getPlayer(){
		return ((CustomGameController) getController()).getPlayer(0);
	}
	
     @Override
     public void render(){
        super.render();
        
        ShapeRenderer sh = WE.getEngineView().getShapeRenderer();
        
        Weapon weapon = ((PlayerWithWeapon) getPlayer()).getWeapon();
        if (weapon != null) {
            drawString("Shots: "+weapon.getShotsLoaded()+"/"+weapon.getShots(),
                Gdx.graphics.getWidth()-100,
                Gdx.graphics.getHeight()-100,
                Color.WHITE.cpy()
            );
           
            //laser
            //project
            Point impact = weapon.getImpactPoint();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);

            Color laserlight = Color.RED.cpy();
            laserlight.a = (float) Math.abs(Math.random());
            sh.setColor(laserlight);
            Gdx.gl20.glLineWidth(2);
            sh.begin(ShapeRenderer.ShapeType.Line);
            sh.line(
                -getCameras().get(0).getProjectionSpaceX()+getPlayer().getPosition().getViewSpcX(this),
                -getCameras().get(0).getProjectionSpaceY()+getPlayer().getPosition().getViewSpcY(this)+AbstractGameObject.SCREEN_HEIGHT,
                -getCameras().get(0).getProjectionSpaceX()+impact.getX(),
                -getCameras().get(0).getProjectionSpaceY()+impact.getViewSpcY(this)
             );
            sh.end();
            
        }
         
        //health
        sh.begin(ShapeRenderer.ShapeType.Filled);
        sh.setColor(
            new Color(
                1-(getPlayer().getHealth()/1000f),
                getPlayer().getHealth()/1000f,
                0,
                1
            )
        );
        sh.rect(
            Gdx.graphics.getWidth()/2-100,
            10,
            getPlayer().getHealth()/10*2,
            50
        );
        sh.end();

        sh.begin(ShapeRenderer.ShapeType.Line);
        sh.setColor(Color.BLACK);
        sh.rect(Gdx.graphics.getWidth()/2-100, 10, 200, 50);
        sh.end();
        
        //mana
        sh.begin(ShapeRenderer.ShapeType.Filled);
        sh.setColor(
            new Color(0,0,1,1)
        );
        sh.rect(
            Gdx.graphics.getWidth()/2-100,
            64,
            getPlayer().getMana()/10*2,
            10
        );
        sh.end();

        
        sh.begin(ShapeRenderer.ShapeType.Line);
        sh.setColor(Color.BLACK);
        sh.rect(Gdx.graphics.getWidth()/2-100, 10, 200, 50);
        sh.end();
        

        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl20.glLineWidth(1);
     }
 
     private class InputListener implements InputProcessor {
        private boolean lftbtndown;

        @Override
        public boolean keyDown(int keycode) {
            if (!WE.getConsole().isActive()) {
                //toggle fullscreen
                if (keycode == Input.Keys.F){
                    WE.setFullscreen(!WE.isFullscreen());
                }

               //reload
                if (keycode == Input.Keys.R) {
                    ((PlayerWithWeapon) getPlayer()).getWeapon().reload();
                 }  

                //reset zoom
                if (keycode == Input.Keys.Z) {
                    getCameras().get(0).setZoom(1);
                    WE.getConsole().add("Zoom reset");
                 }  


                if (keycode == Input.Keys.ESCAPE)// Gdx.app.exit();
                    WE.showMainMenu();
                 
                switch(keycode){
                    case Input.Keys.NUM_1:
                        ( (PlayerWithWeapon) getPlayer()).equipWeapon(0);
                    break;
                    case Input.Keys.NUM_2:
                        ( (PlayerWithWeapon) getPlayer()).equipWeapon(1);
                    break;
                    case Input.Keys.NUM_3:
                        ( (PlayerWithWeapon) getPlayer()).equipWeapon(2);
                    break;
                    case Input.Keys.NUM_4:
                        ( (PlayerWithWeapon) getPlayer()).equipWeapon(3);
                    break;
                    case Input.Keys.NUM_5:
                        ( (PlayerWithWeapon) getPlayer()).equipWeapon(4);
                    break;
                    case Input.Keys.NUM_6:
                        ( (PlayerWithWeapon) getPlayer()).equipWeapon(5);
                    break;
                    case Input.Keys.NUM_7:
                        ( (PlayerWithWeapon) getPlayer()).equipWeapon(6);
                    break;
                    case Input.Keys.NUM_8:
                        ( (PlayerWithWeapon) getPlayer()).equipWeapon(7);
                    break;    
                }
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
            if (((PlayerWithWeapon) getPlayer()).getWeapon() != null) {
                lftbtndown=true;
                return true;
            }
            else
                return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
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

        public boolean isLftbtndown() {
            return lftbtndown;
        }
        
    }
}