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
   
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.Core.WECamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 *A minimap is a view that draws the map from top in a small window.
 * @author Benedikt
 */
public class Minimap {
    private int posX, posY;
    private final float scaleX = 12;
    private final float scaleY = scaleX/2;
    private final float renderSize = (float) (scaleX/Math.sqrt(2));
    
    private Controller controller;
    private WECamera camera;
    private final Color[][] mapdata = new Color[Map.getBlocksX()][Map.getBlocksY()];
    private boolean visible;

    /**
     * Create a minimap.
     * @param controller the controller wich should be represented
     * @param camera the camera wich should be represented on the minimap
     * @param outputX the output-position of the minimap (distance to left)
     * @param outputY  the output-position of the minimap (distance to top)
     */
    public Minimap(Controller controller, WECamera camera, int outputX, int outputY) {
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
        for (int x = 0; x < Map.getBlocksX(); x++) {
            for (int y = 0; y < Map.getBlocksY(); y++) {
                int z = Map.getBlocksZ() -1;//start at top
                Block block = new Coordinate(x, y, z, true).getBlock();
                while ( z>0 && block.getId() ==0 ) {
                    z--;//find topmost block
                    block = new Coordinate(x, y, z, true).getBlock();
                }
                mapdata[x][y] = Block.getRepresentingColor(block.getId(), block.getValue()).cpy();
                mapdata[x][y].a = 1;
                mapdata[x][y].mul(1.3f).mul(z/(float)Map.getBlocksZ());
            }
        }
    }
    
    
    /**
     * Renders the Minimap.
     * @param view the view using this render method 
     */
    public void render(View view) {
        if (visible) {
            //this needs offscreen rendering for a single call with a recalc
            int viewportPosX = posX;
            int viewportPosY = posY;
            
            ShapeRenderer shapeRenderer = view.getShapeRenderer();
                        
            //render the map
            shapeRenderer.begin(ShapeType.Filled);
            for (int x = 0; x < Map.getBlocksX(); x++) {
                for (int y = 0; y < Map.getBlocksY(); y++) {
                    shapeRenderer.setColor(mapdata[x][y]);//get color
                    float rectX = viewportPosX
                                + (x + (y%2 == 1 ? 0.5f : 0) ) * scaleX;
                    float rectY = viewportPosY
                                + y*scaleY;
                    
                    shapeRenderer.translate(rectX, rectY, 0);
                    shapeRenderer.rotate(0, 0, 1, 45);
                    shapeRenderer.rect(0,0,renderSize,renderSize); 
                    shapeRenderer.rotate(0, 0, 1, -45);
                    shapeRenderer.translate(-rectX, -rectY, 0);
                }
            }
            
            //show player position
            if (controller.getPlayer()!=null){
                Color color = Color.BLUE.cpy();
                color.a = 0.8f;
                shapeRenderer.setColor(color);
                float rectX = viewportPosX
                    + ((controller.getPlayer().getPos().getRelX()
                    + (controller.getPlayer().getPos().getCoord().getRelY()%2==1?0.5f:0)
                    )/Block.GAME_DIAGLENGTH
                    - 0.5f)
                    * scaleX;
                float rectY = viewportPosY
                    + (controller.getPlayer().getPos().getRelY()/Block.GAME_DIAGLENGTH
                    - 0.5f
                    )* scaleY*2;
                shapeRenderer.translate(rectX, rectY, 0);
                shapeRenderer.rotate(0, 0, 1, 45);
                shapeRenderer.rect(0,0,renderSize,renderSize);
                shapeRenderer.rotate(0, 0, 1, -45);
                shapeRenderer.translate(-rectX, -rectY, 0);
            }
            shapeRenderer.end();
            
            //Chunk outline
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);
            for (int chunk = 0; chunk < 9; chunk++) {
                shapeRenderer.rect(
                    viewportPosX + chunk%3 *(Chunk.getBlocksX()*scaleX),
                    viewportPosY + chunk/3*(Chunk.getBlocksY()*scaleY),
                    Chunk.getBlocksX()*scaleX,
                    Chunk.getBlocksY()*scaleY
                );
            }
            shapeRenderer.end();

            //chunk coordinates
            for (int chunk = 0; chunk < 9; chunk++) {
                view.drawString(
                    Controller.getMap().getChunkCoords(chunk)[0] +" | "+ Controller.getMap().getChunkCoords(chunk)[1],
                    (int) (viewportPosX + 10 + chunk%3 *Chunk.getBlocksX()*scaleX),
                    (int) (posY + 10 + chunk/3 *(Chunk.getBlocksY()*scaleY)),
                    Color.BLACK
                );
            }

            //bottom getCameras() rectangle
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rect(
                viewportPosX + scaleX * camera.getProjectionPosX() / Block.GAME_DIAGLENGTH,
                viewportPosY + scaleY * camera.getProjectionPosY() / Block.GAME_DIAGLENGTH2,
                scaleX*camera.getProjectionWidth() / Block.GAME_DIAGLENGTH,
                scaleY*2*camera.getProjectionHeight() / Block.GAME_DIAGLENGTH2
            );

            //player level getCameras() rectangle
            if (controller.getPlayer()!=null){
                shapeRenderer.setColor(Color.GRAY);
                shapeRenderer.rect(
                    viewportPosX + scaleX * camera.getProjectionPosX() / Block.SCREEN_WIDTH,
                    viewportPosY + scaleY * camera.getProjectionPosY() / Block.SCREEN_DEPTH2
                    + scaleY *2*(controller.getPlayer().getPos().getCoord().getZ() * Block.SCREEN_HEIGHT2)/ Block.SCREEN_DEPTH,
                    scaleX*camera.getProjectionWidth() / Block.GAME_DIAGLENGTH,
                    scaleY*2*camera.getProjectionHeight() / Block.GAME_DIAGLENGTH2
                );
            }

            //top level getCameras() rectangle
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.rect(
                viewportPosX + scaleX * camera.getProjectionPosX() / Block.SCREEN_WIDTH,
                viewportPosY + scaleY * camera.getProjectionPosY() / Block.SCREEN_DEPTH2
                + scaleY *2*(Chunk.getBlocksZ() * Block.SCREEN_DEPTH2)/ Block.SCREEN_DEPTH,
                scaleX*camera.getProjectionWidth() / Block.GAME_DIAGLENGTH,
                scaleY*2*camera.getProjectionHeight() / Block.GAME_DIAGLENGTH2
            );
            shapeRenderer.end();
            
//            view.drawString(
//                    camera.getOutputPosX()+" | "+ camera.getOutputPosY(),
//                    (int) (viewportPosX + scaleX * camera.getOutputPosX() / Block.SCREEN_WIDTH
//                    + scaleX*camera.get2DWidth() / Block.SCREEN_WIDTH),
//                    (int) (viewportPosY + scaleY * camera.getOutputPosY() / Block.SCREEN_DEPTH2
//                    + scaleY*camera.get2DHeight() / Block.SCREEN_DEPTH2),
//                    Color.BLACK
//                );
                            
            if (controller.getPlayer()!=null){
                //player coordinate
                view.drawString(
                    controller.getPlayer().getPos().getCoord().getRelX() +" | "+ controller.getPlayer().getPos().getCoord().getRelY() +" | "+ (int) controller.getPlayer().getPos().getHeight(),
                    (int) (viewportPosX + (controller.getPlayer().getPos().getCoord().getRelX() + (controller.getPlayer().getPos().getRelY()%2==1?0.5f:0) ) * scaleX+20),
                    (int) (viewportPosY + controller.getPlayer().getPos().getCoord().getRelY() * scaleY - 10),
                    Color.RED
                );
                 int rectX = (int) (viewportPosX
                     + (controller.getPlayer().getPos().getRelX()
                     + (controller.getPlayer().getPos().getCoord().getRelY()%2==1?0.5f:0)
                     )/Block.GAME_DIAGLENGTH * scaleX);
                int rectY = (int) (
                    viewportPosY
                    + controller.getPlayer().getPos().getRelY()/Block.GAME_DIAGLENGTH2 * scaleY
                );
                
                view.drawString(
                    controller.getPlayer().getPos().getRelX() +" | "+ controller.getPlayer().getPos().getRelY() +" | "+ (int) controller.getPlayer().getPos().getHeight(),
                    rectX,
                    rectY,
                    Color.RED
                );
            }

            //camera position
            view.drawString(
                camera.getProjectionPosX() +" | "+ camera.getProjectionPosY(),
                viewportPosX ,
                (int) (viewportPosY + 3*Chunk.getBlocksY()*scaleY + 15),
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