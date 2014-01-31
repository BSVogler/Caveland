package com.BombingGames.WurfelEngine.Core;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractCharacter;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.LightEngine.LightEngine;
import com.BombingGames.WurfelEngine.Core.Map.Cell;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Minimap;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *A controller manages the map and the game data.
 * @author Benedikt Vogler
 */
public class Controller {
    private final boolean ENABLECHUNKSWITCH = true;
    private static LightEngine lightEngine;
    private static Map map;
    private static boolean recalcRequested;
    
        
    private final ArrayList<WECamera> cameras = new ArrayList<WECamera>(6);
    private Minimap minimap;
    /** The speed of time. 1 = real time;*/
    private float timespeed = 1;
    private AbstractCharacter player;  
    
    private FPSdiag fpsdiag;

    /**
     * This method works like a constructor. Everything is loaded. Set you custom chunk generator before calling this method.
     */
    public void init(){
        Gdx.app.log("Controller", "Initializing");
        newMap();
        fpsdiag = new FPSdiag(10,300);
        
        if (WE.getCurrentConfig().useLightEngine()){
            Controller.lightEngine = new LightEngine(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        }
        
        recalcRequested = true;
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
        
        if (ENABLECHUNKSWITCH && cameras.size() >0){
            //earth to right
            if (cameras.get(0).getLeftBorder() <= 0)
                map.setCenter(3);
            else //earth to the left
                if (cameras.get(0).getRightBorder() >= Map.getBlocksX()-1) 
                    map.setCenter(5);

            //scroll up, earth down            
            if (cameras.get(0).getTopBorder() <= 0)
                map.setCenter(1);
            else //scroll down, earth up
                if (cameras.get(0).getBottomBorder() >= Map.getBlocksY()-1)
                map.setCenter(7);
        }
        
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
        
        for (WECamera camera : cameras) {
            camera.update();
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
        map.fillWithBlocks();
    }
    
    /**
     * Returns the currently loaded map.
     * @return the map
     */
    public static Map getMap() {
        return map;
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
     * Returns a camera.
     * @return The virtual cameras rendering the scene
     */
    public ArrayList<WECamera> getCameras() {
        return cameras;
    }

    /**
     * Add a camera.
     * @param camera
     */
    protected void addCamera(WECamera camera) {
        this.cameras.add(camera);
    }

    /**
     * Set the minimap-
     * @param minimap
     */
    protected void setMinimap(Minimap minimap) {
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
    


    public FPSdiag getFpsdiag() {
        return fpsdiag;
    }

    public void dispose(){
        for (AbstractEntity entity :  map.getEntitys()) {
            entity.dispose();
        }
    }
}