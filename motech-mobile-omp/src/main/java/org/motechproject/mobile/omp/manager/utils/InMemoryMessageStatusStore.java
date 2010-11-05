/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
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

package org.motechproject.mobile.omp.manager.utils;

/*
 * Written by Brent Atkinson
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InMemoryMessageStatusStore implements MessageStatusStore {

	private Log log = LogFactory.getLog(InMemoryMessageStatusStore.class);

	long ttl = 1000 * 60 * 15;
	long cleanupInterval = 1000 * 60 * 5;
	long lastCleanup;
	Map<String, MessageStatusEntry> statusMap = new HashMap<String, MessageStatusEntry>();

	public void setTtl(long ttl) {
		this.ttl = ttl;
	}

	public long getTtl() {
		return ttl;
	}

	public long getCleanupInterval() {
		return cleanupInterval;
	}

	public void setCleanupInterval(long cleanupInterval) {
		this.cleanupInterval = cleanupInterval;
	}

	public Map<String, MessageStatusEntry> getStatusMap() {
		return statusMap;
	}

	public void setStatusMap(Map<String, MessageStatusEntry> statusMap) {
		this.statusMap = statusMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.MessageStatusStore#updateStatus(java.lang.String,
	 * com.dreamoval.motech.core.model.MStatus)
	 */
	public void updateStatus(String id, String status) {
		try {
			if (status == null) {
				statusMap.remove(id);
				return;
			}
			MessageStatusEntry statusEntry = statusMap.get(id);
			if (statusEntry == null)
				statusMap.put(id, statusEntry = new MessageStatusEntry());
			statusEntry.setStatus(status);
			statusEntry.setLastUpdate(System.currentTimeMillis());
		} finally {
			requestCleanup();
		}
	}

	private void requestCleanup() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastCleanup > cleanupInterval) {
			try {
				removeDeadEntries();
			} finally {
				lastCleanup = currentTime;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.example.MessageStatusStore#getStatus(java.lang.String)
	 */
	public String getStatus(String id) {
		try {
			MessageStatusEntry statusEntry = statusMap.get(id);
			return statusEntry == null ? null : statusEntry.getStatus();
		} finally {
			requestCleanup();
		}
	}

	private void removeDeadEntries() {

		int removed = 0;

		log.debug("vacuuming dead status entries");

		long currentTime = System.currentTimeMillis();
		Set<String> deadKeys = new HashSet<String>();

		for (String msgId : statusMap.keySet()) {
			MessageStatusEntry statusEntry = statusMap.get(msgId);
			if (statusEntry != null
					&& currentTime - statusEntry.getLastUpdate() > getTtl()) {
				deadKeys.add(msgId);
			}
		}

		for (String deadKey : deadKeys) {
			statusMap.remove(deadKey);
			removed++;
		}

		if (log.isDebugEnabled())
			log.debug("vacuumed " + removed + " dead status entries");
	}

	
}

class MessageStatusEntry {

	private String status;
	private long lastUpdate;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

}

