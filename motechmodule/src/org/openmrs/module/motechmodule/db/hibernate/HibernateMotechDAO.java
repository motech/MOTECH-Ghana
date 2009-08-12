/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.motechmodule.db.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.Log;
import org.openmrs.module.motechmodule.db.MotechDAO;

public class HibernateMotechDAO implements MotechDAO {
	
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public Integer saveFutureServiceDelivery(FutureServiceDelivery fsd) {
		Session session = sessionFactory.getCurrentSession();
		return (Integer) session.save(fsd);
	}
	
	public void updateFutureServiceDelivery(FutureServiceDelivery fsd) {
		Session session = sessionFactory.getCurrentSession();
		session.merge(fsd);
	}
	
	@SuppressWarnings("unchecked")
	public List<FutureServiceDelivery> getFutureServiceDeliveries(Date startDate, Date endDate) {
		Session session = sessionFactory.getCurrentSession();
		return (List<FutureServiceDelivery>) session.createCriteria(FutureServiceDelivery.class).add(
		    Restrictions.between("date", startDate, endDate)).add(
		    Restrictions.or(Restrictions.isNull("patientNotifiedDate"), Restrictions.isNull("userNotifiedDate"))).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<FutureServiceDelivery> getFutureServiceDeliveries() {
		Session session = sessionFactory.getCurrentSession();
		return (List<FutureServiceDelivery>) session.createCriteria(FutureServiceDelivery.class).list();
	}
	
	public Integer saveLog(Log log) {
		Session session = sessionFactory.getCurrentSession();
		return (Integer) session.save(log);
	}
	
	@SuppressWarnings("unchecked")
	public List<Log> getLogs() {
		Session session = sessionFactory.getCurrentSession();
		return (List<Log>) session.createCriteria(Log.class).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getUsersByPersonAttribute(Integer personAttributeTypeId, String personAttributeValue) {
		Session session = sessionFactory.getCurrentSession();
		return (List<Integer>) session.createSQLQuery(
		    "select u.user_id from person p inner join users u on u.user_id = p.person_id "
		            + "inner join person_attribute a on p.person_id = a.person_id "
		            + "where a.voided = false and p.voided = false and u.voided = false "
		            + "and a.person_attribute_type_id = :typeId and a.value = :value group by u.user_id").setInteger(
		    "typeId", personAttributeTypeId).setString("value", personAttributeValue).list();
	}
}
