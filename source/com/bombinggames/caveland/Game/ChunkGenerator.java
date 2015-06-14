package com.bombinggames.caveland.Game;

import com.bombinggames.wurfelengine.core.Map.Generator;

/**
 *
 * @author Benedikt Vogler
 */
public class ChunkGenerator implements Generator {

	@Override
	public byte generate(int x, int y, int z) {
		if (z<3) return 2;
		if (z==3) return 1;
		return 0;
	}
	
}
