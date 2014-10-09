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

package com.BombingGames.WurfelEngine.Core.Map;

import static com.BombingGames.WurfelEngine.Core.Map.Chunk.METAFILESUFFIX;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

/**
 *A class to read the meta file.
 * @author Benedikt Vogler
 */
public class MapMetaData {

    /**
     *
     */
    public static final String VERSION = "0.12";
    private int chunkBlocksX;
    private int chunkBlocksY;
    private int chunkBlocksZ;
    private String mapversion;
    private String mapName;
    private String description = "";
    private String fileName;

    /**
     * Creates an empty objects.
     */
    public MapMetaData() {
    }
   
    
    /**
     * reads the map info file and sets the size of the chunk
     * @param fileName filename
     * @throws java.io.IOException
     */
    public MapMetaData(String fileName) throws IOException {
        BufferedReader bufRead;
        this.fileName = fileName;

        //FileHandle path = Gdx.files.internal("map/map."+METAFILESUFFIX);
        FileHandle path = Gdx.files.absolute(WE.getWorkingDirectory().getAbsolutePath() + "/maps/"+fileName+"/map."+METAFILESUFFIX);
        if (path.exists()){
            Gdx.app.log("MapMetaData","Trying to load Map Info from \"" + path.path() + "\"");
            try {
                bufRead =  path.reader(1024, "UTF8");
                mapName = bufRead.readLine();
                Gdx.app.debug("MapMetaData","Map name: "+mapName);   

                mapversion = bufRead.readLine(); 
                Gdx.app.debug("MapMetaData","Version:"+mapversion);

                String blocksXString = bufRead.readLine();
                Gdx.app.debug("MapMetaData","sizeX:"+blocksXString);
                chunkBlocksX = Integer.parseInt(blocksXString);

                String blocksYString = bufRead.readLine();
                Gdx.app.debug("MapMetaData","sizeY:"+blocksYString);
                chunkBlocksY = Integer.parseInt(blocksYString);

                String blocksZString = bufRead.readLine();
                Gdx.app.debug("MapMetaData","sizeZ:"+blocksZString);
                chunkBlocksZ = Integer.parseInt(blocksZString);
            } catch (IOException ex) {
                throw new IOException(
                    "The meta file could not be read. It must be named 'map."+ Chunk.METAFILESUFFIX + "' and must be at the maps directory:"+ WE.getWorkingDirectory().getAbsolutePath() + "/maps/<mapname>"
                );
            } catch (NullPointerException ex) {
                throw new IOException("Error reading the 'map."+ Chunk.METAFILESUFFIX + "'. It seems the file is  corrupt or outdated.");
            } catch (NumberFormatException ex) {
                throw new IOException("Error reading the 'map."+ Chunk.METAFILESUFFIX + "'. It seems the file is corrupt or outdated.");
            }
        } else {
            Gdx.app.error("MapMetaData", "Map named \""+ fileName +"\" could not be found. Path:"+ path);
            throw new IOException("Map named \""+ fileName +"\" could not be found. Path:"+ path);
        }
    }
    
    /**
     * Writes the data in a folder at the working directory.
     * @return
     * @throws IOException 
     * @since v1.2.28
     */
    public boolean write() throws IOException{
        if ("".equals(fileName)) return false;
        FileHandle path =  Gdx.files.external(WE.getWorkingDirectory().getAbsolutePath() + "/maps/"+fileName+"/");
        if (!path.exists()) path.mkdirs();//create fiel if it is missing
        FileHandle meta = path.child("map."+METAFILESUFFIX);
        String lineFeed = System.getProperty("line.separator");
        
        //meta..createNewFile();
        try (Writer writer = meta.writer(false, "UTF8")) {
            writer.write(mapName+lineFeed);
            writer.write(VERSION+lineFeed);
            writer.write(Integer.toString(chunkBlocksX)+lineFeed);
            writer.write(Integer.toString(chunkBlocksY)+lineFeed);
            writer.write(Integer.toString(chunkBlocksZ)+lineFeed);
            writer.write("0,0"+lineFeed);
            writer.write(description+lineFeed);
        } catch (IOException ex){
            throw ex;
        }
        return true;
    }

    /**
     *
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     *
     * @param chunkBlocksX
     */
    public void setChunkBlocksX(int chunkBlocksX) {
        this.chunkBlocksX = chunkBlocksX;
    }

    /**
     *
     * @param chunkBlocksY
     */
    public void setChunkBlocksY(int chunkBlocksY) {
        this.chunkBlocksY = chunkBlocksY;
    }

    /**
     *
     * @param chunkBlocksZ
     */
    public void setChunkBlocksZ(int chunkBlocksZ) {
        this.chunkBlocksZ = chunkBlocksZ;
    }

    /**
     *
     * @param mapversion
     */
    public void setMapversion(String mapversion) {
        this.mapversion = mapversion;
    }

    /**
     *
     * @param mapName
     */
    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public int getChunkBlocksX() {
        return chunkBlocksX;
    }

    /**
     *
     * @return
     */
    public int getChunkBlocksY() {
        return chunkBlocksY;
    }

    /**
     *
     * @return
     */
    public int getChunkBlocksZ() {
        return chunkBlocksZ;
    }

    /**
     *
     * @return
     */
    public String getMapversion() {
        return mapversion;
    }

    /**
     *
     * @return
     */
    public String getMapName() {
        return mapName;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }
}
