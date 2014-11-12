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
import com.BombingGames.WurfelEngine.Core.Gameobjects.Sides;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Chunk;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;
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

/**
 * Creates a virtual camera wich displays the game world on the viewport.
 *
 * @author Benedikt Vogler
 */
public class Camera {

	/**
	 * The deepest layer is an array which stores the information if there
	 * should be a tile rendered
	 */
	private int zRenderingLimit;
	
	/**
	 * [x][y][z][normal]. to fit z starting at -1, the z axis is shifted by one.
	 */
	private boolean[][][][] clipping = new boolean[Map.getBlocksX()][Map.getBlocksY()][Map.getBlocksZ()+1][3];

	/**
	 * the position of the camera projected. Y-up*
	 */
	private final Vector3 position = new Vector3();
	/**
	 * the unit length direction vector of the camera *
	 */
	private final Vector3 direction = new Vector3(0, 0, -1);
	/**
	 * the unit length up vector of the camera *
	 */
	private final Vector3 up = new Vector3(0, 1, 0);

	/**
	 * the projection matrix *
	 */
	private final Matrix4 projection = new Matrix4();
	/**
	 * the view matrix *
	 */
	private final Matrix4 view = new Matrix4();
	/**
	 * the combined projection and view matrix *
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
	/**
	 * a chunk with an "anchor point"
	 */
	private int fixChunkX;
	private int fixChunkY;

	private boolean fullWindow = false;

	/**
	 * the opacity of thedamage overlay
	 */
	private float damageoverlay = 0f;
	
	private Vector2 screenshake = new Vector2(0, 0);
	private float shakeAmplitude;
	private float shakeTime;
	
	private final GameView gameView;
	private final Controller gameController;
	
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
	 * @param controller
	 */
	public Camera(final int x, final int y, final int width, final int height, GameView view, Controller controller) {
		gameView = view;
		gameController = controller;
		screenWidth = width;
		screenHeight = height;
		screenPosX = x;
		screenPosY = y;

		fixChunkX = Controller.getMap().getChunkCoords(0)[0];
		fixChunkY = Controller.getMap().getChunkCoords(0)[1];

		//set the camera's focus to the center of the map
		position.x = Map.getCenter().getProjectedPosX(view) - getProjectionWidth() / 2;
		position.y = Map.getCenter().getProjectedPosY(view) - getProjectionHeight() / 2;
		position.z = 0;

		zRenderingLimit = Map.getBlocksZ();
	}
	
	/**
	 * Creates a fullscale camera pointing at the middle of the map.
	 * @param view
	 * @param controller
	 */
	public Camera(GameView view, Controller controller) {
		this(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), view, controller);
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
	 * @param controller
	 */
	public Camera(final Coordinate focus, final int x, final int y, final int width, final int height, GameView view, Controller controller) {
		this(x, y, width, height, view, controller);
		WE.getConsole().add("Creating new camera which is focusing a coordinate");
		this.focusCoordinates = focus;
		this.focusEntity = null;
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
	 * @param controller
	 */
	public Camera(final AbstractEntity focusentity, final int x, final int y, final int width, final int height, GameView view, Controller controller) {
		this(x, y, width, height, view, controller);
		if (focusentity == null) {
			throw new NullPointerException("Parameter 'focusentity' is null");
		}
		WE.getConsole().add("Creating new camera which is focusing an entity: " + focusentity.getName());
		this.focusEntity = focusentity;
		this.focusCoordinates = null;
	}

	/**
	 * Updates the camera.
	 *
	 * @param dt
	 */
	public void update(float dt) {
		//refrehs the camera's position in the game world
		if (focusCoordinates != null) {
			//update camera's position according to focusCoordinates
			position.x = focusCoordinates.getProjectedPosX(gameView) - getProjectionWidth() / 2;
			position.y = focusCoordinates.getProjectedPosY(gameView) - getProjectionHeight() / 2;

		} else if (focusEntity != null) {
			//update camera's position according to focusEntity
            position.x = focusEntity.getPosition().getProjectedPosX(gameView) - getProjectionWidth() / 2;            
            position.y = (int) (
                focusEntity.getPosition().getProjectedPosY(gameView)
                - getProjectionHeight()/2
                +focusEntity.getDimensionZ()*AbstractPosition.SQRT12/2
            );

		} else {
			//update camera's position according to fixChunk
			int[] currentTopLeftChunk = Controller.getMap().getChunkCoords(0);
			position.x += (fixChunkX - currentTopLeftChunk[0]) * Chunk.getGameWidth();
			position.y -= (fixChunkY - currentTopLeftChunk[1]) * Chunk.getGameDepth() / 2;

			//update fixChunk
			fixChunkX = currentTopLeftChunk[0];
			fixChunkY = currentTopLeftChunk[1];
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

		Vector3 tmp = position.cpy().add(getProjectionWidth() / 2, getProjectionHeight() / 2, 0);
		view.setToLookAt(tmp, tmp.cpy().add(direction), up);//move camera to the focus 

		//orthographic camera, libgdx stuff
		projection.setToOrtho(
			(-screenWidth / 2) / getScaling(),
			(screenWidth / 2) / getScaling(),
			(-screenHeight / 2) / getScaling(),
			(screenHeight / 2) / getScaling(),
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
			//set up the viewport
			Gdx.gl.glViewport(
				screenPosX,
				Gdx.graphics.getHeight() - screenHeight - screenPosY,
				screenWidth,
				screenHeight
			);

			//render map
			ArrayList<RenderDataDTO> depthlist = createDepthList();

			view.getBatch().begin();
				//view.setDrawmode(GL10.GL_MODULATE);
				Gdx.gl20.glEnable(GL_BLEND); // Enable the OpenGL Blending functionality  
				//Gdx.gl20.glBlendFunc(GL_SRC_ALPHA, GL20.GL_CONSTANT_COLOR); 

				//render vom bottom to top
				for (RenderDataDTO renderobject : depthlist) {
					renderobject.getGameObject().render(view, camera, renderobject.getPosition());
				}
			view.getBatch().end();

			//outline map
			if (CVar.get("debugObjects").getValueb()) {
				view.getShapeRenderer().setColor(Color.RED.cpy());
				view.getShapeRenderer().begin(ShapeRenderer.ShapeType.Line);
				view.getShapeRenderer().rect(0, 0, Map.getGameWidth(), Map.getGameDepth() / 2);
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
	private ArrayList<RenderDataDTO> createDepthList() {
		ArrayList<RenderDataDTO> depthsort = new ArrayList<>(100);//start by size 100

		for (int x = 0, right = Map.getBlocksX(); x < right; x++) {//only objects in view frustum
			for (int y = 0, front = Map.getBlocksY(); y < front; y++) {
				for (int z = -1; z < zRenderingLimit; z++) {//add vertical until renderlimit

					Coordinate coord = new Coordinate(x, y, z, true);
					Block blockAtCoord = coord.getBlock();
                    if (!blockAtCoord.isHidden()//render if not hidden
						&& !isCompletelyClipped(coord) //nor completely clipped
                        &&                          //inside view frustum?
						(position.y + getProjectionHeight())//camera's top
                            >
                            (coord.getProjectedPosY(gameView)- Block.SCREEN_HEIGHT*2)//bottom of sprite, don't know why -Block.SCREEN_HEIGHT2 is not enough
                        &&                                  //inside view frustum?
                            (coord.getProjectedPosY(gameView)+ Block.SCREEN_HEIGHT2+Block.SCREEN_DEPTH)//top of sprite
                            >
                            position.y//camera's bottom
						&&
							(coord.getProjectedPosX(gameView)+ Block.SCREEN_WIDTH2)//right side of sprite
							>
							position.x
						&&
							(coord.getProjectedPosX(gameView)- Block.SCREEN_WIDTH2)//left side of sprite
							<
							position.x + getProjectionWidth()
					) {
						depthsort.add(new RenderDataDTO(blockAtCoord, coord));
					}
				}
			}
		}

		//add entitys
		for (AbstractEntity entity : Controller.getMap().getEntitys()) {
			int proX = entity.getPosition().getProjectedPosX(gameView);
			int proY = entity.getPosition().getProjectedPosY(gameView);
            if (! entity.isHidden()
                &&
					(position.y + getProjectionHeight())
					>
					(proY- Block.SCREEN_HEIGHT*2)//bottom of sprite
				&&
					(proY+ Block.SCREEN_HEIGHT2+Block.SCREEN_DEPTH)//top of sprite
					>
					position.y
				&&
					(proX+ Block.SCREEN_WIDTH2)//right side of sprite
					>
					position.x
				&&
					(proX- Block.SCREEN_WIDTH2)//left side of sprite
					<
					position.x + getProjectionWidth()
                && entity.getPosition().getZ() < zRenderingLimit
                )
				depthsort.add(
					new RenderDataDTO(entity, entity.getPosition())
				);
			}
		//sort the list
		if (depthsort.size() > 0) {
			return sortDepthList(depthsort);
		}
		return depthsort;
	}

	/**
	 * Using Quicksort to sort. From small to big values.
	 *
	 * @param depthsort the unsorted list
	 * @param low the lower border
	 * @param high the higher border
	 */
	private ArrayList<RenderDataDTO> sortDepthList(ArrayList<RenderDataDTO> depthsort, int low, int high) {
		int left = low;
		int right = high;
		int middle = depthsort.get((low + high) / 2).getDepth(gameView);

        while (left <= right){    
            while(depthsort.get(left).getDepth(gameView) < middle) left++; 
            while(depthsort.get(right).getDepth(gameView) > middle) right--;

			if (left <= right) {
				RenderDataDTO tmp = depthsort.set(left, depthsort.get(right));
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
	private ArrayList<RenderDataDTO> sortDepthList(ArrayList<RenderDataDTO> depthsort) {
		int i, j;
		RenderDataDTO newValue;
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
	 * Filters every Block (and side) wich is not visible. Boosts rendering
	 * speed.
	 */
	public void rayCastingClipping() {
		System.out.println("Doing rayCastingClipping.");
		if (zRenderingLimit > 0) {
			//prepare clipping
			for (int x = 0, maxX =Map.getBlocksX(); x < maxX; x++) {
				for (int y = 0, maxY =Map.getBlocksY(); y < maxY; y++) {
					//ground layer
					clipping[x][y][0][0] = true;//clip left side
					clipping[x][y][0][1] = false;//render only top
					clipping[x][y][0][2] = true;//clip right side
					
					//clip by default
					for (int z = 0; z < zRenderingLimit; z++) {
						setClipped(
							x,
							y,
							z,
							true
						);
					}
				}
			}

			//send the rays through top of the map
			for (int x = 0, maxX =Map.getBlocksX(); x < maxX; x++) {
				for (int y = 0, maxY =Map.getBlocksY(); y < maxY + zRenderingLimit * 2; y++) {
					castRay(x, y, Sides.LEFT);
					castRay(x, y, Sides.TOP);
					castRay(x, y, Sides.RIGHT);
				}
			}
		}
	}

	/**
	 * Traces a single ray. This costs less performance than a whole
	 * rayCastingClipping.
	 *
	 * @param x The starting x-coordinate on top of the map.
	 * @param y The starting y-coordinate on top of the map.
	 * @param side The side the ray should check
	 */
	private void castRay(int x, int y, Sides side) {
		int z = zRenderingLimit - 1;//start always from top

		boolean left = true;
		boolean right = true;
		/**
		 * true if entered a liquid
		 */
		boolean liquidfilter = false;
		boolean leftliquid = false;
		boolean rightliquid = false;

		//bring ray to start position
		if (y > Map.getBlocksY() - 1) {
			z -= (y - Map.getBlocksY()) / 2;
			if (y % 2 == 0) {
				y = Map.getBlocksY() - 1;
			} else {
				y = Map.getBlocksY() - 2;
			}
		}

		y += 2;
		z++;
		Coordinate currentCor;
		do {
			y -= 2;
			z--;

			currentCor = new Coordinate(x, y, z, true);
			if (side == Sides.LEFT) {
				//direct neighbour groundBlock on left hiding the complete left side
				if (Controller.getMap().getBlock(x, y, z).hasSides()//block on top
					&& x > 0 && y < Map.getBlocksY() - 1
					&& currentCor.hidingPastBlock((y % 2 == 0 ? -1 : 0), 1, 0)) {
					break; //stop ray
				}
				//liquid
				if (Controller.getMap().getBlock(x, y, z).isLiquid()) {
					if (x > 0 && y + 1 < Map.getBlocksY()
						&& Controller.getMap().getBlock(x - (y % 2 == 0 ? 1 : 0), y + 1, z).isLiquid()
					) {
						liquidfilter = true;
					}

					if (x > 0 && y < Map.getBlocksY() - 1 && z < zRenderingLimit - 1
						&& Controller.getMap().getBlock(x - (y % 2 == 0 ? 1 : 0), y + 1, z + 1).isLiquid()
					) {
						leftliquid = true;
					}

					if (y < Map.getBlocksY() - 2
						&& Controller.getMap().getBlock(x, y + 2, z).isLiquid()
					) {
						rightliquid = true;
					}

					if (leftliquid && rightliquid) {
						liquidfilter = true;
					}
				}

				//two blocks hiding the left side
				if (x > 0 && y < Map.getBlocksY() - 1 && z < zRenderingLimit - 1
					&& currentCor.hidingPastBlock((y % 2 == 0 ? -1 : 0), 1, 1)
				) {
					left = false;
				}
				if (y < Map.getBlocksY() - 2
					&& currentCor.hidingPastBlock(0, 2, 0)
				) {
					right = false;
				}

			} else if (side == Sides.TOP) {//check top side
				if (Controller.getMap().getBlock(x, y, z).hasSides()//block on top
					&& z + 1 < zRenderingLimit
					&& currentCor.hidingPastBlock(0, 0, 1)) {
					break;
				}

				//liquid
				if (Controller.getMap().getBlock(x, y, z).isLiquid()) {
					if (z < zRenderingLimit - 1 && Controller.getMap().getBlock(x, y, z + 1).isLiquid()) {
						liquidfilter = true;
					}

					if (x > 0 && y < Map.getBlocksY() - 1 && z < zRenderingLimit - 1
						&& Controller.getMap().getBlock(x - (y % 2 == 0 ? 1 : 0), y + 1, z + 1).isLiquid()
					) {
						leftliquid = true;
					}

					if (x < Map.getBlocksX() - 1 && y < Map.getBlocksY() - 1 && z < zRenderingLimit - 1
						&& Controller.getMap().getBlock(x + (y % 2 == 0 ? 0 : 1), y + 1, z + 1).isLiquid()
					) {
						rightliquid = true;
					}

					if (leftliquid && rightliquid) {
						liquidfilter = true;
					}
				}

				//two 0- and 2-sides hiding the side 1
				if (x > 0 && y < Map.getBlocksY() - 1 && z < zRenderingLimit - 1
					&& currentCor.hidingPastBlock((y % 2 == 0 ? -1 : 0), 1, 1)
				) {
					left = false;
				}

				if (x < Map.getBlocksX() - 1 && y < Map.getBlocksY() - 1 && z < zRenderingLimit - 1
					&& currentCor.hidingPastBlock((y % 2 == 0 ? 0 : 1), 1, 1)
				) {
					right = false;
				}

			} else if (side == Sides.RIGHT) {
				//block on right hiding the whole right side
				if (Controller.getMap().getBlock(x, y, z).hasSides()//block on top
					&& x + 1 < Map.getBlocksX() && y + 1 < Map.getBlocksY()
					&& currentCor.hidingPastBlock((y % 2 == 0 ? 0 : 1), 1, 0)
				) {
					break;
				}

				//liquid
				if (Controller.getMap().getBlock(x, y, z).isLiquid()) {
					if (x < Map.getBlocksX() - 1 && y < Map.getBlocksY() - 1
						&& Controller.getMap().getBlock(x + (y % 2 == 0 ? 0 : 1), y + 1, z).isLiquid()
					) {
						liquidfilter = true;
					}

					if (y + 2 < Map.getBlocksY()
						&& Controller.getMap().getBlock(x, y + 2, z).isLiquid()
					) {
						leftliquid = true;
					}

					if (x + 1 < Map.getBlocksX() && y + 1 < Map.getBlocksY() && z + 1 < zRenderingLimit
						&& Controller.getMap().getBlock(x + (y % 2 == 0 ? 0 : 1), y + 1, z + 1).isLiquid()
					) {
						rightliquid = true;
					}

					if (leftliquid && rightliquid) {
						liquidfilter = true;
					}
				}

				//two blocks hiding the right side
				if (y + 2 < Map.getBlocksY()
					&& currentCor.hidingPastBlock(0, 2, 0)
				) {
					left = false;
				}

				if (x + 1 < Map.getBlocksX() && y + 1 < Map.getBlocksY() && z + 1 < zRenderingLimit
					&& currentCor.hidingPastBlock((y % 2 == 0 ? 0 : 1), 1, 1)
				) {
					right = false;
				}
			}

			if ((left || right) && !(liquidfilter && Controller.getMap().getBlock(x, y, z).isLiquid())) { //unless both sides are clipped don't clip the whole block
				liquidfilter = false;
				clipping[x][y][z+1][side.getCode()] = false;
			}
		} while (y > 1 && z > -1 //not on back or bottom of map
			&& (left || right) //left or right still visible
			&& (!currentCor.hidingPastBlock(0, 0, 0))
		);

        clipping[x][y][0][1] = !((z <= -1) && (left || right)); //hit ground level and left or right still visible
	}
	
	/**
	 * Traces the ray to a specific groundBlock. This is like the
	 * rayCastingClipping but only a single ray.
	 *
	 * @param coord The coordinate where the ray should point to.
	 * @param neighbours True when neighbours groundBlock also should be scanned
	 */
	public void traceRayTo(Coordinate coord, boolean neighbours) {
		int[] coords = coord.getRel();

		//default  clipped
		setClipped(
			coord.getRelX(),
			coord.getRelY(),
			coord.getZ(),
			true
		);

		//find start position
		while (coords[2] < Map.getBlocksZ() - 1) {
			coords[1] += 2;
			coords[2]++;
		}

		//trace rays
		if (neighbours) {
			castRay(coords[0] - (coords[1] % 2 == 0 ? 1 : 0), coords[1] - 1, Sides.RIGHT);
			castRay(coords[0] + (coords[1] % 2 == 0 ? 0 : 1), coords[1] - 1, Sides.LEFT);
			castRay(coords[0], coords[1] + 2, Sides.TOP);
		}
		castRay(coords[0], coords[1], Sides.LEFT);
		castRay(coords[0], coords[1], Sides.TOP);
		castRay(coords[0], coords[1], Sides.RIGHT);
	}

	/**
	 * Set the zoom factor and regenerates the sprites.
	 *
	 * @param zoom
	 */
	public void setZoom(float zoom) {
		this.zoom = zoom;
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
	 * Returns the zoom multiplied by a scaling factor to achieve the same
	 * viewport with every resolution
	 *
	 * @return a scaling factor applied on the screen
	 */
	public float getScaling() {
		return zoom * screenWidth / CVar.get("renderResolutionWidth").getValuei();
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
			
			rayCastingClipping();
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
		int left = (int) (position.x / AbstractGameObject.SCREEN_WIDTH);
		if (left < 0) {
			return 0;//clamp
		}
		return left;
	}

	/**
	 * Returns the right border of the visible area.
	 *
	 * @return measured in grid-coordinates
	 */
	public int getVisibleRightBorder() {
		int right = (int) ((position.x + getProjectionWidth()) / AbstractGameObject.SCREEN_WIDTH + 1);
		if (right >= Map.getBlocksX()) {
			return Map.getBlocksX() - 1;//clamp
		}
		return right;
	}

	/**
	 * Returns the top seight border of the deepest groundBlock
	 *
	 * @return measured in grid-coordinates
	 */
	public int getVisibleBackBorder() {
		int back = (int) (Map.getBlocksY() - 1 - ((position.y + getProjectionHeight()) / AbstractGameObject.SCREEN_DEPTH2) - 3);
		if (back < 0) {
			return 0;//clamp
		}
		return back;
	}

	/**
	 * Returns the bottom seight border y-coordinate of the highest groundBlock
	 *
	 * @return measured in grid-coordinates, relative to map
	 */
	public int getVisibleFrontBorder() {
		int front = (int) (Map.getBlocksY() - 1 - ((position.y) / AbstractGameObject.SCREEN_DEPTH2 - Map.getBlocksZ() * 2) + 2);
		if (front >= Map.getBlocksY()) {
			return Map.getBlocksY() - 1;//clamp
		}
		return front;
	}

	/**
	 * The Camera Position in the game world.
	 *
	 * @return game in pixels
	 */
	public float getProjectionPosX() {
		return position.x;
	}

	/**
	 * The Camera left Position in the game world.
	 *
	 * @param x game in pixels
	 */
	public void setProjectionPosX(float x) {
		position.x = x;
	}

	/**
	 * The Camera top-position in the game world.
	 *
	 * @return in camera position game space
	 */
	public float getProjectionPosY() {
		return position.y;
	}

	/**
	 * The Camera top-position in the game world.
	 *
	 * @param y in game space
	 */
	public void setProjectionPosY(float y) {
		position.y = y;
	}

	/**
	 * The amount of pixel which are visible in Y direction (projection
	 * dimension). It should be equal
	 * {@link com.BombingGames.WurfelEngine.Core.Configuration#getRenderResolutionWidth()}.
	 * For screen pixels use {@link #getScreenWidth()}.
	 *
	 * @return in pixels
	 */
	public final int getProjectionWidth() {
		return (int) (screenWidth / getScaling());
	}

	/**
	 * The amount of pixel which are visible in Y direction (projection
	 * dimension, game dimension). For screen pixels use {@link #getScreenHeight()
	 * }.
	 *
	 * @return in pixels
	 */
	public final int getProjectionHeight() {
		return (int) (screenHeight / getScaling());
	}

	/**
	 * Returns the position of the cameras output (on the screen)
	 *
	 * @return in pixels
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
	 * Returns the height of the camera output before scaling. To get the real
	 * display size multiply it with scale values.
	 *
	 * @return the value before scaling
	 */
	public int getScreenHeight() {
		return screenHeight;
	}

	/**
	 * Returns the width of the camera output before scaling. To get the real
	 * display size multiply it with scale value.
	 *
	 * @return the value before scaling
	 */
	public int getScreenWidth() {
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
		}
	}

	public void setScreenSize(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;
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

	/**
	 * set every site to clipped
	 * @param x
	 * @param y
	 * @param z
	 * @param clipped 
	 */
	private void setClipped(int x, int y, int z, boolean clipped) {
		clipping[x][y][z+1][0]=clipped;
		clipping[x][y][z+1][1]=clipped;
		clipping[x][y][z+1][2]=clipped;
	}
	
	/**
	 * get the clipping of a coordinate
	 * @param coords
	 * @return true if clipped
	 */
	public boolean[] getClipping(Coordinate coords) {
		return clipping[coords.getRelX()][coords.getRelY()][coords.getZ()+1];
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
	
	/**
	 * 
	 * @param coord
	 * @return is a coordiante completely clipped
	 * @since 1.3.10
	 */
	public boolean isCompletelyClipped(Coordinate coord) {
		boolean[] tmp = getClipping(coord);
		return (tmp[0] && tmp[1] && tmp[2]);
	}
}