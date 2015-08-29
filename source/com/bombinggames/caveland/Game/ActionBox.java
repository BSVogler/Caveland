package com.bombinggames.caveland.Game;

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
	private final Image confirm;
	private Image cancel;
	private ArrayList<String> selectionNames;
	private int selection;
	private String text;
	private final CustomGameView view;
	private int playerNum;
	private ActionBoxConfirmAction confirmAction;
	private ActionBoxCancelAction cancelAction;
	private ActionBoxSelectAction selectAction;

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
	 * @param view
	 * @param title the title of the box
	 * @param text the text of the box. can be null
	 * @param mode
	 */
	public ActionBox(final CustomGameView view, final String title, final BoxModes mode, final String text) {
		this.view = view;
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

		confirm = new Image(new TextureRegionDrawable(AbstractGameObject.getSprite('e', 23, 0)));
		confirm.setPosition(window.getWidth() - 50, -40);
		addActor(confirm);

		if (mode != BoxModes.SIMPLE) {
			cancel = new Image(new TextureRegionDrawable(AbstractGameObject.getSprite('e', 23, 2)));
			cancel.setPosition(window.getWidth() - 250, -40);
			addActor(cancel);
		}
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
	public ActionBox register(final CustomGameView view, final int playerId, AbstractEntity actor) {
		this.playerNum = playerId;
		view.setModalDialogue(this, playerId);
		if (selectAction != null) {
			selectAction.select(selection, view, actor);
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
	 * @param view
	 * @param actor
	 * @return
	 */
	public int confirm(CustomGameView view, AbstractEntity actor) {
		int selectionNum = 0;
		if (mode == BoxModes.SELECTION) {
			selectionNum = selection;
		}
		remove();
		view.setModalDialogue(null, playerNum);
		if (confirmAction != null) {
			confirmAction.confirm(selectionNum, view, actor);
		}
		return selectionNum;
	}

	/**
	 * unregisters this window
	 *
	 * @param view
	 * @param actor
	 * @return
	 */
	public int cancel(CustomGameView view, AbstractEntity actor) {
		int selectionNum = 0;
		if (mode == BoxModes.SELECTION) {
			selectionNum = selection;
		}
		remove();
		view.setModalDialogue(null, playerNum);
		if (cancelAction != null) {
			cancelAction.cancel(selectionNum, view, actor);
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
	 */
	public void down(CustomGameView view, AbstractEntity actor) {
		if (mode == BoxModes.SELECTION) {
			if (selection < selectionNames.size() - 1) {
				selection++;
			}
			if (selectAction != null) {
				selectAction.select(selection, view, actor);
			}
			updateContent();
		}
	}

	/**
	 * go a selection upwards
	 */
	public void up(CustomGameView view, AbstractEntity actor) {
		if (mode == BoxModes.SELECTION) {
			if (selection > 0) {
				selection--;
			}
			if (selectAction != null) {
				selectAction.select(selection, view, actor);
			}
			updateContent();
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

	@FunctionalInterface
	public interface ActionBoxCancelAction {

		/**
		 *
		 * @param result
		 * @param view
		 * @param actor can be null
		 */
		public void cancel(int result, CustomGameView view, AbstractEntity actor);
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
		 * @param view
		 * @param actor can be null
		 */
		public void confirm(int result, CustomGameView view, AbstractEntity actor);

	}

	@FunctionalInterface
	public interface ActionBoxSelectAction {

		/**
		 *
		 * @param result
		 * @param view
		 * @param actor can be null
		 */
		public void select(int result, CustomGameView view, AbstractEntity actor);
	}

}
