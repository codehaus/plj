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
import org.apache.commons.beanutils.PropertyUtils;
import org.pgj.tools.classloaders.ClassStoreException;
import org.pgj.tools.classloaders.PLJClassLoader;
import org.pgj.tools.tuplemapper.TupleMapper;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.Tuple;
import org.pgj.typemapping.TypeMapper;

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
			TupleMapper,
			Serviceable {

	Logger logger = null;
	Map classNameMap = new HashMap();
	Map classNameBackMap = new HashMap();
	PLJClassLoader classLoader = null;

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
			Configuration[] confs = arg0.getChildren("relation");
			for (int i = 0; i < confs.length; i++) {
				String name = confs[i].getAttribute("name");
				String cls = confs[i].getAttribute("class");
				classNameMap.put(name, cls);
				classNameBackMap.put(cls, name);
			}
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.tuplemapper.TupleMapper#mapTuple(org.pgj.typemapping.Tuple)
	 */
	public Object mapTuple(Tuple tuple) throws MappingException {
		try {
			Class cls = classLoader.load((String)classNameMap.get(tuple.getRelationName()));
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
		} catch (InstantiationException e) {
			throw new MappingException(e);
		} catch (IllegalAccessException e) {
			throw new MappingException(e);
		} catch (InvocationTargetException e) {
			throw new MappingException(e);
		} catch (ClassNotFoundException e) {
			throw new MappingException(e);
		} catch (ClassStoreException e) {
			throw new MappingException("Error loading class",e);
		}
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.tuplemapper.TupleMapper#getMappedClass(org.pgj.typemapping.Tuple)
	 */
	public Class getMappedClass(Tuple tuple) {
		if (tuple == null) {
			logger.debug("null tuple -> null class");
			return null;
		}
		logger.debug(tuple.getRelationName());
		Class ret;
		try {
			ret = classLoader.load((String)classNameMap.get(tuple.getRelationName()));
		} catch (ClassNotFoundException e) {
			logger.error("getMappedClass: class not found", e);
			return null;
		} catch (ClassStoreException e) {
			logger.error("getMappedClass: class store problem", e);
			return null;
		}
		logger.debug("class: " + ret);
		return ret;
	}

	/**
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 * 
	 * @avalon.dependency key="classloader" type="org.pgj.tools.classloaders.PLJClassLoader"
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		classLoader = (PLJClassLoader) arg0.lookup("classloader");
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.tuplemapper.TupleMapper#backMap(java.lang.Object)
	 */
	public Tuple backMap(Object obj, TypeMapper typeMapper)
			throws MappingException {
		if (obj == null) {
			throw new MappingException("can't map null");
		}

		try {
			Tuple t = new Tuple();
			String clname = obj.getClass().getName();
			String relname = (String) classNameBackMap.get(clname);

			t.setRelationName(relname);
			Map desc = BeanUtils.describe(obj);
			Iterator i = desc.keySet().iterator();
			while (i.hasNext()) {
				String key = i.next().toString();
				if (!"class".equals(key)) {
					logger.debug("value: "+PropertyUtils
							.getProperty(obj, key).getClass().getName());
					t.addField(key, typeMapper.backMap(PropertyUtils
							.getProperty(obj, key)));
				}
			}

			return t;
		} catch (IllegalAccessException e) {
			logger.error("backmap", e);
			throw new MappingException("backmap", e);
		} catch (InvocationTargetException e) {
			logger.error("backmap", e);
			throw new MappingException("backmap", e);
		} catch (NoSuchMethodException e) {
			logger.error("backmap", e);
			throw new MappingException("backmap", e);
		}
	}


}