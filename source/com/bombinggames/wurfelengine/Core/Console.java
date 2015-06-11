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
package com.bombinggames.wurfelengine.Core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.bombinggames.wurfelengine.Core.CVar.CVar;
import com.bombinggames.wurfelengine.Core.CVar.CVarSystem;
import com.bombinggames.wurfelengine.Core.Gameobjects.BenchmarkBall;
import com.bombinggames.wurfelengine.Core.Map.AbstractMap;
import com.bombinggames.wurfelengine.WE;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 *The message system can manage&show messages (Line).
 * @author Benedikt
 */
public class Console implements CommandsInterface  {
	
    private int timelastupdate = 0;
    private GameplayScreen gameplayRef;//the reference to the associated gameplay
    private TextField textinput;
    private final Stack<Line> messages; 
    private boolean keyConsoleDown;
    private StageInputProcessor inputprocessor;
    private Modes mode;
	private CommandsInterface externalCommands;
	private final TextArea log;
	
	/**
	 * suggestions stuff
	 */
	private boolean keySuggestionDown;
	private String path = "";


    
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
		
		log = new TextArea("Wurfel Engine "+ WE.VERSION +" Console\n", skin);
		log.setBounds(xPos, yPos+52, 750, 550);
		log.setFocusTraversal(false);
		log.setColor(1, 1, 1, 0.5f);
		//log.setAlignment(Align.top, Align.left);
		//log.setWrap(true);
		//Label.LabelStyle customStyle = log.getStyle();
		//customStyle.background = textinput.getStyle().background;
		//log.setStyle(customStyle);
		log.setVisible(false);
		WE.getEngineView().getStage().addActor(log);//add it to the global stage
        textinput = new TextField(path+" $ ", skin);
        textinput.setBounds(xPos, yPos, 750, 50);
        textinput.setBlinkTime(0.3f);
		textinput.setColor(1, 1, 1, 0.5f);
		clearCommandLine();
        textinput.setVisible(false);
        
        WE.getEngineView().getStage().addActor(textinput);//add it to the global stage
    }

    /**
     *
     * @param gameplayRef
     */
	@Override
    public void setGameplayRef(GameplayScreen gameplayRef) {
        this.gameplayRef = gameplayRef;
    }
        
    /**
     * Adds a message with the sender "System"
     * @param message
     */
    public void add(final String message) {
        messages.add(new Line(message, "System", 100));
		log.setText(log.getText()+message);
		log.setCursorPosition(log.getText().length());
        Gdx.app.debug("System",message);
    }
    
    /**
     * Adds a message to the console.
     * @param message
     * @param sender
     */
    public void add(final String message, final String sender){
        messages.add(new Line(message, sender, 100));
		log.setText(log.getText()+message);
		log.setCursorPosition(log.getText().length());
        Gdx.app.debug(sender,message);
    }
    
    /**
     * Adds a message to the console.
     * @param message
     * @param sender
     * @param importance
     */
    public void add(final String message, final String sender, final int importance){
        messages.add(new Line(message, sender, importance));
		log.setText(log.getText()+message);
		log.setCursorPosition(log.getText().length());
        Gdx.app.debug(sender,message);
    }
    
    /**
     * Updates the console.
     * @param dt time in ms
     */
    public void update(float dt){
       timelastupdate += dt;
       
        //open close console/chat box
        if (!keyConsoleDown && Gdx.input.isKeyPressed(WE.CVARS.getValueI("KeyConsole"))) {
            setActive(Modes.Console, !textinput.isVisible());//toggle
        }
        keyConsoleDown = Gdx.input.isKeyPressed(WE.CVARS.getValueI("KeyConsole"));
		
		if (!keySuggestionDown
			&& Gdx.input.isKeyPressed( WE.CVARS.getValueI("KeySuggestion") )
			&& isActive()
		) {
            autoComplete();
        }
        keySuggestionDown = Gdx.input.isKeyPressed(WE.CVARS.getValueI("KeySuggestion"));
		

        //decrease importance every 30ms
        if (timelastupdate >= 30) {
             timelastupdate = 0;
            for (Line m : messages) {
                if (m.getImportance() > 0)
                    m.setImportance(m.getImportance()-1);
            }
        }
		
		if (!textinput.getText().startsWith(path+" $ "))
			setText(path+" $ ");
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
                    clearCommandLine();//clear if openend
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
        }
        textinput.setVisible(active);
		log.setVisible(active);
    }
    
    /**
     *when a message is entered
     */
    public void enter(){
		//add line break if last line ended with line break
		String lineBreak;
		if (!log.newLineAtEnd())
			lineBreak = "\n";
		else
			lineBreak="";
        add(lineBreak + textinput.getText()+"\n", "Console");//add message to message list
        //if (textinput.getText().startsWith("/") && !executeCommand(textinput.getText().substring(1)))//if it is a command try esecuting it
        if (mode==Modes.Console && !textinput.getText().isEmpty() && !executeCommand(
			textinput.getText().substring(textinput.getText().indexOf("$ ")+2)
		))    
            add("Failed executing command.\n", "System");    
        clearCommandLine();
    }
	
	public void clearCommandLine(){
		textinput.setText(path+" $ ");
		textinput.setCursorPosition((path+" $ ").length());
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
     * @return  if there spawn no last message it returns an empty string
     */
    public String getLastMessage(){
        String tmp = messages.lastElement().message;
        return tmp!=null ? tmp : "";
    }
    
    /**
     * Returns the last Message
     * @param sender filter by the sender, e.g. if you want the last message of a specific player
     * @return if there spawn no last message it returns an empty string
     */
    public String getLastMessage(final String sender){
        int i = messages.size()-1;
        while (i>=0 && !messages.get(i).sender.equals(sender)) {
            i--;
        }
        return i>=0 ? messages.get(i).message.substring(0, messages.get(i).message.length()-2) : "";
    }
    
    /**
     *Set the text in the box.
     * @param text
     */
    public void setText(String text){
        textinput.setText(text);
        textinput.setCursorPosition(textinput.getText().length());
		//nextSuggestionNo=0;//start with suggestions all over
    }
	
	/**
	 * suggests a cvar or a dir if cd entered
	 * @return the possible completions
	 */
	public ArrayList<String> autoComplete(){
		ArrayList<String> suggestions = new ArrayList<>(1);
		if ("cd".equals(getCurrentCommand())){
			suggestions.add("cd ");
		}else {
			if ("cd ".equals(getCurrentCommand())){
				suggestions = ls();
				for (int i = 0; i < suggestions.size(); i++) {
					suggestions.set(i, "cd ".concat(suggestions.get(i)));
				}
			}else {
				String commandTillCursor = getCurrentCommand().substring(0, textinput.getCursorPosition()-path.length()-3);
				//get until cursor position
				if ("".equals(path)) {
					suggestions = WE.CVARS.getSuggestions(commandTillCursor);
				} else {
					if (!path.contains(":"))
						suggestions = WE.CVARS.getChildSystem().getSuggestions(commandTillCursor);
					else 
						suggestions = WE.CVARS.getChildSystem().getChildSystem().getSuggestions(commandTillCursor);
				}
			}
		}
		
		//displaySuggestion
		if (suggestions.size()==1) {
			textinput.setText(path+" $ "+suggestions.get(0)+" ");
			textinput.setCursorPosition(textinput.getText().length());
		}
		return suggestions;
	}
    
	/**
	 * Set the factory for custom commands for delegation.
	 * @param externalCommands 
	 */
	public void setCustomCommands(CommandsInterface externalCommands){
		this.externalCommands = externalCommands;	
		externalCommands.setGameplayRef(gameplayRef);
	}
	
	private boolean checkPath(String newPath){
		if (newPath.equals("/") || newPath.isEmpty()) {
			return true;
		} else {
			StringTokenizer tokenizer = new StringTokenizer(newPath, ":");
			String mapname = tokenizer.nextToken();
			if (!new File(WorkingDirectory.getMapsFolder(), mapname).isDirectory())
				return false;
			if (tokenizer.hasMoreTokens() && tokenizer.nextToken().length()!=1){
				return false;
			}
			return true;
		}
	}
	
	/**
	 * returns the currently typed command
	 * @return 
	 */
	public String getCurrentCommand(){
		return textinput.getText().substring(textinput.getText().indexOf("$ ")+2);
	}
	
	public ArrayList<String> ls(){
		ArrayList<String> result = new ArrayList<>(10);
		if (path.isEmpty()){
			File mapsFolder = new File(WorkingDirectory.getMapsFolder().getAbsoluteFile()+ path);
			for (final File fileEntry : mapsFolder.listFiles()) {
				if (fileEntry.isDirectory()) {
					result.add(fileEntry.getName()+"\n");
				}
			}
		} else {
			File mapFolder = new File(WorkingDirectory.getMapsFolder().getAbsoluteFile()+"/" +getMapNameFromPath());
			for (final File fileEntry : mapFolder.listFiles()) {
				if (fileEntry.isDirectory()) {
					result.add(fileEntry.getName().substring(fileEntry.getName().length()-2)+"\n");
				}
			}
		}
		return result;
	}
	
	private String getMapNameFromPath(){
		if (path.indexOf(':')>0)
			return path.substring(0, path.indexOf(':'));
		else return path;
	}
		
    /**
     * Tries executing a command. If that fails trys to set cvar. if that fails trys to execute external commands.
     * @param command
     * @return 
     */
	@Override
    public boolean executeCommand(String command){
        if (command.length() <= 0) return false;
        StringTokenizer st = new StringTokenizer(command, " ");
		String first = st.nextToken().toLowerCase();
        switch (first) {
			case "ls":
				ls().forEach((dir) -> add(dir));
				return true;
			case "clear":
				log.setText("");
				return true;
				
			case "editor":
                WE.loadEditor(true);
                return true;
            case "le":
            case "lightengine":
                if (Controller.getLightEngine()!=null)
                    Controller.getLightEngine().setDebug(!Controller.getLightEngine().isInDebug());
                return true;
            case "quit":
            case "exit":
                Gdx.app.exit();
                return true;
            case "menu":
                WE.showMainMenu();
                return true;
			case "killall":
				for (int i = 0; i < Controller.getMap().getEntitys().size(); i++) {
					Controller.getMap().getEntitys().get(i).dispose();
				}
				Controller.getMap().getEntitys().clear();
				return true;
            case "fullscreen":
                WE.setFullscreen(!WE.isFullscreen());
                return true;
            case "help":
            case "about":
            case "credits":
                add("Wurfel Engine Version:"+WE.VERSION+"\nFor a list of available commands visit the GitHub Wiki.\n"+WE.getCredits(), "System");
                return true;
            case "save":
                return Controller.getMap().save(Controller.getMap().getCurrentSaveSlot());
            case "benchmark":
                new BenchmarkBall().spawn(Controller.getMap().getCenter(Controller.getMap().getGameHeight()));
                //add("Spawned a benchmark ball.", "System");
                return true;
			case "printmap":
				Controller.getMap().print();
				return true;
			case "reloadShaders":
				gameplayRef.getView().loadShaders();
				return true;
        }
		
		if (command.startsWith("cd")){
            if (!st.hasMoreElements()) return false;
            
            String enteredPath = st.nextToken();
			if (enteredPath.length()>0) {
				if (!checkPath(enteredPath)) {
					add("not a valid path");
				} else {
					switch (enteredPath) {
						case "/":
							path="";
							break;
						case "..":
							if (path.length()>1)
								path="";
							break;
						default:
							path = path.concat(enteredPath);//then add new path
							//if access to map
							if (path.length()>0) {
								//make sure it gets loaded
								if (WE.CVARS.getChildSystem()==null)
									if (path.contains(":"))
										WE.CVARS.setChildSystem(new CVarSystem(
											new File(WorkingDirectory.getMapsFolder()+"/"+path.substring(0, path.indexOf(':')))
										));
									else
										WE.CVARS.setChildSystem(new CVarSystem(new File(WorkingDirectory.getMapsFolder()+"/"+path)));
								//access save
								if (path.contains(":"))
										WE.CVARS.getChildSystem().setChildSystem(new CVarSystem(
											new File(WorkingDirectory.getMapsFolder()+"/save"+path.substring(path.indexOf(':')))
										));
							}
							break;
					}
				}
			}
			return true;
        }
        
        
		if (command.startsWith("screenshake")){
			int id = 0;
            if (st.hasMoreElements()){
                id = Integer.valueOf(st.nextToken());  
            }
			float amp = 10;
			if (st.hasMoreElements()){
                amp = Float.valueOf(st.nextToken());  
            }
			float t = 500;
			if (st.hasMoreElements()){
                t = Float.valueOf(st.nextToken());  
            }
			if (id < gameplayRef.getView().getCameras().size())
				gameplayRef.getView().getCameras().get(id).shake(amp, t);
			else {
				add("Camera ID out of range\n","System");
				return false;
			}
		}
        
        if (command.startsWith("loadmap")){
            if (!st.hasMoreElements()) return false;
            
            String mapname = st.nextToken();
            if (mapname.length()>0) {
				int slot = AbstractMap.newSaveSlot(new File(WorkingDirectory.getMapsFolder()+"/"+mapname));
                return Controller.loadMap(new File(WorkingDirectory.getMapsFolder()+"/"+mapname), slot);
			}
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
//            try {
//                ChunkMap.createMapFile(mapname);
//				return executeCommand("loadmap " +mapname);
//            } catch (IOException ex) {
//                add(ex.getMessage(), "Warning");
//                return false;
//            }
        }
         
		//if not a command try setting a cvar
		CVar cvar;
		if ("".equals(path)) {
			cvar = WE.CVARS.get(first);
		} else {
			if (!path.contains(":"))
				cvar = WE.CVARS.getChildSystem().get(first);
			else 
				cvar = WE.CVARS.getChildSystem().getChildSystem().get(first);
		}
		
		if (cvar!=null) {//if registered
			if (st.hasMoreTokens()){
				//set cvar
				String value = st.nextToken();
				cvar.setValue(value);
				add("Set cvar \""+ first + "\" to "+value+"\n", "System");
				return true;
			} else {
				//read cvar
				add("cvar "+ first +" has value "+cvar.toString()+"\n", "System");
				return true;
			}
		} else {
			//try executing with custom commands
			if (externalCommands!=null)
				return externalCommands.executeCommand(command);
			add("CVar \""+first+"\" not found.\n", "System");
			return true;
		}
    }
    
    private class StageInputProcessor extends InputListener {
        private Console parentRef;
		private boolean lastKeyWasTab;

        private StageInputProcessor(Console parent) {
            this.parentRef = parent;
        }

        @Override
        public boolean keyDown(InputEvent event, int keycode){
            if (keycode == Keys.UP){
                parentRef.setText(parentRef.getLastMessage("Console"));
            }
			if (keycode == Keys.DOWN){
                parentRef.setText(path+" $ ");
            }
			
            if (keycode == Keys.ENTER){
                parentRef.enter();
            }
			
			if (keycode == Keys.TAB){
				ArrayList<String> possibilities = autoComplete();
				if (possibilities.size()>1){
					if (lastKeyWasTab){
						add(possibilities+"\n");
					}
				} else {
					if (possibilities.size()>0)
						setText(path + " $ "+possibilities.get(0));
				}
				lastKeyWasTab = true;
            }else {
				lastKeyWasTab = false;
			}
            return true;
        }
    }
}