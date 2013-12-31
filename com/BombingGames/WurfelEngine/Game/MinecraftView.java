package com.BombingGames.WurfelEngine.Game;

import com.BombingGames.WurfelEngine.Core.Controller;
import static com.BombingGames.WurfelEngine.Core.Controller.getLightengine;
import static com.BombingGames.WurfelEngine.Core.Controller.getMap;
import static com.BombingGames.WurfelEngine.Core.Controller.requestRecalc;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.GameplayScreen;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.MainMenu.MainMenuScreen;
import com.BombingGames.WurfelEngine.WEMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.openal.Ogg.Sound;


/**
 *
 * @author Benedikt
 */
public class MinecraftView extends View{
     private MinecraftController controller;
     private Sound gras1;
     private Sound gras2;

    @Override
    public void init(Controller controller) {
        super.init(controller);
         this.controller = (MinecraftController) controller;
         
        gras1 =  (Sound) Gdx.audio.newSound(Gdx.files.internal("com/BombingGames/Game/Sounds/grass1.ogg"));
        gras2 = (Sound) Gdx.audio.newSound(Gdx.files.internal("com/BombingGames/Game/Sounds/grass2.ogg"));
        
         this.controller.getBlockToolbar().setPos(
            (Gdx.graphics.getWidth()/2)
            - Block.getSpritesheet().findRegion("toolbar").originalWidth/2,
            (Gdx.graphics.getHeight())
            - Block.getSpritesheet().findRegion("toolbar").originalHeight);
         
       Gdx.input.setInputProcessor(new InputListener());
    }
    
    
     @Override
     public void render(){
         super.render();
         controller.getBlockToolbar().render(this);
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
                     Gdx.app.log("DEBUG","Set to fullscreen:"+!WEMain.isFullscreen());
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
                 
                 if (keycode == Input.Keys.K) {
                    Zombie zombie = (Zombie) AbstractEntity.getInstance(
                        43,
                        0,
                        controller.getFocusentity().getPos()
                    );
                    zombie.setTarget(controller.getPlayer());
                    zombie.exist();   
                 }
             
                if (keycode == Input.Keys.NUM_1) controller.getBlockToolbar().setSelection(0);
                if (keycode == Input.Keys.NUM_2) controller.getBlockToolbar().setSelection(1);
                if (keycode == Input.Keys.NUM_3) controller.getBlockToolbar().setSelection(2);
                if (keycode == Input.Keys.NUM_4) controller.getBlockToolbar().setSelection(3);
                if (keycode == Input.Keys.NUM_5) controller.getBlockToolbar().setSelection(4);
                if (keycode == Input.Keys.NUM_6) controller.getBlockToolbar().setSelection(5);
                if (keycode == Input.Keys.NUM_7) controller.getBlockToolbar().setSelection(6);
                if (keycode == Input.Keys.NUM_8) controller.getBlockToolbar().setSelection(7);
                if (keycode == Input.Keys.NUM_9) controller.getBlockToolbar().setSelection(8);
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
            if (coords.getZ() < Map.getBlocksZ()-1) coords.addVector(0, 0, 1);
            
            if (button == 0){ //left click
                Controller.getMap().setData(coords, Block.getInstance(0));
                requestRecalc();
                //getCameras().get(0).traceRayTo(coords, true);
                gras1.play();
            } else {//right click
                if (Controller.getMap().getBlock(coords).getId() == 0){
                    Controller.getMap().setData(coords, Block.getInstance(controller.getBlockToolbar().getSelectionID(),0,coords));
                    requestRecalc();
                    gras2.play();
                }
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
            controller.getFocusentity().setPos(ScreenToGameCoords(screenX,screenY).addVector(0, 0, 1));
            return true;
        }

        @Override
        public boolean scrolled(int amount) {
            getController().getCameras().get(0).setZoom(getController().getCameras().get(0).getZoom() - amount/100f);
            
            GameplayScreen.msgSystem().add("Zoom: " + getController().getCameras().get(0).getZoom());   
            return true;
        }
    }
 }
