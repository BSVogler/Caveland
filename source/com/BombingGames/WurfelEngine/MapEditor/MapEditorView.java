/*
 * Copyright 2014 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Bombing Games nor Benedikt Vogler nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.BombingGames.WurfelEngine.MapEditor;

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import static com.BombingGames.WurfelEngine.Core.Controller.requestRecalc;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Sides;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Intersection;
import com.BombingGames.WurfelEngine.Core.Map.Minimap;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 *
 * @author Benedikt Vogler
 */
public class MapEditorView extends GameView {
    private MapEditorController controller;
    /**
     * the camera rendering the sceen
     */
    private Camera camera;
    private float cameraspeed =0.5f;
    /**
     * vector holding information about movement of the camera
     */
    private Vector2 camermove; 
    
    private Navigation nav;
    private BlockSelector bselector;

    @Override
    public void init(Controller controller) {
        super.init(controller);
        Gdx.app.debug("MEView", "Initializing");
        this.controller = (MapEditorController) controller;     
        
        addCamera(camera = new Camera());
        camermove = new Vector2();
        
        controller.setMinimap(
            new Minimap(
                controller,
                getCameras().get(0),
                Gdx.graphics.getWidth() - 400,
                Gdx.graphics.getHeight()-10
            )
        );
        
        controller.getMinimap().toggleVisibility();
        
        nav = new Navigation();
        bselector = new BlockSelector();
        getStage().addActor(bselector);
        

        //setup GUI
        TextureAtlas spritesheet = WE.getAsset("com/BombingGames/WurfelEngine/Core/skin/gui.txt");
        
        //add play button
        final Image playbutton = new Image(spritesheet.findRegion("play_button"));
        playbutton.setX(Gdx.graphics.getWidth()-40);
        playbutton.setY(Gdx.graphics.getHeight()-40);
        playbutton.addListener(new PlayButton(controller, false));
        getStage().addActor(playbutton);
        
         //add load button
        final Image loadbutton = new Image(spritesheet.findRegion("load_button"));
        loadbutton.setX(Gdx.graphics.getWidth()-80);
        loadbutton.setY(Gdx.graphics.getHeight()-40);
        loadbutton.addListener(new LoadButton(this,controller));
        getStage().addActor(loadbutton);
        
         //add save button
        final Image savebutton = new Image(spritesheet.findRegion("save_button"));
        savebutton.setX(Gdx.graphics.getWidth()-120);
        savebutton.setY(Gdx.graphics.getHeight()-40);
        savebutton.addListener(new SaveButton());
        getStage().addActor(savebutton);
        
        //add replaybutton
        final Image replaybutton = new Image(spritesheet.findRegion("replay_button"));
        replaybutton.setX(Gdx.graphics.getWidth()-160);
        replaybutton.setY(Gdx.graphics.getHeight()-40);
        replaybutton.addListener(new PlayButton(controller, true));
        getStage().addActor(replaybutton);
        
        if (Controller.getLightEngine() != null)
            Controller.getLightEngine().setToNoon();
    }

    /**
     *
     * @param speed
     */
    protected void setCameraSpeed(float speed){
        cameraspeed = speed;
    }
    
    /**
     *
     * @param x
     * @param y
     */
    protected void setCameraMoveVector(float x,float y){
        camermove = new Vector2(x, y);
    }
    
    /**
     *
     * @return
     */
    protected Vector2 getCameraMoveVector(){
        return camermove;
    }
    
    @Override
    public void render() {
        super.render();
        nav.render(this);
    }

    @Override
    public void update(final float delta) {
        super.update(delta);
        
        camera.move((int) (camermove.x*cameraspeed*delta), (int) (camermove.y*cameraspeed*delta));
    }

    
    /**
     * Manages the key inpts when in mapeditor view.
     */
    private static class MapEditorInputListener implements InputProcessor {
        private final MapEditorController controller;
        private final MapEditorView view;
        private int buttondown =-1;
        private int layerSelection;

        MapEditorInputListener(MapEditorController controller, MapEditorView view) {
            this.controller = controller;
            this.view = view;
        }


        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Keys.M){
                controller.getMinimap().toggleVisibility();
            }
            
            //manage camera speed
            if (keycode == Keys.SHIFT_LEFT)
                view.setCameraSpeed(1);
        
        //manage camera movement
        if (keycode == Input.Keys.W)
            view.setCameraMoveVector(view.getCameraMoveVector().x, 1);
        if (keycode == Input.Keys.S)
            view.setCameraMoveVector(view.getCameraMoveVector().x, -1);
        if (keycode == Input.Keys.A)
            view.setCameraMoveVector(-1, view.getCameraMoveVector().y);
        if (keycode == Input.Keys.D)
            view.setCameraMoveVector(1, view.getCameraMoveVector().y);
        
        return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (keycode == Keys.SHIFT_LEFT)
                view.setCameraSpeed(0.5f);
            
            if (keycode == Input.Keys.W
                 || keycode == Input.Keys.S
                )
                view.setCameraMoveVector(view.getCameraMoveVector().x, 0);
            
            if (keycode == Input.Keys.A
                 || keycode == Input.Keys.D
                )
                view.setCameraMoveVector(0, view.getCameraMoveVector().y);
             
            
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Coordinate coords = controller.getSelectionEntity().getPos().getCoord();
            
            buttondown=button;
            
            if (button == Buttons.RIGHT){
                //right click
                coords.clampToMapIncludingZ();
                Controller.getMap().setData(
                    coords,
                    Block.getInstance(0)
                );
                
                //getCameras().get(0).traceRayTo(coords, true);
                //gras1.play();
            } else if (button==Buttons.MIDDLE){//middle mouse button
                
                Block block = coords.getBlock();
                controller.getSelectionEntity().setColor(block.getId(), block.getValue());
                
            } else if (button==Buttons.LEFT){ //left click
                Sides normal = Sides.normalToSide(view.screenToGameRaytracing(screenX, screenY).getNormal());
                if (normal==Sides.LEFT)
                    coords = coords.neighbourSidetoCoords(5);
                else if (normal==Sides.TOP)
                    coords.addVector(0, 0, 1);
                else if (normal==Sides.RIGHT)
                    coords = coords.neighbourSidetoCoords(3);

                coords.clampToMapIncludingZ();
                Controller.getMap().setData(coords, controller.getSelectionEntity().getColor());
               // gras2.play();
            }   
            layerSelection = coords.getZ();
            requestRecalc();
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            buttondown = -1;
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            
            //update focusentity
            Point p = view.screenToGameRaytracing(screenX, screenY).getPoint();
                
            if (p != null){
               controller.getSelectionEntity().setPos(p);
            }
            
            Coordinate coords = controller.getSelectionEntity().getPos().getCoord();
            coords.setZ(layerSelection);
            
            if (buttondown==Buttons.LEFT){
                Controller.getMap().setData(coords, controller.getSelectionEntity().getColor());
            } else if (buttondown == Buttons.RIGHT) {
                Controller.getMap().setData(coords, Block.getInstance(0));
            } else return false;
            
            requestRecalc();
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            //update focusentity
            Intersection intersect = view.screenToGameRaytracing(screenX, screenY);
                
            if (intersect.getPoint() != null){
               controller.getSelectionEntity().setPos( intersect.getPoint() );
               controller.getSelectionEntity().setNormal( Sides.normalToSide( intersect.getNormal() ) );
            }
            
            if (screenX<100)
                view.bselector.show();
            else if (screenX > view.bselector.getWidth())
                view.bselector.hide();
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            controller.setCurrentLayer(controller.getCurrentLayer()-amount);
            return true;
        }
    }
    
    private static class PlayButton extends ClickListener{
        private final MapEditorController controller;
        private final boolean replay;
        
        private PlayButton(Controller controller, boolean replay) {
            this.controller = (MapEditorController) controller;
            this.replay = replay;
        }
        
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {            
            controller.switchToGame(replay);
            return true;
        }
    }
    
    private static class LoadButton extends ClickListener{
        private final MapEditorController controller;
        private MapEditorView view;
        
        private LoadButton(GameView view,Controller controller) {
            this.controller = (MapEditorController) controller;
            this.view = (MapEditorView) view;
        }
        
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            controller.getLoadMenu().setOpen(view, true);
            return true;
        }
    }
    
    private static class SaveButton extends ClickListener{
        
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            Controller.getMap().save();
            return true;
        }
    }

    @Override
    public void onEnter() {
        WE.getEngineView().addInputProcessor(new MapEditorInputListener(this.controller, this));
    }
}
