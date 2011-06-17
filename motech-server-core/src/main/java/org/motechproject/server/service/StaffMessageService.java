package org.motechproject.server.service;

import org.motechproject.server.model.ghana.Facility;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: balaji
 * Date: 17/6/11
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
public interface StaffMessageService {
    void sendStaffCareMessages(Date startDate, Date endDate,
                               Date deliveryDate, Date deliveryTime,
                               String[] careGroups,
                               boolean sendUpcoming,
                               boolean blackoutEnabled);

    void sendUpcomingMessages(Date startDate, Date endDate, Date deliveryDate, String[] careGroups, Facility facility);

    void sendDefaulterMessages(Date startDate, Date deliveryDate, String[] careGroups, Facility facility);
}
