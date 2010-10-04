/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) ${year} The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.server.util;

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