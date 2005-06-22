/*
 * Created on Apr 21, 2005
 */
package org.deadcat.pljava.triggers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.postgresql.pljava.TriggerData;


/**
 * A very simple trigger. It is for logging.
 * 
 * @author Laszlo Hornyak
 */
public final class LoggingTrigger {
	
	private final static Logger logger = Logger.getLogger(LoggingTrigger.class);
	
	public static void updateTrigger(TriggerData trigger) throws SQLException {
		ResultSet _old = trigger.getOld();
		ResultSet _new = trigger.getNew();
		String newName = _new.getString("name");
		String oldName = _old.getString("name");
		logger.info("Name was changed from "+oldName+" to "+newName);
	}
	
}
