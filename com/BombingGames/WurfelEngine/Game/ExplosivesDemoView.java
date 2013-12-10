package com.BombingGames.WurfelEngine.Game;

import static com.BombingGames.WurfelEngine.Core.Controller.getLightengine;
import static com.BombingGames.WurfelEngine.Core.Controller.getMap;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.ExplosiveBarrel;
import com.BombingGames.WurfelEngine.Core.GameplayScreen;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.Core.WECamera;
import com.BombingGames.WurfelEngine.MainMenu.MainMenuScreen;
import com.BombingGames.WurfelEngine.WEMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;


/**
 *
 * @author Benedikt
 */
public class ExplosivesDemoView extends View {
 
     /**
     *
     */
    public ExplosivesDemoView() {
         super();
         Gdx.input.setInputProcessor(new InputListener());
     }


     @Override
     public void render(){
         super.render();
     } 
     
     private class InputListener implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
            if (!GameplayScreen.msgSystem().isListeningForInput()) {
                //toggle minimap
                 if (keycode == Input.Keys.M){
                     GameplayScreen.msgSystem().add("Minimap toggled to: "+ getController().getMinimap().toggleVisibility());
                 }
                 //toggle fullscreen
                 if (keycode == Input.Keys.F){
                     WEMain.setFullscreen(!WEMain.isFullscreen());
                 }

                 //toggle eathquake
                 if (keycode == Input.Keys.E){ //((ExplosiveBarrel)(getMapData(Chunk.getBlocksX()+5, Chunk.getBlocksY()+5, 3))).explode();
                     getMap().earthquake(5000);
                 }

                 //pause
                 //if (input.isKeyDown(Input.Keys.P)) Gdx.app.setPaused(true);
                 //time is set 0 but the game keeps running
                   if (keycode == Input.Keys.P) {
                     getController().setTimespeed(0);
                  } 

                 //reset zoom
                 if (keycode == Input.Keys.Z) {
                     getController().getCameras().get(0).setZoom(1);
                     GameplayScreen.msgSystem().add("Zoom reset");
                  }  

                 //show/hide light engine
                 if (keycode == Input.Keys.L) {
                     if (getLightengine() != null) getLightengine().RenderData(!getLightengine().isRenderingData());
                  } 

                  if (keycode == Input.Keys.T) {
                     getController().setTimespeed();
                  } 

                 if (keycode == Input.Keys.ESCAPE)// Gdx.app.exit();
                     WEMain.getInstance().setScreen(new MainMenuScreen());
            }
            
             //toggle input for msgSystem
             if (keycode == Input.Keys.ENTER)
                 GameplayScreen.msgSystem().listenForInput(!GameplayScreen.msgSystem().isListeningForInput());

            return true;            
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            GameplayScreen.msgSystem().getInput(character);
            return true;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Coordinate coords = ScreenToGameCoords(screenX,screenY);
            if (coords.getZ() < Map.getBlocksZ()-1) coords.setZ(coords.getZ()+1);
            
            if (button == 0){ //left click
                getMap().setData(coords, Block.getInstance(71, 0, coords));
                WECamera.traceRayTo(coords, true);
            } else {//right click
                if (getMap().getDataSafe(coords) instanceof ExplosiveBarrel)
                    ((ExplosiveBarrel) getMap().getDataSafe(coords)).explode();
                 if (coords.getZ() < Map.getBlocksZ()-1) coords.setZ(coords.getZ()+1);
                 if (getMap().getDataSafe(coords) instanceof ExplosiveBarrel)
                    ((ExplosiveBarrel) getMap().getDataSafe(coords)).explode();
            }
            return true;
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
            getController().getCameras().get(0).setZoom(getController().getCameras().get(0).getZoom() - amount/100f);
            
            GameplayScreen.msgSystem().add("Zoom: " + getController().getCameras().get(0).getZoom());   
            return true;
        }
       
    }

 }
