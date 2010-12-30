/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.GeneralOutpatientEncounter;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.IdBean;
import org.motechproject.server.svc.MessageBean;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HIVResult;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.util.OpenmrsConstants;

public class RegistrarBeanTest extends TestCase {

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
	OpenmrsBean openmrsBean;
	MessageBean messageBean;
	IdBean idBean;

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
	EncounterType patientHistoryVisitType;
	EncounterType cwcVisitType;
	EncounterType ttVisitType;
	EncounterType ancRegistrationType;
	EncounterType cwcRegistrationType;
	Concept immunizationConcept;
	Concept tetanusConcept;
	Concept iptConcept;
	Concept pregConcept;
	Concept pregStatusConcept;
	Concept dateConfConcept;
	Concept dateConfConfirmedConcept;
	Concept refDateConcept;
	Concept iptiConcept;
	Concept opvDoseConcept;
	Concept pentaDoseConcept;
	Concept csmConcept;
	Concept serialNumberConcept;
	Concept newCaseConcept;
	Concept referredConcept;
	Concept diagnosisConcept;
	Concept secondDiagnosisConcept;
	Concept bcgConcept;
	Concept measlesConcept;
	Concept yellowFeverConcept;
	Concept vitaminAConcept;
	Concept insuredConcept;
	Concept positiveConcept;
	Concept negativeConcept;
	Concept malariaRDTConcept;
	Concept actTreatmentConcept;
	Concept commentsConcept;
	Concept cwcLocationConcept;
	Concept houseConcept;
	Concept communityConcept;
	Concept dewormerConcept;
	Concept weightConcept;
	Concept muacConcept;
	Concept heightConcept;
	Concept maleInvolvedConcept;
	Concept visitNumberConcept;
	Concept ancpncLocationConcept;
	Concept temperatureConcept;
	Concept respirationConcept;
	Concept cordConditionConcept;
	Concept babyConditionConcept;
	Concept lochiaColourConcept;
	Concept lochiaExcessConcept;
	Concept lochiaOdourConcept;
	Concept fundalHeightConcept;
	Concept ancRegNumConcept;
	Concept gravidaConcept;
	Concept parityConcept;
	Concept cwcRegNumConcept;
	Concept bpSystolicConcept;
	Concept bpDiastolicConcept;
	Concept iptReactionConcept;
	Concept reactiveConcept;
	Concept nonReactiveConcept;
	Concept itnConcept;
	Concept fetalHeartRateConcept;
	Concept urineProteinTestConcept;
	Concept urineGlucoseTestConcept;
	Concept traceConcept;
	Concept hemoglobinConcept;
	Concept vdrlConcept;
	Concept vdrlTreatmentConcept;
	Concept pmtctConcept;
	Concept preTestCounselConcept;
	Concept hivTestResultConcept;
	Concept postTestCounselConcept;
	Concept pmtctTreatmentConcept;
	Concept nextANCDateConcept;

	RelationshipType parentChildRelationshipType;

	@Override
	protected void setUp() throws Exception {
		contextService = createMock(ContextService.class);

		locationService = createMock(LocationService.class);
		personService = createMock(PersonService.class);
		userService = createMock(UserService.class);
		patientService = createMock(PatientService.class);
		encounterService = createMock(EncounterService.class);
		obsService = createMock(ObsService.class);
		conceptService = createMock(ConceptService.class);
		motechService = createMock(MotechService.class);
		openmrsBean = createMock(OpenmrsBean.class);
		messageBean = createMock(MessageBean.class);
		idBean = createMock(IdBean.class);

		ghanaLocation = new Location(1);
		ghanaLocation.setName(MotechConstants.LOCATION_GHANA);

		motechIdType = new PatientIdentifierType(1);
		motechIdType.setName(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);

		staffIdType = new PatientIdentifierType(2);
		staffIdType.setName(MotechConstants.PATIENT_IDENTIFIER_STAFF_ID);

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

		patientHistoryVisitType = new EncounterType(9);
		patientHistoryVisitType
				.setName(MotechConstants.ENCOUNTER_TYPE_PATIENTHISTORY);

		cwcVisitType = new EncounterType(10);
		cwcVisitType.setName(MotechConstants.ENCOUNTER_TYPE_CWCVISIT);

		ttVisitType = new EncounterType(11);
		ttVisitType.setName(MotechConstants.ENCOUNTER_TYPE_TTVISIT);

		ancRegistrationType = new EncounterType(12);
		ancRegistrationType.setName(MotechConstants.ENCOUNTER_TYPE_ANCREGVISIT);

		cwcRegistrationType = new EncounterType(13);
		cwcRegistrationType.setName(MotechConstants.ENCOUNTER_TYPE_CWCREGVISIT);

		immunizationConcept = new Concept(6);
		tetanusConcept = new Concept(7);
		iptConcept = new Concept(8);
		pregStatusConcept = new Concept(18);
		dateConfConcept = new Concept(19);
		dateConfConfirmedConcept = new Concept(23);
		pregConcept = new Concept(24);
		refDateConcept = new Concept(25);
		iptiConcept = new Concept(29);
		opvDoseConcept = new Concept(30);
		pentaDoseConcept = new Concept(31);
		csmConcept = new Concept(32);
		serialNumberConcept = new Concept(35);
		newCaseConcept = new Concept(36);
		referredConcept = new Concept(37);
		diagnosisConcept = new Concept(38);
		secondDiagnosisConcept = new Concept(39);
		bcgConcept = new Concept(45);
		measlesConcept = new Concept(46);
		yellowFeverConcept = new Concept(47);
		vitaminAConcept = new Concept(48);
		insuredConcept = new Concept(49);
		positiveConcept = new Concept(50);
		negativeConcept = new Concept(51);
		malariaRDTConcept = new Concept(52);
		actTreatmentConcept = new Concept(53);
		commentsConcept = new Concept(54);
		cwcLocationConcept = new Concept(55);
		houseConcept = new Concept(56);
		communityConcept = new Concept(57);
		dewormerConcept = new Concept(58);
		weightConcept = new Concept(59);
		muacConcept = new Concept(60);
		heightConcept = new Concept(61);
		maleInvolvedConcept = new Concept(62);
		visitNumberConcept = new Concept(63);
		ancpncLocationConcept = new Concept(64);
		temperatureConcept = new Concept(65);
		respirationConcept = new Concept(66);
		cordConditionConcept = new Concept(67);
		babyConditionConcept = new Concept(68);
		lochiaColourConcept = new Concept(69);
		lochiaExcessConcept = new Concept(70);
		lochiaOdourConcept = new Concept(71);
		fundalHeightConcept = new Concept(72);
		ancRegNumConcept = new Concept(73);
		gravidaConcept = new Concept(74);
		parityConcept = new Concept(75);
		cwcRegNumConcept = new Concept(76);
		bpSystolicConcept = new Concept(77);
		bpDiastolicConcept = new Concept(78);
		iptReactionConcept = new Concept(79);
		reactiveConcept = new Concept(80);
		nonReactiveConcept = new Concept(81);
		itnConcept = new Concept(82);
		fetalHeartRateConcept = new Concept(83);
		urineProteinTestConcept = new Concept(84);
		urineGlucoseTestConcept = new Concept(85);
		traceConcept = new Concept(86);
		hemoglobinConcept = new Concept(87);
		vdrlConcept = new Concept(88);
		vdrlTreatmentConcept = new Concept(89);
		pmtctConcept = new Concept(90);
		preTestCounselConcept = new Concept(91);
		hivTestResultConcept = new Concept(92);
		postTestCounselConcept = new Concept(93);
		pmtctTreatmentConcept = new Concept(94);
		nextANCDateConcept = new Concept(95);

		parentChildRelationshipType = new RelationshipType(1);
		parentChildRelationshipType.setaIsToB("Parent");
		parentChildRelationshipType.setbIsToA("Child");

		RegistrarBeanImpl regBeanImpl = new RegistrarBeanImpl();
		regBeanImpl.setContextService(contextService);
		regBeanImpl.setOpenmrsBean(openmrsBean);
		regBeanImpl.setMessageBean(messageBean);
		regBeanImpl.setIdBean(idBean);

		regBean = regBeanImpl;
	}

	@Override
	protected void tearDown() throws Exception {
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
		openmrsBean = null;
		messageBean = null;
		idBean = null;
	}

	public void testRegisterStaff() {

		String firstName = "Jenny", lastName = "Jones", phone = "12078675309", staffType = "CHO";
		String generatedStaffId = "27";

		Capture<User> staffCap = new Capture<User>();
		Capture<String> passCap = new Capture<String>();

		expect(contextService.getUserService()).andReturn(userService)
				.atLeastOnce();

		expect(openmrsBean.getPhoneNumberAttributeType()).andReturn(
				phoneAttributeType);
		expect(userService.getRole(OpenmrsConstants.PROVIDER_ROLE)).andReturn(
				providerRole);

		expect(idBean.generateStaffId()).andReturn(generatedStaffId);

		expect(userService.saveUser(capture(staffCap), capture(passCap)))
				.andReturn(new User());

		replay(contextService, userService, personService, patientService,
				openmrsBean, messageBean, idBean);

		regBean.registerStaff(firstName, lastName, phone, staffType);

		verify(contextService, userService, personService, patientService,
				openmrsBean, messageBean, idBean);

		User staff = staffCap.getValue();
		String password = passCap.getValue();
		assertEquals(firstName, staff.getGivenName());
		assertEquals(lastName, staff.getFamilyName());
		assertEquals(phone, staff.getAttribute(phoneAttributeType).getValue());
		assertTrue(password.matches("[a-zA-Z0-9]{8}"));
		assertEquals(generatedStaffId, staff.getSystemId());
	}

	public void testRegisterPregnantMother() throws ParseException {
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
		expect(contextService.getEncounterService())
				.andReturn(encounterService).atLeastOnce();
		expect(contextService.getObsService()).andReturn(obsService);
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();

		expect(contextService.getAuthenticatedUser()).andReturn(new User());
		idBean.excludeMotechId((User) anyObject(), eq(motechId.toString()));
		expect(openmrsBean.getMotechPatientIdType()).andReturn(motechIdType)
				.atLeastOnce();
		expect(openmrsBean.getGhanaLocation()).andReturn(ghanaLocation)
				.times(2);

		expect(openmrsBean.getInsuredAttributeType()).andReturn(
				insuredAttributeType);
		expect(openmrsBean.getNHISNumberAttributeType()).andReturn(
				nhisAttributeType);
		expect(openmrsBean.getNHISExpirationDateAttributeType()).andReturn(
				nhisExpirationType);
		expect(openmrsBean.getPhoneNumberAttributeType()).andReturn(
				phoneAttributeType);
		expect(openmrsBean.getPhoneTypeAttributeType()).andReturn(
				phoneTypeAttributeType);
		expect(openmrsBean.getMediaTypeAttributeType()).andReturn(
				mediaTypeAttributeType);
		expect(openmrsBean.getLanguageAttributeType()).andReturn(
				languageAttributeType);
		expect(openmrsBean.getDeliveryDayAttributeType()).andReturn(
				deliveryDayAttributeType);
		expect(openmrsBean.getDeliveryTimeAttributeType()).andReturn(
				deliveryTimeAttributeType);
		expect(openmrsBean.getHowLearnedAttributeType()).andReturn(
				howLearnedAttributeType);
		expect(openmrsBean.getInterestReasonAttributeType()).andReturn(
				interestReasonAttributeType);

		expect(patientService.savePatient(capture(patientCap))).andReturn(
				patient);

		expect(openmrsBean.getPregnancyRegistrationVisitEncounterType())
				.andReturn(pregnancyRegVisitType);
		expect(encounterService.saveEncounter(capture(pregnancyEncounterCap)))
				.andReturn(new Encounter());

		expect(openmrsBean.getPregnancyConcept()).andReturn(pregConcept);
		expect(openmrsBean.getPregnancyStatusConcept()).andReturn(
				pregStatusConcept);
		expect(openmrsBean.getDueDateConcept()).andReturn(dateConfConcept);
		expect(openmrsBean.getDueDateConfirmedConcept()).andReturn(
				dateConfConfirmedConcept);
		expect(
				obsService.saveObs(capture(pregnancyObsCap),
						(String) anyObject())).andReturn(new Obs());

		expect(openmrsBean.getPatientRegistrationEncounterType()).andReturn(
				registrationVisitType);
		expect(
				encounterService
						.saveEncounter(capture(registrationEncounterCap)))
				.andReturn(new Encounter());

		replay(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService, openmrsBean, messageBean, idBean);

		regBean.registerPatient(RegistrationMode.USE_PREPRINTED_ID, motechId,
				RegistrantType.PREGNANT_MOTHER, firstName, middleName,
				lastName, prefName, date, birthDateEst, gender, insured, nhis,
				date, null, community, address, phoneNumber, date,
				dueDateConfirmed, enroll, consent, phoneType, mediaType,
				language, dayOfWeek, date, reason, howLearned, null);

		verify(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService, openmrsBean, messageBean, idBean);

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

	public void testRegisterChild() throws ParseException {
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

		Integer patientId = 1;
		Patient child = new Patient(patientId);
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
		Capture<Relationship> relationshipCap = new Capture<Relationship>();
		Capture<Encounter> registrationEncounterCap = new Capture<Encounter>();

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(contextService.getEncounterService())
				.andReturn(encounterService).atLeastOnce();

		expect(contextService.getAuthenticatedUser()).andReturn(new User());
		idBean.excludeMotechId((User) anyObject(), eq(motechId.toString()));
		expect(openmrsBean.getMotechPatientIdType()).andReturn(motechIdType)
				.atLeastOnce();
		expect(openmrsBean.getGhanaLocation()).andReturn(ghanaLocation)
				.times(2);

		expect(openmrsBean.getInsuredAttributeType()).andReturn(
				insuredAttributeType);
		expect(openmrsBean.getNHISNumberAttributeType()).andReturn(
				nhisAttributeType);
		expect(openmrsBean.getNHISExpirationDateAttributeType()).andReturn(
				nhisExpirationType);
		expect(openmrsBean.getPhoneNumberAttributeType()).andReturn(
				phoneAttributeType);
		expect(openmrsBean.getPhoneTypeAttributeType()).andReturn(
				phoneTypeAttributeType);
		expect(openmrsBean.getMediaTypeAttributeType()).andReturn(
				mediaTypeAttributeType);
		expect(openmrsBean.getLanguageAttributeType()).andReturn(
				languageAttributeType);
		expect(openmrsBean.getDeliveryDayAttributeType()).andReturn(
				deliveryDayAttributeType);
		expect(openmrsBean.getDeliveryTimeAttributeType()).andReturn(
				deliveryTimeAttributeType);
		expect(openmrsBean.getHowLearnedAttributeType()).andReturn(
				howLearnedAttributeType);
		expect(openmrsBean.getInterestReasonAttributeType()).andReturn(
				interestReasonAttributeType);

		expect(patientService.savePatient(capture(patientCap)))
				.andReturn(child);

		expect(
				personService
						.getRelationshipTypeByName(MotechConstants.RELATIONSHIP_TYPE_PARENT_CHILD))
				.andReturn(parentChildRelationshipType);
		expect(personService.saveRelationship(capture(relationshipCap)))
				.andReturn(new Relationship());

		messageBean.addMessageProgramEnrollment(patientId,
				pregnancyProgramName, null);
		messageBean.addMessageProgramEnrollment(patientId, careProgramName,
				null);

		expect(openmrsBean.getPatientRegistrationEncounterType()).andReturn(
				registrationVisitType);
		expect(
				encounterService
						.saveEncounter(capture(registrationEncounterCap)))
				.andReturn(new Encounter());

		replay(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService, openmrsBean, messageBean, idBean);

		regBean.registerPatient(RegistrationMode.USE_PREPRINTED_ID, motechId,
				RegistrantType.CHILD_UNDER_FIVE, firstName, middleName,
				lastName, prefName, birthDate, birthDateEst, gender, insured,
				nhis, date, mother, community, address, phoneNumber, date,
				dueDateConfirmed, enroll, consent, phoneType, mediaType,
				language, dayOfWeek, date, reason, howLearned, null);

		verify(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService, openmrsBean, messageBean, idBean);

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

		Relationship relationship = relationshipCap.getValue();
		assertEquals(parentChildRelationshipType, relationship
				.getRelationshipType());
		assertEquals(Integer.valueOf(2), relationship.getPersonA()
				.getPersonId());
		assertEquals(child.getPatientId(), relationship.getPersonB()
				.getPersonId());

		Encounter registration = registrationEncounterCap.getValue();
		assertEquals(0, registration.getAllObs(true).size());
		assertNotNull("Registation date is null", registration
				.getEncounterDatetime());
	}

	public void testRegisterPerson() throws ParseException {
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

		Integer patientId = 2;
		Patient patient = new Patient(patientId);
		Location ghanaLocation = new Location(1);
		Community community = new Community();
		Facility facility = new Facility();
		Location facilityLocation = new Location(2);
		facilityLocation
				.setCountyDistrict(MotechConstants.LOCATION_KASSENA_NANKANA_WEST);
		facility.setLocation(facilityLocation);
		community.setFacility(facility);
		Integer enrollmentObsId = 36;

		Capture<Patient> patientCap = new Capture<Patient>();
		Capture<Obs> refDateObsCap = new Capture<Obs>();
		Capture<Encounter> registrationEncounterCap = new Capture<Encounter>();

		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(contextService.getObsService()).andReturn(obsService);
		expect(contextService.getEncounterService())
				.andReturn(encounterService).atLeastOnce();

		expect(contextService.getAuthenticatedUser()).andReturn(new User());
		idBean.excludeMotechId((User) anyObject(), eq(motechId.toString()));
		expect(openmrsBean.getMotechPatientIdType()).andReturn(motechIdType)
				.atLeastOnce();
		expect(openmrsBean.getGhanaLocation()).andReturn(ghanaLocation)
				.times(3);

		expect(openmrsBean.getInsuredAttributeType()).andReturn(
				insuredAttributeType);
		expect(openmrsBean.getNHISNumberAttributeType()).andReturn(
				nhisAttributeType);
		expect(openmrsBean.getNHISExpirationDateAttributeType()).andReturn(
				nhisExpirationType);
		expect(openmrsBean.getPhoneNumberAttributeType()).andReturn(
				phoneAttributeType);
		expect(openmrsBean.getPhoneTypeAttributeType()).andReturn(
				phoneTypeAttributeType);
		expect(openmrsBean.getMediaTypeAttributeType()).andReturn(
				mediaTypeAttributeType);
		expect(openmrsBean.getLanguageAttributeType()).andReturn(
				languageAttributeType);
		expect(openmrsBean.getDeliveryDayAttributeType()).andReturn(
				deliveryDayAttributeType);
		expect(openmrsBean.getDeliveryTimeAttributeType()).andReturn(
				deliveryTimeAttributeType);
		expect(openmrsBean.getHowLearnedAttributeType()).andReturn(
				howLearnedAttributeType);
		expect(openmrsBean.getInterestReasonAttributeType()).andReturn(
				interestReasonAttributeType);

		expect(patientService.savePatient(capture(patientCap))).andReturn(
				patient);

		expect(openmrsBean.getEnrollmentReferenceDateConcept()).andReturn(
				refDateConcept);
		expect(obsService.saveObs(capture(refDateObsCap), (String) anyObject()))
				.andReturn(new Obs(enrollmentObsId));

		messageBean.addMessageProgramEnrollment(patientId,
				pregnancyProgramName, enrollmentObsId);
		messageBean.addMessageProgramEnrollment(patientId, careProgramName,
				null);

		expect(openmrsBean.getPatientRegistrationEncounterType()).andReturn(
				registrationVisitType);
		expect(
				encounterService
						.saveEncounter(capture(registrationEncounterCap)))
				.andReturn(new Encounter());

		replay(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService, openmrsBean, messageBean, idBean);

		regBean.registerPatient(RegistrationMode.USE_PREPRINTED_ID, motechId,
				RegistrantType.OTHER, firstName, middleName, lastName,
				prefName, date, birthDateEst, gender, insured, nhis, date,
				null, community, address, phoneNumber, date, dueDateConfirmed,
				enroll, consent, phoneType, mediaType, language, dayOfWeek,
				date, reason, howLearned, messagesStartWeek);

		verify(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService, openmrsBean, messageBean, idBean);

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

	public void testDemoRegisterPatient() throws ParseException {
		RegistrationMode registrationMode = RegistrationMode.AUTO_GENERATE_ID;
		Integer motechId = null;
		String generatedMotechId = "123456";
		String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", preferredName = "PrefName";
		String nhis = "456DEF", address = "Address", phoneNumber = "2075555555", language = "Language";
		Date date = new Date();
		Boolean estimatedBirthDate = true, insured = true, enroll = true, consent = true;
		Gender sex = Gender.FEMALE;
		ContactNumberType ownership = ContactNumberType.PERSONAL;
		MediaType format = MediaType.TEXT;
		DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.FAMILY_FRIEND_PREGNANT;
		HowLearned howLearned = HowLearned.FRIEND;

		Community community = new Community();
		User staff = new User(1);
		String program = "Demo Minute Message Program";
		Integer patientId = 5;
		Patient createdPatient = new Patient(patientId);

		Capture<Patient> patientCap = new Capture<Patient>();

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getAuthenticatedUser()).andReturn(staff);

		expect(idBean.generateMotechId()).andReturn(generatedMotechId);
		expect(openmrsBean.getMotechPatientIdType()).andReturn(motechIdType);
		expect(openmrsBean.getGhanaLocation()).andReturn(ghanaLocation);

		expect(openmrsBean.getPhoneNumberAttributeType()).andReturn(
				phoneAttributeType);
		expect(openmrsBean.getPhoneTypeAttributeType()).andReturn(
				phoneTypeAttributeType);
		expect(openmrsBean.getMediaTypeAttributeType()).andReturn(
				mediaTypeAttributeType);
		expect(openmrsBean.getLanguageAttributeType()).andReturn(
				languageAttributeType);
		expect(openmrsBean.getDeliveryDayAttributeType()).andReturn(
				deliveryDayAttributeType);
		expect(openmrsBean.getDeliveryTimeAttributeType()).andReturn(
				deliveryTimeAttributeType);
		expect(openmrsBean.getHowLearnedAttributeType()).andReturn(
				howLearnedAttributeType);
		expect(openmrsBean.getInterestReasonAttributeType()).andReturn(
				interestReasonAttributeType);
		expect(openmrsBean.getInsuredAttributeType()).andReturn(
				insuredAttributeType);
		expect(openmrsBean.getNHISNumberAttributeType()).andReturn(
				nhisAttributeType);
		expect(openmrsBean.getNHISExpirationDateAttributeType()).andReturn(
				nhisExpirationType);

		expect(patientService.savePatient(capture(patientCap))).andReturn(
				createdPatient);
		messageBean.addMessageProgramEnrollment(patientId, program, null);

		replay(contextService, patientService, idBean, openmrsBean, messageBean);

		regBean.demoRegisterPatient(registrationMode, motechId, firstName,
				middleName, lastName, preferredName, date, estimatedBirthDate,
				sex, insured, nhis, date, community, address, phoneNumber,
				enroll, consent, ownership, format, language, dayOfWeek, date,
				reason, howLearned);

		verify(contextService, patientService, idBean, openmrsBean, messageBean);

		Patient capturedPatient = patientCap.getValue();
		assertEquals(generatedMotechId, capturedPatient.getPatientIdentifier(
				motechIdType).getIdentifier());
		assertEquals(preferredName, capturedPatient.getGivenName());
		assertEquals(lastName, capturedPatient.getFamilyName());
		assertEquals(middleName, capturedPatient.getMiddleName());
		Iterator<PersonName> names = capturedPatient.getNames().iterator();
		while (names.hasNext()) {
			PersonName personName = names.next();
			if (personName.isPreferred()) {
				assertEquals(preferredName, personName.getGivenName());
				assertEquals(lastName, personName.getFamilyName());
				assertEquals(middleName, personName.getMiddleName());
			} else {
				assertEquals(firstName, personName.getGivenName());
				assertEquals(lastName, personName.getFamilyName());
				assertEquals(middleName, personName.getMiddleName());
			}
		}
		assertEquals(date, capturedPatient.getBirthdate());
		assertEquals(estimatedBirthDate, capturedPatient
				.getBirthdateEstimated());
		assertEquals(GenderTypeConverter.toOpenMRSString(Gender.FEMALE),
				capturedPatient.getGender());
		assertEquals(address, capturedPatient.getPersonAddress().getAddress1());
		assertEquals(phoneNumber.toString(), capturedPatient.getAttribute(
				phoneAttributeType).getValue());
		assertEquals(ownership, ContactNumberType.valueOf(capturedPatient
				.getAttribute(phoneTypeAttributeType).getValue()));
		assertEquals(format, MediaType.valueOf(capturedPatient.getAttribute(
				mediaTypeAttributeType).getValue()));
		assertEquals(language, capturedPatient.getAttribute(
				languageAttributeType).getValue());
		assertEquals(howLearned, HowLearned.valueOf(capturedPatient
				.getAttribute(howLearnedAttributeType).getValue()));
		assertEquals(reason, InterestReason.valueOf(capturedPatient
				.getAttribute(interestReasonAttributeType).getValue()));
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
		assertEquals(insured, Boolean.valueOf(capturedPatient.getAttribute(
				insuredAttributeType).getValue()));
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
	}

	public void testDemoEnrollPatient() {
		Integer patientId = 1;
		Patient patient = new Patient(patientId);
		String program = "Input Demo Message Program";

		messageBean.addMessageProgramEnrollment(patientId, program, null);

		replay(messageBean);

		regBean.demoEnrollPatient(patient);

		verify(messageBean);
	}

	public void testEditPatient() throws ParseException {

		Integer patientId = 1;
		String phone = "2075551212";
		String nhis = "28";
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		Boolean stopEnrollment = true;

		User staff = new User(2);
		Patient patient = new Patient(patientId);

		Capture<Patient> patientCap = new Capture<Patient>();

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();

		expect(openmrsBean.getPhoneNumberAttributeType()).andReturn(
				phoneAttributeType);
		expect(openmrsBean.getPhoneTypeAttributeType()).andReturn(
				phoneTypeAttributeType);
		expect(openmrsBean.getNHISNumberAttributeType()).andReturn(
				nhisAttributeType);
		expect(openmrsBean.getNHISExpirationDateAttributeType()).andReturn(
				nhisExpirationType);
		expect(patientService.savePatient(capture(patientCap))).andReturn(
				new Patient(patientId));

		messageBean.removeAllMessageProgramEnrollments(patientId);

		replay(contextService, patientService, personService, motechService,
				openmrsBean, messageBean);

		regBean.editPatient(staff, date, patient, phone, phoneType, nhis, date,
				stopEnrollment);

		verify(contextService, patientService, personService, motechService,
				openmrsBean, messageBean);

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
	}

	public void testEditPatientAll() throws ParseException {
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

		Relationship relation = new Relationship(currentMother, patient,
				parentChildRelationshipType);

		Capture<Patient> patientCap = new Capture<Patient>();
		Capture<Relationship> relationCap = new Capture<Relationship>();

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();

		expect(openmrsBean.getInsuredAttributeType()).andReturn(
				insuredAttributeType);
		expect(openmrsBean.getNHISNumberAttributeType()).andReturn(
				nhisAttributeType);
		expect(openmrsBean.getNHISExpirationDateAttributeType()).andReturn(
				nhisExpirationType);
		expect(openmrsBean.getPhoneNumberAttributeType()).andReturn(
				phoneAttributeType);
		expect(openmrsBean.getPhoneTypeAttributeType()).andReturn(
				phoneTypeAttributeType);
		expect(openmrsBean.getMediaTypeAttributeType()).andReturn(
				mediaTypeAttributeType);
		expect(openmrsBean.getLanguageAttributeType()).andReturn(
				languageAttributeType);
		expect(openmrsBean.getDeliveryDayAttributeType()).andReturn(
				deliveryDayAttributeType);
		expect(openmrsBean.getDeliveryTimeAttributeType()).andReturn(
				deliveryTimeAttributeType);
		expect(openmrsBean.getMotherRelationship(patient)).andReturn(relation);
		expect(personService.saveRelationship(capture(relationCap))).andReturn(
				new Relationship());
		expect(openmrsBean.getCommunityByPatient(patient)).andReturn(
				oldCommunity);
		expect(openmrsBean.getCommunityByPatient(patient)).andReturn(null);
		messageBean.removeAllMessageProgramEnrollments(patientId);

		expect(patientService.savePatient(capture(patientCap))).andReturn(
				patient);

		replay(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService, openmrsBean, messageBean);

		regBean.editPatient(patient, firstName, middleName, lastName, prefName,
				date, birthDateEst, sex, insured, nhis, date, newMother,
				community, address, phoneNumber, dueDate, enroll, consent,
				phoneType, mediaType, language, dayOfWeek, date);

		verify(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService, openmrsBean, messageBean);

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

		Relationship motherRelation = relationCap.getValue();
		assertEquals(newMother, motherRelation.getPersonA());
	}

	public void testRegisterPregnancy() throws ParseException {
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

		expect(contextService.getEncounterService())
				.andReturn(encounterService).atLeastOnce();
		expect(contextService.getObsService()).andReturn(obsService);
		expect(contextService.getPatientService()).andReturn(patientService);

		expect(contextService.getAuthenticatedUser()).andReturn(new User());
		expect(openmrsBean.getActivePregnancy(patientId)).andReturn(null);

		expect(openmrsBean.getPhoneNumberAttributeType()).andReturn(
				phoneAttributeType);
		expect(openmrsBean.getPhoneTypeAttributeType()).andReturn(
				phoneTypeAttributeType);
		expect(openmrsBean.getMediaTypeAttributeType()).andReturn(
				mediaTypeAttributeType);
		expect(openmrsBean.getLanguageAttributeType()).andReturn(
				languageAttributeType);
		expect(openmrsBean.getDeliveryDayAttributeType()).andReturn(
				deliveryDayAttributeType);
		expect(openmrsBean.getDeliveryTimeAttributeType()).andReturn(
				deliveryTimeAttributeType);
		expect(openmrsBean.getHowLearnedAttributeType()).andReturn(
				howLearnedAttributeType);
		expect(openmrsBean.getInterestReasonAttributeType()).andReturn(
				interestReasonAttributeType);
		expect(patientService.savePatient((Patient) anyObject())).andReturn(
				new Patient());

		expect(openmrsBean.getGhanaLocation()).andReturn(ghanaLocation);
		expect(openmrsBean.getPregnancyRegistrationVisitEncounterType())
				.andReturn(pregnancyRegVisitType);
		expect(encounterService.saveEncounter(capture(pregnancyEncounterCap)))
				.andReturn(new Encounter());

		expect(openmrsBean.getPregnancyConcept()).andReturn(pregConcept);
		expect(openmrsBean.getPregnancyStatusConcept()).andReturn(
				pregStatusConcept);
		expect(openmrsBean.getDueDateConcept()).andReturn(dateConfConcept);
		expect(openmrsBean.getDueDateConfirmedConcept()).andReturn(
				dateConfConfirmedConcept);
		expect(
				obsService.saveObs(capture(pregnancyObsCap),
						(String) anyObject())).andReturn(new Obs());
		expect(openmrsBean.getCommunityByPatient(patient)).andReturn(null);

		replay(contextService, patientService, motechService, personService,
				locationService, encounterService, obsService, conceptService,
				userService, openmrsBean, messageBean);

		regBean.registerPregnancy(patient, date, dueDateConfirmed, enroll,
				consent, phoneNumber, phoneType, mediaType, language,
				dayOfWeek, date, reason, howLearned);

		verify(contextService, patientService, motechService, personService,
				locationService, encounterService, obsService, conceptService,
				userService, openmrsBean, messageBean);

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
		assertEquals(pregnancyRegVisitType, pregnancyEncounter
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

	public void testRegisterPregnancyExistingNotEnroll() throws ParseException {
		String phoneNumber = "2075555555", language = "Language";
		Date date = new Date();
		Boolean enroll = false, consent = false;
		ContactNumberType ownership = ContactNumberType.PERSONAL;
		MediaType format = MediaType.TEXT;
		DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
		HowLearned howLearned = HowLearned.FRIEND;

		User staff = new User(1);
		Location facility = new Location(2);
		Integer patientId = 5;
		Patient patient = new Patient(patientId);
		Obs pregnanyObs = new Obs(7);
		Obs pregnanyDueDateObs = new Obs(8);

		Capture<Patient> patientCap = new Capture<Patient>();

		expect(openmrsBean.getActivePregnancy(patientId))
				.andReturn(pregnanyObs);
		expect(openmrsBean.getActivePregnancyDueDateObs(patientId, pregnanyObs))
				.andReturn(pregnanyDueDateObs);

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();

		expect(openmrsBean.getPhoneNumberAttributeType()).andReturn(
				phoneAttributeType);
		expect(openmrsBean.getPhoneTypeAttributeType()).andReturn(
				phoneTypeAttributeType);
		expect(openmrsBean.getMediaTypeAttributeType()).andReturn(
				mediaTypeAttributeType);
		expect(openmrsBean.getLanguageAttributeType()).andReturn(
				languageAttributeType);
		expect(openmrsBean.getDeliveryDayAttributeType()).andReturn(
				deliveryDayAttributeType);
		expect(openmrsBean.getDeliveryTimeAttributeType()).andReturn(
				deliveryTimeAttributeType);
		expect(openmrsBean.getHowLearnedAttributeType()).andReturn(
				howLearnedAttributeType);

		expect(patientService.savePatient(capture(patientCap))).andReturn(
				new Patient());

		replay(contextService, patientService, openmrsBean, messageBean);

		regBean.registerPregnancy(staff, facility, date, patient, date, enroll,
				consent, ownership, phoneNumber, format, language, dayOfWeek,
				date, howLearned);

		verify(contextService, patientService, openmrsBean, messageBean);

		Patient capturedPatient = patientCap.getValue();
		assertEquals(phoneNumber.toString(), capturedPatient.getAttribute(
				phoneAttributeType).getValue());
		assertEquals(ownership, ContactNumberType.valueOf(capturedPatient
				.getAttribute(phoneTypeAttributeType).getValue()));
		assertEquals(format, MediaType.valueOf(capturedPatient.getAttribute(
				mediaTypeAttributeType).getValue()));
		assertEquals(language, capturedPatient.getAttribute(
				languageAttributeType).getValue());
		assertEquals(howLearned, HowLearned.valueOf(capturedPatient
				.getAttribute(howLearnedAttributeType).getValue()));
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
	}

	public void testRecordPatientHistory() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		Integer lastIPT = 1, lastTT = 1, lastOPV = 1, lastPenta = 1, lastIPTI = 1;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 0, 0, 0);
		Date date = calendar.getTime();
		calendar.set(2009, 1, 1);
		Date lastIPTDate = calendar.getTime();
		calendar.set(2009, 2, 1);
		Date lastTTDate = calendar.getTime();
		calendar.set(2009, 3, 1);
		Date bcgDate = calendar.getTime();
		calendar.set(2009, 4, 1);
		Date lastOPVDate = calendar.getTime();
		calendar.set(2009, 5, 1);
		Date lastPentaDate = calendar.getTime();
		calendar.set(2009, 6, 1);
		Date measlesDate = calendar.getTime();
		calendar.set(2009, 7, 1);
		Date yellowFeverDate = calendar.getTime();
		calendar.set(2009, 8, 1);
		Date lastIPTIDate = calendar.getTime();
		calendar.set(2009, 9, 1);
		Date lastVitaminADate = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getGhanaLocation()).andReturn(ghanaLocation);
		expect(openmrsBean.getPatientHistoryEncounterType()).andReturn(
				patientHistoryVisitType);

		expect(openmrsBean.getIPTDoseConcept()).andReturn(iptConcept);
		expect(openmrsBean.getTetanusDoseConcept()).andReturn(tetanusConcept);
		expect(openmrsBean.getBCGConcept()).andReturn(bcgConcept);
		expect(openmrsBean.getOPVDoseConcept()).andReturn(opvDoseConcept);
		expect(openmrsBean.getPentaDoseConcept()).andReturn(pentaDoseConcept);
		expect(openmrsBean.getMeaslesConcept()).andReturn(measlesConcept);
		expect(openmrsBean.getYellowFeverConcept()).andReturn(
				yellowFeverConcept);
		expect(openmrsBean.getIPTiDoseConcept()).andReturn(iptiConcept);
		expect(openmrsBean.getVitaminAConcept()).andReturn(vitaminAConcept);
		expect(openmrsBean.getImmunizationsOrderedConcept()).andReturn(
				immunizationConcept).times(4);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean.recordPatientHistory(staff, facility, date, patient, lastIPT,
				lastIPTDate, lastTT, lastTTDate, bcgDate, lastOPV, lastOPVDate,
				lastPenta, lastPentaDate, measlesDate, yellowFeverDate,
				lastIPTI, lastIPTIDate, lastVitaminADate);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(ghanaLocation, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(patientHistoryVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(9, obsSet.size());
		int verifiedObs = 0;
		for (Obs obs : obsSet) {
			assertEquals(patient, obs.getPerson());
			assertEquals(ghanaLocation, obs.getLocation());
			if (iptConcept.equals(obs.getConcept())) {
				assertEquals(lastIPTDate, obs.getObsDatetime());
				assertEquals(new Double(lastIPT), obs.getValueNumeric());
				verifiedObs++;
			} else if (tetanusConcept.equals(obs.getConcept())) {
				assertEquals(lastTTDate, obs.getObsDatetime());
				assertEquals(new Double(lastTT), obs.getValueNumeric());
				verifiedObs++;
			} else if (opvDoseConcept.equals(obs.getConcept())) {
				assertEquals(lastOPVDate, obs.getObsDatetime());
				assertEquals(new Double(lastOPV), obs.getValueNumeric());
				verifiedObs++;
			} else if (pentaDoseConcept.equals(obs.getConcept())) {
				assertEquals(lastPentaDate, obs.getObsDatetime());
				assertEquals(new Double(lastPenta), obs.getValueNumeric());
				verifiedObs++;
			} else if (iptiConcept.equals(obs.getConcept())) {
				assertEquals(lastIPTIDate, obs.getObsDatetime());
				assertEquals(new Double(lastIPTI), obs.getValueNumeric());
				verifiedObs++;
			} else if (immunizationConcept.equals(obs.getConcept())) {
				if (vitaminAConcept.equals(obs.getValueCoded())) {
					assertEquals(lastVitaminADate, obs.getObsDatetime());
					verifiedObs++;
				} else if (yellowFeverConcept.equals(obs.getValueCoded())) {
					assertEquals(yellowFeverDate, obs.getObsDatetime());
					verifiedObs++;
				} else if (measlesConcept.equals(obs.getValueCoded())) {
					assertEquals(measlesDate, obs.getObsDatetime());
					verifiedObs++;
				} else if (bcgConcept.equals(obs.getValueCoded())) {
					assertEquals(bcgDate, obs.getObsDatetime());
					verifiedObs++;
				} else {
					fail("Unexpected immunization Obs coded value: "
							+ obs.getValueCoded());
				}
			} else {
				fail("Unexpected Obs concept: " + obs.getConcept());
			}
		}
		assertEquals("Missing expected obs", 9, verifiedObs);
	}

	public void testRecordPatientHistoryEmpty() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		Integer lastIPT = null, lastTT = null, lastOPV = null, lastPenta = null, lastIPTI = null;
		Date date = new Date();
		Date lastIPTDate = null, lastTTDate = null, bcgDate = null, lastOPVDate = null;
		Date lastPentaDate = null, measlesDate = null, yellowFeverDate = null;
		Date lastIPTIDate = null, lastVitaminADate = null;

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getGhanaLocation()).andReturn(ghanaLocation);
		expect(openmrsBean.getPatientHistoryEncounterType()).andReturn(
				patientHistoryVisitType);

		replay(contextService, encounterService, openmrsBean);

		regBean.recordPatientHistory(staff, facility, date, patient, lastIPT,
				lastIPTDate, lastTT, lastTTDate, bcgDate, lastOPV, lastOPVDate,
				lastPenta, lastPentaDate, measlesDate, yellowFeverDate,
				lastIPTI, lastIPTIDate, lastVitaminADate);

		verify(contextService, encounterService, openmrsBean);
	}

	public void testRecordPatientHistoryPartiallyEmpty() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		Integer lastIPT = 1, lastTT = 1, lastOPV = 1, lastPenta = 1, lastIPTI = 1;
		Date date = new Date();
		Date lastIPTDate = null, lastTTDate = null, bcgDate = null, lastOPVDate = null;
		Date lastPentaDate = null, measlesDate = null, yellowFeverDate = null;
		Date lastIPTIDate = null, lastVitaminADate = null;

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getGhanaLocation()).andReturn(ghanaLocation);
		expect(openmrsBean.getPatientHistoryEncounterType()).andReturn(
				patientHistoryVisitType);

		replay(contextService, encounterService, openmrsBean);

		regBean.recordPatientHistory(staff, facility, date, patient, lastIPT,
				lastIPTDate, lastTT, lastTTDate, bcgDate, lastOPV, lastOPVDate,
				lastPenta, lastPentaDate, measlesDate, yellowFeverDate,
				lastIPTI, lastIPTIDate, lastVitaminADate);

		verify(contextService, encounterService, openmrsBean);
	}

	public void testRegisterANCMotherNoEnrollment() {
		User staff = new User(1);
		Location facility = new Location(2);
		Integer patientId = 3;
		Patient patient = new Patient(patientId);

		Calendar calendar = Calendar.getInstance();
		calendar.set(2009, 12, 1, 8, 30, 0);
		Date date = calendar.getTime();
		calendar.set(2010, 2, 1, 8, 30, 0);
		Date estDeliveryDate = calendar.getTime();
		Date timeOfDay = null;

		String ancRegNumber = "ANCREG1", phoneNumber = null, language = null;
		Double height = 45.2;
		Integer gravida = 1, parity = 0;
		Boolean enroll = false, consent = false;
		ContactNumberType ownership = null;
		MediaType format = null;
		DayOfWeek dayOfWeek = null;
		HowLearned howLearned = null;

		Obs pregnancyObs = new Obs(1);
		Obs pregnancyDueDateObs = null;

		Capture<Encounter> encounterCap = new Capture<Encounter>();
		Capture<Patient> patientCap = new Capture<Patient>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(contextService.getPatientService()).andReturn(patientService);
		expect(openmrsBean.getANCRegistrationEncounterType()).andReturn(
				ancRegistrationType);
		expect(openmrsBean.getANCRegistrationNumberConcept()).andReturn(
				ancRegNumConcept);
		expect(openmrsBean.getGravidaConcept()).andReturn(gravidaConcept);
		expect(openmrsBean.getParityConcept()).andReturn(parityConcept);
		expect(openmrsBean.getHeightConcept()).andReturn(heightConcept);
		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());
		expect(openmrsBean.getActivePregnancy(patientId)).andReturn(
				pregnancyObs);
		expect(
				openmrsBean.getActivePregnancyDueDateObs(patientId,
						pregnancyObs)).andReturn(pregnancyDueDateObs);
		expect(patientService.savePatient(capture(patientCap))).andReturn(
				new Patient());

		replay(contextService, encounterService, patientService, openmrsBean);

		regBean.registerANCMother(staff, facility, date, patient, ancRegNumber,
				estDeliveryDate, height, gravida, parity, enroll, consent,
				ownership, phoneNumber, format, language, dayOfWeek, timeOfDay,
				howLearned);

		verify(contextService, encounterService, patientService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(ancRegistrationType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(4, obsSet.size());
		int verifiedObs = 0;
		for (Obs obs : obsSet) {
			assertEquals(patient, obs.getPerson());
			assertEquals(facility, obs.getLocation());
			assertEquals(date, obs.getObsDatetime());
			if (ancRegNumConcept.equals(obs.getConcept())) {
				assertEquals(ancRegNumber, obs.getValueText());
				verifiedObs++;
			} else if (gravidaConcept.equals(obs.getConcept())) {
				assertEquals(new Double(gravida), obs.getValueNumeric());
				verifiedObs++;
			} else if (parityConcept.equals(obs.getConcept())) {
				assertEquals(new Double(parity), obs.getValueNumeric());
				verifiedObs++;
			} else if (heightConcept.equals(obs.getConcept())) {
				assertEquals(height, obs.getValueNumeric());
				verifiedObs++;
			} else {
				fail("Unexpected Obs concept: " + obs.getConcept());
			}
		}
		assertEquals("Missing expected obs", 4, verifiedObs);

		Patient patientCapture = patientCap.getValue();
		assertEquals(0, patientCapture.getAttributes().size());
	}

	public void testRegisterANCMotherEmptyNoEnrollment() {
		User staff = new User(1);
		Location facility = new Location(2);
		Integer patientId = 3;
		Patient patient = new Patient(patientId);

		Calendar calendar = Calendar.getInstance();
		calendar.set(2009, 12, 1, 8, 30, 0);
		Date date = calendar.getTime();
		Date estDeliveryDate = null;
		Date timeOfDay = null;

		String ancRegNumber = null, phoneNumber = null, language = null;
		Double height = null;
		Integer gravida = null, parity = null;
		Boolean enroll = false, consent = false;
		ContactNumberType ownership = null;
		MediaType format = null;
		DayOfWeek dayOfWeek = null;
		HowLearned howLearned = null;

		Obs pregnancyObs = new Obs(1);
		Obs pregnancyDueDateObs = new Obs(2);

		Capture<Encounter> encounterCap = new Capture<Encounter>();
		Capture<Patient> patientCap = new Capture<Patient>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(contextService.getPatientService()).andReturn(patientService);
		expect(openmrsBean.getANCRegistrationEncounterType()).andReturn(
				ancRegistrationType);
		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());
		expect(openmrsBean.getActivePregnancy(patientId)).andReturn(
				pregnancyObs);
		expect(
				openmrsBean.getActivePregnancyDueDateObs(patientId,
						pregnancyObs)).andReturn(pregnancyDueDateObs);
		expect(patientService.savePatient(capture(patientCap))).andReturn(
				new Patient());

		replay(contextService, encounterService, patientService, openmrsBean);

		regBean.registerANCMother(staff, facility, date, patient, ancRegNumber,
				estDeliveryDate, height, gravida, parity, enroll, consent,
				ownership, phoneNumber, format, language, dayOfWeek, timeOfDay,
				howLearned);

		verify(contextService, encounterService, patientService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(ancRegistrationType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(0, obsSet.size());

		Patient patientCapture = patientCap.getValue();
		assertEquals(0, patientCapture.getAttributes().size());
	}

	public void testRegisterCWCChildNoEnrollment() {
		User staff = new User(1);
		Location facility = new Location(2);
		Integer patientId = 3;
		Patient patient = new Patient(patientId);

		Calendar calendar = Calendar.getInstance();
		calendar.set(2009, 12, 1, 8, 30, 0);
		Date date = calendar.getTime();
		Date timeOfDay = null;

		String cwcRegNumber = "CWCREG1", phoneNumber = null, language = null;
		Boolean enroll = false, consent = false;
		ContactNumberType ownership = null;
		MediaType format = null;
		DayOfWeek dayOfWeek = null;
		HowLearned howLearned = null;

		Capture<Encounter> encounterCap = new Capture<Encounter>();
		Capture<Patient> patientCap = new Capture<Patient>();

		expect(contextService.getPatientService()).andReturn(patientService);
		expect(patientService.savePatient(capture(patientCap))).andReturn(
				new Patient());
		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getCWCRegistrationEncounterType()).andReturn(
				cwcRegistrationType);
		expect(openmrsBean.getCWCRegistrationNumberConcept()).andReturn(
				cwcRegNumConcept);
		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, patientService, openmrsBean);

		regBean.registerCWCChild(staff, facility, date, patient, cwcRegNumber,
				enroll, consent, ownership, phoneNumber, format, language,
				dayOfWeek, timeOfDay, howLearned);

		verify(contextService, encounterService, patientService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(cwcRegistrationType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(1, obsSet.size());
		Obs obs = obsSet.iterator().next();
		assertEquals(patient, obs.getPerson());
		assertEquals(facility, obs.getLocation());
		assertEquals(date, obs.getObsDatetime());
		assertEquals(cwcRegNumConcept, obs.getConcept());
		assertEquals(cwcRegNumber, obs.getValueText());

		Patient patientCapture = patientCap.getValue();
		assertEquals(0, patientCapture.getAttributes().size());
	}

	public void testRegisterCWCChildEmptyNoEnrollment() {
		User staff = new User(1);
		Location facility = new Location(2);
		Integer patientId = 3;
		Patient patient = new Patient(patientId);

		Calendar calendar = Calendar.getInstance();
		calendar.set(2009, 12, 1, 8, 30, 0);
		Date date = calendar.getTime();
		Date timeOfDay = null;

		String cwcRegNumber = null, phoneNumber = null, language = null;
		Boolean enroll = false, consent = false;
		ContactNumberType ownership = null;
		MediaType format = null;
		DayOfWeek dayOfWeek = null;
		HowLearned howLearned = null;

		Capture<Encounter> encounterCap = new Capture<Encounter>();
		Capture<Patient> patientCap = new Capture<Patient>();

		expect(contextService.getPatientService()).andReturn(patientService);
		expect(patientService.savePatient(capture(patientCap))).andReturn(
				new Patient());
		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getCWCRegistrationEncounterType()).andReturn(
				cwcRegistrationType);
		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, patientService, openmrsBean);

		regBean.registerCWCChild(staff, facility, date, patient, cwcRegNumber,
				enroll, consent, ownership, phoneNumber, format, language,
				dayOfWeek, timeOfDay, howLearned);

		verify(contextService, encounterService, patientService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(cwcRegistrationType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(0, obsSet.size());

		Patient patientCapture = patientCap.getValue();
		assertEquals(0, patientCapture.getAttributes().size());
	}

	public void testRecordMotherANCVisitReactivePositive() {
		User staff = new User(1);
		Location facility = new Location(2);
		Integer patientId = 3;
		Patient patient = new Patient(patientId);
		String house = "A12", community = "C23", comments = "Comment";
		Boolean dewormer = true, iptReactive = true, itnUse = true, vdrlReactive = true, vdrlTreatment = true;
		Boolean pmtct = true, preTestCounseled = true, postTestCounseled = true, pmtctTreatment = true;
		Boolean referred = false, maleInvolved = true;
		Integer visitNumber = 1, ancLocation = 3, ttDose = 2, iptDose = 1, bpSystolic = 140, bpDiastolic = 80, fhr = 150;
		Integer urineTestProtein = 1, urineTestGlucose = 1;
		Double weight = 23.1, fht = 25.7, hemoglobin = 21.8;
		HIVResult hivTestResult = HIVResult.POSITIVE;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 8, 30, 0);
		Date date = calendar.getTime();
		calendar.set(2010, 2, 1, 8, 30, 0);
		Date estDeliveryDate = calendar.getTime();
		calendar.set(2010, 1, 1, 8, 30, 0);
		Date nextANCDate = calendar.getTime();

		Obs pregnancyObs = null;
		Obs pregnancyDueDateObs = null;

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getANCVisitEncounterType()).andReturn(ancVisitType);

		expect(openmrsBean.getActivePregnancy(patientId)).andReturn(
				pregnancyObs);

		expect(openmrsBean.getVisitNumberConcept()).andReturn(
				visitNumberConcept);
		expect(openmrsBean.getANCPNCLocationConcept()).andReturn(
				ancpncLocationConcept);
		expect(openmrsBean.getHouseConcept()).andReturn(houseConcept);
		expect(openmrsBean.getCommunityConcept()).andReturn(communityConcept);
		expect(openmrsBean.getSystolicBloodPressureConcept()).andReturn(
				bpSystolicConcept);
		expect(openmrsBean.getDiastolicBloodPressureConcept()).andReturn(
				bpDiastolicConcept);
		expect(openmrsBean.getWeightConcept()).andReturn(weightConcept);
		expect(openmrsBean.getTetanusDoseConcept()).andReturn(tetanusConcept);
		expect(openmrsBean.getIPTDoseConcept()).andReturn(iptConcept);
		expect(openmrsBean.getIPTReactionConcept()).andReturn(
				iptReactionConcept);
		expect(openmrsBean.getReactiveConcept()).andReturn(reactiveConcept);
		expect(openmrsBean.getITNConcept()).andReturn(itnConcept);
		expect(openmrsBean.getFundalHeightConcept()).andReturn(
				fundalHeightConcept);
		expect(openmrsBean.getFetalHeartRateConcept()).andReturn(
				fetalHeartRateConcept);
		expect(openmrsBean.getUrineProteinTestConcept()).andReturn(
				urineProteinTestConcept);
		expect(openmrsBean.getPositiveConcept()).andReturn(positiveConcept);
		expect(openmrsBean.getUrineGlucoseTestConcept()).andReturn(
				urineGlucoseTestConcept);
		expect(openmrsBean.getPositiveConcept()).andReturn(positiveConcept);
		expect(openmrsBean.getHemoglobinConcept()).andReturn(hemoglobinConcept);
		expect(openmrsBean.getVDRLConcept()).andReturn(vdrlConcept);
		expect(openmrsBean.getReactiveConcept()).andReturn(reactiveConcept);
		expect(openmrsBean.getVDRLTreatmentConcept()).andReturn(
				vdrlTreatmentConcept);
		expect(openmrsBean.getDewormerConcept()).andReturn(dewormerConcept);
		expect(openmrsBean.getMaleInvolvementConcept()).andReturn(
				maleInvolvedConcept);
		expect(openmrsBean.getPMTCTConcept()).andReturn(pmtctConcept);
		expect(openmrsBean.getPreHIVTestCounselingConcept()).andReturn(
				preTestCounselConcept);
		expect(openmrsBean.getHIVTestResultConcept()).andReturn(
				hivTestResultConcept);
		expect(openmrsBean.getPostHIVTestCounselingConcept()).andReturn(
				postTestCounselConcept);
		expect(openmrsBean.getPMTCTTreatmentConcept()).andReturn(
				pmtctTreatmentConcept);
		expect(openmrsBean.getReferredConcept()).andReturn(referredConcept);
		expect(openmrsBean.getNextANCDateConcept()).andReturn(
				nextANCDateConcept);
		expect(openmrsBean.getCommentsConcept()).andReturn(commentsConcept);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		expect(
				openmrsBean.getActivePregnancyDueDateObs(patientId,
						pregnancyObs)).andReturn(pregnancyDueDateObs);

		replay(contextService, encounterService, openmrsBean);

		regBean.recordMotherANCVisit(staff, facility, date, patient,
				visitNumber, ancLocation, house, community, estDeliveryDate,
				bpSystolic, bpDiastolic, weight, ttDose, iptDose, iptReactive,
				itnUse, fht, fhr, urineTestProtein, urineTestGlucose,
				hemoglobin, vdrlReactive, vdrlTreatment, dewormer,
				maleInvolved, pmtct, preTestCounseled, hivTestResult,
				postTestCounseled, pmtctTreatment, referred, nextANCDate,
				comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(ancVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(28, obsSet.size());
		int verifiedObs = 0;
		for (Obs obs : obsSet) {
			assertEquals(patient, obs.getPerson());
			assertEquals(date, obs.getObsDatetime());
			assertEquals(facility, obs.getLocation());
			if (visitNumberConcept.equals(obs.getConcept())) {
				assertEquals(new Double(visitNumber), obs.getValueNumeric());
				verifiedObs++;
			} else if (ancpncLocationConcept.equals(obs.getConcept())) {
				assertEquals(new Double(ancLocation), obs.getValueNumeric());
				verifiedObs++;
			} else if (houseConcept.equals(obs.getConcept())) {
				assertEquals(house, obs.getValueText());
				verifiedObs++;
			} else if (communityConcept.equals(obs.getConcept())) {
				assertEquals(community, obs.getValueText());
				verifiedObs++;
			} else if (bpSystolicConcept.equals(obs.getConcept())) {
				assertEquals(new Double(bpSystolic), obs.getValueNumeric());
				verifiedObs++;
			} else if (bpDiastolicConcept.equals(obs.getConcept())) {
				assertEquals(new Double(bpDiastolic), obs.getValueNumeric());
				verifiedObs++;
			} else if (weightConcept.equals(obs.getConcept())) {
				assertEquals(weight, obs.getValueNumeric());
				verifiedObs++;
			} else if (tetanusConcept.equals(obs.getConcept())) {
				assertEquals(new Double(ttDose), obs.getValueNumeric());
				verifiedObs++;
			} else if (iptConcept.equals(obs.getConcept())) {
				assertEquals(new Double(iptDose), obs.getValueNumeric());
				verifiedObs++;
			} else if (iptReactionConcept.equals(obs.getConcept())) {
				assertEquals(reactiveConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (itnConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (fundalHeightConcept.equals(obs.getConcept())) {
				assertEquals(fht, obs.getValueNumeric());
				verifiedObs++;
			} else if (fetalHeartRateConcept.equals(obs.getConcept())) {
				assertEquals(new Double(fhr), obs.getValueNumeric());
				verifiedObs++;
			} else if (urineProteinTestConcept.equals(obs.getConcept())) {
				assertEquals(positiveConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (urineGlucoseTestConcept.equals(obs.getConcept())) {
				assertEquals(positiveConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (hemoglobinConcept.equals(obs.getConcept())) {
				assertEquals(hemoglobin, obs.getValueNumeric());
				verifiedObs++;
			} else if (vdrlConcept.equals(obs.getConcept())) {
				assertEquals(reactiveConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (vdrlTreatmentConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (dewormerConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (maleInvolvedConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (pmtctConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (preTestCounselConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (hivTestResultConcept.equals(obs.getConcept())) {
				assertEquals(hivTestResult.name(), obs.getValueText());
				verifiedObs++;
			} else if (postTestCounselConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (pmtctTreatmentConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (referredConcept.equals(obs.getConcept())) {
				assertEquals(0.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (nextANCDateConcept.equals(obs.getConcept())) {
				assertEquals(nextANCDate, obs.getValueDatetime());
				verifiedObs++;
			} else if (commentsConcept.equals(obs.getConcept())) {
				assertEquals(comments, obs.getValueText());
				verifiedObs++;
			} else {
				fail("Unexpected Obs concept: " + obs.getConcept());
			}
		}
		assertEquals("Missing expected obs", 28, verifiedObs);
	}

	public void testRecordMotherANCVisitNonReactiveNegative() {
		User staff = new User(1);
		Location facility = new Location(2);
		Integer patientId = 3;
		Patient patient = new Patient(patientId);
		String house = "A12", community = "C23", comments = "Comment";
		Boolean dewormer = true, iptReactive = false, itnUse = true, vdrlReactive = false, vdrlTreatment = true;
		Boolean pmtct = true, preTestCounseled = true, postTestCounseled = true, pmtctTreatment = true;
		Boolean referred = false, maleInvolved = true;
		Integer visitNumber = 1, ancLocation = 3, ttDose = 2, iptDose = 1, bpSystolic = 140, bpDiastolic = 80, fhr = 150;
		Integer urineTestProtein = 0, urineTestGlucose = 0;
		Double weight = 23.1, fht = 25.7, hemoglobin = 21.8;
		HIVResult hivTestResult = HIVResult.POSITIVE;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 8, 30, 0);
		Date date = calendar.getTime();
		calendar.set(2010, 2, 1, 8, 30, 0);
		Date estDeliveryDate = calendar.getTime();
		calendar.set(2010, 1, 1, 8, 30, 0);
		Date nextANCDate = calendar.getTime();

		Obs pregnancyObs = null;
		Obs pregnancyDueDateObs = null;

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getANCVisitEncounterType()).andReturn(ancVisitType);

		expect(openmrsBean.getActivePregnancy(patientId)).andReturn(
				pregnancyObs);

		expect(openmrsBean.getVisitNumberConcept()).andReturn(
				visitNumberConcept);
		expect(openmrsBean.getANCPNCLocationConcept()).andReturn(
				ancpncLocationConcept);
		expect(openmrsBean.getHouseConcept()).andReturn(houseConcept);
		expect(openmrsBean.getCommunityConcept()).andReturn(communityConcept);
		expect(openmrsBean.getSystolicBloodPressureConcept()).andReturn(
				bpSystolicConcept);
		expect(openmrsBean.getDiastolicBloodPressureConcept()).andReturn(
				bpDiastolicConcept);
		expect(openmrsBean.getWeightConcept()).andReturn(weightConcept);
		expect(openmrsBean.getTetanusDoseConcept()).andReturn(tetanusConcept);
		expect(openmrsBean.getIPTDoseConcept()).andReturn(iptConcept);
		expect(openmrsBean.getIPTReactionConcept()).andReturn(
				iptReactionConcept);
		expect(openmrsBean.getNonReactiveConcept()).andReturn(
				nonReactiveConcept);
		expect(openmrsBean.getITNConcept()).andReturn(itnConcept);
		expect(openmrsBean.getFundalHeightConcept()).andReturn(
				fundalHeightConcept);
		expect(openmrsBean.getFetalHeartRateConcept()).andReturn(
				fetalHeartRateConcept);
		expect(openmrsBean.getUrineProteinTestConcept()).andReturn(
				urineProteinTestConcept);
		expect(openmrsBean.getNegativeConcept()).andReturn(negativeConcept);
		expect(openmrsBean.getUrineGlucoseTestConcept()).andReturn(
				urineGlucoseTestConcept);
		expect(openmrsBean.getNegativeConcept()).andReturn(negativeConcept);
		expect(openmrsBean.getHemoglobinConcept()).andReturn(hemoglobinConcept);
		expect(openmrsBean.getVDRLConcept()).andReturn(vdrlConcept);
		expect(openmrsBean.getNonReactiveConcept()).andReturn(
				nonReactiveConcept);
		expect(openmrsBean.getVDRLTreatmentConcept()).andReturn(
				vdrlTreatmentConcept);
		expect(openmrsBean.getDewormerConcept()).andReturn(dewormerConcept);
		expect(openmrsBean.getMaleInvolvementConcept()).andReturn(
				maleInvolvedConcept);
		expect(openmrsBean.getPMTCTConcept()).andReturn(pmtctConcept);
		expect(openmrsBean.getPreHIVTestCounselingConcept()).andReturn(
				preTestCounselConcept);
		expect(openmrsBean.getHIVTestResultConcept()).andReturn(
				hivTestResultConcept);
		expect(openmrsBean.getPostHIVTestCounselingConcept()).andReturn(
				postTestCounselConcept);
		expect(openmrsBean.getPMTCTTreatmentConcept()).andReturn(
				pmtctTreatmentConcept);
		expect(openmrsBean.getReferredConcept()).andReturn(referredConcept);
		expect(openmrsBean.getNextANCDateConcept()).andReturn(
				nextANCDateConcept);
		expect(openmrsBean.getCommentsConcept()).andReturn(commentsConcept);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		expect(
				openmrsBean.getActivePregnancyDueDateObs(patientId,
						pregnancyObs)).andReturn(pregnancyDueDateObs);

		replay(contextService, encounterService, openmrsBean);

		regBean.recordMotherANCVisit(staff, facility, date, patient,
				visitNumber, ancLocation, house, community, estDeliveryDate,
				bpSystolic, bpDiastolic, weight, ttDose, iptDose, iptReactive,
				itnUse, fht, fhr, urineTestProtein, urineTestGlucose,
				hemoglobin, vdrlReactive, vdrlTreatment, dewormer,
				maleInvolved, pmtct, preTestCounseled, hivTestResult,
				postTestCounseled, pmtctTreatment, referred, nextANCDate,
				comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(ancVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(28, obsSet.size());
		int verifiedObs = 0;
		for (Obs obs : obsSet) {
			assertEquals(patient, obs.getPerson());
			assertEquals(date, obs.getObsDatetime());
			assertEquals(facility, obs.getLocation());
			if (visitNumberConcept.equals(obs.getConcept())) {
				assertEquals(new Double(visitNumber), obs.getValueNumeric());
				verifiedObs++;
			} else if (ancpncLocationConcept.equals(obs.getConcept())) {
				assertEquals(new Double(ancLocation), obs.getValueNumeric());
				verifiedObs++;
			} else if (houseConcept.equals(obs.getConcept())) {
				assertEquals(house, obs.getValueText());
				verifiedObs++;
			} else if (communityConcept.equals(obs.getConcept())) {
				assertEquals(community, obs.getValueText());
				verifiedObs++;
			} else if (bpSystolicConcept.equals(obs.getConcept())) {
				assertEquals(new Double(bpSystolic), obs.getValueNumeric());
				verifiedObs++;
			} else if (bpDiastolicConcept.equals(obs.getConcept())) {
				assertEquals(new Double(bpDiastolic), obs.getValueNumeric());
				verifiedObs++;
			} else if (weightConcept.equals(obs.getConcept())) {
				assertEquals(weight, obs.getValueNumeric());
				verifiedObs++;
			} else if (tetanusConcept.equals(obs.getConcept())) {
				assertEquals(new Double(ttDose), obs.getValueNumeric());
				verifiedObs++;
			} else if (iptConcept.equals(obs.getConcept())) {
				assertEquals(new Double(iptDose), obs.getValueNumeric());
				verifiedObs++;
			} else if (iptReactionConcept.equals(obs.getConcept())) {
				assertEquals(nonReactiveConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (itnConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (fundalHeightConcept.equals(obs.getConcept())) {
				assertEquals(fht, obs.getValueNumeric());
				verifiedObs++;
			} else if (fetalHeartRateConcept.equals(obs.getConcept())) {
				assertEquals(new Double(fhr), obs.getValueNumeric());
				verifiedObs++;
			} else if (urineProteinTestConcept.equals(obs.getConcept())) {
				assertEquals(negativeConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (urineGlucoseTestConcept.equals(obs.getConcept())) {
				assertEquals(negativeConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (hemoglobinConcept.equals(obs.getConcept())) {
				assertEquals(hemoglobin, obs.getValueNumeric());
				verifiedObs++;
			} else if (vdrlConcept.equals(obs.getConcept())) {
				assertEquals(nonReactiveConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (vdrlTreatmentConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (dewormerConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (maleInvolvedConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (pmtctConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (preTestCounselConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (hivTestResultConcept.equals(obs.getConcept())) {
				assertEquals(hivTestResult.name(), obs.getValueText());
				verifiedObs++;
			} else if (postTestCounselConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (pmtctTreatmentConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (referredConcept.equals(obs.getConcept())) {
				assertEquals(0.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (nextANCDateConcept.equals(obs.getConcept())) {
				assertEquals(nextANCDate, obs.getValueDatetime());
				verifiedObs++;
			} else if (commentsConcept.equals(obs.getConcept())) {
				assertEquals(comments, obs.getValueText());
				verifiedObs++;
			} else {
				fail("Unexpected Obs concept: " + obs.getConcept());
			}
		}
		assertEquals("Missing expected obs", 28, verifiedObs);
	}

	public void testRecordMotherANCVisitTrace() {
		User staff = new User(1);
		Location facility = new Location(2);
		Integer patientId = 3;
		Patient patient = new Patient(patientId);
		String house = "A12", community = "C23", comments = "Comment";
		Boolean dewormer = true, iptReactive = false, itnUse = true, vdrlReactive = false, vdrlTreatment = true;
		Boolean pmtct = true, preTestCounseled = true, postTestCounseled = true, pmtctTreatment = true;
		Boolean referred = false, maleInvolved = true;
		Integer visitNumber = 1, ancLocation = 3, ttDose = 2, iptDose = 1, bpSystolic = 140, bpDiastolic = 80, fhr = 150;
		Integer urineTestProtein = 2, urineTestGlucose = 2;
		Double weight = 23.1, fht = 25.7, hemoglobin = 21.8;
		HIVResult hivTestResult = HIVResult.POSITIVE;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 8, 30, 0);
		Date date = calendar.getTime();
		calendar.set(2010, 2, 1, 8, 30, 0);
		Date estDeliveryDate = calendar.getTime();
		calendar.set(2010, 1, 1, 8, 30, 0);
		Date nextANCDate = calendar.getTime();

		Obs pregnancyObs = null;
		Obs pregnancyDueDateObs = null;

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getANCVisitEncounterType()).andReturn(ancVisitType);

		expect(openmrsBean.getActivePregnancy(patientId)).andReturn(
				pregnancyObs);

		expect(openmrsBean.getVisitNumberConcept()).andReturn(
				visitNumberConcept);
		expect(openmrsBean.getANCPNCLocationConcept()).andReturn(
				ancpncLocationConcept);
		expect(openmrsBean.getHouseConcept()).andReturn(houseConcept);
		expect(openmrsBean.getCommunityConcept()).andReturn(communityConcept);
		expect(openmrsBean.getSystolicBloodPressureConcept()).andReturn(
				bpSystolicConcept);
		expect(openmrsBean.getDiastolicBloodPressureConcept()).andReturn(
				bpDiastolicConcept);
		expect(openmrsBean.getWeightConcept()).andReturn(weightConcept);
		expect(openmrsBean.getTetanusDoseConcept()).andReturn(tetanusConcept);
		expect(openmrsBean.getIPTDoseConcept()).andReturn(iptConcept);
		expect(openmrsBean.getIPTReactionConcept()).andReturn(
				iptReactionConcept);
		expect(openmrsBean.getNonReactiveConcept()).andReturn(
				nonReactiveConcept);
		expect(openmrsBean.getITNConcept()).andReturn(itnConcept);
		expect(openmrsBean.getFundalHeightConcept()).andReturn(
				fundalHeightConcept);
		expect(openmrsBean.getFetalHeartRateConcept()).andReturn(
				fetalHeartRateConcept);
		expect(openmrsBean.getUrineProteinTestConcept()).andReturn(
				urineProteinTestConcept);
		expect(openmrsBean.getTraceConcept()).andReturn(traceConcept);
		expect(openmrsBean.getUrineGlucoseTestConcept()).andReturn(
				urineGlucoseTestConcept);
		expect(openmrsBean.getTraceConcept()).andReturn(traceConcept);
		expect(openmrsBean.getHemoglobinConcept()).andReturn(hemoglobinConcept);
		expect(openmrsBean.getVDRLConcept()).andReturn(vdrlConcept);
		expect(openmrsBean.getNonReactiveConcept()).andReturn(
				nonReactiveConcept);
		expect(openmrsBean.getVDRLTreatmentConcept()).andReturn(
				vdrlTreatmentConcept);
		expect(openmrsBean.getDewormerConcept()).andReturn(dewormerConcept);
		expect(openmrsBean.getMaleInvolvementConcept()).andReturn(
				maleInvolvedConcept);
		expect(openmrsBean.getPMTCTConcept()).andReturn(pmtctConcept);
		expect(openmrsBean.getPreHIVTestCounselingConcept()).andReturn(
				preTestCounselConcept);
		expect(openmrsBean.getHIVTestResultConcept()).andReturn(
				hivTestResultConcept);
		expect(openmrsBean.getPostHIVTestCounselingConcept()).andReturn(
				postTestCounselConcept);
		expect(openmrsBean.getPMTCTTreatmentConcept()).andReturn(
				pmtctTreatmentConcept);
		expect(openmrsBean.getReferredConcept()).andReturn(referredConcept);
		expect(openmrsBean.getNextANCDateConcept()).andReturn(
				nextANCDateConcept);
		expect(openmrsBean.getCommentsConcept()).andReturn(commentsConcept);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		expect(
				openmrsBean.getActivePregnancyDueDateObs(patientId,
						pregnancyObs)).andReturn(pregnancyDueDateObs);

		replay(contextService, encounterService, openmrsBean);

		regBean.recordMotherANCVisit(staff, facility, date, patient,
				visitNumber, ancLocation, house, community, estDeliveryDate,
				bpSystolic, bpDiastolic, weight, ttDose, iptDose, iptReactive,
				itnUse, fht, fhr, urineTestProtein, urineTestGlucose,
				hemoglobin, vdrlReactive, vdrlTreatment, dewormer,
				maleInvolved, pmtct, preTestCounseled, hivTestResult,
				postTestCounseled, pmtctTreatment, referred, nextANCDate,
				comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(ancVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(28, obsSet.size());
		int verifiedObs = 0;
		for (Obs obs : obsSet) {
			assertEquals(patient, obs.getPerson());
			assertEquals(date, obs.getObsDatetime());
			assertEquals(facility, obs.getLocation());
			if (visitNumberConcept.equals(obs.getConcept())) {
				assertEquals(new Double(visitNumber), obs.getValueNumeric());
				verifiedObs++;
			} else if (ancpncLocationConcept.equals(obs.getConcept())) {
				assertEquals(new Double(ancLocation), obs.getValueNumeric());
				verifiedObs++;
			} else if (houseConcept.equals(obs.getConcept())) {
				assertEquals(house, obs.getValueText());
				verifiedObs++;
			} else if (communityConcept.equals(obs.getConcept())) {
				assertEquals(community, obs.getValueText());
				verifiedObs++;
			} else if (bpSystolicConcept.equals(obs.getConcept())) {
				assertEquals(new Double(bpSystolic), obs.getValueNumeric());
				verifiedObs++;
			} else if (bpDiastolicConcept.equals(obs.getConcept())) {
				assertEquals(new Double(bpDiastolic), obs.getValueNumeric());
				verifiedObs++;
			} else if (weightConcept.equals(obs.getConcept())) {
				assertEquals(weight, obs.getValueNumeric());
				verifiedObs++;
			} else if (tetanusConcept.equals(obs.getConcept())) {
				assertEquals(new Double(ttDose), obs.getValueNumeric());
				verifiedObs++;
			} else if (iptConcept.equals(obs.getConcept())) {
				assertEquals(new Double(iptDose), obs.getValueNumeric());
				verifiedObs++;
			} else if (iptReactionConcept.equals(obs.getConcept())) {
				assertEquals(nonReactiveConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (itnConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (fundalHeightConcept.equals(obs.getConcept())) {
				assertEquals(fht, obs.getValueNumeric());
				verifiedObs++;
			} else if (fetalHeartRateConcept.equals(obs.getConcept())) {
				assertEquals(new Double(fhr), obs.getValueNumeric());
				verifiedObs++;
			} else if (urineProteinTestConcept.equals(obs.getConcept())) {
				assertEquals(traceConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (urineGlucoseTestConcept.equals(obs.getConcept())) {
				assertEquals(traceConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (hemoglobinConcept.equals(obs.getConcept())) {
				assertEquals(hemoglobin, obs.getValueNumeric());
				verifiedObs++;
			} else if (vdrlConcept.equals(obs.getConcept())) {
				assertEquals(nonReactiveConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (vdrlTreatmentConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (dewormerConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (maleInvolvedConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (pmtctConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (preTestCounselConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (hivTestResultConcept.equals(obs.getConcept())) {
				assertEquals(hivTestResult.name(), obs.getValueText());
				verifiedObs++;
			} else if (postTestCounselConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (pmtctTreatmentConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (referredConcept.equals(obs.getConcept())) {
				assertEquals(0.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (nextANCDateConcept.equals(obs.getConcept())) {
				assertEquals(nextANCDate, obs.getValueDatetime());
				verifiedObs++;
			} else if (commentsConcept.equals(obs.getConcept())) {
				assertEquals(comments, obs.getValueText());
				verifiedObs++;
			} else {
				fail("Unexpected Obs concept: " + obs.getConcept());
			}
		}
		assertEquals("Missing expected obs", 28, verifiedObs);
	}

	public void testRecordMotherANCVisitEmpty() {
		User staff = new User(1);
		Location facility = new Location(2);
		Integer patientId = 3;
		Patient patient = new Patient(patientId);
		String house = null, community = null, comments = null;
		Boolean dewormer = null, iptReactive = null, itnUse = null, vdrlReactive = null, vdrlTreatment = null;
		Boolean pmtct = null, preTestCounseled = null, postTestCounseled = null, pmtctTreatment = null;
		Boolean referred = null, maleInvolved = null;
		Integer visitNumber = null, ancLocation = null, ttDose = null, iptDose = null, bpSystolic = null, bpDiastolic = null, fhr = null;
		Integer urineTestProtein = null, urineTestGlucose = null;
		Double weight = null, fht = null, hemoglobin = null;
		HIVResult hivTestResult = null;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 8, 30, 0);
		Date date = calendar.getTime();
		Date estDeliveryDate = null;
		Date nextANCDate = null;

		Obs pregnancyObs = null;

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getANCVisitEncounterType()).andReturn(ancVisitType);

		expect(openmrsBean.getActivePregnancy(patientId)).andReturn(
				pregnancyObs);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean.recordMotherANCVisit(staff, facility, date, patient,
				visitNumber, ancLocation, house, community, estDeliveryDate,
				bpSystolic, bpDiastolic, weight, ttDose, iptDose, iptReactive,
				itnUse, fht, fhr, urineTestProtein, urineTestGlucose,
				hemoglobin, vdrlReactive, vdrlTreatment, dewormer,
				maleInvolved, pmtct, preTestCounseled, hivTestResult,
				postTestCounseled, pmtctTreatment, referred, nextANCDate,
				comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(ancVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(0, obsSet.size());
	}

	public void testRecordMotherPNCVisit() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		String house = "A12", community = "C23", comments = "Comment";
		Boolean referred = false, maleInvolved = true, vitaminA = true, lochiaOdourFoul = false, lochiaAmountExcess = false;
		Integer visitNumber = 1, pncLocation = 3, ttDose = 1, lochiaColour = 3;
		Double temperature = 23.1, fht = 25.7;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 8, 30, 0);
		Date date = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getMotherPNCVisitEncounterType()).andReturn(
				pncMotherVisitType);

		expect(openmrsBean.getVisitNumberConcept()).andReturn(
				visitNumberConcept);
		expect(openmrsBean.getANCPNCLocationConcept()).andReturn(
				ancpncLocationConcept);
		expect(openmrsBean.getHouseConcept()).andReturn(houseConcept);
		expect(openmrsBean.getCommunityConcept()).andReturn(communityConcept);
		expect(openmrsBean.getReferredConcept()).andReturn(referredConcept);
		expect(openmrsBean.getMaleInvolvementConcept()).andReturn(
				maleInvolvedConcept);
		expect(openmrsBean.getVitaminAConcept()).andReturn(vitaminAConcept);
		expect(openmrsBean.getImmunizationsOrderedConcept()).andReturn(
				immunizationConcept);
		expect(openmrsBean.getTetanusDoseConcept()).andReturn(tetanusConcept);
		expect(openmrsBean.getLochiaColourConcept()).andReturn(
				lochiaColourConcept);
		expect(openmrsBean.getLochiaFoulConcept())
				.andReturn(lochiaOdourConcept);
		expect(openmrsBean.getLochiaExcessConcept()).andReturn(
				lochiaExcessConcept);
		expect(openmrsBean.getTemperatureConcept()).andReturn(
				temperatureConcept);
		expect(openmrsBean.getFundalHeightConcept()).andReturn(
				fundalHeightConcept);
		expect(openmrsBean.getCommentsConcept()).andReturn(commentsConcept);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean
				.recordMotherPNCVisit(staff, facility, date, patient,
						visitNumber, pncLocation, house, community, referred,
						maleInvolved, vitaminA, ttDose, lochiaColour,
						lochiaAmountExcess, lochiaOdourFoul, temperature, fht,
						comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(pncMotherVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(14, obsSet.size());
		int verifiedObs = 0;
		for (Obs obs : obsSet) {
			assertEquals(patient, obs.getPerson());
			assertEquals(date, obs.getObsDatetime());
			assertEquals(facility, obs.getLocation());
			if (visitNumberConcept.equals(obs.getConcept())) {
				assertEquals(new Double(visitNumber), obs.getValueNumeric());
				verifiedObs++;
			} else if (ancpncLocationConcept.equals(obs.getConcept())) {
				assertEquals(new Double(pncLocation), obs.getValueNumeric());
				verifiedObs++;
			} else if (houseConcept.equals(obs.getConcept())) {
				assertEquals(house, obs.getValueText());
				verifiedObs++;
			} else if (communityConcept.equals(obs.getConcept())) {
				assertEquals(community, obs.getValueText());
				verifiedObs++;
			} else if (referredConcept.equals(obs.getConcept())) {
				assertEquals(0.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (maleInvolvedConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (tetanusConcept.equals(obs.getConcept())) {
				assertEquals(new Double(ttDose), obs.getValueNumeric());
				verifiedObs++;
			} else if (lochiaColourConcept.equals(obs.getConcept())) {
				assertEquals(new Double(lochiaColour), obs.getValueNumeric());
				verifiedObs++;
			} else if (lochiaOdourConcept.equals(obs.getConcept())) {
				assertEquals(0.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (lochiaExcessConcept.equals(obs.getConcept())) {
				assertEquals(0.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (temperatureConcept.equals(obs.getConcept())) {
				assertEquals(temperature, obs.getValueNumeric());
				verifiedObs++;
			} else if (fundalHeightConcept.equals(obs.getConcept())) {
				assertEquals(fht, obs.getValueNumeric());
				verifiedObs++;
			} else if (commentsConcept.equals(obs.getConcept())) {
				assertEquals(comments, obs.getValueText());
				verifiedObs++;
			} else if (immunizationConcept.equals(obs.getConcept())
					&& vitaminAConcept.equals(obs.getValueCoded())) {
				verifiedObs++;
			} else {
				fail("Unexpected Obs concept: " + obs.getConcept());
			}
		}
		assertEquals("Missing expected obs", 14, verifiedObs);
	}

	public void testRecordMotherPNCVisitEmpty() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		String house = null, community = null, comments = null;
		Boolean referred = null, maleInvolved = null, vitaminA = null, lochiaOdourFoul = null, lochiaAmountExcess = null;
		Integer visitNumber = null, pncLocation = null, ttDose = null, lochiaColour = null;
		Double temperature = null, fht = null;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 8, 30, 0);
		Date date = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getMotherPNCVisitEncounterType()).andReturn(
				pncMotherVisitType);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean
				.recordMotherPNCVisit(staff, facility, date, patient,
						visitNumber, pncLocation, house, community, referred,
						maleInvolved, vitaminA, ttDose, lochiaColour,
						lochiaAmountExcess, lochiaOdourFoul, temperature, fht,
						comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(pncMotherVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(0, obsSet.size());
	}

	public void testRecordChildPNCVisit() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		String house = "A12", community = "C23", comments = "Comment";
		Boolean bcg = true, opv0 = true, cordConditionNormal = true, babyConditionGood = true, referred = false, maleInvolved = true;
		Integer visitNumber = 1, pncLocation = 3, respiration = 60;
		Double weight = 4.1, temperature = 20.2;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 8, 30, 0);
		Date date = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getChildPNCVisitEncounterType()).andReturn(
				pncChildVisitType);

		expect(openmrsBean.getVisitNumberConcept()).andReturn(
				visitNumberConcept);
		expect(openmrsBean.getANCPNCLocationConcept()).andReturn(
				ancpncLocationConcept);
		expect(openmrsBean.getHouseConcept()).andReturn(houseConcept);
		expect(openmrsBean.getCommunityConcept()).andReturn(communityConcept);
		expect(openmrsBean.getReferredConcept()).andReturn(referredConcept);
		expect(openmrsBean.getMaleInvolvementConcept()).andReturn(
				maleInvolvedConcept);
		expect(openmrsBean.getWeightConcept()).andReturn(weightConcept);
		expect(openmrsBean.getTemperatureConcept()).andReturn(
				temperatureConcept);
		expect(openmrsBean.getBCGConcept()).andReturn(bcgConcept);
		expect(openmrsBean.getImmunizationsOrderedConcept()).andReturn(
				immunizationConcept);
		expect(openmrsBean.getOPVDoseConcept()).andReturn(opvDoseConcept);
		expect(openmrsBean.getRespiratoryRateConcept()).andReturn(
				respirationConcept);
		expect(openmrsBean.getCordConditionConcept()).andReturn(
				cordConditionConcept);
		expect(openmrsBean.getConditionBabyConcept()).andReturn(
				babyConditionConcept);
		expect(openmrsBean.getCommentsConcept()).andReturn(commentsConcept);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean.recordChildPNCVisit(staff, facility, date, patient,
				visitNumber, pncLocation, house, community, referred,
				maleInvolved, weight, temperature, bcg, opv0, respiration,
				cordConditionNormal, babyConditionGood, comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(pncChildVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(14, obsSet.size());
		int verifiedObs = 0;
		for (Obs obs : obsSet) {
			assertEquals(patient, obs.getPerson());
			assertEquals(date, obs.getObsDatetime());
			assertEquals(facility, obs.getLocation());
			if (visitNumberConcept.equals(obs.getConcept())) {
				assertEquals(new Double(visitNumber), obs.getValueNumeric());
				verifiedObs++;
			} else if (ancpncLocationConcept.equals(obs.getConcept())) {
				assertEquals(new Double(pncLocation), obs.getValueNumeric());
				verifiedObs++;
			} else if (houseConcept.equals(obs.getConcept())) {
				assertEquals(house, obs.getValueText());
				verifiedObs++;
			} else if (communityConcept.equals(obs.getConcept())) {
				assertEquals(community, obs.getValueText());
				verifiedObs++;
			} else if (referredConcept.equals(obs.getConcept())) {
				assertEquals(0.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (maleInvolvedConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (weightConcept.equals(obs.getConcept())) {
				assertEquals(weight, obs.getValueNumeric());
				verifiedObs++;
			} else if (temperatureConcept.equals(obs.getConcept())) {
				assertEquals(temperature, obs.getValueNumeric());
				verifiedObs++;
			} else if (opvDoseConcept.equals(obs.getConcept())) {
				assertEquals(0.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (respirationConcept.equals(obs.getConcept())) {
				assertEquals(new Double(respiration), obs.getValueNumeric());
				verifiedObs++;
			} else if (cordConditionConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (babyConditionConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (commentsConcept.equals(obs.getConcept())) {
				assertEquals(comments, obs.getValueText());
				verifiedObs++;
			} else if (immunizationConcept.equals(obs.getConcept())
					&& bcgConcept.equals(obs.getValueCoded())) {
				verifiedObs++;
			} else {
				fail("Unexpected Obs concept: " + obs.getConcept());
			}
		}
		assertEquals("Missing expected obs", 14, verifiedObs);
	}

	public void testRecordChildPNCVisitEmpty() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		String house = null, community = null, comments = null;
		Boolean bcg = null, opv0 = null, cordConditionNormal = null, babyConditionGood = null, referred = null, maleInvolved = null;
		Integer visitNumber = null, pncLocation = null, respiration = null;
		Double weight = null, temperature = null;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 8, 30, 0);
		Date date = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getChildPNCVisitEncounterType()).andReturn(
				pncChildVisitType);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean.recordChildPNCVisit(staff, facility, date, patient,
				visitNumber, pncLocation, house, community, referred,
				maleInvolved, weight, temperature, bcg, opv0, respiration,
				cordConditionNormal, babyConditionGood, comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(pncChildVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(0, obsSet.size());
	}

	public void testRecordTTVisit() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		Integer ttDose = 3;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 0, 0, 0);
		Date date = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getTTVisitEncounterType()).andReturn(ttVisitType);
		expect(openmrsBean.getTetanusDoseConcept()).andReturn(tetanusConcept);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean.recordTTVisit(staff, facility, date, patient, ttDose);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(ttVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(1, obsSet.size());
		Obs obs = obsSet.iterator().next();
		assertEquals(patient, obs.getPerson());
		assertEquals(facility, obs.getLocation());
		assertEquals(date, obs.getObsDatetime());
		assertEquals(tetanusConcept, obs.getConcept());
		assertEquals(new Double(ttDose), obs.getValueNumeric());
	}

	public void testRecordTTVisitEmpty() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		Integer ttDose = null;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 0, 0, 0);
		Date date = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getTTVisitEncounterType()).andReturn(ttVisitType);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean.recordTTVisit(staff, facility, date, patient, ttDose);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(ttVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(0, obsSet.size());
	}

	public void testRecordChildCWCVisit() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		String house = "A12", community = "C23", comments = "Comment";
		Boolean bcg = true, measles = true, yellowFever = true, csm = true, vitaminA = true, dewormer = true, maleInvolved = true;
		Integer cwcLocation = 3, opvDose = 1, pentaDose = 1, iptiDose = 1;
		Double weight = 4.1, muac = 5.2, height = 10.3;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 0, 0, 0);
		Date date = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getCWCVisitEncounterType()).andReturn(cwcVisitType);

		expect(openmrsBean.getBCGConcept()).andReturn(bcgConcept);
		expect(openmrsBean.getOPVDoseConcept()).andReturn(opvDoseConcept);
		expect(openmrsBean.getPentaDoseConcept()).andReturn(pentaDoseConcept);
		expect(openmrsBean.getMeaslesConcept()).andReturn(measlesConcept);
		expect(openmrsBean.getYellowFeverConcept()).andReturn(
				yellowFeverConcept);
		expect(openmrsBean.getIPTiDoseConcept()).andReturn(iptiConcept);
		expect(openmrsBean.getVitaminAConcept()).andReturn(vitaminAConcept);
		expect(openmrsBean.getCSMConcept()).andReturn(csmConcept);
		expect(openmrsBean.getImmunizationsOrderedConcept()).andReturn(
				immunizationConcept).times(5);

		expect(openmrsBean.getCWCLocationConcept()).andReturn(
				cwcLocationConcept);
		expect(openmrsBean.getHouseConcept()).andReturn(houseConcept);
		expect(openmrsBean.getCommunityConcept()).andReturn(communityConcept);
		expect(openmrsBean.getDewormerConcept()).andReturn(dewormerConcept);
		expect(openmrsBean.getWeightConcept()).andReturn(weightConcept);
		expect(openmrsBean.getMUACConcept()).andReturn(muacConcept);
		expect(openmrsBean.getHeightConcept()).andReturn(heightConcept);
		expect(openmrsBean.getMaleInvolvementConcept()).andReturn(
				maleInvolvedConcept);
		expect(openmrsBean.getCommentsConcept()).andReturn(commentsConcept);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean.recordChildCWCVisit(staff, facility, date, patient,
				cwcLocation, house, community, bcg, opvDose, pentaDose,
				measles, yellowFever, csm, iptiDose, vitaminA, dewormer,
				weight, muac, height, maleInvolved, comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(cwcVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(17, obsSet.size());
		int verifiedObs = 0;
		for (Obs obs : obsSet) {
			assertEquals(patient, obs.getPerson());
			assertEquals(date, obs.getObsDatetime());
			assertEquals(facility, obs.getLocation());
			if (cwcLocationConcept.equals(obs.getConcept())) {
				assertEquals(new Double(cwcLocation), obs.getValueNumeric());
				verifiedObs++;
			} else if (houseConcept.equals(obs.getConcept())) {
				assertEquals(house, obs.getValueText());
				verifiedObs++;
			} else if (communityConcept.equals(obs.getConcept())) {
				assertEquals(community, obs.getValueText());
				verifiedObs++;
			} else if (opvDoseConcept.equals(obs.getConcept())) {
				assertEquals(new Double(opvDose), obs.getValueNumeric());
				verifiedObs++;
			} else if (pentaDoseConcept.equals(obs.getConcept())) {
				assertEquals(new Double(pentaDose), obs.getValueNumeric());
				verifiedObs++;
			} else if (iptiConcept.equals(obs.getConcept())) {
				assertEquals(new Double(iptiDose), obs.getValueNumeric());
				verifiedObs++;
			} else if (dewormerConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (weightConcept.equals(obs.getConcept())) {
				assertEquals(weight, obs.getValueNumeric());
				verifiedObs++;
			} else if (muacConcept.equals(obs.getConcept())) {
				assertEquals(muac, obs.getValueNumeric());
				verifiedObs++;
			} else if (heightConcept.equals(obs.getConcept())) {
				assertEquals(height, obs.getValueNumeric());
				verifiedObs++;
			} else if (maleInvolvedConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (commentsConcept.equals(obs.getConcept())) {
				assertEquals(comments, obs.getValueText());
				verifiedObs++;
			} else if (immunizationConcept.equals(obs.getConcept())) {
				if (vitaminAConcept.equals(obs.getValueCoded())) {
					verifiedObs++;
				} else if (yellowFeverConcept.equals(obs.getValueCoded())) {
					verifiedObs++;
				} else if (measlesConcept.equals(obs.getValueCoded())) {
					verifiedObs++;
				} else if (bcgConcept.equals(obs.getValueCoded())) {
					verifiedObs++;
				} else if (csmConcept.equals(obs.getValueCoded())) {
					verifiedObs++;
				} else {
					fail("Unexpected immunization Obs coded value: "
							+ obs.getValueCoded());
				}
			} else {
				fail("Unexpected Obs concept: " + obs.getConcept());
			}
		}
		assertEquals("Missing expected obs", 17, verifiedObs);
	}

	public void testRecordChildCWCVisitEmpty() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		String house = null, community = null, comments = null;
		Boolean bcg = null, measles = null, yellowFever = null, csm = null, vitaminA = null, dewormer = null, maleInvolved = null;
		Integer cwcLocation = null, opvDose = null, pentaDose = null, iptiDose = null;
		Double weight = null, muac = null, height = null;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 0, 0, 0);
		Date date = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getCWCVisitEncounterType()).andReturn(cwcVisitType);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean.recordChildCWCVisit(staff, facility, date, patient,
				cwcLocation, house, community, bcg, opvDose, pentaDose,
				measles, yellowFever, csm, iptiDose, vitaminA, dewormer,
				weight, muac, height, maleInvolved, comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(cwcVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(0, obsSet.size());
	}

	public void testRecordGeneralOutpatientVisit() {
		Integer staffId = 1, facilityId = 2, diagnosis = 1, secondDiagnosis = 2;
		String serialNumber = "A12", comments = "Comment";
		Boolean insured = true, rdtGiven = false, rdtPositive = false, actTreated = false, newCase = true, referred = false;
		Gender sex = Gender.MALE;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 0, 0, 0);
		Date date = calendar.getTime();
		calendar.set(1979, 9, 1, 0, 0, 0);
		Date dateOfBirth = calendar.getTime();

		Capture<GeneralOutpatientEncounter> encounterCap = new Capture<GeneralOutpatientEncounter>();

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(
				motechService
						.saveGeneralOutpatientEncounter(capture(encounterCap)))
				.andReturn(new GeneralOutpatientEncounter());

		replay(contextService, motechService);

		regBean.recordGeneralOutpatientVisit(staffId, facilityId, date,
				serialNumber, sex, dateOfBirth, insured, diagnosis,
				secondDiagnosis, rdtGiven, rdtPositive, actTreated, newCase,
				referred, comments);

		verify(contextService, motechService);

		GeneralOutpatientEncounter encounter = encounterCap.getValue();
		assertEquals(staffId, encounter.getStaffId());
		assertEquals(facilityId, encounter.getFacilityId());
		assertEquals(date, encounter.getDate());
		assertEquals(serialNumber, encounter.getSerialNumber());
		assertEquals(sex, encounter.getSex());
		assertEquals(dateOfBirth, encounter.getBirthDate());
		assertEquals(insured, encounter.getInsured());
		assertEquals(diagnosis, encounter.getDiagnosis());
		assertEquals(secondDiagnosis, encounter.getSecondaryDiagnosis());
		assertEquals(rdtGiven, encounter.getRdtGiven());
		assertEquals(rdtPositive, encounter.getRdtPositive());
		assertEquals(actTreated, encounter.getActTreated());
		assertEquals(newCase, encounter.getNewCase());
		assertEquals(referred, encounter.getReferred());
		assertEquals(comments, encounter.getComments());
	}

	public void testRecordOutpatientVisit() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		String serialNumber = "A12", comments = "Comment";
		Boolean insured = true, rdtGiven = false, rdtPositive = false, actTreated = false, newCase = true, referred = false;
		Integer diagnosis = 1, secondDiagnosis = 2;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 0, 0, 0);
		Date date = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getOutpatientVisitEncounterType()).andReturn(
				outpatientVisitType);

		expect(openmrsBean.getSerialNumberConcept()).andReturn(
				serialNumberConcept);
		expect(openmrsBean.getInsuredConcept()).andReturn(insuredConcept);
		expect(openmrsBean.getNewCaseConcept()).andReturn(newCaseConcept);
		expect(openmrsBean.getPrimaryDiagnosisConcept()).andReturn(
				diagnosisConcept);
		expect(openmrsBean.getSecondaryDiagnosisConcept()).andReturn(
				secondDiagnosisConcept);
		expect(openmrsBean.getReferredConcept()).andReturn(referredConcept);
		expect(openmrsBean.getACTTreatmentConcept()).andReturn(
				actTreatmentConcept);
		expect(openmrsBean.getCommentsConcept()).andReturn(commentsConcept);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean.recordOutpatientVisit(staff, facility, date, patient,
				serialNumber, insured, diagnosis, secondDiagnosis, rdtGiven,
				rdtPositive, actTreated, newCase, referred, comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(outpatientVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(8, obsSet.size());
		int verifiedObs = 0;
		for (Obs obs : obsSet) {
			assertEquals(patient, obs.getPerson());
			assertEquals(date, obs.getObsDatetime());
			assertEquals(facility, obs.getLocation());
			if (serialNumberConcept.equals(obs.getConcept())) {
				assertEquals(serialNumber, obs.getValueText());
				verifiedObs++;
			} else if (insuredConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (newCaseConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (diagnosisConcept.equals(obs.getConcept())) {
				assertEquals(new Double(diagnosis), obs.getValueNumeric());
				verifiedObs++;
			} else if (secondDiagnosisConcept.equals(obs.getConcept())) {
				assertEquals(new Double(secondDiagnosis), obs.getValueNumeric());
				verifiedObs++;
			} else if (referredConcept.equals(obs.getConcept())) {
				assertEquals(0.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (actTreatmentConcept.equals(obs.getConcept())) {
				assertEquals(0.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (commentsConcept.equals(obs.getConcept())) {
				assertEquals(comments, obs.getValueText());
				verifiedObs++;
			} else {
				fail("Unexpected Obs concept: " + obs.getConcept());
			}
		}
		assertEquals("Missing expected obs", 8, verifiedObs);
	}

	public void testRecordOutpatientVisitRDTPositive() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		String serialNumber = "A12", comments = "Comment";
		Boolean insured = true, rdtGiven = true, rdtPositive = true, actTreated = true, newCase = true, referred = true;
		Integer diagnosis = 5, secondDiagnosis = 7;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 0, 0, 0);
		Date date = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getOutpatientVisitEncounterType()).andReturn(
				outpatientVisitType);

		expect(openmrsBean.getSerialNumberConcept()).andReturn(
				serialNumberConcept);
		expect(openmrsBean.getInsuredConcept()).andReturn(insuredConcept);
		expect(openmrsBean.getNewCaseConcept()).andReturn(newCaseConcept);
		expect(openmrsBean.getPrimaryDiagnosisConcept()).andReturn(
				diagnosisConcept);
		expect(openmrsBean.getSecondaryDiagnosisConcept()).andReturn(
				secondDiagnosisConcept);
		expect(openmrsBean.getReferredConcept()).andReturn(referredConcept);
		expect(openmrsBean.getPositiveConcept()).andReturn(positiveConcept);
		expect(openmrsBean.getMalariaRDTConcept()).andReturn(malariaRDTConcept);
		expect(openmrsBean.getACTTreatmentConcept()).andReturn(
				actTreatmentConcept);
		expect(openmrsBean.getCommentsConcept()).andReturn(commentsConcept);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean.recordOutpatientVisit(staff, facility, date, patient,
				serialNumber, insured, diagnosis, secondDiagnosis, rdtGiven,
				rdtPositive, actTreated, newCase, referred, comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(outpatientVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(9, obsSet.size());
		int verifiedObs = 0;
		for (Obs obs : obsSet) {
			assertEquals(patient, obs.getPerson());
			assertEquals(date, obs.getObsDatetime());
			assertEquals(facility, obs.getLocation());
			if (serialNumberConcept.equals(obs.getConcept())) {
				assertEquals(serialNumber, obs.getValueText());
				verifiedObs++;
			} else if (insuredConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (newCaseConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (diagnosisConcept.equals(obs.getConcept())) {
				assertEquals(new Double(diagnosis), obs.getValueNumeric());
				verifiedObs++;
			} else if (secondDiagnosisConcept.equals(obs.getConcept())) {
				assertEquals(new Double(secondDiagnosis), obs.getValueNumeric());
				verifiedObs++;
			} else if (referredConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (malariaRDTConcept.equals(obs.getConcept())) {
				assertEquals(positiveConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (actTreatmentConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (commentsConcept.equals(obs.getConcept())) {
				assertEquals(comments, obs.getValueText());
				verifiedObs++;
			} else {
				fail("Unexpected Obs concept: " + obs.getConcept());
			}
		}
		assertEquals("Missing expected obs", 9, verifiedObs);
	}

	public void testRecordOutpatientVisitRDTNegative() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		String serialNumber = "A12", comments = "Comment";
		Boolean insured = true, rdtGiven = true, rdtPositive = false, actTreated = true, newCase = true, referred = true;
		Integer diagnosis = 5, secondDiagnosis = 7;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 0, 0, 0);
		Date date = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getOutpatientVisitEncounterType()).andReturn(
				outpatientVisitType);

		expect(openmrsBean.getSerialNumberConcept()).andReturn(
				serialNumberConcept);
		expect(openmrsBean.getInsuredConcept()).andReturn(insuredConcept);
		expect(openmrsBean.getNewCaseConcept()).andReturn(newCaseConcept);
		expect(openmrsBean.getPrimaryDiagnosisConcept()).andReturn(
				diagnosisConcept);
		expect(openmrsBean.getSecondaryDiagnosisConcept()).andReturn(
				secondDiagnosisConcept);
		expect(openmrsBean.getReferredConcept()).andReturn(referredConcept);
		expect(openmrsBean.getNegativeConcept()).andReturn(negativeConcept);
		expect(openmrsBean.getMalariaRDTConcept()).andReturn(malariaRDTConcept);
		expect(openmrsBean.getACTTreatmentConcept()).andReturn(
				actTreatmentConcept);
		expect(openmrsBean.getCommentsConcept()).andReturn(commentsConcept);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean.recordOutpatientVisit(staff, facility, date, patient,
				serialNumber, insured, diagnosis, secondDiagnosis, rdtGiven,
				rdtPositive, actTreated, newCase, referred, comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(outpatientVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(9, obsSet.size());
		int verifiedObs = 0;
		for (Obs obs : obsSet) {
			assertEquals(patient, obs.getPerson());
			assertEquals(date, obs.getObsDatetime());
			assertEquals(facility, obs.getLocation());
			if (serialNumberConcept.equals(obs.getConcept())) {
				assertEquals(serialNumber, obs.getValueText());
				verifiedObs++;
			} else if (insuredConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (newCaseConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (diagnosisConcept.equals(obs.getConcept())) {
				assertEquals(new Double(diagnosis), obs.getValueNumeric());
				verifiedObs++;
			} else if (secondDiagnosisConcept.equals(obs.getConcept())) {
				assertEquals(new Double(secondDiagnosis), obs.getValueNumeric());
				verifiedObs++;
			} else if (referredConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (malariaRDTConcept.equals(obs.getConcept())) {
				assertEquals(negativeConcept, obs.getValueCoded());
				verifiedObs++;
			} else if (actTreatmentConcept.equals(obs.getConcept())) {
				assertEquals(1.0, obs.getValueNumeric());
				verifiedObs++;
			} else if (commentsConcept.equals(obs.getConcept())) {
				assertEquals(comments, obs.getValueText());
				verifiedObs++;
			} else {
				fail("Unexpected Obs concept: " + obs.getConcept());
			}
		}
		assertEquals("Missing expected obs", 9, verifiedObs);
	}

	public void testRecordOutpatientVisitEmpty() {
		User staff = new User(1);
		Location facility = new Location(2);
		Patient patient = new Patient(3);
		String serialNumber = null, comments = null;
		Boolean insured = null, rdtGiven = null, rdtPositive = null, actTreated = null, newCase = null, referred = null;
		Integer diagnosis = null, secondDiagnosis = null;

		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, 12, 1, 0, 0, 0);
		Date date = calendar.getTime();

		Capture<Encounter> encounterCap = new Capture<Encounter>();

		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(openmrsBean.getOutpatientVisitEncounterType()).andReturn(
				outpatientVisitType);

		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextService, encounterService, openmrsBean);

		regBean.recordOutpatientVisit(staff, facility, date, patient,
				serialNumber, insured, diagnosis, secondDiagnosis, rdtGiven,
				rdtPositive, actTreated, newCase, referred, comments);

		verify(contextService, encounterService, openmrsBean);

		Encounter encounter = encounterCap.getValue();
		assertEquals(patient, encounter.getPatient());
		assertEquals(staff, encounter.getProvider());
		assertEquals(facility, encounter.getLocation());
		assertEquals(date, encounter.getEncounterDatetime());
		assertEquals(outpatientVisitType, encounter.getEncounterType());

		Set<Obs> obsSet = encounter.getAllObs();
		assertEquals(0, obsSet.size());
	}

}
