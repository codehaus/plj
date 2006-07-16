package org.codehaus.plg.messages;

import org.codehaus.plj.typemapping.Field;

/**
 * Represents a result.
 * 
 * @author Laszlo Hornyak
 */
public class Result extends Message {

	/** The fields of the data */
	private Field[][] fields;

	/**
	 * Set the dimensions of the result (number of rows and columns). 
	 * Will delete the content if already added.
	 * @param rows		number of rows
	 * @param columns	number of coulmns
	 */
	public void setSize(int rows, int columns) {
		fields = new Field[rows][columns];
	}

	/**
	 * Set the field at the given position.
	 * 
	 * @param row		the row
	 * @param column	the column
	 * @param fld		The Field object to set.
	 */
	public void set(int row, int column, Field fld) {
		fields[row][column] = fld;
	}

	/**
	 * Get the field at the position.
	 * 
	 * @param row		the row
	 * @param column	the column
	 * @return the field at the given position
	 */
	public Field get(int row, int column) {
		return fields[row][column];
	}

	/**
	 * Get the number of rows.
	 * @return the number of rows
	 */
	public int getRows() {
		return fields.length;
	}

	/**
	 * Get the number of columns.
	 * @return the number of columns
	 */
	public int getColumns() {
		if(fields.length == 0)
			return 0;
		return fields[0].length;
	}
}
