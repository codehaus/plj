/*
 * Created on Apr 8, 2004
 */

package org.pgj.tools.tuplemapper;

import org.pgj.typemapping.Tuple;


/**
 * Maps tuples to objects.
 * @author Laszlo Hornyak
 */
public interface TupleMapper {

	/** Map a tuple to a java object. */
	Object mapTuple(Tuple tuple);

	/** Get the mapped class for a given tuple. */
	Class getMappedClass(Tuple tuple);

}