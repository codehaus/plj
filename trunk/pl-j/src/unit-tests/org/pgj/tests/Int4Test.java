/*
 * Created on Oct 12, 2004
 */
package org.pgj.tests;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


/**
 * @author Laszlo Hornyak
 */
//TODO: edit comments for Int4Test
public class Int4Test extends BasicTest {

	public void testInt4Addition() throws Exception {
		PreparedStatement sta = conn.prepareStatement("select plpgj_test_int4_add(?, ?)");
		sta.setInt(1, -200);
		sta.setInt(2, 300);
		ResultSet res = sta.executeQuery();
		if(!res.next())
			throw new Exception("");
		if(res.getInt(1) != 100) throw new Exception("WRONG!!!");
	}

	public void testInt4Substraction() throws Exception {
		PreparedStatement sta = conn.prepareStatement("select plpgj_test_int4_sub(?, ?)");
		sta.setInt(1, -200);
		sta.setInt(2, 300);
		ResultSet res = sta.executeQuery();
		if(!res.next())
			throw new Exception("");
		if(res.getInt(1) != -500) throw new Exception("WRONG!!!");
	}

	public void testInt4Multiplication() throws Exception {
		PreparedStatement sta = conn.prepareStatement("select plpgj_test_int4_mul(?, ?)");
		sta.setInt(1, -2);
		sta.setInt(2, 3);
		ResultSet res = sta.executeQuery();
		if(!res.next())
			throw new Exception("");
		if(res.getInt(1) != -6) throw new Exception("WRONG!!!");
	}
}
