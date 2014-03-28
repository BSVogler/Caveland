/*
 * Copyright 2013 Benedikt Vogler.
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
 * * Neither the name of Bombing Games nor Benedikt Vogler nor the names of its contributors 
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
import java.util.ArrayList;

/**
 * A message is put into the MsgSystem. It contains the message, the sender and the importance.
 * @author Benedikt
 */
class Msg {
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
    public void setImportance(int imp){
        if ((imp>=0) && (imp<=100))
            importance = imp;    
    }
}

/**
 *The message system can manage&show messages (Msg).
 * @author Benedikt
 */
public class MsgSystem extends ArrayList<Msg> {
    private int timelastupdate = 0;
    private boolean waitforinput = false;
    private final int xPos, yPos;    
    private String input = "";

    /**
     * 
     * @param xPos
     * @param yPos
     */
    public MsgSystem(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }
        
    /**
     * Adds a message with the sender "System"
     * @param message
     */
    public void add(String message) {
        add(new Msg(message, "System", 100));
        Gdx.app.debug("System",message);
    }
    
    /**
     * 
     * @param message
     * @param sender
     */
    public void add(String message, String sender){
        add(new Msg(message, sender, 100));
        Gdx.app.debug(sender,message);
    }
    
    /**
     * 
     * @param message
     * @param sender
     * @param importance
     */
    public void add(String message, String sender, int importance){
        add(new Msg(message, sender, importance));
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
            for (int i=0; i < size(); i++) {
                Msg temp = get(i);
                if (temp.getImportance() > 0)
                    temp.setImportance(temp.getImportance()-1); 
                else remove(i);
            }
        }
    }
    
    /**
     * Draws the Messages
     * @param view 
     */
    public void render(View view){
        if (waitforinput) view.drawString("MSG:"+input, xPos, yPos);
        
        view.getBatch().begin();
        for (int i=0; i < size(); i++){
            Msg msg = get(i);
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
     * 
     * @param listen
     */
    public void listenForInput(boolean listen) {
        if (listen != waitforinput && !"".equals(input)) {
            add(input);
            input = "";
        }
        waitforinput = listen;
    }
    
    /**
     * 
     * @return
     */
    public boolean isListeningForInput() {
        return waitforinput;
    }
    
    /**
     *
     * @param characterInput
     */
    public void getInput(char characterInput){
        input += String.valueOf(characterInput);
    }
    
    /**
     * Returns the last Message
     * @return 
     */
    public Msg getLastMessage(){
        return get(size()-1);
    }
}