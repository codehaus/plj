/*
 * Created on Apr 9, 2004
 */
package org.deadcat_enterprises;

import java.util.Map;

import org.apache.log4j.Category;

/**
 * Triggers with maps.
 * @author Laszlo Hornyak
 */
public class MapTriggers {

	static Category cat = Category.getInstance(MapTriggers.class);

	public static void testTableDeleteTrigger(Map _old){
		cat.warn("this record is deleted:"+_old.get("name"));
	}

}
