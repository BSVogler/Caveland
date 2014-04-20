/*
 * Copyright 2013 Benedikt Vogler.
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
package com.BombingGames.WurfelEngine.Core;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.Cell;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 *Creates a virtual camera wich displays the game world on the viewport.  
 * @author Benedikt Vogler
 */
public class WECamera extends Camera {
    /**
     *The deepest layer is an array which stores the information if there should be a tile rendered
     */
    private static final boolean[][] bottomLayerVisibility = new boolean[Map.getBlocksX()][Map.getBlocksY()];
    
    /** the position on the screen (viewportWidth/Height ist the aequivalent)*/
    private int screenPosX, screenPosY;
    
    /** the position in the game world but projected*/
    private int projectionPosX, projectionPosY;
    private float zoom = 1;
    
    private Coordinate focusCoordinates;
    private AbstractEntity focusEntity;
    
    private final Block groundBlock;//the represant of the bottom layer (ground) block
    private boolean toggleChunkSwitch = true;
    private boolean fullWindow = false;
    private static int zRenderingLimit;//must be static because raytracing is global/static
    
    
    /**
     * Creates a fullscale camera pointing at the middle of the map.
     */
    public WECamera(){
        this(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fullWindow = true;
    }
    
    /**
     * Creates a camera pointing at the middle of the map.
     * @param x the position in the application window (viewport position)
     * @param y the position in the application window (viewport position)
     * @param width The width of the image (screen size) the camera creates on the application window (viewport)
     * @param height The height of the image (screen size) the camera creates on the application window (viewport)
     */
    public WECamera(int x, int y, int width, int height){
        viewportWidth = width;
	viewportHeight = height;
        screenPosX = x;
        screenPosY = y;
        
	near = 0;
	up.set(0, -1, 0);
	direction.set(0, 0, 1);

        //set the camera's focus to the center of the map
        projectionPosX = Map.getCenter().getProjectedPosX() - getViewportWidth() / 2;
        projectionPosY = Map.getCenter().getProjectedPosY() - getViewportHeight() / 2;
        
        groundBlock = Block.getInstance(2);//set the ground level groundBlock
        groundBlock.setSideClipping(0, true);
        groundBlock.setSideClipping(2, true);
        
        zRenderingLimit = Map.getBlocksZ();
    }
    
   /**
     * Create a camera focusin a specific coordinate. It can later be changed with <i>focusCoordinates()</i>. Screen size does refer to the output of the camera not the real size on the display.
     * @param focus the coordiante where the camera focuses
     * @param x the position in the application window (viewport position)
     * @param y the position in the application window (viewport position)
     * @param width The width of the image (screen size) the camera creates on the application window (viewport)
     * @param height The height of the image (screen size) the camera creates on the application window (viewport)
     */
    public WECamera(Coordinate focus, int x, int y, int width, int height) {
        this(x, y, width, height);   
        GameplayScreen.msgSystem().add("Creating new camera which is focusing a coordinate");
        this.focusCoordinates = focus;
        this.focusEntity = null;
    }
    
   /**
     * Creates a camera focusing an entity.
     * The values are sceen-size and do refer to the output of the camera not the real display size.
     * @param focusentity the entity wich the camera focuses and follows
     * @param x the position in the application window (viewport position)
     * @param y the position in the application window (viewport position)
     * @param width The width of the image (screen size) the camera creates on the application window (viewport)
     * @param height The height of the image (screen size) the camera creates on the application window (viewport)
     */
    public WECamera(AbstractEntity focusentity, int x, int y, int width, int height) {
        this(x,y,width,height);
        if (focusentity == null)
            throw new NullPointerException("Parameter 'focusentity' is null");
        GameplayScreen.msgSystem().add("Creating new camera which is focusing an entity: "+focusentity.getName());
        this.focusEntity = focusentity;
        this.focusCoordinates = null;
    }
    
     /**
     * Updates the camera.
     */
    @Override
    public void update() {   
        //refrehs the camera's position in the game world
        if (focusCoordinates != null) {
            projectionPosX = focusCoordinates.getProjectedPosX() - getViewportWidth() / 2 - AbstractGameObject.SCREEN_DEPTH2;
            projectionPosY = focusCoordinates.getProjectedPosY() - getViewportHeight() / 2;
        } else if (focusEntity != null ){
            projectionPosX = focusEntity.getPos().getProjectedPosX() - getViewportWidth()/2 + AbstractGameObject.SCREEN_DEPTH2;            
            projectionPosY = focusEntity.getPos().getProjectedPosY() - getViewportHeight()/2 ;
        }
        
        position.set(projectionPosX+ getViewportWidth()/2 , projectionPosY+ getViewportHeight()/2 , 0); 
        view.setToLookAt(position, new Vector3(position).add(direction), up);//move camera to the focus 
       
        //orthographic camera, libgdx stuff
        projection.setToOrtho(
            (-viewportWidth / 2)/getScaling(),
            (viewportWidth / 2)/getScaling(),
            (-viewportHeight / 2)/getScaling(),
            (viewportHeight / 2)/getScaling(),
            0,
            Math.abs(far)
        );
        
        //set up projection matrices
        combined.set(projection);
        Matrix4.mul(combined.val, view.val);

        invProjectionView.set(combined);
        Matrix4.inv(invProjectionView.val);
        frustum.update(invProjectionView);
        apply(Gdx.gl10);//don't know what this does
    }
    
   @Override
    public void update(boolean updateFrustum) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Renders the viewport
     * @param view
     * @param camera  
     */
    public void render(View view, WECamera camera) {
        if (Controller.getMap() != null) {  
            
            view.getBatch().setProjectionMatrix(combined);
             
            //set up the viewport
            Gdx.gl.glViewport(
                screenPosX,
                (int) (Gdx.graphics.getHeight()-viewportHeight-screenPosY),//the parameter for the posY is a bit complicated because the y-axis is turned
                (int) viewportWidth,
                (int) viewportHeight
            );
            
            view.getBatch().begin();
            view.setDrawmode(GL10.GL_MODULATE);
            
            //render ground layer tiles if visible
            for (int x = 0; x < Map.getBlocksX(); x++) {
                for (int y = 0; y < Map.getBlocksY(); y++) {
                    if (bottomLayerVisibility[x][y]){
                        int xPos = new Coordinate(x, y, -1, true).getProjectedPosX();//right side is  half a block more to the right
                        int yPos = new Coordinate(x, y, -1, true).getProjectedPosY();//the top is drawn a quarter blocks higher
                        groundBlock.renderSideAt(view, xPos, yPos, 1);
                    }
                }
            }
            
            //render map
            ArrayList<Renderobject> depthlist = createDepthList();
            
            //render vom bottom to top
            for (Renderobject renderobject : depthlist) {
                renderobject.getObject().render(view, camera, renderobject.getCoords()); 
            }
            
            view.getBatch().end();
        }
    }
  
     /**
     * Fills the map into a list and sorts it in the order of the rendering, called the "depthlist".
     * @return 
     */
    protected ArrayList<Renderobject> createDepthList() {
        ArrayList<Renderobject> depthsort = new ArrayList<Renderobject>();
        
        int left = getVisibleLeftBorder();
        int right = getVisibleRightBorder();
        int top = getVisibleTopBorder();
        int bottom = getVisibleBottomBorder();
        
        for (int x = left; x < right; x++)
            for (int y = top; y < bottom; y++){
                
                //add blocks
                for (int z=0; z < zRenderingLimit; z++){
                    
                    Coordinate coord = new Coordinate(x, y, z, true); 
                    Block blockAtCoord = coord.getBlock();
                    if (! blockAtCoord.isHidden()
                        && !blockAtCoord.isClipped()
                        && 
                            coord.getProjectedPosY()
                        <
                            projectionPosY + getViewportHeight()
                    ) {
                        depthsort.add(new Renderobject(blockAtCoord, coord));
                    }
                }
            }
        
        //add entitys
        for (int i=0; i< Controller.getMap().getEntitys().size(); i++) {
            AbstractEntity entity = Controller.getMap().getEntitys().get(i);
            if (!entity.isHidden() && !entity.isClipped()
                && 
                entity.getPos().getProjectedPosY() < projectionPosY + getViewportHeight()
                )
                    depthsort.add(
                        new Renderobject(entity, entity.getPos())
                    );
        }
        //sort the list
        if (depthsort.size()>0)
            depthsort = sortDepthList(depthsort, 0, depthsort.size()-1);
        else Gdx.app.error("WECamera", "depthsort is empty");
        return depthsort;
    }
    
    /**
     * Using Quicksort to sort.
     * From small to big values.
     * @param low the lower border
     * @param high the higher border
     */
    private ArrayList<Renderobject> sortDepthList(ArrayList<Renderobject> depthsort, int low, int high) {
        int left = low;
        int right = high;
        int middle = depthsort.get((low+high)/2).getDepth();

        while (left <= right){    
            while(depthsort.get(left).getDepth() < middle) left++; 
            while(depthsort.get(right).getDepth() > middle) right--;

            if (left <= right) {
                Renderobject tmp = depthsort.set(left, depthsort.get(right));
                depthsort.set(right, tmp);
                left++; 
                right--;
            }
        }

        if(low < right) sortDepthList(depthsort,low, right);
        if(left < high) sortDepthList(depthsort,left, high);
        
        return depthsort;
    }
        
    /**
     * Filters every Block (and side) wich is not visible. Boosts rendering speed.
     */
    protected static void raytracing(){ 
        //set visibility of every groundBlock to false, except blocks with offset
        Cell[][][] mapdata = Controller.getMap().getData();
        for (int x=0; x < Map.getBlocksX(); x++)
            for (int y=0; y < Map.getBlocksY(); y++)
                for (int z=0; z < zRenderingLimit; z++) {
                    Block block = mapdata[x][y][z].getBlock();
                    
                    boolean notAnalyzable = !block.hasSides()
                        || new Coordinate(x,y,z, true).hasOffset();//Blocks with offset are not in the grid, so can not be analysed => always visible
                    block.setClipped(!notAnalyzable);
                }
                
        //send the rays through top of the map
        for (int x=0; x < Map.getBlocksX(); x++)
            for (int y=0; y < Map.getBlocksY() + zRenderingLimit*2; y++){
                traceRay(x,y, 0);
                traceRay(x,y, 1);
                traceRay(x,y, 2);
            }     
    }
    
    /**
    * Traces a single ray.
    * This costs less performance than a whole raytracing.
     * @param x The starting x-coordinate.
     * @param y The starting y-coordinate.
     * @param side The side the ray should check
     */
    private static void traceRay(int x, int y, int side){
        int z = zRenderingLimit-1;//start always from top
        
        boolean left = true;
        boolean right = true;
        boolean leftliquid = false;
        boolean rightliquid = false;
        boolean liquidfilter = false;

        //bring ray to start position
        if (y > Map.getBlocksY()-1) {
            z -= (y-Map.getBlocksY())/2;
            if (y % 2 == 0)
                y = Map.getBlocksY()-1;
            else
                y = Map.getBlocksY()-2;
        }

        y += 2;
        z++;  
        do {
            y -= 2;
            z--;

            if (side == 0){
                //direct neighbour groundBlock on left hiding the complete left side
                if (Controller.getMap().getBlock(x, y, z).hasSides()//block on top
                    && x > 0 && y < Map.getBlocksY()-1
                    && new Coordinate(x - (y%2 == 0 ? 1:0), y+1, z, true).hidingPastBlock())
                    break; //stop ray

                //liquid
                if (Controller.getMap().getBlock(x, y, z).isLiquid()){
                    if (x > 0 && y+1 < Map.getBlocksY()
                    && Controller.getMap().getBlock(x - (y%2 == 0 ? 1:0), y+1, z).isLiquid())
                        liquidfilter = true;

                    if (x > 0 && y < Map.getBlocksY()-1 && z < zRenderingLimit-1
                        && Controller.getMap().getBlock(x - (y%2 == 0 ? 1:0), y+1, z+1).isLiquid())
                        leftliquid = true;

                    if (y < Map.getBlocksY()-2 &&
                        Controller.getMap().getBlock(x, y+2, z).isLiquid())
                        rightliquid = true;

                    if (leftliquid && rightliquid) liquidfilter = true;
                } 

                //two blocks hiding the left side
                if (x > 0 && y < Map.getBlocksY()-1 && z < zRenderingLimit-1
                    && new Coordinate(x - (y%2 == 0 ? 1:0), y+1, z+1, true).hidingPastBlock())
                    left = false;
                if (y < Map.getBlocksY()-2
                    && new Coordinate(x, y+2, z, true).hidingPastBlock()
                    )
                    right = false;

            } else if (side == 1) {//check top side
                if (Controller.getMap().getBlock(x, y, z).hasSides()//block on top
                    && z+1 < zRenderingLimit
                    && new Coordinate(x, y, z+1, true).hidingPastBlock())
                    break;

                //liquid
                if (Controller.getMap().getBlock(x, y, z).isLiquid()){
                    if (z < zRenderingLimit-1 && Controller.getMap().getBlock(x, y, z+1).isLiquid())
                        liquidfilter = true;

                    if (x>0 && y < Map.getBlocksY()-1 && z < zRenderingLimit-1
                        && Controller.getMap().getBlock(x - (y%2 == 0 ? 1:0), y+1, z+1).isLiquid())
                        leftliquid = true;

                    if (x < Map.getBlocksX()-1  && y < Map.getBlocksY()-1 && z < zRenderingLimit-1
                        &&  Controller.getMap().getBlock(x + (y%2 == 0 ? 0:1), y+1, z+1).isLiquid())
                        rightliquid = true;

                    if (leftliquid && rightliquid) liquidfilter = true;
                }

                //two 0- and 2-sides hiding the side 1
                if (x>0 && y < Map.getBlocksY()-1 && z < zRenderingLimit-1
                    && new Coordinate(x - (y%2 == 0 ? 1:0), y+1, z+1, true).hidingPastBlock())
                    left = false;

                if (x < Map.getBlocksX()-1  && y < Map.getBlocksY()-1 && z < zRenderingLimit-1
                    && new Coordinate(x + (y%2 == 0 ? 0:1), y+1, z+1, true).hidingPastBlock()
                    )
                    right = false;

            } else if (side==2){
                //block on right hiding the whole right side
                if (Controller.getMap().getBlock(x, y, z).hasSides()//block on top
                    && x+1 < Map.getBlocksX() && y+1 < Map.getBlocksY()
                    && new Coordinate(x + (y%2 == 0 ? 0:1), y+1, z, true).hidingPastBlock()
                    ) break;

                //liquid
                if (Controller.getMap().getBlock(x, y, z).isLiquid()){
                   if (x < Map.getBlocksX()-1 && y < Map.getBlocksY()-1
                        && Controller.getMap().getBlock(x + (y%2 == 0 ? 0:1), y+1, z).isLiquid()
                       ) liquidfilter = true;

                    if (y+2 < Map.getBlocksY()
                        &&
                        Controller.getMap().getBlock(x, y+2, z).isLiquid())
                        leftliquid = true;

                    if (x+1 < Map.getBlocksX() && y+1 < Map.getBlocksY() && z+1 < zRenderingLimit
                        &&
                        Controller.getMap().getBlock(x + (y%2 == 0 ? 0:1), y+1, z+1).isLiquid())
                        rightliquid = true;

                    if (leftliquid && rightliquid) liquidfilter = true;
                }

                //two blocks hiding the right side
                if (y+2 < Map.getBlocksY()
                    &&
                    new Coordinate(x, y+2, z, true).hidingPastBlock()
                )
                    left = false;

                if (x+1 < Map.getBlocksX() && y+1 < Map.getBlocksY() && z+1 < zRenderingLimit
                    &&
                    new Coordinate(x + (y%2 == 0 ? 0:1), y+1, z+1, true).hidingPastBlock()
                )
                    right = false;
            }

            if ((left || right) && !(liquidfilter && Controller.getMap().getBlock(x, y, z).isLiquid())){ //unless both sides are clipped don't clip the whole groundBlock
                liquidfilter = false;
                Controller.getMap().getBlock(x, y, z).setSideClipping(side, false);                            
            }                
        } while (y > 1 && z > 0 //not on bottom of map
            && (left || right) //left or right still visible
            && (!new Coordinate(x, y, z, true).hidingPastBlock() || new Coordinate(x, y, z, true).hasOffset()));
        
        bottomLayerVisibility[x][y] =
            (z <= 0)
            && (left || right) //left or right still visible
            && (!new Coordinate(x, y, z, true).hidingPastBlock() || new Coordinate(x, y, z, true).hasOffset());
    }
    
    /**
     * Traces the ray to a specific groundBlock. This is like the raytracing but only a single ray.
     * @param coord The coordinate where the ray should point to.
     * @param neighbours True when neighbours groundBlock also should be scanned
     */
    public static void traceRayTo(Coordinate coord, boolean neighbours){
        Block block = coord.getBlock();
        int[] coords = coord.getRel();
                    
        //Blocks with offset are not in the grid, so can not be calculated => always visible
        block.setClipped(
            !block.hasSides() || coord.hasOffset()
        );
                    
       //find start position
        while (coords[2] < Map.getBlocksZ()-1){
            coords[1] += 2;
            coords[2]++;
        }
        
        //trace rays
        if (neighbours){
            traceRay(coords[0] - (coords[1]%2 == 0 ? 1:0), coords[1]-1, Block.RIGHTSIDE);
            traceRay(coords[0] + (coords[1]%2 == 0 ? 0:1), coords[1]-1, Block.LEFTSIDE);
            traceRay(coords[0], coords[1]+2, Block.TOPSIDE);
        }
        traceRay(coords[0], coords[1], Block.LEFTSIDE);
        traceRay(coords[0], coords[1], Block.TOPSIDE);             
        traceRay(coords[0], coords[1], Block.RIGHTSIDE);
    }
    /**
     * Set the zoom factor and regenerates the sprites.
     * @param zoom
     */
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
    
    /**
     * Returns the zoomfactor.
     * @return
     */
    public float getZoom() {
        return zoom;
    }
    
    /**
     * Returns the zoom multiplied by a scaling factor to achieve the same viewport with every resolution
     * @return a scaling factor
     */
    public float getScaling() {
        return zoom*viewportWidth / WE.getCurrentConfig().getRenderResolutionWidth();
    }

    
    /**
     * Use this if you want to focus on a special groundBlock.
     * @param coord the coordaintes of the groundBlock.
     */
    public void focusOnCoords(Coordinate coord){
        focusCoordinates = coord;
        focusEntity = null;
    }
    
    
    /**
     * Returns the left border of the visible area.
     * @return measured in grid-coordinates 
     */
    public int getVisibleLeftBorder(){
        int leftborder = projectionPosX / AbstractGameObject.SCREEN_WIDTH - 1;
        if (leftborder < 0) leftborder= 0;
        
        return leftborder;
    }
    
    /**
     * Returns the right border of the visible area.
     * @return measured in grid-coordinates
     */
    public int getVisibleRightBorder(){
        int rightborder = (projectionPosX + getViewportWidth()) / AbstractGameObject.SCREEN_WIDTH + 1;
        if (rightborder >= Map.getBlocksX()) rightborder = Map.getBlocksX()-1;

        return rightborder;
    }
    
    /**
     * Returns the top seight border of the deepest groundBlock
     * @return measured in grid-coordinates
     */
    public int getVisibleTopBorder(){    
        int topborder = projectionPosY / AbstractGameObject.SCREEN_DEPTH2 - 3;
        if (topborder < 0) topborder= 0;
        
        return topborder;
    }
    
     /**
     * Returns the bottom seight border y-coordinate of the highest groundBlock
     * @return measured in grid-coordinates
     */
    public int getVisibleBottomBorder(){
        int bottomborder = (projectionPosY+getViewportHeight()) / AbstractGameObject.SCREEN_DEPTH2 + Map.getBlocksZ()*2;
        if (bottomborder >= Map.getBlocksY()) bottomborder = Map.getBlocksY()-1;
        
        return bottomborder;
    }
    
  /**
     * The Camera Position in the game world.
     * @return in pixels
     */
    public int getViewportPosX() {
        return projectionPosX;
    }

    /**
     * The Camera left Position in the game world.
     * @param x in pixels
     */
    public void setViewportPosX(int x) {
        this.projectionPosX = x;
    }

    /**
     * The Camera top-position in the game world.
     * @return in camera position game space
     */
    public int getViewportPosY() {
        return projectionPosY;
    }

    /**
     * The Camera top-position in the game world.
     * @param y in game space
     */
    public void setViewportPosY(int y) {
        this.projectionPosY = y;
    }

    /**
     * The amount of pixel which are visible in Y direction (game pixels). It should be equal View.RENDER_RESOLUTION_WIDTH
     * For screen pixels use <i>ViewportWidth()</i>.
     * @return in pixels
     */
    public final int getViewportWidth() {
        return (int) (viewportWidth / getScaling());
    }
    
  /**
    * The amount of pixel which are visible in Y direction (game pixels). For screen pixels use <i>ViewportHeight()</i>.
    * @return  in pixels
    */
   public final int getViewportHeight() {
        return (int) (viewportHeight / getScaling());
    }

    /**
     * Returns the position of the cameras output (on the screen)
     * @return  in pixels
     */
    public int getScreenPosX() {
        return screenPosX;
    }

    /**
     * Returns the position of the camera (on the screen)
     * @return
     */
    public int getScreenPosY() {
        return screenPosY;
    }
    
    /**
     * Returns the height of the camera output before scaling.
     * To get the real display size multiply it with scale values.
     * @return the value before scaling
     */
    public float getScreenHeight() {
        return viewportHeight;
    }

    /**
     * Returns the width of the camera output before scaling.
     * To get the real display size multiply it with scale value.
     * @return the value before scaling
     */
    public float getScreenWidth() {
        return viewportWidth;
    }

    /**
     *
     * @return
     */
    public boolean togglesChunkSwitch() {
        return toggleChunkSwitch;
    }

    /**
     *
     * @param toggleChunkSwitch
     */
    public void setToggleChunkSwitch(boolean toggleChunkSwitch) {
        this.toggleChunkSwitch = toggleChunkSwitch;
    }

    /**
     * Does the cameras output cover the whole screen?
     * @return 
     */
    public boolean isFullWindow() {
        return fullWindow;
    }

    /**
     * Set to true if the camera's output should cover the whole window
     * @param fullWindow 
     */
    public void setFullWindow(boolean fullWindow) {
        this.fullWindow = fullWindow;
        this.viewportHeight = Gdx.graphics.getHeight();
        this.viewportWidth = Gdx.graphics.getWidth();
        this.screenPosX = 0;
        this.screenPosY = 0;
    }
    
    /**
     * Should be called when resized
     * @param width width of window
     * @param height height of window
     */
    public void resize(int width, int height){
        if (fullWindow){
            this.viewportWidth = width;
            this.viewportHeight = height;
            this.screenPosX = 0;
            this.screenPosY = 0;
        }
    }

    /**
     * Must be static because raytracing is static
     * @return 
     */
    public static int getZRenderingLimit() {
        return zRenderingLimit;
    }

    /**
     * 
     * @param zRenderingLimit minimum is 1
     */
    public static void setZRenderingLimit(int zRenderingLimit) {
        WECamera.zRenderingLimit = zRenderingLimit;
        
        //clamp
        if (zRenderingLimit >= Map.getBlocksZ()) WECamera.zRenderingLimit=Map.getBlocksZ();
        if (zRenderingLimit<1) WECamera.zRenderingLimit=1;//min is 1
        
        Controller.requestRecalc();
    }
    
    /**
     * Move x and y coordinate
     * @param x
     * @param y 
     */
    public void move(int x,int y){
        this.projectionPosX += x;
        this.projectionPosY += y;
    }
}