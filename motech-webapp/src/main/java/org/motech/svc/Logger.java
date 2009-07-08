package org.motech.svc;

import java.util.List;

import org.motech.model.Log;
import org.motech.model.LogType;

public interface Logger {

	void log(LogType type, String message);

	List<Log> getLogs();
}
