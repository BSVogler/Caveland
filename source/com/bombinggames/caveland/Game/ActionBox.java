package com.bombinggames.caveland.Game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author Benedikt Vogler
 */
public class ActionBox extends WidgetGroup {
	/**
	 * contains the content of the window
	 */
	private final Window window;
	private BoxModes mode;
	private final Image confirm;
	private Image cancel;
	private ArrayList<String> selectionOptions;
	private String text;
	
	public static enum BoxModes {
		/**
		 *select from yes or no
		 */
		BOOLEAN(),
		/**
		 * Select from various entries
		 */
		SELECTION(),
		/**
		 *Just a message to confirm
		 */
		SIMPLE(),
		
		/**
		 * reserved for later use
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
			this.text=text;
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
	
	/**
	 * 
	 * @param options
	 */
	public void addSelectionOptions(String... options){
		if (mode==BoxModes.SELECTION){
			if (selectionOptions==null)
				selectionOptions = new ArrayList<>(4);
			selectionOptions.addAll(Arrays.asList(options));
			//clear window content then add new
			window.clear();
			Label textArea = new Label(text, WE.getEngineView().getSkin());
			textArea.setX(10);
			textArea.setWrap(true);
			textArea.setWidth(580);
			textArea.setHeight(30);
			window.addActor(textArea);
			for (String entry : selectionOptions) {
				Label label = new Label(entry, WE.getEngineView().getSkin());
				label.setX(10);
				label.setWidth(580);
				window.add(label);
			}
		}
	}
	
	/**
	 * go a selection upwards
	 */
	public void down(){
	
	}
	
	/**
	 * go a selection downwards
	 */
	public void up(){
	
	}
	
}
