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
import static com.BombingGames.WurfelEngine.Core.Controller.getMap;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import com.BombingGames.WurfelEngine.Core.Gameobjects.EntityShadow;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Selection;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.MapEditor.Toolbar.Tool;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import java.util.ArrayList;

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
    private PlacableSelector leftSelector;
	private PlacableGUI leftColorGUI;
	private PlacableSelector rightSelector;
	private PlacableGUI rightColorGUI;
	
	private Toolbar toolSelection;
	private boolean selecting = false;
	/**
	 * start of selection in view space
	 */
	private int selectDownX;
	/**
	 * start of selection in view space
	 */
	private int selectDownY;

    @Override
    public void init(Controller controller) {
        super.init(controller);
        Gdx.app.debug("MEView", "Initializing");
        this.controller = (MapEditorController) controller;     
        
        addCamera(camera = new Camera(
			this.controller.getGameplayView().getCameras().get(0).getCenter(),
			0,
			0,
			Gdx.graphics.getWidth(),
			Gdx.graphics.getHeight(),
			this)
		);
        
		leftColorGUI = new PlacableGUI(getStage(), this.controller.getSelectionEntity(), true);
		getStage().addActor(leftColorGUI);
        leftSelector = new PlacableSelector(leftColorGUI, true);
        getStage().addActor(leftSelector);
		
		rightColorGUI = new PlacableGUI(getStage(), this.controller.getSelectionEntity(), false);
		getStage().addActor(rightColorGUI);
        rightSelector = new PlacableSelector(rightColorGUI, false);
        getStage().addActor(rightSelector);

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
        savebutton.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Controller.getMap().save(Controller.getMap().getCurrentSaveSlot());
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
		
		toolSelection = new Toolbar(getStage(), spritesheet, leftSelector, rightSelector);
		getStage().addActor(toolSelection);
    }

	@Override
    public void onEnter() {
        WE.getEngineView().addInputProcessor(new MapEditorInputListener(this.controller, this));
		Gdx.input.setCursorCatched(false);
		WE.getEngineView().setMusicLoudness(0);
		Controller.getMap().setGameSpeed(0);
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
     * @param x in view space (?)
     * @param y in view space (?)
     */
    protected void setCameraMoveVector(float x,float y){
        camermove.x = x;
        camermove.y = y;
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
		
		if (controller.getSelectedEntities() != null) {
			ShapeRenderer shr = getShapeRenderer();
			shr.begin(ShapeRenderer.ShapeType.Line);
				shr.setColor(0.8f, 0.8f, 0.8f, 0.8f);
				shr.translate(
					-camera.getViewSpaceX()+camera.getWidthInProjSpc()/2,
					-camera.getViewSpaceY()+camera.getHeightInProjSpc()/2,
					0
				);
					//outlines for selected entities
					for (AbstractEntity selectedEntity : controller.getSelectedEntities()) {
						TextureAtlas.AtlasRegion aR = selectedEntity.getAtlasRegion();
						shr.rect(
							selectedEntity.getPosition().getViewSpcX(this)-aR.getRegionWidth()/2,
							selectedEntity.getPosition().getViewSpcY(this)-aR.getRegionWidth()/2,
							aR.getRegionWidth(),
							aR.getRegionHeight()
						);
					}			
				shr.translate(
					camera.getViewSpaceX()-camera.getWidthInProjSpc()/2,
					camera.getViewSpaceY()-camera.getHeightInProjSpc()/2,
					0
				);

				//selection outline
				if (selecting) {
					shr.rect(
						viewToScreenX(selectDownX, camera),
						viewToScreenY(selectDownY, camera),
						viewToScreenX((int) (screenXtoView(Gdx.input.getX(), camera))-viewToScreenX(selectDownX, camera), camera),//todo bug here
						viewToScreenY((int) (screenYtoView(Gdx.input.getY(), camera))-viewToScreenY(selectDownY, camera), camera)
					);
				}
			shr.end();
		}
        nav.render(this);
		toolSelection.render(WE.getEngineView().getShapeRenderer());
    }

    @Override
    public void update(final float dt) {
        super.update(dt);
        
		if (camera!=null) {
			float rdt= Gdx.graphics.getRawDeltaTime()*1000f;//use "scree"-game time
			camera.move((int) (camermove.x*cameraspeed*rdt), (int) (camermove.y*cameraspeed*rdt));
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
		private Coordinate bucketDown;
		private int lastX;
		private int lastY;

        MapEditorInputListener(MapEditorController controller, MapEditorView view) {
            this.controller = controller;
            this.view = view;
            selection = controller.getSelectionEntity();
        }


        @Override
        public boolean keyDown(int keycode) {
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

			if (keycode==Input.Keys.TAB)
				if (getOrientation()==0)
					setOrientation(2);
				else 
					setOrientation(0);
        
			if (keycode==Input.Keys.FORWARD_DEL)
				for (AbstractEntity ent : controller.getSelectedEntities()) {
					ent.dispose();
				}
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
			buttondown = button;
			selection.update(view, screenX, screenY);
			leftColorGUI.update(selection);
			Coordinate coords = selection.getPosition().getCoord();
            
			if (button==Buttons.MIDDLE){//middle mouse button works as pipet
                CoreData block = coords.getBlock();
				leftColorGUI.setBlock(block);
            } else {
				Tool toggledTool;
				
				if (button == Buttons.RIGHT){
					toggledTool = toolSelection.getRightTool();
				} else {
					toggledTool = toolSelection.getLeftTool();
				}
				
				switch (toggledTool){
					case DRAW:
						RenderBlock block = leftColorGUI.getBlock(selection.getCoordInNormalDirection());
						Controller.getMap().setBlock(block);
						break;
					case REPLACE:
						block = leftColorGUI.getBlock(coords);
						Controller.getMap().setBlock(block);
						break;
					case SELECT:
						if (WE.getEngineView().getCursor()!=2) {//not dragging
							selecting = true;
							selectDownX = (int) screenXtoView(screenX, camera);
							selectDownY = (int) screenYtoView(screenY, camera);
							controller.select( selectDownX, selectDownY, selectDownX, selectDownY );
						}
						break;
					case ERASE:
						if (coords.getZ()>=0)
							Controller.getMap().setBlock(coords, null);
						break;
					case BUCKET:
						bucketDown = coords;
						break;
					case SPAWN:
						leftColorGUI.getEntity().spawn(selection.getNormal().getPosition().cpy());
						break;
				}
				layerSelection = coords.getZ();
			}
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            buttondown = -1;
            
            selection.update(view, screenX, screenY);
			leftColorGUI.update(selection);
			Coordinate coords = selection.getPosition().getCoord();
			
			Tool toggledTool;

			if (button == Buttons.RIGHT){
				toggledTool = toolSelection.getRightTool();
			} else {
				toggledTool = toolSelection.getLeftTool();
			}
			
			switch (toggledTool){
				case DRAW:
					break;
				case REPLACE:
					break;
				case SELECT://release, reset
					selecting = false;
					break;
				case ERASE:
					break;
				case BUCKET:
					if (bucketDown!=null) {
						bucket(bucketDown, coords);
						bucketDown = null;
					}
					break;
				case SPAWN:
					break;
			}
			
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
			selection.update(view, screenX, screenY);
            leftColorGUI.update(selection);

			//dragging selection?
			if (WE.getEngineView().getCursor()==2){
				ArrayList<AbstractEntity> selectedEnts = controller.getSelectedEntities();
				for (AbstractEntity ent : selectedEnts) {
					ent.getPosition().addVector(screenX-lastX, (screenY-lastY)*2, 0);
				}
			} else {
				if (selecting) {//currently selecting
					controller.select(
						selectDownX,
						selectDownY,
						(int) screenXtoView(screenX, camera),
						(int) screenYtoView(screenY, camera)
					);
				}
			}
				
			//dragging with left and has not bucket tool
			if ( (buttondown==Buttons.LEFT && toolSelection.getLeftTool() != Toolbar.Tool.BUCKET)
				&& (buttondown==Buttons.RIGHT && toolSelection.getRightTool() != Toolbar.Tool.BUCKET)
				
			) { 
				Coordinate coords = controller.getSelectionEntity().getPosition().getCoord();
				coords.setZ(layerSelection);
				if (coords.getZ()>=0) {
					if (buttondown==Buttons.LEFT && toolSelection.getLeftTool()==Tool.DRAW){
						RenderBlock block = leftColorGUI.getBlock(coords);
						Controller.getMap().setBlock(block);
					} else if (buttondown == Buttons.RIGHT && toolSelection.getLeftTool()==Tool.DRAW) {
						RenderBlock block = null;
						Controller.getMap().setBlock(block);
					} else return false;
				}
			}
			
			lastX =screenX; 	
			lastY =screenY;
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            selection.update(view, screenX, screenY);
            leftColorGUI.update(selection);
			rightColorGUI.update(selection);
			
			AbstractEntity entityUnderMouse = null;
			if (toolSelection.getLeftTool() == Tool.SELECT && !selecting){
				//find ent under mouse
				for (AbstractEntity ent : getMap().getEntitys()) {
					if (
						ent.getPosition().getViewSpcX(view) + ent.getAtlasRegion().getRegionWidth()/2 >= (int) screenXtoView(screenX, camera) //right sprite borde
						&& ent.getPosition().getViewSpcX(view) - ent.getAtlasRegion().getRegionWidth()/2 <= (int) screenXtoView(screenX, camera) //left spr. border
						&& ent.getPosition().getViewSpcY(view) - ent.getAtlasRegion().getRegionHeight()/2 <= (int) screenYtoView(screenY, camera) //bottom spr. border
						&& ent.getPosition().getViewSpcY(view) + ent.getAtlasRegion().getRegionHeight()/2 >= (int) screenYtoView(screenY, camera) //top spr. border
						&& !(ent instanceof EntityShadow)
						&& !ent.getName().equals("normal")
						&& !ent.getName().equals("selectionEntity")
					)
						entityUnderMouse = ent;
				}
			}
			
			//if entity udner mosue is selected
			if (entityUnderMouse!=null && controller.getSelectedEntities().contains(entityUnderMouse))
				WE.getEngineView().setCursor(2);
			else WE.getEngineView().setCursor(0);

					
			//show selection list if mouse is at position and if tool supports selection		
            if (
				screenX<100
				&& (toolSelection.getLeftTool().selectFromBlocks() || toolSelection.getLeftTool().selectFromEntities())
			)
                view.leftSelector.show();
            else if (view.leftSelector.isVisible() && screenX > view.leftSelector.getWidth())
                view.leftSelector.hide(false);
			
			if (
				screenX > getStage().getWidth()-100
				&& (toolSelection.getRightTool().selectFromBlocks() || toolSelection.getRightTool().selectFromEntities())
			)
                view.rightSelector.show();
            else if (view.rightSelector.isVisible() && screenX < view.rightSelector.getX())
                view.rightSelector.hide(false);
			
			lastX = screenX; 	
			lastY = screenY;
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
			if (!leftSelector.isVisible() && !rightSelector.isVisible()) {
				camera.setZRenderingLimit(camera.getZRenderingLimit()-amount);
				return true;
			}
            return false;
        }

		private void bucket(Coordinate from, Coordinate to) {
			int left = from.getX();
			int right = to.getX();
			if (to.getX()<left) {
				left = to.getX();
				right = from.getX();
			}
			
			int top = from.getY();
			int bottom = to.getY();
			if (to.getY()<top) {
				top = to.getY();
				bottom = from.getY();
			}
			
			for (int x = left; x <= right; x++) {
				for (int y = top; y <= bottom; y++) {
					getMap().setBlock(
						leftColorGUI.getBlock(
							new Coordinate(getMap(), x, y, from.getZ())
						)
					);
				}	
			}
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
}
