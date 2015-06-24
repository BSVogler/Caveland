/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.bombinggames.wurfelengine.core.BasicMainMenu;

import com.bombinggames.wurfelengine.core.AbstractMainMenu;

/**
 *This class provides a simple basic main menu if you don't want to set up your own and just want to test your game controllers.
 * @author Benedikt Vogler
 */
public class BasicMainMenu extends AbstractMainMenu {
 
    private static MenuView view;
    private static MenuController controller;
    private final BasicMenuItem[] menuItems;
    private boolean warning = true;

    /**
     * Use this constructor to pass your controller and views in order of the main menu
     * @param menuItems

     */
    public BasicMainMenu(BasicMenuItem[] menuItems) {
        this.menuItems = menuItems;
    }
    
    
    @Override
    public void init(){
        controller = new MenuController(menuItems); 
        view = new MenuView(controller);
    }

    @Override
    public void renderImpl(float dt) {
        controller.update(dt);
        view.update(dt);
        view.render(warning);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        controller.show();
        view.show();
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
    public static MenuController getController() {
        return controller;
    }

    /**
     * 
     * @return
     */
    public static MenuView getView() {
        return view;
    }
    
    /**
     *If you want to hide the warning message call this method.
     */
    public void supressWarning(){
        warning = false;
    }
}
