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
import com.BombingGames.WurfelEngine.Core.Gameobjects.Selection;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benedikt Vogler
 */
public class MapEditorController extends Controller {
    private int currentLayer = 0;
    private final Controller gameplayController;
    private final GameView gameplayView;
    /**
     * a clone of the map at the time when last tested.
     */
    private Map mapsave;
    private boolean reverseMap;
    private Selection SelectionEntity;

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
        currentLayer = Map.getBlocksZ();
        SelectionEntity = new Selection();
        //focusentity.setPositionY(Block.DIM2+1f);
        SelectionEntity.spawn(new Point(0, 0, Map.getBlocksZ()-1, true));
    }
    

    @Override
    public void onEnter(){
        Gdx.app.debug("MEController", "entered");
        CVar.get("gamespeed").setValue("0");
        if (reverseMap && mapsave != null)
            Controller.setMap(mapsave);
        else
            mapsave = null;
    }
    
   /**
     * Get the value of currentLayer
     *
     * @return the value of currentLayer
     */
    public int getCurrentLayer() {
        return currentLayer;
    }

    /**
     * Set the value of currentLayer, the layer until every block gets filtered.
     *
     * @param currentLayer new value of currentLayer
     */
    public void setCurrentLayer(int currentLayer) {
        this.currentLayer = currentLayer;
        
        //clamp
        if (currentLayer<0)
            this.currentLayer=0;//min is 1
        else if (currentLayer >= Map.getBlocksZ())
            this.currentLayer=Map.getBlocksZ();
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
