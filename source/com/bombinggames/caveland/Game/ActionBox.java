package com.bombinggames.caveland.Game;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
/**
 *
 * @author Benedikt Vogler
 */
public class ActionBox extends WidgetGroup {
	/**
	 * contains the content of the window
	 */
	private final Window window;
	private final BoxModes mode;
	private final Image confirm;
	private Image cancel;
	private ArrayList<String> selectionNames;
	private int selection;
	private String text;
	private final CustomGameView view;
	private int playerNum;
	private ActionBoxConfirmAction confirmAction;
	private ActionBoxCancelAction cancelAction;
	
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
	 * creates a new chat box and set this as a modal window
	 * @param view
	 * @param title the title of the box
	 * @param text the text of the box. can be null
	 * @param mode 
	 */
	public ActionBox(final CustomGameView view, final String title, final BoxModes mode, final String text) {
		this.view = view;
		this.mode = mode;
		
		setPosition(view.getStage().getWidth()/2, view.getStage().getHeight()/2);
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
	
	/**
	 * registeres
	 * @param view 
	 * @param playerId starting with 1
	 * @return 
	 */
	public ActionBox register(final CustomGameView view, final int playerId){
		this.playerNum = playerId;
		view.setModalDialogue(this, playerId);
		view.getStage().addActor(this);
		return this;
	}

	public Window getWindow() {
		return window;
	}

	public BoxModes getMode() {
		return mode;
	}
	
	public ActionBox setConfirmAction(ActionBoxConfirmAction action){
		this.confirmAction = action;
		return this;
	}
	/**
	 * unregisters this window
	 * @param view
	 * @param actor
	 * @return 
	 */
	public int confirm(CustomGameView view, AbstractEntity actor){
		int selectionNum = 0;
		if (mode==BoxModes.SELECTION){
			selectionNum = selection;
		}
		remove();
		view.setModalDialogue(null, playerNum);
		if (confirmAction != null)
			confirmAction.confirm(selectionNum, view, actor);
		return selectionNum;
	}
	
	/**
	 * unregisters this window
	 * @param view
	 * @param actor
	 * @return 
	 */
	public int cancel(CustomGameView view, AbstractEntity actor){
		int selectionNum = 0;
		if (mode==BoxModes.SELECTION){
			selectionNum = selection;
		}
		remove();
		view.setModalDialogue(null, playerNum);
		if (cancelAction != null)
			cancelAction.cancel(selectionNum, view, actor);
		return selectionNum;
	}
	
	/**
	 * Adds strings as options.
	 * @param options
	 * @return 
	 */
	public ActionBox addSelectionNames(String... options){
		if (mode==BoxModes.SELECTION){
			if (selectionNames == null)
				selectionNames = new ArrayList<>(options.length);
			selectionNames.addAll(Arrays.asList(options));
			updateContent();
		}
		return this;
	}
	
	/**
	 * Adds a list of strings as options.
	 * @param options
	 */
	public void addSelectionNames(Collection<String> options){
		if (mode==BoxModes.SELECTION){
			if (selectionNames == null)
				selectionNames = new ArrayList<>(options.size());
			selectionNames.addAll(options);
			updateContent();
		}
	}
	
	/**
	 * go a selection downwards
	 */
	public void down(){
		if (mode == BoxModes.SELECTION) {
			if (selection > 0){
				selection++;
			}
			updateContent();
		}
	}
	
	/**
	 * go a selection upwards
	 */
	public void up(){
		if (mode == BoxModes.SELECTION) {
			if (selection<selectionNames.size()-1){
				selection--;
			}
			updateContent();
		}
	}
	
	/**
	 * clears window content then add new
	 */
	private void updateContent(){
		window.clear();
		Label textArea = new Label(text, WE.getEngineView().getSkin());
		textArea.setX(10);
		textArea.setWrap(true);
		textArea.setWidth(window.getWidth());
		textArea.setHeight(30);
		window.addActor(textArea);
		for (int i = 0; i < selectionNames.size(); i++) {
			String entry = selectionNames.get(i);
			Label label;
			if (selection==i)
				label= new Label("["+entry+"]", WE.getEngineView().getSkin());
			else 
				label= new Label(entry, WE.getEngineView().getSkin());
			label.setX(10);
			label.setY(window.getHeight()-50-i*20);
			label.setWidth(window.getWidth());
			window.add(label);
		}
	}
	
	@FunctionalInterface
	public interface ActionBoxCancelAction {
		public int cancel(int result, CustomGameView view, AbstractEntity actor);
	}
	
	/**
	 *Actions which are called if you close the window
	 * @author Benedikt Vogler
	 */
	@FunctionalInterface
	public interface ActionBoxConfirmAction {

		public int confirm(int result, CustomGameView view, AbstractEntity actor);

	}
	
}
