/*
 * Created on Apr 8, 2004
 */

package org.pgj.tools.tuplemapper.impl.reflected;

import java.lang.reflect.InvocationTargetException;
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
import org.apache.commons.beanutils.BeanUtils;
import org.pgj.tools.tuplemapper.TupleMapper;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.Tuple;


/**
 * A typemapper that operates with reflection.
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="reflectedtuplemapper" lifestyle="singleton"
 * @avalon.service type="org.pgj.tools.tuplemapper.TupleMapper"
 */
public class ReflectedTupleMapper
		implements
			LogEnabled,
			Configurable,
			TupleMapper, Serviceable{

	Logger logger = null;
	Map classmap = new HashMap();

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		try {
			Configuration[] confs = arg0.getChildren("relation");
			for (int i = 0; i < confs.length; i++) {
				classmap.put(confs[i].getAttribute("name"), Class
						.forName(confs[i].getAttribute("class")));
			}
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException("Class not found.", arg0, e);
		}
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.tuplemapper.TupleMapper#mapTuple(org.pgj.typemapping.Tuple)
	 */
	public Object mapTuple(Tuple tuple) throws MappingException {
		try {
			String classname = (String) classmap.get(tuple.getRelationName());
			Class cls = Class.forName(classname);
			Object obj = cls.newInstance();
			Map fldMap = tuple.getFieldMap();
			Iterator it = fldMap.keySet().iterator();
			while (it.hasNext()) {
				String fldName = (String) it.next();
				org.pgj.typemapping.Field fld = (Field) fldMap.get(fldName);
				//TODO: this shouldn`t work only with defaultget
				BeanUtils.setProperty(obj, fldName, fld.defaultGet());
			}
			return obj;
		} catch (ClassNotFoundException e) {
			throw new MappingException(e);
		} catch (InstantiationException e) {
			throw new MappingException(e);
		} catch (IllegalAccessException e) {
			throw new MappingException(e);
		} catch (InvocationTargetException e){
			throw new MappingException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.tuplemapper.TupleMapper#getMappedClass(org.pgj.typemapping.Tuple)
	 */
	public Class getMappedClass(Tuple tuple) {
		return (Class) classmap.get(tuple.getRelationName());
	}

	/**
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 */
	public void service(ServiceManager arg0) throws ServiceException {
	}


}