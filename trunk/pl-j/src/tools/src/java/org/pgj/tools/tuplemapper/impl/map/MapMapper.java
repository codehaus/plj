/*
 * Created on Apr 8, 2004
 */
package org.pgj.tools.tuplemapper.impl.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.pgj.tools.tuplemapper.TupleMapper;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.Tuple;


/**
 * Maps the Tuple into a Map.
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="maptuplemapper" lifestyle="singleton"
 * @avalon.service type="org.pgj.tools.tuplemapper.TupleMapper"
 */
public class MapMapper implements LogEnabled, TupleMapper{

	Logger logger = null;
	
	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.tuplemapper.TupleMapper#mapTuple(org.pgj.typemapping.Tuple)
	 */
	public Object mapTuple(Tuple tuple) throws MappingException {
		HashMap map = new HashMap();
		Map fldmap = tuple.getFieldMap();
		Iterator it = fldmap.keySet().iterator();
		while(it.hasNext()){
			String fldName = (String)it.next();
			Field fld = (Field)fldmap.get(fldName);
			map.put(fldName, fld.defaultGet());
		}
		return map;
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.tuplemapper.TupleMapper#getMappedClass(org.pgj.typemapping.Tuple)
	 */
	public Class getMappedClass(Tuple tuple) {
		return Map.class;
	}


}
