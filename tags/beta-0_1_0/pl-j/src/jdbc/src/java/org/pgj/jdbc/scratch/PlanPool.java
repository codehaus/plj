/*
 * Created on Nov 13, 2004
 */

package org.pgj.jdbc.scratch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.pgj.Client;
import org.pgj.messages.SQLUnPrepare;
import org.pgj.tools.utils.ClientUtils;


/**
 * Plan pool for preparedstatements.
 * 
 * @author Laszlo Hornyak
 */
public class PlanPool {

	private static InheritableThreadLocal tl = new InheritableThreadLocal();

	/**
	 * Get the planpool associated to the thread.
	 * @return The PlanPool instance associated to the thread.
	 */
	public synchronized static PlanPool getPlanPool() {
		PlanPool pp = (PlanPool) tl.get();
		if (pp == null) {
			pp = new PlanPool();
			pp.cli = ClientUtils.getClientforThread();
			tl.set(pp);
		}
		//if the thread did work for a previous client
		//TODO this should be somewhat more friendly (cleanup by executor probably)
		if (pp.cli != ClientUtils.getClientforThread()) {
			tl.set(null);
			return getPlanPool();
		}
		return pp;
	}

	/** The map of statement -> ArrayList of plans */
	private HashMap map = new HashMap();
	
	/** All the plan pool entries */
	private Collection allPlans = new HashSet();

	/** The count of plans */
	private int planCount = 0;

	/** The session the pool is valid in. */
	private Client cli = null;

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
		/** parameter classes */
		public Class[] params;
	}

	int getPlan(String statement, Class[] params, PLJJDBCConnection conn)
			throws SQLException {
		if (conn.getBooleanFromConf("clientThreadingEnabled")) {
			synchronized (this) {
				return doGetPlan(statement, params);
			}
		}
		return doGetPlan(statement, params);
	}

	void closePlan(String statement, Class[] params, PLJJDBCConnection conn)
			throws SQLException {
		if (conn.getBooleanFromConf("clientThreadingEnabled")) {
			synchronized (this) {
				doClosePlan(statement, params, conn);
			}
		} else {
			doClosePlan(statement, params, conn);
		}
	}

	private void doClosePlan(String statement, Class[] params,
			PLJJDBCConnection conn) {
		Collection a = (Collection) map.get(statement);
		Iterator i = a.iterator();
		while (i.hasNext()) {

			PlanPoolEntry e = (PlanPoolEntry) i.next();
			if (!doMatch(e, params))
				continue;

			if (e == null)
				return; //XXX should warn or something!
			e.refCount--;
			return;
		}
		planCount--;

	}

	private int doGetPlan(String statement, Class[] params) {
		Collection l = (Collection) map.get(statement);
		if (l == null) {
			l = new ArrayList();
			map.put(statement, l);
		} else {
			Iterator i = l.iterator();

			while (i.hasNext()) {
				PlanPoolEntry e = (PlanPoolEntry) i.next();
				if (doMatch(e, params))
					return e.plan;
			}
		}
		return -1;
	}

	void putPlan(String statement, Class[] params, int planid,
			PLJJDBCConnection conn) throws SQLException {
		if (conn.getBooleanFromConf("clientThreadingEnabled")) {
			synchronized (this) {
				doPutPlan(statement, params, planid, conn);
			}
		} else {
			doPutPlan(statement, params, planid, conn);
		}
	}

	private void doPutPlan(String statement, Class[] params, int planid,
			PLJJDBCConnection conn) throws SQLException {
		if (planCount > 256) {
			
			PlanPoolEntry worst = null;
			Iterator i = allPlans.iterator();
			while(i.hasNext()){
				PlanPoolEntry e = (PlanPoolEntry)i.next();
				if(e.refCount!= 0)
					continue;
				if(worst == null){
					worst = e;
					continue;
				}
				if(e.requestCnt < worst.requestCnt)
					worst = e;
			}
			
			if(worst != null){
				SQLUnPrepare up = new SQLUnPrepare();
				up.setPlanid(worst.plan);
				conn.doSendMessage(up); //no need for ansver

				List l = (List) map.get(worst.statement);
				l.remove(worst);
				allPlans.remove(worst);
				planCount--;
			}
		}

		PlanPoolEntry ppe = new PlanPoolEntry();
		ppe.statement = statement;
		ppe.plan = planid;
		ppe.params = params;
		ArrayList a = (ArrayList) map.get(statement);
		if (a == null) {
			a = new ArrayList();
			map.put(statement, a);
		}
		a.add(ppe);
		allPlans.add(ppe);
		planCount++;
	}

	private boolean doMatch(PlanPoolEntry ppe, Class[] params) {
		if (ppe.params.length != params.length)
			return false;
		for (int i = 0; i < params.length; i++) {
			if (ppe.params[i] != params[i])
				return false;
		}
		return true;
	}

}