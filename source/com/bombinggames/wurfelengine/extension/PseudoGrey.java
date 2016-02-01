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

/**
* Algorithm by Rich Franzen, 22 July 1999
*mailto:rich@r0k.us
*(c) 1999, Rich Franzen
 * @author Rich Franzen, Benedikt Vogler
 */
public class PseudoGrey {
    /**Transforms a brightness into a pseudogrey-color.
     * @param grey the brightness
     * @return a pseudo-grayscale color
     */
    public static Color toColor(float grey) {
	int i4k, boost, r, g, b;

	i4k = (int)(grey * 4095);
	if (i4k >= 4095)
	{   // only one case of "infinity",
	    // but the >= also protects against over-bounding (r,g,b)
	    r = 255;
	    g = 255;
	    b = 255;
	}
	  else if (i4k >= 0)
	  { // normal case
	    if (i4k < 0xfe0)
	    {
		r = g = b = i4k >> 4;
		boost = i4k & 0x00f;	// 16 possibilities
	    }
	      else
	      { // accelerate rate near "infinity"
		r = g = b = 0xfe;	// base level of 254
		boost  = i4k & 0x01f;	// 32 possibilities
		boost /= 2;		// back down to 16
	      }

	    if (boost >= 14)
	    {
		r += 1;
		g += 1;
	    }
	      else if (boost >= 11)
	      {
		g += 1;
		b += 1;
	      }
	      else if (boost >= 9)
		g += 1;
	      else if (boost >= 7)
	      {
		r += 1;
		b += 1;
	      }
	      else if (boost >= 5)
		r += 1;
	      else if (boost >= 2)
		b += 1;
	  }
	  else
	  { // protect against error condition of negative grey
	    r = 0;
	    g = 0;
	    b = 0;
	  }
	return new Color(r/256f, g/256f, b/256f,1);
    }

    /**
     *
     * @param c
     * @return
     */
    public static float toFloat(Color c) {
	int	red, green, blue;
	int	cMin, cMax;
	int	base12;
	float	grey;

	red   = (int) (c.r*255);
	green = (int) (c.g*255);
	blue  = (int) (c.b*255);
	cMin  = Math.min(red, Math.min(green, blue));
	cMax  = Math.max(red, Math.max(green, blue));

	if (cMin == 255)
	    grey = 1.0f;
	  else if (cMax == cMin)
	  { // short-cut this common case
	    base12 = cMin << 4;
	    grey = base12 / 4095.0f;
	  }
	  else if ((cMax - cMin) < 2)
	  { // pseudoGrey
	    int	delta = 0;			// luma weights:
	    if (cMax == blue)   delta += 2;	// .114 * 16 == 1.824
	    if (cMax == red)    delta += 5;	// .299 * 16 == 4.784
	    if (cMax == green)  delta += 9;	// .587 * 16 == 9.392
	    if (cMin == 254)    delta *= 2;  // accelerate near "infinity"
	    base12 = (cMin << 4) + delta;
	    grey = base12 / 4095.0f;
	  }
	  else
	  { // use luma conversion
	    grey  = (.299f*red + .587f*green + .114f*blue) / 255.0f;
	  }

	return grey;
    }
}