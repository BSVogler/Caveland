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
package com.bombinggames.wurfelengine.core.LightEngine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.WE;

/**
 * Something shiny infinetelly far away.
 * @author Benedikt Vogler
 */
public class GlobalLightSource {   
    /**
     *The brightness of the light source
     */
    private float power;
    private Color tone; //the color of the light
    private Color ambient;
    /**
     * Current height above horizon.
     */
    private float height;
    /**
     * current angle in degrees CCW.
     */
    private float azimuth;
    private final int amplitude; //the max possible angle (from horizon) the sun can has
	private boolean fixedPosition;

    /**
     * A GlobalLightSource can be the moon, the sun or even something new.
     * @param azimuth The starting position in degrees from left. CCW
     * @param height The starting position in degrees above the horizon.
     * @param color the starting color of the light. With this parameter you controll its brightness. Full bright is white.
     * @param ambient
     * @param amplitudeHeight the maximum degree during a day the LightSource rises
     */
    public GlobalLightSource(float azimuth, float height, Color color, Color ambient, int amplitudeHeight) {
        this.azimuth = azimuth;
        this.height = height;
        this.tone = color;
        this.ambient = ambient;
        this.amplitude = amplitudeHeight;
    }

    /**
     * A light source shines can shine brighter and darker. This amplitude is called power. What it real emits says the resulting light.
     * @return a value between 0 and 1
     */
    public float getPower() {
        return power;
    }
    
    /**
     *
     * @return
     */
    public Color getTone() {
        return tone.cpy();
    }

    /**
     *current height above horizon
     * @return in degrees 0-360°
     */
    public float getHeight() {
        return height;
    }

    /**
     *
     * @return in degrees 0-360°
     */
    public float getAzimuth() {
        return azimuth;
    }

    /**
     *
     * @return
     */
    public float getAzimuthSpeed() {
        return WE.getCvars().getValueF("LEAzimutSpeed");
    }

    /**
     *
     * @return
     */
    public int getMaxAngle() {
        return amplitude;
    }

    /**
     *The Latitude posiiton. 
     * @param height in degrees 0-360°
     */
    public void setHeight(final float height) {
        this.height = height % 360;
        if (this.height < 0)
            this.height += 360;
    }

    /**
     *The longitudinal position
     * @param azimuth in degrees 0-360°
     */
    public void setAzimuth(final float azimuth) {
        this.azimuth = azimuth % 360;
        if (this.azimuth < 0)
            this.azimuth += 360;
    }

    /**
     *
     * @param tone
     */
    public void setTone(final Color tone) {
        this.tone = tone;
    }

	/**
	 * 
	 * @param fixedPosition if true does not move
	 */
	public void setFixedPosition(boolean fixedPosition) {
		this.fixedPosition = fixedPosition;
	}
	
    /**
     *
     * @param dt
     */
    public void update(float dt) {    
        //automove
		if (!fixedPosition)
			if (getAzimuthSpeed() != 0) {
				azimuth += getAzimuthSpeed() * dt;
				height = (float) (amplitude * Math.sin((azimuth + WE.getCvars().getValueI("worldSpinAngle")) * Math.PI / 180));
			}
            
        //brightness calculation
        if (height > amplitude/2 && height < 180)
                power = 1;//day
        else if (height < -amplitude/2)
                 power = 0;//night
            else if (height < amplitude/2) 
                    power = (float) (0.5f + 0.5f*Math.sin(height * Math.PI/amplitude)); //morning & evening
        
        //clamp
        if (power > 1f) power=1f;
        if (power < 0f) power=0f;  
        //power =1f;
        
        //if (azimuth>180+IGLPrototype.TWISTDIRECTION)
        //color = new Color(127 + (int) (power * 128), 255, 255);
        //else color = new Color(1f,1f,1f);
        //I_a = (int) ((90-height)*0.1f);
    }

    /**
     * Returns the light the GLS emits.
     * @return applied with pseudogray, copy safe
     */
    public Color getLight() {
        return tone.cpy().mul(power);
    }
    
    /**
     * Returns the ambient the GLS emits.
     * @return applied with pseudogray. "pointer-safe"
     */
    public Color getAmbient() {
        return ambient.cpy().mul(power);
    }
	
	/**
	 * 
	 * @return the normal of the GlobalLightSource
	 */
	public Vector3 getNormal() {
		return new Vector3(
			(float) -Math.cos(getAzimuth()*Math.PI/180f),
			(float) Math.sin(getAzimuth()*Math.PI/180f),
			(float) Math.sin(getHeight()*Math.PI/180f)
		).nor();
	}
}
