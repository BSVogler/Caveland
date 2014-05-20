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
package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;

/**
 * A cell is field in the map containing a block. It can have an offset.
 * @author Benedikt Vogler
 */
public class Cell implements Cloneable {
    private Block block;
    private int[] cellOffset = new int[]{0, 0,0};

    /**
     *Create a new cell containing air.
     */
    public Cell() {
        block = Block.getInstance(0);
    }
    
    /**
     *Create a new block in this cell.
     * @param id
     */
    public Cell(int id){
        block = Block.getInstance(id);
    }
    
    /**
     *Create a new block in this cell.
     * @param id
     * @param value
     */
    public Cell(int id, int value){
       this.block = Block.getInstance(id, value); 
    }
    
    /**
     *Create a new block in this cell.
     * @param id
     * @param value
     * @param coords
     */
    public Cell(int id, int value, Coordinate coords){
       this.block = Block.getInstance(id, value, coords);
    }

    /**
     *
     * @return
     */
    public Block getBlock() {
        return block;
    }

    /**
     *Set the block inside this cell. The offset stays the same.
     * @param block
     */
    public void setBlock(Block block) {
        this.block = block;
    }

    /**
     *The cell offset has it's center in the top left corner.
     * @return
     */
    public int[] getCellOffset() {
        return cellOffset;
    }

    /**
     *The cell offset has it's center in the top left corner.
     * @param cellOffset
     */
    public void setCellOffset(int[] cellOffset) {
        this.cellOffset = cellOffset;
    }

    /**
     *
     * @param field
     * @param offset
     */
    public void setCellOffset(int field, int offset) {
        this.cellOffset[field] = offset;
    }

    /**
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    protected Cell clone() throws CloneNotSupportedException {
        Cell clone = (Cell) super.clone();
        //clone.block = block;
        //clone.cellOffset = cellOffset;
        return clone;
        
    }
    
    

}
