/*
 * Created on Apr 8, 2004
 */

package org.pgj.tools.tuplemapper;

import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.Tuple;
import org.pgj.typemapping.TypeMapper;


/**
 * Maps tuples to objects and back.
 * Implementations will tipically need a typemapper to accomplish their job.
 * 
 * @author Laszlo Hornyak
 */
public interface TupleMapper {

	/**
	 * Map a tuple to a java object.
	 * @param tuple the tuple to be mapped to bean
	 * @return the bean representing the tuple
	 * @throws MappingException	if the tuple can`t be mapped to a bean
	 */
	Object mapTuple(Tuple tuple) throws MappingException;

	/**
	 * Get the mapped class for a given tuple.
	 * @param tuple	the tuple to be mapped to bean
	 * @return the class of the bean
	 * @throws MappingException if the tuple can not be mapped to a class
	 */
	Class getMappedClass(Tuple tuple) throws MappingException;

	/**
	 * Maps an object back to a tupple.
	 * @param obj the object to be mapped as tuple
	 * @return the tuple
	 * @throws MappingException if the object couldn`t be mapped back to tuple.
	 */
	Tuple backMap(Object obj, TypeMapper typemapper) throws MappingException;
}