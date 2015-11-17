/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
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
package com.bombinggames.wurfelengine.extension;
   
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Map;
import com.bombinggames.wurfelengine.core.Map.MapObserver;
import com.bombinggames.wurfelengine.core.Map.Point;
import java.util.ArrayList;

/**
 *A minimap is a view that draws the map from top in a small window.
 * @author Benedikt
 */
public class Minimap implements MapObserver {
    /**
	 * distance from left
	 */
	private final int posX;
	/**
	 * distance from bottom
	 */
	private final int posY;
    private final float scaleX = 12;
    private final float scaleY = scaleX/2;
	/**
	 * the size of a block
	 */
    private final float renderSize = (float) (scaleX/Math.sqrt(2));
    
    private Camera camera;
    private Color[][] mapdata;
    private boolean visible = true;
    private int maximumZ;
	private ArrayList<AbstractEntity> trackedEnt = new ArrayList<>(1);
	private FrameBuffer fbo;
	private TextureRegion fboRegion;
	private boolean needsrebuild = true;
	private Map map;

	/**
     * Create a minimap. Visible by default.
     * @param outputX the output-position of the minimap (distance to left)
     * @param outputY the output-position of the minimap (distance from bottom)
	 */
	public Minimap(final int outputX, final int outputY) {
		this.posX = outputX;
        this.posY = outputY;
	}
	
    /**
     * Create a minimap. Visible by default.
     * @param camera the camera wich should be represented on the minimap
     * @param outputX the output-position of the minimap (distance to left)
     * @param outputY  the output-position of the minimap (distance from bottom)
     */
    public Minimap(final Camera camera, final int outputX, final int outputY) {
		this.camera = camera;
        this.posX = outputX;
        this.posY = outputY;
    }

	/**
	 * 
	 * @param trackedEnt 
	 */
	public void setTrackedEnt(ArrayList<AbstractEntity> trackedEnt) {
		this.trackedEnt = trackedEnt;
	}
	
	
	
    
    /**
     * Updates the minimap- Should only be done after changing the map.
	 * @param view
     */
    public void buildTexture(GameView view){
        mapdata = new Color[Chunk.getBlocksX()][Chunk.getBlocksY()];
        for (int x = 0; x < Chunk.getBlocksX(); x++) {
            for (int y = 0; y < Chunk.getBlocksY(); y++) {
                mapdata[x][y] = new Color();
            }
        }
        
        maximumZ = 0;
        int[][] topTileZ = new int[Chunk.getBlocksX()][Chunk.getBlocksY()];
        
        //fing top tile
        for (int x = 0; x < mapdata.length; x++) {
            for (int y = 0; y < mapdata[x].length; y++) {
                int z = Chunk.getBlocksZ() -1;//start at top
                while ( z>-1 && Controller.getMap().getBlock(x, y, z).getSpriteId() ==0 ) {
                    z--;//find topmost block in row
                }

                topTileZ[x][y] = z;
                if (z>maximumZ)
                    maximumZ=z; 
            }
        }
            
        //set color
        for (int x = 0; x < mapdata.length; x++) {
            for (int y = 0; y < mapdata[x].length; y++) {

                if (topTileZ[x][y]<0)//ground floor
                    mapdata[x][y] = RenderBlock.getRepresentingColor(Controller.getMap().getNewGroundBlockInstance().getSpriteId(),(byte) 0);
                else {
                    Block block = Controller.getMap().getBlock(x, y, topTileZ[x][y]);
                    if (block.getSpriteId()!=0)
                        mapdata[x][y] = RenderBlock.getRepresentingColor(block.getSpriteId(), block.getSpriteValue());
                    else 
                        mapdata[x][y] = new Color();//make air black
                } 
                mapdata[x][y].mul(1.5f*(topTileZ[x][y]+2)/(float)(maximumZ+1));
                mapdata[x][y].a = 1; //full alpha level
            }
        }
		
		//render map to frame buffer
		fbo = new FrameBuffer(
			Pixmap.Format.RGBA8888,
			(int) (mapdata.length*scaleX)+20,
			//(int) (mapdata[0].length*scaleY)+20,
			1080,
			false
		);
		
		fboRegion = new TextureRegion(fbo.getColorBufferTexture());
        fboRegion.flip(false, true);
				
		fbo.bind();
		
		ShapeRenderer sh = view.getShapeRenderer();
		sh.translate(0, mapdata[0].length*scaleY, 0);//start from top, 10px offset to left to prevent clipping
			//render the map
			sh.begin(ShapeType.Filled);
				for (int x = 0; x < Chunk.getBlocksX(); x++) {
					for (int y = 0; y < Chunk.getBlocksY(); y++) {
						sh.setColor(mapdata[x][y]);//get color
						float rectX = (x + (y%2 == 1 ? 0.5f : 0) ) * scaleX;
						float rectY = - (y+1)*scaleY;

						sh.translate(rectX, rectY, 0);
						sh.rotate(0, 0, 1, 45);
						sh.rect(0,0,renderSize,renderSize); 
						sh.rotate(0, 0, 1, -45);
						sh.translate(-rectX, -rectY, 0);
					}
				}
			sh.end();

			sh.begin(ShapeType.Line);

				for (AbstractEntity ent : trackedEnt) {

					//show entity position
					Color color = Color.BLUE.cpy();
					color.a = 0.8f;
					sh.setColor(color);
					float rectX = 
						+ ((ent.getPosition().getX()
						+ (ent.getPosition().toCoord().getY()%2==1?0.5f:0)
						)/Block.GAME_DIAGLENGTH
						- 0.5f)
						* scaleX;
					float rectY = 
						- (ent.getPosition().getY()/Block.GAME_DIAGLENGTH
						+ 0.5f
						)* scaleY*2;
					sh.translate(rectX, rectY, 0);
					sh.rotate(0, 0, 1, 45);
					sh.rect(0,0,renderSize,-renderSize);
					sh.rotate(0, 0, 1, -45);
					sh.translate(-rectX, -rectY, 0);

					 Point tmpPos = ent.getPosition();
					//player coordinate
					view.drawString(
						tmpPos.toCoord().getX() +" | "+ tmpPos.toCoord().getY() +" | "+ (int) tmpPos.getZ(),
						(int) (posX+(tmpPos.toCoord().getX() + (tmpPos.getY()%2==1?0.5f:0) ) * scaleX+20),
						(int) (posY- tmpPos.toCoord().getY() * scaleY + 10),
						Color.RED
					);
					rectX = (int) (
						(tmpPos.getX()
							+ (tmpPos.toCoord().getY()%2==1 ? 0.5f : 0)
						  ) / Block.GAME_DIAGLENGTH * scaleX
					);
					rectY = (int) (tmpPos.getY()/Block.GAME_DIAGLENGTH2 * scaleY);

					view.drawString(tmpPos.getX() +" | "+ tmpPos.getY() +" | "+ (int) tmpPos.getZ(),
						(int) (posX+rectX),
						(int) (posY+rectY),
						Color.RED
					);
				}

				//Chunk outline
				sh.setColor(Color.BLACK);
				for (int chunk = 0; chunk < 9; chunk++) {
					sh.rect(
						chunk%3 *(Chunk.getBlocksX()*scaleX),
						- chunk/3*(Chunk.getBlocksY()*scaleY),
						Chunk.getBlocksX()*scaleX,
						-Chunk.getBlocksY()*scaleY
					);
				}
			sh.end();
		sh.translate(0, -mapdata[0].length*scaleY, 0);//start from top, 10px offset to left to prevent clipping

		//chunk coordinates
//		for (int chunk = 0; chunk < 9; chunk++) {
//			view.drawString(
//				Controller.getMap().getChunkCoords(chunk)[0] +" | "+ Controller.getMap().getChunkCoords(chunk)[1],
//				(int) (posX + 10 + chunk%3 *Chunk.getBlocksX()*scaleX),
//				(int) (posY - 10 - chunk/3 *(Chunk.getBlocksY()*scaleY)),
//				Color.BLACK
//			);
//		}

		fbo.end();
		needsrebuild= false;
    }
    
    /**
     * Renders the Minimap.
     * @param view the view using this render method 
     */
    public void render(final GameView view) {
        if (visible) {
            //this needs offscreen rendering for a single call with a recalc
			if (fboRegion!=null){
				view.getSpriteBatch().begin();
				view.getSpriteBatch().draw(fboRegion, posX, posY);
				view.getSpriteBatch().end();
			}
			
			ShapeRenderer sh = view.getShapeRenderer();
			sh.translate(posX, posY, 0);
			
			if (camera!=null){
				//bottom getCameras() rectangle
				sh.begin(ShapeType.Line);
				
					sh.translate(0, mapdata[0].length*scaleY, 0);
						sh.setColor(Color.RED);
						sh.rect(
							scaleX * camera.getVisibleLeftBorder(),
							-scaleY * camera.getVisibleBackBorder(),
							scaleX*(camera.getVisibleRightBorder()-camera.getVisibleLeftBorder()+1),
							-scaleY*(camera.getVisibleFrontBorderLow()-camera.getVisibleBackBorder())
						);

						//ground level
						sh.setColor(Color.GREEN);
					sh.translate(0, -mapdata[0].length*scaleY, 0);//projection is y-up
					sh.rect(scaleX * camera.getViewSpaceX() / Block.VIEW_WIDTH,
						scaleY * camera.getViewSpaceY() / Block.VIEW_DEPTH2,
						scaleX*camera.getWidthInProjSpc()/ Block.VIEW_WIDTH,
						scaleY*camera.getHeightInProjSpc()/ Block.VIEW_DEPTH2
					);

					//player level getCameras() rectangle
			//            if (controller.getPlayer()!=null){
			//                sh.setColor(Color.GRAY);
			//                sh.rect(
			//                    scaleX * camera.getProjectionPosX() / RenderBlock.VIEW_WIDTH,
			//                    + scaleY * camera.getProjectionPosY() / RenderBlock.VIEW_DEPTH2
			//                        + scaleY *2*(controller.getPlayer().getPosition().getCoord().getZ() * RenderBlock.VIEW_HEIGHT)/ RenderBlock.VIEW_DEPTH,
			//                    scaleX*camera.getProjectionWidth() / RenderBlock.VIEW_WIDTH,
			//                    scaleY*camera.getProjectionHeight() / RenderBlock.VIEW_DEPTH2
			//                );
			//            }

					//top level getCameras() rectangle
					sh.setColor(Color.WHITE);
					sh.rect(scaleX * camera.getViewSpaceX() / Block.VIEW_WIDTH,
						scaleY * camera.getViewSpaceY() / Block.VIEW_DEPTH2
							-scaleY *2*(Chunk.getBlocksZ() * Block.VIEW_HEIGHT)/ Block.VIEW_DEPTH,
						scaleX*camera.getWidthInProjSpc() / Block.VIEW_WIDTH,
						scaleY*camera.getHeightInProjSpc() / Block.VIEW_DEPTH2
					);
					
				sh.end();

				//camera position
				view.drawString(
					camera.getViewSpaceX() +" | "+ camera.getViewSpaceY(),
					posX,
					(int) (posY- 3*Chunk.getBlocksY()*scaleY + 15),
					Color.WHITE
				);
			}
			sh.translate(-posX, -posY, 0);
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

	/**
	 *
	 * @return
	 */
	public boolean isNeedingRebuild() {
		return needsrebuild;
	}
	
	/**
	 *
	 */
	public void needsRebuild() {
		needsrebuild = true;
	}

	/**
	 * Set a camera which will be represented on the minimap.
	 * @param camera 
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	@Override
	public void onMapChange() {
		needsRebuild();
	}

	@Override
	public void onChunkChange(Chunk chunk) {
	}

	@Override
	public void onMapReload() {
	}
	
	
}