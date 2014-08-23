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

import com.BombingGames.WurfelEngine.Core.GameplayScreen;
import static com.BombingGames.WurfelEngine.Core.Map.Chunk.METAFILESUFFIX;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author Benedikt Vogler
 */
public class MapMetaData {
    private int chunkBlocksX;
    private int chunkBlocksY;
    private int chunkBlocksZ;
    private String mapversion;
    private String mapName;
   
    /**
     * reads the map info file and sets the size of the chunk
     * @param fileName filename
     * @throws java.io.IOException
     */
    public MapMetaData(String fileName) throws IOException {
        BufferedReader bufRead;

        //FileHandle path = Gdx.files.internal("map/map."+METAFILESUFFIX);
        FileHandle path = new FileHandle(WE.getWorkingDirectory().getAbsolutePath() + "/maps/"+fileName+"/map."+METAFILESUFFIX);
        if (path.exists()){
            Gdx.app.log("Chunk","Trying to load Map Info from \"" + path.path() + "\"");
            try {
                bufRead =  path.reader(1024);
                mapName = bufRead.readLine();
                mapName = mapName.substring(2, mapName.length());
                GameplayScreen.msgSystem().add("Loading map called: "+mapName);   

                mapversion = bufRead.readLine(); 
                mapversion = mapversion.substring(2, mapversion.length());
                GameplayScreen.msgSystem().add("Map Version:"+mapversion, "System");

                String blocksXString = bufRead.readLine();
                Gdx.app.debug("Chunk","sizeX:"+blocksXString);
                blocksXString = blocksXString.substring(2, blocksXString.length());
                chunkBlocksX = Integer.parseInt(blocksXString);

                String blocksYString = bufRead.readLine();
                Gdx.app.debug("Chunk","sizeY:"+blocksYString);
                blocksYString = blocksYString.substring(2, blocksYString.length());
                chunkBlocksY = Integer.parseInt(blocksYString);

                String blocksZString = bufRead.readLine();
                Gdx.app.debug("Chunk","sizeZ:"+blocksZString);
                blocksZString = blocksZString.substring(2, blocksZString.length());
                chunkBlocksZ = Integer.parseInt(blocksZString);
            } catch (IOException ex) {
                throw new IOException(
                    "The meta file could not be read. It must be named 'map."+ Chunk.METAFILESUFFIX + "' and must be at the maps directory:"+ WE.getWorkingDirectory().getAbsolutePath() + "/maps/<mapname>"
                );
            }
        } else {
            Gdx.app.error("Chunk", "Map named \""+ fileName +"\" could not be found. Path:"+ path);
            throw new IOException("Map named \""+ fileName +"\" could not be found. Path:"+ path);
        }
    }

    public int getChunkBlocksX() {
        return chunkBlocksX;
    }

    public int getChunkBlocksY() {
        return chunkBlocksY;
    }

    public int getChunkBlocksZ() {
        return chunkBlocksZ;
    }

    public String getMapversion() {
        return mapversion;
    }

    public String getMapName() {
        return mapName;
    }
}
