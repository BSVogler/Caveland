/*
 *
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
package com.bombinggames.caveland.gameobjects.collectibles;

/**
 * a enum which lists the types of collectibles. Saves information about the
 * sprites and connets the enum with the classes.
 */
public enum CollectibleType {

	/**
	 *
	 *//**
	 *
	 */
	Rails((byte) 16, (byte) 2),
	/**
	 *
	 */
	Wood((byte) 46, (byte) 5),
	/**
	 *
	 */
	Explosives((byte) 47, (byte) 8),
	/**
	 *
	 */
	Ironore((byte) 48, (byte) 5),
	/**
	 *
	 */
	Coal((byte) 49, (byte) 5),
	/**
	 *
	 */
	Cristall((byte) 50, (byte) 5),
	/**
	 *
	 */
	Sulfur((byte) 51, (byte) 5),
	/**
	 *
	 */
	Stone((byte) 52, (byte) 5),
	/**
	 *
	 */
	Toolkit((byte) 53, (byte) 8),
	/**
	 *
	 */
	Torch((byte) 54, (byte) 2),
	/**
	 *
	 */
	Iron((byte) 55, (byte) 4),
	/**
	 *
	 */
	Powercable((byte) 57, (byte) 1),
	DropSpaceFlagConstructionKit((byte) 23, (byte) 1);

	/**
	 *
	 * @param value
	 * @return
	 */
	public static CollectibleType fromValue(String value) {
		if (value != null) {
			for (CollectibleType type : CollectibleType.values()) {
				if (type.name().equals(value)) {
					return type;
				}
			}
		}
		return null;
	}

	private final byte id;
	private final int steps;

	private CollectibleType(byte id, int steps) {
		this.id = id;
		this.steps = steps;
	}

	/**
	 * get the sprite id of this type
	 *
	 * @return
	 */
	public byte getId() {
		return id;
	}

	/**
	 * the amount of sprites
	 *
	 * @return
	 */
	public int getAnimationSteps() {
		return steps;
	}

	/**
	 * factory method to createInstance an abstract entitiy from the definition
	 *
	 * @return
	 */
	public Collectible createInstance() {
		if (null != this) {
			switch (this) {
				case Explosives:
					return new TFlint();
				case Toolkit:
					return new ConstructionKit();
				case Torch:
					return new TorchCollectible();
				case Rails:
					return new InstantConstructionKit(this);
				case Powercable:
					return new InstantConstructionKit(this);
				case DropSpaceFlagConstructionKit:
					return new DropSpaceFlagConstructionKit();
				default:
					return new Collectible(this);
			}
		}
		return null;
	}

}
