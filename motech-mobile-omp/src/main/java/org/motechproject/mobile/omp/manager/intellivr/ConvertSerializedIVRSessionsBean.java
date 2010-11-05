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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.MessageRequest;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Standalone program for converting {@link IVRSession} objects that have been serialized to the
 * file system in the previous version of the {@link IntellIVRBean} to the new style {@link IVRCallSession}
 * objects that are persisted to the database.  SEtting dry run to true will just result in a print
 * out of the objects that would be created based on the input.
 * @author fcbrooks
 *
 */
public class ConvertSerializedIVRSessionsBean {

	private IVRDAO ivrDao;
	private Resource ivrSessionSerialResource;
	private long bundlingDelay;
	private boolean dryRun;
	
	public IVRDAO getIvrDao() {
		return ivrDao;
	}

	public void setIvrDao(IVRDAO ivrDao) {
		this.ivrDao = ivrDao;
	}

	public Resource getIvrSessionSerialResource() {
		return ivrSessionSerialResource;
	}

	public void setIvrSessionSerialResource(Resource ivrSessionSerialResource) {
		this.ivrSessionSerialResource = ivrSessionSerialResource;
	}
	
	public long getBundlingDelay() {
		return bundlingDelay;
	}

	public void setBundlingDelay(long bundlingDelay) {
		this.bundlingDelay = bundlingDelay;
	}

	public boolean isDryRun() {
		return dryRun;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	@Transactional
	public void convert() {
		
		if ( dryRun )
			System.out.println("WARNING - Dry run set to true.  No data will be written to database.");

		Map<String, IVRSession> ivrSessions = loadIvrSessions();

		for ( IVRSession session : ivrSessions.values() ){
			Date now = new Date();
			IVRCallSession newSession = new IVRCallSession(
					session.getUserId()		== null ? null : session.getUserId(), 
					session.getPhone()		== null ? null : session.getPhone(), 
					session.getLanguage()	== null ? null : session.getLanguage(), 
					session.getUserId()		== null ? IVRCallSession.INBOUND : IVRCallSession.OUTBOUND, 
					session.getAttempts(), 
					session.getDays(), 
					session.getState(), 
					getNextAttemptDate(session), 
					session.getState() 		== IVRSession.OPEN ? addToDate(now, GregorianCalendar.MILLISECOND, (int)bundlingDelay) : getNextAttemptDate(session));
			if ( session.getState() == IVRSession.REPORT_WAIT ) {
				IVRCall call = new IVRCall(
						new Date(),
						null, 
						null, 
						0, 
						session.getSessionId(), 
						IVRCallStatus.REQUESTED, 
						session.getUserId() == null ? "Client called IVR system" : "Call request accepted", 
						newSession);
				newSession.getCalls().add(call);
			}
			Set<MessageRequest> messageRequests = new TreeSet<MessageRequest>(new MessageRequestComparator());
			for ( GatewayRequest request : session.getGatewayRequests() )
				messageRequests.add(request.getMessageRequest());
			newSession.getMessageRequests().addAll(messageRequests);
			if ( !dryRun )
				ivrDao.saveIVRCallSession(newSession);
			System.out.println("Created session: " + newSession);
			for ( IVRCall c : newSession.getCalls())
				System.out.println("Added call: " + c);
		}
		
	}

	private Date getNextAttemptDate(IVRSession session) {
		
		Date nextAttemptDate = null;
		boolean dateSet = false;
		
		for ( GatewayRequest gr : session.getGatewayRequests() ) {
			if ( !dateSet && gr.getDateFrom() != null ) {
				nextAttemptDate = gr.getDateFrom();
				dateSet = true;
			}
		}
					
		return nextAttemptDate == null ? new Date() : nextAttemptDate;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String, IVRSession> loadIvrSessions() {

		Map<String, IVRSession> loadedSessions = new HashMap<String, IVRSession>();

		if ( ivrSessionSerialResource != null ) {

			ObjectInputStream objIn = null;

			try {

				objIn = new ObjectInputStream(new FileInputStream(ivrSessionSerialResource.getFile()));

				loadedSessions = (Map<String, IVRSession>)objIn.readObject();

				for ( IVRSession s : loadedSessions.values() ) {
					System.out.println("Loaded existing session " + s.getSessionId());
				}

				return loadedSessions;

			} catch (IOException e) {
				System.out.println("Cached IVRSessions not loaded due to following error: " + e.getMessage());
			} catch (ClassNotFoundException e) {
				System.out.println("Cached IVRSessions not loaded due to following error: " + e.getMessage());
			} finally {
				if ( objIn != null )
					try {
						objIn.close();
					} catch (IOException e) {
					}
			}

		}

		return loadedSessions;
		
	}
	
	private Date addToDate(Date start, int field, int amount) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(start);
		cal.add(field, amount);
		return cal.getTime();
	}

	private class MessageRequestComparator implements Comparator<MessageRequest> {
		public int compare(MessageRequest m1, MessageRequest m2) {
			return m1.getId().compareTo(m2.getId());
		}		
	}
}
