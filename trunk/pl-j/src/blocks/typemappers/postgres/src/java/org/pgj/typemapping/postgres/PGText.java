/*
 * Created on Nov 6, 2004
 */
package org.pgj.typemapping.postgres;


/**
 * I am lazy.
 * 
 * @author Laszlo Hornyak
 */
//TODO the character classes need a major review
public class PGText extends PGVarchar {

	public String rdbmsType() {
		return "text";
	}
}
