package org.pgj.typemapping;

import org.pgj.messages.Result;

/**
 * Interface for type mapper blocks.
 * Type mapper blocks create Java objects from database data.
 * 
 * @author Laszlo Hornyak
 */
public interface TypeMapper {

	/**
	 * Is the typemapper able to map the type to a field?
	 * @param typeName			the name of the RDBMS data type
	 * @return true if so, false otherwise
	 */
	boolean canMap(String typeName);

	/**
	 * Map an RDBMS data to a Field.
	 * @param raw_data			raw data from the RDBMS
	 * @param type				The name of the RDBMS type (varchar, int, etc)
	 * @return The Field representation of the data.
	 */
	Field map(byte[] raw_data, String type) throws MappingException;

	/**
	 * Map back an Object to an RDBMS data. Use default mapping for the object.
	 * @param object			The object to map to RDBMS data type.
	 * @return The field mapped back.
	 * @throws MappingException if the object cannot be mapped to an RDBMS type.
	 */
	Field backMap(Object object) throws MappingException;

	/**
	 * Map back a java type to a RDBMS type with specifying the asked type.
	 * @param object			The object to map back to RDBMS type
	 * @param type				The RDBMS type name.
	 * @throws MappingException if the object cannot be mappedto the asked RDBMS type.
	 */
	Field backMap(Object object, String type) throws MappingException;

	/**
	 * Tells if the typemapper is able to map the Object to an RDBMS type.
	 * @param obj			the java object
	 * @return true if so, false otherwise
	 */
	boolean canBackMap(Object obj);

	/**
	 * Create a result object from a java object (backmap).
	 * @param obj			a java object
	 * @return the result object.
	 * @throws MappingException if the object type cannot be mapped to an RDBMS type.
	 */
	Result createResult(Object obj) throws MappingException;
	
	/**
	 * Returns the rdbms type of the given class.
	 * @param cl	the class
	 * @return	the name of the rdbms type
	 * @throws MappingException	if the class is not mappable
	 */
	String getRDBMSTypeFor(Class cl) throws MappingException;
}
