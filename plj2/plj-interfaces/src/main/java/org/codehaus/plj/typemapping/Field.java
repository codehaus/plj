package org.codehaus.plj.typemapping;

/**
 * Represensts a data of the RDBMS.
 * A field is responsible for parsing and mapping raw datatype to java types.
 * 
 * @author Laszlo Hornyak
 */
public interface Field {

	/**
	 * Tells if the field is null or not.
	 * @return true if the field is NULL.
	 */
	boolean isNull();

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
	 * @param clazz
	 * @return
	 * @throws MappingException if the data cannot be mapped into the specified class.
	 */
	Object get(Class clazz) throws MappingException;

	/**
	 * Get the data in the default represenation.
	 * @return
	 * @throws MappingException
	 */
	Object defaultGet() throws MappingException;

	/** 
	 * Returns the RDBMS datatype it represents.
	 * @return RDBMS datatype this class represents.
	 */
	String rdbmsType();

}
