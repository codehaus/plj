/*
 * Created on Oct 13, 2004
 */
package org.pgj.bsf;


/**
 * Interface for the script repository.
 * 
 * @author Laszlo Hornyak
 */
public interface ScriptRepository {
	Script loadScript(String name) throws ScriptNotFoundException, ScriptStoreException;
	void storeScript(Script script) throws ScriptStoreException;
	void deleteScript(String name) throws ScriptStoreException, ScriptNotFoundException;
}
