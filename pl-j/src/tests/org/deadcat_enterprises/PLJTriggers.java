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

	public TestTableRecord testTableInsertTrigger(TestTableRecord _new) {
		cat.warn("id: "+_new.getId());
		cat.warn("adding to testtable: " + _new.getName());
		_new.setName("(checked by trigger)".concat(_new.getName()));
		return _new;
	}

	public void testTableUpdateTrigger(TestTableRecord _old,
			TestTableRecord _new) {
		cat.warn("the entry: " + _old.getName() + " is modified to "
				+ _new.getName());
	}

	public TestTableRecord testTable2InsertTrigger(TestTableRecord _new) {
		cat.warn("adding to testtable: " + _new.getName());
		_new.getName().replaceAll("kocka", "K0CK@");
		return _new;
	}

	public TestTableRecord beforeInsertRowTrigger(TestTableRecord _new) {
		cat.warn("before insert trigger - testtable2: " + _new.getName() + " with id "+_new.getId());
		_new.setName(_new.getName().concat("(before)"));
		return _new;
	}
	
	public TestTableRecord afterInsertRowTrigger(TestTableRecord _new) {
		cat.warn("after insert trigger - testtable2: " + _new.getName() + " with id "+_new.getId());
		_new.setName(_new.getName().concat("(before)"));
		return _new;
	}
	
	
}