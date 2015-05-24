package com.bombinggames.caveland.Game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bombinggames.wurfelengine.Core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.WE;
/**
 *
 * @author Benedikt Vogler
 */
public class ActionBox extends Window {
	private String text;
	private BoxModes mode;
	
	
	private static enum BoxModes {
		/**
		 *select from yes or no
		 */
		BOOLEAN(),
		/**
		 * Selectr from various entries
		 */
		SELECTION(),
		/**
		 *Just a message to confirm
		 */
		SIMPLE();
	}

	/**
	 * creates a new chat box
	 * @param stage
	 * @param title the title of the box
	 * @param text the text of the box
	 * @param mode 
	 */
	public ActionBox(Stage stage, String title, String text, BoxModes mode) {
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
		
		Image confirm = new Image(new TextureRegionDrawable(AbstractGameObject.getSprite('e', 23, 0)));
		confirm.setPosition(60, 50);
		addActor(confirm);
		
		if (mode!=BoxModes.SIMPLE){
			Image cancel = new Image(new TextureRegionDrawable(AbstractGameObject.getSprite('e', 23, 2)));
			cancel.setPosition(10, 50);
			addActor(cancel);
		}
	}
	
}
