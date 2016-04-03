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
package com.bombinggames.wurfelengine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import static com.badlogic.gdx.graphics.GL20.GL_BLEND;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.gameobjects.Renderable;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Iterators.CameraSpaceIterator;
import com.bombinggames.wurfelengine.core.map.Map;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import com.bombinggames.wurfelengine.core.map.rendering.SideSprite;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Creates a virtual camera wich displays the game world on the viewport. A camer acan be locked to an entity.
 *
 * @author Benedikt Vogler
 */
public class Camera {

	/**
	 * top limit in game space
	 */
	private float zRenderingLimit = Float.POSITIVE_INFINITY;

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
	 * the viewMat matrix *
	 */
	private final Matrix4 viewMat = new Matrix4();
	/**
	 * the combined projection and viewMat matrix
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

	/*
	default is 1, higher is closer
	*/	
	private float zoom = 1;

	private AbstractEntity focusEntity;

	private boolean fullWindow = false;

	/**
	 * the opacity of thedamage overlay
	 */
	private float damageoverlay = 0f;

	private final Vector2 screenshake = new Vector2(0, 0);
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
	private final LinkedList<Renderable> depthlist = new LinkedList<>();
	/**
	 * amount of objects to be rendered, used as an index during filling
	 */
	private int objectsToBeRendered = 0;
	private int renderResWidth;
	private int maxsprites;
	private final Point center = new Point(0, 0, 0);
	private final ArrayList<RenderCell> modifiedCells = new ArrayList<>(30);
	private final LinkedList<AbstractEntity> renderAppendix = new LinkedList<>();
	/**
	 * The radius which is used for loading the chunks around the center. May be reduced after the first time to a smaller value.
	 */
	private int loadingRadius = 10;
	private int id;

	/**
	 * Updates the needed chunks after recaclucating the center chunk of the
	 * camera. It is set via an absolute value.
	 */
	private void initFocus() {
		centerChunkX = (int) Math.floor(position.x / Chunk.getViewWidth());
		centerChunkY = (int) Math.floor(-position.y / Chunk.getViewDepth());
		if (WE.getCVars().getValueB("mapUseChunks")) {
			checkNeededChunks();
		}
	}

	/**
	 * Creates a fullscale camera pointing at the middle of the map.
	 *
	 * @param view
	 */
	public Camera(final GameView view) {
		gameView = view;
		screenWidth = Gdx.graphics.getBackBufferWidth();
		screenHeight = Gdx.graphics.getBackBufferHeight();
		updateViewSpaceSize();

		Point center = Controller.getMap().getCenter();
		position.x = center.getViewSpcX();
		position.y = center.getViewSpcY();
		fullWindow = true;
		initFocus();
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
	public Camera(final GameView view, final int x, final int y, final int width, final int height) {
		zRenderingLimit = Chunk.getBlocksZ()-1;

		gameView = view;
		screenWidth = width;
		screenHeight = height;
		screenPosX = x;
		screenPosY = y;
		renderResWidth = WE.getCVars().getValueI("renderResolutionWidth");
		updateViewSpaceSize();

		Point center = Controller.getMap().getCenter();
		position.x = center.getViewSpcX();
		position.y = center.getViewSpcY();
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
	public Camera(final GameView view, final int x, final int y, final int width, final int height, final Point center) {
		gameView = view;
		screenWidth = width;
		screenHeight = height;
		screenPosX = x;
		screenPosY = y;
		renderResWidth = WE.getCVars().getValueI("renderResolutionWidth");
		updateViewSpaceSize();
		position.x = center.getViewSpcX();
		position.y = center.getViewSpcY();
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
	public Camera(final GameView view, final int x, final int y, final int width, final int height, final AbstractEntity focusentity) {
		gameView = view;
		screenWidth = width;
		screenHeight = height;
		screenPosX = x;
		screenPosY = y;
		renderResWidth = WE.getCVars().getValueI("renderResolutionWidth");
		updateViewSpaceSize();
		if (focusentity == null) {
			throw new NullPointerException("Parameter 'focusentity' is null");
		}
		WE.getConsole().add("Creating new camera which is focusing an entity: " + focusentity.getName());
		this.focusEntity = focusentity;
		if (!focusentity.hasPosition()) {
			throw new NullPointerException(focusentity.getName() + " is not spawned yet");
		}
		position.x = focusEntity.getPosition().getViewSpcX();
		position.y = (int) (focusEntity.getPosition().getViewSpcY()
						+ focusEntity.getDimensionZ() * RenderCell.ZAXISSHORTENING/2);//have middle of object in center
		initFocus();
	}

	/**
	 * Updates the camera.
	 *
	 * @param dt
	 */
	public final void update(float dt) {
		if (active) {
			if (focusEntity != null && focusEntity.hasPosition()) {
				//update camera's position according to focusEntity
				Vector2 newPos = new Vector2(
					focusEntity.getPosition().getViewSpcX(),
					(int) (focusEntity.getPosition().getViewSpcY()
						+ focusEntity.getDimensionZ() * RenderCell.ZAXISSHORTENING/2)//have middle of object in center
				);

				//only follow if outside leap radius
				if (position.dst(newPos) > WE.getCVars().getValueI("CameraLeapRadius")) {
					Vector2 diff = position.cpy().sub(newPos);
					diff.nor().scl(WE.getCVars().getValueI("CameraLeapRadius"));
					position.x = newPos.x;
					position.y = newPos.y;
					position.add(diff);
				}
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

			//move camera to the focus
			viewMat.setToLookAt(
				new Vector3(position, 0),
				new Vector3(position, -1),
				up
			);

			//orthographic camera, libgdx stuff
			projection.setToOrtho(
				-getWidthInProjSpc() / 2,
				getWidthInProjSpc() / 2,
				-getHeightInProjSpc() / 2,
				getHeightInProjSpc() / 2,
				0,
				1
			);

			//set up projection matrices
			combined.set(projection);
			Matrix4.mul(combined.val, viewMat.val);
			
			//recalculate the center position
			updateCenter();

			//don't know what this does
			//Gdx.gl20.glMatrixMode(GL20.GL_PROJECTION);
			//Gdx.gl20.glLoadMatrixf(projection.val, 0);
			//Gdx.gl20.glMatrixMode(GL20.GL_MODELVIEW);
			//Gdx.gl20.glLoadMatrixf(viewMat.val, 0);
			//invProjectionView.set(combined);
			//Matrix4.inv(invProjectionView.val);
		}
	}

	/**
	 * Check if center has to be moved and if chunks must be loaded or unloaded
	 * performs according actions.<br>
	 * 
	 */
	public void updateCenter() {
		//check for chunk movement
		int oldX = centerChunkX;
		int oldY = centerChunkY;

		//check if chunkswitch left
		if (
			getVisibleLeftBorder()
			<
			(centerChunkX-1)*Chunk.getBlocksX()
			//&& centerChunkX-1==//calculated xIndex -1
			) {
			centerChunkX--;
		}

		if (
			getVisibleRightBorder()
			>=
			(centerChunkX+2)*Chunk.getBlocksX()
			//&& centerChunkX-1==//calculated xIndex -1
			) {
			centerChunkX++;
		}

		int dxMovement = getCenter().getChunkX()-oldX;
		if (dxMovement*dxMovement > 1){
			//above relative move does not work. use absolute position of center
			centerChunkX = getCenter().getChunkX();
		}
		
		//the following commented lines were working once and is still a preferable way to do this algo because it avoid spots wher small movements causes ofen recalcucating of HSD. At the moment is absolute calculated. The commented code is relative based.
		
//		if (
//		getVisibleBackBorder()
//		<
//		chunkMap.getChunkContaining(centerChunkX, centerChunkY-1).getTopLeftCoordinate().getX()
//		//&& centerChunkX-1==//calculated xIndex -1
//		) {
//		centerChunkY--;
//		}
//		//check in viewMat space
//		if (
//		position.y- getHeightInProjSpc()/2
//		<
//		map.getBlocksZ()*RenderCell.VIEW_HEIGHT
//		-RenderCell.VIEW_DEPTH2*(
//		chunkMap.getChunkContaining(centerChunkX, centerChunkY+1).getTopLeftCoordinate().getY()+Chunk.getBlocksY()//bottom coordinate
//		)
//		//&& centerChunkX-1==//calculated xIndex -1
//		) {
//		centerChunkY++;
//		}

		//this line is needed because the above does not work, calcualtes absolute position
		centerChunkY = (int) Math.floor(-position.y / Chunk.getViewDepth());

		checkNeededChunks();
	}

	/**
	 * checks which chunks must be loaded around the center
	 */
	private void checkNeededChunks() {
		//check every chunk
		if (centerChunkX == 0 && centerChunkY == 0 || WE.getCVars().getValueB("mapChunkSwitch")) {
			for (int x = -loadingRadius; x <= loadingRadius; x++) {
				int lRad = loadingRadius/2;
				if (lRad <= 2) {
					lRad = 2;
				}
				for (int y = -lRad; y <= lRad; y++) {
					checkChunk(centerChunkX + x, centerChunkY + y);
				}
			}
			//after the first time reduce
			if (loadingRadius > 2) {
				loadingRadius = 2;
			}
		}
	}

	/**
	 * Checks if chunk must be loaded or deleted.
	 *
	 * @param x
	 * @param y
	 */
	private void checkChunk(int x, int y) {
		Map chunkMap = Controller.getMap();
		if (chunkMap.getChunk(x, y) == null) {
			chunkMap.loadChunk(x, y);//load missing chunks
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

			view.getSpriteBatch().setProjectionMatrix(combined);
			view.getShapeRenderer().setProjectionMatrix(combined);
			//set up the viewport, yIndex-up
			HdpiUtils.glViewport(
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
			view.getSpriteBatch().begin();
			//send a Vector4f to GLSL
			if (WE.getCVars().getValueB("enablelightengine")) {
				view.getShader().setUniformf(
					"sunNormal",
					Controller.getLightEngine().getSun(getCenter()).getNormal()
				);
				view.getShader().setUniformf(
					"sunColor",
					Controller.getLightEngine().getSun(getCenter()).getLight()
				);
				
				if (Controller.getLightEngine().getMoon(getCenter()) == null) {
					view.getShader().setUniformf(
						"moonNormal",
						new Vector3()
					);
					view.getShader().setUniformf(
						"moonColor",
						new Color()
					);
					view.getShader().setUniformf(
						"ambientColor",
						new Color()
					);
				} else {
					view.getShader().setUniformf(
						"moonNormal",
						Controller.getLightEngine().getMoon(getCenter()).getNormal()
					);
					view.getShader().setUniformf(
						"moonColor",
						Controller.getLightEngine().getMoon(getCenter()).getLight()
					);
					view.getShader().setUniformf(
						"ambientColor",
						Controller.getLightEngine().getAmbient(getCenter())
					);
				}
			}

			//bind normal map to texture unit 1
			if (WE.getCVars().getValueB("LEnormalMapRendering")) {
				AbstractGameObject.getTextureNormal().bind(1);
			}

				//bind diffuse color to texture unit 0
			//important that we specify 0 otherwise we'll still be bound to glActiveTexture(GL_TEXTURE1)
			AbstractGameObject.getTextureDiffuse().bind(0);

			//settings for this frame
			RenderCell.setStaticShade(WE.getCVars().getValueB("enableAutoShade"));
			SideSprite.setAO(WE.getCVars().getValueF("ambientOcclusion"));
			
			//render vom bottom to top
			for (Renderable obj : depthlist) {
				obj.render(view, camera);
			}
			view.getSpriteBatch().end();

			//if debugging render outline again
			if (WE.getCVars().getValueB("DevDebugRendering")) {
				view.setDebugRendering(true);
				view.getSpriteBatch().begin();
				//render vom bottom to top
				for (Renderable obj : depthlist) {
					obj.render(view, camera);
				}
				view.getSpriteBatch().end();
			}

			//outline 3x3 chunks
			if (WE.getCVars().getValueB("DevDebugRendering")) {
				drawDebug(view, camera);
			}
			if (damageoverlay > 0.0f) {
				//WE.getEngineView().getSpriteBatch().setShader(new custom shader);
				WE.getEngineView().getSpriteBatch().begin();
				Texture texture = WE.getAsset("com/bombinggames/wurfelengine/core/images/bloodblur.png");
				Sprite overlay = new Sprite(texture);
				overlay.setOrigin(0, 0);
				//somehow reverse the viewport transformation, needed for split-screen
				overlay.setSize(
					getWidthInScreenSpc(),
					getHeightInScreenSpc() * (float) Gdx.graphics.getHeight() / getHeightInScreenSpc()
				);
				overlay.setColor(1, 0, 0, damageoverlay);
				overlay.draw(WE.getEngineView().getSpriteBatch());
				WE.getEngineView().getSpriteBatch().end();
			}
		}
	}

	/**
	 * Fills the cameracontent plus entities into a list and sorts it in the order of the rendering,
	 * called the "depthlist". This is done every frame.
	 *
	 * @return the depthlist
	 */
	private void createDepthList() {
		depthlist.clear();
		maxsprites = WE.getCVars().getValueI("MaxSprites");

		//inverse dirty flag
		AbstractGameObject.inverseMarkedFlag(id);
		
		//add entitys which should be rendered
		ArrayList<AbstractEntity> ents = Controller.getMap().getEntities();
				
		//add entities to renderstorage
		ArrayList<RenderCell> modifiedCells = this.modifiedCells;
		modifiedCells.clear();
		modifiedCells.ensureCapacity(ents.size());
		LinkedList<AbstractEntity> renderAppendix = this.renderAppendix;
		renderAppendix.clear();
		
		for (AbstractEntity ent : ents) {
			if (ent.hasPosition()
				&& !ent.isHidden()
				&& inViewFrustum(ent.getPosition().getViewSpcX(),
					ent.getPosition().getViewSpcY()
				)
				&& ent.getPosition().getZ() < zRenderingLimit
			) {
				RenderCell cell = gameView.getRenderStorage().getCell(ent.getPosition().add(0, 0, RenderCell.GAME_EDGELENGTH));//add in cell above
				ent.getPosition().add(0, 0, -RenderCell.GAME_EDGELENGTH);//reverse change in line above
				if (cell != null) {
					cell.addCoveredEnts(ent);
					modifiedCells.add(cell);
				} else {
					//add at end of renderList
					renderAppendix.add(ent);
				}
			}
		}
		
		//iterate over renderstorage
		objectsToBeRendered = 0;
		//clear/reset flags
		CameraSpaceIterator iterator = new CameraSpaceIterator(
			gameView.getRenderStorage(),
			centerChunkX,
			centerChunkY,
			0,
			Chunk.getBlocksZ() - 1
		);
		//check/visit every visible cell
		while (iterator.hasNext()) {
			RenderCell cell = iterator.next();

			if (cell != null) {
				if (inViewFrustum(
					cell.getPosition().getViewSpcX(),
					cell.getPosition().getViewSpcY()
				)) {
					visit(cell);
				}
			}
		}
		for (RenderCell modifiedCell : modifiedCells) {
			modifiedCell.clearCoveredEnts();
		}
		depthlist.addAll(renderAppendix);//render every entity which has not parent block at the end
	}
	
	/**
	 * topological sort
	 * @param n root node
	 */
	private void visit(AbstractGameObject n) {
		if (!n.isMarkedDS(id)) {
			LinkedList<AbstractGameObject> covered = n.getCovered(gameView.getRenderStorage());
			if (covered.size() > 0) {
				n.markPermanentDS(id);
					for (AbstractGameObject m : covered) {
						if (inViewFrustum(
							m.getPosition().getViewSpcX(),
							m.getPosition().getViewSpcY()
						)) {
							visit(m);
						}
					}
			}
			if (n.shouldBeRendered(this)) {
				if (objectsToBeRendered < maxsprites) {
					//fill only up to available size
					depthlist.add(n);
					objectsToBeRendered++;
				}
			}
		}
	}

	/**
	 * checks if the projected position is inside the viewMat Frustum
	 *
	 * @param proX projective space
	 * @param proY projective space
	 * @return
	 */
	public boolean inViewFrustum(int proX, int proY){
		return
				(position.y + getHeightInProjSpc() / 2)
				>
				(proY - RenderCell.VIEW_HEIGHT * 2)//bottom of sprite
			&&
				(proY + RenderCell.VIEW_HEIGHT2 + RenderCell.VIEW_DEPTH)//top of sprite
				>
				position.y - getHeightInProjSpc() / 2
			&&
				(proX + RenderCell.VIEW_WIDTH2)//right side of sprite
				>
				position.x - getWidthInProjSpc() / 2
			&&
				(proX - RenderCell.VIEW_WIDTH2)//left side of sprite
				<
				position.x + getWidthInProjSpc() / 2
		;
	}

	/**
	 * Set the zoom factor.
	 *
	 * @param zoom 1 is default
	 */
	public void setZoom(float zoom) {
		this.zoom = zoom;
		updateViewSpaceSize();//todo check for redundant call?
	}

	/**
	 * the width of the internal render resolution
	 *
	 * @param resolution
	 */
	public void setInternalRenderResolution(int resolution) {
		renderResWidth = resolution;
		updateViewSpaceSize();
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
		return screenWidth / (float) renderResWidth;
	}

	/**
	 * If the limit is set to the map's height or more it becomes deactivated.
	 *
	 * @param limit minimum is 0, everything to this limit becomes rendered
	 */
	public void setZRenderingLimit(float limit) {
		if (limit != zRenderingLimit) {//only if it differs

			zRenderingLimit = limit;

			//clamp
			if (limit < 0) {
				zRenderingLimit = 0;//min is 0
			}
		}
	}
	
	/**
	 * Returns the left border of the actual visible area.
	 *
	 * @return left x position in view space
	 */
	public float getVisibleLeftBorderVS() {
		return (position.x - getWidthInProjSpc() / 2)- RenderCell.VIEW_WIDTH2;
	}

	/**
	 * Returns the left border of the actual visible area.
	 *
	 * @return the left (X) border coordinate
	 */
	public int getVisibleLeftBorder() {
		return (int) ((position.x - getWidthInProjSpc() / 2) / RenderCell.VIEW_WIDTH - 1);
	}

	/**
	 * Returns the right seight border of the camera covered area currently
	 * visible.
	 *
	 * @return measured in grid-coordinates
	 */
	public int getVisibleRightBorder() {
		return (int) ((position.x + getWidthInProjSpc() / 2) / RenderCell.VIEW_WIDTH + 1);
	}
	
	/**
	 * Returns the right seight border of the camera covered area currently
	 * visible.
	 *
	 * @return measured in grid-coordinates
	 */
	public float getVisibleRightBorderVS() {
		return position.x + getWidthInProjSpc() / 2 + RenderCell.VIEW_WIDTH2;
	}

	/**
	 * Returns the top seight border of the camera covered groundBlock
	 *
	 * @return measured in grid-coordinates
	 */
	public int getVisibleBackBorder() {
		//TODO verify
		return (int) ((position.y + getHeightInProjSpc() / 2)//camera top border
			/ -RenderCell.VIEW_DEPTH2//back to game space
		);
	}

	/**
	 * Returns the bottom seight border y-coordinate of the lowest cell
	 *
	 * @return measured in grid-coordinates
	 * @see #getVisibleFrontBorderHigh()
	 */
	public int getVisibleFrontBorderLow() {
		return (int) (
			(position.y- getHeightInProjSpc()/2) //bottom camera border
			/ -RenderCell.VIEW_DEPTH2 //back to game coordinates
		);
	}

	/**
	 * Returns the bottom seight border y-coordinate of the highest cell
	 *
	 * @return measured in grid-coordinates
	 * @see #getVisibleFrontBorderLow()
	 */
	public int getVisibleFrontBorderHigh() {
		return (int) ((position.y - getHeightInProjSpc() / 2) //bottom camera border
			/ -RenderCell.VIEW_DEPTH2 //back to game coordinates
			+ Chunk.getBlocksY()*3 * RenderCell.VIEW_HEIGHT / RenderCell.VIEW_DEPTH2 //todo verify, try to add z component
			);
	}

	/**
	 * The Camera Position in the game world.
	 *
	 * @return game in pixels
	 */
	public float getViewSpaceX() {
		return getCenter().getViewSpcX();
	}

	/**
	 * The Camera's center position in the game world. viewMat space. yIndex up
	 *
	 * @return in camera position game space
	 */
	public float getViewSpaceY() {
		return getCenter().getViewSpcY();
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
		viewSpaceWidth = renderResWidth;
		viewSpaceHeight = (int) (screenHeight / getScreenSpaceScaling());
	}

	/**
	 * The amount of game world pixels which are visible in X direction after
	 * the zoom has been applied. For screen pixels use
	 * {@link #getWidthInScreenSpc()}.
	 *
	 * @return in viewMat pixels
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
		this.screenWidth = Gdx.graphics.getWidth();
		this.screenHeight = Gdx.graphics.getHeight();
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
		if (width < Gdx.graphics.getWidth() || height < Gdx.graphics.getHeight()) {
			fullWindow = false;
		}
		this.screenWidth = width;
		this.screenHeight = height;
		updateViewSpaceSize();
	}

	/**
	 * Move xIndex and yIndex coordinate
	 *
	 * @param x in game space
	 * @param y in game space
	 */
	public void move(int x, int y) {
		if (focusEntity != null) {
			focusEntity.getPosition().add(x, y, 0);
		} else {
			position.x += x;
			position.y -= y/2;
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

	/**
	 * Returns the focuspoint
	 *
	 * @return in game space, copy safe
	 */
	public Point getCenter() {
		return (Point) center.set(
			position.x,
			-position.y * 2,
			0
		);//view to game
	}
	
	/**
	 * Set the cameras center to a point. If the camera is locked to a an entity this lock will be removed.
	 * @param point game space. z gets ignored
	 */
	public void setCenter(Point point){
		focusEntity = null;
		position.x = point.getViewSpcX();
		position.y = point.getViewSpcY();//game to view space transformation
	}

	/**
	 *
	 * @param focusEntity
	 */
	public void setFocusEntity(AbstractEntity focusEntity) {
		if (this.focusEntity != focusEntity) {
			this.focusEntity = focusEntity;
			position.set(focusEntity.getPosition().getViewSpcX(),
				(int) (focusEntity.getPosition().getViewSpcY()
					+ focusEntity.getDimensionZ() * RenderCell.ZAXISSHORTENING/2)//have middle of object in center
			);
		}
	}
	
	/**
	 * enable or disable the camera
	 *
	 * @param active
	 */
	public void setActive(boolean active) {
		//turning on
		if (!this.active && active) {
			if (WE.getCVars().getValueB("mapUseChunks")) {
				checkNeededChunks();
			}
		}

		this.active = active;
	}

	private void drawDebug(GameView view, Camera camera) {
		ShapeRenderer sh = view.getShapeRenderer();
		sh.setColor(Color.RED.cpy());
		sh.begin(ShapeRenderer.ShapeType.Line);
		sh.rect(-Chunk.getGameWidth(),//one chunk to the left
			-Chunk.getGameDepth(),//two chunks down
			Chunk.getGameWidth()*3,
			Chunk.getGameDepth()*3 / 2
		);
		sh.line(-Chunk.getGameWidth(),
			-Chunk.getGameDepth() / 2,
			-Chunk.getGameWidth() + Chunk.getGameWidth()*3,
			-Chunk.getGameDepth() / 2
		);
		view.getShapeRenderer().line(-Chunk.getGameWidth(),
			0,
			-Chunk.getGameWidth() + Chunk.getGameWidth()*3,
			0
		);
		sh.line(
			0,
			Chunk.getGameDepth() / 2,
			0,
			-Chunk.getGameDepth()
		);
		sh.line(
			Chunk.getGameWidth(),
			Chunk.getGameDepth() / 2,
			Chunk.getGameWidth(),
			-Chunk.getGameDepth()
		);
		sh.end();
	}

	public int getCenterChunkX() {
		return centerChunkX;
	}

	public int getCenterChunkY() {
		return centerChunkY;
	}

	public boolean isEnabled() {
		return active;
	}

	public void setId(int id){
		this.id = id;
	}
	
	
	void dispose() {
	}

}
