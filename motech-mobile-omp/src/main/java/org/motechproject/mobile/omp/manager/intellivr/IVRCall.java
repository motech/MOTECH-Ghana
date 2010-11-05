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

package org.motechproject.mobile.omp.manager.intellivr;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a request for the IVR system to place an outbound call or an 
 * inbound call by a user to the IVR system
 * @author fcbrooks
 *
 */
public class IVRCall {

	private long id;
	private int version;
	private Date created;
	private Date connected;
	private Date disconnected;
	private int duration;
	private String externalId;
	private IVRCallStatus status;
	private String statusReason;
	private IVRCallSession session;
	private Set<IVRMenu> menus;
	
	public IVRCall() {}
	
	public IVRCall(Date created, Date connected,Date disconnected,int duration,String externalId,IVRCallStatus status, String statusReason, IVRCallSession session) {
		this.created = created == null ? new Date() : created;
		this.connected = connected;
		this.disconnected = disconnected;
		this.duration = duration;
		this.externalId = externalId;
		this.status = status;
		this.statusReason = statusReason;
		this.session = session;
		menus = new HashSet<IVRMenu>();
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getConnected() {
		return connected;
	}
	public void setConnected(Date connected) {
		this.connected = connected;
	}
	public Date getDisconnected() {
		return disconnected;
	}
	public void setDisconnected(Date disconnected) {
		this.disconnected = disconnected;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * 
	 * @return the identifier shared with the IVR system for identifying this call
	 */
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public IVRCallStatus getStatus() {
		return status;
	}

	public void setStatus(IVRCallStatus status) {
		this.status = status;
	}

	/**
	 * 
	 * @return a String with human readable detail about the call status
	 */
	public String getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	/**
	 * 
	 * @return the {@link IVRCallSession} this belongs to
	 */
	public IVRCallSession getSession() {
		return session;
	}

	public void setSession(IVRCallSession session) {
		this.session = session;
	}

	/**
	 * 
	 * @return the {@link IVRMenu} that make up the call
	 */
	public Set<IVRMenu> getMenus() {
		return menus;
	}

	public void setMenus(Set<IVRMenu> menus) {
		this.menus = menus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((connected == null) ? 0 : connected.hashCode());
		result = prime * result
				+ ((disconnected == null) ? 0 : disconnected.hashCode());
		result = prime * result + duration;
		result = prime * result
				+ ((externalId == null) ? 0 : externalId.hashCode());
		result = prime * result + ((menus == null) ? 0 : menus.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((statusReason == null) ? 0 : statusReason.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IVRCall other = (IVRCall) obj;
		if (connected == null) {
			if (other.connected != null)
				return false;
		} else if (!connected.equals(other.connected))
			return false;
		if (disconnected == null) {
			if (other.disconnected != null)
				return false;
		} else if (!disconnected.equals(other.disconnected))
			return false;
		if (duration != other.duration)
			return false;
		if (externalId == null) {
			if (other.externalId != null)
				return false;
		} else if (!externalId.equals(other.externalId))
			return false;
		if (menus == null) {
			if (other.menus != null)
				return false;
		} else if (!menus.equals(other.menus))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (statusReason == null) {
			if (other.statusReason != null)
				return false;
		} else if (!statusReason.equals(other.statusReason))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("[IVRCall");
		builder.append(" id=");
		builder.append(id);
		builder.append(" version=");
		builder.append(version);
		builder.append(" created=");
		builder.append(created == null ? "null" : created);
		builder.append(" connected=");
		builder.append(connected == null ? "null" : connected);
		builder.append(" disconnected=");
		builder.append(disconnected == null ? "null" : disconnected);
		builder.append(" duration=");
		builder.append(duration);
		builder.append(" externalId=");
		builder.append(externalId == null ? "null" : externalId);
		builder.append(" status=");
		builder.append(status == null ? "null" : status.name());
		builder.append(" statusReason=");
		builder.append(statusReason == null ? "null" : statusReason);
		builder.append(" session=");
		builder.append(session.getId());
		builder.append("]");
				
		return builder.toString();
	}



}
