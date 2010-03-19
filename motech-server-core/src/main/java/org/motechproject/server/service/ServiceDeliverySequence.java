package org.motechproject.server.service;

import java.util.Date;

public interface ServiceDeliverySequence {

	String getName();

	boolean meetsRequirements(Integer patientId, Date date);

	void updateServiceDeliveries(Integer patientId, Date date);

	Integer getDeliveryId(ServiceDelivery service, Integer patientId);

	Date getEarliestDate(ServiceDelivery service, Integer patientId);

	Date getLatestDate(ServiceDelivery service, Integer patientId);

	Date getPreferredStartDate(ServiceDelivery service, Integer patientId);

	Date getPreferredEndDate(ServiceDelivery service, Integer patientId);

}
