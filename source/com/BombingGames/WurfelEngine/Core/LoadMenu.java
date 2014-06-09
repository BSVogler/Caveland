/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 * 
 * Copyright 2014 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
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

package com.BombingGames.WurfelEngine.Core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import java.io.File;

/**
 *A menu for choosing a map.
 * @author Benedikt Vogler
 */
public class LoadMenu {
    private Window window;
    private final static int margin = 100;

    public void viewInit(View view){
        window = new Window("Choose a map", view.getSkin());
        window.setWidth(Gdx.graphics.getWidth()-margin*2);
        window.setHeight(Gdx.graphics.getHeight()-margin*2);
        window.setKeepWithinStage(true);
        window.setModal(true);
        window.setVisible(false);
        view.getStage().addActor(window);
    }
    
    public boolean isOpen() {
       return window.isVisible();
    }

    public void setOpen(boolean open) {
        window.setVisible(open);

        if (open){
            int i=0;
            File wd = WorkingDirectory.getWorkingDirectory("Wurfel Engine");
            for (final File fileEntry : wd.listFiles()) {
                if (fileEntry.isDirectory()) {
                    window.add(fileEntry.getName());
                    i++;
                    //listFilesForFolder(fileEntry);
                } else {
                    //System.out.println(fileEntry.getName());
                }
            }
        }
    }
}
