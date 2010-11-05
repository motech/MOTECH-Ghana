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

import java.util.List;

/**
 * Interface to a variety of IVR data that is useful for creating 
 * operational reports.
 * 
 * @author fcbrooks
 *
 */
public interface IVRCallStatsProvider {
	
	/**
	 * 
	 * @return Count of all the IVRCallSession created
	 */
	int getCountIVRCallSessions();
	
	/**
	 * @param minutes
	 * @return Count of the IVRCallSessions created in prior minutes provided
	 */
	int getCountIVRSessionsInLastMinutes(int minutes);
	
	/**
	 * @param hours
	 * @return Count of the IVRCallSessions created in the prior hours
	 */
	int getCountIVRCallSessionsInLastHours(int hours);
	
	/**
	 * @param days
	 * @return Count of the IVRCallSessions created in the prior days calendar days
	 */
	int getCountIVRCallSessionsInLastDays(int days);
	
	/**
	 * 
	 * @return Count of all IVRCalls created
	 */
	int getCountIVRCalls();
	
	/**
	 * 
	 * @param minutes
	 * @return Count of all IVRCalls in prior minutes
	 */
	int getCountIVRCallsInLastMinutes(int minutes);
	
	/**
	 * 
	 * @param hours
	 * @return Count of all IVRCalls in prior hours
	 */
	int getCountIVRCallsInLastHours(int hours);
	
	/**
	 * 
	 * @param days
	 * @return Count of allIVRCalls in prior calendar days
	 */
	int getCountIVRCallsInLastDays(int days);
	
	/**
	 * 
	 * @param status
	 * @return Count of IVRCalls with given status
	 */
	int getCountIVRCallsWithStatus(IVRCallStatus status);
	
	/**
	 * 
	 * @param minutes
	 * @param status
	 * @return Count of IVRCalls in prior minutes with provided status
	 */
	int getCountIVRCallsInLastMinutesWithStatus(int minutes, IVRCallStatus status);
	
	/**
	 * 
	 * @param hours
	 * @param status
	 * @return Count of IVRCalls in the prior hours with provided status
	 */
	int getCountIVRCallsInLastHoursWithStatus(int hours, IVRCallStatus status);
	
	/**
	 * 
	 * @param days
	 * @param status
	 * @return Count of IVRCalls with the prior calendar days with provided status
	 */
	int getCountIVRCallsInLastDaysWithStatus(int days, IVRCallStatus status);
	
	/**
	 *	Returns one {@link IVRRecordingStat} entry for each distinct recording
	 *	name a report has been received about.
	 * @return List of {@link IVRRecordingStat} 
	 */
	List<IVRRecordingStat> getIVRRecordingStats();
	
	/**
	 * 
	 * @return List with one entry for each {@link IVRCallStatus} type
	 */
	List<IVRCallStatusStat> getIVRCallStatusStats();
	
	/**
	 * 
	 * @param minutes
	 * @return Same as above but filtered by prior minutes
	 */
	List<IVRCallStatusStat> getIVRCallStatusStatsFromLastMinutes(int minutes);
	
	/**
	 * 
	 * @param hours
	 * @return Same as above but filtered by prior hours
	 */
	List<IVRCallStatusStat> getIVRCallStatusStatsFromLastHours(int hours);
	
	/**
	 * 
	 * @param days
	 * @return Same as above but filtered by prior calendar day
	 */
	List<IVRCallStatusStat> getIVRCallStatusStatsFromLastDays(int days);
	
	/**
	 * 
	 * @return All {@link IVRCallSession}
	 */
	List<IVRCallSession> getIVRCallSessions();
	
	/**
	 * 
	 * @param minutes
	 * @return {@link IVRCallSession} created in prior minutes
	 */
	List<IVRCallSession> getIVRCallSessionsInLastMinutes(int minutes);
	
	/**
	 * 
	 * @param hours
	 * @return {@link IVRCallSession} created in prior hours
	 */
	List<IVRCallSession> getIVRCallSessionsInLastHours(int hours);
	
	/**
	 * 
	 * @param days
	 * @return {@link IVRCallSession} created in prior calendar days
	 */
	List<IVRCallSession> getIVRCallSessionsInLastDays(int days);
	
	/**
	 * 
	 * @param user
	 * @return {@link IVRCallSession} for provided user
	 */
	List<IVRCallSession> getIVRCallSessionsForUser(String user);
	
	/**
	 * 
	 * @param phone
	 * @return {@link IVRCallSession} for provided phone number
	 */
	List<IVRCallSession> getIVRCallSessionsForPhone(String phone);
}
