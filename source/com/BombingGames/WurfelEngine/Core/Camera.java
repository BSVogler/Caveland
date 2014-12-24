/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 * 
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
package com.BombingGames.WurfelEngine.Core;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.CameraSpaceIterator;
import com.BombingGames.WurfelEngine.Core.Map.Chunk;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.LinkedWithMap;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Creates a virtual camera wich displays the game world on the viewport.
 *
 * @author Benedikt Vogler
 */
public class Camera implements LinkedWithMap {

	/**
	 * The deepest layer is an array which stores the information if there
	 * should be a tile rendered
	 */
	private int zRenderingLimit;
	
	private ClippingCell[][] clipping;
	/**
	 * the position of the camera in view space. Y-up
	 */
	private final Vector2 position = new Vector2();
	/**
	 * the unit length up vector of the camera
	 */
	private final Vector3 up = new Vector3(0, 1, 0);

	/**
	 * the projection matrix
	 */
	private final Matrix4 projection = new Matrix4();
	/**
	 * the view matrix *
	 */
	private final Matrix4 view = new Matrix4();
	/**
	 * the combined projection and view matrix
	 */
	private final Matrix4 combined = new Matrix4();

	/**
	 * the viewport width&height. Origin top left.
	 */
	private int screenWidth, screenHeight;

	/**
	 * the position on the screen (viewportWidth/Height ist the affiliated). Origin top left.
	 */
	private int screenPosX, screenPosY;

	private float zoom = 1;

	private Coordinate focusCoordinates;
	private AbstractEntity focusEntity;

	private boolean fullWindow = false;

	/**
	 * the opacity of thedamage overlay
	 */
	private float damageoverlay = 0f;
	
	private Vector2 screenshake = new Vector2(0, 0);
	private float shakeAmplitude;
	private float shakeTime;
	
	private final GameView gameView;
	private int gameSpaceWidth;
	private int gameSpaceHeight;
	private int centerChunkX;
	private int centerChunkY;
	
	/**
	 * Creates a camera pointing at the middle of the map.
	 *
	 * @param x the position in the application window (viewport position). Origin top left
	 * @param y the position in the application window (viewport position). Origin top left
	 * @param width The width of the image (screen size) the camera creates on
	 * the application window (viewport)
	 * @param height The height of the image (screen size) the camera creates on
	 * the application window (viewport)
	 * @param view
	 */
	public Camera(final int x, final int y, final int width, final int height, GameView view) {
		gameView = view;
		screenWidth = width;
		screenHeight = height;
		screenPosX = x;
		screenPosY = y;
		updateGameSpaceSize();

		//set the camera's focus to the center of the map
		position.x = Map.getCenter().getViewSpcX(view);
		position.y = Map.getCenter().getViewSpcY(view);
		
		zRenderingLimit = Map.getBlocksZ();
		
		updateNeededData();
	}
	
	/**
	 * Creates a fullscale camera pointing at the middle of the map.
	 * @param view
	 */
	public Camera(GameView view) {
		this(
			0,
			0,
			Gdx.graphics.getWidth(),
			Gdx.graphics.getHeight(),
			view
		);
		fullWindow = true;
	}

	/**
	 * Create a camera focusin a specific coordinate. It can later be changed
	 * with <i>focusCoordinates()</i>. Screen size does refer to the output of
	 * the camera not the real size on the display.
	 *
	 * @param focus the coordiante where the camera focuses
	 * @param x the position in the application window (viewport position). Origin top left
	 * @param y the position in the application window (viewport position). Origin top left
	 * @param width The width of the image (screen size) the camera creates on
	 * the application window (viewport)
	 * @param height The height of the image (screen size) the camera creates on
	 * the application window (viewport)
	 * @param view
	 */
	public Camera(final Coordinate focus, final int x, final int y, final int width, final int height, GameView view) {
		this(x, y, width, height, view);
		WE.getConsole().add("Creating new camera which is focusing a coordinate");
		this.focusCoordinates = focus;
		this.focusEntity = null;
		updateGameSpaceSize();
	}

	/**
	 * Creates a camera focusing an entity. The values are sceen-size and do
	 * refer to the output of the camera not the real display size.
	 *
	 * @param focusentity the entity wich the camera focuses and follows
	 * @param x the position in the application window (viewport position). Origin top left
	 * @param y the position in the application window (viewport position). Origin top left
	 * @param width The width of the image (screen size) the camera creates on
	 * the application window (viewport)
	 * @param height The height of the image (screen size) the camera creates on
	 * the application window (viewport)
	 * @param view
	 */
	public Camera(final AbstractEntity focusentity, final int x, final int y, final int width, final int height, GameView view) {
		this(x, y, width, height, view);
		if (focusentity == null) {
			throw new NullPointerException("Parameter 'focusentity' is null");
		}
		WE.getConsole().add("Creating new camera which is focusing an entity: " + focusentity.getName());
		this.focusEntity = focusentity;
		this.focusCoordinates = null;
		updateGameSpaceSize();
	}

	/**
	 * Updates the camera.
	 *
	 * @param dt
	 */
	public void update(float dt) {
		//refresh the camera's position in the game world
		if (focusCoordinates != null) {
			//update camera's position according to focusCoordinates
			position.x = focusCoordinates.getViewSpcX(gameView);
			position.y = focusCoordinates.getViewSpcY(gameView);

		} else if (focusEntity != null) {
			//update camera's position according to focusEntity
            position.x = focusEntity.getPosition().getViewSpcX(gameView);
            position.y = (int) (
                focusEntity.getPosition().getViewSpcY(gameView)
              + focusEntity.getDimensionZ()*AbstractPosition.SQRT12/2
            );
		}
		
		//aplly screen shake
		if (shakeTime > 0) {
			screenshake.x = (float) (Math.random() * shakeAmplitude - shakeAmplitude / 2);
			screenshake.y = (float) (Math.random() * shakeAmplitude - shakeAmplitude / 2);
			shakeTime -= dt;
		} else {
			screenshake.x = 0;
			screenshake.y = 0;
		}

		position.x += screenshake.x;
		position.y += screenshake.y;

		updateNeededData();
		
		//move camera to the focus 
		view.setToLookAt(
			new Vector3(position, 0),
			new Vector3(position, -1),
			up
		);

		//orthographic camera, libgdx stuff
		projection.setToOrtho(
			-getWidthInViewSpc() / 2,
			getWidthInViewSpc() / 2,
			-getHeightInViewSpc() / 2,
			getHeightInViewSpc() / 2,
			0,
			1
		);

		//set up projection matrices
		combined.set(projection);
		Matrix4.mul(combined.val, view.val);
		
        //don't know what this does
		//Gdx.gl20.glMatrixMode(GL20.GL_PROJECTION);
		//Gdx.gl20.glLoadMatrixf(projection.val, 0);
		//Gdx.gl20.glMatrixMode(GL20.GL_MODELVIEW);
		//Gdx.gl20.glLoadMatrixf(view.val, 0);
        //invProjectionView.set(combined);
		//Matrix4.inv(invProjectionView.val);
	}
	
	private void updateNeededData(){
		this.centerChunkX = (int) Math.floor(position.x / Chunk.getViewWidth());
		this.centerChunkY = (int) Math.floor(-position.y / Chunk.getViewDepth());
		
		//check every chunk
		if (centerChunkX==0 && centerChunkY==0 || CVar.get("enableChunkSwitch").getValueb()) {
			checkChunk(centerChunkX-1, centerChunkY-1);
			checkChunk(centerChunkX  , centerChunkY-1);
			checkChunk(centerChunkX+1, centerChunkY-1);
			checkChunk(centerChunkX-1, centerChunkY  );
			checkChunk(centerChunkX  , centerChunkY  );
			checkChunk(centerChunkX+1, centerChunkY  );
			checkChunk(centerChunkX-1, centerChunkY+1);
			checkChunk(centerChunkX  , centerChunkY+1);
			checkChunk(centerChunkX+1, centerChunkY+1);
		}
	}
	
	/**
	 * Checks if chunk must be loaded or deleted.
	 * @param x
	 * @param y 
	 */
	private void checkChunk(int x, int y){
		Map map = Controller.getMap();
		if (map.getChunk(x, y)==null) {
			map.loadChunk(x, y);
		} else {
			map.getChunk(x, y).increaseCameraAccesCounter();//mark that it was accessed
		}
	}

	/**
	 * Renders the viewport
	 *
	 * @param view
	 * @param camera
	 */
	public void render(final GameView view, final Camera camera) {
		if (Controller.getMap() != null) { //render only if map exists 

			view.getBatch().setProjectionMatrix(combined);
			view.getShapeRenderer().setProjectionMatrix(combined);
			//set up the viewport, y-up
			Gdx.gl.glViewport(
				screenPosX,
				Gdx.graphics.getHeight() - screenHeight - screenPosY,
				screenWidth,
				screenHeight
			);

			//render map
			ArrayList<AbstractGameObject> depthlist = createDepthList();
			
			Gdx.gl20.glEnable(GL_BLEND); // Enable the OpenGL Blending functionality 
			//Gdx.gl20.glBlendFunc(GL_SRC_ALPHA, GL20.GL_CONSTANT_COLOR);
			

			view.setDebugRendering(false);
			view.getBatch().begin();
				//render vom bottom to top
				for (AbstractGameObject renderobject : depthlist) {
					renderobject.render(view, camera);
				}
			view.getBatch().end();

			//if debugging render outline again
			if (CVar.get("debugObjects").getValueb()) {
				view.setDebugRendering(true);
				view.getBatch().begin();
					//render vom bottom to top
					for (AbstractGameObject renderobject : depthlist) {
						renderobject.render(view, camera);
					}
				view.getBatch().end();	
			}
			
			//outline 3x3 chunks
			if (CVar.get("debugObjects").getValueb()) {
				view.getShapeRenderer().setColor(Color.RED.cpy());
				view.getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);
				view.getShapeRenderer().rect(
					-Chunk.getGameWidth(),//one chunk to the left
					-Chunk.getGameDepth(),//two chunks down
					Map.getGameWidth(),
					Map.getGameDepth() / 2
				);
				view.getShapeRenderer().line(
					-Chunk.getGameWidth(),
					-Chunk.getGameDepth()/2,
					-Chunk.getGameWidth()+Map.getGameWidth(),
					-Chunk.getGameDepth()/2
				);
				view.getShapeRenderer().line(
					-Chunk.getGameWidth(),
					0,
					-Chunk.getGameWidth()+Map.getGameWidth(),
					0
				);
				view.getShapeRenderer().line(
					0,
					Chunk.getGameDepth()/2,
					0,
					-Chunk.getGameDepth()
				);
				view.getShapeRenderer().line(
					Chunk.getGameWidth(),
					Chunk.getGameDepth()/2,
					Chunk.getGameWidth(),
					-Chunk.getGameDepth()
				);
				view.getShapeRenderer().end();
			}
		}

		if (damageoverlay > 0.0f) {
			WE.getEngineView().getBatch().begin();
				Texture texture = WE.getAsset("com/BombingGames/WurfelEngine/Core/images/bloodblur.png");
				Sprite overlay = new Sprite(texture);
				overlay.setOrigin(0, 0);
				overlay.scale(1.4f);
				overlay.setColor(1, 0, 0, damageoverlay);
				overlay.draw(WE.getEngineView().getBatch());
			WE.getEngineView().getBatch().end();
		}
		
		view.drawString("z level: " + zRenderingLimit, screenPosX+200, screenPosY+100, true);
	}

	/**
	 * Fills the map into a list and sorts it in the order of the rendering,
	 * called the "depthlist".
	 *
	 * @return
	 */
	private ArrayList<AbstractGameObject> createDepthList() {
		ArrayList<AbstractGameObject> depthsort = new ArrayList<>(400);//start by size 400
		if (CVar.get("enableHSD").getValueb()){
			//add hidden surfeace depth buffer
			for (ClippingCell[] y : clipping) {
				for (ClippingCell x : y) {
					for (Block block : x) {
						//only add if in view plane to-do
						if (
							inViewFrustum(
								block.getPosition().getViewSpcX(gameView),
								block.getPosition().getViewSpcY(gameView))
							)
							depthsort.add(block);
					}
				}
			}
		}else {
			CameraSpaceIterator iterator = new CameraSpaceIterator(centerChunkX, centerChunkY, -1);
			while (iterator.hasNext()) {//up to zRenderingLimit
				Block block = iterator.next();
				if (!block.isHidden()){
					depthsort.add(block);
				}
			}
		}
		
		//add entitys
		for (AbstractEntity entity : Controller.getMap().getEntitys()) {
            if (! entity.isHidden()
				&& inViewFrustum(
					entity.getPosition().getViewSpcX(gameView),
					entity.getPosition().getViewSpcY(gameView)
				   )
                && entity.getPosition().getZGrid() < zRenderingLimit
            )
				depthsort.add(entity);
			}
		//sort the list
		if (depthsort.size() > 0) {
			return sortDepthList(depthsort, 0, depthsort.size()-1);
		}
		return depthsort;
	}
	
	/**
	 * checks if the projected position is inside the view Frustum
	 * @param proX
	 * @param proY
	 * @return 
	 */
	private boolean inViewFrustum(int proX, int proY){
		return 
				(position.y + getHeightInViewSpc()/2)
				>
				(proY- Block.SCREEN_HEIGHT*2)//bottom of sprite
			&&
				(proY+ Block.SCREEN_HEIGHT2+Block.SCREEN_DEPTH)//top of sprite
				>
				position.y - getHeightInViewSpc()/2
			&&
				(proX+ Block.SCREEN_WIDTH2)//right side of sprite
				>
				position.x - getWidthInViewSpc()/2
			&&
				(proX- Block.SCREEN_WIDTH2)//left side of sprite
				<
				position.x + getWidthInViewSpc()/2
		;
	}

	/**
	 * Using Quicksort to sort. From small to big values.
	 *
	 * @param depthsort the unsorted list
	 * @param low the lower border
	 * @param high the higher border
	 */
	private ArrayList<AbstractGameObject> sortDepthList(ArrayList<AbstractGameObject> depthsort, int low, int high) {
		int left = low;
		int right = high;
		int middle = depthsort.get((low + high) / 2).getDepth(gameView);

        while (left <= right){    
            while(depthsort.get(left).getDepth(gameView) < middle) left++; 
            while(depthsort.get(right).getDepth(gameView) > middle) right--;

			if (left <= right) {
				AbstractGameObject tmp = depthsort.set(left, depthsort.get(right));
				depthsort.set(right, tmp);
				left++;
				right--;
			}
		}

		if (low < right) {
			sortDepthList(depthsort, low, right);
		}
		if (left < high) {
			sortDepthList(depthsort, left, high);
		}

		return depthsort;
	}

	/**
	 * Using InsertionSort to sort. Needs further testing but actually a bit
	 * faster than quicksort because data ist almost presorted.
	 *
	 * @param depthsort unsorted list
	 * @return sorted list
	 * @since 1.2.20
	 */
	private ArrayList<AbstractGameObject> sortDepthList(ArrayList<AbstractGameObject> depthsort) {
		int i, j;
		AbstractGameObject newValue;
		for (i = 1; i < depthsort.size(); i++) {
			newValue = depthsort.get(i);
			j = i;
			while (j > 0 && depthsort.get(j - 1).getDepth(gameView) > newValue.getDepth(gameView)) {
				depthsort.set(j, depthsort.get(j - 1));
				j--;
			}
			depthsort.set(j, newValue);
		}
		return depthsort;
	}
	
	public void hiddenSurfaceDetection(){
		//create empty array clipping fields
		clipping = new ClippingCell[Map.getBlocksX()][Map.getBlocksY()+Map.getBlocksZ()];
		for (ClippingCell[] y : clipping) {
			for (int x = 0; x < y.length; x++) {
				y[x] = new ClippingCell();
			}
		}
		//the iterator which iterates over the map
		CameraSpaceIterator iterMap = new CameraSpaceIterator(centerChunkX, centerChunkY, -1);
		
		//loop over map covered by camera
		while (iterMap.hasNext()){
			Block block = iterMap.next();
			
			if (!block.isHidden()) {//ignore hidden blocks
				int x= -Chunk.getBlocksX() * ( centerChunkX-1 - iterMap.getCurrentChunk().getChunkX() )//skip chunks
					+ iterMap.getCurrentIndex()[0];//position inside the chunk
				int y = -Chunk.getBlocksY() * ( centerChunkY-1 - iterMap.getCurrentChunk().getChunkY() )//skip chunks
					+ iterMap.getCurrentIndex()[1]-iterMap.getCurrentIndex()[2]*2;//y and z projected on one plane
				ClippingCell cell = clipping[x][y]; //tmp var for current cell
				
				if (cell.isEmpty()){
					cell.push(block);
				} else {
					if ( block.getDepth(gameView) > cell.getLast().getDepth(gameView) ){
						if (!block.isTransparent()) {
							cell.clear();
						}
						cell.push(block);
					}
				}
			}
		}
	}

	/**
	 * Set the zoom factor and regenerates the sprites.
	 *
	 * @param zoom
	 */
	public void setZoom(float zoom) {
		this.zoom = zoom;
		updateGameSpaceSize();
	}

	/**
	 * Returns the zoomfactor.
	 *
	 * @return zoomfactor applied on the game world
	 */
	public float getZoom() {
		return zoom;
	}

	/**
	 * Returns a scaling factor calculated by the width to achieve the same
	 * viewport size with every resolution
	 *
	 * @return a scaling factor applied on the projection
	 */
	public float getScreenSpaceScaling() {
		return screenWidth / (float) CVar.get("renderResolutionWidth").getValuei();
	}
	
	/**
	 * 
	 * @return The highest level wich is rendered.
	 */
	public int getZRenderingLimit() {
		return zRenderingLimit;
	}

	/**
	 *
	 * @param limit minimum is 0
	 */
	public void setZRenderingLimit(int limit) {
		if (limit != zRenderingLimit){//only if it differs
		
			zRenderingLimit = limit;

			//clamp
			if (limit >= Map.getBlocksZ()) {
				zRenderingLimit = Map.getBlocksZ();
			} else if (limit < 0) {
				zRenderingLimit = 0;//min is 0
			}
			
			hiddenSurfaceDetection();
		}
	}

	/**
	 * Use this if you want to focus on a special groundBlock.
	 *
	 * @param coord the coordaintes of the groundBlock.
	 */
	public void focusOnCoords(Coordinate coord) {
		focusCoordinates = coord;
		focusEntity = null;
	}

	/**
	 * Returns the left border of the visible area.
	 *
	 * @return measured in grid-coordinates
	 */
	public int getVisibleLeftBorder() {
		//TODO
		return (int) ((position.x-getWidthInViewSpc()/2) / AbstractGameObject.SCREEN_WIDTH);
	}
	
	/**
	 * Get the leftmost block-coordinate covered by the camera.
	 * @return the left (X) border coordinate
	 */
	public int getCoveredLeftBorder() {
		return Controller.getMap().getChunk(centerChunkX-1, centerChunkY).getTopLeftCoordinate().getX();
	}

	/**
	 * Returns the right border of the camera covered area.
	 *
	 * @return measured in grid-coordinates
	 */
	public int getVisibleRightBorder() {
		//TODO
		return (int) ((position.x + getWidthInViewSpc()/2) / AbstractGameObject.SCREEN_WIDTH + 1);
	}
	
	/**
	 * Get the rightmost block-coordinate covered by the camera.
	 * @return the right (X) border coordinate
	 */
	public int getCoveredRightBorder() {
		return Controller.getMap().getChunk(centerChunkX+1, centerChunkY).getTopLeftCoordinate().getX()
			+ Chunk.getBlocksX()-1;
	}

	/**
	 * Returns the top seight border of the camera covered groundBlock
	 * @return measured in grid-coordinates
	 */
	public int getVisibleBackBorder() {
		//TODO
		return (int) (
			(position.y + getHeightInViewSpc()/2)//camera top border
			/ -AbstractGameObject.SCREEN_DEPTH2);//back to game space
	}
	
	/**
	 * 
	 * @return the top/back (Y) border coordinate
	 */
	public int getCoveredBackBorder() {
		return Controller.getMap().getChunk(centerChunkX, centerChunkY-1).getTopLeftCoordinate().getY();
	}

	/**
	 * Returns the bottom seight border y-coordinate of the highest groundBlock
	 *
	 * @return measured in grid-coordinates
	 */
	public int getVisibleFrontBorder() {
		return (int) (
			(position.y+ getHeightInViewSpc()/2) //bottom camera border
			/ -AbstractGameObject.SCREEN_DEPTH2 //back to game coordinates
		);
	}
	
	/**
	 * 
	 * @return the bottom/front (Y) border coordinate
	 */
	public int getCoveredFrontBorder() {
		return Controller.getMap().getChunk(centerChunkX, centerChunkY+1).getTopLeftCoordinate().getY()
			+ Chunk.getBlocksY()-1;
	}

	/**
	 * The Camera Position in the game world.
	 *
	 * @return game in pixels
	 */
	public float getViewSpaceX() {
		return position.x;
	}

	/**
	 * The Camera left Position in the game world.
	 *
	 * @param x game in pixels
	 */
	public void setViewSpaceX(float x) {
		position.x = x;
	}

	/**
	 * The Camera's center position in the game world. view space. y up
	 *
	 * @return in camera position game space
	 */
	public float getViewSpaceY() {
		return position.y;
	}

	/**
	 * The Camera's center position in the game world. view space. y up
	 *
	 * @param y in game space
	 */
	public void setProjectionSpaceY(float y) {
		position.y = y;
	}

	/**
	 * The amount of game pixel which are visible in X direction without zoom.
	 * For screen pixels use {@link #getWidthInScreenSpc()}.
	 *
	 * @return in game pixels
	 */
	public final int getWidthInGameSpc() {
		return gameSpaceWidth;
	}

	/**
	 * The amount of game pixel which are visible in Y direction without zoom. For screen pixels use {@link #getHeightInScreenSpc() }.
	 *
	 * @return in game pixels
	 */
	public final int getHeightInGameSpc() {
		return gameSpaceHeight;
	}
	
	/**
	 * updates the cache
	 */
	public final void updateGameSpaceSize(){
		gameSpaceWidth = CVar.get("renderResolutionWidth").getValuei();
		gameSpaceHeight = screenHeight;
	}
		
	/**
	 * The amount of game world pixels which are visible in X direction after the zoom has been applied.
	 * For screen pixels use {@link #getWidthInScreenSpc()}.
	 *
	 * @return in view pixels
	 */
	public final int getWidthInViewSpc() {
		return (int) (gameSpaceWidth/zoom);
	}

	/**
	 * The amount of game pixel which are visible in Y direction after the zoom has been applied. For screen pixels use {@link #getHeightInScreenSpc() }.
	 *
	 * @return in view pixels
	 */
	public final int getHeightInViewSpc() {
		return (int) (gameSpaceHeight/getScreenSpaceScaling()/zoom);
	}
	
	/**
	 * The amount of pixels rendered in x direction. The zoom has been applied.<br />
	 * For screen pixels use {@link #getWidthInScreenSpc()}.
	 *
	 * @return in projection pixels
	 */
	public final int getWidthInProjectionSpc() {
		return (int) (gameSpaceWidth*zoom);
	}

	/**
	 * The amount of pixels rendered in x direction. The zoom has been applied.<br />
	 * For screen pixels use {@link #getHeightInScreenSpc()}.
	 *
	 * @return in projection pixels
	 */
	public final int getHeightInProjectionSpc() {
		return (int) (gameSpaceHeight*zoom);
	}
	
	/**
	 * Returns the position of the cameras output (on the screen)
	 *
	 * @return in projection pixels
	 */
	public int getScreenPosX() {
		return screenPosX;
	}

	/**
	 * Returns the position of the camera (on the screen)
	 *
	 * @return
	 */
	public int getScreenPosY() {
		return screenPosY;
	}

	/**
	 * Returns the height of the camera outpu.
	 *
	 * @return the value before scaling
	 */
	public int getHeightInScreenSpc() {
		return screenHeight;
	}

	/**
	 * Returns the width of the camera output.
	 *
	 * @return the value before scaling
	 */
	public int getWidthInScreenSpc() {
		return screenWidth;
	}

	/**
	 * Does the cameras output cover the whole screen?
	 *
	 * @return
	 */
	public boolean isFullWindow() {
		return fullWindow;
	}

	/**
	 * Set to true if the camera's output should cover the whole window
	 *
	 * @param fullWindow
	 */
	public void setFullWindow(boolean fullWindow) {
		this.fullWindow = fullWindow;
		this.screenHeight = Gdx.graphics.getHeight();
		this.screenWidth = Gdx.graphics.getWidth();
		this.screenPosX = 0;
		this.screenPosY = 0;
		updateGameSpaceSize();
	}

	/**
	 * Should be called when resized
	 *
	 * @param width width of window
	 * @param height height of window
	 */
	public void resize(int width, int height) {
		if (fullWindow) {
			this.screenWidth = width;
			this.screenHeight = height;
			this.screenPosX = 0;
			this.screenPosY = 0;
			updateGameSpaceSize();
		}
	}

	public void setScreenSize(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;
		updateGameSpaceSize();
	}

	/**
	 * Move x and y coordinate
	 *
	 * @param x
	 * @param y
	 */
	public void move(int x, int y) {
		this.position.x += x;
		this.position.y += y;
	}

	/**
	 *
	 * @param opacity
	 */
	public void setDamageoverlayOpacity(float opacity) {
		this.damageoverlay = opacity;
	}

	/**
	 * shakes the screen
	 * @param amplitude
	 * @param time 
	 */
	public void shake(float amplitude, float time) {
		shakeAmplitude = amplitude;
		shakeTime = time;
	}

	
	@Override
	public void onMapChange() {
		hiddenSurfaceDetection();
	}

	@Override
	public void onChunkChange(Chunk chunk) {
	}
	
	

	/**
	 * Returns the focuspoint
	 * @return 
	 */
	public Coordinate getCenter() {
		if (focusCoordinates!=null)
			return focusCoordinates;
		else if (focusEntity!=null)
			return focusEntity.getPosition().getCoord();
		else return
			new Point(
				position.x,
				Map.getGameDepth()-position.y,
				0
			).getCoord();//view to game
	}

	public boolean[] getClipping(Coordinate coords) {
		int indexX = coords.getX()-getCoveredLeftBorder();
		int indexY = coords.getY()-getCoveredBackBorder()-coords.getZ()*2;
		
//check if covered by depth buffer
		if ( indexX >= 0 && indexY >= 0 ) {
			return new boolean[]{
				clipping[indexX][indexY].getClippingLeft(),
				clipping[indexX][indexY].getClippingTop(),
				clipping[indexX][indexY].getClippingRight()
			};
		} else {
			//if not return fully clipped
			return new boolean[]{true, true, true};
		}
	}

	/**
	 * 
	 * @param coord
	 * @return is a coordiante clipped somewhere? 
	 * @since 1.3.10
	 */
	public boolean isClipped(Coordinate coord) {
		boolean[] tmp = getClipping(coord);
		return (tmp[0] || tmp[1] || tmp[2]);
	}

	public int getCenterChunkCoordX() {
		return centerChunkX;
	}

	public int getCenterChunkCoordY() {
		return centerChunkY;
	}

	private static class ClippingCell extends ArrayDeque<Block>{
		private static final long serialVersionUID = 1L;
		private boolean topLeft, topRight, leftLeft, leftRight, rightLeft, rightRight;

		public boolean getClippingLeft() {
			return false;//to-do
		}

		public boolean getClippingTop() {
			return false;//to-do
		}

		public boolean getClippingRight() {
			return false;//to-do
		}
	}
}