/*
 * Created on Apr 8, 2004
 */
package org.deadcat_enterprises;


/**
 * Represents a record of the test table. Triggers will love it.
 * @author Laszlo Hornyak
 */
public class TestTableRecord {

	private Integer id;
	private String name;

	/**
	 * @return Returns the id.
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
}
