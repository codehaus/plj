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
import org.pgj.typemapping.TypeMapper;

/**
 * PostgreSQL type mapper.
 * @avalon.component name="postgre-typemapper" type="org.pgj.typemapping.TypeMapper"
 * @avalon.service type="org.pgj.typemapping.TypeMapper"
 */
public class PostgresTypeMapper
	implements TypeMapper, Configurable, Initializable, LogEnabled {

	HashMap map = new HashMap();

	HashMap backMap = new HashMap();

	/** Avalon logger */
	Logger logger = null;

	public PostgresTypeMapper() {
		super();
	}

	public Field map(byte[] raw_data, String type) throws MappingException {
		logger.debug("mapping: " + type);
		//if(type.equals())
		return null;
	}

	//
	//from Configurable
	//
	public void configure(Configuration conf) throws ConfigurationException {

		Configuration mapConfig = conf.getChild("map");

		Configuration[] typeConfigs = mapConfig.getChildren("type");

		try {

			for (int i = 0; i < typeConfigs.length; i++) {
				map.put(
					typeConfigs[i].getAttribute("db"),
					Class.forName(typeConfigs[i].getAttribute("class")));
			}

		} catch (ClassNotFoundException cnfe) {
			throw new ConfigurationException(
				"configured type mapper class was not found",
				cnfe);
		}

		Configuration backMapConfig = conf.getChild("backmap");
		Configuration[] backMappings = backMapConfig.getChildren("type");

		for (int i = 0; i < backMappings.length; i++) {
			Configuration backMapping = backMappings[i];
			String bmClassName = backMapping.getAttribute("class");

			if (bmClassName == null) {
				throw new ConfigurationException("class is a required attribute");
			}

			String bmType = backMapping.getAttribute("type");

			if (bmType == null) {
				throw new ConfigurationException("type is a required attribute");
			}

			Class bmClass = null;
			try {
				bmClass = Class.forName(bmClassName);
			} catch (ClassNotFoundException cnfe) {
				throw new ConfigurationException(
					"The class " + bmClassName + " does not exist.",
					cnfe);
			}

			Class mapperClass = (Class) map.get(bmType);

			if (mapperClass == null) {
				throw new ConfigurationException(
					"No typemapper configured for " + bmType + ".");
			}

			backMap.put(bmClass, mapperClass);

		}
	}

	//
	//from initializable
	//
	public void initialize() throws java.lang.Exception {
		logger.debug("initialized");
	}

	//
	//from LogEnabled
	//
	public void enableLogging(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @see TypeMapper#backMap(Object, String)
	 */
	public Field backMap(Object object, String type) throws MappingException {

		if (type == null)
			return (backMap(object));

		logger.debug(
			"backMap -ing "
				+ (object == null ? "[null]" : object.getClass().getName())
				+ " to "
				+ type);

		Class mapperClass = (Class) map.get(type);
		if (mapperClass == null) {
			throw new MappingException("No mapper class for " + type);
		}

		AbstractPGField field = null;

		try {
			field = (AbstractPGField) mapperClass.newInstance();
		} catch (InstantiationException e) {
			throw new MappingException(
				"InstantiationException. " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new MappingException(
				"IllegalAccessException. " + e.getMessage());
		}

		field.backMap(object);

		return field;
	}

	/**
	 * @see TypeMapper#backMap(Object)
	 */
	public Field backMap(Object object) throws MappingException {

		logger.debug(
			"backMap -ing "
				+ (object == null ? "[null]" : object.getClass().getName()));

		AbstractPGField fld = null;
		Class ocl = object.getClass();
		Class fcl = (Class) backMap.get(ocl);

		//this secures that derived classes can be mapped too.
		while (fcl == null && ocl != Object.class) {
			logger.debug(ocl.getName() + " cannot be mapped back, but i try its superclass.");
			ocl = ocl.getSuperclass();
			fcl = (Class) backMap.get(ocl);
		}

		//check if no back mapping possible
		if(fcl == null){
			logger.debug("did not find backmapping.");
			throw new MappingException(ocl.getName() + " cannot be mapped back");
		}
		try {
			fld = (AbstractPGField) fcl.newInstance();
		} catch (InstantiationException e) {
			throw new MappingException(
				"InstantiationException. " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new MappingException(
				"IllegalAccessException. " + e.getMessage());
		}
		fld.backMap(object);

		return fld;

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
}