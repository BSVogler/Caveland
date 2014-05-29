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

import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import java.util.ArrayList;

/**
 *The message system can manage&show messages (Msg).
 * @author Benedikt
 */
public class MsgSystem {
    private int timelastupdate = 0;
    private Stage stage;
    private final GameplayScreen gameplay;
    private boolean active = false;
    private final TextField textinput;
    private final ArrayList<Msg> messages = new ArrayList<Msg>(20);  
    
    /**
     * A message is put into the MsgSystem. It contains the message, the sender and the importance.
     * @author Benedikt
     */
    private class Msg {
        private final String message;
        private String sender = "System";
        private int importance = 1;


        protected Msg(String pmessage, String psender, int imp) {
            message = pmessage;
            sender = psender;
            importance = imp;
        }

      

        /**
         * 
         * @return
         */
        public int getImportance(){
            return importance;
        }

        /**
         * Sets the importance
         * @param imp
         */
        public void setImportance(final int imp){
            if ((imp>=0) && (imp<=100))
                importance = imp;    
        }
    }

    /**
     * 
     * @param gameplay
     * @param xPos
     * @param yPos
     */
    public MsgSystem(final GameplayScreen gameplay, final int xPos, final int yPos) {
        this.gameplay = gameplay;
        Skin skin = new Skin(Gdx.files.internal("com/BombingGames/WurfelEngine/Core/skin/uiskin.json"));
        textinput = new TextField("", skin);
        textinput.setBounds(xPos-200, yPos, 400, 50);
        textinput.setBlinkTime(0.2f);
        textinput.setCursorPosition(0);
        textinput.setVisible(false);
    }
    
    /**
     * "Handshake" with the view rendering the scene. This will add the GUI to the stage.
     * @param view the view managing the input and rendering it
     */
    public void viewInit(View view){
        stage = view.getStage();
        stage.addActor(textinput);
    }
        
    /**
     * Adds a message with the sender "System"
     * @param message
     */
    public void add(final String message) {
        messages.add(new Msg(message, "System", 100));
        Gdx.app.debug("System",message);
    }
    
    /**
     * 
     * @param message
     * @param sender
     */
    public void add(final String message, final String sender){
        messages.add(new Msg(message, sender, 100));
        Gdx.app.debug(sender,message);
    }
    
    /**
     * 
     * @param message
     * @param sender
     * @param importance
     */
    public void add(final String message, final String sender, final int importance){
        messages.add(new Msg(message, sender, importance));
        Gdx.app.debug(sender,message);
    }
    
    /**
     * Updates the Message System
     * @param delta
     */
    public void update(float delta){
       timelastupdate += delta;
       
       //derease importance every 30ms
       if (timelastupdate >= 30) {
            timelastupdate = 0;
           for (Msg m : messages) {
               if (m.getImportance() > 0)
                   m.setImportance(m.getImportance()-1);
           }
        }
    }
    
    /**
     * Draws the Messages
     * @param view 
     */
    public void render(final View view){  
        view.getBatch().begin();
        
        int y=0;
        for (Msg msg : messages) {
            Color color = Color.BLUE.cpy();
            if ("System".equals(msg.sender)) color = Color.GREEN.cpy();
                else if ("Warning".equals(msg.sender)) color = Color.RED.cpy();
            
            //draw
            view.getFont().setColor(color);
            view.getFont().draw(view.getBatch(), msg.sender+": "+msg.message, 10,50+y);
            y+=20;
        }
         view.getBatch().end();
    }

    /**
     * Tell the msg system if it should listen for input.
     * @param active If deactivating the input will be saved.
     */
    public void setActive(final boolean active) {
        if (!active && !textinput.getText().isEmpty()) {
            add(textinput.getText(), "Console");//add message to message list
            if (textinput.getText().startsWith("/") && !executeCommand(textinput.getText().substring(1)))
                add("Failed executing command.", "System");    
            textinput.setText("");
        }
        this.active = active;
        textinput.setVisible(active);
        if (active && stage!=null)
            stage.setKeyboardFocus(textinput);
    }
    
    /**
     * 
     * @return
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Returns the last Message
     * @return  if there exist no last message it returns an empty string
     */
    public String getLastMessage(){
        String tmp = messages.get(messages.size()-1).message;
        return tmp!=null ? tmp : "";
    }
    
    /**
     * Returns the last Message
     * @param sender filter by the sender, e.g. if you want the last message of a specific player
     * @return if there exist no last message it returns an empty string
     */
    public String getLastMessage(final String sender){
        int i = messages.size()-1;
        while (i>=0 && !messages.get(i).sender.equals(sender)) {
            i--;
        }
        return i>=0 ? messages.get(i).message : "";
    }
    
    public void setText(String text){
        textinput.setText(text);
        textinput.setCursorPosition(textinput.getText().length());
    }
    
    public boolean executeCommand(String command){
        if (command.equals("editor")){
            WE.loadEditor(true);
            return true;
        }else if (command.equals("lightengine")){ 
            Controller.getLightengine().renderData(!Controller.getLightengine().isRenderingData());
            return true;
        }
        return false;    
    }
}