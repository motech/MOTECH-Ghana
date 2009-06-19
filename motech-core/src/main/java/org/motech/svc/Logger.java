package org.motech.svc;

import javax.ejb.Local;

import org.motech.model.LogType;

@Local
public interface Logger {

	void log(LogType type, String message);

}
