/*
 * Created on May 10, 2004
 */
package org.pgj.messages;

import org.pgj.typemapping.Tuple;


/**
 * Result of triggers.
 * 
 * @author Laszlo Hornyak
 */
public class TupleResult extends Message {
	private Tuple tuple = null;
	/**
	 * @return Returns the tuple.
	 */
	public Tuple getTuple() {
		return tuple;
	}
	/**
	 * @param tuple The tuple to set.
	 */
	public void setTuple(Tuple tuple) {
		this.tuple = tuple;
	}
}
