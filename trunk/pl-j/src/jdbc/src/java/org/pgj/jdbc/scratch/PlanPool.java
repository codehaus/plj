/*
 * Created on Nov 13, 2004
 */

package org.pgj.jdbc.scratch;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import org.pgj.messages.SQLUnPrepare;


/**
 * Plan pool for preparedstatements.
 * 
 * @author Laszlo Hornyak
 */
class PlanPool {

	private static InheritableThreadLocal tl = new InheritableThreadLocal();

	synchronized static PlanPool getPlanPool() {
		PlanPool pp = (PlanPool) tl.get();
		if (pp == null) {
			pp = new PlanPool();
			tl.set(pp);
		}
		return pp;
	}

	private HashMap map = new HashMap();

	private class PlanPoolEntry {

		/** just for statistic information, when it was created... */
		public long created = System.currentTimeMillis();
		/** how many times it was executed. */
		public long requestCnt = 0;
		/** The prpeared statement in the from it was sent to the DB */
		public String statement = null;
		/** the plan id */
		public int plan = 0;
		/** how many open preparedstatement is using it */
		public int refCount = 0;
	}

	public int getPlan(String statement, PLJJDBCConnection conn)
			throws SQLException {
		if (conn.getBooleanFromConf("clientThreadingEnabled")) {
			synchronized (this) {
				return doGetPlan(statement);
			}
		} 
		return doGetPlan(statement);
	}

	void closePlan(String statement, PLJJDBCConnection conn)
			throws SQLException {
		if (conn.getBooleanFromConf("clientThreadingEnabled")) {
			synchronized (this) {
				doClosePlan(statement, conn);
			}
		} else {
			doClosePlan(statement, conn);
		}
	}

	private void doClosePlan(String statement, PLJJDBCConnection conn) {
		PlanPoolEntry e = (PlanPoolEntry) map.get(statement);
		if (e == null)
			return; //XXX should warn or something!
		e.refCount--;
	}

	private int doGetPlan(String statement) {
		PlanPoolEntry e = (PlanPoolEntry) map.get(statement);
		if (e == null)
			return -1;
		e.refCount++;
		return e.plan;
	}

	void putPlan(String statement, int planid, PLJJDBCConnection conn)
			throws SQLException {
		if (conn.getBooleanFromConf("clientThreadingEnabled")) {
			synchronized (this) {
				doPutPlan(statement, planid, conn);
			}
		} else {
			doPutPlan(statement, planid, conn);
		}
	}

	private void doPutPlan(String statement, int planid, PLJJDBCConnection conn) {
		PlanPoolEntry ppe = new PlanPoolEntry();
		ppe.statement = statement;
		ppe.plan = planid;
		if (map.keySet().size() > 512) {
			//find a plan to free
			PlanPoolEntry worst = null;
			String worstPlan = null;
			Iterator i = map.keySet().iterator();
			while (i.hasNext()) {
				String planSta = (String) i.next();
				PlanPoolEntry plan = (PlanPoolEntry) map.get(worstPlan);
				if (worstPlan == null && worst == null) {
					worstPlan = planSta;
					worst = plan;
					continue;
				}
				if (plan.requestCnt > worst.requestCnt) {
					worstPlan = planSta;
					worst = plan;
				}
			}
			//unprepare
			SQLUnPrepare up = new SQLUnPrepare();
			up.setPlanid(worst.plan);
			conn.doSendMessage(up); //no need for ansver
		}

	}

}