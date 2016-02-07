package com.bombinggames.caveland.game;

import com.bombinggames.caveland.gameobjects.MineCart;
import com.bombinggames.caveland.gameobjects.collectibles.CollectibleType;
import java.util.ArrayList;

/**
 *A list which stores the possibles recipes.
 * @author Benedikt Vogler
 */
public class CraftingRecipesList extends ArrayList<Recipe>{
	
	/**
	 * adds teh receipes to a list
	 */
	public CraftingRecipesList() {
		add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Sulfur,
					CollectibleType.Coal
				},
				"TFlint",
				CollectibleType.Explosives
			)
		);
		
		add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Wood,
					CollectibleType.Wood
				},
				"Construction Kit",
				CollectibleType.Toolkit
			)
		);
		
		add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Wood,
					CollectibleType.Coal
				},
				"Torch",
				CollectibleType.Torch
			)
		);
		
		add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Iron,
					CollectibleType.Iron
				},
				"Rails Construction Kit",
				CollectibleType.Rails
			)
		);
		
		add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Iron,
					CollectibleType.Iron
				},
				"Power Cable",
				CollectibleType.Powercable
			)
		);
		
		add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Iron,
					CollectibleType.Iron,
					CollectibleType.Wood
				},
				"Minecart",
				MineCart.class
			)
		);
		add(
			new Recipe(
				new CollectibleType[]{
					CollectibleType.Iron
			},
			"Drop Space Flag",
			CollectibleType.DropSpaceFlagConstructionKit
			)
		);
	}
}
