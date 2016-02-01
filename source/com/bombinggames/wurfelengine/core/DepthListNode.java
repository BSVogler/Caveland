/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2016 Benedikt Vogler.
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
package com.bombinggames.wurfelengine.core;

import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;
import java.util.Iterator;

/**
 *
 * @author Benedikt Vogler
 */
public class DepthListNode implements Iterable<AbstractGameObject>{
	private DepthListNode next = null;
	private DepthListNode prev = null;
	final AbstractGameObject content;

	public DepthListNode(AbstractGameObject content) {
		this.content = content;
	}
	
	public DepthListNode getNext() {
		return next;
	}

	public AbstractGameObject getContent() {
		return content;
	}
	
	public void add(AbstractGameObject content){
		next = new DepthListNode(content);
		next.prev = this;
	}

	@Override
	public Iterator<AbstractGameObject> iterator() {
		return new DepthListIterator(this);
	}
	
	public ReverseDepthListIterator iteratorReverse() {
		return new ReverseDepthListIterator(this);
	}

	void addEnd(AbstractEntity content) {
		DepthListNode last = this;
		while (last.next != null) {
			last = last.next;	
		}
		last.add(content);
	}
	
	public class DepthListIterator implements Iterator<AbstractGameObject>{
		DepthListNode current;

		public DepthListIterator(DepthListNode start) {
			this.current = start;
		}
		
		@Override
		public boolean hasNext() {
			return current.next != null;
		}

		@Override
		public AbstractGameObject next() {
			current = current.next;
			return current.getContent();
		}


		/**
		 * to the right
		 * @param ent 
		 */
		void insert(AbstractEntity ent) {
			DepthListNode tmp = null;
			if (current.next != null) {
				tmp = current.next;
			}
			current.next = new DepthListNode(ent);
			current.next.prev = current;
			current.next.next = tmp;
		}
	};
	
	public class ReverseDepthListIterator{
		DepthListNode current;

		public ReverseDepthListIterator(DepthListNode start) {
			this.current = start;
		}
		
		public boolean hasPrev() {
			return current.prev != null;
		}

		public AbstractGameObject prev() {
			current = current.prev;
			return current.getContent();
		}

		/**
		 * to the right
		 * @param ent 
		 */
		void insert(AbstractEntity ent) {
			DepthListNode tmp = null;
			if (current.next != null) {
				tmp = current.next;
			}
			current.next = new DepthListNode(ent);
			current.next.prev = current;
			current.next.next = tmp;
		}
	};
	
}
