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
package org.openmrs.module.motechmodule.impl;

import java.util.Date;
import java.util.List;

import org.motech.model.FutureServiceDelivery;
import org.motech.model.Log;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.motechmodule.MotechService;
import org.openmrs.module.motechmodule.db.hibernate.HibernateMotechDAO;

public class MotechServiceImpl extends BaseOpenmrsService implements MotechService {
	
	private HibernateMotechDAO motechDAO;
	
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
	
	public List<FutureServiceDelivery> getFutureServiceDeliveries(Date startDate, Date endDate) {
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
	
}
