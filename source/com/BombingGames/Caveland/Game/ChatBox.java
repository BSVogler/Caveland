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

	/**
	 * creates a new chat box
	 * @param stage
	 * @param title
	 * @param text 
	 */
	public ChatBox(Stage stage, String title, String text) {
		super(title, WE.getEngineView().getSkin());
		setPosition(stage.getWidth()/2, stage.getHeight());
		setWidth(600);
		setHeight(200);
		Label textArea = new Label(text, WE.getEngineView().getSkin());
		textArea.setX(10);
		textArea.setWrap(true);
		textArea.setWidth(580);
		textArea.setHeight(180);
		addActor(textArea);
	}
	
}
