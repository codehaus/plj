package org.pgj.messages;

public class SQLFetch extends SQLCursor{
	
	public final static int FETCH_FORWARD = 0;
	public final static int FETCH_BACKWARD = 1;
	
	int direction = FETCH_FORWARD;
	int count = 1;
	
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

