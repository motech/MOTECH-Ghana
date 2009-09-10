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
package org.motech.openmrs.module;

import java.util.Date;
import java.util.List;

import org.motech.model.FutureServiceDelivery;
import org.motech.model.Log;
import org.openmrs.User;
import org.openmrs.api.OpenmrsService;
import org.springframework.transaction.annotation.Transactional;

import com.dreamoval.motech.webapp.webservices.MessageService;

public interface MotechService extends OpenmrsService {

	@Transactional
	void saveFutureServiceDelivery(FutureServiceDelivery service);

	@Transactional
	void updateFutureServiceDelivery(FutureServiceDelivery service);

	@Transactional
	void saveLog(Log log);

	@Transactional(readOnly = true)
	List<FutureServiceDelivery> getAllFutureServiceDeliveries();

	@Transactional(readOnly = true)
	List<FutureServiceDelivery> getFutureServiceDeliveries(Date startDate,
			Date endDate);

	@Transactional(readOnly = true)
	List<Log> getAllLogs();

	@Transactional(readOnly = true)
	User getUserByPhoneNumber(String phoneNumber);

	MessageService getMobileService();
}
