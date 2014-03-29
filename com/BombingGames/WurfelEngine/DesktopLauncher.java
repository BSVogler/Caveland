package com.BombingGames.WurfelEngine;

import com.BombingGames.WurfelEngine.Core.BasicMainMenu.BasicMainMenu;
import com.BombingGames.WurfelEngine.Core.BasicMainMenu.GameControllerWithCamera;
import com.BombingGames.WurfelEngine.Core.BasicMainMenu.BasicMenuItem;
import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.MapEditor.MapEditorController;
import com.BombingGames.WurfelEngine.MapEditor.MapEditorView;

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
            new BasicMenuItem(0, "Load Map", GameControllerWithCamera.class, View.class, new Configuration()),
            new BasicMenuItem(1, "Map Editor", MapEditorController.class, MapEditorView.class, new Configuration()),
            new BasicMenuItem(2, "Options"),
            new BasicMenuItem(3, "Exit")
        };   
        
        WE.setMainMenu(new BasicMainMenu(menuItems));
        WE.launch();  
    }
    
}
