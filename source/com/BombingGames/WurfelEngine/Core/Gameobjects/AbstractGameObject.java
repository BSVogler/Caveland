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
package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.io.Serializable;

/**
 *An AbstractGameObject is something wich can be found in the game world.
 * @author Benedikt
 */
public abstract class AbstractGameObject implements Serializable, HasID {
	private transient static final long serialVersionUID = 2L;
	
    /**Screen depth of a block/object sprite in pixels. This is the length from the top to the middle border of the block.
     */
    public transient static final int VIEW_DEPTH = 100;
    /**The half (1/2) of VIEW_DEPTH. The short form of: VIEW_DEPTH/2*/
    public transient static final int VIEW_DEPTH2 = VIEW_DEPTH / 2;
    /**A quarter (1/4) of VIEW_DEPTH. The short form of: VIEW_DEPTH/4*/
    public transient static final int VIEW_DEPTH4 = VIEW_DEPTH / 4;
    
    /**
     * The width (x-axis) of the sprite size.
     */
    public transient static final int VIEW_WIDTH = 200;
    /**The half (1/2) of VIEW_WIDTH. The short form of: VIEW_WIDTH/2*/
    public transient static final int VIEW_WIDTH2 = VIEW_WIDTH / 2;
    /**A quarter (1/4) of VIEW_WIDTH. The short form of: VIEW_WIDTH/4*/
    public transient static final int VIEW_WIDTH4 = VIEW_WIDTH / 4;
    
    /**
     * The height (y-axis) of the sprite size.
     */
    public transient static final int VIEW_HEIGHT = 122;
    /**The half (1/2) of VIEW_HEIGHT. The short form of: VIEW_WIDTH/2*/
    public transient static final int VIEW_HEIGHT2 = VIEW_HEIGHT / 2;
    /**A quarter (1/4) of VIEW_HEIGHT. The short form of: VIEW_WIDTH/4*/
    public transient static final int VIEW_HEIGHT4 = VIEW_HEIGHT / 4;
    
    /**
     * The game space dimension size's aequivalent to VIEW_DEPTH or VIEW_WIDTH.
     * Because the x axis is not shortened those two are equal.
     */
    public transient static final int GAME_DIAGLENGTH = VIEW_WIDTH;
    
    /**Half (1/2) of GAME_DIAGLENGTH.
     */
    public transient static final int GAME_DIAGLENGTH2 = VIEW_WIDTH2;
	
	    /**
     * The game spaces dimension in pixel (edge length). 1 game meter ^= 1 GAME_EDGELENGTH
 The value is calculated by VIEW_HEIGHT*sqrt(2) because of the axis shortening.
     */
    public transient static final int GAME_EDGELENGTH = (int) (GAME_DIAGLENGTH / 1.41421356237309504880168872420969807856967187537694807317667973799f);
    
	/**
     * Half (1/2) of GAME_EDGELENGTH.
     */
    public transient static final int GAME_EDGELENGTH2 = GAME_EDGELENGTH/2;
    
	/**
	 * Some magic number which is the factor by what the Z axis is distorted because of the angle pf projection.
	 */
	public transient static final float ZAXISSHORTENING = VIEW_HEIGHT/(float) GAME_EDGELENGTH;
		
    /**the max. amount of different object types*/
    public transient static final int OBJECTTYPESNUM = 124;
      /**the max. amount of different values*/
    public transient static final int VALUESNUM = 64;
    

        
    /**The sprite texture which contains every object texture*/
    private transient static TextureAtlas spritesheet;
	private transient static String spritesheetPath = "com/BombingGames/WurfelEngine/Core/images/Spritesheet";
    private transient static Pixmap pixmap;
    private transient static AtlasRegion[][][] sprites = new AtlasRegion['z'][OBJECTTYPESNUM][VALUESNUM];//{category}{id}{value}
    private transient static int drawCalls =0;
	private static Texture textureDiff;
	private static Texture textureNormal;
	
    private CoreData coreData;
	//render information
    private boolean hidden; 
    private float rotation;
	private float scaling;
	private byte graphicsID;
	
	/**
	 * default is RGBA 0x80808080.
	 */
	private transient Color tint = new Color(0.5f, 0.5f, 0.5f, 1); 
	
    /**
     * Creates an object.
     * @param id the id of the object
     * @param value 
     */
    protected AbstractGameObject(byte id, byte value) {
        coreData = CoreData.getInstance(id, value);
		this.graphicsID = id;
    }
    
    /**
     * Get the category letter for accessing sprites.
     * @return
     */
    public abstract char getCategory();
    
	/**
	 *
	 * @return
	 */
	public abstract int getDimensionZ();
	
	  /**
     * Return the coordinates of the SelfAware object.
     * @return Reference to the position object which points to the location in the game world.
     */
    public abstract AbstractPosition getPosition();
    
    /**
     * Set the coordinates without safety check.
     * @param pos the coordinates you want to set
     */
    public abstract void setPosition(AbstractPosition pos);

	/**
	 * the diffuse map
	 * @return 
	 */
	public static Texture getTextureDiffuse() {
		return textureDiff;
	}

	/**
	 * the normal map
	 * @return 
	 */
	public static Texture getTextureNormal() {
		return textureNormal;
	}
	
	/**
	 * Set your custom spritesheet path. the suffix will be added
	 * @param customPath format like "com/BombingGames/WurfelEngine/Core/images/Spritesheet" without suffix 
	 */
	public static void setCustomSpritesheet(String customPath) {
		AbstractGameObject.spritesheetPath = customPath;
	}

	/**
	 * path of the spritesheet
	 * @return 
	 */
	public static String getSpritesheetPath() {
		return spritesheetPath;
	}
	
	/**
     * Returns the depth of the object. Nearer objects have a bigger depth.
	 * @param view
     * @return distance from zero level
     */
    public int getDepth(GameView view) {
        return (int) (getPosition().getDepth(view)
            + getDimensionZ()/AbstractPosition.SQRT2
        );
    }
	
     /**
     *
     * @return
     */
    public static AtlasRegion[][][] getSprites() {
        return sprites;
    }

    /**
     * Reset couner for this frame
     */
    public static void resetDrawCalls() {
        AbstractGameObject.drawCalls = 0;
    }

    /**
     * Maybe not quite correct. A single block has only one drawcall even it should consist of three.
     * @return 
     */
    public static int getDrawCalls() {
        return drawCalls;
    }
    
    /**
     * When calling sprite.draw this hsould also be called for statistics.
     */
    protected void increaseDrawCalls(){
        drawCalls++;
    }
        
    /**
     * Load the spritesheet from memory.
     */
    public static void loadSheet() {
        //spritesheet = new TextureAtlas(Gdx.files.internal("com/BombingGames/Game/Blockimages/Spritesheet.txt"), true);
        Gdx.app.log("AGameObject", "getting spritesheet");
        if (spritesheet == null) {
            spritesheet = WE.getAsset(spritesheetPath+".txt");
        }
		textureDiff = spritesheet.getTextures().first();
        if (WE.CVARS.getValueB("LEnormalMapRendering"))
			textureNormal = WE.getAsset(spritesheetPath+"Normal.png");
		
        //load again for pixmap, allows access to image color data;
        if (WE.CVARS.getValueB("loadPixmap")) {
			if (pixmap == null) {
				//pixmap = WurfelEngine.getInstance().manager.get("com/BombingGames/Game/Blockimages/Spritesheet.png", Pixmap.class);
				pixmap = new Pixmap(
					Gdx.files.internal(spritesheetPath+".png")
				);
			}
		}
    }

    /**
     * Returns a sprite texture. You may use your own method like in <i>Block</i>.
     * @param category the category of the sprite e.g. "b" for blocks
     * @param id the id of the object
     * @param value the value of the object
     * @return 
     */
    public static AtlasRegion getSprite(final char category, final int id, final int value) {
        if (spritesheet == null) return null;
        if (sprites[category][id][value] == null){ //load if not already loaded
            AtlasRegion sprite = spritesheet.findRegion(category+Integer.toString(id)+"-"+value);
            if (sprite == null){ //if there is no sprite show the default "sprite not found sprite" for this category
                Gdx.app.debug("Spritesheet", category+Integer.toString(id)+"-"+value + " not found");
                sprite = spritesheet.findRegion(category+"0-0");
                if (sprite == null) {//load generic error sprite if category sprite failed
                    sprite = spritesheet.findRegion("error");
                    if (sprite == null) throw new NullPointerException("Sprite and category error not found and even the generic error sprite could not be found. Something with the sprites is fucked up.");
                }
            }
            sprites[category][id][value] = sprite;
            return sprite;
        } else {
            return sprites[category][id][value];
        }
    }


    //getter & setter
    
     /**
     * Returns the spritesheet used for rendering.
     * @return the spritesheet used by the objects
     */
    public static TextureAtlas getSpritesheet() {
        return spritesheet;
    }

    /**
     *
     * @return
     */
    public static Pixmap getPixmap() {
        return pixmap;
    }
    
    /**
     * Draws an object in the color of the light engine and with the lightlevel. Only draws if not hidden.
     * @param view the view using this render method
     * @param camera The camera rendering the scene
     */
    public void render(GameView view, Camera camera) {
        render(
            view,
            camera,
            null
        );
    }
    
    /**
     * Draws an object if it is not hidden and not clipped.
     * @param view the view using this render method
     * @param camera The camera rendering the scene
     * @param color custom blending color
     */
    public void render(GameView view, Camera camera, Color color) {
        if (!hidden) {  
			if (WE.CVARS.getValueB("enableFog")) {
				//can use CVars for dynamic change. using harcored values for performance reasons
				float factor = (float) (Math.exp((camera.getVisibleBackBorder()-getPosition().getCoord().getY())*0.17+2));
				if (color ==null) {
					color = new Color(0.5f, 0.5f, 0.5f, 1).add(
						0.3f*factor,
						0.4f*factor,
						1f*factor,
						0f
					);
				} else {
					color.add(
						0.3f*factor,
						0.4f*factor,
						1f*factor,
						0f
					);
				}
			}
            render(
                view,
                getPosition().getViewSpcX(view),
                getPosition().getViewSpcY(view),
				color
            );
        }
    }
    
    /**
     * Renders at a custom position.
     * @param view
     * @param xPos rendering position, center of sprite in projection (?) space 
     * @param yPos rendering position, center of sprite in projection (?) space
     */
    public void render(GameView view, int xPos, int yPos) {
		render(view, xPos, yPos, null);
    }
    
	    /**
     * Renders at a custom position with a custom light.
     * @param view
     * @param xPos rendering position, center of sprite in projection space (?)
     * @param yPos rendering position, center of sprite in projection space (?)
	 * @param color color which gets multiplied with the tint. No change ( multiply with 1) is RGBA 0x80808080.
     */
    public void render(GameView view, int xPos, int yPos, Color color) {
		if (getId() != 0){
			AtlasRegion texture = getSprite(getCategory(), graphicsID, getValue());
			Sprite sprite = new Sprite(texture);
			sprite.setOrigin(
				texture.originalWidth/2 - texture.offsetX,
				VIEW_HEIGHT2 - texture.offsetY
			);
			sprite.rotate(rotation);
			sprite.scale(scaling);

			sprite.setPosition(xPos+texture.offsetX-texture.originalWidth/2,
				yPos//center
					-VIEW_HEIGHT2
					+texture.offsetY
			);
			
			//hack for transient field tint
			if (tint == null) tint = new Color(0.5f, 0.5f, 0.5f, 1); 
			if (color!=null)
				sprite.setColor(tint.cpy().mul(color.r+0.5f, color.g+0.5f, color.b+0.5f, color.a+0.5f));
			else sprite.setColor(tint);
        
			if (view.debugRendering()){
				ShapeRenderer sh = view.getShapeRenderer();
				sh.begin(ShapeRenderer.ShapeType.Line);
				//sprite outline
				sh.rect(
					sprite.getX(),
					sprite.getY(),
					sprite.getWidth(),
					sprite.getHeight()
				);
				//crossing lines
				sh.line(
					xPos-VIEW_WIDTH2,
					yPos-VIEW_DEPTH2,
					xPos+VIEW_WIDTH2,
					yPos+VIEW_DEPTH2
				);
				sh.line(
					xPos-VIEW_WIDTH2,
					yPos+VIEW_DEPTH2,
					xPos+VIEW_WIDTH2,
					yPos-VIEW_DEPTH2
				);
				//bounding box
				sh.line(xPos-VIEW_WIDTH2, yPos, xPos, yPos-VIEW_DEPTH2);
				sh.line(xPos-VIEW_WIDTH2, yPos, xPos, yPos+VIEW_DEPTH2);
				sh.line(xPos, yPos-VIEW_DEPTH2, xPos+VIEW_WIDTH2, yPos);
				sh.line(xPos, yPos+VIEW_DEPTH2, xPos+VIEW_WIDTH2, yPos);
				sh.end();
			} else {
				sprite.draw(view.getBatch());
				drawCalls++;
			}
		}
    }
	
    //getter & setter

	public CoreData getCoreData() {
		return coreData;
	}

	@Override
    public byte getId() {
		if (coreData==null) return 0;
        return coreData.getId();
    }
	
	@Override
    public byte getValue() {
		if (coreData==null) return 0;
        return coreData.getValue();
    }

	/**
	 * the id of the sprite. should be the same as id but in some cases some objects share their sprites.
	 * @return 
	 */
	public int getSpriteId() {
		return graphicsID;
	}

	@Override
    public float getLightlevel() {
        return coreData.getLightlevel();
    }

    /**
     * Returns the name of the object
     * @return the name of the object
     */
	@Override
    public abstract String getName();


    /**
     * Returns the rotation of the object.
     * @return
     */
    public float getRotation() {
        return rotation;
    }

	/**
	 * Returns the scale factor of the object.
	 * @return 0 is no scaling
	 */
	public float getScaling() {
		return scaling;
	}
	
    /**
     * Returns true, when set as hidden. Hidden objects are not rendered even when they are clipped ("clipped" by the meaning of the raytracing).
     * @return if the object is invisible
     */
    public boolean isHidden() {
        return hidden;
    }

	@Override
    public void setLightlevel(float lightlevel) {
        this.coreData.setLightlevel(lightlevel);
    }

    /**
     * Set the value of the object.
     * @param value
     */
    public void setValue(byte value) {
        this.coreData.setValue(value);
    }


    /**
     * Hide an object. It won't be rendered even if it is clipped.
     * @param hidden
     */
    public void setHidden(boolean hidden){
        this.hidden = hidden;
    }

    /**
     *
     * @param rotation set the rotation in degrees.
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
	
	/**
	 * 
	 * @param scaling 0 no scaling
	 */
	public void setScaling(float scaling){
		this.scaling = scaling;
	}
    
	/**
	 * the id of the sprite. should be the same as id but in some cases some objects share their sprites.
	 * @param id 
	 */
	public void setGraphicsId(byte id) {
		graphicsID = id;
	}
	
	
    /**
     *disposes static fields
     */
    public static void staticDispose(){
        spritesheet.dispose();//is this line needed?
        WE.getAssetManager().unload(spritesheetPath+".txt");
        spritesheet = null;
        sprites = new AtlasRegion['z'][OBJECTTYPESNUM][VALUESNUM];
        //pixmap.dispose();
        pixmap = null;
    }

	   /**
     *
     * @return from maximum 1000
     */
	public float getHealth() {
		return coreData.getHealth();
	}

	/**
	 * clamps to [0..1000]
	 * @param health 
	 */
	public void setHealth(byte health) {
		if (health>100) health=100;
		if (health<0) health=0;
		this.coreData.setHealth(health);
	}

	/**
	 * give the object a tint. The default brightness is RGBA 0x808080FF so you can make it brighter and darker by modifying R, G and B.
	 * @param color 
	 */
	public void setColor(Color color) {
		this.tint = color;
	}

	/**
	 * get the tint of the object. The default brightness is RGBA 0x808080FF so you can make it brighter and darker by modifying R, G and B.
	 * @return not copy safe
	 */
	public Color getColor() {
		return tint;
	}
	
	/**
	 * Should i.g. not be used for rendering.
	 * @return the sprite used for rendering
	 */
	public AtlasRegion getAtlasRegion(){
		return getSprite(getCategory(), graphicsID, getValue());
	}
}