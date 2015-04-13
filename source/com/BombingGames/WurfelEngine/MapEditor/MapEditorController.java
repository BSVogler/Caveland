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

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.EntityShadow;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Selection;
import com.BombingGames.WurfelEngine.Core.Map.AbstractMap;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benedikt Vogler
 */
public class MapEditorController extends Controller {
    private final Controller gameplayController;
    private final GameView gameplayView;
    /**
     * a clone of the map at the time when last tested.
     */
    private AbstractMap mapsave;
    private boolean reverseMap;
    private Selection selectionEntity = new Selection();
	private ArrayList<AbstractEntity> selectedEntities = new ArrayList<>(4);

   /**
     * USe this constructor if there are no specific gameplay classes. The editor then chooses some basic classes.
     */
    public MapEditorController() {
        this(null, null);
    }
    
    /**
     * Create an editor controller with coresponding gameplay classes.
     * @param gameplayView the old gameplay classes. If <i>null</i>: the editor then chooses a basic view.
     * @param gameplayController the old gameplay classes.  If <i>null</i>: the editor then chooses a basic controller.
     */
    public MapEditorController(GameView gameplayView, Controller gameplayController) {
        if (gameplayController == null)
            this.gameplayController = new Controller();
        else
            this.gameplayController = gameplayController;
        
        if (gameplayView == null)
            this.gameplayView = new GameView();
        else
            this.gameplayView = gameplayView;
    }

	/**
	 *
	 * @return
	 */
	public Controller getGameplayController() {
		return gameplayController;
	}

	/**
	 *
	 * @return
	 */
	public GameView getGameplayView() {
		return gameplayView;
	}
	
    @Override
    public void init() {
        super.init();
        Gdx.app.log("MapEditorController", "Initializing");
		if (!selectionEntity.spawned()) selectionEntity.spawn(
			new Point(getMap(), 0, 0, getMap().getBlocksZ()-1)
		);
    }
    

    @Override
    public void onEnter(){
		super.onEnter();
		WE.CVARS.get("timespeed").setValue(0f);//stop the game time
        Gdx.app.debug("MEController", "entered");
        if (reverseMap && mapsave != null)
            Controller.setMap(mapsave);
        else
            mapsave = null;
		
        if (!selectionEntity.spawned()) selectionEntity.spawn(
			new Point(getMap(),0, 0, getMap().getBlocksZ()-1)
		);
    }
    
    /**
     *
     * @param reverseMap
     */
    public void setReverseMap(boolean reverseMap) {
        this.reverseMap = reverseMap;
    }
    
    /**
     * Leave editor
     * @param replay true when everything should be reloaded, else just a switch to last status
     */
    public void switchToGame(boolean replay){
		if (replay)
			WE.switchSetupWithInit(gameplayController, gameplayView);
		else
			WE.switchSetup(gameplayController, gameplayView);
    }

    /**
     *Get the entity laying under the cursor.
     * @return
     */
    public Selection getSelectionEntity() {
        return selectionEntity;
    }
	
	/**
	 * Select every entity in this area.
	 * @param x1 view space
	 * @param y1 view space
	 * @param x2 view space
	 * @param y2 view space
	 * @return the selection. unfiltered
	 */
	public ArrayList<AbstractEntity> select(int x1, int y1, int x2, int y2){
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
		
		selectedEntities.clear();
		for (AbstractEntity ent : getMap().getEntitys()) {
			if (
				ent.getPosition().getViewSpcX(gameplayView) + ent.getAtlasRegion().getRegionWidth()/2 >= x1 //right sprite borde
				&& ent.getPosition().getViewSpcX(gameplayView) - ent.getAtlasRegion().getRegionWidth()/2 <= x2 //left spr. border
				&& ent.getPosition().getViewSpcY(gameplayView) - ent.getAtlasRegion().getRegionHeight()/2 <= y2 //bottom spr. border
				&& ent.getPosition().getViewSpcY(gameplayView) + ent.getAtlasRegion().getRegionHeight()/2 >= y1 //top spr. border
			)
				selectedEntities.add(ent);
		}
		return selectedEntities;
	}

	/**
	 * filter map editor entities
	 * @return selected entities but map editor entities 
	 */
	public ArrayList<AbstractEntity> getSelectedEntities() {
		ArrayList<AbstractEntity> selection = new ArrayList<>(selectedEntities.size());
		for (AbstractEntity ent : selectedEntities) {
			if (!(ent instanceof EntityShadow) && !ent.getName().equals("normal") &&!ent.equals(selectionEntity))
				selection.add(ent);
		}
		return selection;
	}
	
	
	@Override
    public void exit(){
        Gdx.app.debug("MEController", "exited");
        try {
            mapsave = Controller.getMap().clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(MapEditorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	/**
	 *
	 * @return
	 */
	public boolean hasMapSave(){
		return mapsave != null;
	}
    
}
