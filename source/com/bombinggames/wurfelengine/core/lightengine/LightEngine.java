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
package com.bombinggames.wurfelengine.core.lightengine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.Side;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Position;


/**
 * This Light engine calculates phong shading for three normals over the day.
 * The data is relative to a position on a map because the lighting can be different on different positions on the map.
 * @author Benedikt Vogler
 * @version 1.1.8
 * @since  WE1.1
 */
public class LightEngine {
    /**
     * The Version of the light engine.
     */
    public static final String VERSION = "1.3";
	/** 
	 * display a visual representation of the data?
	 */
    private boolean debuging = false;
    //diagramm data
    private int posX = Gdx.graphics.getWidth()/2;
    private int posY = Gdx.graphics.getHeight()/2;
    private final int size = 500;
	/**
	 * shade pixel or vertex based
	 */
	private boolean pixelBasedShading = true;
    
    
    //diffuse light
    private final float k_diff = 100/255f; //the min and max span. value between 0 and 1 empirisch bestimmter Reflexionsfaktor für diffuse Komponente der Reflexion
    private float I_diff0, I_diff1, I_diff2;
    
    //specular light
    private final int n_spec = 12; //  constant factor describing the Oberflächenbeschaffenheit (rau smaller 32, glatt bigger 32, infinity would be a perfect mirror)
    private final float k_specular = 1-k_diff; //empirisch bestimmter reflection factor of mirroring component of reflection. Value "k_diff+kspecular <= 1" therefore 1-k_diff is biggest possible value 
    private float I_spec1;
             
    /**the brightness of each side including amb+diff+spec. The value should be between 0 and 1*/
    private static float I_0, I_1, I_2;
    
    private GlobalLightSource sun;
    private GlobalLightSource moon; 

    /**
     * 
     */
    public LightEngine() {
        sun = new GlobalLightSource(
			-WE.getCVars().getValueI("worldSpinAngle"),
			0,
			new Color(1, 1, 1, 1),
			new Color(0.5f, 0.5f, 0.4f, 1),
			1f,
			60
		);
		moon = new Moon(
			180-WE.getCVars().getValueI("worldSpinAngle"),
			0,
			new Color(0.4f,0.9f,0.9f,1),
			new Color(0, 0, 0.1f, 1),
			1.0f,
			45
		);
		
		pixelBasedShading = WE.getCVars().getValueB("LEnormalMapRendering");
		//restore light engine setting position
		getSun(new Coordinate(0, 0, 0)).setAzimuth(Controller.getMap().getSaveCVars().getValueF("LEsunAzimuth"));
		getMoon(new Coordinate(0, 0, 0)).setAzimuth(Controller.getMap().getSaveCVars().getValueF("LEmoonAzimuth"));
    }
	
    /**
     *
     * @param xPos the x position of the diagrams position (center)
     * @param yPos the y position of the diagrams position (center) 
     */
    public LightEngine(int xPos, int yPos) {
        this();
        this.posX = xPos;
        this.posY = yPos;
    }
	
    /**
     * 
     * @param dt
     */
    public void update(float dt) {
        sun.update(dt);
		
		//set moon to rise if sun is going down and moon is not about to rise
		if (getTimeOfDay() > 0.25f &&
			getTimeOfDay() < 0.3f &&
			Math.abs((moon.getAzimuth() -210 - WE.getCVars().getValueI("worldSpinAngle"))%360) > 10
		) {
			moon.setAzimuth(210 + WE.getCVars().getValueI("worldSpinAngle"));
		}
		
        if (moon != null) {
			moon.update(dt);
			float moonI = moon.getPower();
			//calcualte moon light in diff and spec
			float tmp = (float) (moonI * k_diff * Math.cos(((moon.getHeight()) * Math.PI)/180) * Math.cos(((moon.getAzimuth()-45)*Math.PI)/180));
			if (tmp>0) I_diff0+=tmp;

			tmp = (float) (moonI * k_diff * Math.cos(((moon.getHeight()-90)*Math.PI)/180));   
			if (tmp>0) I_diff1+=tmp;

			tmp = (float) (moonI  * k_diff * Math.cos(((moon.getHeight())*Math.PI)/180)*Math.cos(((moon.getAzimuth()-135)*Math.PI)/180));
			if (tmp>0) I_diff2+=tmp;
			
			//specular
			I_spec1 +=(float) (
				moonI
				* k_specular
				* Math.pow(
					Math.sin((moon.getHeight())*Math.PI/180)*Math.sin((moon.getAzimuth())*Math.PI/180)/Math.sqrt(2)//y
				  + Math.sin((moon.getHeight()-90)*Math.PI/180)/Math.sqrt(2)//z
				,n_spec)
				*(n_spec+2)/(2*Math.PI)
			);
		}
		
        float sunI = sun.getPower();
        
        //diffusion
   //diff0
		I_diff0 = (float) (sunI * k_diff * Math.cos(((sun.getHeight()) * Math.PI) / 180) * Math.cos(((sun.getAzimuth() - 45) * Math.PI) / 180));
		if (I_diff0 < 0) {
			I_diff0 = 0;
		}

		//diff0
		I_diff1 = (float) (sunI * k_diff * Math.cos(((sun.getHeight() - 90) * Math.PI) / 180));
		if (I_diff1 < 0) {
			I_diff1 = 0;
		}

		//diff2
		I_diff2 = (float) (sunI * k_diff * Math.cos(((sun.getHeight()) * Math.PI) / 180) * Math.cos(((sun.getAzimuth() - 135) * Math.PI) / 180));
		if (I_diff2 < 0) {
			I_diff2 = 0;
		}
        
        //specular
        
        //it is impossible to get specular light with a GlobalLightSource over the horizon on side 0 and 2. Just left in case i it someday helps somebody.
//        I_spec0 =(int) (
//                        sunI
//                        * k_specular
//                        * Math.pow(
//                            Math.sin((sun.getHeight())*Math.PI/180)*Math.sin((sun.getAzimuth())*Math.PI/180)* Math.sqrt(2)/Math.sqrt(3)//y
//                          + Math.sin((sun.getHeight()-75)*Math.PI/180)/Math.sqrt(3)//z
//                        ,n_spec)
//                        *(n_spec+2)/(2*Math.PI)
//                        );

        
      I_spec1 = (float) (sunI
			* k_specular
			* Math.pow(
				Math.sin(sun.getHeight() * Math.PI / 180) * Math.sin(sun.getAzimuth() * Math.PI / 180) / Math.sqrt(2)//y
				+ Math.sin((sun.getHeight() - 90) * Math.PI / 180) / Math.sqrt(2)//z
				, n_spec)
			* (n_spec + 2) / (2 * Math.PI));
         
      //it is impossible to get specular light with a GlobalLightSource over the horizon on side 0 and 2. Just left in case it someday may help somebody.
        //        I_spec2 =(int) (
        //                        sunI
        //                        * k_specular
        //                        * Math.pow(
        //                            Math.cos((sun.getHeight() - 35.26) * Math.PI/360)
        //                           *Math.cos((sun.getAzimuth() + 180) * Math.PI/360)
        //                        ,n_spec)
        //                        *(n_spec+2)/(2*Math.PI)
        //                        );   
               
        I_0 = I_diff0;
        I_1 = I_diff1 + I_spec1;
        I_2 = I_diff2;
        
        
       //update input
		if (Gdx.input.isButtonPressed(0) && debuging) {
			//sun.setHeight(sun.getHeight()+Gdx.input.getDeltaY()*30f);
			sun.setAzimuth(Gdx.input.getX());
			if (moon != null) {
				moon.setAzimuth(Gdx.input.getX() - 180);
			}
		}
    }
    
  /**
	 * Returns the average brightness.
	 *
	 * @return
	 */
	public static float getVertexBrightness() {
		return (I_0 + I_1 + I_2) / 3f;
	}
	
	/**
	 * You can pass the position if the sun changes relative to the postition.
	 *
	 * @param pos relative to this position
	 * @return
	 */
	public GlobalLightSource getSun(Position pos) {
		return sun;
	}

	/**
	 * You can pass the position if the moon changes relative to the postition.
	 *
	 * @param pos
	 * @return
	 */
	public GlobalLightSource getMoon(Position pos) {
		return moon;
	}

	/**
	 *
	 * @param moon
	 */
	public void setMoon(GlobalLightSource moon) {
		this.moon = moon;
	}

	/**
	 * restores position via save cvar "LEsunAzimuth".
	 * @param sun 
	 */
	public void setSun(GlobalLightSource sun) {
		this.sun = sun;
		getSun(new Coordinate(0, 0, 0)).setAzimuth(Controller.getMap().getSaveCVars().getValueF("LEsunAzimuth"));
	}

	/**
	 * The light engine can shade the world pixel based or vertext based.
	 *
	 * @return true if rendering via normal map false if vertext based
	 */
	public boolean isShadingPixelBased() {
		return pixelBasedShading;
	}
    
//        /**
//     * Gets average color. 
//     * @return pseudoGrey color
//     * @see #getColor(com.bombinggames.wurfelengine.Core.Gameobjects.Side)
//     */
//    public Color getColor(){
//		if (pixelBasedShading)
//			return Color.WHITE.cpy();
//        return getAmbient().add( getEmittingLights().mul( getVertexBrightness() ) );
//        //more precise (?) but slower
////        float r = (getColor(Side.LEFT).r + getColor(Side.TOP).r + getColor(Side.RIGHT).r)/3f;
////        float g = (getColor(Side.LEFT).g + getColor(Side.TOP).g + getColor(Side.RIGHT).g)/3f;
////        float b = (getColor(Side.LEFT).b + getColor(Side.TOP).b + getColor(Side.RIGHT).b)/3f;
////        return new Color(r,g,b,1);
//    }
        
//    /**
//     * Get's the brightness to a normal.
//     * @param normal 0 left 1 top or 2 right
//     * @return pseudoGrey color
//     */
//    public Color getColor(Side normal){
//          if (normal==Side.LEFT)
//            return getAmbient().add(getDiff(normal));
//        else if (normal==Side.TOP)
//            return getAmbient().add(getDiff(normal)).add(getSpec(normal));
//        else
//            return getAmbient().add(getDiff(normal));
//    }
    
    /**
     * 
     * @param normal
     * @return pseudoGrey color, copy safe
     */
     private Color getDiff(Side normal, Position pos){
      if (null == normal || pos == null) {
			 throw new IllegalArgumentException();
		 }
		 switch (normal) {
			 case LEFT:
				 return getEmittingLights(pos).mul(I_diff0, I_diff0, I_diff0, 1);
			 case TOP:
				 return getEmittingLights(pos).mul(I_diff1, I_diff1, I_diff1, 1);
			 default:
				 return getEmittingLights(pos).mul(I_diff2, I_diff2, I_diff2, 1);
		 }
    }
     
   /**
	 *
	 * @param normal
	 * @return pseudoGrey color, copy safe
	 */
	private Color getSpec(Side normal, Position pos) {
		if (normal == Side.TOP) {
			return getEmittingLights(pos).mul(I_spec1);//only top side can have specular light
		} else {
			return Color.BLACK.cpy();
		}
	}

	/**
	 * copy safe
	 *
	 * @param normal
	 * @param pos
	 * @return
	 */
	public Color getColor(Side normal, Position pos) {
		return getSpec(normal, pos).add(getDiff(normal, pos));
	}

    
   /**
	 * Returns the sum of every light source's ambient light
	 *
	 * @param pos
	 * @return a color with a tone
	 */
	public Color getAmbient(Position pos) {
		Color amb = getSun(pos).getAmbient();
		if (getMoon(pos) != null) {
			amb.add(getMoon(pos).getAmbient());
		}
		return amb;
	}

	/**
	 * Mix of both light sources. Used for diff and spec.
	 *
	 * @return a color with a tone, copy safe
	 */
	private Color getEmittingLights(Position pos) {
		Color light = getSun(pos).getLight();
		if (getMoon(pos) != null) {
			light.add(getMoon(pos).getLight());
		}
		return light;
	}
    
   /**
	 *
	 * @return
	 */
	public boolean isInDebug() {
		return debuging;
	}

	/**
	 * Should diagrams be rendered showing the data of the LE.
	 *
	 * @param debug
	 */
	public void setDebug(boolean debug) {
		this.debuging = debug;
	}

    /**
	 * Shows the data of the light engine in diagramms.
	 *
	 * @param view
	 * @param pos display the light data at this position
	 */
	public void render(GameView view, Position pos) {
		if (debuging) {

			//g.setLineWidth(2);
			ShapeRenderer shR = view.getShapeRenderer();

			//surrounding sphere
			Gdx.gl20.glLineWidth(2);
			shR.setColor(Color.BLACK);
			shR.begin(ShapeType.Line);
			shR.circle(posX, posY, size);

			//cut through
			shR.translate(posX, posY, 0);
			shR.scale(1f, (0.5f), 1f);
			shR.circle(0, 0, size);
			shR.scale(1f, (2), 1f);
			shR.translate(-posX, -posY, 0);

			//sun position
			//longitude
			shR.setColor(Color.RED);
			shR.line(
				posX,
				posY,
				posX + (int) (size * sun.getNormal().x),
				posY - (int) (size / 2 * sun.getNormal().y)
			);

			//connection to end pos
			if (sun.getNormal().z > 0) {
				shR.setColor(Color.GRAY);
				shR.line(
					posX + (int) (size * sun.getNormal().x),
					posY - (int) (size / 2 * sun.getNormal().y),
					posX + (int) (size * sun.getNormal().x),
					posY + (int) (size * (-sun.getNormal().y / 2 + sun.getNormal().z))
				);
			}

			//latitude
			shR.setColor(Color.MAGENTA);
			shR.line(
				posX,
				posY,
				posX - (int) (size / 2 * (1 - sun.getNormal().z) + size / 2),
				posY + (int) (size / 2 * sun.getNormal().z)
			);

			//long+lat of sun position
			shR.setColor(Color.YELLOW);
			shR.line(
				posX,
				posY,
				posX + (int) (size * sun.getNormal().x),
				posY + (int) (size * (-sun.getNormal().y / 2 + sun.getNormal().z))
			);

			view.drawString(
				"SUN",
				posX + (int) (size * sun.getNormal().x),
				posY + (int) (size * (-sun.getNormal().y / 2 + sun.getNormal().z)),
				true
			);

			if (moon != null) {
				//connection to end pos
				if (moon.getNormal().z > 0) {
					shR.setColor(Color.GRAY);
					shR.line(
						posX + (int) (size * moon.getNormal().x),
						posY - (int) (size / 2 * moon.getNormal().y),
						posX + (int) (size * moon.getNormal().x),
						posY + (int) (size * (-moon.getNormal().y / 2 + moon.getNormal().z))
					);
				}
				shR.setColor(Color.BLUE);
				shR.line(
					posX,
					posY,
					posX + (int) (size * moon.getNormal().x),
					posY + (int) (size * (-moon.getNormal().y / 2 + moon.getNormal().z))
				);
				view.drawString(
					"MOON",
					posX + (int) (size * moon.getNormal().x),
					posY + (int) (size * (-moon.getNormal().y / 2 + moon.getNormal().z)),
					true
				);
			}
			shR.end();

			view.getSpriteBatch().begin();
			int y = Gdx.graphics.getHeight() - 150;
			view.drawString(
				"Lat: " + sun.getHeight() + "\n"
				+ "Long: " + sun.getAzimuth() + "\n"
				+ "PowerSun: " + sun.getPower() * 100 + "%\n"
				+ "Normal:" + sun.getNormal(),
				900,
				100,
				false
			);
			if (moon != null) {
				view.drawString(
					"Power Moon: " + moon.getPower() * 100 + "%",
					420,
					y + 45,
					false);
			}
			view.drawString(
					"Power Sun: " + sun.getPower() * 100 + "%",
					420,
					y + 70,
					false);
			view.drawString("Ambient: ", 600, y + 110, false);
			view.drawString("Diffuse: ", 680, y + 110, false);
			view.drawString(getAmbient(pos).toString(), 620, y += 10, false);
			view.getSpriteBatch().end();
			// view.drawString("avg. color: "+sun.getColor().toString(), 600, y+=10, Color.WHITE);

			shR.begin(ShapeType.Filled);
			//draw ambient light
			shR.setColor(Color.WHITE);
			shR.rect(600, y += 10, 70, 70);//background
			shR.setColor(sun.getAmbient());
			y += 10;
			shR.rect(600, y + 25, 19, 25);//draw sun ambient color
			if (moon != null) {
				shR.setColor(moon.getAmbient());//draw moon ambient color
				shR.rect(600, y, 19, 25);
			}
			shR.setColor(getAmbient(pos));//draw combined color
			shR.rect(620, y, 50, 50);

			view.drawString("+", 670, y + 25, true);

			//draw sun
			shR.setColor(Color.WHITE);
			shR.rect(680, y - 10, 70, 70);
			//shR.setColor(getEmittingLights().mul(getVertexBrightness()));
			shR.setColor(getSun(pos).getLight());
			shR.rect(690, y, 50, 50);

			y -= 60;
			//moon
			if (getMoon(pos) != null) {
				shR.setColor(Color.WHITE);
				shR.rect(680, y - 10, 70, 70);
				//shR.setColor(getEmittingLights().mul(getVertexBrightness()));
				shR.setColor(getMoon(pos).getLight());
				shR.rect(690, y, 50, 50);
			}

			//view.drawString("=", 760, y + 25, true);
			//draw result
//                shR.setColor(Color.WHITE);
//                shR.rect(770, y-10, 70, 70);
//                shR.setColor(getColor(Side.LEFT));
//                shR.rect(780, y, 25, 30);
//                shR.setColor(getColor(Side.TOP));
//                shR.rect(780, y+25, 50, 30);
//                shR.setColor(getColor(Side.RIGHT));
//                shR.rect(805, y, 25, 30);
			// shR.setColor(getColor());
			//shR.rect(800, y+20, 15, 15);
//				shR.setColor(Color.WHITE);
//                shR.rect(780, y-10, 70, 70);
//                shR.setColor(getSun(pos).getLight());
//                shR.rect(790, y, 50, 50);
			//info bars
			//left side
			y = Gdx.graphics.getHeight() - 100;
			view.drawString(Float.toString(I_diff0), (int) (I_0 * size), y, true);
			shR.setColor(Color.RED);
			shR.rect(0, y, I_diff0 * size, 8);

			//top side
			y = Gdx.graphics.getHeight() - 180;
			view.drawString(I_diff1 + "\n+" + I_spec1 + "\n=" + I_1, (int) (I_1 * size), y, true);
			shR.setColor(Color.RED);
			shR.rect(0, y, I_diff1 * size, 8);
			shR.setColor(Color.BLUE);
			shR.rect(I_diff1 * size, y, I_spec1 * size, 6);

			//right side
			y = Gdx.graphics.getHeight() - 260;
			view.drawString(Float.toString(I_diff2), (int) (I_2 * size), y, true);
			shR.setColor(Color.RED);
			shR.rect(0, y, I_diff2 * size, 8);

			shR.end();
			Gdx.gl20.glLineWidth(1);//reset
		}
	}

	/**
	 *
	 * @param pos
	 */
	public void setToNoon(Position pos) {
		getSun(pos).setAzimuth(90);
		if (getMoon(pos) != null) {
			getMoon(pos).setAzimuth(270);
		}
	}

	/**
	 *
	 * @param pos
	 */
	public void setToNight(Position pos) {
		getSun(pos).setAzimuth(270);
		getMoon(pos).setAzimuth(90);
	}
	
	/**
	 *
	 * @return value between 0 and 1
	 */
	public float getTimeOfDay() {
		return (sun.getAzimuth() + WE.getCVars().getValueI("worldSpinAngle")) % 360 / 360f;
	}
}
