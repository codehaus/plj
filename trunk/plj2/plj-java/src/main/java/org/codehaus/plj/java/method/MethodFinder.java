/*
 * Created on Oct 2, 2004
 */

package org.codehaus.plj.java.method;

import java.lang.reflect.Method;

import org.codehaus.plj.messages.AbstractCall;
import org.codehaus.plj.typemapping.MappingException;


/**
 * A Component interface to help find methods.
 * 
 * @author Laszlo Hornyak
 */
public interface MethodFinder {

	Method findMethod(AbstractCall call, Class clazz) throws MappingException,
			NoSuchMethodException;

	Object[] createParameters(AbstractCall call, Method method)
			throws MappingException;
}