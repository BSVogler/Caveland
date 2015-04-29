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
package com.BombingGames.WurfelEngine.Core;

import com.badlogic.gdx.Gdx;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A class which helps getting OS specific information.
 * @author Martin Brunokowsky, Benedikt S. Vogler
 */
public class WorkingDirectory {
	private static String applicationName = "Wurfel Engine";

	/**
	 * set a custom name, should be the name of the game
	 * @param applicationName the name of the folder
	 */
	public static void setApplicationName(String applicationName) {
		WorkingDirectory.applicationName = applicationName;
	}
	
	
    /**
     * 
     * @return Get the folder where the data is stored.
     */
    public static File getWorkingDirectory() {
        String userHome = System.getProperty("user.home", ".");
        File workingDirectory;
        switch (getPlatform()) {
        case linux:
        case solaris:
                workingDirectory = new File(userHome, '.' + applicationName + '/');
                break;
        case windows:
                String applicationData = System.getenv("APPDATA");
                if (applicationData != null)
                        workingDirectory = new File(applicationData, applicationName + '/');
                else
                        workingDirectory = new File(userHome, '.' + applicationName + '/');
                break;
        case mac:
                workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
                break;
        case android:
            workingDirectory = new File(System.getenv("EXTERNAL_STORAGE"), applicationName + '/');
            break;
        default:
                workingDirectory = new File(userHome, applicationName + '/');
        }
        if ((!workingDirectory.exists()) && (!workingDirectory.mkdirs()))
                throw new RuntimeException("The working directory could not be created: " + workingDirectory);
        return workingDirectory;
    }

    /**
     * 
     * @return the os
     */
    public static OS getPlatform() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win"))
				return OS.windows;
		if (osName.contains("mac"))
				return OS.mac;
		if (osName.contains("solaris"))
				return OS.solaris;
		if (osName.contains("sunos"))
				return OS.solaris;
		if (System.getProperty("java.vm.name").equalsIgnoreCase("Dalvik"))
			return OS.android;
		if (osName.contains("linux"))
				return OS.linux;
		if (osName.contains("unix"))
				return OS.linux;
		return OS.unknown;
    }

    public static enum OS {
		linux, solaris, windows, mac, android, unknown;
    }
    
    /**
     * 
     * @return Get the folder where the maps are stored.
     * @since 1.2.X
     */
    public static File getMapsFolder(){
        return new File(getWorkingDirectory(),"maps");
    }
	
	/**
	 * unpacks a map to working directory
	 * @param foldername the name of the map folder. Will be created if non existend.
	 * @param source msut be a zip file without ".foldername" files
	 * @return trlue if everything went okay
	 * @since 1.3.13
	 */
	public static boolean unpackMap(String foldername, InputStream source) {
		File dest = new File(getMapsFolder().getAbsolutePath()+"/"+foldername+"/");
		//if directory not exists, create it
		if(!dest.exists()){
		   dest.mkdirs();
		   Gdx.app.log("WorkingDirectoy", "created map at "+ dest);
		}

		//Gdx.app.log("WorkingDirectoy", "coping chunks into "+ dest);
		//buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try (ZipInputStream zis = new ZipInputStream(source)) {
			ZipEntry ze = zis.getNextEntry();
			while(ze != null){
				String file = ze.getName();
				File newFile = new File(dest.getParent() + File.separator + file);
				System.out.println("Unzipping to "+newFile.getAbsolutePath());

				if (newFile.isDirectory()) {
					//ingore directorys in zip
				} else {
					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
				}
				//close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			//close last ZipEntry
			zis.closeEntry();
			source.close();
		} catch (IOException ex) {
			Logger.getLogger(WorkingDirectory.class.getName()).log(Level.SEVERE, null, ex);
		}
		return true;
	}
}