/*
 * Created on Apr 3, 2004
 */

package org.pgj.typemapping;

import java.util.HashMap;
import java.util.Map;


/**
 * Tuple is a row of a table or view.
 * and it can be mapped to anything. Tipicaly sent by triggers as new and old.
 * @author Laszlo Hornyak
 */
public class Tuple {

	private String relationName = null;

	//TODO: map is not realy safe for this purpose...
	private Map fieldMap = new HashMap();

	/**
	 * @return
	 */
	public Map getFieldMap() {
		return fieldMap;
	}

	/**
	 * @param fieldName
	 * @param field
	 */
	public void addField(String fieldName, Field field) {
		fieldMap.put(fieldName, field);
	}

	/**
	 * @param fieldName
	 * @return
	 */
	public Field getField(String fieldName) {
		return (Field) fieldMap.get(fieldName);
	}

	/**
	 * @return Returns the relationName.
	 */
	public String getRelationName() {
		return relationName;
	}

	/**
	 * @param relationName The relationName to set.
	 */
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
}