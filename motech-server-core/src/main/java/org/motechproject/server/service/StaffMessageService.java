package org.motechproject.server.service;

import org.motechproject.server.annotation.RunWithPrivileges;
import org.motechproject.server.model.ghana.Facility;
import org.openmrs.util.OpenmrsConstants;

import java.util.Date;


public interface StaffMessageService {

    @RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES,
			OpenmrsConstants.PRIV_VIEW_USERS })
    void sendStaffCareMessages(Date startDate, Date endDate,
                               Date deliveryDate, Date deliveryTime,
                               String[] careGroups,
                               boolean sendUpcoming,
                               boolean blackoutEnabled);


    void sendUpcomingMessages(Date startDate, Date endDate, Date deliveryDate, String[] careGroups, Facility facility);

    void sendDefaulterMessages(Date startDate, Date deliveryDate, String[] careGroups, Facility facility);
}
