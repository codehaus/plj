/*
 * Created on Oct 2, 2004
 */

package org.pgj.tools.methodfinder.impl;

import java.lang.reflect.Method;
import java.util.Vector;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.pgj.messages.AbstractCall;
import org.pgj.messages.CallRequest;
import org.pgj.messages.TriggerCallRequest;
import org.pgj.tools.methodfinder.MethodFinder;
import org.pgj.tools.tuplemapper.TupleMapper;
import org.pgj.typemapping.Field;
import org.pgj.typemapping.MappingException;


/**
 * Default method finder.
 * 
 * @avalon.component name="method-finder"
 * @avalon.service type="org.pgj.tools.methodfinder.MethodFinder"
 * 
 * @dna.component
 * @dna.service type="org.pgj.tools.methodfinder.MethodFinder"
 * 
 * @author Laszlo Hornyak
 */
public class DefaultMethodFinder
		implements
			MethodFinder,
			Configurable,
			Serviceable,
			LogEnabled {

	private Logger logger = null;
	protected TupleMapper tupleMapper = null;
	private boolean useDefault = true;

	/* (non-Javadoc)
	 * @see org.pgj.tools.methodfinder.MethodFinder#findMethod(org.pgj.messages.AbstractCall, java.lang.Class)
	 */
	public Method findMethod(AbstractCall call, Class clazz)
			throws MappingException, NoSuchMethodException {
		if (call instanceof CallRequest) {
			return findJSProcMethod((CallRequest) call, clazz);
		} else if (call instanceof TriggerCallRequest) {
			return findTriggerMethod((TriggerCallRequest) call, clazz);
		}
		throw new NoSuchMethodException("Call type not handled:" + call == null
				? "null"
				: call.getClass().getName());
	}

	protected Method findJSProcMethod(CallRequest call, Class clazz)
			throws MappingException, NoSuchMethodException {

		Vector paramvector = call.getParams();
		org.pgj.typemapping.Field[] params = new org.pgj.typemapping.Field[paramvector
				.size()];
		Class[] paramclasses = new Class[params.length];

		for (int i = 0; i < params.length; i++) {
			params[i] = (org.pgj.typemapping.Field) paramvector.get(i);
			paramclasses[i] = params[i].getPreferredClass();
		}
		Method callm = null;

		callm = clazz.getMethod(call.getMethodname(), paramclasses);
		if(callm.getModifiers() != 9)
			throw new NoSuchMethodException("A JSProc method must be public static.");

		return callm;
	}

	protected Method findTriggerMethod(TriggerCallRequest call, Class clazz)
			throws MappingException, NoSuchMethodException {
		
		Class[] paramClasses = null;
		if (call.getRowmode() == TriggerCallRequest.TRIGGER_ROWMODE_ROW) {
			switch (call.getReason()) {
				case TriggerCallRequest.TRIGGER_REASON_UPDATE :
					paramClasses = new Class[2];
					paramClasses[0] = tupleMapper.getMappedClass(call.getNew());
					paramClasses[1] = tupleMapper.getMappedClass(call.getOld());
					break;
				case TriggerCallRequest.TRIGGER_REASON_DELETE :
					paramClasses = new Class[1];
					paramClasses[0] = tupleMapper.getMappedClass(call.getOld());
					break;
				case TriggerCallRequest.TRIGGER_REASON_INSERT :
					paramClasses = new Class[1];
					paramClasses[0] = tupleMapper.getMappedClass(call.getNew());
					break;
			}
		} else {
			paramClasses = new Class[0];
		}
		Method callm = clazz.getMethod(call.getMethodname(), paramClasses);
		
		if(callm.getModifiers() != 9)
			throw new NoSuchMethodException("A JSProc method must be public static.");
		
		return callm;
	}

	/* (non-Javadoc)
	 * @see org.pgj.tools.methodfinder.MethodFinder#createParameters(org.pgj.messages.AbstractCall, java.lang.reflect.Method)
	 */
	public Object[] createParameters(AbstractCall call, Method method)
			throws MappingException {
		if (call instanceof CallRequest) {
			return createParametersforJSProc((CallRequest) call, method);
		} else if (call instanceof TriggerCallRequest) {
			return createParametersforTrigger((TriggerCallRequest) call, method);
		}
		return null;
	}

	protected Object[] createParametersforJSProc(CallRequest call, Method method)
			throws MappingException {

		Object[] paramobjs = new Object[method.getParameterTypes().length];

		for (int i = 0; i < paramobjs.length; i++) {
			paramobjs[i] = ((Field) call.getParams().get(i)).defaultGet();
		}
		return paramobjs;
	}

	protected Object[] createParametersforTrigger(TriggerCallRequest call,
			Method method) throws MappingException {
		Object[] paramObjects = null;
		if (call.getRowmode() == TriggerCallRequest.TRIGGER_ROWMODE_ROW) {
			switch (call.getReason()) {
				case TriggerCallRequest.TRIGGER_REASON_UPDATE :
					paramObjects = new Object[2];
					paramObjects[0] = tupleMapper.mapTuple(call.getNew());
					paramObjects[1] = tupleMapper.mapTuple(call.getOld());
					break;
				case TriggerCallRequest.TRIGGER_REASON_DELETE :
					paramObjects = new Object[1];
					paramObjects[0] = tupleMapper.mapTuple(call.getOld());
					break;
				case TriggerCallRequest.TRIGGER_REASON_INSERT :
					paramObjects = new Object[1];
					paramObjects[0] = tupleMapper.mapTuple(call.getNew());
					break;
			}
		} else {
			paramObjects = new Object[0];
		}
		return paramObjects;
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
	 */
	public void configure(Configuration arg0) throws ConfigurationException {
		useDefault = arg0.getChild("useDefault").getValueAsBoolean();
	}

	/**
	 * @see org.apache.avalon.framework.service.Serviceable#service(org.apache.avalon.framework.service.ServiceManager)
	 * 
	 * @avalon.dependency key="tuple-mapper" type="org.pgj.tools.tuplemapper.TupleMapper"
	 * 
	 * 
	 * @dna.dependency key="tuple-mapper" type="org.pgj.tools.tuplemapper.TupleMapper"
	 * 
	 */
	public void service(ServiceManager arg0) throws ServiceException {
		tupleMapper = (TupleMapper) arg0.lookup("tuple-mapper");
	}

	/* (non-Javadoc)
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger arg0) {
		logger = arg0;
	}

}