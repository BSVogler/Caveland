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

package com.bombinggames.wurfelengine.core.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.WorkingDirectory;
import com.bombinggames.wurfelengine.core.cvar.CVarSystemMap;
import java.io.File;

/**
 *A button which creates a new save if you click on it
 * @author Benedikt Vogler
 */
public class MapButton extends TextButton {

    /**
     *
     * @param fileName fileName of map
     */
    public MapButton(String fileName) {
        super("",WE.getEngineView().getSkin());
        //read description and map name
        setColor(Color.LIGHT_GRAY.cpy());
        setName(fileName);
        setSize(150, 50);
		CVarSystemMap cvars = new CVarSystemMap(
			new File(WorkingDirectory.getMapsFolder()+"/"+fileName+"/meta.wecvar")
		);
		cvars.load();
		String mapname;
		try {
			mapname = cvars.getValueS("mapname");
		} catch (NullPointerException ex) {
			mapname = "no map name set";
		}
		setText("/"+fileName+"/ "+mapname);
		
		String description;
		try {
			description = cvars.getValueS("description");
		} catch (NullPointerException ex) {
			description = "";
		}
		
		if (!"".equals(description))
			add(description);
		else{
			add("no description found");
		}
		
//        } catch (IOException ex) {
//            setText("/"+fileName+"/ Error reading file");
//            setColor(Color.GRAY.cpy());
//        }
        addListener(new ButtonChangeListener(this));
    }

    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        WE.getEngineView().getFont().setColor(Color.GRAY.cpy());
        super.draw(batch, parentAlpha);
                
        //missing: background
        //selection?
    }

    private static class ButtonChangeListener extends ChangeListener {
        private final MapButton parent;

        protected ButtonChangeListener(MapButton parent) {
            this.parent = parent;
        }

        @Override
        public void changed(ChangeListener.ChangeEvent event, Actor actor) {
			int slot = Map.newSaveSlot(new File(WorkingDirectory.getMapsFolder()+"/"+parent.getName()));
            Controller.loadMap(
				new File(WorkingDirectory.getMapsFolder()+"/"+parent.getName()),
				slot
			);
        }
    }
    
    
}
