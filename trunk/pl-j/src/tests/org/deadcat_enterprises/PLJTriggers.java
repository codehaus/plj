/*
 * Created on Apr 8, 2004
 */
package org.deadcat_enterprises;

import org.apache.log4j.Category;


/**
 * Trigger test class.
 * @author Laszlo Hornyak
 */
public class PLJTriggers {
	private static Category cat = Category.getInstance(PLJTriggers.class);

	public void testTableInsertTrigger(TestTableRecord _new){
		cat.warn("adding to testtable: " + _new.getName());
	}

	public void testTableUpdateTrigger(TestTableRecord _old, TestTableRecord _new){
		cat.warn("the entry: " + _old.getName() + " is modified to " + _new.getName());
	}

}
