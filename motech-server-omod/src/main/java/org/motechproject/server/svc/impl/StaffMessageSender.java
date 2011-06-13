package org.motechproject.server.svc.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.*;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.factory.DistrictFactory;
import org.motechproject.server.omod.filters.FilterChain;
import org.motechproject.server.svc.RCTService;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.server.ws.WebServiceModelConverterImpl;
import org.motechproject.ws.Care;
import org.motechproject.ws.CareMessageGroupingStrategy;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.mobile.MessageService;
import org.openmrs.Location;
import org.openmrs.Patient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StaffMessageSender {

    private static Log log = LogFactory.getLog(StaffMessageSender.class);
    private RegistrarBeanImpl registrarBeanImpl;
    private FilterChain expectedEncountersFilter;
    private FilterChain expectedObsFilter;
    private ContextService contextService;
    private MessageService mobileService;
    private RCTService rctService;

    public StaffMessageSender() {
    }

    public StaffMessageSender(RegistrarBeanImpl registrarBeanImpl,
                              ContextService contextService,
                              MessageService mobileService,
                              RCTService rctService) {
        this.registrarBeanImpl = registrarBeanImpl;
        this.contextService = contextService;
        this.mobileService = mobileService;
        this.rctService = rctService;
    }

    public void sendStaffCareMessages(Date startDate, Date endDate,
                                      Date deliveryDate, Date deliveryTime,
                                      String[] careGroups,
                                      boolean sendUpcoming,
                                      boolean blackoutEnabled) {

        final boolean shouldBlackOut = blackoutEnabled && registrarBeanImpl.isMessageTimeWithinBlackoutPeriod(deliveryDate);
        if (shouldBlackOut) {
            log.debug("Cancelling nurse messages during blackout");
            return;
        }
        List<Facility> facilities = motechService().getAllFacilities();
        deliveryDate = registrarBeanImpl.adjustTime(deliveryDate, deliveryTime);
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

    private MotechService motechService() {
        return contextService.getMotechService();
    }

    private boolean facilityPhoneNumberOrLocationNotAvailable(Facility facility) {
        return (facility.getPhoneNumber() == null) || (facility.getLocation() == null);
    }

    private void sendUpcomingMessages(Date startDate, Date endDate, Date deliveryDate, String[] careGroups, Facility facility) {
        WebServiceModelConverterImpl modelConverter = new WebServiceModelConverterImpl();
        modelConverter.setRegistrarBean(registrarBeanImpl);
        List<ExpectedEncounter> upcomingEncounters = filterRCTEncounters(getUpcomingExpectedEncounters(facility, careGroups, startDate, endDate));
        List<ExpectedObs> upcomingObs = filterRCTObs(getUpcomingExpectedObs(facility, careGroups, startDate, endDate));
        final String facilityPhoneNumber = facility.getPhoneNumber();
        final boolean upcomingEventsPresent = !(upcomingEncounters.isEmpty() && upcomingObs.isEmpty());
        if (upcomingEventsPresent) {
            Care[] upcomingCares = modelConverter.upcomingToWebServiceCares(upcomingEncounters, upcomingObs, true);
            log.info("Sending upcoming care message to " + facility.name() + " at " + facilityPhoneNumber);
            sendStaffUpcomingCareMessage(facilityPhoneNumber, deliveryDate, upcomingCares, getCareMessageGroupingStrategy(facility.getLocation()));
        } else {
            sendNoUpcomingCareMessage(facility, facilityPhoneNumber);
        }
    }

    private boolean sendStaffUpcomingCareMessage(String phoneNumber,
                                                 Date messageStartDate,
                                                 Care[] cares, CareMessageGroupingStrategy groupingStrategy) {

        try {
            org.motechproject.ws.MessageStatus messageStatus;
            messageStatus = mobileService.sendBulkCaresMessage(null, phoneNumber, cares, groupingStrategy,
                    MediaType.TEXT, messageStartDate, null);

            return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
        } catch (Exception e) {
            log.error("Mobile WS staff upcoming care message failure", e);
            return false;
        }
    }

    private void sendDefaulterMessages(Date startDate, Date deliveryDate, String[] careGroups, Facility facility) {
        log.debug("Starting Sending of defaulter messages for " + facility.getLocation().getName());
        List<ExpectedEncounter> defaultedExpectedEncounters = getDefaultedExpectedEncounters(facility, careGroups, startDate);
        List<ExpectedObs> defaultedExpectedObs = getDefaultedExpectedObs(facility, careGroups, startDate);

        List<ExpectedEncounter> filteredDefaultedEncounters = expectedEncountersFilter.doFilter(new ArrayList<ExpectedEncounter>(defaultedExpectedEncounters));
        List<ExpectedObs> filteredDefaultedExpectedObs = expectedObsFilter.doFilter(new ArrayList<ExpectedObs>(defaultedExpectedObs));
        final String facilityPhoneNumber = facility.getPhoneNumber();

        final boolean defaultersPresent = !(filteredDefaultedEncounters.isEmpty() && filteredDefaultedExpectedObs.isEmpty());
        if (defaultersPresent) {
            WebServiceModelConverterImpl modelConverter = new WebServiceModelConverterImpl();
            modelConverter.setRegistrarBean(registrarBeanImpl);
            Care[] defaultedCares = modelConverter.defaultedToWebServiceCares(filteredDefaultedEncounters, filteredDefaultedExpectedObs);
            log.info("Sending defaulter message to " + facility.name() + " at " + facilityPhoneNumber);
            Boolean alertsSent = sendStaffDefaultedCareMessage(facilityPhoneNumber, deliveryDate, defaultedCares, getCareMessageGroupingStrategy(facility.getLocation()));
            incrementDefaultedEncountersAlertCount(filteredDefaultedEncounters, alertsSent);
            incrementDefaultedObservationsAlertCount(filteredDefaultedExpectedObs, alertsSent);
        } else {
            sendNoDefaultersMessage(facility, facilityPhoneNumber);
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


    private Integer getMaxQueryResults() {
        String maxResultsProperty = contextService.getAdministrationService().getGlobalProperty(
                MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS);
        if (maxResultsProperty != null) {
            return Integer.parseInt(maxResultsProperty);
        }
        log.error("Max Query Results Property not found");
        return null;
    }

    private List<ExpectedEncounter> filterRCTEncounters(List<ExpectedEncounter> allDefaulters) {
        List<ExpectedEncounter> toBeRemoved = new ArrayList<ExpectedEncounter>();
        for (ExpectedEncounter allDefaulter : allDefaulters) {
            ExpectedEncounter expectedEncounter = allDefaulter;
            if (meetsFilteringCriteria(expectedEncounter.getPatient())) {
                toBeRemoved.add(expectedEncounter);
            }
        }
        allDefaulters.removeAll(toBeRemoved);
        return allDefaulters;
    }

    private List<ExpectedObs> filterRCTObs(List<ExpectedObs> allDefaulters) {
        List<ExpectedObs> toBeRemoved = new ArrayList<ExpectedObs>();
        for (ExpectedObs allDefaulter : allDefaulters) {
            ExpectedObs expectedObs = allDefaulter;
            if (meetsFilteringCriteria(expectedObs.getPatient())) {
                toBeRemoved.add(expectedObs);
            }
        }
        allDefaulters.removeAll(toBeRemoved);
        return allDefaulters;
    }

    private boolean meetsFilteringCriteria(Patient patient) {
        if (patient == null) return true;
        if (rctService.isPatientRegisteredAndInTreatmentGroup(patient)) return false;
        return isFromUpperEast(patient) && (patient.getId()) > 5717;
    }

    private Boolean isFromUpperEast(Patient patient) {
        Facility facility = getFacilityByPatient(patient);
        return facility != null && facility.isInRegion("Upper East");
    }

    private Facility getFacilityByPatient(Patient patient) {
        return motechService().facilityFor(patient);
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

    private CareMessageGroupingStrategy getCareMessageGroupingStrategy(Location facilityLocation) {
        return new DistrictFactory().getDistrictWithName(facilityLocation.getCountyDistrict()).getCareMessageGroupingStrategy();
    }

    private void sendNoUpcomingCareMessage(Facility facility, String phoneNumber) {
        log.info("Sending 'no upcoming care' message to " + facility.name() + " at " + phoneNumber);
        try {
            org.motechproject.ws.MessageStatus messageStatus = mobileService.sendMessage(facility.name() + " has no upcoming care for this week", phoneNumber);
            if (messageStatus == org.motechproject.ws.MessageStatus.FAILED) {
                log.error("Unable to message " + phoneNumber + " that they have no upcoming care");
            }
        } catch (Exception e) {
            log.error("Unable to message " + phoneNumber + " that they have no upcoming care", e);
        }
    }

    private void sendNoDefaultersMessage(Facility facility, String phoneNumber) {
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

    private boolean sendStaffDefaultedCareMessage(String phoneNumber,
                                                  Date messageStartDate,
                                                  Care[] cares, CareMessageGroupingStrategy groupingStrategy) {

        try {
            org.motechproject.ws.MessageStatus messageStatus;
            messageStatus = mobileService.sendDefaulterMessage(null, phoneNumber, cares, groupingStrategy, MediaType.TEXT, messageStartDate, null);
            return messageStatus != org.motechproject.ws.MessageStatus.FAILED;
        } catch (Exception e) {
            log.error("Mobile WS staff defaulted care message failure", e);
            return false;
        }
    }

    private CareConfiguration careConfigurationFor(String careName) {
        return motechService().getCareConfigurationFor(careName);
    }


    public void setExpectedEncountersFilter(FilterChain expectedEncountersFilter) {
        this.expectedEncountersFilter = expectedEncountersFilter;

    }

    public void setExpectedObsFilter(FilterChain expectedObsFilter) {
        this.expectedObsFilter = expectedObsFilter;
    }
}