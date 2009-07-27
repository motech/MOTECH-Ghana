package org.motech.model;

import java.util.Date;

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
