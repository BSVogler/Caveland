package com.BombingGames.WurfelEngine;

import com.BombingGames.WurfelEngine.Core.BasicMainMenu.BasicMainMenu;
import com.BombingGames.WurfelEngine.Core.BasicMainMenu.BasicMenuItem;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.View;

/**
 *A test class for starting the engine.
 * @author Benedikt Vogler
 */
public class DesktopLauncher {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WE.construct("Wurfelengine V" + WE.VERSION, args);
        
        BasicMenuItem[] menuItems = new BasicMenuItem[]{
            new BasicMenuItem(0, "Load Map", Controller.class, View.class, new Configuration()),
            new BasicMenuItem(1, "Options"),
            new BasicMenuItem(2, "Exit")
        };   
        
        WE.setMainMenu(new BasicMainMenu(menuItems));
        WE.launch();  
    }
    
}
