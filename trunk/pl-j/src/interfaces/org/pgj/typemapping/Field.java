package org.pgj.typemapping;

/**
 * Represensts a data of the RDBMS.
 * A field is responsible for parsing and mapping raw datatype to java types.
 */
public interface Field {

	/**
	 * A field is created from array bytes (raw data).
	 * @param raw			The raw byte[] data from the RDBMS.
	 */
	void set(byte[] raw);

	/**
	 * Get the raw data.
	 * @return the raw data in the RDBMS specific representation.
	 */
	byte[] get();

	/**
	 * Get the java classes this field is mapped to.
	 * @return the classes supported by the Field
	 */
	Class[] getJavaClasses();

	/**
	 * The preferred class.
	 * @return the preferred class.
	 */
	Class getPreferredClass();

	/**
	 * Returns the data mapped to the class 'clazz'.
	 * @throws MappingException if the data cannot be mapped into the specified class.
	 */
	Object get(Class clazz) throws MappingException;

	/**
	 * Get the data in the default represenation.
	 */
	Object defaultGet() throws MappingException;

	/** 
	 * Returns the RDBMS datatype it represents.
	 */
	String rdbmsType();

}
