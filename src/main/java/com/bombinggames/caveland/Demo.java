package com.bombinggames.caveland;

import static com.bombinggames.caveland.Caveland.VERSION;
import com.bombinggames.caveland.game.CLGameController;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.caveland.mainmenu.CustomLoading;
import com.bombinggames.wurfelengine.WE;

/**
 *
 * @author Benedikt S. Vogler
 */
public class Demo {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Caveland.configureEngine();
		//WE.getCVars().register(cvar, VERSION);
		WE.getCVars().get("ignorePlayer").setValue(true);
		WE.addPostLaunchCommands(() -> {
			CLGameController controller = new CLGameController();
			controller.setMapName("demo");
			controller.useSaveSlot(0);
			WE.initAndStartGame(controller, new CLGameView(), new CustomLoading());
		});

		WE.launch("Caveland Dome " + VERSION, args);
	}

}
