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

package com.bombinggames.wurfelengine.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import static com.bombinggames.wurfelengine.core.Controller.getMap;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.gameobjects.Cursor;
import com.bombinggames.wurfelengine.core.gameobjects.EntityShadow;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Position;
import com.bombinggames.wurfelengine.mapeditor.Toolbar.Tool;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Benedikt Vogler
 */
public class EditorView extends GameView implements Telegraph {
    private Controller controller;
	/**
	 * used for copying data from the current game view
	 */
	private GameView gameplayView;
    /**
     * the camera rendering the sceen
     */
    private Camera camera;
    private float cameraspeed =0.5f;
    /**
     * vector holding information about movement of the camera
     */
    private final Vector2 camermove = new Vector2(); 
    
    private final Navigation nav = new Navigation();
    private PlacableTable leftSelector;
	private PlacableGUI leftColorGUI;
	private PlacableTable rightSelector;
	private PlacableGUI rightColorGUI;
	
	private Toolbar toolSelection;
	private boolean selecting = false;
	/**
	 * start of selection in screen space
	 */
	private int selectDownX;
	/**
	 * start of selection in screen space
	 */
	private int selectDownY;
	/**
	 * the direction where the entities move
	 */
	private int moveEntities;

    @Override
    public void init(final Controller controller, final GameView oldView) {
        super.init(controller, oldView);
        Gdx.app.debug("MEView", "Initializing");
		
        this.controller = controller;
        this.gameplayView = oldView;
		
		if (oldView != null) {
			setRenderStorage(oldView.getRenderStorage());//use the same render storage
		}
		
		if (oldView != null){
			camera = new Camera(
				this,
				0,
				0,
				Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(),
				oldView.getCameras().get(0).getCenter()//keep position
			);
		} else {
			camera = new Camera(
				this,
				0,
				0,
				Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight()
			);
		}
		
        addCamera(camera);
		
		leftColorGUI = new PlacableGUI(getStage(), this.controller.getCursor(), true);
		getStage().addActor(leftColorGUI);
        leftSelector = new PlacableTable(leftColorGUI, true);
        getStage().addActor(leftSelector);
		
		rightColorGUI = new PlacableGUI(getStage(), this.controller.getCursor(), false);
		getStage().addActor(rightColorGUI);
        rightSelector = new PlacableTable(rightColorGUI, false);
        getStage().addActor(rightSelector);

        //setup GUI
        TextureAtlas spritesheet = WE.getAsset("com/bombinggames/wurfelengine/core/skin/gui.txt");
        
         //add load button
//        final Image loadbutton = new Image(spritesheet.findRegion("load_button"));
//        loadbutton.setX(Gdx.graphics.getWidth()-80);
//        loadbutton.setY(Gdx.graphics.getHeight()-40);
//        loadbutton.addListener(new LoadButton(this,controller));
//        getStage().addActor(loadbutton);
        
        //add save button
        final Image savebutton = new Image(spritesheet.findRegion("save_button"));
		savebutton.setX(Gdx.graphics.getWidth() - 150);
		savebutton.setY(Gdx.graphics.getHeight() - 50);
		savebutton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.save();
			}
		});
		getStage().addActor(savebutton);
        
        if (Controller.getLightEngine() != null)
            Controller.getLightEngine().setToNoon(getCameras().get(0).getCenter());
		
		toolSelection = new Toolbar(this, spritesheet, leftSelector, rightSelector, controller.getCursor());
		getStage().addActor(toolSelection);
    }

	@Override
    public void onEnter() {
		camera.setCenter(gameplayView.getCameras().get(0).getCenter().cpy());//always keep the camera position
        WE.getEngineView().addInputProcessor(new EditorInputListener(this.controller, this));
		Gdx.input.setCursorCatched(false);
		WE.SOUND.pauseMusic();
		WE.getCVars().get("timespeed").setValue(0f);//stop the game time
    }
	
	/**
	 * Select every entity in this area.
	 * @param x1 view space
	 * @param y1 view space
	 * @param x2 view space
	 * @param y2 view space
	 */
	public void select(int x1, int y1, int x2, int y2) {
		//1 values are the smaller ones, make sure that this is the case
		if (x2 < x1) {
			int tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		if (y2 < y1) {
			int tmp = y1;
			y1 = y2;
			y2 = tmp;
		}
		ArrayList<AbstractEntity> newSel = new ArrayList<>(4);
		for (AbstractEntity ent : getMap().getEntities()) {
			if (ent.hasPosition()) {
				TextureAtlas.AtlasRegion aR = ent.getSprite();
				if (aR != null
					&& ent.getPosition().getViewSpcX() + ent.getSprite().getRegionWidth() / 2 >= x1 //right sprite borde
					&& ent.getPosition().getViewSpcX() - ent.getSprite().getRegionWidth() / 2 <= x2 //left spr. border
					&& ent.getPosition().getViewSpcY() - ent.getSprite().getRegionHeight() / 2 <= y2 //bottom spr. border
					&& ent.getPosition().getViewSpcY() + ent.getSprite().getRegionHeight() / 2 >= y1 //top spr. border
				){
					newSel.add(ent);
					MessageManager.getInstance().dispatchMessage(
						this,
						ent,
						Events.selectInEditor.getId()
					);
				}
			}
		}
		//identify the ones which are deselected
		ArrayList<AbstractEntity> unselect = getController().getSelectedEntities();
		unselect.removeAll(newSel);
		for (AbstractEntity unEnt : unselect) {
			MessageManager.getInstance().dispatchMessage(
				this,
				unEnt,
				Events.deselectInEditor.getId()
			);
		}
		
		getController().setSelectedEnt(newSel);
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
     * @param x in game space
     * @param y in game space
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
		ShapeRenderer shr = getShapeRenderer();
		if (controller.getSelectedEntities() != null) {
			shr.begin(ShapeRenderer.ShapeType.Line);
			shr.setColor(0.8f, 0.8f, 0.8f, 0.8f);
			
			//outlines for selected entities
			for (AbstractEntity selectedEntity : controller.getSelectedEntities()) {
				TextureAtlas.AtlasRegion aR = selectedEntity.getSprite();
				shr.rect(
					selectedEntity.getPosition().getProjectionSpaceX(this, camera) - aR.getRegionWidth() / 2,
					selectedEntity.getPosition().getProjectionSpaceY(this, camera) - aR.getRegionHeight()/ 2,
					aR.getRegionWidth(),
					aR.getRegionHeight()
				);
				drawString(
					selectedEntity.getName(),
					selectedEntity.getPosition().getProjectionSpaceX(this, camera) + aR.getRegionWidth() / 2,
					selectedEntity.getPosition().getProjectionSpaceY(this, camera) - aR.getRegionHeight()/ 2,
					true
				);
			}
			shr.end();
			shr = WE.getEngineView().getShapeRenderer();
			shr.begin(ShapeRenderer.ShapeType.Line);
			//selection outline
			if (selecting) {
				WE.getEngineView().getShapeRenderer().rect(
					selectDownX,
					-selectDownY+Gdx.graphics.getHeight(),
					Gdx.input.getX() - selectDownX,//todo bug here
					(selectDownY-Gdx.input.getY())
				);
			}
			WE.getEngineView().getShapeRenderer().end();
		}
		
		nav.render(this);
		toolSelection.render(shr);
    }

    @Override
	public void update(final float dt) {
		if (camera != null) {
			float rdt = Gdx.graphics.getRawDeltaTime() * 1000f;//use "screen"-game time
			camera.move((int) (camermove.x * cameraspeed * rdt), (int) (camermove.y * cameraspeed * rdt));
		}
		
		//move selected entities up or down
		if (moveEntities != 0) {
			controller.getSelectedEntities().forEach(
				(AbstractEntity e) -> e.getPosition().setZ(e.getPosition().getZ()+moveEntities*5)
			);
		}
		
		super.update(dt);
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return true;
	}

    
    /**
     * Manages the key inpts when in mapeditor view.
     */
    private class EditorInputListener implements InputProcessor {
        private final Controller controller;
        private final EditorView view;
		/**
		 * the last button which went down
		 */
        private int buttondown =-1;
		/**
		 * the z layer during touch down
		 */
        private int dragLayer;
        private final Cursor cursor;
		private Coordinate bucketDown;
		private int lastX;
		private int lastY;
		private boolean symDown;
		private boolean shiftDown;

        EditorInputListener(Controller controller, EditorView view) {
            this.controller = controller;
            this.view = view;
            cursor = controller.getCursor();
        }


        @Override
        public boolean keyDown(int keycode) {
            //manage camera speed
            if (keycode == Keys.SHIFT_LEFT)
                view.setCameraSpeed(1);

			if (keycode == Keys.G)
				WE.switchView(gameplayView, false);
			
			if (keycode == Keys.ESCAPE)
				WE.switchView(gameplayView, false);
			
			if (keycode == Keys.E) {
				moveEntities = 1;
			}

			if (keycode == Input.Keys.Q) {
				moveEntities = -1;
			}
		
			if (keycode == Input.Keys.SYM) {
				symDown = true;
			}

			if (keycode == Input.Keys.SHIFT_LEFT) {
				shiftDown = true;
			}
			
			//manage camera movement
			if (keycode == Input.Keys.W)
				view.setCameraMoveVector(view.getCameraMoveVector().x, -1);
			if (keycode == Input.Keys.S)
				view.setCameraMoveVector(view.getCameraMoveVector().x, 1);
			if (keycode == Input.Keys.A)
				view.setCameraMoveVector(-1, view.getCameraMoveVector().y);
			if (keycode == Input.Keys.D)
				view.setCameraMoveVector(1, view.getCameraMoveVector().y);

			if (keycode == Input.Keys.FORWARD_DEL || keycode == Input.Keys.DEL) {
				for (AbstractEntity ent : controller.getSelectedEntities()) {
					ent.dispose();
				}
			}
			
			if (keycode == Input.Keys.NUM_1) {
				if (Tool.values().length > 0) {
					toolSelection.selectTool(true, Tool.values()[0]);
				}
			}
			if (keycode == Input.Keys.NUM_2) {
				if (Tool.values().length > 1) {
					toolSelection.selectTool(true, Tool.values()[1]);
				}
			}
			if (keycode == Input.Keys.NUM_3) {
				if (Tool.values().length > 2) {
					toolSelection.selectTool(true, Tool.values()[2]);
				}
			}
			if (keycode == Input.Keys.NUM_4) {
				if (Tool.values().length > 3) {
					toolSelection.selectTool(true, Tool.values()[3]);
				}
			}
			if (keycode == Input.Keys.NUM_5) {
				if (Tool.values().length > 4) {
					toolSelection.selectTool(true, Tool.values()[4]);
				}
			}
			return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (keycode == Keys.SHIFT_LEFT) {
                view.setCameraSpeed(0.5f);
				shiftDown = false;
			}
            
            if (keycode == Input.Keys.W
                 || keycode == Input.Keys.S
                )
                view.setCameraMoveVector(view.getCameraMoveVector().x, 0);
            
           if (keycode == Input.Keys.A
				|| keycode == Input.Keys.D) {
				view.setCameraMoveVector(0, view.getCameraMoveVector().y);
			}
		   
		   if (keycode == Keys.E || keycode == Keys.Q) {
				moveEntities = 0;
			}
		   
		   if (keycode == Input.Keys.SYM)
				symDown = false;
            
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
			if (symDown && character == 'z' && !shiftDown) {
				controller.undoCommand();
				return true;
			}
			
			if (symDown && (character == 'Z' || shiftDown && character == 'z')) {
				controller.redoCommand();
				return true;
			}
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			buttondown = button;
			cursor.update(view, screenX, screenY);
			leftColorGUI.update(cursor);
			Coordinate coords = cursor.getPosition().toCoord();
            
			//pipet
			if (button==Buttons.MIDDLE || (button==Buttons.LEFT && Gdx.input.isKeyPressed(Keys.ALT_LEFT))){//middle mouse button works as pipet
                Block block = coords.getBlock();
				leftColorGUI.setBlock(block);
            } else {
				Tool toolUsed;
				
				if (button == Buttons.RIGHT){
					toolUsed = toolSelection.getRightTool();
				} else {
					toolUsed = toolSelection.getLeftTool();
				}
				
				switch (toolUsed){
					case DRAW:
						dragLayer = cursor.getCoordInNormalDirection().getZ();
						getController().executeCommand(toolUsed.getCommand(gameplayView, cursor, leftColorGUI));
						break;
					case REPLACE:
					case ERASE:
						dragLayer = coords.getZ();
						getController().executeCommand(toolUsed.getCommand(gameplayView, cursor, leftColorGUI));
						break;
					case BUCKET:
						bucketDown = coords;
						break;
					case SELECT:
						if (WE.getEngineView().getCursor() != 2) {//not dragging
							selecting = true;
							selectDownX = screenX;
							selectDownY = screenY;
							select((int) screenXtoView(screenX, camera), (int) screenYtoView(screenY, camera), (int) screenXtoView(screenX, camera), (int) screenYtoView(screenY, camera));
						}
						break;
					case SPAWN:
						getController().executeCommand(toolUsed.getCommand(gameplayView, cursor, leftColorGUI)
						);
						break;
				}
			}
            return false;
        }

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			buttondown = -1;

			cursor.update(view, screenX, screenY);
			leftColorGUI.update(cursor);
			Coordinate coords = cursor.getPosition().toCoord();

			Tool toggledTool;

			if (button == Buttons.RIGHT) {
				toggledTool = toolSelection.getRightTool();
			} else {
				toggledTool = toolSelection.getLeftTool();
			}

			switch (toggledTool) {
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
					if (bucketDown != null) {
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
			cursor.update(view, screenX, screenY);
			leftColorGUI.update(cursor);

			//dragging selection?
			if (WE.getEngineView().getCursor() == 2) {
				ArrayList<AbstractEntity> selectedEnts = controller.getSelectedEntities();
				//remvoe duplicate position
				Map<Position, AbstractEntity> map = new LinkedHashMap<>(selectedEnts.size());
				for (AbstractEntity ays : selectedEnts) {
					map.put(ays.getPosition(), ays);
				}
				selectedEnts.clear();
				selectedEnts.addAll(map.values());
				
				for (AbstractEntity ent : selectedEnts) {
					ent.getPosition().add(screenX - lastX, (screenY - lastY) * 2, 0);
				}
			} else if (selecting) {//currently selecting
				select(
					(int) screenXtoView(selectDownX, camera),
					(int) screenYtoView(selectDownY, camera),
					(int) screenXtoView(screenX, camera),
					(int) screenYtoView(screenY, camera)
				);
			}
				
			//dragging with left and draw tool
			if ((buttondown == Buttons.LEFT && toolSelection.getLeftTool() == Toolbar.Tool.DRAW)
				|| (buttondown == Buttons.RIGHT && toolSelection.getRightTool() == Toolbar.Tool.DRAW)) {
				Coordinate coords = controller.getCursor().getPosition().toCoord();
				coords.setZ(dragLayer);
				if (coords.getZ() >= 0) {
					if (Controller.getMap().getBlock(coords) == null) {
						Controller.getMap().setBlock(coords, leftColorGUI.getBlock());
					}
				}
			}
			
			if ((buttondown==Buttons.LEFT && toolSelection.getLeftTool()== Toolbar.Tool.REPLACE)
				|| (buttondown==Buttons.RIGHT && toolSelection.getRightTool() == Toolbar.Tool.REPLACE)
			) {
				Coordinate coords = controller.getCursor().getPosition().toCoord();
				coords.setZ(dragLayer);
				if (coords.getZ() >= 0) {
					if (Controller.getMap().getBlock(coords) != null) {
						Controller.getMap().setBlock(coords, leftColorGUI.getBlock());
					}
				}
			}
			
			lastX =screenX; 	
			lastY =screenY;
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            cursor.update(view, screenX, screenY);
            leftColorGUI.update(cursor);
			rightColorGUI.update(cursor);
			
			AbstractEntity entityUnderMouse = null;
			if (toolSelection.getLeftTool() == Tool.SELECT && !selecting){
				//find ent under mouse
				for (AbstractEntity ent : getMap().getEntities()) {
					if (
						ent.hasPosition()
						&& ent.getSpriteId() > 0
						&& ent.getSpriteValue() > -1
						&& ent.getPosition().getViewSpcX() + ent.getSprite().getRegionWidth()/2 >= (int) screenXtoView(screenX, camera) //right sprite borde
						&& ent.getPosition().getViewSpcX() - ent.getSprite().getRegionWidth()/2 <= (int) screenXtoView(screenX, camera) //left spr. border
						&& ent.getPosition().getViewSpcY() - ent.getSprite().getRegionHeight()/2 <= (int) screenYtoView(screenY, camera) //bottom spr. border
						&& ent.getPosition().getViewSpcY() + ent.getSprite().getRegionHeight()/2 >= (int) screenYtoView(screenY, camera) //top spr. border
						&& !(ent instanceof EntityShadow)
						&& !ent.getName().equals("normal")
						&& !ent.getName().equals("selectionEntity")
					)
						entityUnderMouse = ent;
				}
			}
			
			//if entity under mosue is selected
			if (entityUnderMouse != null && controller.getSelectedEntities().contains(entityUnderMouse)) {
				WE.getEngineView().setCursor(2);
			} else {
				WE.getEngineView().setCursor(0);
			}

					
			//show selection list if mouse is at position and if tool supports selection		
//            if (
//				screenX<100
//				&& (toolSelection.getLeftTool().selectFromBlocks() || toolSelection.getLeftTool().selectFromEntities())
//			)
//                view.leftSelector.show();
//            else if (view.leftSelector.isVisible() && screenX > view.leftSelector.getWidth())
//                view.leftSelector.hide(false);
//			
//			if (
//				screenX > getStage().getWidth()-100
//				&& (toolSelection.getRightTool().selectFromBlocks() || toolSelection.getRightTool().selectFromEntities())
//			)
//                view.rightSelector.show();
//            else if (view.rightSelector.isVisible() && screenX < view.rightSelector.getX())
//                view.rightSelector.hide(false);
			
			lastX = screenX; 	
			lastY = screenY;
            return false;
        }

		@Override
		public boolean scrolled(int amount) {
			camera.setZRenderingLimit(view.getRenderStorage().getZRenderingLimit()*Block.GAME_EDGELENGTH - amount);
			return true;
		}

		private void bucket(Coordinate from, Coordinate to) {
			int left = from.getX();
			int right = to.getX();
			if (to.getX() < left) {
				left = to.getX();
				right = from.getX();
			}

			int top = from.getY();
			int bottom = to.getY();
			if (to.getY() < top) {
				top = to.getY();
				bottom = from.getY();
			}

			for (int x = left; x <= right; x++) {
				for (int y = top; y <= bottom; y++) {
					getMap().setBlock(
						new Coordinate(x, y, from.getZ()),
						leftColorGUI.getBlock()
					);
				}
			}
		}

	}

	private class LoadButton extends ClickListener {

		private final Controller controller;
		private final EditorView view;

		private LoadButton(GameView view, Controller controller) {
			this.controller = controller;
			this.view = (EditorView) view;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			getLoadMenu().setOpen(view, true);
			return true;
		}
	}
}
