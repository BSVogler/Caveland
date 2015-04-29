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

import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * A view is an object which renders the data. Game space or not does not matter for this class.
 * @author Benedikt Vogler
 */
public abstract class View {
    private static ShaderProgram shader;
	
	/**
	 *
	 * @return
	 */
	public abstract SpriteBatch getBatch();

	/**
	 *
	 * @return
	 */
	public abstract ShapeRenderer getShapeRenderer();
	
	/**
	 * true if current rendering is debug only
	 */
	private boolean inDebug;
	
	/**
	 * initializes the view.
	 */
    public void init(){
		loadShaders();
    }
    
	/**
	 * Get the loaded shader program of the view.
	 * @return 
	 */
    public ShaderProgram getShader() {
        return shader;
    }
	
    	/**
	 * enable debug rendering only
	 * @param debug 
	 */
	void setDebugRendering(boolean debug) {
		this.inDebug = debug;
	}
	
		/**
	 * 
	 * @return true if current rendering is debug only
	 */
	public boolean debugRendering() {
		return inDebug;
	}
	
	/**
	 * reloads the shaders
	 */
	public void loadShaders(){
		String vertexShader;
		String fragmentShader;
		//shaders are very fast to load and the asset loader does not support text files out of the box
		if (WE.CVARS.getValueB("LEnormalMapRendering")){
			vertexShader = Gdx.files.internal("com/BombingGames/WurfelEngine/Core/vertexNM.vs").readString();
			fragmentShader = Gdx.files.internal("com/BombingGames/WurfelEngine/Core/fragmentNM.fs").readString();
		} else {
			vertexShader = Gdx.files.internal("com/BombingGames/WurfelEngine/Core/vertex.vs").readString();
			fragmentShader = Gdx.files.internal("com/BombingGames/WurfelEngine/Core/fragment.fs").readString();
		}
		//Setup shader
		ShaderProgram.pedantic = false;
		
		ShaderProgram newshader = new ShaderProgram(vertexShader, fragmentShader);
		if (newshader.isCompiled())
			shader = newshader;
		else if (shader==null)
			throw new GdxRuntimeException("Could not compile shader: "+newshader.getLog());
		
		//print any warnings
		if (shader.getLog().length()!=0)
			System.out.println(shader.getLog());
		
		//setup default uniforms
		shader.begin();
		//our normal map
		shader.setUniformi("u_normals", 1); //GL_TEXTURE1
		shader.end();
	}
}
