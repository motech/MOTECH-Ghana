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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class IntellIVRDAO implements IVRDAO {

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public long saveIVRCallSession(IVRCallSession callSession) {
		return ((Long)sessionFactory.getCurrentSession().save(callSession)).longValue();
	}
	
	@SuppressWarnings("unchecked")
	public List<IVRCallSession> loadIVRCallSessions() {
		return sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCallSession.class)
		.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<IVRCallSession> loadIVRCallSessionsByUser(String user) {
		return sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCallSession.class)
		.add(Restrictions.eq("userId", user))
		.list();
	}

	@SuppressWarnings("unchecked")
	public List<IVRCallSession> loadIVRCallSessionsByPhone(String phone) {
		return sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCallSession.class)
		.add(Restrictions.eq("phone", phone))
		.list();		
	}
	
	public IVRCallSession loadIVRCallSession(long id) {
		return (IVRCallSession)sessionFactory.getCurrentSession().load(IVRCallSession.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<IVRCallSession> loadIVRCallSessionsByState(Integer[] states) {
		return sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCallSession.class)
		.add(Restrictions.in("state", states))
		.list();
	}

	@SuppressWarnings("unchecked")
	public List<IVRCallSession> loadIVRCallSessions(
			String user, String phone, String language, Integer[] states, int attempts, int days, String callDirection) {
		return sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCallSession.class)
		.add(Restrictions.eq("userId", user))
		.add(Restrictions.eq("phone", phone))
		.add(Restrictions.in("state", states))
		.add(Restrictions.eq("attempts", attempts))
		.add(Restrictions.eq("days", days))
		.add(Restrictions.eq("callDirection", callDirection))
		.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<IVRCallSession> loadIVRCallSessionsByStateNextAttemptBeforeDate(
			Integer[] states, Date date) {
		return sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCallSession.class)
		.add(Restrictions.in("state", states))
		.add(Restrictions.le("nextAttempt", date))
		.list();
		
	}
	
	public IVRCall loadIVRCallByExternalId(String externalId) {
		return (IVRCall)sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCall.class)
		.add(Restrictions.eq("externalId", externalId))
		.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<IVRCallSession> loadIVRCallSessionsCreatedBetweenDates(
			Date start, Date end) {
		return sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCallSession.class)
		.add(Restrictions.ge("created", start))
		.add(Restrictions.le("created", end))
		.list();
	}

	public int countIVRCallSessionsCreatedBetweenDates(Date start, Date end) {
		return (Integer)sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCallSession.class)
		.setProjection(Projections.rowCount())
		.add(Restrictions.ge("created", start))
		.add(Restrictions.le("created", end))
		.list()
		.get(0);
	}

	public int countIVRCallSesssions() {
		return (Integer)sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCallSession.class)
		.setProjection(Projections.rowCount())
		.list()
		.get(0);
	}

	public int countIVRCalls() {
		return (Integer)sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCall.class)
		.setProjection(Projections.rowCount())
		.list()
		.get(0);
	}

	public int countIVRCallsCreatedBetweenDates(Date start, Date end) {
		return (Integer)sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCall.class)
		.setProjection(Projections.rowCount())
		.add(Restrictions.ge("created", start))
		.add(Restrictions.le("created", end))
		.list()
		.get(0);
	}

	public int countIVRCallsWithStatus(IVRCallStatus status) {
		return (Integer)sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCall.class)
		.setProjection(Projections.rowCount())
		.add(Restrictions.eq("status", status))
		.list()
		.get(0);
	}

	public int countIVRCallsCreatedBetweenDatesWithStatus(Date start, Date end, IVRCallStatus status) {
		return (Integer)sessionFactory
		.getCurrentSession()
		.createCriteria(IVRCall.class)
		.setProjection(Projections.rowCount())
		.add(Restrictions.ge("created", start))
		.add(Restrictions.le("created", end))
		.add(Restrictions.eq("status", status))
		.list()
		.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<IVRRecordingStat> getIVRRecordingStats() {
		List queryResult = 
		sessionFactory
		.getCurrentSession()
		.createQuery("select name, count(ivr_menu_id), avg(duration) from org.motechproject.mobile.omp.manager.intellivr.IVRMenu group by name")
		.list();
		List<IVRRecordingStat> returnValue = new ArrayList<IVRRecordingStat>();
		for ( Object o : queryResult ) {
			Object[] row = (Object[]) o;
			returnValue.add(new IVRRecordingStat((String)row[0], (Long)row[1], (Double)row[2]));
		}
		return returnValue;
	}

	@SuppressWarnings("unchecked")
	public List<IVRCallStatusStat> getIVRCallStatusStats() {
		List queryResult = 
			sessionFactory
			.getCurrentSession()
			.createQuery("select status, count(ivr_call_id) from org.motechproject.mobile.omp.manager.intellivr.IVRCall group by status")
			.list();
		Map<IVRCallStatus, Object[]> map = new HashMap<IVRCallStatus, Object[]>();
		for ( Object o : queryResult ) {
			Object[] row = (Object[])o;
			map.put((IVRCallStatus)row[0], row);
		}
		List<IVRCallStatusStat> returnValue = new ArrayList<IVRCallStatusStat>();
		for ( IVRCallStatus s : IVRCallStatus.values() ) {
			if ( map.containsKey(s) ) {
				Object[] o = map.get(s);
				returnValue.add(new IVRCallStatusStat((IVRCallStatus)o[0], (Long)o[1]));
			} else
				returnValue.add(new IVRCallStatusStat(s,0));
		}
		return returnValue;
	}

	@SuppressWarnings("unchecked")
	public List<IVRCallStatusStat> getIVRCallStatusStatsBetweenDates(
			Date start, Date end) {
		List queryResult = 
			sessionFactory
			.getCurrentSession()
			.createQuery("select status, count(ivr_call_id) from org.motechproject.mobile.omp.manager.intellivr.IVRCall where created >= :start and created <= :end group by status")
			.setTimestamp("start", start)
			.setTimestamp("end", end)
			.list();
		Map<IVRCallStatus, Object[]> map = new HashMap<IVRCallStatus, Object[]>();
		for ( Object o : queryResult ) {
			Object[] row = (Object[])o;
			map.put((IVRCallStatus)row[0], row);
		}
		List<IVRCallStatusStat> returnValue = new ArrayList<IVRCallStatusStat>();
		for ( IVRCallStatus s : IVRCallStatus.values() ) {
			if ( map.containsKey(s) ) {
				Object[] o = map.get(s);
				returnValue.add(new IVRCallStatusStat((IVRCallStatus)o[0], (Long)o[1]));
			} else
				returnValue.add(new IVRCallStatusStat(s,0));
		}
		return returnValue;
	}


}