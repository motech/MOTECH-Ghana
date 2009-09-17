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
package org.motech.openmrs.module.impl;

import java.util.Date;
import java.util.List;

import org.motech.model.FutureServiceDelivery;
import org.motech.model.Log;
import org.motech.model.db.hibernate.HibernateMotechDAO;
import org.motech.openmrs.module.MotechService;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;

import com.dreamoval.motech.webapp.webservices.MessageService;

/**
 * An implementation of the MotechService interface using OpenMRS.
 */
public class MotechServiceImpl extends BaseOpenmrsService implements
		MotechService {

	private HibernateMotechDAO motechDAO;
	private MessageService mobileService;

	public HibernateMotechDAO getMotechDAO() {
		return motechDAO;
	}

	public void setMotechDAO(HibernateMotechDAO motechDAO) {
		this.motechDAO = motechDAO;
	}

	public List<FutureServiceDelivery> getAllFutureServiceDeliveries() {
		return motechDAO.getFutureServiceDeliveries();
	}

	public List<Log> getAllLogs() {
		return motechDAO.getLogs();
	}

	public List<FutureServiceDelivery> getFutureServiceDeliveries(
			Date startDate, Date endDate) {
		return motechDAO.getFutureServiceDeliveries(startDate, endDate);
	}

	public void saveFutureServiceDelivery(FutureServiceDelivery service) {
		motechDAO.saveFutureServiceDelivery(service);
	}

	public void saveLog(Log log) {
		motechDAO.saveLog(log);
	}

	public void updateFutureServiceDelivery(FutureServiceDelivery service) {
		motechDAO.updateFutureServiceDelivery(service);
	}

	public User getUserByPhoneNumber(String phoneNumber) {
		Integer phoneAttributeTypeId = Context.getPersonService()
				.getPersonAttributeTypeByName("Phone Number")
				.getPersonAttributeTypeId();
		// If more than one user matches phone number, first user in list is
		// returned
		Integer userId = motechDAO.getUsersByPersonAttribute(
				phoneAttributeTypeId, phoneNumber).get(0);
		return Context.getUserService().getUser(userId);
	}

	public MessageService getMobileService() {
		return mobileService;
	}

	public void setMobileService(MessageService mobileService) {
		this.mobileService = mobileService;
	}

}
