package org.codehaus.plj.postgresql;

import org.codehaus.plj.typemapping.MappingException;

/**
 * Void datatype for PostgreSQL.
 * 
 * @author Laszlo Hornyak
 */
public class PGVoid extends AbstractPGField {

	public boolean isNull() {
		return true;
	}
	/**
	 * Constructor for PGVoid.
	 */
	public PGVoid() {
		super();
	}

	private static final Class[] cls = new Class[] {Void.class};
	
	public Class[] getJavaClasses() {
		return cls;
	}

	public Class getPreferredClass() {
		return Void.class;
	}

	public Object get(Class clazz) throws MappingException {
		return null;
	}

	public Object defaultGet() throws MappingException {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.Field#rdbmsType()
	 */
	public String rdbmsType() {
		return "void";
	}

	protected void backMap(Object obj) throws MappingException {
	}
}
