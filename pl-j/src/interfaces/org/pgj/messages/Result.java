package org.pgj.messages;

import org.pgj.typemapping.*;

/**
 * Represents a result.
 * @author bitfakk
 */
public class Result extends Message {
	/** The fields of the data */
	private Field[][] fields;

	public void setSize(int rows, int columns){
		fields = new Field[rows][columns];
	}
	
	public void set(int row, int column, Field fld){
		fields[row][column] = fld;
	}
	
	public Field get(int row, int column){
		return fields[row][column];
	}
	
	public int getRows(){
		return fields.length;
	}
	
	public int getColumns(){
		return fields[0].length;
	}
	
}
