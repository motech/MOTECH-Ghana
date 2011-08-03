package org.motechproject.server.omod.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.factory.DistrictFactory;
import org.motechproject.server.filters.FilterChain;
import org.motechproject.server.model.*;
import org.motechproject.server.model.ghana.Facility;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.service.StaffMessageService;
import org.motechproject.server.util.DateUtil;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.server.ws.WebServiceCareModelConverter;
import org.motechproject.ws.Care;
import org.motechproject.ws.CareMessageGroupingStrategy;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.mobile.MessageService;
import org.openmrs.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StaffMessageServiceImpl implements StaffMessageService {

    private static Log log = LogFactory.getLog(StaffMessageServiceImpl.class);
    @Autowired
    @Qualifier("expectedEncountersFilter")
    private FilterChain expectedEncountersFilter;

    @Autowired
    @Qualifier("expectedObsFilter")
    private FilterChain expectedObsFilter;

    @Autowired
    private ContextService contextService;

    private MessageService mobileService;

    @Autowired
    private WebServiceCareModelConverter careModelConverter;

    public StaffMessageServiceImpl() {
    }


    public StaffMessageServiceImpl(
            ContextService contextService,
            MessageService mobileService
    ) {
        this.contextService = contextService;
        this.mobileService = mobileService;
    }

    public void sendStaffCareMessages(Date startDate, Date endDate,
                                      Date deliveryDate, Date deliveryTime,
                                      String[] careGroups,
                                      boolean sendUpcoming,
                                      boolean blackoutEnabled,
                                      boolean sendNoDefaulterAndNoUpcomingCareMessage) {

        final boolean shouldBlackOut = blackoutEnabled && isMessageTimeWithinBlackoutPeriod(deliveryDate);
        if (shouldBlackOut) {
            log.debug("Cancelling nurse messages during blackout");
            return;
        }
        List<Facility> facilities = motechService().getAllFacilities();
        deliveryDate = adjustTime(deliveryDate, deliveryTime);
        for (Facility facility : facilities) {
            if (facilityPhoneNumberOrLocationNotAvailable(facility)) {
                continue;
            }
            sendDefaulterMessages(startDate, deliveryDate, careGroups, facility, sendNoDefaulterAndNoUpcomingCareMessage);
            if (sendUpcoming) {
                sendUpcomingMessages(startDate, endDate, deliveryDate, careGroups, facility, sendNoDefaulterAndNoUpcomingCareMessage);
            }
        }
    }

    public void sendUpcomingMessages(Date startDate, Date endDate, Date deliveryDate, String[] careGroups, Facility facility, Boolean sendNoUpcomingCareMessage) {

        List<ExpectedEncounter> upcomingEncounters = getUpcomingExpectedEncounters(facility, careGroups, startDate, endDate);
        List<ExpectedObs> upcomingObs = getUpcomingExpectedObs(facility, careGroups, startDate, endDate);

        final String facilityPhoneNumber = facility.getPhoneNumber();
        final boolean upcomingEventsPresent = !(upcomingEncounters.isEmpty() && upcomingObs.isEmpty());

        if (upcomingEventsPresent) {
            Care[] upcomingCares = careModelConverter.upcomingToWebServiceCares(upcomingEncounters, upcomingObs, true);
            log.info("Sending upcoming care message to " + facility.name() + " at " + facilityPhoneNumber);
            sendStaffUpcomingCareMessage(deliveryDate, upcomingCares, getCareMessageGroupingStrategy(facility.getLocation()), facility);
        } else if (sendNoUpcomingCareMessage) {
            sendNoUpcomingCareMessage(facility);
        }
    }

    public void sendDefaulterMessages(Date startDate, Date deliveryDate, String[] careGroups, Facility facility, Boolean sendNoDefaulterMessage) {
        log.info("Starting Sending of defaulter messages for " + facility.getLocation().getName());
        List<ExpectedEncounter> defaultedExpectedEncounters = getDefaultedExpectedEncounters(facility, careGroups, startDate);
        List<ExpectedObs> defaultedExpectedObs = getDefaultedExpectedObs(facility, careGroups, startDate);

        List<ExpectedEncounter> filteredDefaultedEncounters = expectedEncountersFilter.doFilter(new ArrayList<ExpectedEncounter>(defaultedExpectedEncounters));
        List<ExpectedObs> filteredDefaultedExpectedObs = expectedObsFilter.doFilter(new ArrayList<ExpectedObs>(defaultedExpectedObs));
        final String facilityPhoneNumber = facility.getPhoneNumber();

        final boolean defaultersPresent = !(filteredDefaultedEncounters.isEmpty() && filteredDefaultedExpectedObs.isEmpty());
        if (defaultersPresent) {
            Care[] defaultedCares = careModelConverter.defaultedToWebServiceCares(filteredDefaultedEncounters, filteredDefaultedExpectedObs);
            log.info("Sending defaulter message to " + facility.name() + " at " + facilityPhoneNumber);
            Boolean alertsSent = sendStaffDefaultedCareMessage(facility, deliveryDate, defaultedCares, getCareMessageGroupingStrategy(facility.getLocation()));
            incrementDefaultedEncountersAlertCount(filteredDefaultedEncounters, alertsSent);
            incrementDefaultedObservationsAlertCount(filteredDefaultedExpectedObs, alertsSent);
        } else if (sendNoDefaulterMessage) {
            sendNoDefaultersMessage(facility);
        }
    }

    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }

    public void setExpectedEncountersFilter(FilterChain expectedEncountersFilter) {
        this.expectedEncountersFilter = expectedEncountersFilter;

    }

    public void setExpectedObsFilter(FilterChain expectedObsFilter) {
        this.expectedObsFilter = expectedObsFilter;
    }

    public void setMobileService(MessageService mobileService) {
        this.mobileService = mobileService;
    }


    public void setCareModelConverter(WebServiceCareModelConverter careModelConverter) {
        this.careModelConverter = careModelConverter;
    }

    private boolean sendStaffUpcomingCareMessage(Date messageStartDate,
                                                 Care[] cares, CareMessageGroupingStrategy groupingStrategy,
                                                 Facility facility) {
        try {
            String facilityPhoneNumbers = StringUtils.join(facility.getAvailablePhoneNumbers(), ",");
            org.motechproject.ws.MessageStatus messageStatus;
            messageStatus = mobileService.sendBulkCaresMessage(null, facilityPhoneNumbers, cares, groupingStrategy, MediaType.TEXT, messageStartDate, null);
            return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
        } catch (Exception e) {
            log.error("Mobile WS staff upcoming care message failure", e);
            return false;
        }
    }

    private void sendNoUpcomingCareMessage(Facility facility) {
        String facilityPhoneNumbers = StringUtils.join(facility.getAvailablePhoneNumbers(), ",");
        log.info("Sending 'no upcoming care' message to " + facility.name() + " at " + facilityPhoneNumbers);
        try {
            org.motechproject.ws.MessageStatus messageStatus = mobileService.sendMessage(facility.name() + " has no upcoming care for this week", facilityPhoneNumbers);
            handleUpcomingCareMessageResponse(facilityPhoneNumbers, messageStatus);
        } catch (Exception e) {
            handleUpcomingCareMessageException(facilityPhoneNumbers, e);
        }
    }

    private boolean sendStaffDefaultedCareMessage(Facility facility, Date messageStartDate,
                                                  Care[] cares, CareMessageGroupingStrategy groupingStrategy) {
        try {
            org.motechproject.ws.MessageStatus messageStatus;
            String facilityPhoneNumbers = StringUtils.join(facility.getAvailablePhoneNumbers(), ",");
            messageStatus = mobileService.sendDefaulterMessage(null, facilityPhoneNumbers, cares, groupingStrategy, MediaType.TEXT, messageStartDate, null);
            return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
        } catch (Exception e) {
            log.error("Mobile WS staff defaulted care message failure", e);
            return false;
        }
    }

    private void sendNoDefaultersMessage(Facility facility) {
        String facilityPhoneNumbers = StringUtils.join(facility.getAvailablePhoneNumbers(), ",");
        log.info("Sending 'no defaulters' message to " + facility.name() + " at " + facilityPhoneNumbers);
        try {
            org.motechproject.ws.MessageStatus messageStatus = mobileService.sendMessage(facility.name() + " has no defaulters for this week", facilityPhoneNumbers);
            if (messageStatus == org.motechproject.ws.MessageStatus.FAILED) {
                log.error("Unable to message " + facilityPhoneNumbers + " that they have no defaulters");
            }
        } catch (Exception e) {
            log.error("Unable to message " + facilityPhoneNumbers + " that they have no defaulters", e);
        }
    }

    private void incrementDefaultedEncountersAlertCount(List<ExpectedEncounter> defaultedEncounters, Boolean delivered) {
        for (ExpectedEncounter defaultedEncounter : defaultedEncounters) {
            DefaultedExpectedEncounterAlert alert = motechService().getDefaultedEncounterAlertFor(defaultedEncounter);
            if (alert == null) {
                DefaultedExpectedEncounterAlert encounterAlert = new DefaultedExpectedEncounterAlert(defaultedEncounter, careConfigurationFor(defaultedEncounter.getName()), delivered ? 1 : 0, 1);
                motechService().saveOrUpdateDefaultedEncounterAlert(encounterAlert);
                continue;
            }
            alert.attempted();
            if (delivered) alert.delivered();
            motechService().saveOrUpdateDefaultedEncounterAlert(alert);
        }
    }

    private void incrementDefaultedObservationsAlertCount(List<ExpectedObs> defaultedObservations, Boolean delivered) {
        for (ExpectedObs defaultedObs : defaultedObservations) {
            DefaultedExpectedObsAlert alert = motechService().getDefaultedObsAlertFor(defaultedObs);
            if (alert == null) {
                DefaultedExpectedObsAlert obsAlert = new DefaultedExpectedObsAlert(defaultedObs, careConfigurationFor(defaultedObs.getName()), delivered ? 1 : 0, 1);
                motechService().saveOrUpdateDefaultedObsAlert(obsAlert);
                continue;
            }
            alert.attempted();
            if (delivered) alert.delivered();
            motechService().saveOrUpdateDefaultedObsAlert(alert);
        }
    }

    private List<ExpectedEncounter> getUpcomingExpectedEncounters(
            Facility facility, String[] groups, Date fromDate, Date toDate) {
        Integer maxResults = getMaxQueryResults();
        return motechService().getExpectedEncounter(null, facility, groups, null,
                toDate, null, fromDate, maxResults);
    }


    private List<ExpectedObs> getUpcomingExpectedObs(Facility facility,
                                                     String[] groups, Date fromDate, Date toDate) {
        Integer maxResults = getMaxQueryResults();
        return motechService().getExpectedObs(null, facility, groups, null,
                toDate, null, fromDate, maxResults);
    }

    private List<ExpectedEncounter> getDefaultedExpectedEncounters(Facility facility, String[] groups, Date forDate) {
        Integer maxResults = getMaxQueryResults();
        return motechService().getExpectedEncounter(null, facility, groups, null,
                null, forDate, forDate, maxResults);
    }

    private List<ExpectedObs> getDefaultedExpectedObs(Facility facility,
                                                      String[] groups, Date forDate) {
        Integer maxResults = getMaxQueryResults();
        return motechService().getExpectedObs(null, facility, groups, null, null,
                forDate, forDate, maxResults);
    }


    private CareMessageGroupingStrategy getCareMessageGroupingStrategy(Location facilityLocation) {
        return new DistrictFactory().getDistrictWithName(facilityLocation.getCountyDistrict()).getCareMessageGroupingStrategy();
    }

    private CareConfiguration careConfigurationFor(String careName) {
        return motechService().getCareConfigurationFor(careName);
    }


    private Integer getMaxQueryResults() {
        String maxResultsProperty = contextService.getAdministrationService().getGlobalProperty(
                MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS);
        if (maxResultsProperty != null) {
            return Integer.parseInt(maxResultsProperty);
        }
        log.error("Max Query Results Property not found");
        return null;
    }

    boolean isMessageTimeWithinBlackoutPeriod(Date deliveryDate) {
        Date checkForDate = (deliveryDate != null ? deliveryDate : new Date());
        Blackout blackout = motechService().getBlackoutSettings();
        if (blackout == null) {
            return false;
        }

        Calendar blackoutCalendar = new DateUtil().calendarFor(checkForDate);

        adjustForBlackoutStartDate(checkForDate, blackout, blackoutCalendar);
        Date blackoutStart = blackoutCalendar.getTime();
        setBlackOutTime(blackout.getEndTime(), blackoutCalendar);

        if (blackoutCalendar.getTime().before(blackoutStart)) {
            // Add a day if blackout end date before start date after setting time
            blackoutCalendar.add(Calendar.DATE, 1);
        }
        Date blackoutEnd = blackoutCalendar.getTime();

        return checkForDate.after(blackoutStart) && checkForDate.before(blackoutEnd);
    }

    private void adjustForBlackoutStartDate(Date date, Blackout blackout, Calendar blackoutCalendar) {
        setBlackOutTime(blackout.getStartTime(), blackoutCalendar);

        if (date.before(blackoutCalendar.getTime())) {
            // Remove a day if blackout start date before the message date
            blackoutCalendar.add(Calendar.DATE, -1);
        }
    }

    private MotechService motechService() {
        return contextService.getMotechService();
    }


    private boolean facilityPhoneNumberOrLocationNotAvailable(Facility facility) {
        return (facility.getPhoneNumber() == null) || (facility.getLocation() == null);
    }

    private void setBlackOutTime(Date blackoutTime, Calendar blackoutCalendar) {
        Calendar timeCalendar = new DateUtil().calendarFor(blackoutTime);
        blackoutCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
        blackoutCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        blackoutCalendar.set(Calendar.SECOND, timeCalendar.get(Calendar.SECOND));
    }

    // copied from registrar bean to eliminate dependency on registrar bean.
    // should be moved to appropriate abstraction later.
    private Date adjustTime(Date date, Date time) {
        if (date == null || time == null) {
            return date;
        }
        DateUtil dateUtil = new DateUtil();
        Calendar calendar = dateUtil.calendarFor(date);

        Calendar timeCalendar = dateUtil.calendarFor(time);
        calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, 0);
        if (calendar.getTime().before(date)) {
            // Add a day if before original date
            // after setting the time of day
            calendar.add(Calendar.DATE, 1);
        }
        return calendar.getTime();
    }

    private void handleUpcomingCareMessageException(String phoneNumber, Exception e) {
        log.error("Unable to message " + phoneNumber + " that they have no upcoming care", e);
    }

    private void handleUpcomingCareMessageResponse(String phoneNumber, org.motechproject.ws.MessageStatus messageStatus) {
        if (messageStatus == org.motechproject.ws.MessageStatus.FAILED) {
            log.error("Unable to message " + phoneNumber + " that they have no upcoming care");
        }
    }
}