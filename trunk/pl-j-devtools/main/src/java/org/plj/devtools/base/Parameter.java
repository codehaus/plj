/*
 * Created on Feb 27, 2005
 */
package org.plj.devtools.base;


/**
 * @author Laszlo Hornyak
 */
//TODO: edit comments for Parameter
public class Parameter {

	public final static int mode_in = 1;
	public final static int mode_out = 2;
	public final static int mode_inout = 3;

	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRdbmsType() {
		return rdbmsType;
	}
	public void setRdbmsType(String rdbmsType) {
		this.rdbmsType = rdbmsType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	private String name;
	private int mode;
	private String type;
	private String rdbmsType; 
	
}
