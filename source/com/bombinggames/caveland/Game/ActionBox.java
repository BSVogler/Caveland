package com.bombinggames.caveland.Game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A dialog which can fucntion as a selection tool or a message.
 *
 * @author Benedikt Vogler
 */
public class ActionBox extends WidgetGroup {

	/**
	 * contains the content of the window
	 */
	private final Window window;
	private final BoxModes mode;
	private ArrayList<String> selectionNames;
	private int selection;
	private String text;
	private ActionBoxConfirmAction confirmAction;
	private ActionBoxCancelAction cancelAction;
	private ActionBoxSelectAction selectAction;
	private boolean closed = false;


	public static enum BoxModes {

		/**
		 * select from yes or no
		 */
		BOOLEAN(),
		/**
		 * Select from various entries
		 */
		SELECTION(),
		/**
		 * Just a message to confirm
		 */
		SIMPLE(),
		/**
		 * reserved for later use
		 */
		CUSTOM();
	}

	/**
	 * creates a new chat box and set this as a modal window
	 *
	 * @param title the title of the box
	 * @param text the text of the box. can be null
	 * @param mode
	 */
	public ActionBox(final String title, final BoxModes mode, final String text) {
		this.mode = mode;

		window = new Window(title, WE.getEngineView().getSkin());
		window.setWidth(600);
		window.setHeight(200);
		window.align(Align.center);
		addActor(window);
		if (text != null) {
			this.text = text;
			Label textArea = new Label(text, WE.getEngineView().getSkin());
			textArea.setX(10);
			textArea.setWrap(true);
			textArea.setWidth(580);
			textArea.setHeight(180);
			window.addActor(textArea);
		}

		TextureAtlas.AtlasRegion sprite = AbstractGameObject.getSprite('i', (byte) 23, (byte) 0);
		if (sprite != null) {
			Image confirm = new Image(new TextureRegionDrawable(sprite));
			confirm.setPosition(window.getWidth() - 50, -40);
			addActor(confirm);
		}
		Label confirmLabel = new Label("Confirm", WE.getEngineView().getSkin());
		confirmLabel.setPosition(window.getWidth() - 50, -40);
		addActor(confirmLabel);
		
		if (mode != BoxModes.SIMPLE) {
			sprite = AbstractGameObject.getSprite('i', (byte) 23,(byte) 2);
			if (sprite != null) {
				Image cancel = new Image(new TextureRegionDrawable(sprite));
				cancel.setPosition(window.getWidth() - 250, -40);
				addActor(cancel);
			}
			Label cancelLabel = new Label("Cancel", WE.getEngineView().getSkin());
			cancelLabel.setPosition(window.getWidth() - 250, -40);
			addActor(cancelLabel);
		}
	}

	@Override
	public float getWidth() {
		return window.getWidth();
	}
	
	@Override
	public float getHeight() {
		return window.getHeight();
	}
	
	/**
	 * registeres the window as a modal box so that the input gets redirected to
	 * it.
	 *
	 * @param view
	 * @param playerId starting with 1
	 * @param actor the entitiy which is connected to the dialogue. can be null
	 * @return
	 */
	public ActionBox register(final CLGameView view, final int playerId, AbstractEntity actor) {
		view.setModalDialogue(this, playerId);
		if (selectAction != null) {
			selectAction.select(false, selection, actor);
		}
		return this;
	}

	public Window getWindow() {
		return window;
	}

	public BoxModes getMode() {
		return mode;
	}

	/**
	 * Set the command which should be triggered once you confirm the dialogue.
	 *
	 * @param action
	 * @return itself for chaining
	 */
	public ActionBox setConfirmAction(ActionBoxConfirmAction action) {
		this.confirmAction = action;
		return this;
	}

	/**
	 * Set the command which should be triggered once you confirm the dialogue.
	 *
	 * @param action
	 * @return itself for chaining
	 */
	public ActionBox setCancelAction(ActionBoxCancelAction action) {
		this.cancelAction = action;
		return this;
	}

	/**
	 * Set the action what should ahppen if you select something before
	 * confirming it.
	 *
	 * @param hoverAction new value of selectAction
	 * @return itself for chaining
	 */
	public ActionBox setSelectAction(ActionBoxSelectAction hoverAction) {
		this.selectAction = hoverAction;
		return this;
	}

	/**
	 * unregisters this window
	 *
	 * @param actor
	 * @return
	 */
	public int confirm(AbstractEntity actor) {
		int selectionNum = 0;
		if (mode == BoxModes.SELECTION) {
			selectionNum = selection;
		}
		remove();
		WE.SOUND.play("menuConfirm");
		closed = true;
		if (confirmAction != null) {
			confirmAction.confirm(selectionNum, actor);
		}
		return selectionNum;
	}

	/**
	 * unregisters this window
	 *
	 * @param actor
	 * @return
	 */
	public int cancel(AbstractEntity actor) {
		int selectionNum = 0;
		if (mode == BoxModes.SELECTION) {
			selectionNum = selection;
		}
		remove();
		closed = true;
		WE.SOUND.play("menuAbort");
		if (cancelAction != null) {
			cancelAction.cancel(selectionNum, actor);
		}
		return selectionNum;
	}

	/**
	 * Adds strings as options.
	 *
	 * @param options
	 * @return itself for chaining
	 */
	public ActionBox addSelectionNames(String... options) {
		if (mode == BoxModes.SELECTION) {
			if (selectionNames == null) {
				selectionNames = new ArrayList<>(options.length);
			}
			selectionNames.addAll(Arrays.asList(options));
			updateContent();
		}
		return this;
	}

	/**
	 * Adds a list of strings as options.
	 *
	 * @param options
	 */
	public void addSelectionNames(Collection<String> options) {
		if (mode == BoxModes.SELECTION) {
			if (selectionNames == null) {
				selectionNames = new ArrayList<>(options.size());
			}
			selectionNames.addAll(options);
			updateContent();
		}
	}

	/**
	 * go a selection downwards
	 * @param actor
	 */
	public void down(AbstractEntity actor) {
		if (mode == BoxModes.SELECTION) {
			if (selection < selectionNames.size() - 1) {
				selection++;
			}
			WE.SOUND.play("menuSelect");
			updateContent();
		}
		if (mode == BoxModes.SELECTION || mode == BoxModes.CUSTOM) {
			if (selectAction != null) {
				selectAction.select(false, selection, actor);
			}
		}
	}

	/**
	 * go a selection upwards
	 * @param actor
	 */
	public void up(AbstractEntity actor) {
		if (mode == BoxModes.SELECTION) {
			if (selection > 0) {
				selection--;
			}
			WE.SOUND.play("menuSelect");
			updateContent();
		}
		if (mode == BoxModes.SELECTION || mode == BoxModes.CUSTOM) {
			if (selectAction != null) {
				selectAction.select(true, selection, actor);
			}
		}
	}

	/**
	 * clears window content then add new
	 */
	private void updateContent() {
		window.clear();
		window.add(text);
		window.row();
		//adds every selection
		int max = selectionNames.size();
		if (max > 4) {
			max = 4;
		}
		for (int i = 0; i < max; i++) {
			String entry = selectionNames.get(i);
			if (selection == i) {
				window.add(new Label("[" + entry + "]", WE.getEngineView().getSkin()));
			} else {
				window.add(new Label(entry, WE.getEngineView().getSkin()));
			}
			if (selectionNames.size() > 4) {
				entry = selectionNames.get(i + 4);
				if (selection == i + 4) {
					window.add(new Label("[" + entry + "]", WE.getEngineView().getSkin()));
				} else {
					window.add(new Label(entry, WE.getEngineView().getSkin()));
				}
			}
			window.row();
		}
		//window.pack();
	}

	boolean closed() {
		return closed;
	}
	
	@FunctionalInterface
	public interface ActionBoxCancelAction {

		/**
		 *
		 * @param result
		 * @param actor can be null
		 */
		public void cancel(int result, AbstractEntity actor);
	}

	/**
	 * Actions which are called if you close the window by confirming
	 *
	 * @author Benedikt Vogler
	 */
	@FunctionalInterface
	public interface ActionBoxConfirmAction {

		/**
		 *
		 * @param result the number of the selection
		 * @param actor can be null
		 */
		public void confirm(int result, AbstractEntity actor);

	}

	@FunctionalInterface
	public interface ActionBoxSelectAction {

		/**
		 *
		 * @param up
		 * @param result if a selection box returns the selection, else is -1
		 * @param actor can be null
		 */
		public void select(boolean up, int result, AbstractEntity actor);
	}

}
