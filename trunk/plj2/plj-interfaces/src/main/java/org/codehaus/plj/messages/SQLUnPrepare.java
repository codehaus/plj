/*
 * Created on Nov 7, 2004
 */

package org.codehaus.plj.messages;

/**
 * Free plan for the resource.
 * 
 * @author Laszlo Hornyak
 */
public class SQLUnPrepare extends SQL {
	private int planid = 0;
	public int getPlanid() {
		return planid;
	}
	public void setPlanid(int planid) {
		this.planid = planid;
	}
}
