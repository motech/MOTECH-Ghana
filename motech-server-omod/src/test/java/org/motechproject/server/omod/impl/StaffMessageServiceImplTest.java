package org.motechproject.server.omod.impl;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.filters.ExpectedEncounterFilterChain;
import org.motechproject.server.filters.ExpectedObsFilterChain;
import org.motechproject.server.filters.Filter;
import org.motechproject.server.model.*;
import org.motechproject.server.model.ghana.Facility;
import org.motechproject.server.model.ghana.KassenaNankana;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.svc.RCTService;
import org.motechproject.server.svc.impl.DefaultedExpectedEncounterAlertMatcher;
import org.motechproject.server.svc.impl.DefaultedExpectedObsAlertMatcher;
import org.motechproject.server.svc.impl.RegistrarBeanImpl;
import org.motechproject.server.util.DateUtil;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.server.ws.WebServiceCareModelConverterImpl;
import org.motechproject.server.ws.WebServicePatientModelConverterImpl;
import org.motechproject.ws.*;
import org.motechproject.ws.MessageStatus;
import org.motechproject.ws.mobile.MessageService;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.*;
import static org.easymock.EasyMock.*;


public class StaffMessageServiceImplTest {

    RegistrarBeanImpl registrarBean;

    ContextService contextService;
    AdministrationService adminService;
    MotechService motechService;
    RCTService rctService;
    MessageService mobileService;
    StaffMessageServiceImpl staffMessageServiceImpl;

    @Before
    public void setUp() throws Exception {
        contextService = createMock(ContextService.class);
        adminService = createMock(AdministrationService.class);
        motechService = createMock(MotechService.class);
        rctService = createMock(RCTService.class);
        mobileService = createMock(MessageService.class);

        registrarBean = new RegistrarBeanImpl();
        registrarBean.setContextService(contextService);
        registrarBean.setAdministrationService(adminService);
        registrarBean.setRctService(rctService);
        registrarBean.setMobileService(mobileService);
        ExpectedEncounterFilterChain expectedEncounterFilterChain = new ExpectedEncounterFilterChain();
        expectedEncounterFilterChain.setFilters(new ArrayList<Filter<ExpectedEncounter>>());
        ExpectedObsFilterChain expectedObsFilterChain = new ExpectedObsFilterChain();
        expectedObsFilterChain.setFilters(new ArrayList<Filter<ExpectedObs>>());
        staffMessageServiceImpl = new StaffMessageServiceImpl(contextService, mobileService, rctService);
        staffMessageServiceImpl.setExpectedEncountersFilter(expectedEncounterFilterChain);
        staffMessageServiceImpl.setExpectedObsFilter(expectedObsFilterChain);

        WebServiceCareModelConverterImpl careModelConverter = new WebServiceCareModelConverterImpl();
        careModelConverter.setContextService(contextService);
        staffMessageServiceImpl.setCareModelConverter(careModelConverter);

    }

    @Test
    public void defaultersAlertCountShouldBeIncreasedForExistingAlerts() {
        String[] careGroups = new String[]{"ANC","TT1"};
        List<Facility> facilities = facilitiesFor("Central Region");

        Patient patient = new Patient(1);
        ExpectedEncounter enc = new ExpectedEncounter();
        enc.setId(1L);
        enc.setPatient(patient);
        enc.setName("ANC");
        List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();
        expectedEncounters.add(enc);

        ExpectedObs obs = new ExpectedObs();
        obs.setId(1L);
        obs.setPatient(patient);
        obs.setName("TT1");
        List<ExpectedObs> expectedObservations = new ArrayList<ExpectedObs>();
        expectedObservations.add(obs);

        WebServicePatientModelConverterImpl converter = new WebServicePatientModelConverterImpl();
        converter.setRegistrarBean(registrarBean);

        CareConfiguration careConfigurationForANC = new CareConfiguration(1L, "ANC", 3);
        CareConfiguration careConfigurationForTT1 = new CareConfiguration(2L, "TT1", 3);

        Date someDate = new Date();

        Integer maxResults = 1;
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(contextService.getAdministrationService()).andReturn(adminService).times(2);
        expect(motechService.getBlackoutSettings()).andReturn(null);
        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn(maxResults.toString()).times(2);
        expect(motechService.getExpectedEncounter(null, facilities.get(0), careGroups, null, null, someDate, someDate, maxResults)).andReturn(expectedEncounters);
        expect(motechService.getExpectedObs(null, facilities.get(0), careGroups, null, null, someDate, someDate, maxResults)).andReturn(expectedObservations);
        expect(motechService.getCommunityByPatient(same(patient))).andReturn(null).times(2);


        expect(mobileService.sendDefaulterMessage(EasyMock.<String>isNull(), eq("0123456789"), EasyMock.<Care[]>anyObject(),
                EasyMock.<CareMessageGroupingStrategy>anyObject(), EasyMock.<MediaType>anyObject(), EasyMock.<Date>anyObject(), EasyMock.<Date>isNull()))
                .andReturn(org.motechproject.ws.MessageStatus.DELIVERED);

        expect(motechService.getDefaultedEncounterAlertFor(enc)).andReturn(new DefaultedExpectedEncounterAlert(enc, careConfigurationForANC, 1, 1));
        expect(motechService.getDefaultedObsAlertFor(obs)).andReturn((new DefaultedExpectedObsAlert(obs, careConfigurationForTT1, 2, 1)));

        motechService.saveOrUpdateDefaultedEncounterAlert(equalsExpectedEncounterAlert(new DefaultedExpectedEncounterAlert(enc, careConfigurationForANC, 2, 2)));
        expectLastCall();

        motechService.saveOrUpdateDefaultedObsAlert(equalsExpectedObsAlert(new DefaultedExpectedObsAlert(obs, careConfigurationForTT1, 3, 2)));
        expectLastCall();

        replay(contextService, motechService, adminService, mobileService);

        staffMessageServiceImpl.sendStaffCareMessages(someDate, someDate, someDate, someDate, careGroups, false, true);

        verify(contextService, motechService, adminService,  mobileService);

    }

    @Test
    public void onlyAttemptCountShouldBeIncrementedIfAlertNotDelivered() {
        String[] careGroups = new String[]{"ANC","TT1"};
        List<Facility> facilities = facilitiesFor("Central Region");

        Patient patient = new Patient(1);
        ExpectedEncounter enc = new ExpectedEncounter();
        enc.setId(1L);
        enc.setPatient(patient);
        enc.setName("ANC");
        List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();
        expectedEncounters.add(enc);

        ExpectedObs obs = new ExpectedObs();
        obs.setId(1L);
        obs.setPatient(patient);
        obs.setName("TT1");
        List<ExpectedObs> expectedObservations = new ArrayList<ExpectedObs>();
        expectedObservations.add(obs);

        WebServicePatientModelConverterImpl converter = new WebServicePatientModelConverterImpl();
        converter.setRegistrarBean(registrarBean);

        CareConfiguration careConfigurationForANC = new CareConfiguration(1L, "ANC", 3);
        CareConfiguration careConfigurationForTT1 = new CareConfiguration(2L, "TT1", 3);

        Date someDate = new Date();

        Integer maxResults = 1;
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(contextService.getAdministrationService()).andReturn(adminService).times(2);
        expect(motechService.getBlackoutSettings()).andReturn(null);
        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn(maxResults.toString()).times(2);
        expect(motechService.getExpectedEncounter(null, facilities.get(0), careGroups, null, null, someDate, someDate, maxResults)).andReturn(expectedEncounters);
        expect(motechService.getExpectedObs(null, facilities.get(0), careGroups, null, null, someDate, someDate, maxResults)).andReturn(expectedObservations);
        expect(motechService.getCommunityByPatient(same(patient))).andReturn(null).times(2);

        expect(mobileService.sendDefaulterMessage(EasyMock.<String>isNull(), eq("0123456789"), EasyMock.<Care[]>anyObject(),
                EasyMock.<CareMessageGroupingStrategy>anyObject(), EasyMock.<MediaType>anyObject(), EasyMock.<Date>anyObject(), EasyMock.<Date>isNull()))
                .andReturn(MessageStatus.FAILED);

        expect(motechService.getDefaultedEncounterAlertFor(enc)).andReturn(new DefaultedExpectedEncounterAlert(enc, careConfigurationForANC, 1, 2));
        expect(motechService.getDefaultedObsAlertFor(obs)).andReturn((new DefaultedExpectedObsAlert(obs, careConfigurationForTT1, 1, 1)));

        motechService.saveOrUpdateDefaultedEncounterAlert(equalsExpectedEncounterAlert(new DefaultedExpectedEncounterAlert(enc, careConfigurationForANC, 1 , 3)));
        expectLastCall();

        motechService.saveOrUpdateDefaultedObsAlert(equalsExpectedObsAlert(new DefaultedExpectedObsAlert(obs, careConfigurationForTT1, 1, 2)));
        expectLastCall();

        replay(contextService, motechService, adminService, mobileService);

        staffMessageServiceImpl.sendStaffCareMessages(someDate, someDate, someDate, someDate, careGroups, false, true);

        verify(contextService, motechService, adminService, mobileService);

    }

    @Test
    public void newDefaultersAlertsShouldBeCreatedForPatientsWhenNoneExists() {



        String[] careGroups = new String[]{"ANC","TT1"};
        List<Facility> facilities = facilitiesFor("Central Region");

        Patient patient = new Patient(1);
        ExpectedEncounter enc = new ExpectedEncounter();
        enc.setId(1L);
        enc.setPatient(patient);
        enc.setName("ANC");
        List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();
        expectedEncounters.add(enc);

        ExpectedObs obs = new ExpectedObs();
        obs.setId(1L);
        obs.setPatient(patient);
        obs.setName("TT1");
        List<ExpectedObs> expectedObservations = new ArrayList<ExpectedObs>();
        expectedObservations.add(obs);

        WebServicePatientModelConverterImpl converter = new WebServicePatientModelConverterImpl();
        converter.setRegistrarBean(registrarBean);

        CareConfiguration careConfigurationForANC = new CareConfiguration(1L, "ANC", 3);
        CareConfiguration careConfigurationForTT1 = new CareConfiguration(2L, "TT1", 3);

        Date someDate = new Date();

        Integer maxResults = 1;
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(contextService.getAdministrationService()).andReturn(adminService).times(2);
        expect(motechService.getBlackoutSettings()).andReturn(null);
        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn(maxResults.toString()).times(2);
        expect(motechService.getExpectedEncounter(null, facilities.get(0), careGroups, null, null, someDate, someDate, maxResults)).andReturn(expectedEncounters);
        expect(motechService.getExpectedObs(null, facilities.get(0), careGroups, null, null, someDate, someDate, maxResults)).andReturn(expectedObservations);
        expect(motechService.getCommunityByPatient(same(patient))).andReturn(null).times(2);


        expect(mobileService.sendDefaulterMessage(EasyMock.<String>isNull(), eq("0123456789"), EasyMock.<Care[]>anyObject(),
                EasyMock.<CareMessageGroupingStrategy>anyObject(), EasyMock.<MediaType>anyObject(), EasyMock.<Date>anyObject(), EasyMock.<Date>isNull()))
                .andReturn(MessageStatus.DELIVERED);
        expect(motechService.getDefaultedEncounterAlertFor(enc)).andReturn(null);
        expect(motechService.getCareConfigurationFor("ANC")).andReturn(careConfigurationForANC);
        expect(motechService.getDefaultedObsAlertFor(obs)).andReturn(null);
        expect(motechService.getCareConfigurationFor("TT1")).andReturn(careConfigurationForTT1);

        motechService.saveOrUpdateDefaultedEncounterAlert(equalsExpectedEncounterAlert(new DefaultedExpectedEncounterAlert(enc, careConfigurationForANC, 1, 1)));
        expectLastCall();

        motechService.saveOrUpdateDefaultedObsAlert(equalsExpectedObsAlert(new DefaultedExpectedObsAlert(obs, careConfigurationForTT1, 1, 1)));
        expectLastCall();

        WebServiceCareModelConverterImpl careModelConverter = new WebServiceCareModelConverterImpl();
        careModelConverter.setContextService(contextService);

        replay(contextService, motechService, adminService, mobileService);

        staffMessageServiceImpl.sendStaffCareMessages(someDate, someDate, someDate, someDate, careGroups, false, true);

        verify(contextService, motechService, adminService,  mobileService);

    }

    @Test
    public void testSendStaffCareMessagesGroupByCommunity() {

        Date forDate = new Date();
        String careGroups[] = {"ANC", "TT", "IPT"};

        Location location = new Location();
        location.setName("Test Facility");
        location.setRegion("Upper East");
        location.setCountyDistrict(new KassenaNankana().toString());

        Facility facility = new Facility();
        facility.setLocation(location);
        facility.setPhoneNumber("+1 555 123-1234");

        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(facility);


        Patient p = new Patient(5716);


        ExpectedEncounter enc = new ExpectedEncounter();
        enc.setPatient(p);

        List<ExpectedEncounter> encounters = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> emptyObs = new ArrayList<ExpectedObs>();

        encounters.add(enc);

        final DefaultedExpectedEncounterAlert defaultedExpectedEncounterAlert = anyObject();

        // To Mock
        expect(motechService.getCommunityByPatient(p)).andReturn(null);
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(contextService.getAdministrationService()).andReturn(adminService).times(2);

        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn("35").anyTimes();
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null, null, forDate, forDate, 35)).andReturn(encounters);
        expect(motechService.getExpectedObs(null, facility, careGroups, null, null, forDate, forDate, 35)).andReturn(emptyObs);

        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(motechService.getCareConfigurationFor(EasyMock.<String>anyObject())).andReturn(EasyMock.<CareConfiguration>anyObject());
        expect(motechService.getDefaultedEncounterAlertFor(enc)).andReturn(defaultedExpectedEncounterAlert);


        Capture<String> capturedMessageId = new Capture<String>();
        Capture<String> capturedPhoneNumber = new Capture<String>();
        Capture<Care[]> capturedCares = new Capture<Care[]>();
        Capture<CareMessageGroupingStrategy> capturedStrategy = new Capture<CareMessageGroupingStrategy>();
        Capture<MediaType> capturedMediaType = new Capture<MediaType>();
        Capture<Date> capturedStartDate = new Capture<Date>();
        Capture<Date> capturedEndDate = new Capture<Date>();


        expect(mobileService.sendDefaulterMessage(capture(capturedMessageId), capture(capturedPhoneNumber),
                capture(capturedCares), capture(capturedStrategy),
                capture(capturedMediaType), capture(capturedStartDate),
                capture(capturedEndDate))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED);

        motechService.saveOrUpdateDefaultedEncounterAlert(EasyMock.<DefaultedExpectedEncounterAlert>anyObject());
        expectLastCall().atLeastOnce();


        replay(contextService, adminService, motechService, mobileService, rctService);


        staffMessageServiceImpl.sendStaffCareMessages(forDate, forDate,
                forDate, forDate,
                careGroups,
                false,
                false);

        verify(contextService, adminService, motechService, mobileService, rctService);

        assertEquals(CareMessageGroupingStrategy.COMMUNITY, capturedStrategy.getValue());
    }

    @Test
    public void testShouldSendUpcomingCareMessageWhenThereAreUpcomingExpectedEncounters() {

        Date forDate = new Date();
        String careGroups[] = {"ANC", "TT", "IPT"};

        Location location = new Location();
        location.setName("Test Facility");
        location.setCountyDistrict(new KassenaNankana().toString());
        location.setRegion("Test Regiion");

        Facility facility = new Facility();
        facility.setLocation(location);
        String facilityPhoneNumber = "+1 555 123-1234";
        facility.setPhoneNumber(facilityPhoneNumber);

        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(facility);
        facility.setLocation(location);
        facility.setLocation(location);


        Patient p = new Patient(5716);

        ExpectedEncounter enc = new ExpectedEncounter();
        enc.setPatient(p);

        List<ExpectedEncounter> encounters = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> emptyObs = new ArrayList<ExpectedObs>();
        final DefaultedExpectedEncounterAlert defaultedExpectedEncounterAlert = anyObject();
        encounters.add(enc);

        // To Mock
        expect(motechService.getCommunityByPatient(p)).andReturn(null);
        expect(motechService.getCommunityByPatient(p)).andReturn(null);
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(motechService.facilityFor(p)).andReturn(facility).anyTimes();
        expect(contextService.getAdministrationService()).andReturn(adminService).anyTimes();
        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn("35").anyTimes();
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null,
                null, forDate, forDate, 35)).andReturn(encounters);
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null,
                forDate, null, forDate, 35)).andReturn(encounters);
        expect(motechService.getExpectedObs(null, facility, careGroups, null,
                null, forDate, forDate, 35)).andReturn(emptyObs);
        expect(motechService.getExpectedObs(null, facility, careGroups, null,
                forDate, null, forDate, 35)).andReturn(emptyObs);

        expect(motechService.getCareConfigurationFor(EasyMock.<String>anyObject())).andReturn(EasyMock.<CareConfiguration>anyObject());
        expect(motechService.getDefaultedEncounterAlertFor(enc)).andReturn(defaultedExpectedEncounterAlert);


        Capture<String> capturedMessageId = new Capture<String>();
        Capture<String> capturedPhoneNumber = new Capture<String>();
        Capture<Care[]> capturedCares = new Capture<Care[]>();
        Capture<CareMessageGroupingStrategy> capturedStrategy = new Capture<CareMessageGroupingStrategy>();
        Capture<MediaType> capturedMediaType = new Capture<MediaType>();
        Capture<Date> capturedStartDate = new Capture<Date>();
        Capture<Date> capturedEndDate = new Capture<Date>();

        expect(mobileService.sendDefaulterMessage(capture(capturedMessageId), capture(capturedPhoneNumber), capture(capturedCares), capture(capturedStrategy), capture(capturedMediaType), capture(capturedStartDate),
                capture(capturedEndDate))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED);

        expect(mobileService.sendBulkCaresMessage(capture(capturedMessageId), capture(capturedPhoneNumber), capture(capturedCares), capture(capturedStrategy), capture(capturedMediaType), capture(capturedStartDate),
                capture(capturedEndDate))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED);

        Capture<List<ExpectedEncounter>> defaultedEncounters = new Capture<List<ExpectedEncounter>>();
        expect(rctService.filterRCTEncounters(capture(defaultedEncounters))).andReturn(encounters);

        Capture<List<ExpectedObs>> defaulteObs = new Capture<List<ExpectedObs>>();
        expect(rctService.filterRCTObs(capture(defaulteObs))).andReturn(emptyObs);


        motechService.saveOrUpdateDefaultedEncounterAlert(EasyMock.<DefaultedExpectedEncounterAlert>anyObject());
        expectLastCall().atLeastOnce();

        replay(contextService, adminService, motechService, mobileService, rctService);

        staffMessageServiceImpl.sendStaffCareMessages(forDate, forDate,
                forDate, forDate,
                careGroups,
                true,
                false);

        verify(contextService, adminService, motechService, mobileService, rctService);

        assertEquals(CareMessageGroupingStrategy.COMMUNITY, capturedStrategy.getValue());
    }

    @Test
    public void testSendStaffCareMessagesWhenNoDefaulters() {

        Date forDate = new Date();
        String careGroups[] = {"ANC", "TT", "IPT"};

        Location location = new Location();
        location.setName("Test Facility");

        Facility facility = new Facility();
        facility.setLocation(location);
        facility.setPhoneNumber("+1 555 123-1234");

        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(facility);

        List<ExpectedEncounter> emptyEncounters = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> emptyObs = new ArrayList<ExpectedObs>();

        // To Mock
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(contextService.getAdministrationService()).andReturn(adminService).anyTimes();
        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn("35").anyTimes();
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null,
                null, forDate, forDate, 35)).andReturn(emptyEncounters);
        expect(motechService.getExpectedObs(null, facility, careGroups, null,
                null, forDate, forDate, 35)).andReturn(emptyObs);

        Capture<String> capturedMessage = new Capture<String>();
        Capture<String> capturedPhoneNumber = new Capture<String>();

        expect(mobileService.sendMessage(capture(capturedMessage), capture(capturedPhoneNumber))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED);

        replay(contextService, adminService, motechService, mobileService);

        staffMessageServiceImpl.sendStaffCareMessages(forDate, forDate,
                forDate, forDate,
                careGroups,
                false,
                false);

        verify(contextService, adminService, motechService, mobileService);

        assertEquals("Test Facility has no defaulters for this week", capturedMessage.getValue());
        assertEquals("+1 555 123-1234", capturedPhoneNumber.getValue());
    }

    @Test
    public void testShouldSendDefaulterAlertsForAllDefaultsOnAnEncounter() {
        int expectedNumberOfDefaultsOnAnEncounter = 50;
        for (int i = 0; i < expectedNumberOfDefaultsOnAnEncounter; i++) {
            reset(rctService, contextService, adminService, motechService, mobileService);
            testSendStaffCareMessagesGroupByCommunity();
        }
    }

    @Test
    public void testShouldSendNoUpcomingCareMessagesWhenThereAreNoUpcomingExpectedEncounters() {

        Date forDate = new Date();
        String careGroups[] = {"ANC", "TT", "IPT"};

        Location location = new Location();
        location.setName("Test Facility");

        Facility facility = new Facility();
        facility.setLocation(location);
        facility.setPhoneNumber("+1 555 123-1234");

        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(facility);

        List<ExpectedEncounter> emptyEncounters = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> emptyObs = new ArrayList<ExpectedObs>();

        // To Mock
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(contextService.getAdministrationService()).andReturn(adminService).anyTimes();
        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn("35").anyTimes();
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null,
                null, forDate, forDate, 35)).andReturn(emptyEncounters);
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null,
                forDate, null, forDate, 35)).andReturn(emptyEncounters);
        expect(motechService.getExpectedObs(null, facility, careGroups, null,
                null, forDate, forDate, 35)).andReturn(emptyObs);
        expect(motechService.getExpectedObs(null, facility, careGroups, null,
                forDate, null, forDate, 35)).andReturn(emptyObs);

        Capture<String> capturedDefaulterMessage = new Capture<String>();
        Capture<String> capturedDefaulterPhoneNumber = new Capture<String>();
        Capture<String> capturedUpcomingCareMessage = new Capture<String>();
        Capture<String> capturedUpcomingCarePhoneNumber = new Capture<String>();

        expect(mobileService.sendMessage(capture(capturedDefaulterMessage), capture(capturedDefaulterPhoneNumber))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED);
        expect(mobileService.sendMessage(capture(capturedUpcomingCareMessage), capture(capturedUpcomingCarePhoneNumber))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED);

        Capture<List<ExpectedEncounter>> defaultedEncounters = new Capture<List<ExpectedEncounter>>();
        expect(rctService.filterRCTEncounters(capture(defaultedEncounters))).andReturn(emptyEncounters);

        Capture<List<ExpectedObs>> defaulteObs = new Capture<List<ExpectedObs>>();
        expect(rctService.filterRCTObs(capture(defaulteObs))).andReturn(emptyObs);

        replay(contextService, adminService, motechService, mobileService, rctService);

        staffMessageServiceImpl.sendStaffCareMessages(forDate, forDate,
                forDate, forDate,
                careGroups,
                true,
                false);

        verify(contextService, adminService, motechService, mobileService, rctService);

        assertEquals("Test Facility has no defaulters for this week", capturedDefaulterMessage.getValue());
        assertEquals("+1 555 123-1234", capturedDefaulterPhoneNumber.getValue());

        assertEquals("Test Facility has no upcoming care for this week", capturedUpcomingCareMessage.getValue());
        assertEquals("+1 555 123-1234", capturedUpcomingCarePhoneNumber.getValue());
    }

    @Test
    public void testShouldFindOutIfMessageTimeIsDuringBlackoutPeriod() {
        DateUtil dateUtil = new DateUtil();
        Calendar calendar = dateUtil.getCalendarWithTime(23, 13, 54);
        Date morningMessageTime = calendar.getTime();
        calendar = dateUtil.getCalendarWithTime(3, 13, 54);
        Date nightMessageTime = calendar.getTime();
        calendar = dateUtil.getCalendarWithTime(19, 30, 30);
        Date eveningMessageTime = calendar.getTime();

        Blackout blackout = new Blackout(Time.valueOf("23:00:00"), Time.valueOf("06:00:00"));

        expect(contextService.getMotechService()).andReturn(motechService).times(3);
        expect(motechService.getBlackoutSettings()).andReturn(blackout).times(3);
        replay(contextService, adminService, motechService);

        assertTrue(staffMessageServiceImpl.isMessageTimeWithinBlackoutPeriod(morningMessageTime));
        assertTrue(staffMessageServiceImpl.isMessageTimeWithinBlackoutPeriod(nightMessageTime));
        assertFalse(staffMessageServiceImpl.isMessageTimeWithinBlackoutPeriod(eveningMessageTime));
        verify(contextService, adminService, motechService);

    }

    @Test
    public void testShouldSendDefaulterMessagesToAllPhoneNumbersOfAFacility() {
        Date forDate = new Date();
        String careGroups[] = {"ANC", "TT", "IPT"};

        Location location = new Location();
        location.setName("Test Facility");
        location.setRegion("Upper East");
        location.setCountyDistrict(new KassenaNankana().toString());

        Facility facility = new Facility();
        facility.setLocation(location);
        facility.setPhoneNumber("+1 555 123-1234");
        facility.setAdditionalPhoneNumber1("+ 2 555 123-1234");

        List<Facility> facilities = new ArrayList<Facility>();
        facilities.add(facility);


        Patient p = new Patient(5716);


        ExpectedEncounter enc = new ExpectedEncounter();
        enc.setPatient(p);

        List<ExpectedEncounter> encounters = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> emptyObs = new ArrayList<ExpectedObs>();

        encounters.add(enc);

        final DefaultedExpectedEncounterAlert defaultedExpectedEncounterAlert = anyObject();

        // To Mock
        expect(motechService.getCommunityByPatient(p)).andReturn(null);
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(contextService.getAdministrationService()).andReturn(adminService).times(2);

        expect(adminService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn("35").anyTimes();
        expect(motechService.getExpectedEncounter(null, facility, careGroups, null, null, forDate, forDate, 35)).andReturn(encounters);
        expect(motechService.getExpectedObs(null, facility, careGroups, null, null, forDate, forDate, 35)).andReturn(emptyObs);

        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(motechService.getCareConfigurationFor(EasyMock.<String>anyObject())).andReturn(EasyMock.<CareConfiguration>anyObject());
        expect(motechService.getDefaultedEncounterAlertFor(enc)).andReturn(defaultedExpectedEncounterAlert);


        Capture<String> capturedMessageId = new Capture<String>();
        Capture<String> capturedPhoneNumber = new Capture<String>();
        Capture<Care[]> capturedCares = new Capture<Care[]>();
        Capture<CareMessageGroupingStrategy> capturedStrategy = new Capture<CareMessageGroupingStrategy>();
        Capture<MediaType> capturedMediaType = new Capture<MediaType>();
        Capture<Date> capturedStartDate = new Capture<Date>();
        Capture<Date> capturedEndDate = new Capture<Date>();


        expect(mobileService.sendDefaulterMessage(capture(capturedMessageId), capture(capturedPhoneNumber),
                capture(capturedCares), capture(capturedStrategy),
                capture(capturedMediaType), capture(capturedStartDate),
                capture(capturedEndDate))).andReturn(org.motechproject.ws.MessageStatus.DELIVERED).times(2);

        motechService.saveOrUpdateDefaultedEncounterAlert(EasyMock.<DefaultedExpectedEncounterAlert>anyObject());
        expectLastCall().atLeastOnce();


        replay(contextService, adminService, motechService, mobileService, rctService);


        staffMessageServiceImpl.sendStaffCareMessages(forDate, forDate,
                forDate, forDate,
                careGroups,
                false,
                false);

        verify(contextService, adminService, motechService, mobileService, rctService);

        assertEquals(CareMessageGroupingStrategy.COMMUNITY, capturedStrategy.getValue());
    }

    private static DefaultedExpectedEncounterAlert equalsExpectedEncounterAlert(DefaultedExpectedEncounterAlert alert) {
        reportMatcher(new DefaultedExpectedEncounterAlertMatcher(alert));
        return alert;
    }

    private static DefaultedExpectedObsAlert equalsExpectedObsAlert(DefaultedExpectedObsAlert alert) {
        reportMatcher(new DefaultedExpectedObsAlertMatcher(alert));
        return alert;
    }

    private List<Facility> facilitiesFor(String... regions) {
        List<Facility> facilities = new ArrayList<Facility>();
        for (String region : regions) {
            Facility facility = new Facility();
            facility.setId(1L);
            facility.setPhoneNumber("0123456789");
            Location location = new Location();
            location.setRegion(region);
            facility.setLocation(location);
            facilities.add(facility);
        }
        return facilities;
    }

}
