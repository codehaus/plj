/*
 * Created on Oct 2, 2004
 */

package org.pgj.tools.methodfinder;

import java.lang.reflect.Method;

import org.pgj.messages.AbstractCall;
import org.pgj.typemapping.MappingException;


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