
package org.pgj.typemapping.postgres;

import java.util.HashMap;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.pgj.messages.Result;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;
import org.pgj.typemapping.ReturnMappingException;
import org.pgj.typemapping.TypeMapper;

/**
 * PostgreSQL type mapper.
 * 
 * @author Laszlo Hornyak
 * 
 * @avalon.component name="postgre-typemapper" lifestyle="singleton"
 * @avalon.service type="org.pgj.typemapping.TypeMapper"
 * 
 * @dna.component name="postgre-typemapper"
 * @dna.service type="org.pgj.typemapping.TypeMapper"
 * 
 */
public class PostgresTypeMapper
		implements
			TypeMapper,
			Configurable,
			Initializable,
			LogEnabled {

	/** Map for maping types into java classes */
	private HashMap map = new HashMap();

	/** Map for maping classes into DB types. */
	private HashMap backMap = new HashMap();

	/** Map classnames into rdbms types. */
	private HashMap typeBackMap = new HashMap();

	/** Avalon logger */
	private Logger logger = null;

	public PostgresTypeMapper() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.TypeMapper#map(byte[], java.lang.String)
	 */
	public Field map(byte[] raw_data, String type) throws MappingException {
		try {
			logger.debug("mapping: " + type);
			Class clazz = (Class) map.get(type);
			if (clazz == null)
				throw new MappingException("no field type configured for "
						+ type);
			AbstractPGField fld = (AbstractPGField) clazz.newInstance();
			if (raw_data == null) {
				fld.setNull(true);
			} else {
				fld.set(raw_data);
			}
			return fld;
		} catch (InstantiationException e) {
			throw new MappingException(
					"Configuration error. Class could not be instantiated", e);
		} catch (IllegalAccessException e) {
			throw new MappingException(
					"Configuration error. Class could not be instantiated", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration conf) throws ConfigurationException {

		Configuration mapConfig = conf.getChild("map");

		Configuration[] typeConfigs = mapConfig.getChildren("type");

		try {

			for (int i = 0; i < typeConfigs.length; i++) {
				map.put(typeConfigs[i].getAttribute("db"), Class
						.forName(typeConfigs[i].getAttribute("class")));
			}

		} catch (ClassNotFoundException cnfe) {
			throw new ConfigurationException(
					"configured type mapper class was not found", cnfe);
		}

		Configuration backMapConfig = conf.getChild("backmap");
		Configuration[] backMappings = backMapConfig.getChildren("type");

		for (int i = 0; i < backMappings.length; i++) {
			Configuration backMapping = backMappings[i];
			String bmClassName = backMapping.getAttribute("class");

			if (bmClassName == null) {
				throw new ConfigurationException(
						"class is a required attribute");
			}

			String bmType = backMapping.getAttribute("type");

			if (bmType == null) {
				throw new ConfigurationException("type is a required attribute");
			}

			Class bmClass = null;
			typeBackMap.put(bmClassName, bmType);
			try {
				bmClass = Class.forName(bmClassName);
			} catch (ClassNotFoundException cnfe) {
				throw new ConfigurationException("The class " + bmClassName
						+ " does not exist.", cnfe);
			}

			Class mapperClass = (Class) map.get(bmType);

			if (mapperClass == null) {
				throw new ConfigurationException(
						"No typemapper configured for " + bmType + ".");
			}

			backMap.put(bmClass, mapperClass);

		}
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.activity.Initializable#initialize()
	 */
	public void initialize() throws java.lang.Exception {
		logger.debug("initialized");
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @see TypeMapper#backMap(Object, String)
	 */
	public Field backMap(Object object, String type) throws MappingException {

		if (type == null)
			return (backMap(object));

		logger.debug("backMap -ing "
				+ (object == null ? "[null]" : object.getClass().getName())
				+ " to " + type);

		Class mapperClass = (Class) map.get(type);
		if (mapperClass == null) {
			throw new MappingException("No mapper class for " + type);
		}

		AbstractPGField field = null;

		try {
			field = (AbstractPGField) mapperClass.newInstance();
		} catch (InstantiationException e) {
			throw new MappingException("InstantiationException. "
					+ e.getMessage());
		} catch (IllegalAccessException e) {
			throw new MappingException("IllegalAccessException. "
					+ e.getMessage());
		}

		field.backMap(object);

		return field;
	}

	/**
	 * @see TypeMapper#backMap(Object)
	 */
	public Field backMap(Object object) throws MappingException {

		if (object == null) {
			return null;
		}

		try {
			logger
					.debug("backMap -ing "
							+ (object == null ? "[null]" : object.getClass()
									.getName()));

			AbstractPGField fld = null;
			Class ocl = object.getClass();
			Class fcl = (Class) backMap.get(ocl);

			//this secures that derived classes can be mapped too.
			while (fcl == null && ocl != Object.class) {
				logger.debug(ocl.getName()
						+ " cannot be mapped back, but i try its superclass.");
				ocl = ocl.getSuperclass();
				fcl = (Class) backMap.get(ocl);
			}

			//check if no back mapping possible
			if (fcl == null) {
				logger.debug("did not find backmapping.");
				throw new MappingException(ocl.getName()
						+ " cannot be mapped back");
			}
			try {
				fld = (AbstractPGField) fcl.newInstance();
			} catch (InstantiationException e) {
				throw new MappingException("InstantiationException. "
						+ e.getMessage());
			} catch (IllegalAccessException e) {
				throw new MappingException("IllegalAccessException. "
						+ e.getMessage());
			}
			fld.backMap(object);

			return fld;
		} catch (MappingException e) {
			throw new ReturnMappingException(e);
		}

	}

	/**
	 * @see TypeMapper#canBackMap(Object)
	 */
	public boolean canBackMap(Object obj) {
		if (obj == null)
			return true;

		Class objClass = obj.getClass();
		return (backMap.get(objClass) != null);

	}

	/**
	 * @see TypeMapper#canMap(String)
	 */
	public boolean canMap(String typeName) {
		return map.get(typeName) != null;
	}

	/**
	 * @see TypeMapper#createResult(Object)
	 */
	public Result createResult(Object obj) throws MappingException {

		Result ret = new Result();

		ret.setSize(1, 1);

		ret.set(0, 0, backMap(obj));

		return ret;
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.TypeMapper#getRDBMSTypeFor(java.lang.Class)
	 */
	public String getRDBMSTypeFor(Class cl) throws MappingException {
		String typ = (String) typeBackMap.get(cl.getName());
		if (typ == null)
			throw new MappingException("no backmapping for" + cl);
		return typ;
	}

	/* (non-Javadoc)
	 * @see org.pgj.typemapping.TypeMapper#createResult(java.lang.Object, java.lang.String, boolean)
	 */
	public Result createResult(Object obj, String expect, boolean strict)
			throws MappingException {
		Result ret = new Result();

		ret.setSize(1, 1);

		Class eMapperClass = (Class) this.map.get(expect);
		if (expect == null) {
			if (strict)
				throw new MappingException(
						"No mapping for expected result type ");
		}
		AbstractPGField efld = null;

		try {
			efld = (AbstractPGField) eMapperClass.newInstance();
			efld.backMap(obj);
		} catch (MappingException e) {
			if (strict)
				throw e;
			return createResult(obj);
		} catch (InstantiationException e) {
			throw new MappingException("Reflection error on backmapping", e);
		} catch (IllegalAccessException e) {
			throw new MappingException("Reflection error on backmapping", e);
		}

		ret.set(0, 0, efld);

		return ret;
	}
}