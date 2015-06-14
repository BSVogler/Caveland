package com.bombinggames.caveland.Game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.WE;
/**
 *
 * @author Benedikt Vogler
 */
public class ActionBox extends WidgetGroup {
	private Window window;
	private String text;
	private BoxModes mode;
	private final Image confirm;
	private Image cancel;
	
	
	public static enum BoxModes {
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
		SIMPLE(),
		
		/**
		 * 
		 */
		CUSTOM();
	}

	/**
	 * creates a new chat box
	 * @param stage
	 * @param title the title of the box
	 * @param text the text of the box. can be null
	 * @param mode 
	 */
	public ActionBox(Stage stage, String title, BoxModes mode, String text) {
		setPosition(stage.getWidth()/2, stage.getHeight()/2);
		window = new Window(title, WE.getEngineView().getSkin());
		window.setWidth(600);
		window.setHeight(200);
		addActor(window);
		if (text!=null) {
			Label textArea = new Label(text, WE.getEngineView().getSkin());
			textArea.setX(10);
			textArea.setWrap(true);
			textArea.setWidth(580);
			textArea.setHeight(180);
			window.addActor(textArea);
		}
		
		confirm = new Image(new TextureRegionDrawable(AbstractGameObject.getSprite('e', 23, 0)));
		confirm.setPosition(window.getWidth()-50, -40);
		addActor(confirm);
		
		if (mode != BoxModes.SIMPLE){
			cancel = new Image(new TextureRegionDrawable(AbstractGameObject.getSprite('e', 23, 2)));
			cancel.setPosition(window.getWidth()-250, -40);
			addActor(cancel);
		}
	}

	public Window getWindow() {
		return window;
	}

	public BoxModes getMode() {
		return mode;
	}
	
	public void confirm(){
		remove();
	}
	
	public void cancel(){
		remove();
	}
	
}
