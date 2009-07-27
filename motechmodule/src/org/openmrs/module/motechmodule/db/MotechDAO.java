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
package org.openmrs.module.motechmodule.db;

import java.util.Date;
import java.util.List;

import org.motech.model.FutureServiceDelivery;
import org.motech.model.Log;

public interface MotechDAO {
	
	Integer saveFutureServiceDelivery(FutureServiceDelivery fsd);
	
	void updateFutureServiceDelivery(FutureServiceDelivery fsd);
	
	List<FutureServiceDelivery> getFutureServiceDeliveries(Date startDate, Date endDate);
	
	List<FutureServiceDelivery> getFutureServiceDeliveries();
	
	Integer saveLog(Log log);
	
	List<Log> getLogs();
	
}
