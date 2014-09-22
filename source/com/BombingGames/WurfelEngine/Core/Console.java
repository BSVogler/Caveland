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

import com.BombingGames.WurfelEngine.Core.Gameobjects.BenchmarkBall;
import com.BombingGames.WurfelEngine.Core.Map.Generators.FullMapGenerator;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Minimap;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import java.io.IOException;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 *The message system can manage&show messages (Line).
 * @author Benedikt
 */
public class Console {
    private int timelastupdate = 0;
    private GameplayScreen gameplayRef;//the reference to the associated gameplay
    private TextField textinput;
    private final Stack<Line> messages; 
    private boolean keyConsoleDown;
    private StageInputProcessor inputprocessor;
    private Modes mode;
    
    private enum Modes {
        Chat, Console
    }
    /**
     * A message is put into the Console. It contains the message, the sender and the importance.
     * @author Benedikt
     */
    private class Line {
        private final String message;
        private String sender = "System";
        private int importance = 1;


        protected Line(String pmessage, String psender, int imp) {
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
     * @param skin
     * @param xPos
     * @param yPos
     */
    public Console(Skin skin, final int xPos, final int yPos) {
        this.messages = new Stack<>();
        textinput = new TextField("", skin);
        textinput.setBounds(xPos-200, yPos, 400, 50);
        textinput.setBlinkTime(0.3f);
        textinput.setCursorPosition(0);
        textinput.setVisible(false);
        
        WE.getEngineView().getStage().addActor(textinput);//add it to the global stage
    }

    public void setGameplayRef(GameplayScreen gameplayRef) {
        this.gameplayRef = gameplayRef;
    }
        
    /**
     * Adds a message with the sender "System"
     * @param message
     */
    public void add(final String message) {
        messages.add(new Line(message, "System", 100));
        Gdx.app.debug("System",message);
    }
    
    /**
     * 
     * @param message
     * @param sender
     */
    public void add(final String message, final String sender){
        messages.add(new Line(message, sender, 100));
        Gdx.app.debug(sender,message);
    }
    
    /**
     * 
     * @param message
     * @param sender
     * @param importance
     */
    public void add(final String message, final String sender, final int importance){
        messages.add(new Line(message, sender, importance));
        Gdx.app.debug(sender,message);
    }
    
    /**
     * Updates the Message System.
     * @param delta time in ms
     */
    public void update(float delta){
       timelastupdate += delta;
       
        //open close console/chat box
        if (!keyConsoleDown && Gdx.input.isKeyPressed(WE.getCurrentConfig().getConsoleKey())) {
            setActive(Modes.Console, !textinput.isVisible());//toggle
        }
        keyConsoleDown = Gdx.input.isKeyPressed(WE.getCurrentConfig().getConsoleKey());

        //decrease importance every 30ms
        if (timelastupdate >= 30) {
             timelastupdate = 0;
            for (Line m : messages) {
                if (m.getImportance() > 0)
                    m.setImportance(m.getImportance()-1);
            }
         }
    }
    
    /**
     * Draws the Messages
     * @param batch
     */
    public void render(final SpriteBatch batch){  
        batch.begin();
        
        int y=0;
        for (Line msg : messages) {
            Color color = Color.BLUE.cpy();
            if (null != msg.sender) switch (msg.sender) {
                case "System":
                    color = Color.GREEN.cpy();
                    break;
                case "Warning":
                    color = Color.RED.cpy();
                    break;
            }
            
            //draw
            WE.getEngineView().getFont().setColor(color);
            WE.getEngineView().getFont().drawMultiLine(batch, msg.sender+": "+msg.message, 10,50+y);
            y+=20;
        }
        batch.end();
    }

    /**
     * Tell the msg system if it should listen for input.
     * @param active If deactivating the input will be saved.
     */
    private void setActive(Modes mode, final boolean active) {
        this.mode = mode;
        if (mode == Modes.Chat) {
            if (!active && !textinput.getText().isEmpty()) {//message entered and closing?
                enter();
            } else {
                if (active && !textinput.isVisible()){//window should be opened?
                    textinput.setText("");//clear if openend
                }
            }
        }
        
        if (active && !textinput.isVisible()){//window should be opened?
            inputprocessor = new StageInputProcessor(this);
            WE.getEngineView().getStage().addListener(inputprocessor);
            WE.getEngineView().getStage().setKeyboardFocus(textinput);
        }else {
            WE.getEngineView().getStage().removeListener(inputprocessor);
            WE.getEngineView().getStage().setKeyboardFocus(null);
            String inputText = textinput.getText();
            if (inputText.length()>0) inputText = inputText.substring(0, textinput.getText().length()-1);
            setText(inputText);//hack to prevent the key to open/clsoe to appear
        }
        textinput.setVisible(active);
    }
    
    public void enter(){
        add(textinput.getText(), "Console");//add message to message list
        //if (textinput.getText().startsWith("/") && !executeCommand(textinput.getText().substring(1)))//if it is a command try esecuting it
        if (mode==Modes.Console && !executeCommand(textinput.getText()))    
            add("Failed executing command.", "System");    
        setText("");
    }
    
    /**
     * Is the window open?
     * @return
     */
    public boolean isActive() {
        return textinput.isVisible();
    }
    
    /**
     * Returns the last Message
     * @return  if there exist no last message it returns an empty string
     */
    public String getLastMessage(){
        String tmp = messages.lastElement().message;
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
    
    /**
     *Set the text in the box.
     * @param text
     */
    public void setText(String text){
        textinput.setText(text);
        textinput.setCursorPosition(textinput.getText().length());
    }
    
    /**
     * Tries executing a command
     * @param command
     * @return 
     */
    public boolean executeCommand(String command){
        if (command.length() <= 0) return false;
        StringTokenizer st = new StringTokenizer(command, " ");
        switch (st.nextToken()) {
            case "editor":
                WE.loadEditor(true);
                return true;
            case "le":
            case "lightengine":
                if (Controller.getLightEngine()!=null)
                    Controller.getLightEngine().renderData(!Controller.getLightEngine().isRenderingData());
                return true;
            case "quit":
            case "exit":
                Gdx.app.exit();
                return true;
            case "menu":
                WE.showMainMenu();
                return true;
            case "fullscreen":
                WE.setFullscreen(!WE.isFullscreen());
                return true;
            case "help":
            case "about":
            case "credits":
                add("Wurfel Engine Version:"+WE.VERSION+"\nFor a list of available commands visit the GitHub Wiki.\n"+WE.getCredits(), "System");
                return true;
            case "minimap":
                if (gameplayRef==null) return false;
                if (gameplayRef.getController().getMinimap()==null){
                    add("No minimap found. Creating new", "System");
                    gameplayRef.getController().setMinimap(new Minimap(gameplayRef.getController(), gameplayRef.getView().getCameras().get(0), 0, Gdx.graphics.getHeight()));
                }
                gameplayRef.getController().getMinimap().toggleVisibility();
                return true;
            case "clearmap":
                Controller.getMap().fillWithAir();
                Controller.requestRecalc();
                return true;
            case "save":
                return Controller.getMap().save();
            case "devtools":
            case "dev":
                if (gameplayRef==null) return false;
                gameplayRef.getController().getDevTools().setVisible(!gameplayRef.getController().getDevTools().isVisible());
                return true;
            case "benchmark":
                new BenchmarkBall(Map.getCenter(Map.getGameHeight())).exist();
                //add("Spawned a benchmark ball.", "System");
                return true;
        }
        
        if (command.startsWith("fillmap")) {
            int id = 2;
            if (st.hasMoreElements()){
                id = Integer.valueOf(st.nextToken());  
            }
            
            Controller.getMap().fill(new FullMapGenerator(id), false);
            Controller.requestRecalc();
            return true;
        }
        
        if (command.startsWith("loadmap")){
            if (!st.hasMoreElements()) return false;
            
            String mapname = st.nextToken();
            if (mapname.length()>0)
                return Controller.loadMap(mapname);
        }
        
        if (command.startsWith("newmap")){
            String mapname;
            if (st.hasMoreTokens())
                mapname = st.nextToken();
            else
                return false;
            
//            Generator generator = new Generator() {
//
//                @Override
//                public int generate(int x, int y, int z) {
//                    return 0;
//                }
//            };
//            Controller.getMap().setGenerator(generator);
            try {
                Map.createMapFile(mapname);
            } catch (IOException ex) {
                add(ex.getMessage(), "Warning");
                return false;
            }
            return executeCommand("loadmap " +mapname);
        }
         
        if (command.startsWith("gamespeed")){
            if (command.length()==9){
                add("Gamespeed: "+gameplayRef.getController().getTimespeed(), "System");
                return true;
            } else {
                try {
                    gameplayRef.getController().setTimespeed(Float.parseFloat(command.substring(10)));
                    return true;
                } catch(NumberFormatException e) {
                    add("Tried using value: "+command.substring(10)+". Please enter float value in format like \"0.5\" ", "Warning");
                }
            }
        }
        
        return false;    
    }
    
    private class StageInputProcessor extends InputListener {
        private Console parentRef;

        private StageInputProcessor(Console parent) {
            this.parentRef = parent;
        }

        @Override
        public boolean keyDown(InputEvent event, int keycode){
            if (keycode == Keys.UP){
                parentRef.setText(parentRef.getLastMessage("Console"));
            }
            if (keycode == Keys.ENTER){
                parentRef.enter();
            }
            return true;
        }
    }
}