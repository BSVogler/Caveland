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
package com.BombingGames.WurfelEngine.Core.Map;
   
import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.EngineView;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 *A minimap is a view that draws the map from top in a small window.
 * @author Benedikt
 */
public class Minimap {
    private final int posX, posY;
    private final float scaleX = 12;
    private final float scaleY = scaleX/2;
    private final float renderSize = (float) (scaleX/Math.sqrt(2));
    
    private Controller controller;
    private Camera camera;
    private final Color[][] mapdata = new Color[Map.getBlocksX()][Map.getBlocksY()];
    private boolean visible;
    private int highestLevel;

    /**
     * Create a minimap.
     * @param controller the controller wich should be represented
     * @param camera the camera wich should be represented on the minimap
     * @param outputX the output-position of the minimap (distance to left)
     * @param outputY  the output-position of the minimap (distance from bottom)
     */
    public Minimap(final Controller controller, final Camera camera, final int outputX, final int outputY) {
        if (controller == null || camera == null) throw new NullPointerException("Parameter controller or camera is null");
        this.posX = outputX;
        this.posY = outputY;
        this.controller = controller;
        this.camera = camera;
        
        for (int x = 0; x < Map.getBlocksX(); x++) {
            for (int y = 0; y < Map.getBlocksY(); y++) {
                mapdata[x][y] = new Color();
            }
        }
    }
    
    /**
     * Updates the minimap- Should only be done after changing the map.
     */
    public void buildMinimap(){
        highestLevel = 0;
        int[][] topTileZ = new int[Map.getBlocksX()][Map.getBlocksY()];
        
        //fing top tile
        for (int x = 0; x < mapdata.length; x++) {
            for (int y = 0; y < mapdata[x].length; y++) {
                int z = Map.getBlocksZ() -1;//start at top
                while ( z>0 && Controller.getMap().getBlock(x, y, z).getId() ==0 ) {
                    z--;//find topmost block in row
                }

                topTileZ[x][y] = z;
                if (z>highestLevel)
                    highestLevel=z; 
            }
        }
            
        //set color
        for (int x = 0; x < mapdata.length; x++) {
            for (int y = 0; y < mapdata[x].length; y++) {
                Block block = Controller.getMap().getBlock(x, y, topTileZ[x][y]);
                if (block.getId()!=0)
                    mapdata[x][y] = Block.getRepresentingColor(block.getId(), block.getValue()).cpy();
                else mapdata[x][y] = new Color();//make air black
                mapdata[x][y].mul(1.5f*topTileZ[x][y]/(float)highestLevel);
                mapdata[x][y].a = 1; //full alpha level
            }
        }
    }
    
    
    /**
     * Renders the Minimap.
     * @param view the view using this render method 
     */
    public void render(final GameView view) {
        if (visible) {
            //this needs offscreen rendering for a single call with a recalc
            
            ShapeRenderer sh = EngineView.getShapeRenderer();
                        
            //render the map
            sh.begin(ShapeType.Filled);
            for (int x = 0; x < Map.getBlocksX(); x++) {
                for (int y = 0; y < Map.getBlocksY(); y++) {
                    sh.setColor(mapdata[x][y]);//get color
                    float rectX = posX + (x + (y%2 == 1 ? 0.5f : 0) ) * scaleX;
                    float rectY = posY - (y+1)*scaleY;
                    
                    sh.translate(rectX, rectY, 0);
                    sh.rotate(0, 0, 1, 45);
                    sh.rect(0,0,renderSize,renderSize); 
                    sh.rotate(0, 0, 1, -45);
                    sh.translate(-rectX, -rectY, 0);
                }
            }
            sh.end();
            
            sh.begin(ShapeType.Line);
            //show player position
            if (controller.getPlayer()!=null){
                Color color = Color.BLUE.cpy();
                color.a = 0.8f;
                sh.setColor(color);
                float rectX = posX
                    + ((controller.getPlayer().getPos().getRelX()
                    + (controller.getPlayer().getPos().getCoord().getRelY()%2==1?0.5f:0)
                    )/Block.GAME_DIAGLENGTH
                    - 0.5f)
                    * scaleX;
                float rectY = posY
                    - (controller.getPlayer().getPos().getRelY()/Block.GAME_DIAGLENGTH
                    + 0.5f
                    )* scaleY*2;
                sh.translate(rectX, rectY, 0);
                sh.rotate(0, 0, 1, 45);
                sh.rect(0,0,renderSize,-renderSize);
                sh.rotate(0, 0, 1, -45);
                sh.translate(-rectX, -rectY, 0);
            }
            
            //Chunk outline
            sh.setColor(Color.BLACK);
            for (int chunk = 0; chunk < 9; chunk++) {
                sh.rect(
                    posX + chunk%3 *(Chunk.getBlocksX()*scaleX),
                    posY - chunk/3*(Chunk.getBlocksY()*scaleY),
                    Chunk.getBlocksX()*scaleX,
                    -Chunk.getBlocksY()*scaleY
                );
            }
            sh.end();

            //chunk coordinates
            for (int chunk = 0; chunk < 9; chunk++) {
                view.drawString(
                    Controller.getMap().getChunkCoords(chunk)[0] +" | "+ Controller.getMap().getChunkCoords(chunk)[1],
                    (int) (posX + 10 + chunk%3 *Chunk.getBlocksX()*scaleX),
                    (int) (posY - 10 - chunk/3 *(Chunk.getBlocksY()*scaleY)),
                    Color.BLACK
                );
            }

            //bottom getCameras() rectangle
            sh.begin(ShapeType.Line);
            sh.setColor(Color.GREEN);
            sh.rect(
                posX + scaleX * camera.getViewportPosX() / Block.GAME_DIAGLENGTH,
                posY - scaleY * camera.getViewportPosY() / Block.GAME_DIAGLENGTH2,
                scaleX*camera.getViewportWidth() / Block.GAME_DIAGLENGTH,
                -scaleY*2*camera.getViewportHeight() / Block.GAME_DIAGLENGTH2
            );

            //player level getCameras() rectangle
            if (controller.getPlayer()!=null){
                sh.setColor(Color.GRAY);
                sh.rect(
                    posX + scaleX * camera.getViewportPosX() / Block.SCREEN_WIDTH,
                    posY - scaleY * camera.getViewportPosY() / Block.SCREEN_DEPTH2
                        - scaleY *2*(controller.getPlayer().getPos().getCoord().getZ() * Block.SCREEN_HEIGHT2)/ Block.SCREEN_DEPTH,
                    scaleX*camera.getViewportWidth() / Block.GAME_DIAGLENGTH,
                    scaleY*2*camera.getViewportHeight() / Block.GAME_DIAGLENGTH2
                );
            }

            //top level getCameras() rectangle
            sh.setColor(Color.WHITE);
            sh.rect(
                posX + scaleX * camera.getViewportPosX() / Block.SCREEN_WIDTH,
                posY - scaleY * camera.getViewportPosY() / Block.SCREEN_DEPTH2
                    - scaleY *2*(Chunk.getBlocksZ() * Block.SCREEN_DEPTH2)/ Block.SCREEN_DEPTH,
                scaleX*camera.getViewportWidth() / Block.GAME_DIAGLENGTH,
                scaleY*2*camera.getViewportHeight() / Block.GAME_DIAGLENGTH2
            );
            sh.end();
            
//            view.drawString(
//                    camera.getOutputPosX()+" | "+ camera.getOutputPosY(),
//                    (int) (viewportPosX + scaleX * camera.getOutputPosX() / Block.SCREEN_WIDTH
//                    + scaleX*camera.get2DWidth() / Block.SCREEN_WIDTH),
//                    (int) (viewportPosY + scaleY * camera.getOutputPosY() / Block.SCREEN_DEPTH2
//                    + scaleY*camera.get2DHeight() / Block.SCREEN_DEPTH2),
//                    Color.BLACK
//                );

            if (controller.getPlayer()!=null){

                Point tmpPos = controller.getPlayer().getPos();
                //player coordinate
                view.drawString(
                    tmpPos.getCoord().getRelX() +" | "+ tmpPos.getCoord().getRelY() +" | "+ (int) tmpPos.getHeight(),
                    (int) (posX + (tmpPos.getCoord().getRelX() + (tmpPos.getRelY()%2==1?0.5f:0) ) * scaleX+20),
                    (int) (posY - tmpPos.getCoord().getRelY() * scaleY + 10),
                    Color.RED
                );
                int rectX = (int) (
                    posX
                    + (tmpPos.getRelX()
                        + (tmpPos.getCoord().getRelY()%2==1?0.5f:0)
                      ) / Block.GAME_DIAGLENGTH * scaleX
                );
                int rectY = (int) (
                    posY
                    - tmpPos.getRelY()/Block.GAME_DIAGLENGTH2 * scaleY
                );
                
                view.drawString(
                    tmpPos.getRelX() +" | "+ tmpPos.getRelY() +" | "+ (int) tmpPos.getHeight(),
                    rectX,
                    rectY,
                    Color.RED
                );
            }

            //camera position
            view.drawString(
                camera.getViewportPosX() +" | "+ camera.getViewportPosY(),
                posX ,
                (int) (posY - 3*Chunk.getBlocksY()*scaleY + 15),
                Color.WHITE
            );
        }
    }
    
    /**
     * Toggle between visible and invisible.
     * @return The new visibility of the minimap. True= visible.
     */
    public boolean toggleVisibility(){
        visible = !visible;
        return visible;
    }
}