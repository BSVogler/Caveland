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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import java.util.ArrayList;

/**
 *The message system can manage&show messages (Msg).
 * @author Benedikt
 */
public class MsgSystem {
    private int timelastupdate = 0;
    private boolean active = false;
    private final TextField textinput;
    private final ArrayList<Msg> messages = new ArrayList<Msg>(20);  
    
    /**
     * A message is put into the MsgSystem. It contains the message, the sender and the importance.
     * @author Benedikt
     */
    private class Msg {
        private final String fmessage;
        private String sender = "System";
        private int importance = 1;


        protected Msg(String pmessage, String psender, int imp) {
            fmessage = pmessage;
            sender = psender;
            importance = imp;
        }

        /**
         * 
         * @return
         */
        public String getMessage(){
            return fmessage;    
        }

        /**
         * 
         * @return
         */
        public String getSender(){
            return sender;
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
     * @param xPos
     * @param yPos
     */
    public MsgSystem(final int xPos, final int yPos) {
        Skin skin = new Skin(Gdx.files.internal("com/BombingGames/WurfelEngine/Core/skin/uiskin.json"));
        textinput = new TextField("Enter your message here!", skin);
        textinput.setBounds(xPos, yPos, 400, 50);
        textinput.setBlinkTime(200);
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
        
        if (active){
            //view.drawString("MSG:"+input, xPos, yPos, Color.WHITE.cpy());
            textinput.draw(view.getBatch(), 1);
        }
        for (int i=0; i < messages.size(); i++){
            Msg msg = messages.get(i);
            Color color = Color.BLUE.cpy();
            if ("System".equals(msg.getSender())) color = Color.GREEN.cpy();
                else if ("Warning".equals(msg.getSender())) color = Color.RED.cpy();
            
            //draw
            view.getFont().setColor(color);
            view.getFont().draw(view.getBatch(), msg.getMessage(), 10,50+i*20);
        }
         view.getBatch().end();
    }

    /**
     * Tell the msg system if it should listen for input.
     * @param active If deactivating the input will be saved.
     */
    public void show(final boolean active) {
        if (active != this.active && !textinput.getText().isEmpty()) {
            add(textinput.getText());//add message to message list
            textinput.setText("");
        }
        this.active = active;
    }
    
    /**
     * 
     * @return
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     *Add a key to the textbox.
     * @param characterInput
     */
    public void addInput(final char characterInput){
        textinput.setText(textinput.getText()+Character.toString(characterInput));
//        if (characterInput =='\b')//if backspace remove a letter
//            input = input.substring(0, input.length()-1);
//        else
//            input += String.valueOf(characterInput);
    }
    
    /**
     * Returns the last Message
     * @param sender filter by the sender, e.g. if you want the last message of a specific player
     * @return if there exist no last message of the filter type returns null
     */
    public String getLastMessage(final String sender){
        Msg result = null;
        int i = messages.size()-1;
        while (!messages.get(i).getSender().equals(sender) && i>0) {
            result = messages.get(i);
            i--;
        }
        return (result != null ? result.getMessage() : null);
    }
}