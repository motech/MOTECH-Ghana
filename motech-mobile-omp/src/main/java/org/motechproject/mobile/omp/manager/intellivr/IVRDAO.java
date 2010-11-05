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
import java.util.List;

/**
 * DAO for accessing the IVR specific types in the database
 * @author fcbrooks
 *
 */
public interface IVRDAO {

	/**
	 * 
	 * @param callSession
	 * @return id of saved/updated session
	 */
	public long saveIVRCallSession(IVRCallSession callSession);
	
	/**
	 * 
	 * @return List of all {@link IVRCallSession}
	 */
	public List<IVRCallSession> loadIVRCallSessions();
	
	/**
	 * 
	 * @param user
	 * @return List of {@link IVRCallSession} for provided user
	 */
	public List<IVRCallSession> loadIVRCallSessionsByUser(String user);
	
	/**
	 * 
	 * @param phone
	 * @return List of {@link IVRCallSession} for provided phone number
	 */
	public List<IVRCallSession> loadIVRCallSessionsByPhone(String phone);
	
	/**
	 * 
	 * @param id
	 * @return {@link IVRCallSession} with provided id
	 */
	public IVRCallSession loadIVRCallSession(long id);
	
	/**
	 * 
	 * @param states
	 * @return List of {@link IVRCallSession} in provided states
	 */
	public List<IVRCallSession> loadIVRCallSessionsByState(Integer[] states);
	
	/**
	 * 
	 * @param user
	 * @param phone
	 * @param language
	 * @param states
	 * @param attempts
	 * @param days
	 * @param callDirection
	 * @return List of {@link IVRCallSession} matching criteria
	 */
	public List<IVRCallSession> loadIVRCallSessions(String user,String phone,String language, Integer[] states, int attempts, int days, String callDirection);
	
	/**
	 * 
	 * @param states
	 * @param date
	 * @return List of {@link IVRCallSession} in provided state with nextAttempt prior to date
	 */
	public List<IVRCallSession> loadIVRCallSessionsByStateNextAttemptBeforeDate(Integer[] states, Date date);
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @return List of {@link IVRCallSession} with created between start and end
	 */
	public List<IVRCallSession> loadIVRCallSessionsCreatedBetweenDates(Date start, Date end);
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @return Count of {@link IVRCallSession} with created between start and end
	 */
	public int countIVRCallSessionsCreatedBetweenDates(Date start, Date end);
	
	/**
	 * 
	 * @return Count of all {@link IVRCallSession}
	 */
	public int countIVRCallSesssions();
	
	/**
	 * 
	 * @return Count of all {@link IVRCall}
	 */
	public int countIVRCalls();
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @return Count of {@link IVRCall} created between start and end
	 */
	public int countIVRCallsCreatedBetweenDates(Date start, Date end);
	
	/**
	 * 
	 * @param status
	 * @return Count of all {@link IVRCall} with provided {@link IVRCallStatus}
	 */
	public int countIVRCallsWithStatus(IVRCallStatus status);
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @param status
	 * @return
	 */
	public int countIVRCallsCreatedBetweenDatesWithStatus(Date start, Date end, IVRCallStatus status);
	
	/**
	 * 
	 * @param externalId
	 * @return {@link IVRCall} with provided external id
	 */
	public IVRCall loadIVRCallByExternalId(String externalId);
	
	/**
	 * The system is onyl aware of recordings it has received reports about.  This
	 * may be fewer than those defined on the actual IVR system
	 * @return List of stats for each recording
	 */
	public List<IVRRecordingStat> getIVRRecordingStats();
	
	/**
	 * 
	 * @return List with one entry for each {@link IVRCallStatus} type
	 */
	public List<IVRCallStatusStat> getIVRCallStatusStats();
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @return Same as above but for {@link IVRCall} with created between start and end
	 */
	public List<IVRCallStatusStat> getIVRCallStatusStatsBetweenDates(Date start, Date end);	
}