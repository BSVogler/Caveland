package com.BombingGames.Caveland.Game;

import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

/**
 *
 * @author Benedikt Vogler
 */
public class ChatBox extends Window {
	private String text;

	public ChatBox(Stage stage, String title, String text) {
		super(title, WE.getEngineView().getSkin());
		setPosition(stage.getWidth()/2, stage.getHeight());
		setWidth(600);
		setHeight(200);
		addActor(new Label(text, WE.getEngineView().getSkin()));
	}
	
}
