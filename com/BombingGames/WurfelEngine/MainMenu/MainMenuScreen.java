package com.BombingGames.WurfelEngine.MainMenu;

import com.badlogic.gdx.Screen;
 
/**
 * The game state of the Main Menu.
 * @author Benedikt
 */
public class MainMenuScreen implements Screen{
    private static boolean loadMap = false;
 
    private static View view;
    private static Controller controller;
    
    /**
     * Creates the main Menu
     */
    public MainMenuScreen() {
        controller = new Controller(); 
        view = new View();
    }

    
    @Override
    public void render(float delta) {
        controller.update((int) (delta*1000));
        view.render(controller);
        view.update(delta*1000);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
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
    }
  
    /**
     * 
     * @return
     */
    public static Controller getController() {
        return controller;
    }

    /**
     * 
     * @return
     */
    public static View getView() {
        return view;
    }

    /**
     * 
     * @return
     */
    public static boolean shouldLoadMap() {
        return loadMap;
    }

    /**
     * 
     * @param loadmap
     */
    public static void setLoadMap(boolean loadmap) {
        MainMenuScreen.loadMap = loadmap;
    }
}