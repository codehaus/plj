package org.pgj.tools;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.sun.tools.corba.se.idl.toJavaPortable.Compile;

/*
 * Created on Sep 7, 2003
 */

/**
 * A Hack IDL task. This class is Sun JDK dependent, I hope once a day there will 
 * be a common interface for compilers.
 * @author Laszlo Hornyak
 * @version 0.1
 */
public class IDLTask extends Task {

	String IDL = null;
	String side = "server";
	String includes = null;
	String output = null;
	boolean keep = false;

	/**
	 * 
	 */
	public IDLTask() {
		super();
	}

	/**
	 * @return
	 */
	public String getIdl() {
		return IDL;
	}

	/**
	 * @param string
	 */
	public void setIdl(String string) {
		IDL = string;
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		if(IDL == null)
			throw new BuildException("IDL property is mandatory");
		
		Vector v = new Vector();
		v.add("-f");
		v.add(side);
		if(output != null){
			v.add("-td");
			v.add(output);
		}
		if(includes != null){
			v.add("-i");
			v.add(includes);
		}
		if(keep)
			v.add("-keep");
		
		v.add(IDL);
		
		String args[] = new String[v.size()];
		for(int i = 0; i < args.length; i++)
			args[i] = (String)v.get(i);
		
		Compile.main(args);
	}

	/**
	 * @return
	 */
	public String getIncludes() {
		return includes;
	}

	/**
	 * @return
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * @return
	 */
	public String getSide() {
		return side;
	}

	/**
	 * @param string
	 */
	public void setIncludes(String string) {
		includes = string;
	}

	/**
	 * @param string
	 */
	public void setOutput(String string) {
		output = string;
	}

	/**
	 * @param string
	 */
	public void setSide(String string) {
		side = string;
	}

	/**
	 * @return
	 */
	public boolean isKeep() {
		return keep;
	}

	/**
	 * @param b
	 */
	public void setKeep(boolean b) {
		keep = b;
	}

}
