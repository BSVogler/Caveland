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

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Selection;
import com.BombingGames.WurfelEngine.Core.Map.Map;
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
    private Map mapsave;
    private boolean reverseMap;
    private Selection SelectionEntity;
	private ArrayList<AbstractEntity> selectedEntities = new ArrayList<>(4);

   /**
     * USe this constructor if there are no specific gameplay classes. The editor then chooses some basic classes.
     */
    public MapEditorController() {
        this(null, null);
    }
    
    /**
     * Create an editor controller with coressponding gameplay classes.
     * @param gameplayView the old gameplay classes. If "null": the editor then chooses a basic controller.
     * @param gameplayController the old gameplay classes.  If "null": the editor then chooses a basic view.
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

    @Override
    public void init() {
        super.init();
        Gdx.app.log("MapEditorController", "Initializing");
        SelectionEntity = new Selection();
        //focusentity.setPositionY(Block.DIM2+1f);
        SelectionEntity.spawn(new Point(0, 0, Map.getBlocksZ()-1));
    }
    

    @Override
    public void onEnter(){
		super.onEnter();
		CVar.get("timespeed").setValuef(0f);
        Gdx.app.debug("MEController", "entered");
        if (reverseMap && mapsave != null)
            Controller.setMap(mapsave);
        else
            mapsave = null;
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
        return SelectionEntity;
    }
	
	/**
	 * select every entity in this area
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2 
	 */
	public void select(int x1, int y1, int x2, int y2){
		//1 values are the smaller ones, make sure that this is the case
		if (x1 < x2) {
			int tmp = x1;
			x1 = x2; 
			x2 = tmp;
		}
		
		if (y1 < y2) {
			int tmp = y1;
			y1 = y2; 
			y2 = tmp;
		}
		
		selectedEntities.clear();
		for (AbstractEntity ent : getMap().getEntitys()) {
			selectedEntities.add(ent);
		}
	}

	public ArrayList<AbstractEntity> getSelectedEntities() {
		return selectedEntities;
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
	
	public boolean hasMapSave(){
		return mapsave != null;
	}
    
}
