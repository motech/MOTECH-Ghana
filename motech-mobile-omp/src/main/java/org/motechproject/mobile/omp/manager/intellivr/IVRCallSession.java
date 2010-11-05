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

import org.motechproject.mobile.core.model.MessageRequest;

/**
 * Represents a group of MessageRequest to be delivered to a particular user at
 * a particular number at the same time.  Tracks the number of time delivery has been attempted,
 * when it will be retried if at all, and if the session was the result of a user calling the system
 * or the reverse. 
 * @author fcbrooks
 *
 */
public class IVRCallSession {

	public static String INBOUND 	= "IN";
	public static String OUTBOUND	= "OUT";
	
	public static int OPEN 			=	0;
	public static int SEND_WAIT		=	1;
	public static int REPORT_WAIT	=	2;
	public static int CLOSED		=	3;
	
	private long id;
	private int version;
	private String userId;
	private String phone;
	private String language;
	private String callDirection;
	private int attempts;
	private int days;
	private int state;
	private Date created;
	private Date nextAttempt;
	private Set<MessageRequest> messageRequests;
	private Set<IVRCall> calls;
	
	public IVRCallSession() {}
	
	public IVRCallSession(String userId, 
							String phone, 
							String language, 
							String callDirection, 
							int attempts, 
							int days, 
							int state,
							Date created,
							Date nextAttempt) {
		this.userId = userId;
		this.phone = phone;
		this.language = language;
		this.callDirection = callDirection;
		this.attempts = attempts;
		this.days = days;
		this.state = state;
		this.created = created == null ? new Date() : created;
		this.nextAttempt = nextAttempt;
		messageRequests = new HashSet<MessageRequest>();
		calls = new HashSet<IVRCall>();
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

	/**
	 * 
	 * @return identifier for the user
	 */
	public String getUserId() {
		return userId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * 
	 * @return phone number to call. Null for INBOUND calls
	 */
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	/**
	 * 
	 * @return language of call.  Null for INBOUND
	 */
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * 
	 * @return OUT if system called user.  IN if user called system
	 */
	public String getCallDirection() {
		return callDirection;
	}

	public void setCallDirection(String callDirection) {
		this.callDirection = callDirection;
	}

	/**
	 * 
	 * @return number of attempts to call the user requested
	 */
	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	/**
	 * 
	 * @return number of days call requests have been completed for
	 */
	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * 
	 * @return next time a call rquest will be made
	 */
	public Date getNextAttempt() {
		return nextAttempt;
	}

	public void setNextAttempt(Date nextAttempt) {
		this.nextAttempt = nextAttempt;
	}

	/**
	 * 
	 * @return the underlying {@link MessageRequest}
	 */
	public Set<MessageRequest> getMessageRequests(){
		return messageRequests;
	}

	public void setMessageRequests(Set<MessageRequest> messageRequests) {
		this.messageRequests = messageRequests;
	}

	/**
	 * 
	 * @return {@link IVRCall}.  One for each OUTBOUND request or INBOUND call.
	 */
	public Set<IVRCall> getCalls() {
		return calls;
	}

	public void setCalls(Set<IVRCall> calls) {
		this.calls = calls;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + attempts;
		result = prime * result
				+ ((callDirection == null) ? 0 : callDirection.hashCode());
		result = prime * result + ((calls == null) ? 0 : calls.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + days;
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result
				+ ((messageRequests == null) ? 0 : messageRequests.hashCode());
		result = prime * result
				+ ((nextAttempt == null) ? 0 : nextAttempt.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result + state;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
		IVRCallSession other = (IVRCallSession) obj;
		if (attempts != other.attempts)
			return false;
		if (callDirection == null) {
			if (other.callDirection != null)
				return false;
		} else if (!callDirection.equals(other.callDirection))
			return false;
		if (calls == null) {
			if (other.calls != null)
				return false;
		} else if (!calls.equals(other.calls))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (days != other.days)
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (messageRequests == null) {
			if (other.messageRequests != null)
				return false;
		} else if (!messageRequests.equals(other.messageRequests))
			return false;
		if (nextAttempt == null) {
			if (other.nextAttempt != null)
				return false;
		} else if (!nextAttempt.equals(other.nextAttempt))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (state != other.state)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {	
		
		StringBuilder builder = new StringBuilder();
		
		builder.append("[IVRCallSession");
		builder.append(" id=");
		builder.append(id);
		builder.append(" version=");
		builder.append(version);
		builder.append(" userId=");
		builder.append(userId == null ? "null" : userId);
		builder.append(" phone=");
		builder.append(phone == null ? "null" : phone);
		builder.append(" language=");
		builder.append(language == null ? "null" : language);
		builder.append(" direction=");
		builder.append(callDirection == null ? "null" : callDirection);
		builder.append(" attempt=");
		builder.append(attempts);
		builder.append(" days=");
		builder.append(days);
		builder.append(" created=");
		builder.append(created == null ? "null" : created);
		builder.append(" nextAttempt=");
		builder.append(nextAttempt == null ? "null" : nextAttempt);
		builder.append(" state=");
		builder.append(state == OPEN ? "OPEN" : 
					  (state == SEND_WAIT ? "SEND_WAIT" : 
					  (state == REPORT_WAIT ? "REPORT_WAIT" : 
					  (state == CLOSED ? "CLOSED" : "INVALID"))));
		builder.append("]");
		
		return builder.toString();
	}


}