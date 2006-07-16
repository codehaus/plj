package org.codehaus.plg.messages;

/**
 * SQL fetch message.
 * 
 * @author Laszlo Hornyak
 */
public class SQLFetch extends SQLCursor{
	
	/** fetch direction: forward. */
	public final static int FETCH_FORWARD = 0;
	/** fetch direction: backward. */
	public final static int FETCH_BACKWARD = 1;
	
	/** Direction of the data fetch (FETCH_BACKWARD or FETCH_FORWARD (default) ) */
	private int direction = FETCH_FORWARD;
	/** Number of records to fetch */
	private int count = 1;
	
	public int getDirection(){
		return direction;
	}
	
	public void setDirection(int direction){
		this.direction = direction;
	}
	
	public int getCount(){
		return count;
	}
	
	public void setCount(int count){
		this.count = count;
	}
	
}

