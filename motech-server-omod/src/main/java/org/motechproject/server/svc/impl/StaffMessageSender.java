package org.motechproject.server.svc.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.*;
import org.motechproject.server.omod.factory.DistrictFactory;
import org.motechproject.server.omod.filters.FilterChain;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.svc.RCTService;
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

public class StaffMessageSender {

    private static Log log = LogFactory.getLog(StaffMessageSender.class);
    @Autowired
    @Qualifier("expectedEncountersFilter")
    private FilterChain expectedEncountersFilter;

    @Autowired
    @Qualifier("expectedObsFilter")
    private FilterChain expectedObsFilter;

    @Autowired
    private ContextService contextService;

    @Autowired
    @Qualifier("rctBeanProxy")
    private RCTService rctService;

    private MessageService mobileService;

    @Autowired
    private WebServiceCareModelConverter careModelConverter;

    public StaffMessageSender() {
    }


    public StaffMessageSender(
            ContextService contextService,
            MessageService mobileService,
            RCTService rctService) {
        this.contextService = contextService;
        this.mobileService = mobileService;
        this.rctService = rctService;
    }

    public void sendStaffCareMessages(Date startDate, Date endDate,
                                      Date deliveryDate, Date deliveryTime,
                                      String[] careGroups,
                                      boolean sendUpcoming,
                                      boolean blackoutEnabled) {

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
            sendDefaulterMessages(startDate, deliveryDate, careGroups, facility);
            if (sendUpcoming) {
                sendUpcomingMessages(startDate, endDate, deliveryDate, careGroups, facility);
            }
        }
    }

    public void sendUpcomingMessages(Date startDate, Date endDate, Date deliveryDate, String[] careGroups, Facility facility) {

        List<ExpectedEncounter> upcomingEncounters = rctService.filterRCTEncounters(getUpcomingExpectedEncounters(facility, careGroups, startDate, endDate));
        List<ExpectedObs> upcomingObs = rctService.filterRCTObs(getUpcomingExpectedObs(facility, careGroups, startDate, endDate));

        final String facilityPhoneNumber = facility.getPhoneNumber();
        final boolean upcomingEventsPresent = !(upcomingEncounters.isEmpty() && upcomingObs.isEmpty());

        if (upcomingEventsPresent) {
            Care[] upcomingCares = careModelConverter.upcomingToWebServiceCares(upcomingEncounters, upcomingObs, true);
            log.info("Sending upcoming care message to " + facility.name() + " at " + facilityPhoneNumber);
            sendStaffUpcomingCareMessage(deliveryDate, upcomingCares, getCareMessageGroupingStrategy(facility.getLocation()), facility);
        } else {
            sendNoUpcomingCareMessage(facility);
        }
    }

    public void sendDefaulterMessages(Date startDate, Date deliveryDate, String[] careGroups, Facility facility) {
        log.debug("Starting Sending of defaulter messages for " + facility.getLocation().getName());
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
        } else {
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
            boolean result = false;
            for (String phoneNumber : facility.getAvailablePhoneNumbers()) {
                org.motechproject.ws.MessageStatus messageStatus;
                messageStatus = mobileService.sendBulkCaresMessage(null, phoneNumber, cares, groupingStrategy, MediaType.TEXT, messageStartDate, null);
                result |= messageStatus != org.motechproject.ws.MessageStatus.FAILED;
            }
            return result;
        } catch (Exception e) {
            log.error("Mobile WS staff upcoming care message failure", e);
            return false;
        }
    }

    private void sendNoUpcomingCareMessage(Facility facility) {

        for (String phoneNumber : facility.getAvailablePhoneNumbers()) {
            log.info("Sending 'no upcoming care' message to " + facility.name() + " at " + phoneNumber);
            try {
                org.motechproject.ws.MessageStatus messageStatus = mobileService.sendMessage(facility.name() + " has no upcoming care for this week", phoneNumber);
                handleUpcomingCareMessageResponse(phoneNumber, messageStatus);
            } catch (Exception e) {
                handleUpcomingCareMessageException(phoneNumber, e);
            }
        }
    }

    private boolean sendStaffDefaultedCareMessage(Facility facility, Date messageStartDate,
                                                  Care[] cares, CareMessageGroupingStrategy groupingStrategy) {
        try {
            org.motechproject.ws.MessageStatus messageStatus;
            boolean result = false;
            for (String phoneNumber : facility.getAvailablePhoneNumbers()) {
                messageStatus = mobileService.sendDefaulterMessage(null, phoneNumber, cares, groupingStrategy, MediaType.TEXT, messageStartDate, null);
                result |= messageStatus != org.motechproject.ws.MessageStatus.FAILED;
            }
            return result;
        } catch (Exception e) {
            log.error("Mobile WS staff defaulted care message failure", e);
            return false;
        }
    }

    private void sendNoDefaultersMessage(Facility facility) {

        for (String phoneNumber : facility.getAvailablePhoneNumbers()) {
            log.info("Sending 'no defaulters' message to " + facility.name() + " at " + phoneNumber);
            try {
                org.motechproject.ws.MessageStatus messageStatus = mobileService.sendMessage(facility.name() + " has no defaulters for this week", phoneNumber);
                if (messageStatus == org.motechproject.ws.MessageStatus.FAILED) {
                    log.error("Unable to message " + phoneNumber + " that they have no defaulters");
                }
            } catch (Exception e) {
                log.error("Unable to message " + phoneNumber + " that they have no defaulters", e);
            }

        }
    }

    private void incrementDefaultedEncountersAlertCount(List<ExpectedEncounter> defaultedEncounters, Boolean delivered) {
        for (ExpectedEncounter defaultedEncounter : defaultedEncounters) {
            DefaultedExpectedEncounterAlert alert = motechService().getDefaultedEncounterAlertFor(defaultedEncounter);
            if (alert == null) {
                DefaultedExpectedEncounterAlert encounterAlert = new DefaultedExpectedEncounterAlert(defaultedEncounter, careConfigurationFor(defaultedEncounter.getName()), 1, 1);
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
                DefaultedExpectedObsAlert obsAlert = new DefaultedExpectedObsAlert(defaultedObs, careConfigurationFor(defaultedObs.getName()), 1, 1);
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