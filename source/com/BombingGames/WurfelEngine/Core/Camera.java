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
package com.BombingGames.WurfelEngine.Core;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.BombingGames.WurfelEngine.Core.Map.AbstractMap;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Chunk;
import com.BombingGames.WurfelEngine.Core.Map.ChunkMap;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Iterators.CameraSpaceIterator;
import com.BombingGames.WurfelEngine.Core.Map.Iterators.DataIterator;
import com.BombingGames.WurfelEngine.Core.Map.LinkedWithMap;
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
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Creates a virtual camera wich displays the game world on the viewport.
 *
 * @author Benedikt Vogler
 */
public class Camera implements LinkedWithMap {
	/**
	 * the map which is covered by the camera
	 */
	private AbstractMap map = Controller.getMap();
	/**
	 * top limit
	 */
	private int zRenderingLimit = map.getBlocksZ();
	
	private boolean zRenderinlimitEnabled = false;

	private RenderBlock[][][] cameraContentBlocks;
	
	/**
	 * the position of the camera in view space. Y-up. Read only field.
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
	 * the position on the screen (viewportWidth/Height ist the affiliated).
	 * Origin top left.
	 */
	private int screenPosX, screenPosY;

	private float zoom = 1;

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
	private int viewSpaceWidth;
	private int viewSpaceHeight;
	private int centerChunkX;
	private int centerChunkY;
	/**
	 * true if camera is currently rendering
	 */
	private boolean active = false;
	private AbstractGameObject[] depthlist;
	/**
	 * amount of objects to be rendered
	 */
	private int objectsToBeRendered = 0;

	/**
	 * Updates the needed chunks after recaclucating the center chunk of the
	 * camera. It is set via an absolute value.
	 */
	private void initFocus() {
		centerChunkX = (int) Math.floor(position.x / Chunk.getViewWidth());
		centerChunkY = (int) Math.floor(-position.y / Chunk.getViewDepth());
		if (WE.CVARS.getValueB("mapUseChunks"))
			updateNeededChunks();
	}

	/**
	 * Creates a camera pointing at the middle of the map.
	 *
	 * @param x the position in the application window (viewport position).
	 * Origin top left
	 * @param y the position in the application window (viewport position).
	 * Origin top left
	 * @param width The width of the image (screen size) the camera creates on
	 * the application window (viewport)
	 * @param height The height of the image (screen size) the camera creates on
	 * the application window (viewport)
	 * @param view
	 */
	public Camera(final int x, final int y, final int width, final int height, GameView view) {
		map = Controller.getMap();
		zRenderingLimit = map.getBlocksZ();
		
		gameView = view;
		screenWidth = width;
		screenHeight = height;
		screenPosX = x;
		screenPosY = y;
		updateViewSpaceSize();

		Point center = map.getCenter();
		position.x = center.getViewSpcX(gameView);
		position.y = center.getViewSpcY(gameView);
		initFocus();
	}

	/**
	 * Creates a fullscale camera pointing at the middle of the map.
	 *
	 * @param view
	 */
	public Camera(GameView view) {
		gameView = view;
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		updateViewSpaceSize();

		Point center = map.getCenter();
		position.x = center.getViewSpcX(gameView);
		position.y = center.getViewSpcY(gameView);
		fullWindow = true;
		initFocus();
	}

	/**
	 * Create a camera focusin a specific coordinate. It can later be changed
	 * with <i>focusCoordinates()</i>. Screen size does refer to the output of
	 * the camera not the real size on the display.
	 *
	 * @param center the point where the camera focuses
	 * @param x the position in the application window (viewport position).
	 * Origin top left
	 * @param y the position in the application window (viewport position).
	 * Origin top left
	 * @param width The width of the image (screen size) the camera creates on
	 * the application window (viewport)
	 * @param height The height of the image (screen size) the camera creates on
	 * the application window (viewport)
	 * @param view
	 */
	public Camera(final Point center, final int x, final int y, final int width, final int height, GameView view) {
		gameView = view;
		screenWidth = width;
		screenHeight = height;
		screenPosX = x;
		screenPosY = y;
		updateViewSpaceSize();
		position.x = center.getViewSpcX(gameView);
		position.y = center.getViewSpcY(gameView);
		initFocus();
	}

	/**
	 * Creates a camera focusing an entity. The values are sceen-size and do
	 * refer to the output of the camera not the real display size.
	 *
	 * @param focusentity the entity wich the camera focuses and follows
	 * @param x the position in the application window (viewport position).
	 * Origin top left
	 * @param y the position in the application window (viewport position).
	 * Origin top left
	 * @param width The width of the image (screen size) the camera creates on
	 * the application window (viewport)
	 * @param height The height of the image (screen size) the camera creates on
	 * the application window (viewport)
	 * @param view
	 */
	public Camera(final AbstractEntity focusentity, final int x, final int y, final int width, final int height, GameView view) {
		gameView = view;
		screenWidth = width;
		screenHeight = height;
		screenPosX = x;
		screenPosY = y;
		updateViewSpaceSize();
		if (focusentity == null) {
			throw new NullPointerException("Parameter 'focusentity' is null");
		}
		WE.getConsole().add("Creating new camera which is focusing an entity: " + focusentity.getName());
		this.focusEntity = focusentity;
		if (!focusentity.spawned()) {
			throw new NullPointerException(focusentity.getName() + " is not spawned yet");
		}
		position.x = focusEntity.getPosition().getViewSpcX(gameView);
		position.y = (int) (focusEntity.getPosition().getViewSpcY(gameView)
			+ focusEntity.getDimensionZ() * AbstractPosition.SQRT12 / 2);
		initFocus();
	}

	/**
	 * Updates the camera.
	 *
	 * @param dt
	 */
	public final void update(float dt) {
		if (active) {
			if (focusEntity != null) {
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
			
			//recalculate the center position
			updateCenter();
			
			//move camera to the focus 
			view.setToLookAt(
				new Vector3(position, 0),
				new Vector3(position, -1),
				up
			);

			//orthographic camera, libgdx stuff
			projection.setToOrtho(
				(gameView.getOrientation() == 2 ? -1 : 1) * -getWidthInProjSpc() / 2,
				(gameView.getOrientation() == 2 ? -1 : 1) * getWidthInProjSpc() / 2,
				-getHeightInProjSpc() / 2,
				getHeightInProjSpc() / 2,
				0,
				1
			);

			//set up projection matrices
			combined.set(projection);
			Matrix4.mul(combined.val, view.val);
			
			if (cameraContentBlocks!=null) {
				for (RenderBlock[][] x : cameraContentBlocks) {
					for (RenderBlock[] y : x) {
						for (RenderBlock z : y) {
							if (z != null)
								z.update(dt);
						}
					}
				}
			}

			//don't know what this does
			//Gdx.gl20.glMatrixMode(GL20.GL_PROJECTION);
			//Gdx.gl20.glLoadMatrixf(projection.val, 0);
			//Gdx.gl20.glMatrixMode(GL20.GL_MODELVIEW);
			//Gdx.gl20.glLoadMatrixf(view.val, 0);
			//invProjectionView.set(combined);
			//Matrix4.inv(invProjectionView.val);
		}
	}
	
	/**
	 * Check if center has to be moved and if chunks must be loaded or unloaded performs according actions.
	 */
	public void updateCenter(){
		//if chunkmap check for chunk movement
		if (WE.CVARS.getValueB("mapUseChunks")) {
			int oldX = centerChunkX;
			int oldY = centerChunkY;
			
			ChunkMap chunkMap = (ChunkMap) map;

			
			//check if chunkswitch left
			if (
				getVisibleLeftBorder()
				<
				chunkMap.getChunk(centerChunkX-1, centerChunkY).getTopLeftCoordinate().getX()
				//&& centerChunkX-1==//calculated xIndex -1
				) {
				centerChunkX--;
			}

			if (
				getVisibleRightBorder()
				>
				chunkMap.getChunk(centerChunkX+1, centerChunkY).getTopLeftCoordinate().getX()+Chunk.getBlocksX()
				//&& centerChunkX-1==//calculated xIndex -1
				) {
				centerChunkX++;
			}

			//the following commented lines were working once and is still a preferable way to do this algo because it avoid spots wher small movements causes ofen recalcucating of HSD. At the moment is absolute calculated. The commented code is relative baded.
			/*
			if (
				getVisibleBackBorder()
				<
				chunkMap.getChunk(centerChunkX, centerChunkY-1).getTopLeftCoordinate().getX()
				//&& centerChunkX-1==//calculated xIndex -1
				) {
				centerChunkY--;
			}
			//check in view space
			if (
				position.y- getHeightInProjSpc()/2
				<
				map.getBlocksZ()*AbstractGameObject.VIEW_HEIGHT
				-AbstractGameObject.VIEW_DEPTH2*(
				chunkMap.getChunk(centerChunkX, centerChunkY+1).getTopLeftCoordinate().getY()+Chunk.getBlocksY()//bottom coordinate
				)
				//&& centerChunkX-1==//calculated xIndex -1
				) {
				centerChunkY++;
			}*/
			
			//his line is needed because the above does not work
			centerChunkY = (int) Math.floor(-position.y / Chunk.getViewDepth());
			
			updateNeededChunks();
			if (oldX!=centerChunkX || oldY!=centerChunkY)
				updateCache();
		}
	}

	/**
	 * checks which chunks must be loaded around the center
	 */
	private void updateNeededChunks() {
		//check every chunk
		if (centerChunkX == 0 && centerChunkY == 0 || WE.CVARS.getValueB("mapChunkSwitch")) {
			checkChunk(centerChunkX - 1, centerChunkY - 1);
			checkChunk(centerChunkX, centerChunkY - 1);
			checkChunk(centerChunkX + 1, centerChunkY - 1);
			checkChunk(centerChunkX - 1, centerChunkY);
			checkChunk(centerChunkX, centerChunkY);
			checkChunk(centerChunkX + 1, centerChunkY);
			checkChunk(centerChunkX - 1, centerChunkY + 1);
			checkChunk(centerChunkX, centerChunkY + 1);
			checkChunk(centerChunkX + 1, centerChunkY + 1);
		}
	}

	/**
	 * Checks if chunk must be loaded or deleted.
	 *
	 * @param x
	 * @param y
	 */
	private void checkChunk(int x, int y) {
		ChunkMap chunkMap = (ChunkMap) map;
		if (chunkMap.getChunk(x, y) == null) {
			chunkMap.loadChunk(x, y);//load missing chunks
		} else {
			chunkMap.getChunk(x, y).increaseCameraHandleCounter();//mark that it was accessed
		}
	}
	
	/**
	 * Renders the viewport
	 *
	 * @param view
	 * @param camera
	 */
	public void render(final GameView view, final Camera camera) {
		if (active && Controller.getMap() != null) { //render only if map exists 

			view.getBatch().setProjectionMatrix(combined);
			view.getShapeRenderer().setProjectionMatrix(combined);
			//set up the viewport, yIndex-up
			Gdx.gl.glViewport(
				screenPosX,
				Gdx.graphics.getHeight() - screenHeight - screenPosY,
				screenWidth,
				screenHeight
			);

			//render map
			createDepthList();

			Gdx.gl20.glEnable(GL_BLEND); // Enable the OpenGL Blending functionality 
			//Gdx.gl20.glBlendFunc(GL_SRC_ALPHA, GL20.GL_CONSTANT_COLOR);

			view.setDebugRendering(false);
			view.getBatch().begin();
				//send a Vector4f to GLSL
				if (WE.CVARS.getValueB("enablelightengine")) {
					view.getShader().setUniformf(
						"sunNormal",
						Controller.getLightEngine().getSun().getNormal()
					);
					view.getShader().setUniformf(
						"sunColor",
						Controller.getLightEngine().getSun().getLight()
					);
					view.getShader().setUniformf(
						"moonNormal",
						Controller.getLightEngine().getMoon().getNormal()
					);
					view.getShader().setUniformf(
						"moonColor",
						Controller.getLightEngine().getMoon().getLight()
					);
					view.getShader().setUniformf(
						"ambientColor",
						Controller.getLightEngine().getAmbient()
					);
				}

				//bind normal map to texture unit 1
				if (WE.CVARS.getValueB("LEnormalMapRendering")) {
					AbstractGameObject.getTextureNormal().bind(1);
				}

					//bind diffuse color to texture unit 0
				//important that we specify 0 otherwise we'll still be bound to glActiveTexture(GL_TEXTURE1)
				AbstractGameObject.getTextureDiffuse().bind(0);

				//render vom bottom to top
				for (int i = 0; i < objectsToBeRendered; i++) {
					depthlist[i].render(view, camera);
				}
			view.getBatch().end();

			//if debugging render outline again
			if (WE.CVARS.getValueB("DevDebugRendering")) {
				view.setDebugRendering(true);
				view.getBatch().begin();
				//render vom bottom to top
				for (AbstractGameObject renderobject : depthlist) {
					renderobject.render(view, camera);
				}
				view.getBatch().end();
			}

			//outline 3x3 chunks
			if (WE.CVARS.getValueB("DevDebugRendering")) {
				view.getShapeRenderer().setColor(Color.RED.cpy());
				view.getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);
				view.getShapeRenderer().rect(-Chunk.getGameWidth(),//one chunk to the left
					-Chunk.getGameDepth(),//two chunks down
map.getGameWidth(),
					map.getGameDepth() / 2
				);
				view.getShapeRenderer().line(-Chunk.getGameWidth(),
					-Chunk.getGameDepth() / 2,
					-Chunk.getGameWidth() + map.getGameWidth(),
					-Chunk.getGameDepth() / 2
				);
				view.getShapeRenderer().line(-Chunk.getGameWidth(),
					0,
					-Chunk.getGameWidth() + map.getGameWidth(),
					0
				);
				view.getShapeRenderer().line(
					0,
					Chunk.getGameDepth() / 2,
					0,
					-Chunk.getGameDepth()
				);
				view.getShapeRenderer().line(
					Chunk.getGameWidth(),
					Chunk.getGameDepth() / 2,
					Chunk.getGameWidth(),
					-Chunk.getGameDepth()
				);
				view.getShapeRenderer().end();
			}
			if (damageoverlay > 0.0f) {
				WE.getEngineView().getBatch().begin();
				Texture texture = WE.getAsset("com/BombingGames/WurfelEngine/Core/images/bloodblur.png");
				Sprite overlay = new Sprite(texture);
				overlay.setOrigin(0, 0);
				//somehow reverse the viewport transformation, needed for split-screen
				overlay.setSize(getWidthInScreenSpc(), getHeightInScreenSpc() * (float) Gdx.graphics.getHeight() / getHeightInScreenSpc());
				overlay.setColor(1, 0, 0, damageoverlay);
				overlay.draw(WE.getEngineView().getBatch());
				WE.getEngineView().getBatch().end();
			}
		}
	}

	/**
	 * Fills the map into a list and sorts it in the order of the rendering,
	 * called the "depthlist".
	 *
	 * @return
	 */
	private AbstractGameObject[] createDepthList() {
		//register memory space onyl once then reuse
		if (depthlist==null || WE.CVARS.getValueI("MaxSprites") != depthlist.length)
			depthlist = new AbstractGameObject[WE.CVARS.getValueI("MaxSprites")];
		
		objectsToBeRendered=0;
		DataIterator iterator = new DataIterator(
			cameraContentBlocks,//iterate over camera content
			0,					//from layer0
			map.getBlocksZ()//one more because of ground layer
		);
		iterator.setBorders(
			getVisibleLeftBorder()-getCoveredLeftBorder(),
			getVisibleRightBorder()-getCoveredLeftBorder(),
			getVisibleBackBorder()-getCoveredBackBorder(),
			getVisibleFrontBorderHigh()-getCoveredBackBorder()
		);
		
		if (WE.CVARS.getValueB("enableHSD")) {
			//add hidden surfeace depth buffer
			while (iterator.hasNext()) {//up to zRenderingLimit	it
				RenderBlock block = (RenderBlock) iterator.next();
				//only add if in view plane to-do
				if (
					block != null
					&& !block.isClipped()
					&& !block.isHidden()
					&& inViewFrustum(
						block.getPosition().getViewSpcX(gameView),
						block.getPosition().getViewSpcY(gameView))
					&& (!zRenderinlimitEnabled || block.getPosition().getZ() < zRenderingLimit)
				) {
					depthlist[objectsToBeRendered] = block;
					objectsToBeRendered++;
					if (objectsToBeRendered >= depthlist.length) break;//fill only up to available size
				}
			}
		} else {
			while (iterator.hasNext()) {//up to zRenderingLimit
				RenderBlock block = (RenderBlock) iterator.next();
				if (block!= null && !block.isHidden()) {
					depthlist[objectsToBeRendered] = block;
					objectsToBeRendered++;
					if (objectsToBeRendered >= depthlist.length) break;//fill only up to available size
				}
			}
		}
		
		if (objectsToBeRendered < depthlist.length) {

			//add entitys
			for (AbstractEntity entity : Controller.getMap().getEntitys()) {
				if (
				!entity.isHidden()
					&& inViewFrustum(
						entity.getPosition().getViewSpcX(gameView),
						entity.getPosition().getViewSpcY(gameView)
					)
					&&  (!zRenderinlimitEnabled || entity.getPosition().getZGrid() < zRenderingLimit)
				) {
					depthlist[objectsToBeRendered] = entity;
					objectsToBeRendered++;
					if (objectsToBeRendered >= depthlist.length) break;//fill only up to available size
				}
			}
		}
		//sort the list
		if (objectsToBeRendered > 1) {
			return sortDepthListParallel(depthlist);
		}
		return depthlist;
	}

	/**
	 * checks if the projected position is inside the view Frustum
	 *
	 * @param proX projective space
	 * @param proY
	 * @return
	 */
	private boolean inViewFrustum(int proX, int proY){
		return 
				(position.y + getHeightInProjSpc() / 2)
				>
				(proY - RenderBlock.VIEW_HEIGHT * 2)//bottom of sprite
			&&
				(proY + RenderBlock.VIEW_HEIGHT2 + RenderBlock.VIEW_DEPTH)//top of sprite
				>
				position.y - getHeightInProjSpc() / 2
			&&
				(proX + RenderBlock.VIEW_WIDTH2)//right side of sprite
				>
				position.x - getWidthInProjSpc() / 2
			&&
				(proX - RenderBlock.VIEW_WIDTH2)//left side of sprite
				<
				position.x + getWidthInProjSpc() / 2
		;
	}

	/**
	 * Using Quicksort to sort. From small to big values.
	 *
	 * @param depthsort the unsorted list
	 * @param low the lower border
	 * @param high the higher border
	 */
	private ArrayList<AbstractGameObject> sortDepthListQuick(ArrayList<AbstractGameObject> depthsort, int low, int high) {
		int left = low;
		int right = high;
		int middle = depthsort.get((low + high) / 2).getDepth(gameView);

		while (left <= right) {
			while (depthsort.get(left).getDepth(gameView) < middle) {
				left++;
			}
			while (depthsort.get(right).getDepth(gameView) > middle) {
				right--;
			}
		
			if (left <= right) {
				AbstractGameObject tmp = depthsort.set(left, depthsort.get(right));
				depthsort.set(right, tmp);
				left++;
				right--;
			}
		}
		return depthsort;
	}
	
	/**
	 * experimental feature. Can be slower or faster depending on the scene.
	 * @param depthsort the unsorted list
	 * @return sorted ArrayList 
	 * @since v1.5.3
	 */
	private AbstractGameObject[] sortDepthListParallel(AbstractGameObject[] depthsort){
		Arrays.parallelSort(depthsort,
			0,
			objectsToBeRendered,
			(AbstractGameObject o1, AbstractGameObject o2) -> {
				int a = o1.getDepth(gameView);
				int b = o2.getDepth(gameView);
				if (a < b)
					return -1;
				else if (a==b)
					return 0;
				else
					return 1;
			}
		);
		
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
	private ArrayList<AbstractGameObject> sortDepthListInsertion(ArrayList<AbstractGameObject> depthsort) {
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

	
	/**
	 * updates cached values like clipping
	 */
	protected void updateCache(){
		fillCameraContentBlocks();
		hiddenSurfaceDetection();
	}

	/**
	 * fill the view frustum in the camera with renderblocks
	 */
	private void fillCameraContentBlocks(){
	//fill viewFrustum with RenderBlock data
		cameraContentBlocks = new RenderBlock[map.getBlocksX()][map.getBlocksY()][map.getBlocksZ() + 1];//z +1  because of ground layer
		
		//1. add ground layer
		for (int x = 0; x < cameraContentBlocks.length; x++) {
			for (int y = 0; y < cameraContentBlocks[x].length; y++) {
				cameraContentBlocks[x][y][0] = map.getGroundBlock().toBlock();
				cameraContentBlocks[x][y][0].setPosition(
					new Coordinate(
						map,
						getCoveredLeftBorder()+x,
						getCoveredBackBorder()+y,
						-1
					)
				);
			}
		}
		
		//2. put every other block in the view frustum
		CameraSpaceIterator csIter = new CameraSpaceIterator(
			map,
			centerChunkX,
			centerChunkY,
			0,
			map.getBlocksZ()-1
		);
		
		while (csIter.hasNext()) {
			CoreData block = csIter.next();
			if (block != null) {
				int[] ind = csIter.getCurrentIndex();
				cameraContentBlocks[ind[0]][ind[1]][ind[2]+1] = block.toBlock();
				if (cameraContentBlocks[ind[0]][ind[1]][ind[2]+1] != null)
					cameraContentBlocks[ind[0]][ind[1]][ind[2]+1].setPosition(
						new Coordinate(
							map,
							getCoveredLeftBorder() + ind[0],
							getCoveredBackBorder() + ind[1],
							ind[2]
						)
					);
			}
		}
	}
	
	/**
	 * performs a simple viewFrustum check by looking at the direct neighbours.
	 */
	protected void hiddenSurfaceDetection() {
		Gdx.app.debug("Camera", "hsd around " + centerChunkX + "," + centerChunkY);
		//iterate over max. view frustum
		DataIterator dataIter = new DataIterator(
			cameraContentBlocks,
			0,
			zRenderingLimit//+1 because of ground layer
		);
		
		while (dataIter.hasNext()) {
			RenderBlock next = (RenderBlock) dataIter.next();
			
			if (next != null) {
				//calculate index position relative to camera border
				int x = dataIter.getCurrentIndex()[0];
				int y = dataIter.getCurrentIndex()[1];
				int z = dataIter.getCurrentIndex()[2];
				
				if (z > 0) {//bottom layer always has sides always clipped, 0 is ground layer in this case
					RenderBlock neighbour;
					if (y % 2 == 0) {//next row is shifted right
						if (x>0) {
							neighbour = cameraContentBlocks[x-1][y+1][z];
							if (neighbour!= null && (neighbour.hidingPastBlock() || (neighbour.getCoreData().isLiquid() && next.getCoreData().isLiquid()))) {//left
								next.setClippedLeft();
							}
						} else {
							next.setClippedLeft();
						}
						if (y<cameraContentBlocks[x].length) {
							neighbour = cameraContentBlocks[x][y+1][z];
							if (neighbour!= null && (neighbour.hidingPastBlock() || (neighbour.getCoreData().isLiquid() && next.getCoreData().isLiquid()))) {//right
								next.setClippedRight();
							}
						} else {
							next.setClippedRight();
						}
					} else {//next row is shifted right
						if (y < cameraContentBlocks[x].length-1) {
							neighbour = cameraContentBlocks[x][y+1][z];
							if (neighbour!= null && (neighbour.hidingPastBlock() || (neighbour.getCoreData().isLiquid() && next.getCoreData().isLiquid()))) {//left
								next.setClippedLeft();
							}
						} else {
							next.setClippedLeft();
						}
						if ( x < cameraContentBlocks.length-1 && y < cameraContentBlocks[x].length-1) {
							neighbour = cameraContentBlocks[x+1][y+1][z];
							if (neighbour!= null && (neighbour.hidingPastBlock() || (neighbour.getCoreData().isLiquid() && next.getCoreData().isLiquid()))) {//right
								next.setClippedRight();
							}
						} else {
							next.setClippedRight();
						}
					}
				} else {
					next.setClippedLeft();
					next.setClippedRight();
				}

				//check top
				if (
					z < map.getBlocksZ()
					&& cameraContentBlocks[x][y][z+1] != null
					&& (
						cameraContentBlocks[x][y][z+1].hidingPastBlock()
						|| cameraContentBlocks[x][y][z+1].getCoreData().isLiquid() && next.getCoreData().isLiquid()
					)
				) {
					next.setClippedTop();
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
		updateViewSpaceSize();//todo check for redundant call?
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
		return screenWidth / (float) WE.CVARS.getValueI("renderResolutionWidth");
	}

	/**
	 *
	 * @return The highest level wich is rendered.
	 */
	public int getZRenderingLimit() {
		return zRenderingLimit;
	}

	/**
	 * If the limit is set to the map's height or more it becomes deactivated.
	 * @param limit minimum is 0, everything to this limit becomes rendered
	 */
	public void setZRenderingLimit(int limit) {
		if (limit != zRenderingLimit) {//only if it differs

			zRenderingLimit = limit;
			zRenderinlimitEnabled = true;
			
			//clamp
			if (limit >= map.getBlocksZ()) {
				zRenderingLimit = map.getBlocksZ();
				zRenderinlimitEnabled = false;
			} else if (limit < 0) {
				zRenderingLimit = 0;//min is 0
			}
			hiddenSurfaceDetection();
		}
	}

	/**
	 * Returns the left border of the visible area.
	 *
	 * @return measured in grid-coordinates
	 */
	public int getVisibleLeftBorder() {
		return (int) ((position.x - getWidthInProjSpc() / 2) / AbstractGameObject.VIEW_WIDTH-1);
	}

	/**
	 * Get the leftmost block-coordinate covered by the camera.
	 *
	 * @return the left (X) border coordinate
	 */
	public int getCoveredLeftBorder() {
		return (centerChunkX-1)*Chunk.getBlocksX();
	}

	/**
	 * Returns the right seight border of the camera covered area currently visible.
	 *
	 * @return measured in grid-coordinates
	 */
	public int getVisibleRightBorder() {
		return (int) ((position.x + getWidthInProjSpc() / 2) / AbstractGameObject.VIEW_WIDTH + 1);
	}

	/**
	 * Get the rightmost block-coordinate covered by the camera viewFrustum.
	 *
	 * @return the right (X) border coordinate
	 */
	public int getCoveredRightBorder() {
		return (centerChunkX + 1)*Chunk.getBlocksX() - 1;
	}

	/**
	 * Returns the top seight border of the camera covered groundBlock
	 *
	 * @return measured in grid-coordinates
	 */
	public int getVisibleBackBorder() {
		//TODO verify
		return (int) (
			(position.y + getHeightInProjSpc() / 2)//camera top border
			/ -AbstractGameObject.VIEW_DEPTH2//back to game space
		);
	}

	/**
	 * Clipping
	 * @return the top/back (Y) border coordinate
	 */
	public int getCoveredBackBorder() {
		return (centerChunkY-1)*Chunk.getBlocksY();
	}

	/**
	 * Returns the bottom seight border y-coordinate of the lowest block
	 *
	 * @return measured in grid-coordinates
	 * @see #getVisibleFrontBorderHigh() 
	 */
	public int getVisibleFrontBorderLow() {
		return (int) (
			(position.y- getHeightInProjSpc()/2) //bottom camera border
			/ -AbstractGameObject.VIEW_DEPTH2 //back to game coordinates
		);
	}
	
		/**
	 * Returns the bottom seight border y-coordinate of the highest block
	 *
	 * @return measured in grid-coordinates
	 * @see #getVisibleFrontBorderLow() 
	 */
	public int getVisibleFrontBorderHigh() {
		return (int) (
			(position.y- getHeightInProjSpc()/2) //bottom camera border
			/ -AbstractGameObject.VIEW_DEPTH2 //back to game coordinates
			+cameraContentBlocks[0][0].length*AbstractGameObject.VIEW_HEIGHT/AbstractGameObject.VIEW_DEPTH2 //todo verify, try to add z component
		);
	}

	/**
	 *
	 * @return the bottom/front (Y) border coordinate
	 */
	public int getCoveredFrontBorder() {
		return (centerChunkY + 1)*Chunk.getBlocksY() - 1;
	}

	/**
	 * The Camera Position in the game world.
	 *
	 * @return game in pixels
	 */
	public float getViewSpaceX() {
		return getCenter().getViewSpcX(gameView);
	}

	/**
	 * The Camera's center position in the game world. view space. yIndex up
	 *
	 * @return in camera position game space
	 */
	public float getViewSpaceY() {
		return getCenter().getViewSpcY(gameView);
	}

	/**
	 * The amount of game pixel which are visible in X direction without zoom.
	 * For screen pixels use {@link #getWidthInScreenSpc()}.
	 *
	 * @return in game pixels
	 */
	public final int getWidthInViewSpc() {
		return viewSpaceWidth;
	}

	/**
	 * The amount of game pixel which are visible in Y direction without zoom.
	 * For screen pixels use {@link #getHeightInScreenSpc() }.
	 *
	 * @return in game pixels
	 */
	public final int getHeightInViewSpc() {
		return viewSpaceHeight;
	}

	/**
	 * updates the cache
	 */
	public final void updateViewSpaceSize() {
		viewSpaceWidth = WE.CVARS.getValueI("renderResolutionWidth");
		viewSpaceHeight = (int) (screenHeight /getScreenSpaceScaling());
	}

	/**
	 * The amount of game world pixels which are visible in X direction after
	 * the zoom has been applied. For screen pixels use
	 * {@link #getWidthInScreenSpc()}.
	 *
	 * @return in view pixels
	 */
	public final int getWidthInProjSpc() {
		return (int) (viewSpaceWidth / zoom);
	}

	/**
	 * The amount of game pixel which are visible in Y direction after the zoom
	 * has been applied. For screen pixels use {@link #getHeightInScreenSpc() }.
	 *
	 * @return in projective pixels
	 */
	public final int getHeightInProjSpc() {
		return (int) (viewSpaceHeight / zoom);
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
	 * @return yIndex-down
	 */
	public int getScreenPosY() {
		return screenPosY;
	}

	/**
	 * Returns the height of the camera output.
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
		updateViewSpaceSize();
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
			updateViewSpaceSize();
		}
	}

	/**
	 * updates the screen size
	 *
	 * @param width
	 * @param height
	 */
	public void setScreenSize(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;
		updateViewSpaceSize();
	}

	/**
	 * Move xIndex and yIndex coordinate
	 *
	 * @param x in game space if has focusentity, else in view space (?)
	 * @param y in game space if has focusentity, else in view space (?)
	 */
	public void move(int x, int y) {
		if (focusEntity != null) {
			focusEntity.getPosition().addVector(x, y, 0);
		} else {
			position.x += x;
			position.y += y;
		}
		updateCenter();
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
	 *
	 * @param amplitude
	 * @param time
	 */
	public void shake(float amplitude, float time) {
		shakeAmplitude = amplitude;
		shakeTime = time;
	}

	@Override
	public void onMapChange() {
		if (active) {
			updateCache();
		}
	}

	@Override
	public void onChunkChange(Chunk chunk) {
		//does nothing
	}

	/**
	 * Returns the focuspoint
	 *
	 * @return
	 */
	public Point getCenter() {
		if (focusEntity != null) {
			return focusEntity.getPosition();
		} else {
			return new Point(
				map,
				position.x,
				-position.y * 2,
				0
			);//view to game
		}
	}

	/**
	 * get if a coordinate is clipped
	 *
	 * @param coords
	 * @return
	 */
	public boolean isClipped(Coordinate coords) {
		if (coords.getZ() >= zRenderingLimit) {
			return true;
		}

		//get the index position in the clipping field
		int indexX = coords.getX() - getCoveredLeftBorder();
		int indexY = coords.getY() - getCoveredBackBorder();
		//check if covered by camera
		if (
			   indexX >= 0
			&& indexX < cameraContentBlocks.length
			&& indexY >= 0
			&& indexY < cameraContentBlocks[0].length
			&& cameraContentBlocks[indexX][indexY][coords.getZ()+1] != null //not air
		) {
			return cameraContentBlocks[indexX][indexY][coords.getZ()+1].isClipped();
		} else {
			//if not return fully clipped
			return true;
		}
	}

	public void orientationChange() {
		hiddenSurfaceDetection();
	}

	/**
	 * enable or disable the camera
	 *
	 * @param active
	 */
	public void setActive(boolean active) {
		//turning on
		if ( this.active == false && active == true ) {
			if (WE.CVARS.getValueB("mapUseChunks"))
				updateNeededChunks();
			updateCache();
		}

		this.active = active;
	}
}
