/*
 * Created on Apr 8, 2004
 */

package org.pgj.tools.tuplemapper.impl.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.pgj.tools.tuplemapper.TupleMapper;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.Tuple;
import org.pgj.typemapping.TypeMapper;


/**
 * Maps the Tuple into a Map.
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="map-tuplemapper" lifestyle="singleton"
 * @avalon.service type="org.pgj.tools.tuplemapper.TupleMapper"
 */
public class MapMapper
		implements
			LogEnabled,
			TupleMapper,
			Configurable,
			Serviceable {

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
		while (it.hasNext()) {
			String fldName = (String) it.next();
			Field fld = (Field) fldmap.get(fldName);
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

	Map backmap = new HashMap();

	/* (non-Javadoc)
	 * @see org.pgj.tools.tuplemapper.TupleMapper#backMap(java.lang.Object)
	 */
	public Tuple backMap(Object obj) throws MappingException {
		// TODO Auto-generated method stub
		if (obj == null)
			throw new MappingException("Tuple can`t be NULL");
		if (obj instanceof Map)
			throw new MappingException("This mapper works only with maps");
		Map map = (Map) obj;
		Tuple tuple = new Tuple();
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Object val = map.get(key);

		}
		return tuple;
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		// TODO Auto-generated method stub
		Configuration backmaps = arg0.getChild("backmaps");
		Configuration[] items = backmaps.getChildren("backmap");
		for (int i = 0; i < items.length; i++) {
			String classname = items[i].getAttribute("className");
			String tupleType = items[i].getAttribute("tupleType");
			backmap.put(classname, tupleType);
		}
	}

	/**
	 * @avalon.dependency type="org.pgj.typemapping.TypeMapper"
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		// TODO Auto-generated method stub
		TypeMapper typemapper = (TypeMapper)arg0.lookup(TypeMapper.class.getName());
	}


}