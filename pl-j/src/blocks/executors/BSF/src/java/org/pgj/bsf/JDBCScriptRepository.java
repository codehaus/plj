/*
 * Created on Oct 16, 2004
 */
package org.pgj.bsf;


/**
 * JDBC script repository to run 
 * 
 * @author Laszlo Hornyak
 */
public class JDBCScriptRepository implements ScriptRepository{

	/* (non-Javadoc)
	 * @see org.pgj.bsf.ScriptLoader#loadScript(java.lang.String)
	 */
	public Script loadScript(String name) throws ScriptNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.pgj.bsf.ScriptLoader#storeScript(org.pgj.bsf.Script)
	 */
	public void storeScript(Script script) throws ScriptStoreException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.pgj.bsf.ScriptLoader#deleteScript(java.lang.String)
	 */
	public void deleteScript(String name) throws ScriptStoreException, ScriptNotFoundException {
		// TODO Auto-generated method stub
		
	}

}
