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
package com.BombingGames.WurfelEngine.MapEditor;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 *
 * @author Benedikt Vogler
 */
public class ToolSelection {
	private int selectionLeft = 0;
	private int selectionRight = 0;
	
	private int leftPos;
	private int bottomPos;
	
	private final Image[] items =new Image[3]; 

	public ToolSelection(Stage stage, TextureAtlas sprites) {
		items[0] = new Image(sprites.findRegion("draw_button"));
		items[1] = new Image(sprites.findRegion("bucket_button"));
		items[2] = new Image(sprites.findRegion("draw_button"));
		leftPos = (int) (stage.getWidth()/2-50);
		bottomPos = (int) (stage.getHeight()-100);
		
		for (int i = 0; i < items.length; i++) {
			items[i].setPosition(leftPos+i*50, bottomPos);
			items[i].addListener(new ToolSelectionListener(i));
			stage.addActor(items[i]);
		}
	}
	
	
	
	public void render(ShapeRenderer shR){
		shR.begin(ShapeRenderer.ShapeType.Line);
		//draw left
		shR.setColor(Color.GREEN);
		shR.rect(items[selectionLeft].getX(), items[selectionLeft].getY(), 50, 50);
				//draw right
		shR.setColor(Color.BLUE);
		shR.rect(items[selectionRight].getX(), items[selectionLeft].getY(), 50, 50);

		shR.end();
	}

	/**
	 * index of left mouse button.
	 * @return 
	 */
	public int getSelectionLeft() {
		return selectionLeft;
	}

	/**
	 * index of right mouse button.
	 * @return 
	 */
	public int getSelectionRight() {
		return selectionRight;
	}

	private class ToolSelectionListener extends InputListener {
		private final int index;
	
		ToolSelectionListener(int index) {
			this.index = index;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			if (button==Buttons.LEFT) selectionLeft = index;
			else if (button==Buttons.RIGHT) selectionRight = index;
			return true;
		}

		
	}
}
