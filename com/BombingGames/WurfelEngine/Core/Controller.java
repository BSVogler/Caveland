package com.BombingGames.WurfelEngine.Core;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractCharacter;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.LightEngine.LightEngine;
import com.BombingGames.WurfelEngine.Core.Map.Cell;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Minimap;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.MainMenu.MainMenuScreen;
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
        for (AbstractEntity entity : map.getEntitys())
            entity.update(delta);
       
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
        map = new Map(!MainMenuScreen.shouldLoadMap(),-45);
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
     * Get the neighbour block to a side. It may be itself
     * @param coords 
     * @param side the id of the side
     * @return the neighbour block
     */
    public static Block getNeighbourBlock(Coordinate coords, int side){
        return Controller.getMap().getDataSafe(coords.neighbourSidetoCoords(side));
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
    
    /**
     * Use the light engine
     * @param xPos the x position of the diagrams position
     * @param yPos the y position of the diagrams position 
     */
    public static void useLightEngine(int xPos, int yPos) {
        Controller.lightEngine = new LightEngine(xPos, yPos);
    }
    
    /**
     * Game poition to game coordinate
     * @param pos the position on the map
     * @param depthCheck when true the coordiantes are checked with depth, use this for screen to coords. This is only possible if the position are on the map.
     * @return 
     */
    public static Coordinate findCoordinate(Point pos, boolean depthCheck){
        //find out where the click went
        Coordinate coords = new Coordinate(
            (int) (pos.getRelX()) / Block.GAME_DIAGSIZE,
            (int) (pos.getRelY()) / Block.GAME_DIAGSIZE*2,
            pos.getHeight(),
            true
        );
       
        //find the specific coordinate
        Coordinate specificCoords = coords.neighbourSidetoCoords(
            Coordinate.getNeighbourSide(pos.getRelX() % Block.GAME_DIAGSIZE, pos.getRelY() % (Block.GAME_DIAGSIZE))
        );
        coords.setRelX(specificCoords.getRelX());
        coords.setRelY(specificCoords.getRelY());
        
        //trace ray down if wanted
        if (depthCheck && pos.onLoadedMap()) {
            coords.setRelY(coords.getRelY() + (depthCheck? coords.getZ()*2 : 0));
            //if selection is not found by that specify it
            if (coords.getBlock().isHidden()){
                //trace ray down to bottom. for each step 2 y and 1 z down
                do {
                    coords.setRelY(coords.getRelY()-2);
                    coords.setZ(coords.getZ()-1);
                } while (coords.getBlock().isHidden() && coords.getZ()>0);
            }
        }
        
        return coords;
    }

    public void dispose(){
        for (AbstractEntity entity :  map.getEntitys()) {
            entity.dispose();
        }
    }
}