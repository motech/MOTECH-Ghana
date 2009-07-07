package org.motech.svc;

import org.motech.model.LogType;

public interface Logger {

	void log(LogType type, String message);

}
