/*
 * Created on Feb 27, 2004
 */

package org.pgj.messages;

/**
 * Log message.
 * 
 * @author Laszlo Hornyak
 */
public class Log extends Message {

	public static final int LEVEL_DEBUG = 1;
	public static final int LEVEL_INFO = 2;
	public static final int LEVEL_WARN = 3;
	public static final int LEVEL_ERROR = 4;
	public static final int LEVEL_FATAL = 5;
	private int level;
	private String category = null;
	private String message;

	/**
	 * @return Returns the category.
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category The category to set.
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return Returns the level.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level The level to set.
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message The message to set.
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
