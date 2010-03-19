package org.motech.util;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A handler for for java.util.logging. It diverts the logging messages to the
 * log4j framework. It is useful in general for this purpose, but was initially
 * written to recapture the logging of libraries that use java.util logging
 * (notably the JAW-WS RI).
 * 
 * @author batkinson
 * 
 */
public class JDKL4JHandler extends Handler {

	private Map<String, SoftReference<org.apache.log4j.Logger>> cachedLogs = new ConcurrentHashMap<String, SoftReference<org.apache.log4j.Logger>>();

	private org.apache.log4j.Logger getLog(String logName) {
		SoftReference<org.apache.log4j.Logger> logRef = cachedLogs.get(logName);
		if (logRef == null || logRef.get() == null) {
			org.apache.log4j.Logger log = org.apache.log4j.Logger
					.getLogger(logName);
			logRef = new SoftReference<org.apache.log4j.Logger>(log);
			cachedLogs.put(logName, logRef);
		}
		return logRef.get();
	}

	@Override
	public void publish(LogRecord record) {
		org.apache.log4j.Logger log = getLog(record.getLoggerName());
		String message = record.getMessage();
		Throwable exception = record.getThrown();
		Level level = record.getLevel();
		if (level == Level.SEVERE) {
			log.error(message, exception);
		} else if (level == Level.WARNING) {
			log.warn(message, exception);
		} else if (level == Level.INFO) {
			log.info(message, exception);
		} else if (level == Level.CONFIG) {
			log.debug(message, exception);
		} else {
			log.trace(message, exception);
		}
	}

	@Override
	public void flush() {
		// nothing to do
	}

	@Override
	public void close() {
		// nothing to do
	}
}