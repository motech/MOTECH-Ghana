/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.server.svc.impl;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.server.messaging.MessageNotFoundException;
import org.motechproject.server.model.*;
import org.motechproject.server.model.MessageStatus;
import org.motechproject.server.omod.*;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.*;
import org.openmrs.*;
import org.openmrs.Patient;
import org.openmrs.api.*;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.util.OpenmrsConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class RegistrarBeanTest {

    RegistrarBean regBean;

    ContextService contextService;
    LocationService locationService;
    PersonService personService;
    UserService userService;
    PatientService patientService;
    EncounterService encounterService;
    ObsService obsService;
    ConceptService conceptService;
    MotechService motechService;
    IdentifierSourceService idService;
    RelationshipService relationshipService;
    AuthenticationService authenticationService;

    SequentialIdentifierGenerator staffIdGenerator;

    Location ghanaLocation;
    PatientIdentifierType motechIdType;
    PatientIdentifierType staffIdType;
    PersonAttributeType phoneAttributeType;
    PersonAttributeType nhisAttributeType;
    PersonAttributeType languageAttributeType;
    PersonAttributeType phoneTypeAttributeType;
    PersonAttributeType mediaTypeAttributeType;
    PersonAttributeType deliveryTimeAttributeType;
    PersonAttributeType deliveryDayAttributeType;
    PersonAttributeType nhisExpirationType;
    PersonAttributeType insuredAttributeType;
    PersonAttributeType howLearnedAttributeType;
    PersonAttributeType interestReasonAttributeType;
    Role providerRole;
    EncounterType ancVisitType;
    EncounterType pncChildVisitType;
    EncounterType pncMotherVisitType;
    EncounterType pregnancyRegVisitType;
    EncounterType pregnancyTermVisitType;
    EncounterType pregnancyDelVisitType;
    EncounterType outpatientVisitType;
    EncounterType registrationVisitType;
    ConceptName immunizationConceptNameObj;
    Concept immunizationConcept;
    ConceptName tetanusConceptNameObj;
    Concept tetanusConcept;
    ConceptName iptConceptNameObj;
    Concept iptConcept;
    ConceptName itnConceptNameObj;
    Concept itnConcept;
    ConceptName visitNumConceptNameObj;
    Concept visitNumConcept;
    ConceptName pregConceptNameObj;
    Concept pregConcept;
    ConceptName pregStatusConceptNameObj;
    Concept pregStatusConcept;
    ConceptName dateConfConceptNameObj;
    Concept dateConfConcept;
    ConceptName dateConfConfirmedConceptNameObj;
    Concept dateConfConfirmedConcept;
    ConceptName gravidaConceptNameObj;
    Concept gravidaConcept;
    ConceptName parityConceptNameObj;
    Concept parityConcept;
    ConceptName refDateNameObj;
    Concept refDateConcept;
    ConceptName hivTestNameObj;
    Concept hivTestConcept;
    ConceptName terminationTypeNameObj;
    Concept terminationTypeConcept;
    ConceptName terminationComplicationNameObj;
    Concept terminationComplicationConcept;
    ConceptName iptiNameObj;
    Concept iptiConcept;
    ConceptName opvDoseNameObj;
    Concept opvDoseConcept;
    ConceptName pentaDoseNameObj;
    Concept pentaDoseConcept;
    ConceptName csmNameObj;
    Concept csmConcept;
    ConceptName deathCauseNameObj;
    Concept deathCauseConcept;
    ConceptName maternalDeathCauseNameObj;
    Concept maternalDeathCauseConcept;
    ConceptName serialNumberNameObj;
    Concept serialNumberConcept;
    ConceptName newCaseNameObj;
    Concept newCaseConcept;
    ConceptName referredNameObj;
    Concept referredConcept;
    ConceptName diagnosisNameObj;
    Concept diagnosisConcept;
    ConceptName secondDiagnosisNameObj;
    Concept secondDiagnosisConcept;
    ConceptName deliveyModeNameObj;
    Concept deliveyModeConcept;
    ConceptName deliveryLocationNameObj;
    Concept deliveryLocationConcept;
    ConceptName deliveredByNameObj;
    Concept deliveredByConcept;
    ConceptName deliveryOutcomeNameObj;
    Concept deliveryOutcomeConcept;
    ConceptName birthOutcomeNameObj;
    Concept birthOutcomeConcept;
    RelationshipType parentChildRelationshipType;
    IdentifierGenerator identifierGenerator;
    private RegistrarBeanImpl regBeanImpl;

    @Before
    public void setUp() throws Exception {
        contextService = createMock(ContextService.class);

        locationService = createMock(LocationService.class);
        personService = createMock(PersonService.class);
        userService = createMock(UserService.class);
        patientService = createMock(PatientService.class);
        encounterService = createMock(EncounterService.class);
        obsService = createMock(ObsService.class);
        conceptService = createMock(ConceptService.class);
        motechService = createMock(MotechService.class);
        idService = createMock(IdentifierSourceService.class);
        relationshipService = createMock(RelationshipService.class);
        identifierGenerator = createMock(IdentifierGenerator.class);
        authenticationService = createMock(AuthenticationService.class);

        ghanaLocation = new Location(1);
        ghanaLocation.setName(MotechConstants.LOCATION_GHANA);

        motechIdType = new PatientIdentifierType(1);
        motechIdType.setName(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);

        staffIdType = new PatientIdentifierType(2);
        staffIdType.setName(MotechConstants.PATIENT_IDENTIFIER_STAFF_ID);

        staffIdGenerator = new SequentialIdentifierGenerator();
        staffIdGenerator.setName(MotechConstants.IDGEN_SEQ_ID_GEN_STAFF_ID);
        staffIdGenerator.setIdentifierType(staffIdType);

        phoneAttributeType = new PersonAttributeType(2);
        phoneAttributeType
                .setName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);

        nhisAttributeType = new PersonAttributeType(4);
        nhisAttributeType.setName(MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER);

        languageAttributeType = new PersonAttributeType(5);
        languageAttributeType
                .setName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE);

        phoneTypeAttributeType = new PersonAttributeType(6);
        phoneTypeAttributeType
                .setName(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE);

        mediaTypeAttributeType = new PersonAttributeType(7);
        mediaTypeAttributeType
                .setName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);

        deliveryTimeAttributeType = new PersonAttributeType(8);
        deliveryTimeAttributeType
                .setName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);

        nhisExpirationType = new PersonAttributeType(12);
        nhisExpirationType
                .setName(MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE);

        insuredAttributeType = new PersonAttributeType(18);
        insuredAttributeType.setName(MotechConstants.PERSON_ATTRIBUTE_INSURED);

        howLearnedAttributeType = new PersonAttributeType(23);
        howLearnedAttributeType
                .setName(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED);

        interestReasonAttributeType = new PersonAttributeType(24);
        interestReasonAttributeType
                .setName(MotechConstants.PERSON_ATTRIBUTE_INTEREST_REASON);

        deliveryDayAttributeType = new PersonAttributeType(25);
        deliveryDayAttributeType
                .setName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY);

        providerRole = new Role(OpenmrsConstants.PROVIDER_ROLE);

        ancVisitType = new EncounterType(1);
        ancVisitType.setName(MotechConstants.ENCOUNTER_TYPE_ANCVISIT);

        pncChildVisitType = new EncounterType(2);
        pncChildVisitType.setName(MotechConstants.ENCOUNTER_TYPE_PNCCHILDVISIT);

        pncMotherVisitType = new EncounterType(3);
        pncMotherVisitType
                .setName(MotechConstants.ENCOUNTER_TYPE_PNCMOTHERVISIT);

        pregnancyRegVisitType = new EncounterType(4);
        pregnancyRegVisitType
                .setName(MotechConstants.ENCOUNTER_TYPE_PREGREGVISIT);

        outpatientVisitType = new EncounterType(5);
        outpatientVisitType
                .setName(MotechConstants.ENCOUNTER_TYPE_OUTPATIENTVISIT);

        pregnancyTermVisitType = new EncounterType(6);
        pregnancyTermVisitType
                .setName(MotechConstants.ENCOUNTER_TYPE_PREGTERMVISIT);

        pregnancyDelVisitType = new EncounterType(7);
        pregnancyDelVisitType
                .setName(MotechConstants.ENCOUNTER_TYPE_PREGDELVISIT);

        registrationVisitType = new EncounterType(8);
        registrationVisitType
                .setName(MotechConstants.ENCOUNTER_TYPE_PATIENTREGVISIT);

        immunizationConceptNameObj = new ConceptName(
                MotechConstants.CONCEPT_IMMUNIZATIONS_ORDERED, Locale
                        .getDefault());
        immunizationConcept = new Concept(6);

        tetanusConceptNameObj = new ConceptName(
                MotechConstants.CONCEPT_TETANUS_TOXOID_DOSE, Locale
                        .getDefault());
        tetanusConcept = new Concept(7);

        iptConceptNameObj = new ConceptName(
                MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT_DOSE,
                Locale.getDefault());
        iptConcept = new Concept(8);

        itnConceptNameObj = new ConceptName(
                MotechConstants.CONCEPT_INSECTICIDE_TREATED_NET_USAGE, Locale
                        .getDefault());
        itnConcept = new Concept(9);

        visitNumConceptNameObj = new ConceptName(
                MotechConstants.CONCEPT_VISIT_NUMBER, Locale.getDefault());
        visitNumConcept = new Concept(10);

        pregStatusConceptNameObj = new ConceptName(
                MotechConstants.CONCEPT_PREGNANCY_STATUS, Locale.getDefault());
        pregStatusConcept = new Concept(18);

        dateConfConceptNameObj = new ConceptName(
                MotechConstants.CONCEPT_ESTIMATED_DATE_OF_CONFINEMENT, Locale
                        .getDefault());
        dateConfConcept = new Concept(19);

        gravidaConceptNameObj = new ConceptName(
                MotechConstants.CONCEPT_GRAVIDA, Locale.getDefault());
        gravidaConcept = new Concept(20);

        parityConceptNameObj = new ConceptName(MotechConstants.CONCEPT_PARITY,
                Locale.getDefault());
        parityConcept = new Concept(22);

        dateConfConfirmedConceptNameObj = new ConceptName(
                MotechConstants.CONCEPT_DATE_OF_CONFINEMENT_CONFIRMED, Locale
                        .getDefault());
        dateConfConfirmedConcept = new Concept(23);

        pregConceptNameObj = new ConceptName(MotechConstants.CONCEPT_PREGNANCY,
                Locale.getDefault());
        pregConcept = new Concept(24);

        refDateNameObj = new ConceptName(
                MotechConstants.CONCEPT_ENROLLMENT_REFERENCE_DATE, Locale
                        .getDefault());
        refDateConcept = new Concept(25);

        hivTestNameObj = new ConceptName(
                MotechConstants.CONCEPT_HIV_TEST_RESULT, Locale.getDefault());
        hivTestConcept = new Concept(26);

        terminationTypeNameObj = new ConceptName(
                MotechConstants.CONCEPT_TERMINATION_TYPE, Locale.getDefault());
        terminationTypeConcept = new Concept(27);

        terminationComplicationNameObj = new ConceptName(
                MotechConstants.CONCEPT_TERMINATION_COMPLICATION, Locale
                        .getDefault());
        terminationComplicationConcept = new Concept(28);

        iptiNameObj = new ConceptName(
                MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT_INFANTS_DOSE,
                Locale.getDefault());
        iptiConcept = new Concept(29);

        opvDoseNameObj = new ConceptName(
                MotechConstants.CONCEPT_ORAL_POLIO_VACCINATION_DOSE, Locale
                        .getDefault());
        opvDoseConcept = new Concept(30);

        pentaDoseNameObj = new ConceptName(
                MotechConstants.CONCEPT_PENTA_VACCINATION_DOSE, Locale
                        .getDefault());
        pentaDoseConcept = new Concept(31);

        csmNameObj = new ConceptName(
                MotechConstants.CONCEPT_CEREBRO_SPINAL_MENINGITIS_VACCINATION,
                Locale.getDefault());
        csmConcept = new Concept(32);

        deathCauseNameObj = new ConceptName(
                MotechConstants.CONCEPT_CAUSE_OF_DEATH, Locale.getDefault());
        deathCauseConcept = new Concept(33);

        serialNumberNameObj = new ConceptName(
                MotechConstants.CONCEPT_SERIAL_NUMBER, Locale.getDefault());
        serialNumberConcept = new Concept(35);

        newCaseNameObj = new ConceptName(MotechConstants.CONCEPT_NEW_CASE,
                Locale.getDefault());
        newCaseConcept = new Concept(36);

        referredNameObj = new ConceptName(MotechConstants.CONCEPT_REFERRED,
                Locale.getDefault());
        referredConcept = new Concept(37);

        diagnosisNameObj = new ConceptName(
                MotechConstants.CONCEPT_PRIMARY_DIAGNOSIS, Locale.getDefault());
        diagnosisConcept = new Concept(38);

        secondDiagnosisNameObj = new ConceptName(
                MotechConstants.CONCEPT_SECONDARY_DIAGNOSIS, Locale
                        .getDefault());
        secondDiagnosisConcept = new Concept(39);

        deliveyModeNameObj = new ConceptName(
                MotechConstants.CONCEPT_DELIVERY_MODE, Locale.getDefault());
        deliveyModeConcept = new Concept(40);

        deliveryLocationNameObj = new ConceptName(
                MotechConstants.CONCEPT_DELIVERY_LOCATION, Locale.getDefault());
        deliveryLocationConcept = new Concept(41);

        deliveredByNameObj = new ConceptName(
                MotechConstants.CONCEPT_DELIVERED_BY, Locale.getDefault());
        deliveredByConcept = new Concept(42);

        deliveryOutcomeNameObj = new ConceptName(
                MotechConstants.CONCEPT_DELIVERY_OUTCOME, Locale.getDefault());
        deliveryOutcomeConcept = new Concept(43);

        birthOutcomeNameObj = new ConceptName(
                MotechConstants.CONCEPT_BIRTH_OUTCOME, Locale.getDefault());
        birthOutcomeConcept = new Concept(44);

        parentChildRelationshipType = new RelationshipType(1);
        parentChildRelationshipType.setaIsToB("Parent");
        parentChildRelationshipType.setbIsToA("Child");

        regBeanImpl = new RegistrarBeanImpl();
        regBeanImpl.setContextService(contextService);
        regBeanImpl.setRelationshipService(relationshipService);
        regBeanImpl.setIdentifierGenerator(identifierGenerator);
        regBeanImpl.setLocationService(locationService);
        regBeanImpl.setPersonService(personService);
        regBeanImpl.setUserService(userService);
        regBeanImpl.setPatientService(patientService);
        regBeanImpl.setEncounterService(encounterService);
        regBeanImpl.setObsService(obsService);
        regBeanImpl.setConceptService(conceptService);
        regBeanImpl.setAuthenticationService(authenticationService);
    }

    @After
    public void tearDown() throws Exception {
        regBean = null;

        contextService = null;
        locationService = null;
        personService = null;
        userService = null;
        patientService = null;
        encounterService = null;
        obsService = null;
        conceptService = null;
        motechService = null;
        idService = null;

        staffIdGenerator = null;
    }

    @Test
    @Ignore
    public void registerPregnantMother() throws ParseException {
        regBean = regBeanImpl;
        Integer motechId = 123456;
        String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
        String nhis = "456DEF";
        String address = "Address";
        String phoneNumber = "2075555555";
        String language = "Language";
        Date date = new Date();
        Boolean birthDateEst = true, insured = true, dueDateConfirmed = true, enroll = true, consent = true;
        Gender gender = Gender.FEMALE;
        ContactNumberType phoneType = ContactNumberType.PERSONAL;
        MediaType mediaType = MediaType.TEXT;
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        InterestReason reason = InterestReason.CURRENTLY_PREGNANT;
        HowLearned howLearned = HowLearned.FRIEND;

        Patient patient = new Patient(2);
        Location ghanaLocation = new Location(1);
        Community community = new Community();
        Facility facility = new Facility();
        Location facilityLocation = new Location(2);
        facilityLocation
                .setCountyDistrict(MotechConstants.LOCATION_KASSENA_NANKANA);
        facility.setLocation(facilityLocation);
        community.setFacility(facility);

        Capture<Patient> patientCap = new Capture<Patient>();
        Capture<Encounter> pregnancyEncounterCap = new Capture<Encounter>();
        Capture<Encounter> registrationEncounterCap = new Capture<Encounter>();
        Capture<Obs> pregnancyObsCap = new Capture<Obs>();

        expect(contextService.getPatientService()).andReturn(patientService)
                .atLeastOnce();
        expect(contextService.getPersonService()).andReturn(personService)
                .atLeastOnce();
        expect(contextService.getLocationService()).andReturn(locationService)
                .atLeastOnce();
        expect(contextService.getEncounterService())
                .andReturn(encounterService).atLeastOnce();
        expect(contextService.getObsService()).andReturn(obsService);
        expect(contextService.getConceptService()).andReturn(conceptService)
                .atLeastOnce();
        expect(contextService.getMotechService()).andReturn(motechService)
                .atLeastOnce();
        expect(contextService.getAuthenticatedUser()).andReturn(new User());
        expect(idService.saveLogEntry((LogEntry) anyObject())).andReturn(
                new LogEntry());
        expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
                .andReturn(ghanaLocation);


        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_INSURED))
                .andReturn(insuredAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER))
                .andReturn(nhisAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE))
                .andReturn(nhisExpirationType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER))
                .andReturn(phoneAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE))
                .andReturn(phoneTypeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE))
                .andReturn(mediaTypeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE))
                .andReturn(languageAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY))
                .andReturn(deliveryDayAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME))
                .andReturn(deliveryTimeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED))
                .andReturn(howLearnedAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_INTEREST_REASON))
                .andReturn(interestReasonAttributeType);

        expect(patientService.savePatient(capture(patientCap))).andReturn(
                patient);

        expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
                .andReturn(ghanaLocation);
        expect(
                encounterService
                        .getEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGREGVISIT))
                .andReturn(pregnancyRegVisitType);
        expect(encounterService.saveEncounter(capture(pregnancyEncounterCap)))
                .andReturn(new Encounter());
        expect(conceptService.getConcept(MotechConstants.CONCEPT_PREGNANCY))
                .andReturn(pregConcept);
        expect(
                conceptService
                        .getConcept(MotechConstants.CONCEPT_PREGNANCY_STATUS))
                .andReturn(pregStatusConcept);
        expect(
                conceptService
                        .getConcept(MotechConstants.CONCEPT_ESTIMATED_DATE_OF_CONFINEMENT))
                .andReturn(dateConfConcept);
        expect(
                conceptService
                        .getConcept(MotechConstants.CONCEPT_DATE_OF_CONFINEMENT_CONFIRMED))
                .andReturn(dateConfConfirmedConcept);
        expect(
                obsService.saveObs(capture(pregnancyObsCap),
                        (String) anyObject())).andReturn(new Obs());
        expect(
                patientService.
                        getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID))
                .andReturn(motechIdType).atLeastOnce();

        replay(contextService, patientService, motechService, personService,
                locationService, userService, encounterService, obsService,
                conceptService, idService);

        regBean.registerPatient(RegistrationMode.USE_PREPRINTED_ID, motechId,
                RegistrantType.PREGNANT_MOTHER, firstName, middleName,
                lastName, prefName, date, birthDateEst, gender, insured, nhis,
                date, null, community, address, phoneNumber, date,
                dueDateConfirmed, enroll, consent, phoneType, mediaType,
                language, dayOfWeek, date, reason, howLearned, null);

        verify(contextService, patientService, motechService, personService,
                locationService, userService, encounterService, obsService,
                conceptService, idService);

        Patient capturedPatient = patientCap.getValue();
        assertEquals(motechId.toString(), capturedPatient.getPatientIdentifier(
                motechIdType).getIdentifier());
        assertEquals(prefName, capturedPatient.getGivenName());
        assertEquals(lastName, capturedPatient.getFamilyName());
        assertEquals(middleName, capturedPatient.getMiddleName());
        Iterator<PersonName> names = capturedPatient.getNames().iterator();
        while (names.hasNext()) {
            PersonName personName = names.next();
            if (personName.isPreferred()) {
                assertEquals(prefName, personName.getGivenName());
                assertEquals(lastName, personName.getFamilyName());
                assertEquals(middleName, personName.getMiddleName());
            } else {
                assertEquals(firstName, personName.getGivenName());
                assertEquals(lastName, personName.getFamilyName());
                assertEquals(middleName, personName.getMiddleName());
            }
        }
        assertEquals(date, capturedPatient.getBirthdate());
        assertEquals(birthDateEst, capturedPatient.getBirthdateEstimated());
        assertEquals(GenderTypeConverter.toOpenMRSString(Gender.FEMALE),
                capturedPatient.getGender());
        assertEquals(1, community.getResidents().size());
        assertEquals(address, capturedPatient.getPersonAddress().getAddress1());
        assertEquals(insured, Boolean.valueOf(capturedPatient.getAttribute(
                insuredAttributeType).getValue()));
        assertEquals(nhis, capturedPatient.getAttribute(nhisAttributeType)
                .getValue());
        Date nhisExpDate = (new SimpleDateFormat(MotechConstants.DATE_FORMAT))
                .parse(capturedPatient.getAttribute(nhisExpirationType)
                        .getValue());
        Calendar nhisExpCal = Calendar.getInstance();
        nhisExpCal.setTime(date);
        int year = nhisExpCal.get(Calendar.YEAR);
        int month = nhisExpCal.get(Calendar.MONTH);
        int day = nhisExpCal.get(Calendar.DAY_OF_MONTH);
        nhisExpCal.setTime(nhisExpDate);
        assertEquals(year, nhisExpCal.get(Calendar.YEAR));
        assertEquals(month, nhisExpCal.get(Calendar.MONTH));
        assertEquals(day, nhisExpCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(phoneNumber.toString(), capturedPatient.getAttribute(
                phoneAttributeType).getValue());
        assertEquals(phoneType, ContactNumberType.valueOf(capturedPatient
                .getAttribute(phoneTypeAttributeType).getValue()));
        assertEquals(mediaType, MediaType.valueOf(capturedPatient.getAttribute(
                mediaTypeAttributeType).getValue()));
        assertEquals(language, capturedPatient.getAttribute(
                languageAttributeType).getValue());
        assertEquals(howLearned, HowLearned.valueOf(capturedPatient
                .getAttribute(howLearnedAttributeType).getValue()));
        assertEquals(reason, InterestReason.valueOf(capturedPatient
                .getAttribute(interestReasonAttributeType).getValue()));
        assertEquals(dayOfWeek, DayOfWeek.valueOf(capturedPatient.getAttribute(
                deliveryDayAttributeType).getValue()));
        Calendar timeOfDayCal = Calendar.getInstance();
        timeOfDayCal.setTime(date);
        int hour = timeOfDayCal.get(Calendar.HOUR_OF_DAY);
        int min = timeOfDayCal.get(Calendar.MINUTE);
        Date timeOfDayDate = (new SimpleDateFormat(
                MotechConstants.TIME_FORMAT_DELIVERY_TIME))
                .parse(capturedPatient.getAttribute(deliveryTimeAttributeType)
                        .getValue());
        timeOfDayCal.setTime(timeOfDayDate);
        assertEquals(hour, timeOfDayCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(min, timeOfDayCal.get(Calendar.MINUTE));

        Encounter pregnancyEncounter = pregnancyEncounterCap.getValue();
        assertNotNull(pregnancyEncounter.getEncounterDatetime());
        assertEquals(ghanaLocation, pregnancyEncounter.getLocation());
        assertEquals(patient, pregnancyEncounter.getPatient());
        assertEquals(pregnancyRegVisitType, pregnancyEncounter
                .getEncounterType());

        Obs pregnancyObs = pregnancyObsCap.getValue();
        assertNotNull(pregnancyObs.getObsDatetime());
        assertEquals(patient.getPatientId(), pregnancyObs.getPerson()
                .getPersonId());
        assertEquals(ghanaLocation, pregnancyObs.getLocation());
        assertEquals(pregConcept, pregnancyObs.getConcept());

        Set<Obs> pregnancyObsMembers = pregnancyObs.getGroupMembers();
        assertEquals(3, pregnancyObsMembers.size());

        boolean containsPregnancyStatusObs = false;
        boolean containsDueDateObs = false;
        boolean containsDueDateConfirmedObs = false;
        Iterator<Obs> obsIterator = pregnancyObsMembers.iterator();
        while (obsIterator.hasNext()) {
            Obs memberObs = obsIterator.next();
            assertEquals(patient.getPatientId(), memberObs.getPerson()
                    .getPersonId());
            assertEquals(ghanaLocation, memberObs.getLocation());
            if (pregStatusConcept.equals(memberObs.getConcept())) {
                containsPregnancyStatusObs = true;
                assertEquals(Boolean.TRUE, memberObs.getValueAsBoolean());
            } else if (dateConfConcept.equals(memberObs.getConcept())) {
                containsDueDateObs = true;
                assertEquals(date, memberObs.getValueDatetime());
            } else if (dateConfConfirmedConcept.equals(memberObs.getConcept())) {
                containsDueDateConfirmedObs = true;
                assertEquals(dueDateConfirmed, memberObs.getValueAsBoolean());
            }
        }
        assertTrue("Pregnancy Status Obs missing", containsPregnancyStatusObs);
        assertTrue("Due Date Obs missing", containsDueDateObs);
        assertTrue("Due Date Confirmed Obs missing",
                containsDueDateConfirmedObs);

        Encounter registration = registrationEncounterCap.getValue();
        assertEquals(0, registration.getAllObs(true).size());
        assertNotNull("Registation date is null", registration
                .getEncounterDatetime());
    }

    @Test
    public void registerChild() throws ParseException {
        regBean = regBeanImpl;
        Integer motechId = 123456;
        String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
        String nhis = "456DEF";
        String address = "Address";
        String phoneNumber = "2075555555";
        String language = "Language";
        Date date = new Date();
        Boolean birthDateEst = true, insured = true, dueDateConfirmed = true, enroll = true, consent = true;
        Gender gender = Gender.FEMALE;
        ContactNumberType phoneType = ContactNumberType.PERSONAL;
        MediaType mediaType = MediaType.TEXT;
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        InterestReason reason = InterestReason.FAMILY_FRIEND_PREGNANT;
        HowLearned howLearned = HowLearned.FRIEND;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -2);
        Date birthDate = calendar.getTime();

        String pregnancyProgramName = "Weekly Info Child Message Program";
        String careProgramName = "Expected Care Message Program";

        Patient child = new Patient(1);
        child.setBirthdate(birthDate);
        Patient mother = new Patient(2);
        Location ghanaLocation = new Location(1);
        Community community = new Community();
        Facility facility = new Facility();
        Location facilityLocation = new Location(2);
        facilityLocation
                .setCountyDistrict(MotechConstants.LOCATION_KASSENA_NANKANA_WEST);
        facility.setLocation(facilityLocation);
        community.setFacility(facility);

        Capture<Patient> patientCap = new Capture<Patient>();
        Capture<MessageProgramEnrollment> enrollment1Cap = new Capture<MessageProgramEnrollment>();
        Capture<MessageProgramEnrollment> enrollment2Cap = new Capture<MessageProgramEnrollment>();
        Capture<Encounter> registrationEncounterCap = new Capture<Encounter>();


        expect(
                contextService.getPatientService()).andReturn(patientService).atLeastOnce();
        expect(
                contextService.getMotechService()).andReturn(motechService).atLeastOnce();
        expect(
                contextService.getEncounterService()).andReturn(encounterService).atLeastOnce();
        expect(
                authenticationService.getAuthenticatedUser()).andReturn(new User());
        expect(
                patientService
                        .getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID))
                .andReturn(motechIdType).atLeastOnce();
        expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
                .andReturn(ghanaLocation).times(2);

        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_INSURED))
                .andReturn(insuredAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER))
                .andReturn(nhisAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE))
                .andReturn(nhisExpirationType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER))
                .andReturn(phoneAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE))
                .andReturn(phoneTypeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE))
                .andReturn(mediaTypeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE))
                .andReturn(languageAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY))
                .andReturn(deliveryDayAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME))
                .andReturn(deliveryTimeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED))
                .andReturn(howLearnedAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_INTEREST_REASON))
                .andReturn(interestReasonAttributeType);

        expect(patientService.savePatient(capture(patientCap)))
                .andReturn(child);

        expect(
                motechService.getActiveMessageProgramEnrollments(child
                        .getPatientId(), pregnancyProgramName, null, null,
                        null, null)).andReturn(
                new ArrayList<MessageProgramEnrollment>());
        expect(
                motechService
                        .saveMessageProgramEnrollment(capture(enrollment1Cap)))
                .andReturn(new MessageProgramEnrollment());
        expect(
                motechService.getActiveMessageProgramEnrollments(child
                        .getPatientId(), careProgramName, null, null, null,
                        null)).andReturn(
                new ArrayList<MessageProgramEnrollment>());
        expect(
                motechService
                        .saveMessageProgramEnrollment(capture(enrollment2Cap)))
                .andReturn(new MessageProgramEnrollment());
        expect(
                encounterService
                        .getEncounterType(MotechConstants.ENCOUNTER_TYPE_PATIENTREGVISIT))
                .andReturn(registrationVisitType);
        expect(
                encounterService
                        .saveEncounter(capture(registrationEncounterCap)))
                .andReturn(new Encounter());

        replay(contextService, patientService, motechService, personService,
                locationService, userService, encounterService, obsService,
                conceptService, idService, authenticationService);

        regBean.registerPatient(RegistrationMode.USE_PREPRINTED_ID, motechId,
                RegistrantType.CHILD_UNDER_FIVE, firstName, middleName,
                lastName, prefName, birthDate, birthDateEst, gender, insured,
                nhis, date, mother, community, address, phoneNumber, date,
                dueDateConfirmed, enroll, consent, phoneType, mediaType,
                language, dayOfWeek, date, reason, howLearned, null);

        verify(contextService, patientService, motechService, personService,
                locationService, userService, encounterService, obsService,
                conceptService, idService, authenticationService);

        Patient capturedPatient = patientCap.getValue();
        assertEquals(prefName, capturedPatient.getGivenName());
        assertEquals(lastName, capturedPatient.getFamilyName());
        assertEquals(middleName, capturedPatient.getMiddleName());
        Iterator<PersonName> names = capturedPatient.getNames().iterator();
        while (names.hasNext()) {
            PersonName personName = names.next();
            if (personName.isPreferred()) {
                assertEquals(prefName, personName.getGivenName());
                assertEquals(lastName, personName.getFamilyName());
                assertEquals(middleName, personName.getMiddleName());
            } else {
                assertEquals(firstName, personName.getGivenName());
                assertEquals(lastName, personName.getFamilyName());
                assertEquals(middleName, personName.getMiddleName());
            }
        }
        assertEquals(birthDate, capturedPatient.getBirthdate());
        assertEquals(birthDateEst, capturedPatient.getBirthdateEstimated());
        assertEquals(GenderTypeConverter.toOpenMRSString(gender),
                capturedPatient.getGender());
        assertEquals(address, capturedPatient.getPersonAddress().getAddress1());
        assertEquals(insured, Boolean.valueOf(capturedPatient.getAttribute(
                insuredAttributeType).getValue()));
        assertEquals(nhis, capturedPatient.getAttribute(nhisAttributeType)
                .getValue());
        Date nhisExpDate = (new SimpleDateFormat(MotechConstants.DATE_FORMAT))
                .parse(capturedPatient.getAttribute(nhisExpirationType)
                        .getValue());
        Calendar nhisExpCal = Calendar.getInstance();
        nhisExpCal.setTime(date);
        int year = nhisExpCal.get(Calendar.YEAR);
        int month = nhisExpCal.get(Calendar.MONTH);
        int day = nhisExpCal.get(Calendar.DAY_OF_MONTH);
        nhisExpCal.setTime(nhisExpDate);
        assertEquals(year, nhisExpCal.get(Calendar.YEAR));
        assertEquals(month, nhisExpCal.get(Calendar.MONTH));
        assertEquals(day, nhisExpCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(phoneNumber.toString(), capturedPatient.getAttribute(
                phoneAttributeType).getValue());
        assertEquals(phoneType, ContactNumberType.valueOf(capturedPatient
                .getAttribute(phoneTypeAttributeType).getValue()));
        assertEquals(mediaType, MediaType.valueOf(capturedPatient.getAttribute(
                mediaTypeAttributeType).getValue()));
        assertEquals(language, capturedPatient.getAttribute(
                languageAttributeType).getValue());
        assertEquals(howLearned, HowLearned.valueOf(capturedPatient
                .getAttribute(howLearnedAttributeType).getValue()));
        assertEquals(reason, InterestReason.valueOf(capturedPatient
                .getAttribute(interestReasonAttributeType).getValue()));
        assertEquals(dayOfWeek, DayOfWeek.valueOf(capturedPatient.getAttribute(
                deliveryDayAttributeType).getValue()));
        Calendar timeOfDayCal = Calendar.getInstance();
        timeOfDayCal.setTime(date);
        int hour = timeOfDayCal.get(Calendar.HOUR_OF_DAY);
        int min = timeOfDayCal.get(Calendar.MINUTE);
        Date timeOfDayDate = (new SimpleDateFormat(
                MotechConstants.TIME_FORMAT_DELIVERY_TIME))
                .parse(capturedPatient.getAttribute(deliveryTimeAttributeType)
                        .getValue());
        timeOfDayCal.setTime(timeOfDayDate);
        assertEquals(hour, timeOfDayCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(min, timeOfDayCal.get(Calendar.MINUTE));

        MessageProgramEnrollment enrollment1 = enrollment1Cap.getValue();
        assertEquals(child.getPatientId(), enrollment1.getPersonId());
        assertEquals(pregnancyProgramName, enrollment1.getProgram());
        assertNotNull("Enrollment start date should not be null", enrollment1
                .getStartDate());
        assertNull("Enrollment end date should be null", enrollment1
                .getEndDate());

        MessageProgramEnrollment enrollment2 = enrollment2Cap.getValue();
        assertEquals(child.getPatientId(), enrollment2.getPersonId());
        assertEquals(careProgramName, enrollment2.getProgram());
        assertNotNull("Enrollment start date should not be null", enrollment2
                .getStartDate());
        assertNull("Enrollment end date should be null", enrollment2
                .getEndDate());

        Encounter registration = registrationEncounterCap.getValue();
        assertEquals(0, registration.getAllObs(true).size());
        assertNotNull("Registation date is null", registration
                .getEncounterDatetime());
    }

    @Test
    public void registerPerson() throws ParseException {
        regBean = regBeanImpl;
        Integer motechId = 123456;
        String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
        String nhis = "456DEF";
        String address = "Address";
        String phoneNumber = "2075555555";
        String language = "Language";
        Date date = new Date();
        Boolean birthDateEst = true, insured = true, dueDateConfirmed = true, enroll = true, consent = true;
        Gender gender = Gender.FEMALE;
        ContactNumberType phoneType = ContactNumberType.PERSONAL;
        MediaType mediaType = MediaType.TEXT;
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        InterestReason reason = InterestReason.FAMILY_FRIEND_PREGNANT;
        HowLearned howLearned = HowLearned.FRIEND;
        Integer messagesStartWeek = 5;

        String pregnancyProgramName = "Weekly Info Pregnancy Message Program";
        String careProgramName = "Expected Care Message Program";

        Patient patient = new Patient(2);
        Location ghanaLocation = new Location(1);
        Community community = new Community();
        Facility facility = new Facility();
        Location facilityLocation = new Location(2);
        facilityLocation
                .setCountyDistrict(MotechConstants.LOCATION_KASSENA_NANKANA_WEST);
        facility.setLocation(facilityLocation);
        community.setFacility(facility);

        Capture<Patient> patientCap = new Capture<Patient>();
        Capture<MessageProgramEnrollment> enrollment1Cap = new Capture<MessageProgramEnrollment>();
        Capture<MessageProgramEnrollment> enrollment2Cap = new Capture<MessageProgramEnrollment>();
        Capture<Obs> refDateObsCap = new Capture<Obs>();
        Capture<Encounter> registrationEncounterCap = new Capture<Encounter>();

        expect(
                contextService.getPatientService()).
                andReturn(patientService).atLeastOnce();
        expect(
                contextService.getMotechService()).andReturn(motechService)
                .atLeastOnce();
        expect(
                contextService.getConceptService()).andReturn(conceptService)
                .atLeastOnce();
        expect(
                contextService.getEncounterService()).andReturn(encounterService)
                .atLeastOnce();

        expect(
                patientService
                        .getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID))
                .andReturn(motechIdType).atLeastOnce();

        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_INSURED))
                .andReturn(insuredAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER))
                .andReturn(nhisAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE))
                .andReturn(nhisExpirationType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER))
                .andReturn(phoneAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE))
                .andReturn(phoneTypeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE))
                .andReturn(mediaTypeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE))
                .andReturn(languageAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY))
                .andReturn(deliveryDayAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME))
                .andReturn(deliveryTimeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED))
                .andReturn(howLearnedAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_INTEREST_REASON))
                .andReturn(interestReasonAttributeType);

        expect(patientService.savePatient(capture(patientCap))).andReturn(
                patient);

        expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
                .andReturn(ghanaLocation).atLeastOnce();
        expect(
                conceptService
                        .getConcept(MotechConstants.CONCEPT_ENROLLMENT_REFERENCE_DATE))
                .andReturn(refDateConcept);
        expect(obsService.saveObs(capture(refDateObsCap), (String) anyObject()))
                .andReturn(new Obs());

        expect(
                motechService.getActiveMessageProgramEnrollments(patient
                        .getPatientId(), pregnancyProgramName, null, null,
                        null, null)).andReturn(
                new ArrayList<MessageProgramEnrollment>());
        expect(
                motechService
                        .saveMessageProgramEnrollment(capture(enrollment1Cap)))
                .andReturn(new MessageProgramEnrollment());
        expect(
                motechService.getActiveMessageProgramEnrollments(patient
                        .getPatientId(), careProgramName, null, null, null,
                        null)).andReturn(
                new ArrayList<MessageProgramEnrollment>());
        expect(
                motechService
                        .saveMessageProgramEnrollment(capture(enrollment2Cap)))
                .andReturn(new MessageProgramEnrollment());
        expect(
                encounterService
                        .getEncounterType(MotechConstants.ENCOUNTER_TYPE_PATIENTREGVISIT))
                .andReturn(registrationVisitType);
        expect(
                encounterService
                        .saveEncounter(capture(registrationEncounterCap)))
                .andReturn(new Encounter());

        replay(contextService, patientService, motechService, personService,
                locationService, userService, encounterService, obsService,
                conceptService, idService);

        regBean.registerPatient(RegistrationMode.USE_PREPRINTED_ID, motechId,
                RegistrantType.OTHER, firstName, middleName, lastName,
                prefName, date, birthDateEst, gender, insured, nhis, date,
                null, community, address, phoneNumber, date, dueDateConfirmed,
                enroll, consent, phoneType, mediaType, language, dayOfWeek,
                date, reason, howLearned, messagesStartWeek);

        verify(contextService, patientService, motechService, personService,
                locationService, userService, encounterService, obsService,
                conceptService, idService);

        Patient capturedPatient = patientCap.getValue();
        assertEquals(motechId.toString(), capturedPatient.getPatientIdentifier(
                motechIdType).getIdentifier());
        assertEquals(prefName, capturedPatient.getGivenName());
        assertEquals(lastName, capturedPatient.getFamilyName());
        assertEquals(middleName, capturedPatient.getMiddleName());
        Iterator<PersonName> names = capturedPatient.getNames().iterator();
        while (names.hasNext()) {
            PersonName personName = names.next();
            if (personName.isPreferred()) {
                assertEquals(prefName, personName.getGivenName());
                assertEquals(lastName, personName.getFamilyName());
                assertEquals(middleName, personName.getMiddleName());
            } else {
                assertEquals(firstName, personName.getGivenName());
                assertEquals(lastName, personName.getFamilyName());
                assertEquals(middleName, personName.getMiddleName());
            }
        }
        assertEquals(date, capturedPatient.getBirthdate());
        assertEquals(birthDateEst, capturedPatient.getBirthdateEstimated());
        assertEquals(GenderTypeConverter.toOpenMRSString(Gender.FEMALE),
                capturedPatient.getGender());
        assertEquals(1, community.getResidents().size());
        assertEquals(address, capturedPatient.getPersonAddress().getAddress1());
        assertEquals(phoneNumber.toString(), capturedPatient.getAttribute(
                phoneAttributeType).getValue());
        assertEquals(phoneType, ContactNumberType.valueOf(capturedPatient
                .getAttribute(phoneTypeAttributeType).getValue()));
        assertEquals(mediaType, MediaType.valueOf(capturedPatient.getAttribute(
                mediaTypeAttributeType).getValue()));
        assertEquals(language, capturedPatient.getAttribute(
                languageAttributeType).getValue());
        assertEquals(howLearned, HowLearned.valueOf(capturedPatient
                .getAttribute(howLearnedAttributeType).getValue()));
        assertEquals(reason, InterestReason.valueOf(capturedPatient
                .getAttribute(interestReasonAttributeType).getValue()));
        assertEquals(dayOfWeek, DayOfWeek.valueOf(capturedPatient.getAttribute(
                deliveryDayAttributeType).getValue()));
        Calendar timeOfDayCal = Calendar.getInstance();
        timeOfDayCal.setTime(date);
        int hour = timeOfDayCal.get(Calendar.HOUR_OF_DAY);
        int min = timeOfDayCal.get(Calendar.MINUTE);
        Date timeOfDayDate = (new SimpleDateFormat(
                MotechConstants.TIME_FORMAT_DELIVERY_TIME))
                .parse(capturedPatient.getAttribute(deliveryTimeAttributeType)
                        .getValue());
        timeOfDayCal.setTime(timeOfDayDate);
        assertEquals(hour, timeOfDayCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(min, timeOfDayCal.get(Calendar.MINUTE));

        MessageProgramEnrollment enrollment1 = enrollment1Cap.getValue();
        assertEquals(patient.getPatientId(), enrollment1.getPersonId());
        assertEquals(pregnancyProgramName, enrollment1.getProgram());
        assertNotNull("Enrollment start date should not be null", enrollment1
                .getStartDate());
        assertNull("Enrollment end date should be null", enrollment1
                .getEndDate());

        MessageProgramEnrollment enrollment2 = enrollment2Cap.getValue();
        assertEquals(patient.getPatientId(), enrollment2.getPersonId());
        assertEquals(careProgramName, enrollment2.getProgram());
        assertNotNull("Enrollment start date should not be null", enrollment2
                .getStartDate());
        assertNull("Enrollment end date should be null", enrollment2
                .getEndDate());

        Obs refDateObs = refDateObsCap.getValue();
        assertEquals(patient.getPatientId(), refDateObs.getPersonId());
        assertEquals(ghanaLocation, refDateObs.getLocation());
        assertEquals(refDateConcept, refDateObs.getConcept());
        assertNotNull("Enrollment reference date value is null", refDateObs
                .getValueDatetime());

        Encounter registration = registrationEncounterCap.getValue();
        assertEquals(0, registration.getAllObs(true).size());
        assertNotNull("Registation date is null", registration
                .getEncounterDatetime());
    }

    @Test
    public void editPatient() throws ParseException {
        regBean = regBeanImpl;
        Integer patientId = 1, mothersMotechId = 2;
        String phone = "2075551212";
        String nhis = "28";
        Date date = new Date();
        ContactNumberType phoneType = ContactNumberType.PERSONAL;
        Boolean stopEnrollment = true;

        User staff = new User(2);
        Patient patient = new Patient(patientId);
        Patient mother = new Patient(mothersMotechId);

        List<MessageProgramEnrollment> enrollments = new ArrayList<MessageProgramEnrollment>();
        MessageProgramEnrollment enrollment1 = new MessageProgramEnrollment();
        enrollments.add(enrollment1);
        MessageProgramEnrollment enrollment2 = new MessageProgramEnrollment();
        enrollments.add(enrollment2);

        List<Message> enrollment1Messages = new ArrayList<Message>();
        enrollment1Messages.add(new Message());

        Capture<Patient> patientCap = new Capture<Patient>();
        Capture<MessageProgramEnrollment> enrollment1Cap = new Capture<MessageProgramEnrollment>();
        Capture<MessageProgramEnrollment> enrollment2Cap = new Capture<MessageProgramEnrollment>();
        Capture<Message> enrollment1MessageCap = new Capture<Message>();

        expect(contextService.getMotechService()).andReturn(motechService)
                .atLeastOnce();
        expect(contextService.getConceptService()).andReturn(conceptService)
                .atLeastOnce();
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER))
                .andReturn(phoneAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE))
                .andReturn(phoneTypeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER))
                .andReturn(nhisAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE))
                .andReturn(nhisExpirationType);
        expect(patientService.savePatient(capture(patientCap))).andReturn(
                new Patient(patientId));

        expect(
                motechService.getActiveMessageProgramEnrollments(patientId,
                        null, null, null, null, null)).andReturn(enrollments);
        expect(
                motechService.getMessages(enrollment1,
                        MessageStatus.SHOULD_ATTEMPT)).andReturn(
                enrollment1Messages);
        expect(motechService.saveMessage(capture(enrollment1MessageCap)))
                .andReturn(new Message());
        expect(
                motechService.getMessages(enrollment2,
                        MessageStatus.SHOULD_ATTEMPT)).andReturn(
                new ArrayList<Message>());
        expect(
                motechService
                        .saveMessageProgramEnrollment(capture(enrollment1Cap)))
                .andReturn(new MessageProgramEnrollment());
        expect(
                motechService
                        .saveMessageProgramEnrollment(capture(enrollment2Cap)))
                .andReturn(new MessageProgramEnrollment());

        Concept pregnancyConcept = new Concept();
        Obs pregnancyObs = new Obs(1);
        List<Obs> pregnancies = Arrays.asList(pregnancyObs);

        expect(conceptService.getConcept(eq(MotechConstants.CONCEPT_PREGNANCY))).andReturn(pregnancyConcept);

        expect(conceptService.getConcept(eq(MotechConstants.CONCEPT_PREGNANCY_STATUS))).andReturn(pregnancyConcept);

        Concept question = new Concept(10);
        expect(conceptService.getConcept(eq(MotechConstants.CONCEPT_ESTIMATED_DATE_OF_CONFINEMENT))).andReturn(question).times(2);

        expect(motechService.getActivePregnancies(anyInt(), EasyMock.<Concept>anyObject(), EasyMock.<Concept>anyObject())).andReturn(pregnancies);

        expect(personService.getPerson(patientId)).andReturn(patient);

        Obs obs = new Obs();
        Encounter encounter = new Encounter(1);
        encounter.setPatient(patient);
        encounter.setEncounterDatetime(new Date());
        encounter.setLocation(new Location(1));
        obs.setEncounter(encounter);
        expect(obsService.getObservations(Arrays.asList((Person) patient), null, Arrays.asList(question),
                null, null, null, null, null, pregnancyObs.getId(), null, null, false)).andReturn(Arrays.asList(obs));

        expect(obsService.saveObs(EasyMock.<Obs>anyObject(), EasyMock.<String>anyObject())).andReturn(new Obs());

        expect(obsService.voidObs(EasyMock.<Obs>anyObject(), EasyMock.<String>anyObject())).andReturn(new Obs());

        expect(motechService.getActiveMessageProgramEnrollments(anyInt(), EasyMock.<String>anyObject(), anyInt(),
                anyLong(), anyLong(), anyInt())).andReturn(new ArrayList<MessageProgramEnrollment>());

        expect(relationshipService.saveOrUpdateMotherRelationship(mother, patient, false)).andReturn(null);

        replay(contextService, patientService, personService, motechService, conceptService, obsService, relationshipService);

        regBean.editPatient(staff, date, patient, mother, phone, phoneType, nhis, date,
                date, stopEnrollment);

        verify(contextService, patientService, personService, motechService, conceptService, obsService, relationshipService);

        Patient capturedPatient = patientCap.getValue();
        assertEquals(phone.toString(), capturedPatient.getAttribute(
                phoneAttributeType).getValue());
        assertEquals(phoneType.toString(), capturedPatient.getAttribute(
                phoneTypeAttributeType).getValue());
        assertEquals(nhis, capturedPatient.getAttribute(nhisAttributeType)
                .getValue());
        Date nhisExpDate = (new SimpleDateFormat(MotechConstants.DATE_FORMAT))
                .parse(capturedPatient.getAttribute(nhisExpirationType)
                        .getValue());
        Calendar nhisExpCal = Calendar.getInstance();
        nhisExpCal.setTime(date);
        int year = nhisExpCal.get(Calendar.YEAR);
        int month = nhisExpCal.get(Calendar.MONTH);
        int day = nhisExpCal.get(Calendar.DAY_OF_MONTH);
        nhisExpCal.setTime(nhisExpDate);
        assertEquals(year, nhisExpCal.get(Calendar.YEAR));
        assertEquals(month, nhisExpCal.get(Calendar.MONTH));
        assertEquals(day, nhisExpCal.get(Calendar.DAY_OF_MONTH));

        MessageProgramEnrollment enrollment1Captured = enrollment1Cap
                .getValue();
        assertNotNull("Enrollment 1 end date should be set",
                enrollment1Captured.getEndDate());

        Message enrollment1MessageCaptured = enrollment1MessageCap.getValue();
        assertEquals(MessageStatus.CANCELLED, enrollment1MessageCaptured
                .getAttemptStatus());

        MessageProgramEnrollment enrollment2Captured = enrollment1Cap
                .getValue();
        assertNotNull("Enrollment 2 end date should be set",
                enrollment2Captured.getEndDate());

    }

    @Test
    public void editPatientAll() throws ParseException {
        regBean = regBeanImpl;
        Integer patientId = 2;
        String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
        String nhis = "456DEF", address = "Address";
        String phoneNumber = "2075555555", language = "Language";
        Date date = new Date();
        Gender sex = Gender.FEMALE;
        Boolean birthDateEst = true, enroll = false, consent = true, insured = true;
        ContactNumberType phoneType = ContactNumberType.PERSONAL;
        MediaType mediaType = MediaType.TEXT;
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
        Date dueDate = null;

        Patient patient = new Patient(patientId);
        Patient currentMother = new Patient(3);
        Patient newMother = new Patient(4);
        Community oldCommunity = new Community();
        oldCommunity.setCommunityId(1);
        oldCommunity.getResidents().add(patient);
        Community community = new Community();
        community.setCommunityId(2);

        List<Relationship> relationships = new ArrayList<Relationship>();
        Relationship relation = new Relationship(currentMother, patient,
                parentChildRelationshipType);
        relationships.add(relation);

        Capture<Patient> patientCap = new Capture<Patient>();

        expect(contextService.getMotechService()).andReturn(motechService)
                .atLeastOnce();
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_INSURED))
                .andReturn(insuredAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER))
                .andReturn(nhisAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE))
                .andReturn(nhisExpirationType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER))
                .andReturn(phoneAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE))
                .andReturn(phoneTypeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE))
                .andReturn(mediaTypeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE))
                .andReturn(languageAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY))
                .andReturn(deliveryDayAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME))
                .andReturn(deliveryTimeAttributeType);

        expect(motechService.getCommunityByPatient(patient)).andReturn(
                oldCommunity);
        expect(motechService.getCommunityByPatient(patient)).andReturn(null);
        expect(
                motechService.getActiveMessageProgramEnrollments(patientId,
                        null, null, null, null, null)).andReturn(
                new ArrayList<MessageProgramEnrollment>());

        expect(patientService.savePatient(capture(patientCap))).andReturn(
                patient);

        replay(contextService, patientService, motechService, personService,
                locationService, userService, encounterService, obsService,
                conceptService);

        regBean.editPatient(patient, firstName, middleName, lastName, prefName,
                date, birthDateEst, sex, insured, nhis, date, newMother,
                community, address, phoneNumber, dueDate, enroll, consent,
                phoneType, mediaType, language, dayOfWeek, date);

        verify(contextService, patientService, motechService, personService,
                locationService, userService, encounterService, obsService,
                conceptService);

        Patient capturedPatient = patientCap.getValue();
        assertEquals(date, capturedPatient.getBirthdate());
        assertEquals(birthDateEst, capturedPatient.getBirthdateEstimated());
        assertEquals(sex, GenderTypeConverter.valueOfOpenMRS(capturedPatient
                .getGender()));
        assertEquals(prefName, capturedPatient.getGivenName());
        assertEquals(lastName, capturedPatient.getFamilyName());
        assertEquals(middleName, capturedPatient.getMiddleName());
        assertEquals(2, capturedPatient.getNames().size());
        for (PersonName name : capturedPatient.getNames()) {
            if (!name.isPreferred()) {
                assertEquals(firstName, name.getGivenName());
                assertEquals(lastName, name.getFamilyName());
                assertEquals(middleName, name.getMiddleName());
            }
        }
        assertEquals(0, oldCommunity.getResidents().size());
        assertEquals(1, community.getResidents().size());
        assertEquals(capturedPatient, community.getResidents().iterator()
                .next());
        assertEquals(address, capturedPatient.getPersonAddress().getAddress1());
        assertEquals(insured, Boolean.valueOf(capturedPatient.getAttribute(
                insuredAttributeType).getValue()));
        assertEquals(nhis, capturedPatient.getAttribute(nhisAttributeType)
                .getValue());

        Date nhisExpDate = (new SimpleDateFormat(MotechConstants.DATE_FORMAT))
                .parse(capturedPatient.getAttribute(nhisExpirationType)
                        .getValue());
        Calendar nhisExpCal = Calendar.getInstance();
        nhisExpCal.setTime(date);
        int year = nhisExpCal.get(Calendar.YEAR);
        int month = nhisExpCal.get(Calendar.MONTH);
        int day = nhisExpCal.get(Calendar.DAY_OF_MONTH);
        nhisExpCal.setTime(nhisExpDate);
        assertEquals(year, nhisExpCal.get(Calendar.YEAR));
        assertEquals(month, nhisExpCal.get(Calendar.MONTH));
        assertEquals(day, nhisExpCal.get(Calendar.DAY_OF_MONTH));

        assertEquals(phoneNumber, capturedPatient.getAttribute(
                phoneAttributeType).getValue());
        assertEquals(phoneType, ContactNumberType.valueOf(capturedPatient
                .getAttribute(phoneTypeAttributeType).getValue()));
        assertEquals(mediaType, MediaType.valueOf(capturedPatient.getAttribute(
                mediaTypeAttributeType).getValue()));
        assertEquals(language, capturedPatient.getAttribute(
                languageAttributeType).getValue());

        assertEquals(dayOfWeek, DayOfWeek.valueOf(capturedPatient.getAttribute(
                deliveryDayAttributeType).getValue()));

        Date timeOfDayDate = (new SimpleDateFormat(
                MotechConstants.TIME_FORMAT_DELIVERY_TIME))
                .parse(capturedPatient.getAttribute(deliveryTimeAttributeType)
                        .getValue());
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(date);
        int hour = timeCal.get(Calendar.HOUR_OF_DAY);
        int minute = timeCal.get(Calendar.MINUTE);
        timeCal.setTime(timeOfDayDate);
        assertEquals(hour, timeCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(minute, timeCal.get(Calendar.MINUTE));
    }

    @Test
    @Ignore
    public void registerPregnancy() throws ParseException {
        regBean = regBeanImpl;
        Integer patientId = 2;
        Date date = new Date();
        Boolean dueDateConfirmed = true, enroll = true, consent = true;
        String phoneNumber = "2075555555";
        String language = "Language";
        ContactNumberType phoneType = ContactNumberType.PERSONAL;
        MediaType mediaType = MediaType.TEXT;
        HowLearned howLearned = HowLearned.FRIEND;
        InterestReason reason = InterestReason.CURRENTLY_PREGNANT;
        DayOfWeek dayOfWeek = DayOfWeek.MONDAY;

        Patient patient = new Patient(patientId);
        Location ghanaLocation = new Location(1);

        Capture<Encounter> pregnancyEncounterCap = new Capture<Encounter>();
        Capture<Obs> pregnancyObsCap = new Capture<Obs>();

        expect(contextService.getPersonService()).andReturn(personService)
                .atLeastOnce();
        expect(contextService.getMotechService()).andReturn(motechService)
                .atLeastOnce();
        expect(contextService.getLocationService()).andReturn(locationService)
                .atLeastOnce();
        expect(contextService.getEncounterService())
                .andReturn(encounterService).atLeastOnce();
        expect(contextService.getObsService()).andReturn(obsService);
        expect(contextService.getConceptService()).andReturn(conceptService)
                .atLeastOnce();
        expect(contextService.getPatientService()).andReturn(patientService);

        expect(contextService.getAuthenticatedUser()).andReturn(new User());
        expect(encounterService.getEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGDELVISIT)).andReturn(pregnancyDelVisitType);
        expect(encounterService.saveEncounter(capture(pregnancyEncounterCap))).andReturn(new Encounter());
        expect(
                motechService.getActivePregnancies(patientId, pregConcept,
                        pregStatusConcept)).andReturn(new ArrayList<Obs>());

        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER))
                .andReturn(phoneAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE))
                .andReturn(phoneTypeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE))
                .andReturn(mediaTypeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE))
                .andReturn(languageAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY))
                .andReturn(deliveryDayAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME))
                .andReturn(deliveryTimeAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED))
                .andReturn(howLearnedAttributeType);
        expect(
                personService
                        .getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_INTEREST_REASON))
                .andReturn(interestReasonAttributeType);
        expect(patientService.savePatient((Patient) anyObject())).andReturn(
                new Patient());

        expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
                .andReturn(ghanaLocation);
        expect(conceptService.getConcept(MotechConstants.CONCEPT_PREGNANCY))
                .andReturn(pregConcept).atLeastOnce();
        expect(
                conceptService
                        .getConcept(MotechConstants.CONCEPT_PREGNANCY_STATUS))
                .andReturn(pregStatusConcept).atLeastOnce();
        expect(
                conceptService
                        .getConcept(MotechConstants.CONCEPT_ESTIMATED_DATE_OF_CONFINEMENT))
                .andReturn(dateConfConcept);
        expect(
                conceptService
                        .getConcept(MotechConstants.CONCEPT_DATE_OF_CONFINEMENT_CONFIRMED))
                .andReturn(dateConfConfirmedConcept);
        expect(
                obsService.saveObs(capture(pregnancyObsCap),
                        (String) anyObject())).andReturn(new Obs());
        expect(motechService.getCommunityByPatient(patient)).andReturn(null);

        replay(contextService, patientService, motechService, personService,
                locationService, encounterService, obsService, conceptService,
                userService);

        regBean.registerPregnancy(patient, date, dueDateConfirmed, enroll,
                consent, phoneNumber, phoneType, mediaType, language,
                dayOfWeek, date, reason, howLearned);

        verify(contextService, patientService, motechService, personService,
                locationService, encounterService, obsService, conceptService,
                userService);

        assertEquals(phoneNumber, patient.getAttribute(phoneAttributeType)
                .getValue());
        assertEquals(phoneType, ContactNumberType.valueOf(patient.getAttribute(
                phoneTypeAttributeType).getValue()));
        assertEquals(mediaType, MediaType.valueOf(patient.getAttribute(
                mediaTypeAttributeType).getValue()));
        assertEquals(language, patient.getAttribute(languageAttributeType)
                .getValue());
        assertEquals(dayOfWeek, DayOfWeek.valueOf(patient.getAttribute(
                deliveryDayAttributeType).getValue()));

        Date timeOfDayDate = (new SimpleDateFormat(
                MotechConstants.TIME_FORMAT_DELIVERY_TIME)).parse(patient
                .getAttribute(deliveryTimeAttributeType).getValue());
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(date);
        int hour = timeCal.get(Calendar.HOUR_OF_DAY);
        int minute = timeCal.get(Calendar.MINUTE);
        timeCal.setTime(timeOfDayDate);
        assertEquals(hour, timeCal.get(Calendar.HOUR_OF_DAY));
        assertEquals(minute, timeCal.get(Calendar.MINUTE));

        Encounter pregnancyEncounter = pregnancyEncounterCap.getValue();
        assertNotNull(pregnancyEncounter.getEncounterDatetime());
        assertEquals(ghanaLocation, pregnancyEncounter.getLocation());
        assertEquals(patient, pregnancyEncounter.getPatient());
        assertEquals(pregnancyDelVisitType, pregnancyEncounter
                .getEncounterType());

        Obs pregnancyObs = pregnancyObsCap.getValue();
        assertNotNull(pregnancyObs.getObsDatetime());
        assertEquals(patientId, pregnancyObs.getPerson().getPersonId());
        assertEquals(ghanaLocation, pregnancyObs.getLocation());
        assertEquals(pregConcept, pregnancyObs.getConcept());

        Set<Obs> pregnancyObsMembers = pregnancyObs.getGroupMembers();
        assertEquals(3, pregnancyObsMembers.size());

        boolean containsPregnancyStatusObs = false;
        boolean containsDueDateObs = false;
        boolean containsDueDateConfirmedObs = false;
        Iterator<Obs> obsIterator = pregnancyObsMembers.iterator();
        while (obsIterator.hasNext()) {
            Obs memberObs = obsIterator.next();
            assertEquals(patientId, memberObs.getPerson().getPersonId());
            assertEquals(ghanaLocation, memberObs.getLocation());
            if (pregStatusConcept.equals(memberObs.getConcept())) {
                containsPregnancyStatusObs = true;
                assertEquals(Boolean.TRUE, memberObs.getValueAsBoolean());
            } else if (dateConfConcept.equals(memberObs.getConcept())) {
                containsDueDateObs = true;
                assertEquals(date, memberObs.getValueDatetime());
            } else if (dateConfConfirmedConcept.equals(memberObs.getConcept())) {
                containsDueDateConfirmedObs = true;
                assertEquals(dueDateConfirmed, memberObs.getValueAsBoolean());
            }
        }
        assertTrue("Pregnancy Status Obs missing", containsPregnancyStatusObs);
        assertTrue("Due Date Obs missing", containsDueDateObs);
        assertTrue("Due Date Confirmed Obs missing",
                containsDueDateConfirmedObs);
    }

    @Ignore("Troubled phones saving being disabled for now")
    @Test
    public void setMessageStatusSuccessMessageFoundNotTroubled() {
        String messageId = "12345678-1234-1234-1234-123456789012";
        Boolean success = true;

        Integer recipientId = 2;
        String phoneNumber = "1234567890";
        Person recipient = new Person();
        recipient.addAttribute(new PersonAttribute(phoneAttributeType,
                phoneNumber));
        TroubledPhone troubledPhone = null;
        Message message = new Message();
        ScheduledMessage scheduledMessage = new ScheduledMessage();
        scheduledMessage.setRecipientId(recipientId);
        message.setSchedule(scheduledMessage);

        Capture<Message> messageCap = new Capture<Message>();

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(contextService.getPersonService()).andReturn(personService)
                .atLeastOnce();
        expect(motechService.getMessage(messageId)).andReturn(message);
        expect(personService.getPerson(recipientId)).andReturn(recipient);
        expect(motechService.getTroubledPhone(phoneNumber)).andReturn(
                troubledPhone);
        expect(motechService.saveMessage(capture(messageCap))).andReturn(
                message);

        replay(contextService, motechService, personService);

        regBean.setMessageStatus(messageId, success);

        verify(contextService, motechService, personService);

        Message capturedMessage = messageCap.getValue();
        assertEquals(MessageStatus.DELIVERED, capturedMessage
                .getAttemptStatus());
    }

    @Ignore
    @Test
    public void setMessageStatusSuccessMessageFoundTroubled() {
        String messageId = "12345678-1234-1234-1234-123456789012";
        Boolean success = true;

        Integer recipientId = 2;
        String phoneNumber = "1234567890";
        Person recipient = new Person();
        recipient.addAttribute(new PersonAttribute(phoneAttributeType,
                phoneNumber));
        TroubledPhone troubledPhone = new TroubledPhone();
        Message message = new Message();
        ScheduledMessage scheduledMessage = new ScheduledMessage();
        scheduledMessage.setRecipientId(recipientId);
        message.setSchedule(scheduledMessage);

        Capture<Message> messageCap = new Capture<Message>();

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(contextService.getPersonService()).andReturn(personService)
                .atLeastOnce();
        expect(motechService.getMessage(messageId)).andReturn(message);
        expect(personService.getPerson(recipientId)).andReturn(recipient);
        expect(motechService.getTroubledPhone(phoneNumber)).andReturn(
                troubledPhone);
        motechService.removeTroubledPhone(phoneNumber);
        expect(motechService.saveMessage(capture(messageCap))).andReturn(
                message);

        replay(contextService, motechService, personService);

        regBean.setMessageStatus(messageId, success);

        verify(contextService, motechService, personService);

        Message capturedMessage = messageCap.getValue();
        assertEquals(MessageStatus.DELIVERED, capturedMessage
                .getAttemptStatus());
    }

    @Ignore
    @Test
    public void setMessageStatusFailureMessageFoundNotTroubled() {
        String messageId = "12345678-1234-1234-1234-123456789012";
        Boolean success = false;

        Integer recipientId = 2;
        String phoneNumber = "1234567890";
        Person recipient = new Person();
        recipient.addAttribute(new PersonAttribute(phoneAttributeType,
                phoneNumber));
        TroubledPhone troubledPhone = null;
        Message message = new Message();
        ScheduledMessage scheduledMessage = new ScheduledMessage();
        scheduledMessage.setRecipientId(recipientId);
        message.setSchedule(scheduledMessage);

        Capture<Message> messageCap = new Capture<Message>();

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(contextService.getPersonService()).andReturn(personService)
                .atLeastOnce();
        expect(motechService.getMessage(messageId)).andReturn(message);
        expect(personService.getPerson(recipientId)).andReturn(recipient);
        expect(motechService.getTroubledPhone(phoneNumber)).andReturn(
                troubledPhone);
        motechService.addTroubledPhone(phoneNumber);
        expect(motechService.saveMessage(capture(messageCap))).andReturn(
                message);

        replay(contextService, motechService, personService);

        regBean.setMessageStatus(messageId, success);

        verify(contextService, motechService, personService);

        Message capturedMessage = messageCap.getValue();
        assertEquals(MessageStatus.ATTEMPT_FAIL, capturedMessage
                .getAttemptStatus());
    }

    @Ignore
    @Test
    public void setMessageStatusFailureMessageFoundTroubled() {
        String messageId = "12345678-1234-1234-1234-123456789012";
        Boolean success = false;

        Integer recipientId = 2;
        String phoneNumber = "1234567890";
        Person recipient = new Person();
        recipient.addAttribute(new PersonAttribute(phoneAttributeType,
                phoneNumber));
        Integer previousFailures = 1;
        TroubledPhone troubledPhone = new TroubledPhone();
        troubledPhone.setSendFailures(previousFailures);
        Message message = new Message();
        ScheduledMessage scheduledMessage = new ScheduledMessage();
        scheduledMessage.setRecipientId(recipientId);
        message.setSchedule(scheduledMessage);

        Capture<TroubledPhone> troubledPhoneCap = new Capture<TroubledPhone>();
        Capture<Message> messageCap = new Capture<Message>();

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(contextService.getPersonService()).andReturn(personService)
                .atLeastOnce();
        expect(motechService.getMessage(messageId)).andReturn(message);
        expect(personService.getPerson(recipientId)).andReturn(recipient);
        expect(motechService.getTroubledPhone(phoneNumber)).andReturn(
                troubledPhone);
        motechService.saveTroubledPhone(capture(troubledPhoneCap));
        expect(motechService.saveMessage(capture(messageCap))).andReturn(
                message);

        replay(contextService, motechService, personService);

        regBean.setMessageStatus(messageId, success);

        verify(contextService, motechService, personService);

        Message capturedMessage = messageCap.getValue();
        assertEquals(MessageStatus.ATTEMPT_FAIL, capturedMessage
                .getAttemptStatus());

        Integer expectedFailures = 2;
        TroubledPhone capturedTroubledPhone = troubledPhoneCap.getValue();
        assertEquals(expectedFailures, capturedTroubledPhone.getSendFailures());
    }

    @Test
    public void setMessageStatusMessageNotFound() {
        regBean = regBeanImpl;
        String messageId = "12345678-1234-1234-1234-123456789012";
        Boolean success = true;

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getMessage(messageId)).andReturn(null);

        replay(contextService, motechService, personService);

        try {
            regBean.setMessageStatus(messageId, success);
            fail("Expected org.motechproject.server.messaging.MessageNotFoundException: none thrown");
        } catch (MessageNotFoundException e) {

        } catch (Exception e) {
            fail("Expected org.motechproject.server.messaging.MessageNotFoundException: other thrown");
        }

        verify(contextService, motechService, personService);
    }

}
