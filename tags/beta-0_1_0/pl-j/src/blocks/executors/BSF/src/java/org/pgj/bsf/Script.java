/*
 * Created on Oct 16, 2004
 */
package org.pgj.bsf;


/**
 * A Script bean.
 * @author Laszlo Hornyak
 */
public class Script {

	private String language = null;
	private String source = null;
	private String scrName = null;

	public Script(String lang, String src, String name) {
		language = lang;
		source = src;
		scrName = name;
	}


	
	public String getLanguage() {
		return language;
	}
	public String getSource() {
		return source;
	}
	public String getName() {
		return scrName;
	}
	public void setName(String name) {
		this.scrName = name;
	}
}
