package com.BombingGames.Caveland.Game;

import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

/**
 *
 * @author Benedikt Vogler
 */
public class ChatBox extends Window {
	private String text;

	public ChatBox(String text) {
		super("Person who speaks", WE.getEngineView().getSkin());
		
		addActor(new Label(text, WE.getEngineView().getSkin()));
	}
	
}
