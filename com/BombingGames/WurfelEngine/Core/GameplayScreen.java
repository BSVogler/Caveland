package com.BombingGames.WurfelEngine.Core;

import com.BombingGames.WurfelEngine.Core.Loading.LoadingController;
import com.BombingGames.WurfelEngine.Core.Loading.LoadingScreen;
import com.BombingGames.WurfelEngine.WEMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

/**
 * The GameplayScreen State. This is state where the magic happens.
 * @author Benedikt
 */
public class GameplayScreen implements Screen{ 
  /**
     * Contains the Message System
     */
    private static MsgSystem msgSystem;    
    
    private View view = null;
    private Controller controller = null;
    private final LoadingController loadingController;
    
    /**
     * Create the gameplay state.
     * @param controller The controller of this screen.
     * @param view  The view of this screen.
     */
    public GameplayScreen(Controller controller, View view) {
        Gdx.app.log("GameplayScreen", "Initializing");
        msgSystem = new MsgSystem(Gdx.graphics.getWidth()/2, 3*Gdx.graphics.getHeight()/4);

        loadingController = new LoadingController();
        loadingController.init(WEMain.getInstance().manager);

        WEMain.getInstance().setScreen(new LoadingScreen(loadingController));
        
        this.controller = controller;
        this.view = view;
    }
             

    /**
     * Returns the Message System. Use .add() to add messages to the msgSystem.
     * @return The msgSystem.
     */
    public static MsgSystem msgSystem() {
        return msgSystem;
    }

    /**
     *
     * @return
     */
    public View getView() {
        return view;
    }

    /**
     *
     * @return
     */
    public Controller getController() {
        return controller;
    }
    
    

    @Override
    public void render(float delta) {
        controller.update(delta*1000);
        view.render();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.graphics.setTitle("Wurfelengine V" + WEMain.VERSION + " " + Gdx.graphics.getWidth() + "x"+Gdx.graphics.getHeight());
    }

    @Override
    public void show() {
        this.controller.init();
        this.view.init(controller);

    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        controller.dispose();
    }
    
}