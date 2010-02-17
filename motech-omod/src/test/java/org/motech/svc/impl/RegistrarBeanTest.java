package org.motech.svc.impl;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motech.messaging.MessageNotFoundException;
import org.motech.model.HIVStatus;
import org.motech.model.Log;
import org.motech.model.Message;
import org.motech.model.MessageProgramEnrollment;
import org.motech.model.MessageStatus;
import org.motech.model.ScheduledMessage;
import org.motech.model.TroubledPhone;
import org.motech.model.WhoRegistered;
import org.motech.model.WhyInterested;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;
import org.motech.svc.RegistrarBean;
import org.motech.util.GenderTypeConverter;
import org.motech.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
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

	Location ghanaLocation;
	PatientIdentifierType ghanaIdType;
	PersonAttributeType nurseIdAttributeType;
	PersonAttributeType primaryPhoneAttributeType;
	PersonAttributeType secondaryPhoneAttributeType;
	PersonAttributeType clinicAttributeType;
	PersonAttributeType nhisAttributeType;
	PersonAttributeType languageTextAttributeType;
	PersonAttributeType languageVoiceAttributeType;
	PersonAttributeType primaryPhoneTypeAttributeType;
	PersonAttributeType secondaryPhoneTypeAttributeType;
	PersonAttributeType mediaTypeInformationalAttributeType;
	PersonAttributeType mediaTypeReminderAttributeType;
	PersonAttributeType deliveryTimeAttributeType;
	PersonAttributeType nhisExpirationType;
	PersonAttributeType whoRegisteredType;
	PersonAttributeType ghsRegisteredAttributeType;
	PersonAttributeType ghsANCRegNumberAttributeType;
	PersonAttributeType ghsCWCRegNumberAttributeType;
	PersonAttributeType insuredAttributeType;
	PersonAttributeType hivStatusAttributeType;
	PersonAttributeType religionAttributeType;
	PersonAttributeType occupationAttributeType;
	PersonAttributeType howLearnedAttributeType;
	PersonAttributeType whyInterestedAttributeType;
	Role providerRole;
	EncounterType ancVisitType;
	EncounterType pncVisitType;
	EncounterType ppcVisitType;
	EncounterType pregnancyVisitType;
	EncounterType generalVisitType;
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
	ConceptName hivStatusNameObj;
	Concept hivStatusConcept;
	ConceptName abortiontypeNameObj;
	Concept abortiontypeConcept;
	ConceptName complicationNameObj;
	Concept complicationConcept;
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
	ConceptName referralNameObj;
	Concept referralConcept;
	ConceptName diagnosisNameObj;
	Concept diagnosisConcept;
	ConceptName secondDiagnosisNameObj;
	Concept secondDiagnosisConcept;
	ConceptName deliveyMethodNameObj;
	Concept deliveyMethodConcept;
	ConceptName deliveryLocationNameObj;
	Concept deliveryLocationConcept;
	ConceptName deliveredByNameObj;
	Concept deliveredByConcept;
	ConceptName deliveryOutcomeNameObj;
	Concept deliveryOutcomeConcept;
	ConceptName birthOutcomeNameObj;
	Concept birthOutcomeConcept;
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

		ghanaLocation = new Location(1);
		ghanaLocation.setName(MotechConstants.LOCATION_GHANA);

		ghanaIdType = new PatientIdentifierType(1);
		ghanaIdType.setName(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID);

		primaryPhoneAttributeType = new PersonAttributeType(2);
		primaryPhoneAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER);

		clinicAttributeType = new PersonAttributeType(3);
		clinicAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER);

		nhisAttributeType = new PersonAttributeType(4);
		nhisAttributeType.setName(MotechConstants.PERSON_ATTRIBUTE_NHIS_NUMBER);

		languageTextAttributeType = new PersonAttributeType(5);
		languageTextAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_TEXT);

		primaryPhoneTypeAttributeType = new PersonAttributeType(6);
		primaryPhoneTypeAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE);

		mediaTypeInformationalAttributeType = new PersonAttributeType(7);
		mediaTypeInformationalAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_INFORMATIONAL);

		deliveryTimeAttributeType = new PersonAttributeType(8);
		deliveryTimeAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);

		nurseIdAttributeType = new PersonAttributeType(9);
		nurseIdAttributeType.setName(MotechConstants.PERSON_ATTRIBUTE_CHPS_ID);

		secondaryPhoneAttributeType = new PersonAttributeType(10);
		secondaryPhoneAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER);

		secondaryPhoneTypeAttributeType = new PersonAttributeType(11);
		secondaryPhoneTypeAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE);

		nhisExpirationType = new PersonAttributeType(12);
		nhisExpirationType
				.setName(MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE);

		mediaTypeReminderAttributeType = new PersonAttributeType(13);
		mediaTypeReminderAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_REMINDER);

		languageVoiceAttributeType = new PersonAttributeType(14);
		languageVoiceAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_VOICE);

		whoRegisteredType = new PersonAttributeType(15);
		whoRegisteredType
				.setName(MotechConstants.PERSON_ATTRIBUTE_WHO_REGISTERED);

		ghsRegisteredAttributeType = new PersonAttributeType(16);
		ghsRegisteredAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_GHS_REGISTERED);

		ghsANCRegNumberAttributeType = new PersonAttributeType(17);
		ghsANCRegNumberAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_GHS_ANC_REG_NUMBER);

		insuredAttributeType = new PersonAttributeType(18);
		insuredAttributeType.setName(MotechConstants.PERSON_ATTRIBUTE_INSURED);

		hivStatusAttributeType = new PersonAttributeType(19);
		hivStatusAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_HIV_STATUS);

		religionAttributeType = new PersonAttributeType(20);
		religionAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_RELIGION);

		occupationAttributeType = new PersonAttributeType(21);
		occupationAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_OCCUPATION);

		ghsCWCRegNumberAttributeType = new PersonAttributeType(22);
		ghsCWCRegNumberAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_GHS_CWC_REG_NUMBER);

		howLearnedAttributeType = new PersonAttributeType(23);
		howLearnedAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED);

		whyInterestedAttributeType = new PersonAttributeType(24);
		whyInterestedAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_WHY_INTERESTED);

		providerRole = new Role(OpenmrsConstants.PROVIDER_ROLE);

		ancVisitType = new EncounterType(1);
		ancVisitType.setName(MotechConstants.ENCOUNTER_TYPE_ANCVISIT);

		pncVisitType = new EncounterType(2);
		pncVisitType.setName(MotechConstants.ENCOUNTER_TYPE_PNCVISIT);

		ppcVisitType = new EncounterType(3);
		ppcVisitType.setName(MotechConstants.ENCOUNTER_TYPE_PPCVISIT);

		pregnancyVisitType = new EncounterType(4);
		pregnancyVisitType
				.setName(MotechConstants.ENCOUNTER_TYPE_PREGNANCYVISIT);

		generalVisitType = new EncounterType(5);
		generalVisitType.setName(MotechConstants.ENCOUNTER_TYPE_GENERALVISIT);

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

		hivStatusNameObj = new ConceptName(MotechConstants.CONCEPT_HIV_STATUS,
				Locale.getDefault());
		hivStatusConcept = new Concept(26);

		abortiontypeNameObj = new ConceptName(
				MotechConstants.CONCEPT_ABORTIONTYPE, Locale.getDefault());
		abortiontypeConcept = new Concept(27);

		complicationNameObj = new ConceptName(
				MotechConstants.CONCEPT_COMPLICATION, Locale.getDefault());
		complicationConcept = new Concept(28);

		iptiNameObj = new ConceptName(
				MotechConstants.CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT_INFANTS,
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

		maternalDeathCauseNameObj = new ConceptName(
				MotechConstants.CONCEPT_MATERNAL_CAUSE_OF_DEATH, Locale
						.getDefault());
		maternalDeathCauseConcept = new Concept(34);

		serialNumberNameObj = new ConceptName(
				MotechConstants.CONCEPT_SERIAL_NUMBER, Locale.getDefault());
		serialNumberConcept = new Concept(35);

		newCaseNameObj = new ConceptName(MotechConstants.CONCEPT_NEW_CASE,
				Locale.getDefault());
		newCaseConcept = new Concept(36);

		referralNameObj = new ConceptName(MotechConstants.CONCEPT_REFERRAL,
				Locale.getDefault());
		referralConcept = new Concept(37);

		diagnosisNameObj = new ConceptName(
				MotechConstants.CONCEPT_PRIMARY_DIAGNOSIS, Locale.getDefault());
		diagnosisConcept = new Concept(38);

		secondDiagnosisNameObj = new ConceptName(
				MotechConstants.CONCEPT_SECONDARY_DIAGNOSIS, Locale
						.getDefault());
		secondDiagnosisConcept = new Concept(39);

		deliveyMethodNameObj = new ConceptName(
				MotechConstants.CONCEPT_DELIVERY_METHOD, Locale.getDefault());
		deliveyMethodConcept = new Concept(40);

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

		RegistrarBeanImpl regBeanImpl = new RegistrarBeanImpl();
		regBeanImpl.setContextService(contextService);

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
	}

	public void testRegisterChild() {

		Date childDob = new Date(874984), nhisExpires = new Date(3784784);
		String motherId = "PATIENT1234";
		String childId = "CHILD3783";
		Gender childGender = Gender.MALE;
		String childFirstName = "Harold";
		String nhis = "NHISNUMBER";
		Integer nurseInternalId = 1;

		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getLocationService()).andReturn(locationService)
				.atLeastOnce();

		User nurseUser = new User(nurseInternalId);

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();

		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_GHS_CWC_REG_NUMBER))
				.andReturn(ghsCWCRegNumberAttributeType);
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
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_WHO_REGISTERED))
				.andReturn(whoRegisteredType);

		expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
				.andReturn(ghanaLocation);
		expect(
				patientService
						.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID))
				.andReturn(ghanaIdType);

		Patient mother = new Patient();

		PatientIdentifier motherIdObj = new PatientIdentifier();
		motherIdObj.setIdentifierType(ghanaIdType);
		motherIdObj.setIdentifier(motherId);
		mother.addIdentifier(motherIdObj);

		PersonAddress testAddress = new PersonAddress();
		testAddress.setRegion("AREGION");
		testAddress.setCountyDistrict("ADISTRICT");
		mother.addAddress(testAddress);

		Capture<Patient> childCapture = new Capture<Patient>();
		expect(patientService.savePatient(capture(childCapture))).andReturn(
				new Patient());

		replay(contextService, personService, patientService, locationService);

		regBean.registerChild(nurseUser, mother, childId, childDob,
				childGender, childFirstName, nhis, nhisExpires);

		verify(contextService, personService, patientService, locationService);

		Patient child = childCapture.getValue();
		assertEquals(childId, child.getPatientIdentifier(ghanaIdType)
				.getIdentifier());
		assertEquals(testAddress.getRegion(), child.getPersonAddress()
				.getRegion());
		assertEquals(testAddress.getCountyDistrict(), child.getPersonAddress()
				.getCountyDistrict());
		assertEquals(testAddress.getCityVillage(), child.getPersonAddress()
				.getCityVillage());
		assertEquals(testAddress.getAddress1(), child.getPersonAddress()
				.getAddress1());
		assertEquals(childDob, child.getBirthdate());
		assertEquals(childFirstName, child.getGivenName());
		assertEquals(childGender, GenderTypeConverter.valueOfOpenMRS(child
				.getGender()));
		PersonAttribute nhisAttr = child.getAttribute(nhisAttributeType);
		assertEquals(nhis, nhisAttr.getValue());
		assertEquals(nhisExpires.toString(), child.getAttribute(
				nhisExpirationType).getHydratedObject());
		assertEquals(WhoRegistered.CHPS_STAFF, WhoRegistered.valueOf(child
				.getAttribute(whoRegisteredType).getValue()));
	}

	public void testRegisterClinic() {
		String clinicName = "A-Test-Clinic-Name";
		String description = "A Ghana Clinic Location";
		Integer clinicId = 3;
		Integer parentId = 2;

		String country = "Country";
		String region = "Region";
		String district = "District";
		String community = "Community";

		Location clinicparent = new Location(parentId);
		clinicparent.setCountry(country);
		clinicparent.setRegion(region);
		clinicparent.setCountyDistrict(district);
		clinicparent.setCityVillage(community);

		expect(contextService.getLocationService()).andReturn(locationService);

		Capture<Location> clinicCap = new Capture<Location>();
		Capture<Location> parentCap = new Capture<Location>();

		expect(locationService.saveLocation(capture(clinicCap))).andReturn(
				new Location(clinicId));
		expect(locationService.getLocation(parentId)).andReturn(clinicparent);
		expect(locationService.saveLocation(capture(parentCap))).andReturn(
				new Location());
		expect(locationService.saveLocation(capture(clinicCap))).andReturn(
				new Location());

		replay(contextService, locationService);

		regBean.registerClinic(clinicName, parentId);

		verify(contextService, locationService);

		Location capturedClinic = clinicCap.getValue();
		assertEquals(clinicName, capturedClinic.getName());
		assertEquals(description, capturedClinic.getDescription());

		assertEquals(country, capturedClinic.getCountry());
		assertEquals(region, capturedClinic.getRegion());
		assertEquals(district, capturedClinic.getCountyDistrict());
		assertEquals(community, capturedClinic.getCityVillage());
		assertEquals(clinicName, capturedClinic.getNeighborhoodCell());

		Location capturedParent = parentCap.getValue();
		Set<Location> childLocations = capturedParent.getChildLocations();
		assertEquals(1, childLocations.size());

		Location childLocation = childLocations.iterator().next();
		assertEquals(clinicName, childLocation.getName());
		assertEquals(parentId, childLocation.getParentLocation()
				.getLocationId());
	}

	public void testRegisterNurse() {

		String name = "Jenny", id = "123abc", phone = "12078675309", clinic = "Mayo Clinic";

		Location clinicLocation = new Location(1);
		clinicLocation.setName(clinic);

		Capture<User> nurseCap = new Capture<User>();

		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getUserService()).andReturn(userService);
		expect(contextService.getLocationService()).andReturn(locationService);

		expect(personService.parsePersonName(name)).andReturn(
				new PersonName(name, null, null));
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_CHPS_ID))
				.andReturn(nurseIdAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER))
				.andReturn(primaryPhoneAttributeType);
		expect(userService.getRole(OpenmrsConstants.PROVIDER_ROLE)).andReturn(
				providerRole);
		expect(locationService.getLocation(clinic)).andReturn(clinicLocation);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER))
				.andReturn(clinicAttributeType);
		expect(userService.saveUser(capture(nurseCap), (String) anyObject()))
				.andReturn(new User());

		replay(contextService, personService, userService, locationService);

		regBean.registerNurse(name, id, phone, clinic);

		verify(contextService, personService, userService, locationService);

		User nurse = nurseCap.getValue();
		assertEquals(name, nurse.getGivenName());
		assertEquals(id, nurse.getAttribute(nurseIdAttributeType).getValue());
		assertEquals(phone, nurse.getAttribute(primaryPhoneAttributeType)
				.getValue());
		assertEquals(clinicLocation.getLocationId().toString(), nurse
				.getAttribute(clinicAttributeType).getValue());
	}

	public void testRegisterPregnantMother() {
		String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
		String regNumberGHS = "123ABC", nhis = "456DEF";
		String region = "Region", district = "District", community = "Community", address = "Address";
		String religion = "Religion", occupation = "Occupation";
		String primaryPhone = "12075555555", secondaryPhone = "12075555556";
		String languageVoice = "LanguageVoice", languageText = "LanguageText";
		Date date = new Date();
		Boolean birthDateEst = true, registeredGHS = true, insured = true, dueDateConfirmed = true, registerPregProgram = true;
		Integer clinic = 1, gravida = 0, parity = 1;
		HIVStatus hivStatus = HIVStatus.UNKNOWN;
		ContactNumberType primaryPhoneType = ContactNumberType.PERSONAL, secondaryPhoneType = ContactNumberType.PUBLIC;
		MediaType mediaTypeInfo = MediaType.TEXT, mediaTypeReminder = MediaType.VOICE;
		WhoRegistered whoRegistered = WhoRegistered.CHPS_STAFF;

		String pregnancyProgramName = "Weekly Pregnancy Message Program";

		Patient patient = new Patient(2);
		Location ghanaLocation = new Location(1);

		Capture<Patient> patientCap = new Capture<Patient>();
		Capture<MessageProgramEnrollment> enrollmentCap = new Capture<MessageProgramEnrollment>();
		Capture<Obs> pregnancyObsCap = new Capture<Obs>();

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getLocationService()).andReturn(locationService)
				.atLeastOnce();
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(contextService.getObsService()).andReturn(obsService);
		expect(contextService.getConceptService()).andReturn(conceptService)
				.atLeastOnce();

		expect(
				patientService
						.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID))
				.andReturn(ghanaIdType);
		expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
				.andReturn(ghanaLocation);

		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_GHS_REGISTERED))
				.andReturn(ghsRegisteredAttributeType);
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
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER))
				.andReturn(clinicAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER))
				.andReturn(primaryPhoneAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE))
				.andReturn(primaryPhoneTypeAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER))
				.andReturn(secondaryPhoneAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE))
				.andReturn(secondaryPhoneTypeAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_INFORMATIONAL))
				.andReturn(mediaTypeInformationalAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_REMINDER))
				.andReturn(mediaTypeReminderAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_TEXT))
				.andReturn(languageTextAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_VOICE))
				.andReturn(languageVoiceAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_WHO_REGISTERED))
				.andReturn(whoRegisteredType);

		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_GHS_ANC_REG_NUMBER))
				.andReturn(ghsANCRegNumberAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HIV_STATUS))
				.andReturn(hivStatusAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_RELIGION))
				.andReturn(religionAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_OCCUPATION))
				.andReturn(occupationAttributeType);

		expect(patientService.savePatient(capture(patientCap))).andReturn(
				patient);

		expect(
				motechService.getActiveMessageProgramEnrollments(patient
						.getPatientId(), pregnancyProgramName)).andReturn(
				new ArrayList<MessageProgramEnrollment>());
		expect(
				motechService
						.saveMessageProgramEnrollment(capture(enrollmentCap)))
				.andReturn(new MessageProgramEnrollment());

		expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
				.andReturn(ghanaLocation);
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
		expect(conceptService.getConcept(MotechConstants.CONCEPT_GRAVIDA))
				.andReturn(gravidaConcept);
		expect(conceptService.getConcept(MotechConstants.CONCEPT_PARITY))
				.andReturn(parityConcept);
		expect(
				obsService.saveObs(capture(pregnancyObsCap),
						(String) anyObject())).andReturn(new Obs());

		replay(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService);

		regBean.registerPregnantMother(firstName, middleName, lastName,
				prefName, date, birthDateEst, registeredGHS, regNumberGHS,
				insured, nhis, date, region, district, community, address,
				clinic, date, dueDateConfirmed, gravida, parity, hivStatus,
				registerPregProgram, primaryPhone, primaryPhoneType,
				secondaryPhone, secondaryPhoneType, mediaTypeInfo,
				mediaTypeReminder, languageVoice, languageText, whoRegistered,
				religion, occupation);

		verify(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService);

		Patient capturedPatient = patientCap.getValue();
		assertEquals(regNumberGHS, capturedPatient.getPatientIdentifier(
				ghanaIdType).getIdentifier());
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
		assertEquals(region, capturedPatient.getPersonAddress().getRegion());
		assertEquals(district, capturedPatient.getPersonAddress()
				.getCountyDistrict());
		assertEquals(community, capturedPatient.getPersonAddress()
				.getCityVillage());
		assertEquals(address, capturedPatient.getPersonAddress().getAddress1());
		assertEquals(clinic, Integer.valueOf(capturedPatient.getAttribute(
				clinicAttributeType).getValue()));
		assertEquals(registeredGHS, Boolean.valueOf(capturedPatient
				.getAttribute(ghsRegisteredAttributeType).getValue()));
		assertEquals(regNumberGHS, capturedPatient.getAttribute(
				ghsANCRegNumberAttributeType).getValue());
		assertEquals(insured, Boolean.valueOf(capturedPatient.getAttribute(
				insuredAttributeType).getValue()));
		assertEquals(nhis, capturedPatient.getAttribute(nhisAttributeType)
				.getValue());
		assertEquals(date.toString(), capturedPatient.getAttribute(
				nhisExpirationType).getValue());
		assertEquals(hivStatus, HIVStatus.valueOf(capturedPatient.getAttribute(
				hivStatusAttributeType).getValue()));
		assertEquals(primaryPhone, capturedPatient.getAttribute(
				primaryPhoneAttributeType).getValue());
		assertEquals(secondaryPhone, capturedPatient.getAttribute(
				secondaryPhoneAttributeType).getValue());
		assertEquals(primaryPhoneType, ContactNumberType
				.valueOf(capturedPatient.getAttribute(
						primaryPhoneTypeAttributeType).getValue()));
		assertEquals(secondaryPhoneType, ContactNumberType
				.valueOf(capturedPatient.getAttribute(
						secondaryPhoneTypeAttributeType).getValue()));
		assertEquals(mediaTypeInfo, MediaType.valueOf(capturedPatient
				.getAttribute(mediaTypeInformationalAttributeType).getValue()));
		assertEquals(mediaTypeReminder, MediaType.valueOf(capturedPatient
				.getAttribute(mediaTypeReminderAttributeType).getValue()));
		assertEquals(languageText, capturedPatient.getAttribute(
				languageTextAttributeType).getValue());
		assertEquals(languageVoice, capturedPatient.getAttribute(
				languageVoiceAttributeType).getValue());
		assertEquals(whoRegistered, WhoRegistered.valueOf(capturedPatient
				.getAttribute(whoRegisteredType).getValue()));
		assertEquals(religion, capturedPatient.getAttribute(
				religionAttributeType).getValue());
		assertEquals(occupation, capturedPatient.getAttribute(
				occupationAttributeType).getValue());

		MessageProgramEnrollment enrollment = enrollmentCap.getValue();
		assertEquals(patient.getPatientId(), enrollment.getPersonId());
		assertEquals(pregnancyProgramName, enrollment.getProgram());
		assertNotNull("Enrollment start date should not be null", enrollment
				.getStartDate());
		assertNull("Enrollment end date should be null", enrollment
				.getEndDate());

		Obs pregnancyObs = pregnancyObsCap.getValue();
		assertNotNull(pregnancyObs.getObsDatetime());
		assertEquals(patient.getPatientId(), pregnancyObs.getPerson()
				.getPersonId());
		assertEquals(ghanaLocation, pregnancyObs.getLocation());
		assertEquals(pregConcept, pregnancyObs.getConcept());

		Set<Obs> pregnancyObsMembers = pregnancyObs.getGroupMembers();
		assertEquals(5, pregnancyObsMembers.size());

		boolean containsPregnancyStatusObs = false;
		boolean containsDueDateObs = false;
		boolean containsDueDateConfirmedObs = false;
		boolean containsGravidaObs = false;
		boolean containsParityObs = false;
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
			} else if (gravidaConcept.equals(memberObs.getConcept())) {
				containsGravidaObs = true;
				assertEquals(0.0, memberObs.getValueNumeric());
			} else if (parityConcept.equals(memberObs.getConcept())) {
				containsParityObs = true;
				assertEquals(1.0, memberObs.getValueNumeric());
			}
		}
		assertTrue("Pregnancy Status Obs missing", containsPregnancyStatusObs);
		assertTrue("Due Date Obs missing", containsDueDateObs);
		assertTrue("Due Date Confirmed Obs missing",
				containsDueDateConfirmedObs);
		assertTrue("Gravida Obs missing", containsGravidaObs);
		assertTrue("Parity Obs missing", containsParityObs);
	}

	@SuppressWarnings("unchecked")
	public void testRegisterChildWithProgram() {
		String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
		String regNumberGHS = "123ABC", nhis = "456DEF";
		String region = "Region", district = "District", community = "Community", address = "Address";
		String primaryPhone = "12075555555", secondaryPhone = "12075555556";
		String languageVoice = "LanguageVoice", languageText = "LanguageText";
		Date date = new Date();
		Boolean birthDateEst = true, registeredGHS = true, insured = true, registerPregProgram = true;
		String motherRegNum = "1234";
		Integer clinic = 1;
		ContactNumberType primaryPhoneType = ContactNumberType.PERSONAL, secondaryPhoneType = ContactNumberType.PUBLIC;
		MediaType mediaTypeInfo = MediaType.TEXT, mediaTypeReminder = MediaType.VOICE;
		WhoRegistered whoRegistered = WhoRegistered.CHPS_STAFF;
		Gender sex = Gender.FEMALE;

		String pregnancyProgramName = "Weekly Info Child Message Program";

		Patient child = new Patient(1);
		Patient mother = new Patient(2);
		Location ghanaLocation = new Location(1);

		Capture<Patient> patientCap = new Capture<Patient>();
		Capture<MessageProgramEnrollment> enrollmentCap = new Capture<MessageProgramEnrollment>();
		Capture<Relationship> relationshipCap = new Capture<Relationship>();

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getLocationService()).andReturn(locationService);
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();

		expect(
				patientService
						.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID))
				.andReturn(ghanaIdType);
		expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
				.andReturn(ghanaLocation);

		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_GHS_REGISTERED))
				.andReturn(ghsRegisteredAttributeType);

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
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER))
				.andReturn(clinicAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER))
				.andReturn(primaryPhoneAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE))
				.andReturn(primaryPhoneTypeAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER))
				.andReturn(secondaryPhoneAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE))
				.andReturn(secondaryPhoneTypeAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_INFORMATIONAL))
				.andReturn(mediaTypeInformationalAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_REMINDER))
				.andReturn(mediaTypeReminderAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_TEXT))
				.andReturn(languageTextAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_VOICE))
				.andReturn(languageVoiceAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_WHO_REGISTERED))
				.andReturn(whoRegisteredType);

		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_GHS_CWC_REG_NUMBER))
				.andReturn(ghsCWCRegNumberAttributeType);

		expect(patientService.savePatient(capture(patientCap)))
				.andReturn(child);

		expect(
				patientService
						.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID))
				.andReturn(ghanaIdType);
		List<Patient> motherList = new ArrayList<Patient>();
		motherList.add(mother);
		expect(
				patientService.getPatients((String) eq(null), eq(motherRegNum),
						(List) anyObject(), eq(true))).andReturn(motherList);

		// expect(patientService.getPatient(motherRegNum)).andReturn(mother);
		expect(
				personService
						.getRelationshipTypeByName(MotechConstants.RELATIONSHIP_TYPE_PARENT_CHILD))
				.andReturn(parentChildRelationshipType);
		expect(personService.saveRelationship(capture(relationshipCap)))
				.andReturn(new Relationship());

		expect(
				motechService.getActiveMessageProgramEnrollments(child
						.getPatientId(), pregnancyProgramName)).andReturn(
				new ArrayList<MessageProgramEnrollment>());
		expect(
				motechService
						.saveMessageProgramEnrollment(capture(enrollmentCap)))
				.andReturn(new MessageProgramEnrollment());

		replay(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService);

		regBean.registerChild(firstName, middleName, lastName, prefName, date,
				birthDateEst, sex, motherRegNum, registeredGHS, regNumberGHS,
				insured, nhis, date, region, district, community, address,
				clinic, registerPregProgram, primaryPhone, primaryPhoneType,
				secondaryPhone, secondaryPhoneType, mediaTypeInfo,
				mediaTypeReminder, languageVoice, languageText, whoRegistered);

		verify(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService);

		Patient capturedPatient = patientCap.getValue();
		assertEquals(regNumberGHS, capturedPatient.getPatientIdentifier(
				ghanaIdType).getIdentifier());
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
		assertEquals(GenderTypeConverter.toOpenMRSString(sex), capturedPatient
				.getGender());
		assertEquals(region, capturedPatient.getPersonAddress().getRegion());
		assertEquals(district, capturedPatient.getPersonAddress()
				.getCountyDistrict());
		assertEquals(community, capturedPatient.getPersonAddress()
				.getCityVillage());
		assertEquals(address, capturedPatient.getPersonAddress().getAddress1());
		assertEquals(clinic, Integer.valueOf(capturedPatient.getAttribute(
				clinicAttributeType).getValue()));
		assertEquals(registeredGHS, Boolean.valueOf(capturedPatient
				.getAttribute(ghsRegisteredAttributeType).getValue()));
		assertEquals(regNumberGHS, capturedPatient.getAttribute(
				ghsCWCRegNumberAttributeType).getValue());
		assertEquals(insured, Boolean.valueOf(capturedPatient.getAttribute(
				insuredAttributeType).getValue()));
		assertEquals(nhis, capturedPatient.getAttribute(nhisAttributeType)
				.getValue());
		assertEquals(date.toString(), capturedPatient.getAttribute(
				nhisExpirationType).getValue());
		assertEquals(primaryPhone, capturedPatient.getAttribute(
				primaryPhoneAttributeType).getValue());
		assertEquals(secondaryPhone, capturedPatient.getAttribute(
				secondaryPhoneAttributeType).getValue());
		assertEquals(primaryPhoneType, ContactNumberType
				.valueOf(capturedPatient.getAttribute(
						primaryPhoneTypeAttributeType).getValue()));
		assertEquals(secondaryPhoneType, ContactNumberType
				.valueOf(capturedPatient.getAttribute(
						secondaryPhoneTypeAttributeType).getValue()));
		assertEquals(mediaTypeInfo, MediaType.valueOf(capturedPatient
				.getAttribute(mediaTypeInformationalAttributeType).getValue()));
		assertEquals(mediaTypeReminder, MediaType.valueOf(capturedPatient
				.getAttribute(mediaTypeReminderAttributeType).getValue()));
		assertEquals(languageText, capturedPatient.getAttribute(
				languageTextAttributeType).getValue());
		assertEquals(languageVoice, capturedPatient.getAttribute(
				languageVoiceAttributeType).getValue());
		assertEquals(whoRegistered, WhoRegistered.valueOf(capturedPatient
				.getAttribute(whoRegisteredType).getValue()));

		MessageProgramEnrollment enrollment = enrollmentCap.getValue();
		assertEquals(child.getPatientId(), enrollment.getPersonId());
		assertEquals(pregnancyProgramName, enrollment.getProgram());
		assertNotNull("Enrollment start date should not be null", enrollment
				.getStartDate());
		assertNull("Enrollment end date should be null", enrollment
				.getEndDate());

		Relationship relationship = relationshipCap.getValue();
		assertEquals(parentChildRelationshipType, relationship
				.getRelationshipType());
		assertEquals(Integer.valueOf(2), relationship.getPersonA()
				.getPersonId());
		assertEquals(child.getPatientId(), relationship.getPersonB()
				.getPersonId());
	}

	public void testRegisterPerson() {
		String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
		String region = "Region", district = "District", community = "Community", address = "Address";
		String religion = "Religion", occupation = "Occupation";
		String primaryPhone = "12075555555", secondaryPhone = "12075555556";
		String languageVoice = "LanguageVoice", languageText = "LanguageText";
		String howLearned = "HowLearned";
		Date date = new Date();
		Boolean birthDateEst = true, registerPregProgram = true;
		Integer clinic = 1, messagesStartWeek = 23;
		Gender sex = Gender.FEMALE;
		ContactNumberType primaryPhoneType = ContactNumberType.PERSONAL, secondaryPhoneType = ContactNumberType.PUBLIC;
		MediaType mediaTypeInfo = MediaType.TEXT, mediaTypeReminder = MediaType.VOICE;
		WhyInterested whyInterested = WhyInterested.IN_HOUSEHOLD_PREGNANCY;

		String pregnancyProgramName = "Weekly Info Pregnancy Message Program";

		Person person = new Person(2);
		Location ghanaLocation = new Location(1);

		Capture<Person> personCap = new Capture<Person>();
		Capture<MessageProgramEnrollment> enrollmentCap = new Capture<MessageProgramEnrollment>();
		Capture<Obs> refDateObsCap = new Capture<Obs>();

		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getLocationService()).andReturn(locationService)
				.atLeastOnce();
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(contextService.getObsService()).andReturn(obsService);
		expect(contextService.getConceptService()).andReturn(conceptService)
				.atLeastOnce();

		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER))
				.andReturn(clinicAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER))
				.andReturn(primaryPhoneAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE))
				.andReturn(primaryPhoneTypeAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER))
				.andReturn(secondaryPhoneAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE))
				.andReturn(secondaryPhoneTypeAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_INFORMATIONAL))
				.andReturn(mediaTypeInformationalAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_REMINDER))
				.andReturn(mediaTypeReminderAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_TEXT))
				.andReturn(languageTextAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_VOICE))
				.andReturn(languageVoiceAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_RELIGION))
				.andReturn(religionAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_OCCUPATION))
				.andReturn(occupationAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED))
				.andReturn(howLearnedAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_WHY_INTERESTED))
				.andReturn(whyInterestedAttributeType);

		expect(personService.savePerson(capture(personCap))).andReturn(person);

		expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
				.andReturn(ghanaLocation);
		expect(
				conceptService
						.getConcept(MotechConstants.CONCEPT_ENROLLMENT_REFERENCE_DATE))
				.andReturn(refDateConcept);
		expect(obsService.saveObs(capture(refDateObsCap), (String) anyObject()))
				.andReturn(new Obs());

		expect(
				motechService.getActiveMessageProgramEnrollments(person
						.getPersonId(), pregnancyProgramName)).andReturn(
				new ArrayList<MessageProgramEnrollment>());
		expect(
				motechService
						.saveMessageProgramEnrollment(capture(enrollmentCap)))
				.andReturn(new MessageProgramEnrollment());

		replay(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService);

		regBean.registerPerson(firstName, middleName, lastName, prefName, date,
				birthDateEst, sex, region, district, community, address,
				clinic, registerPregProgram, messagesStartWeek, primaryPhone,
				primaryPhoneType, secondaryPhone, secondaryPhoneType,
				mediaTypeInfo, mediaTypeReminder, languageVoice, languageText,
				howLearned, religion, occupation, whyInterested);

		verify(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService);

		Person capturedPerson = personCap.getValue();
		assertEquals(prefName, capturedPerson.getGivenName());
		assertEquals(lastName, capturedPerson.getFamilyName());
		assertEquals(middleName, capturedPerson.getMiddleName());
		Iterator<PersonName> names = capturedPerson.getNames().iterator();
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
		assertEquals(date, capturedPerson.getBirthdate());
		assertEquals(birthDateEst, capturedPerson.getBirthdateEstimated());
		assertEquals(GenderTypeConverter.toOpenMRSString(Gender.FEMALE),
				capturedPerson.getGender());
		assertEquals(region, capturedPerson.getPersonAddress().getRegion());
		assertEquals(district, capturedPerson.getPersonAddress()
				.getCountyDistrict());
		assertEquals(community, capturedPerson.getPersonAddress()
				.getCityVillage());
		assertEquals(address, capturedPerson.getPersonAddress().getAddress1());
		assertEquals(clinic, Integer.valueOf(capturedPerson.getAttribute(
				clinicAttributeType).getValue()));
		assertEquals(primaryPhone, capturedPerson.getAttribute(
				primaryPhoneAttributeType).getValue());
		assertEquals(secondaryPhone, capturedPerson.getAttribute(
				secondaryPhoneAttributeType).getValue());
		assertEquals(primaryPhoneType, ContactNumberType.valueOf(capturedPerson
				.getAttribute(primaryPhoneTypeAttributeType).getValue()));
		assertEquals(secondaryPhoneType, ContactNumberType
				.valueOf(capturedPerson.getAttribute(
						secondaryPhoneTypeAttributeType).getValue()));
		assertEquals(mediaTypeInfo, MediaType.valueOf(capturedPerson
				.getAttribute(mediaTypeInformationalAttributeType).getValue()));
		assertEquals(mediaTypeReminder, MediaType.valueOf(capturedPerson
				.getAttribute(mediaTypeReminderAttributeType).getValue()));
		assertEquals(languageText, capturedPerson.getAttribute(
				languageTextAttributeType).getValue());
		assertEquals(languageVoice, capturedPerson.getAttribute(
				languageVoiceAttributeType).getValue());
		assertEquals(religion, capturedPerson.getAttribute(
				religionAttributeType).getValue());
		assertEquals(occupation, capturedPerson.getAttribute(
				occupationAttributeType).getValue());
		assertEquals(howLearned, capturedPerson.getAttribute(
				howLearnedAttributeType).getValue());
		assertEquals(whyInterested, WhyInterested.valueOf(capturedPerson
				.getAttribute(whyInterestedAttributeType).getValue()));

		MessageProgramEnrollment enrollment = enrollmentCap.getValue();
		assertEquals(person.getPersonId(), enrollment.getPersonId());
		assertEquals(pregnancyProgramName, enrollment.getProgram());
		assertNotNull("Enrollment start date should not be null", enrollment
				.getStartDate());
		assertNull("Enrollment end date should be null", enrollment
				.getEndDate());

		Obs refDateObs = refDateObsCap.getValue();
		assertEquals(person.getPersonId(), refDateObs.getPersonId());
		assertEquals(ghanaLocation, refDateObs.getLocation());
		assertEquals(refDateConcept, refDateObs.getConcept());
		assertNotNull("Enrollment reference date value is null", refDateObs
				.getValueDatetime());
	}

	public void testEditPatient() {

		Integer patientId = 1;
		String pPhone = "12075551212", sPhone = "120773733373", nhis = "28";
		Date nhisExpires = new Date();
		ContactNumberType pPhoneType = ContactNumberType.PERSONAL;
		ContactNumberType sPhoneType = ContactNumberType.HOUSEHOLD;

		User nurse = new User(2);
		Patient patient = new Patient(patientId);

		Capture<Patient> patientCap = new Capture<Patient>();

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();

		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER))
				.andReturn(primaryPhoneAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE))
				.andReturn(primaryPhoneTypeAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER))
				.andReturn(secondaryPhoneAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE))
				.andReturn(secondaryPhoneTypeAttributeType);
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

		replay(contextService, patientService, personService);

		regBean.editPatient(nurse, patient, pPhone, pPhoneType, sPhone,
				sPhoneType, nhis, nhisExpires);

		verify(contextService, patientService, personService);

		Patient capturedPatient = patientCap.getValue();
		assertEquals(pPhone, capturedPatient.getAttribute(
				primaryPhoneAttributeType).getValue());
		assertEquals(pPhoneType.toString(), capturedPatient.getAttribute(
				primaryPhoneTypeAttributeType).getValue());
		assertEquals(sPhone, capturedPatient.getAttribute(
				secondaryPhoneAttributeType).getValue());
		assertEquals(sPhoneType.toString(), capturedPatient.getAttribute(
				secondaryPhoneTypeAttributeType).getValue());
		assertEquals(nhis, capturedPatient.getAttribute(nhisAttributeType)
				.getValue());
		assertEquals(nhisExpires.toString(), capturedPatient.getAttribute(
				nhisExpirationType).getValue());
	}

	public void testEditPatientAll() {
		Integer patientId = 2, clinic = 1;
		String firstName = "FirstName", lastName = "LastName", prefName = "PrefName";
		String regNumberGHS = "123ABC", nhis = "456DEF";
		String region = "Region", district = "District", community = "Community", address = "Address";
		String primaryPhone = "12075555555", secondaryPhone = "12075555556";
		String languageVoice = "LanguageVoice", languageText = "LanguageText";
		String religion = "Religion", occupation = "Occupation";
		Date date = new Date();
		Gender sex = Gender.FEMALE;
		Boolean birthDateEst = true, registeredGHS = true, insured = true;
		ContactNumberType primaryPhoneType = ContactNumberType.PERSONAL, secondaryPhoneType = ContactNumberType.PUBLIC;
		MediaType mediaTypeInfo = MediaType.TEXT, mediaTypeReminder = MediaType.VOICE;
		HIVStatus hivStatus = HIVStatus.NEGATIVE;

		Patient patient = new Patient(patientId);
		Location ghanaLocation = new Location(1);

		Capture<Patient> patientCap = new Capture<Patient>();

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getLocationService()).andReturn(locationService)
				.atLeastOnce();

		expect(patientService.getPatient(patientId)).andReturn(patient);

		expect(
				patientService
						.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_GHANA_CLINIC_ID))
				.andReturn(ghanaIdType);
		expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
				.andReturn(ghanaLocation);

		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_GHS_REGISTERED))
				.andReturn(ghsRegisteredAttributeType);
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
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HEALTH_CENTER))
				.andReturn(clinicAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER))
				.andReturn(primaryPhoneAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE))
				.andReturn(primaryPhoneTypeAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER))
				.andReturn(secondaryPhoneAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE))
				.andReturn(secondaryPhoneTypeAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_INFORMATIONAL))
				.andReturn(mediaTypeInformationalAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_REMINDER))
				.andReturn(mediaTypeReminderAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_VOICE))
				.andReturn(languageVoiceAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_TEXT))
				.andReturn(languageTextAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_RELIGION))
				.andReturn(religionAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_OCCUPATION))
				.andReturn(occupationAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HIV_STATUS))
				.andReturn(hivStatusAttributeType);

		expect(patientService.savePatient(capture(patientCap))).andReturn(
				patient);

		replay(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService);

		regBean.editPatient(patientId, firstName, lastName, prefName, date,
				birthDateEst, sex, registeredGHS, regNumberGHS, insured, nhis,
				date, region, district, community, address, clinic,
				primaryPhone, primaryPhoneType, secondaryPhone,
				secondaryPhoneType, mediaTypeInfo, mediaTypeReminder,
				languageVoice, languageText, religion, occupation, hivStatus);

		verify(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService);

		Patient capturedPatient = patientCap.getValue();
		assertEquals(date, capturedPatient.getBirthdate());
		assertEquals(birthDateEst, capturedPatient.getBirthdateEstimated());
		assertEquals(sex, GenderTypeConverter.valueOfOpenMRS(capturedPatient
				.getGender()));
		assertEquals(firstName, capturedPatient.getGivenName());
		assertEquals(lastName, capturedPatient.getFamilyName());
		assertEquals(prefName, capturedPatient.getMiddleName());
		assertEquals(region, capturedPatient.getPersonAddress().getRegion());
		assertEquals(district, capturedPatient.getPersonAddress()
				.getCountyDistrict());
		assertEquals(community, capturedPatient.getPersonAddress()
				.getCityVillage());
		assertEquals(address, capturedPatient.getPersonAddress().getAddress1());
		assertEquals(regNumberGHS, capturedPatient.getPatientIdentifier(
				ghanaIdType).getIdentifier());
		assertEquals(registeredGHS, Boolean.valueOf(capturedPatient
				.getAttribute(ghsRegisteredAttributeType).getValue()));
		assertEquals(insured, Boolean.valueOf(capturedPatient.getAttribute(
				insuredAttributeType).getValue()));
		assertEquals(nhis, capturedPatient.getAttribute(nhisAttributeType)
				.getValue());
		assertEquals(date.toString(), capturedPatient.getAttribute(
				nhisExpirationType).getValue());
		assertEquals(clinic, Integer.valueOf(capturedPatient.getAttribute(
				clinicAttributeType).getValue()));
		assertEquals(primaryPhone, capturedPatient.getAttribute(
				primaryPhoneAttributeType).getValue());
		assertEquals(secondaryPhone, capturedPatient.getAttribute(
				secondaryPhoneAttributeType).getValue());
		assertEquals(primaryPhoneType, ContactNumberType
				.valueOf(capturedPatient.getAttribute(
						primaryPhoneTypeAttributeType).getValue()));
		assertEquals(secondaryPhoneType, ContactNumberType
				.valueOf(capturedPatient.getAttribute(
						secondaryPhoneTypeAttributeType).getValue()));
		assertEquals(mediaTypeInfo, MediaType.valueOf(capturedPatient
				.getAttribute(mediaTypeInformationalAttributeType).getValue()));
		assertEquals(mediaTypeReminder, MediaType.valueOf(capturedPatient
				.getAttribute(mediaTypeReminderAttributeType).getValue()));
		assertEquals(languageText, capturedPatient.getAttribute(
				languageTextAttributeType).getValue());
		assertEquals(languageVoice, capturedPatient.getAttribute(
				languageVoiceAttributeType).getValue());
		assertEquals(religion, capturedPatient.getAttribute(
				religionAttributeType).getValue());
		assertEquals(occupation, capturedPatient.getAttribute(
				occupationAttributeType).getValue());
		assertEquals(hivStatus, HIVStatus.valueOf(capturedPatient.getAttribute(
				hivStatusAttributeType).getValue()));
	}

	public void testStopPregnancyProgram() {

		String pregnancyProgram1 = "Weekly Pregnancy Message Program", pregnancyProgram2 = "Weekly Info Pregnancy Message Program";

		User nurse = new User(3);
		Integer patientId = 2;
		Patient patient = new Patient(patientId);

		Capture<MessageProgramEnrollment> enrollmentCapture = new Capture<MessageProgramEnrollment>();
		Capture<Message> messageCapture = new Capture<Message>();

		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();

		List<MessageProgramEnrollment> enrollments = new ArrayList<MessageProgramEnrollment>();
		MessageProgramEnrollment enrollment = new MessageProgramEnrollment();
		enrollments.add(enrollment);

		List<Message> messages = new ArrayList<Message>();
		Message message = new Message();
		messages.add(message);

		expect(
				motechService.getActiveMessageProgramEnrollments(patientId,
						pregnancyProgram1)).andReturn(
				new ArrayList<MessageProgramEnrollment>());
		expect(
				motechService.getActiveMessageProgramEnrollments(patientId,
						pregnancyProgram2)).andReturn(enrollments);
		expect(
				motechService.getMessages(enrollment,
						MessageStatus.SHOULD_ATTEMPT)).andReturn(messages);
		expect(motechService.saveMessage(capture(messageCapture))).andReturn(
				new Message());
		expect(
				motechService
						.saveMessageProgramEnrollment(capture(enrollmentCapture)))
				.andReturn(new MessageProgramEnrollment());

		replay(contextService, motechService);

		regBean.stopPregnancyProgram(nurse, patient);

		verify(contextService, motechService);

		MessageProgramEnrollment enrollmentCaptured = enrollmentCapture
				.getValue();
		assertNotNull("Enrollment end date must be set", enrollmentCaptured
				.getEndDate());

		Message messageCaptured = messageCapture.getValue();
		assertEquals(MessageStatus.CANCELLED, messageCaptured
				.getAttemptStatus());
	}

	public void testRegisterPregnancy() {
		Integer patientId = 2;
		Date date = new Date();
		Boolean dueDateConfirmed = true, registerPregProgram = true;
		String primaryPhone = "12075555555", secondaryPhone = "12075555556";
		String languageVoice = "LanguageVoice", languageText = "LanguageText";
		ContactNumberType primaryPhoneType = ContactNumberType.PERSONAL, secondaryPhoneType = ContactNumberType.PUBLIC;
		MediaType mediaTypeInfo = MediaType.TEXT, mediaTypeReminder = MediaType.VOICE;
		WhoRegistered whoRegistered = WhoRegistered.CHPS_STAFF;
		String howLearned = "HowLearned";

		String pregnancyProgramName = "Weekly Pregnancy Message Program";

		Patient patient = new Patient(patientId);
		Location ghanaLocation = new Location(1);

		Capture<Patient> patientCap = new Capture<Patient>();
		Capture<MessageProgramEnrollment> enrollmentCap = new Capture<MessageProgramEnrollment>();
		Capture<Obs> pregnancyObsCap = new Capture<Obs>();

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(contextService.getLocationService()).andReturn(locationService)
				.atLeastOnce();
		expect(contextService.getObsService()).andReturn(obsService);
		expect(contextService.getConceptService()).andReturn(conceptService)
				.atLeastOnce();

		expect(patientService.getPatient(patientId)).andReturn(patient);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER))
				.andReturn(primaryPhoneAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE))
				.andReturn(primaryPhoneTypeAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER))
				.andReturn(secondaryPhoneAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE))
				.andReturn(secondaryPhoneTypeAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_INFORMATIONAL))
				.andReturn(mediaTypeInformationalAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE_REMINDER))
				.andReturn(mediaTypeReminderAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_TEXT))
				.andReturn(languageTextAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_LANGUAGE_VOICE))
				.andReturn(languageVoiceAttributeType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_WHO_REGISTERED))
				.andReturn(whoRegisteredType);
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED))
				.andReturn(howLearnedAttributeType);
		expect(patientService.savePatient(capture(patientCap))).andReturn(
				new Patient());

		expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
				.andReturn(ghanaLocation);
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
				motechService.getActiveMessageProgramEnrollments(patient
						.getPatientId(), pregnancyProgramName)).andReturn(
				new ArrayList<MessageProgramEnrollment>());
		expect(
				motechService
						.saveMessageProgramEnrollment(capture(enrollmentCap)))
				.andReturn(new MessageProgramEnrollment());

		replay(contextService, patientService, motechService, personService,
				locationService, encounterService, obsService, conceptService,
				userService);

		regBean.registerPregnancy(patientId, date, dueDateConfirmed,
				registerPregProgram, primaryPhone, primaryPhoneType,
				secondaryPhone, secondaryPhoneType, mediaTypeInfo,
				mediaTypeReminder, languageVoice, languageText, whoRegistered,
				howLearned);

		verify(contextService, patientService, motechService, personService,
				locationService, encounterService, obsService, conceptService,
				userService);

		Patient capturedPatient = patientCap.getValue();
		assertEquals(primaryPhone, capturedPatient.getAttribute(
				primaryPhoneAttributeType).getValue());
		assertEquals(secondaryPhone, capturedPatient.getAttribute(
				secondaryPhoneAttributeType).getValue());
		assertEquals(primaryPhoneType, ContactNumberType
				.valueOf(capturedPatient.getAttribute(
						primaryPhoneTypeAttributeType).getValue()));
		assertEquals(secondaryPhoneType, ContactNumberType
				.valueOf(capturedPatient.getAttribute(
						secondaryPhoneTypeAttributeType).getValue()));
		assertEquals(mediaTypeInfo, MediaType.valueOf(capturedPatient
				.getAttribute(mediaTypeInformationalAttributeType).getValue()));
		assertEquals(mediaTypeReminder, MediaType.valueOf(capturedPatient
				.getAttribute(mediaTypeReminderAttributeType).getValue()));
		assertEquals(languageText, capturedPatient.getAttribute(
				languageTextAttributeType).getValue());
		assertEquals(languageVoice, capturedPatient.getAttribute(
				languageVoiceAttributeType).getValue());
		assertEquals(whoRegistered, WhoRegistered.valueOf(capturedPatient
				.getAttribute(whoRegisteredType).getValue()));
		assertEquals(howLearned, capturedPatient.getAttribute(
				howLearnedAttributeType).getValue());

		MessageProgramEnrollment enrollment = enrollmentCap.getValue();
		assertEquals(patient.getPatientId(), enrollment.getPersonId());
		assertEquals(pregnancyProgramName, enrollment.getProgram());
		assertNotNull("Enrollment start date should not be null", enrollment
				.getStartDate());
		assertNull("Enrollment end date should be null", enrollment
				.getEndDate());

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

	public void testLog() {
		LogType type = LogType.FAILURE;
		String message = "A simple message";
		Date beforeCall = new Date();

		Capture<Log> logCap = new Capture<Log>();

		expect(contextService.getMotechService()).andReturn(motechService);

		expect(motechService.saveLog(capture(logCap))).andReturn(new Log());

		replay(contextService, motechService);

		regBean.log(type, message);

		verify(contextService, motechService);

		Log log = logCap.getValue();
		Date logDate = log.getDate();
		Date afterCall = new Date();

		assertEquals(type, log.getType());
		assertEquals(message, log.getMessage());
		assertNotNull("Date is null", logDate);
		assertFalse("Date not between invocation and return", logDate
				.before(beforeCall)
				|| logDate.after(afterCall));
	}

	public void testLogMessageTrim() {
		LogType type = LogType.SUCCESS;
		// String length trimmed from 306 to 255 characters (max length for log
		// message)
		String originalMessage = "This message is just too long and will be trimmed. "
				+ "This message is just too long and will be trimmed. "
				+ "This message is just too long and will be trimmed. "
				+ "This message is just too long and will be trimmed. "
				+ "This message is just too long and will be trimmed. "
				+ "This message is just too long and will be trimmed. ";
		String trimmedMessage = "This message is just too long and will be trimmed. "
				+ "This message is just too long and will be trimmed. "
				+ "This message is just too long and will be trimmed. "
				+ "This message is just too long and will be trimmed. "
				+ "This message is just too long and will be trimmed. ";
		Date beforeCall = new Date();

		Capture<Log> logCap = new Capture<Log>();

		expect(contextService.getMotechService()).andReturn(motechService);

		expect(motechService.saveLog(capture(logCap))).andReturn(new Log());

		replay(contextService, motechService);

		regBean.log(type, originalMessage);

		verify(contextService, motechService);

		Log log = logCap.getValue();
		Date logDate = log.getDate();
		Date afterCall = new Date();

		assertEquals(type, log.getType());
		assertEquals(trimmedMessage, log.getMessage());
		assertNotNull("Date is null", logDate);
		assertFalse("Date not between invocation and return", logDate
				.before(beforeCall)
				|| logDate.after(afterCall));
	}

	public void testSetMessageStatusSuccessMessageFoundNotTroubled() {
		String messageId = "12345678-1234-1234-1234-123456789012";
		Boolean success = true;

		Integer recipientId = 2;
		String phoneNumber = "1234567890";
		Person recipient = new Person();
		recipient.addAttribute(new PersonAttribute(primaryPhoneAttributeType,
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
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER))
				.andReturn(primaryPhoneAttributeType);
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

	public void testSetMessageStatusSuccessMessageFoundTroubled() {
		String messageId = "12345678-1234-1234-1234-123456789012";
		Boolean success = true;

		Integer recipientId = 2;
		String phoneNumber = "1234567890";
		Person recipient = new Person();
		recipient.addAttribute(new PersonAttribute(primaryPhoneAttributeType,
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
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER))
				.andReturn(primaryPhoneAttributeType);
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

	public void testSetMessageStatusFailureMessageFoundNotTroubled() {
		String messageId = "12345678-1234-1234-1234-123456789012";
		Boolean success = false;

		Integer recipientId = 2;
		String phoneNumber = "1234567890";
		Person recipient = new Person();
		recipient.addAttribute(new PersonAttribute(primaryPhoneAttributeType,
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
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER))
				.andReturn(primaryPhoneAttributeType);
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

	public void testSetMessageStatusFailureMessageFoundTroubled() {
		String messageId = "12345678-1234-1234-1234-123456789012";
		Boolean success = false;

		Integer recipientId = 2;
		String phoneNumber = "1234567890";
		Person recipient = new Person();
		recipient.addAttribute(new PersonAttribute(primaryPhoneAttributeType,
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
		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER))
				.andReturn(primaryPhoneAttributeType);
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

	public void testSetMessageStatusMessageNotFound() {
		String messageId = "12345678-1234-1234-1234-123456789012";
		Boolean success = true;

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(contextService.getPersonService()).andReturn(personService);
		expect(motechService.getMessage(messageId)).andReturn(null);

		replay(contextService, motechService, personService);

		try {
			regBean.setMessageStatus(messageId, success);
			fail("Expected org.motech.messaging.MessageNotFoundException: none thrown");
		} catch (MessageNotFoundException e) {

		} catch (Exception e) {
			fail("Expected org.motech.messaging.MessageNotFoundException: other thrown");
		}

		verify(contextService, motechService, personService);
	}
}
