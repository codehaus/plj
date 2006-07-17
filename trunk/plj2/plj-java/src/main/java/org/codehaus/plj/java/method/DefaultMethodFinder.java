/*
 * Created on Oct 2, 2004
 */

package org.codehaus.plj.java.method;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;

import org.codehaus.plj.TupleMapper;
import org.codehaus.plj.messages.AbstractCall;
import org.codehaus.plj.messages.CallRequest;
import org.codehaus.plj.messages.TriggerCallRequest;
import org.codehaus.plj.typemapping.Field;
import org.codehaus.plj.typemapping.MappingException;



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
			MethodFinder {

	private Logger logger = null;
	private TupleMapper tupleMapper;

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

		List paramvector = call.getParams();
		Field[] params = new Field[paramvector
				.size()];
		Class[] paramclasses = new Class[params.length];

		for (int i = 0; i < params.length; i++) {
			params[i] = (Field) paramvector.get(i);
			paramclasses[i] = params[i].getPreferredClass();
		}
		Method callm = null;

		callm = clazz.getMethod(call.getMethodname(), paramclasses);
		if (callm.getModifiers() != 9){
			logger.info("Method "+call.getMethodname()+" in class "+clazz.getName()+" should be public static.");
			throw new NoSuchMethodException(
					"A JSProc method must be public static.");
		}
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

		if (callm.getModifiers() != 9)
			throw new NoSuchMethodException(
					"A JSProc method must be public static.");

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

}