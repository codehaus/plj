/*
 * Created on Oct 2, 2004
 */

package org.pgj.tools.methodfinder.impl;

import java.lang.reflect.Method;

import org.pgj.messages.TriggerCallRequest;
import org.pgj.tools.pljava.PLJavaTriggerData;
import org.pgj.typemapping.MappingException;
import org.postgresql.pljava.TriggerData;


/**
 * PL/Java method finder, it has it's own special trigger api.
 * 
 * @avalon.component name="method-finder"
 * @avalon.service type="org.pgj.tools.methodfinder.MethodFinder"
 * @author Laszlo Hornyak
 */
public class PLJavaMethodFinder extends DefaultMethodFinder {


	private final static Class[] pljava_args = new Class[]{TriggerData.class};

	protected Method findTriggerMethod(TriggerCallRequest call, Class clazz)
			throws MappingException, NoSuchMethodException {
		return clazz.getMethod(call.getMethodname(), pljava_args);
	}

	protected Object[] createParametersforTrigger(TriggerCallRequest call,
			Method method) throws MappingException {
		PLJavaTriggerData trigdata = new PLJavaTriggerData(call.getOld(), call
				.getNew());
		return new Object[]{trigdata};
	}
}