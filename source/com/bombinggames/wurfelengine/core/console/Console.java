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
package com.bombinggames.wurfelengine.core.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import com.bombinggames.wurfelengine.core.WorkingDirectory;
import com.bombinggames.wurfelengine.core.cvar.CVar;
import com.bombinggames.wurfelengine.core.cvar.CVarSystemMap;
import com.bombinggames.wurfelengine.core.cvar.CVarSystemSave;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * The message system can manage&show messages (Line).
 *
 * @author Benedikt
 */
public class Console {
	
    private int timelastupdate = 0;
    private GameplayScreen gameplayRef;//the reference to the associated gameplay
    private final TextField textinput;
    private final Stack<Line> messages; 
    private boolean keyConsoleDown;
    private StageInputProcessor inputprocessor;
	/**
	 * the mode of the console
	 */
    private Modes mode;
	private final TextArea log;
	private final ArrayList<ConsoleCommand> registeredCommands = new ArrayList<>(10);
	
	/**
	 * suggestions stuff
	 */
	private boolean keySuggestionDown;
	/**
	 * current path where the console is in
	 */
	private String path = "";

	/**
	 *
	 * @return
	 */
	public String getPath() {
		return path;
	}


    
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
		
		//register engine commands
		registeredCommands.add(new BenchmarkCommand());
		registeredCommands.add(new CdCommand());
		registeredCommands.add(new LECommand());
		registeredCommands.add(new CreditsCommand());
		registeredCommands.add(new EditorCommand());
		registeredCommands.add(new ExitCommand());
		registeredCommands.add(new KillallCommand());
		registeredCommands.add(new LoadMapCommand());
		registeredCommands.add(new TeleportCommand());
		registeredCommands.add(new ScreenshakeCommand());
		registeredCommands.add(new SaveCommand());
		registeredCommands.add(new ReloadShadersCommand());
		registeredCommands.add(new SaveCommand());
		registeredCommands.add(new PrintmapCommand());
		registeredCommands.add(new MenuCommand());
		registeredCommands.add(new LsCommand());
		registeredCommands.add(new FullscreenCommand());
		registeredCommands.add(new ManCommand());
		registeredCommands.add(new FillWithAirCommand());
		
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
    public void setGameplayRef(GameplayScreen gameplayRef) {
        this.gameplayRef = gameplayRef;
    }
        
    /**
     * Adds a message with the sender "System"
     * @param message
     */
    public void add(final String message) {
		messages.add(new Line(message, "System", 100));
		log.setText(log.getText() + message);
		log.setCursorPosition(log.getText().length());
		Gdx.app.debug("System", message);
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
       
        //open close console/chat box. Update is called when the console is not active. The StageInputProcessor oly when it is ipen
        if (!keyConsoleDown && Gdx.input.isKeyPressed(WE.getCVars().getValueI("KeyConsole"))) {
            setActive(Modes.Console, !textinput.isVisible());//toggle
        }
        keyConsoleDown = Gdx.input.isKeyPressed(WE.getCVars().getValueI("KeyConsole"));
		
		if (
			!keySuggestionDown
			&& Gdx.input.isKeyPressed( WE.getCVars().getValueI("KeySuggestion") )
			&& isActive()
		) {
            autoComplete();
        }
        keySuggestionDown = Gdx.input.isKeyPressed(WE.getCVars().getValueI("KeySuggestion"));
		

		//decrease importance every 30ms
		if (timelastupdate >= 30) {
			timelastupdate = 0;
			for (Line m : messages) {
				if (m.getImportance() > 0) {
					m.setImportance(m.getImportance() - 1);
				}
			}
		}
		
		if (!textinput.getText().startsWith(path+" $ "))
			setText(path+" $ "+ textinput.getText());
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
			} else if (active && !textinput.isVisible()) {//window should be opened?
				clearCommandLine();//clear if openend
			}
        }
        
		if (active && !textinput.isVisible()) {//window should be opened?
			inputprocessor = new StageInputProcessor(this);
			WE.getEngineView().getStage().addListener(inputprocessor);
			WE.getEngineView().getStage().setKeyboardFocus(textinput);

		} else {
			WE.getEngineView().getStage().removeListener(inputprocessor);
			WE.getEngineView().getStage().setKeyboardFocus(null);
		}
		textinput.setVisible(active);
		log.setVisible(active);
    }
    
	/**
	 * when a message is entered
	 */
	public void enter() {
		//add line break if last line ended with line break
		String lineBreak;
		if (!log.newLineAtEnd()) {
			lineBreak = "\n";
		} else {
			lineBreak = "";
		}
		add(lineBreak + textinput.getText() + "\n", "Console");//add message to message list
		if (!textinput.getText().isEmpty()) {
			String command = textinput.getText().substring(textinput.getText().indexOf("$ ") + 2);
			WE.getCVars().get("lastConsoleCommand").setValue(command);
			if (!executeCommand(command)) {
				add("Failed executing command.\n", "System");
			}
			clearCommandLine();
		}
	}
	
	/**
	 *
	 */
	public void clearCommandLine() {
		textinput.setText(path + " $ ");
		textinput.setCursorPosition((path + " $ ").length());
	}
 
	/**
	 * Is the window open?
	 *
	 * @return
	 */
	public boolean isActive() {
		return textinput.isVisible();
	}

	/**
	 * Returns the last Message
	 *
	 * @return if there spawn no last message it returns an empty string
	 */
	public String getLastMessage() {
		String tmp = messages.lastElement().message;
		return tmp != null ? tmp : "";
	}
    
 /**
	 * Returns the last Message
	 *
	 * @param sender filter by the sender, e.g. if you want the last message of
	 * a specific player
	 * @param skip how many items you want to skip
	 * @return if there spawn no last message it returns an empty string
	 */
	public String getLastMessage(final String sender, int skip) {
		//filter
		ArrayList<Line> filt = new ArrayList<>(10);
		for (Line message : messages) {
			if (message.sender.equals(sender)) {
				filt.add(message);
			}
		}
		if (filt.isEmpty()) {
			return "";
		}

		if (skip >= filt.size()) {
			skip = filt.size() - 1;
		}

		if (skip < 0) {
			skip = 0;
		}

		String line = filt.get(filt.size() - 1 - skip).message;//apply filter
		return line.substring(textinput.getText().indexOf("$ ") + 2, line.length() - 1);
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
			suggestions.add("cd ");//only add space
		} else {
			if ("cd ".equals(getCurrentCommand())){
				suggestions = ls();
				for (int i = 0; i < suggestions.size(); i++) {
					suggestions.set(i, "cd ".concat(suggestions.get(i)));
				}
			} else {
				//get until cursor position
				String commandTillCursor = getCurrentCommand().substring(0, textinput.getCursorPosition()-path.length()-3);
				
				//suggest command
				for (ConsoleCommand command : registeredCommands) {
					if (command.getCommandName().startsWith(commandTillCursor.toLowerCase()))
						suggestions.add(command.getCommandName());
				}
	
				//suggest cvar
				if ("".equals(path)) {
					suggestions.addAll(WE.getCVars().getSuggestions(commandTillCursor));
				} else {
					if (path.contains(":"))
						suggestions = WE.getCVarsSave().getSuggestions(commandTillCursor);
					else
						suggestions = WE.getCVarsMap().getSuggestions(commandTillCursor);
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
	 * Add new commands
	 * @param command 
	 */
	public void addCommand(ConsoleCommand command){
		registeredCommands.add(command);
	}
	
	/**
	 *
	 * @return
	 */
	protected ArrayList<ConsoleCommand> getRegisteredCommands(){
		return registeredCommands;
	}
	
	/**
	 * check if the path is valid relative to the current one
	 * @param newPath
	 * @return 
	 */
	public boolean checkPath(String newPath){
		if (newPath.equals("/") || newPath.isEmpty()) {
			return true;
		} else {
			StringTokenizer tokenizer = new StringTokenizer(newPath, ":");
			String mapname = tokenizer.nextToken();
			if (!new File(WorkingDirectory.getMapsFolder(), mapname).isDirectory())
				return false;
			if (tokenizer.hasMoreTokens() && (tokenizer.nextToken().length()!=1 || path.contains(":"))){
				return false;
			}
			return tokenizer.countTokens() <= 2;
		}
	}
	
	/**
	 * returns the currently typed command
	 * @return 
	 */
	public String getCurrentCommand(){
		return textinput.getText().substring(textinput.getText().indexOf("$ ")+2);
	}
	
	/**
	 * displays the content of a folder.
	 * @return 
	 */
	public ArrayList<String> ls(){
		ArrayList<String> result = new ArrayList<>(10);
		if (path.isEmpty()){//display maps
			File mapsFolder = new File(WorkingDirectory.getMapsFolder().getAbsoluteFile()+ path);
			for (final File fileEntry : mapsFolder.listFiles()) {
				if (fileEntry.isDirectory()) {
					result.add(fileEntry.getName()+"\n");
				}
			}
		} else {//display saves
			File mapFolder = new File(WorkingDirectory.getMapsFolder().getAbsoluteFile()+"/" +getMapNameFromPath());
			for (final File fileEntry : mapFolder.listFiles()) {
				if (fileEntry.isDirectory()) {
					result.add(fileEntry.getName().substring(fileEntry.getName().length()-1)+"\n");
				}
			}
		}
		return result;
	}
	
	private String getMapNameFromPath() {
		if (path.indexOf(':') > 0) {
			return path.substring(0, path.indexOf(':'));
		} else {
			return path;
		}
	}
	
    /**
     * Tries executing a command. If that fails trys to set cvar. if that fails trys to execute external commands.
     * @param command
     * @return 
     */
    public boolean executeCommand(String command){
        if (command.length() <= 0) return false;
        StringTokenizer st = new StringTokenizer(command, " ");
		if (!st.hasMoreTokens()) return false;
		String first = st.nextToken().toLowerCase();
		
		//first check if it a command
		for (ConsoleCommand comm : registeredCommands) {
			if (comm.getCommandName().equals(first)) {
				return comm.perform(st, gameplayRef);
			}
		}
		
		//if not a command try setting a cvar
		CVar cvar;
		if ("".equals(path)) {
			cvar = WE.getCVars().get(first);
		} else {
			if (!path.contains(":"))
				cvar = WE.getCVarsMap().get(first);
			else 
				cvar = WE.getCVarsSave().get(first);
		}
		
		if (cvar != null) {//if registered
			if (st.hasMoreTokens()){
				//set cvar
				String value = st.nextToken();
				cvar.setValue(value);
			}
			//read cvar
			add("cvar "+ first +" has value "+cvar.toString()+"\n", "System");
			return true;
		}
		add(command +": command not found\n", "System");
		return false;
    }
    
    private class StageInputProcessor extends InputListener {
        private final Console parentRef;
		private boolean lastKeyWasTab;
		private int posInLastCommands = -1;

        private StageInputProcessor(Console parent) {
            this.parentRef = parent;
        }

		/**
		 * only called when the console is open
		 * @param event
		 * @param keycode
		 * @return 
		 */
        @Override
        public boolean keyDown(InputEvent event, int keycode){
            if (keycode == Keys.UP){
				//filter
				ArrayList<Line> filt = new ArrayList<>(10);
				for (Line message : messages) {
					if (message.sender.equals("Console")) {
						filt.add(message);
					}
				}	
				if (posInLastCommands < filt.size()) {
					posInLastCommands++;
				}
				if (posInLastCommands >= filt.size()){
					parentRef.setText(WE.getCVars().getValueS("lastConsoleCommand"));
				} else {
					parentRef.setText(parentRef.getLastMessage("Console",posInLastCommands));
				}
            }
			if (keycode == Keys.DOWN){
				if (posInLastCommands > -1) {
					posInLastCommands--;
				}
				if (posInLastCommands == -1) {
					parentRef.setText(path + " $ ");
				} else {
					parentRef.setText(parentRef.getLastMessage("Console", posInLastCommands));
				}
            }
			
            if (keycode == Keys.ENTER){
				posInLastCommands = -1;
                parentRef.enter();
            }
			
			if (keycode == Keys.ESCAPE){
                setActive(Modes.Console, false);//toggle
            }
			
			if (keycode == Keys.TAB){
				ArrayList<String> possibilities = autoComplete();
				if (possibilities.size()>1){
					if (lastKeyWasTab){
						add(possibilities+"\n");
					}
				} else {
					if (possibilities.size()==1)
						setText(path + " $ "+possibilities.get(0));
				}
				lastKeyWasTab = true;
            }else {
				lastKeyWasTab = false;
			}
            return true;
        }
    }

	/**
	 * Set a path as the current path for the console access.
	 * @param path 
	 */
	public void setPath(String path) {
		if (checkPath(path)) {
			if ("/".equals(path)) {
				this.path = "";
			} else {
				this.path = path;
			}
			//check that map cvars are loaded
			String mapName = getMapNameFromPath();
			if (mapName.length() > 0) { //load map cvar
				if (WE.getCVarsMap() == null) {
					WE.getCVars().setMapCVars(new CVarSystemMap(
						new File(WorkingDirectory.getMapsFolder() + "/" + mapName + "/meta.wecvar")
					));
					WE.getCVarsMap().load();
				}

				//access save
				if (path.contains(":")) {
					WE.getCVarsMap().setSaveCVars(new CVarSystemSave(
						new File(WorkingDirectory.getMapsFolder() + "/" + mapName + "/save" + path.substring(path.indexOf(':') + 1) + "/meta.wecvar")
					));
					//todo overwrites currently loaded cvars
					WE.getCVarsSave().load();
				}
			}
		}  else {
			WE.getConsole().add("not a valid path");
		}
	}
	
}