package org.motechproject.server.model;

import java.sql.Time;

/**
 * Represents a blackout interval for the message delivery system(s).
 * 
 * @author batkinson
 * 
 */
public class Blackout {

	Integer id;
	Time startTime;
	Time endTime;

	Blackout() {
	}

	public Blackout(Time startTime, Time endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Time getEndTime() {
		return endTime;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}
}
