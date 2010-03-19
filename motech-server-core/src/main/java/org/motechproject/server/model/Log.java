package org.motechproject.server.model;

import java.util.Date;

import org.motechproject.ws.LogType;

/**
 * Represents an application log message as opposed to system-level log
 * messages. This type of message is intended to be persisted to a logging table
 * for presumably reporting purposes.
 */
public class Log {

	private Integer id;
	private LogType type;
	private Date date;
	private String message;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LogType getType() {
		return type;
	}

	public void setType(LogType type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
