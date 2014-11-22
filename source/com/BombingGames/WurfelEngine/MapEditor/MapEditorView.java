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
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Selection;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Sides;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Minimap;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
    private Vector2 camermove = new Vector2(); 
    
    private Navigation nav = new Navigation();
    private BlockSelector bselector;
	private ColorGUI colorGUI;
	
	private Button blockButton;
	private Button entitiesButton;

    @Override
    public void init(Controller controller) {
        super.init(controller);
        Gdx.app.debug("MEView", "Initializing");
        this.controller = (MapEditorController) controller;     
        
        addCamera(camera = new Camera(this, controller));
        
        if (getMinimap()==null)
			setMinimap(
            new Minimap(
					getCameras().get(0),
					Gdx.graphics.getWidth() - 600,
					Gdx.graphics.getHeight()-300
				)
			);
        
		colorGUI = new ColorGUI(getStage());
		getStage().addActor(colorGUI);
        bselector = new BlockSelector(colorGUI);
        getStage().addActor(bselector);

        blockButton = new TextButton("Blocks", WE.getEngineView().getSkin());
		blockButton.setPosition(bselector.getWidth(), 100);
		blockButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				bselector.showBlocks();
			}
		});
		getStage().addActor(blockButton);
		
		entitiesButton = new TextButton("Entitys", WE.getEngineView().getSkin());
		entitiesButton.setPosition(bselector.getWidth(), 200);
		entitiesButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				bselector.showEntities();
			}
		});
		getStage().addActor(entitiesButton);


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
        savebutton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				Controller.getMap().save();
			}
		});
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
    public void update(final float dt) {
        super.update(dt);
        
		if (camera!=null) {
			camera.move((int) (camermove.x*cameraspeed*dt), (int) (camermove.y*cameraspeed*dt));
			camera.setZRenderingLimit(controller.getCurrentLayer());
		}
    }

    
    /**
     * Manages the key inpts when in mapeditor view.
     */
    private class MapEditorInputListener implements InputProcessor {
        private final MapEditorController controller;
        private final MapEditorView view;
        private int buttondown =-1;
        private int layerSelection;
        private Selection selection;

        MapEditorInputListener(MapEditorController controller, MapEditorView view) {
            this.controller = controller;
            this.view = view;
            selection = controller.getSelectionEntity();
        }


        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Keys.M){
                getMinimap().toggleVisibility();
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
            Coordinate coords = selection.getPosition().getCoord();
            
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
                coords.clampToMapIncludingZ();
                Block block = coords.getBlock();
                colorGUI.setBlock(block.getId(), block.getValue());
            } else if (button==Buttons.LEFT){ //left click
                Sides normal = selection.getNormalSides();
                if (normal==Sides.LEFT)
                    coords = coords.neighbourSidetoCoords(5);
                else if (normal==Sides.TOP)
                    coords.addVector(0, 0, 1);
                else if (normal==Sides.RIGHT)
                    coords = coords.neighbourSidetoCoords(3);

                coords.clampToMapIncludingZ();
                Controller.getMap().setData(coords, colorGUI.getBlock(controller.getSelectionEntity().getPosition().getCoord()));
               // gras2.play();
            }   
            layerSelection = coords.getZ();
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            buttondown = -1;
            
            selection.update(view, screenX, screenY);
            
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
			selection.update(view, screenX, screenY);
            
            Coordinate coords = controller.getSelectionEntity().getPosition().getCoord();
            coords.setZ(layerSelection);
            
            if (buttondown==Buttons.LEFT){
                Controller.getMap().setData(coords, colorGUI.getBlock(controller.getSelectionEntity().getPosition().getCoord()));
            } else if (buttondown == Buttons.RIGHT) {
                Controller.getMap().setData(coords, Block.getInstance(0));
            } else return false;
            
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            selection.update(view, screenX, screenY);
            
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
    
    private class LoadButton extends ClickListener{
        private final MapEditorController controller;
        private MapEditorView view;
        
        private LoadButton(GameView view,Controller controller) {
            this.controller = (MapEditorController) controller;
            this.view = (MapEditorView) view;
        }
        
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            getLoadMenu().setOpen(view, true);
            return true;
        }
    }
    
    @Override
    public void onEnter() {
        WE.getEngineView().addInputProcessor(new MapEditorInputListener(this.controller, this));
    }
}
