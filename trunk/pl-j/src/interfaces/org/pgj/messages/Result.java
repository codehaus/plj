package org.pgj.messages;

import org.pgj.typemapping.Field;

/**
 * Represents a result.
 * 
 * @author Laszlo Hornyak
 */
public class Result extends Message {

	/** The fields of the data */
	private Field[][] fields;

	public void setSize(int rows, int columns) {
		fields = new Field[rows][columns];
	}

	public void set(int row, int column, Field fld) {
		fields[row][column] = fld;
	}

	public Field get(int row, int column) {
		return fields[row][column];
	}

	public int getRows() {
		return fields.length;
	}

	public int getColumns() {
		if(fields.length == 0)
			return 0;
		return fields[0].length;
	}
}
