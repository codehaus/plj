/*
 * Created on Oct 16, 2004
 */
package org.pgj.bsf;


/**
 * A Script bean.
 * @author Laszlo Hornyak
 */
class Script {

	private String language = null;
	private String source = null;

	public Script(String lang, String src) {
		language = lang;
		source = src;
	}


	
	public String getLanguage() {
		return language;
	}
	public String getSource() {
		return source;
	}
}
