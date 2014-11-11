package com.BombingGames.Caveland.Game;

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Controllable;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 *
 * @author Benedikt Vogler
 */
public class SplitScreenView extends GameView {
    private Controller controller;
    
    @Override
    public void init(Controller controller) {
        super.init(controller);
        this.controller = controller;
        WE.getEngineView().addInputProcessor(new InputListener());
        
        addCamera(
            new Camera(
				getPlayer(),
				0, //left
				0, //top
				Gdx.graphics.getWidth(), //width
				Gdx.graphics.getHeight()/2,//height
				this,
			controller
			)
        );
        
         Camera camera2 = new Camera(
            0, //left
            Gdx.graphics.getHeight()/2, //top
            Gdx.graphics.getWidth(), //width
            Gdx.graphics.getHeight()/2,//height
			this,
			controller
        );
        addCamera(camera2);
    }
    

    @Override
    public void update(float dt){
        super.update(dt);
        Input input = Gdx.input;
        
        //walks
        getPlayer().walk(
            input.isKeyPressed(Input.Keys.W),
            input.isKeyPressed(Input.Keys.S),
            input.isKeyPressed(Input.Keys.A),
            input.isKeyPressed(Input.Keys.D),
            .25f
        );
        if (input.isKeyPressed(Input.Keys.SPACE))
            getPlayer().jump();
    }
	
	public Controllable getPlayer(){
		return ((CustomGameController) getController()).getPlayer(0);
	}
 
     private class InputListener implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
            if (!WE.getConsole().isActive()) {
                //toggle fullscreen
                if (keycode == Input.Keys.F){
                    WE.setFullscreen(!WE.isFullscreen());
                }


                //reset zoom
                if (keycode == Input.Keys.Z) {
                    getCameras().get(0).setZoom(1);
                    WE.getConsole().add("Zoom reset");
                 }  


                if (keycode == Input.Keys.ESCAPE)// Gdx.app.exit();
                    WE.showMainMenu();
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
    }
}
