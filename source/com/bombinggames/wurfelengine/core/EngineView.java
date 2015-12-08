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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.bombinggames.wurfelengine.MapEditor.EditorToggler;
import com.bombinggames.wurfelengine.WE;

/**
 * A view which is not dependend on the currently active game. Singleton.
 * @author Benedikt Vogler
 * @since 1.2.26
 */
public class EngineView extends GameView {//is GameView so it can render in game space
    private BitmapFont font;
    private Skin skin;
    private Pixmap cursor;
    private InputMultiplexer inpMulPlex;
    private Array<InputProcessor> inactiveInpProcssrs;
	private Pixmap cursorDrag;
	private Pixmap cursorPointer;
	private int cursorId;
	private OrthographicCamera camera;
    private EditorToggler editorToggler;

	@Override
	public void init(Controller controller, GameView oldView) {
		super.init(controller, oldView);
		Gdx.app.debug("EngineView","Initializing...");
        //set up font
        //font = WurfelEngine.getInstance().manager.get("com/bombinggames/wurfelengine/EngineCore/arial.fnt"); //load font
        font = new BitmapFont(false);
        //font.scale(2);

        font.setColor(Color.GREEN);
        //font.scale(-0.5f);
        
        //load sprites
        Gdx.input.setInputProcessor(getStage());

        skin = new Skin(Gdx.files.internal("com/bombinggames/wurfelengine/core/skin/uiskin.json"));
        
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
        getSpriteBatch().setProjectionMatrix(camera.combined);
		getShapeRenderer().setProjectionMatrix(camera.combined);
		
		editorToggler = new EditorToggler();
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		editorToggler.setVisible(WE.getCvars().getValueB("editorVisible"));
		editorToggler.update(this, dt);
	}
	
    /**
     * Resets the input processors.
     */
    public void resetInputProcessors() {
        Gdx.input.setInputProcessor(getStage());
        inpMulPlex = null;
        inactiveInpProcssrs = null;
        addInputProcessor(getStage());
    }
    
    /**
     * Add an inputProcessor to the views.
     * @param processor 
     */
    public void addInputProcessor(final InputProcessor processor){
        inpMulPlex = new InputMultiplexer(Gdx.input.getInputProcessor());
        inpMulPlex.addProcessor(processor);
        Gdx.input.setInputProcessor(inpMulPlex);
    }
    
    /**
     * Deactivates every input processor but one.
     * @param processor the processor you want to "filter"
     * @see #unfocusInputProcessor() 
     * @since V1.2.21
     */
    public void focusInputProcessor(final InputProcessor processor){
        inactiveInpProcssrs = inpMulPlex.getProcessors();//save current ones
        Gdx.input.setInputProcessor(getStage()); //reset
        addInputProcessor(processor);//add the focus
    }
    
    /**
     * Reset that every input processor works again.
     * @see #focusInputProcessor(com.badlogic.gdx.InputProcessor)
     * @since V1.2.21
     */
    public void unfocusInputProcessor(){
        Gdx.app.debug("View", "There are IPs: "+inactiveInpProcssrs.toString(","));
        Gdx.input.setInputProcessor(getStage()); //reset
        for (InputProcessor ip : inactiveInpProcssrs) {
            addInputProcessor(ip);
        }
    }
    
    /**
     * 
     * @return
     */
    public BitmapFont getFont() {
        return font;
    }
    
    /**
     *
     * @return
     */
    public Skin getSkin() {
        return skin;
    }
	
    /**
     * 
	 * @param id 0 default, 1 pointer, 2 drag 
     */
    public void setCursor(int id) {
		cursorId = id;
		if (id==0) {
			if (cursor == null)
				cursor = new Pixmap(Gdx.files.internal("com/bombinggames/wurfelengine/core/images/cursor.png"));
			Gdx.input.setCursorImage(cursor, 8, 8);
		} else if (id==1) {
			if (cursorPointer==null)
				cursorPointer = new Pixmap(Gdx.files.internal("com/bombinggames/wurfelengine/core/images/wecursor.png"));
			Gdx.input.setCursorImage(cursorPointer, 0, 0);
		} else if (id==2) {
			if (cursorPointer==null)
				cursorDrag = new Pixmap(Gdx.files.internal("com/bombinggames/wurfelengine/core/images/cursor_drag.png"));
			Gdx.input.setCursorImage(cursorDrag, 0, 0);
		} else {
			Gdx.input.setCursorImage(cursor, 0, 0);
		}
    }

	/**
	 * returns the current cursor
	 * @return 0 default, 1 pointer, 2 drag 
	 */
	public int getCursor() {
		return cursorId;
	}

	public EditorToggler getEditorToggler() {
		return editorToggler;
	}

	@Override
	public void onEnter() {
	}
}
