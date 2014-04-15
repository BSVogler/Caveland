package com.BombingGames.WurfelEngine.Core;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractCharacter;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.LightEngine.LightEngine;
import com.BombingGames.WurfelEngine.Core.Map.Cell;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Minimap;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *A controller manages the map and the game data.
 * @author Benedikt Vogler
 */
public class Controller {
    private static LightEngine lightEngine;
    private static Map map;
    private static boolean recalcRequested;
    private boolean initalized= false;
    
        
    private Minimap minimap;
    /** The speed of time. 1 = real time;*/
    private float timespeed = 1;
    private AbstractCharacter player;  
    
    private FPSdiag fpsdiag;
    
    /**
     * Shoud be called before the objects get initialized.
     * Initializes class fields.
     */
    public static void classInit(){
        newMap();
    }
    
    /**
     * This method works like a constructor. Everything is loaded here.
     */
    public void init(){
        Gdx.app.log("Controller", "Initializing");
        fpsdiag = new FPSdiag(10,50);
        
        if (WE.getCurrentConfig().useLightEngine()){
            Controller.lightEngine = new LightEngine(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        }
        
        recalcRequested = true;
        initalized = true;
    }
        
     /**
     * Main method which is called every refresh.
     * @param delta time since last call
     */
    public void update(float delta) {
        delta *= timespeed;
        if (lightEngine != null) lightEngine.update(delta);
        
         //update the log
        GameplayScreen.msgSystem().update(delta);
        
        
        
        //update every static update method
        AbstractGameObject.updateStaticUpdates(delta);
        
        //update every block on the map
        Cell[][][] mapdata = map.getData();
        for (int x=0; x < Map.getBlocksX(); x++)
            for (int y=0; y < Map.getBlocksY(); y++)
                for (int z=0; z < Map.getBlocksZ(); z++)
                    mapdata[x][y][z].getBlock().update(delta);
        
        //update every entity
        for (int i = 0; i < map.getEntitys().size(); i++) {
            map.getEntitys().get(i).update(delta);
        }
       
        for (int i = map.getEntitys().size()-1; i >= 0; i--) {
            if (map.getEntitys().get(i).shouldBeDestroyed())
                map.getEntitys().remove(i);
        }
        
        
        fpsdiag.update(delta);
                
        //recalculates the light if requested
        recalcIfRequested();
    }

    
     /**
     * Informs the map that a recalc is requested. It will do it in the next update. This method  to limit update calls to to per frame
     */
    public static void requestRecalc(){
        Gdx.app.debug("Controller", "A recalc was requested.");
        recalcRequested = true;
    }
    
    /**
     * When the recalc was requested it calls raytracing and light recalculing. This method should be called every update.
     * Request a recalc with <i>reuqestRecalc()</i>. 
     */
    public void recalcIfRequested(){
        if (recalcRequested) {
            Gdx.app.log("Controller", "Recalcing.");
            WECamera.raytracing();
            LightEngine.calcSimpleLight();
            if (minimap != null) minimap.buildMinimap();
            recalcRequested = false;
        }
    }
    
    /**
     * Creates a new Map.
     */
    public static void newMap(){
        map = new Map(!WE.getCurrentConfig().shouldLoadMap());
        map.fillWithAir();
    }
    
    /**
     * Returns the currently loaded map.
     * @return the map
     */
    public static Map getMap() {
        if (map == null)
            throw new NullPointerException("There is no map yet.");
        else return map;
    }

    public static void setMap(Map map) {
        Controller.map = map;
    }
    
    /**
     * Returns the player
     * @return the player
     */
    public AbstractCharacter getPlayer() {
        return player;
    }

   /**
     * Sets a player 
     * @param player 
     */
    public void setPlayer(AbstractCharacter player) {
        this.player = player;
        player.exist();
    }   
    
    /**
     * Returns the minimap.
     * @return 
     */
    public Minimap getMinimap() {
        return minimap;
    }
    
   

    /**
     * Set the minimap-
     * @param minimap
     */
    public void setMinimap(Minimap minimap) {
        this.minimap = minimap;
    }

    /**
     *
     * @return
     */
    public static LightEngine getLightengine() {
        return lightEngine;
    }

    /**
     *
     * @return
     */
    public float getTimespeed() {
        return timespeed;
    }

    /**
     *
     */
    public void setTimespeed() {
        JFrame frame = new JFrame("InputDialog Example #2");
        try {
            this.timespeed = Float.parseFloat(JOptionPane.showInputDialog(frame, "Use dot as separator.", "Set the speed of time", JOptionPane.QUESTION_MESSAGE));
        } catch(NumberFormatException e) {
            this.timespeed = 1;
            Gdx.app.error("JFrame", "Invalid nubmer entered: "+e.toString());
        } catch(NullPointerException e){
            Gdx.app.debug("JFrame", "Canceled: "+e.toString());
        }
    }
    
    /**
     *
     * @param timespeed
     */
    public void setTimespeed(float timespeed) {
        this.timespeed = timespeed;
    }

    /**
     *
     * @return
     */
    public FPSdiag getFPSdiag() {
        return fpsdiag;
    }

    /**
     *
     */
    public static void disposeClass(){
        Gdx.app.debug("ControllerClass", "Disposing.");
        AbstractGameObject.staticDispose();
        Block.staticDispose();
    }

    public boolean isInitalized() {
        return initalized;
    }  
    
        /**
     * should be called when entered
     */
    public void enter(){}
    
    /**
     * should get called when you leave the editor
     */
    public void exit(){}
}