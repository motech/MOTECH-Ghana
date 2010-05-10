package org.motechproject.server.svc.impl;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motechproject.server.messaging.MessageNotFoundException;
import org.motechproject.server.model.HIVStatus;
import org.motechproject.server.model.Log;
import org.motechproject.server.model.Message;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.MessageStatus;
import org.motechproject.server.model.ScheduledMessage;
import org.motechproject.server.model.TroubledPhone;
import org.motechproject.server.model.WhoRegistered;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
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
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.service.IdentifierSourceService;
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
	IdentifierSourceService idService;

	Location ghanaLocation;
	PatientIdentifierType motechIdType;
	PersonAttributeType nurseIdAttributeType;
	PersonAttributeType phoneAttributeType;
	PersonAttributeType communityAttributeType;
	PersonAttributeType nhisAttributeType;
	PersonAttributeType languageAttributeType;
	PersonAttributeType phoneTypeAttributeType;
	PersonAttributeType mediaTypeAttributeType;
	PersonAttributeType deliveryTimeAttributeType;
	PersonAttributeType deliveryDayAttributeType;
	PersonAttributeType nhisExpirationType;
	PersonAttributeType insuredAttributeType;
	PersonAttributeType howLearnedAttributeType;
	PersonAttributeType whyInterestedAttributeType;
	Role providerRole;
	EncounterType ancVisitType;
	EncounterType pncChildVisitType;
	EncounterType pncMotherVisitType;
	EncounterType pregnancyRegVisitType;
	EncounterType pregnancyTermVisitType;
	EncounterType pregnancyDelVisitType;
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
		idService = createMock(IdentifierSourceService.class);

		ghanaLocation = new Location(1);
		ghanaLocation.setName(MotechConstants.LOCATION_GHANA);

		motechIdType = new PatientIdentifierType(1);
		motechIdType.setName(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID);

		phoneAttributeType = new PersonAttributeType(2);
		phoneAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);

		communityAttributeType = new PersonAttributeType(3);
		communityAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_COMMUNITY);

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

		nurseIdAttributeType = new PersonAttributeType(9);
		nurseIdAttributeType.setName(MotechConstants.PERSON_ATTRIBUTE_CHPS_ID);

		nhisExpirationType = new PersonAttributeType(12);
		nhisExpirationType
				.setName(MotechConstants.PERSON_ATTRIBUTE_NHIS_EXP_DATE);

		insuredAttributeType = new PersonAttributeType(18);
		insuredAttributeType.setName(MotechConstants.PERSON_ATTRIBUTE_INSURED);

		howLearnedAttributeType = new PersonAttributeType(23);
		howLearnedAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_HOW_LEARNED);

		whyInterestedAttributeType = new PersonAttributeType(24);
		whyInterestedAttributeType
				.setName(MotechConstants.PERSON_ATTRIBUTE_WHY_INTERESTED);

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

		generalVisitType = new EncounterType(5);
		generalVisitType.setName(MotechConstants.ENCOUNTER_TYPE_GENERALVISIT);

		pregnancyTermVisitType = new EncounterType(6);
		pregnancyTermVisitType
				.setName(MotechConstants.ENCOUNTER_TYPE_PREGTERMVISIT);

		pregnancyDelVisitType = new EncounterType(7);
		pregnancyDelVisitType
				.setName(MotechConstants.ENCOUNTER_TYPE_PREGDELVISIT);

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
		idService = null;
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
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER))
				.andReturn(phoneAttributeType);
		expect(userService.getRole(OpenmrsConstants.PROVIDER_ROLE)).andReturn(
				providerRole);
		expect(locationService.getLocation(clinic)).andReturn(clinicLocation);

		expect(userService.saveUser(capture(nurseCap), (String) anyObject()))
				.andReturn(new User());

		replay(contextService, personService, userService, locationService);

		regBean.registerNurse(name, id, phone, clinic);

		verify(contextService, personService, userService, locationService);

		User nurse = nurseCap.getValue();
		assertEquals(name, nurse.getGivenName());
		assertEquals(id, nurse.getAttribute(nurseIdAttributeType).getValue());
		assertEquals(phone, nurse.getAttribute(phoneAttributeType).getValue());
	}

	public void testRegisterPregnantMother() throws ParseException {
		Integer motechId = 123456;
		String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
		String nhis = "456DEF";
		String address = "Address";
		Integer phoneNumber = 2075555555;
		String language = "Language";
		Date date = new Date();
		Boolean birthDateEst = true, insured = true, dueDateConfirmed = true, enroll = true, consent = true;
		Integer gravida = 0, parity = 1, community = 123;
		Gender gender = Gender.FEMALE;
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType mediaType = MediaType.TEXT;
		DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.CURRENTLY_PREGNANT;
		HowLearned howLearned = HowLearned.FRIEND;

		String pregnancyProgramName = "Weekly Pregnancy Message Program";
		String careProgramName = "Expected Care Message Program";

		Patient patient = new Patient(2);
		Location ghanaLocation = new Location(1);

		Capture<Patient> patientCap = new Capture<Patient>();
		Capture<MessageProgramEnrollment> enrollment1Cap = new Capture<MessageProgramEnrollment>();
		Capture<MessageProgramEnrollment> enrollment2Cap = new Capture<MessageProgramEnrollment>();
		Capture<Encounter> pregnancyEncounterCap = new Capture<Encounter>();
		Capture<Obs> pregnancyObsCap = new Capture<Obs>();

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getLocationService()).andReturn(locationService)
				.atLeastOnce();
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(contextService.getEncounterService())
				.andReturn(encounterService).atLeastOnce();
		expect(contextService.getObsService()).andReturn(obsService);
		expect(contextService.getConceptService()).andReturn(conceptService)
				.atLeastOnce();
		expect(contextService.getIdentifierSourceService())
				.andReturn(idService).atLeastOnce();

		expect(idService.getAllIdentifierSources(false)).andReturn(
				new ArrayList<IdentifierSource>());
		expect(contextService.getAuthenticatedUser()).andReturn(new User());
		expect(idService.saveLogEntry((LogEntry) anyObject())).andReturn(
				new LogEntry());
		expect(
				patientService
						.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID))
				.andReturn(motechIdType).atLeastOnce();
		expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
				.andReturn(ghanaLocation);

		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_COMMUNITY))
				.andReturn(communityAttributeType);
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
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_WHY_INTERESTED))
				.andReturn(whyInterestedAttributeType);

		expect(patientService.savePatient(capture(patientCap))).andReturn(
				patient);

		expect(
				motechService.getActiveMessageProgramEnrollments(patient
						.getPatientId(), pregnancyProgramName, null))
				.andReturn(new ArrayList<MessageProgramEnrollment>());
		expect(
				motechService
						.saveMessageProgramEnrollment(capture(enrollment1Cap)))
				.andReturn(new MessageProgramEnrollment());
		expect(
				motechService.getActiveMessageProgramEnrollments(patient
						.getPatientId(), careProgramName, null)).andReturn(
				new ArrayList<MessageProgramEnrollment>());
		expect(
				motechService
						.saveMessageProgramEnrollment(capture(enrollment2Cap)))
				.andReturn(new MessageProgramEnrollment());

		expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
				.andReturn(ghanaLocation);
		expect(
				encounterService
						.getEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGREGVISIT))
				.andReturn(pregnancyRegVisitType);
		expect(contextService.getAuthenticatedUser()).andReturn(new User());
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
		expect(conceptService.getConcept(MotechConstants.CONCEPT_GRAVIDA))
				.andReturn(gravidaConcept);
		expect(conceptService.getConcept(MotechConstants.CONCEPT_PARITY))
				.andReturn(parityConcept);
		expect(
				obsService.saveObs(capture(pregnancyObsCap),
						(String) anyObject())).andReturn(new Obs());

		replay(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService, idService);

		regBean.registerPatient(RegistrationMode.USE_PREPRINTED_ID, motechId,
				RegistrantType.PREGNANT_MOTHER, firstName, middleName,
				lastName, prefName, date, birthDateEst, gender, insured, nhis,
				date, null, community, address, phoneNumber, date,
				dueDateConfirmed, gravida, parity, enroll, consent, phoneType,
				mediaType, language, dayOfWeek, date, reason, howLearned, null);

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
		assertEquals(community.toString(), capturedPatient.getAttribute(
				communityAttributeType).getValue());
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
				.getAttribute(whyInterestedAttributeType).getValue()));
		assertEquals(dayOfWeek, DayOfWeek.valueOf(capturedPatient.getAttribute(
				deliveryDayAttributeType).getValue()));
		Calendar timeOfDayCal = Calendar.getInstance();
		timeOfDayCal.setTime(date);
		int hour = timeOfDayCal.get(Calendar.HOUR_OF_DAY);
		int min = timeOfDayCal.get(Calendar.MINUTE);
		Date timeOfDayDate = (new SimpleDateFormat(
				MotechConstants.TIME_FORMAT_PERSON_ATTRIBUTE_DELIVERY_TIME))
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

	public void testRegisterChild() throws ParseException {
		Integer motechId = 123456;
		String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
		String nhis = "456DEF";
		String address = "Address";
		Integer phoneNumber = 2075555555;
		String language = "Language";
		Date date = new Date();
		Boolean birthDateEst = true, insured = true, dueDateConfirmed = true, enroll = true, consent = true;
		Integer gravida = 0, parity = 1, community = 123;
		Gender gender = Gender.FEMALE;
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType mediaType = MediaType.TEXT;
		DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
		InterestReason reason = InterestReason.FAMILY_FRIEND_PREGNANT;
		HowLearned howLearned = HowLearned.FRIEND;

		String pregnancyProgramName = "Weekly Info Child Message Program";
		String careProgramName = "Expected Care Message Program";

		Patient child = new Patient(1);
		Patient mother = new Patient(2);
		Location ghanaLocation = new Location(1);

		Capture<Patient> patientCap = new Capture<Patient>();
		Capture<MessageProgramEnrollment> enrollment1Cap = new Capture<MessageProgramEnrollment>();
		Capture<MessageProgramEnrollment> enrollment2Cap = new Capture<MessageProgramEnrollment>();
		Capture<Relationship> relationshipCap = new Capture<Relationship>();

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getLocationService()).andReturn(locationService);
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(contextService.getIdentifierSourceService())
				.andReturn(idService).atLeastOnce();

		expect(idService.getAllIdentifierSources(false)).andReturn(
				new ArrayList<IdentifierSource>());
		expect(contextService.getAuthenticatedUser()).andReturn(new User());
		expect(idService.saveLogEntry((LogEntry) anyObject())).andReturn(
				new LogEntry());
		expect(
				patientService
						.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID))
				.andReturn(motechIdType).atLeastOnce();
		expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
				.andReturn(ghanaLocation);

		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_COMMUNITY))
				.andReturn(communityAttributeType);
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
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_WHY_INTERESTED))
				.andReturn(whyInterestedAttributeType);

		expect(patientService.savePatient(capture(patientCap)))
				.andReturn(child);

		expect(
				personService
						.getRelationshipTypeByName(MotechConstants.RELATIONSHIP_TYPE_PARENT_CHILD))
				.andReturn(parentChildRelationshipType);
		expect(personService.saveRelationship(capture(relationshipCap)))
				.andReturn(new Relationship());

		expect(
				motechService.getActiveMessageProgramEnrollments(child
						.getPatientId(), pregnancyProgramName, null))
				.andReturn(new ArrayList<MessageProgramEnrollment>());
		expect(
				motechService
						.saveMessageProgramEnrollment(capture(enrollment1Cap)))
				.andReturn(new MessageProgramEnrollment());
		expect(
				motechService.getActiveMessageProgramEnrollments(child
						.getPatientId(), careProgramName, null)).andReturn(
				new ArrayList<MessageProgramEnrollment>());
		expect(
				motechService
						.saveMessageProgramEnrollment(capture(enrollment2Cap)))
				.andReturn(new MessageProgramEnrollment());

		replay(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService, idService);

		regBean.registerPatient(RegistrationMode.USE_PREPRINTED_ID, motechId,
				RegistrantType.CHILD_UNDER_FIVE, firstName, middleName,
				lastName, prefName, date, birthDateEst, gender, insured, nhis,
				date, mother, community, address, phoneNumber, date,
				dueDateConfirmed, gravida, parity, enroll, consent, phoneType,
				mediaType, language, dayOfWeek, date, reason, howLearned, null);

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
		assertEquals(GenderTypeConverter.toOpenMRSString(gender),
				capturedPatient.getGender());
		assertEquals(community.toString(), capturedPatient.getAttribute(
				communityAttributeType).getValue());
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
				.getAttribute(whyInterestedAttributeType).getValue()));
		assertEquals(dayOfWeek, DayOfWeek.valueOf(capturedPatient.getAttribute(
				deliveryDayAttributeType).getValue()));
		Calendar timeOfDayCal = Calendar.getInstance();
		timeOfDayCal.setTime(date);
		int hour = timeOfDayCal.get(Calendar.HOUR_OF_DAY);
		int min = timeOfDayCal.get(Calendar.MINUTE);
		Date timeOfDayDate = (new SimpleDateFormat(
				MotechConstants.TIME_FORMAT_PERSON_ATTRIBUTE_DELIVERY_TIME))
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

		Relationship relationship = relationshipCap.getValue();
		assertEquals(parentChildRelationshipType, relationship
				.getRelationshipType());
		assertEquals(Integer.valueOf(2), relationship.getPersonA()
				.getPersonId());
		assertEquals(child.getPatientId(), relationship.getPersonB()
				.getPersonId());
	}

	public void testRegisterPerson() throws ParseException {
		Integer motechId = 123456;
		String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
		String nhis = "456DEF";
		String address = "Address";
		Integer phoneNumber = 2075555555;
		String language = "Language";
		Date date = new Date();
		Boolean birthDateEst = true, insured = true, dueDateConfirmed = true, enroll = true, consent = true;
		Integer gravida = 0, parity = 1, community = 123;
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

		Capture<Patient> patientCap = new Capture<Patient>();
		Capture<MessageProgramEnrollment> enrollment1Cap = new Capture<MessageProgramEnrollment>();
		Capture<MessageProgramEnrollment> enrollment2Cap = new Capture<MessageProgramEnrollment>();
		Capture<Obs> refDateObsCap = new Capture<Obs>();

		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getLocationService()).andReturn(locationService)
				.atLeastOnce();
		expect(contextService.getMotechService()).andReturn(motechService)
				.atLeastOnce();
		expect(contextService.getObsService()).andReturn(obsService);
		expect(contextService.getConceptService()).andReturn(conceptService)
				.atLeastOnce();
		expect(contextService.getIdentifierSourceService())
				.andReturn(idService).atLeastOnce();

		expect(idService.getAllIdentifierSources(false)).andReturn(
				new ArrayList<IdentifierSource>());
		expect(contextService.getAuthenticatedUser()).andReturn(new User());
		expect(idService.saveLogEntry((LogEntry) anyObject())).andReturn(
				new LogEntry());
		expect(
				patientService
						.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID))
				.andReturn(motechIdType).atLeastOnce();

		expect(
				personService
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_COMMUNITY))
				.andReturn(communityAttributeType);
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
						.getPersonAttributeTypeByName(MotechConstants.PERSON_ATTRIBUTE_WHY_INTERESTED))
				.andReturn(whyInterestedAttributeType);

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
						.getPatientId(), pregnancyProgramName, null))
				.andReturn(new ArrayList<MessageProgramEnrollment>());
		expect(
				motechService
						.saveMessageProgramEnrollment(capture(enrollment1Cap)))
				.andReturn(new MessageProgramEnrollment());
		expect(
				motechService.getActiveMessageProgramEnrollments(patient
						.getPatientId(), careProgramName, null)).andReturn(
				new ArrayList<MessageProgramEnrollment>());
		expect(
				motechService
						.saveMessageProgramEnrollment(capture(enrollment2Cap)))
				.andReturn(new MessageProgramEnrollment());

		replay(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService, idService);

		regBean.registerPatient(RegistrationMode.USE_PREPRINTED_ID, motechId,
				RegistrantType.OTHER, firstName, middleName, lastName,
				prefName, date, birthDateEst, gender, insured, nhis, date,
				null, community, address, phoneNumber, date, dueDateConfirmed,
				gravida, parity, enroll, consent, phoneType, mediaType,
				language, dayOfWeek, date, reason, howLearned,
				messagesStartWeek);

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
		assertEquals(community.toString(), capturedPatient.getAttribute(
				communityAttributeType).getValue());
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
				.getAttribute(whyInterestedAttributeType).getValue()));
		assertEquals(dayOfWeek, DayOfWeek.valueOf(capturedPatient.getAttribute(
				deliveryDayAttributeType).getValue()));
		Calendar timeOfDayCal = Calendar.getInstance();
		timeOfDayCal.setTime(date);
		int hour = timeOfDayCal.get(Calendar.HOUR_OF_DAY);
		int min = timeOfDayCal.get(Calendar.MINUTE);
		Date timeOfDayDate = (new SimpleDateFormat(
				MotechConstants.TIME_FORMAT_PERSON_ATTRIBUTE_DELIVERY_TIME))
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
	}

	public void testEditPatient() throws ParseException {

		Integer patientId = 1;
		Integer phone = 2075551212;
		String nhis = "28";
		Date date = new Date();
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		Boolean stopEnrollment = true;

		User nurse = new User(2);
		Patient patient = new Patient(patientId);

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

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
		expect(contextService.getPersonService()).andReturn(personService)
				.atLeastOnce();
		expect(contextService.getMotechService()).andReturn(motechService)
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
						null, null)).andReturn(enrollments);
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

		replay(contextService, patientService, personService, motechService);

		regBean.editPatient(nurse, date, patient, phone, phoneType, nhis, date,
				stopEnrollment);

		verify(contextService, patientService, personService, motechService);

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

	public void testEditPatientAll() throws ParseException {
		Integer patientId = 2, clinic = 1;
		String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
		String regNumberGHS = "123ABC", nhis = "456DEF";
		String region = "Region", district = "District", community = "Community", address = "Address";
		String primaryPhone = "2075555555", secondaryPhone = "2075555556";
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
						.getPatientIdentifierTypeByName(MotechConstants.PATIENT_IDENTIFIER_MOTECH_ID))
				.andReturn(motechIdType);
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

		expect(patientService.savePatient(capture(patientCap))).andReturn(
				patient);

		replay(contextService, patientService, motechService, personService,
				locationService, userService, encounterService, obsService,
				conceptService);

		regBean.editPatient(patientId, firstName, middleName, lastName,
				prefName, date, birthDateEst, sex, registeredGHS, regNumberGHS,
				insured, nhis, date, region, district, community, address,
				clinic, primaryPhone, primaryPhoneType, secondaryPhone,
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
		assertEquals(region, capturedPatient.getPersonAddress().getRegion());
		assertEquals(district, capturedPatient.getPersonAddress()
				.getCountyDistrict());
		assertEquals(community, capturedPatient.getPersonAddress()
				.getCityVillage());
		assertEquals(address, capturedPatient.getPersonAddress().getAddress1());
		assertEquals(regNumberGHS, capturedPatient.getPatientIdentifier(
				motechIdType).getIdentifier());
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
		assertEquals(primaryPhone, capturedPatient.getAttribute(
				phoneAttributeType).getValue());
		assertEquals(primaryPhoneType, ContactNumberType
				.valueOf(capturedPatient.getAttribute(phoneTypeAttributeType)
						.getValue()));
		assertEquals(mediaTypeInfo, MediaType.valueOf(capturedPatient
				.getAttribute(mediaTypeAttributeType).getValue()));
		assertEquals(languageText, capturedPatient.getAttribute(
				languageAttributeType).getValue());
	}

	public void testRegisterPregnancy() {
		Integer patientId = 2;
		Date date = new Date();
		Boolean dueDateConfirmed = true, registerPregProgram = true;
		String primaryPhone = "2075555555", secondaryPhone = "2075555556";
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
		Capture<Encounter> pregnancyEncounterCap = new Capture<Encounter>();
		Capture<Obs> pregnancyObsCap = new Capture<Obs>();

		expect(contextService.getPatientService()).andReturn(patientService)
				.atLeastOnce();
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

		expect(patientService.getPatient(patientId)).andReturn(patient);
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
		expect(patientService.savePatient(capture(patientCap))).andReturn(
				new Patient());

		expect(locationService.getLocation(MotechConstants.LOCATION_GHANA))
				.andReturn(ghanaLocation);
		expect(
				encounterService
						.getEncounterType(MotechConstants.ENCOUNTER_TYPE_PREGREGVISIT))
				.andReturn(pregnancyRegVisitType);
		expect(contextService.getAuthenticatedUser()).andReturn(new User());
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
				motechService.getActiveMessageProgramEnrollments(patient
						.getPatientId(), pregnancyProgramName, null))
				.andReturn(new ArrayList<MessageProgramEnrollment>());
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
				phoneAttributeType).getValue());
		assertEquals(primaryPhoneType, ContactNumberType
				.valueOf(capturedPatient.getAttribute(phoneTypeAttributeType)
						.getValue()));
		assertEquals(mediaTypeInfo, MediaType.valueOf(capturedPatient
				.getAttribute(mediaTypeAttributeType).getValue()));
		assertEquals(languageText, capturedPatient.getAttribute(
				languageAttributeType).getValue());

		MessageProgramEnrollment enrollment = enrollmentCap.getValue();
		assertEquals(patient.getPatientId(), enrollment.getPersonId());
		assertEquals(pregnancyProgramName, enrollment.getProgram());
		assertNotNull("Enrollment start date should not be null", enrollment
				.getStartDate());
		assertNull("Enrollment end date should be null", enrollment
				.getEndDate());

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

	public void testSetMessageStatusSuccessMessageFoundTroubled() {
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

	public void testSetMessageStatusFailureMessageFoundNotTroubled() {
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

	public void testSetMessageStatusFailureMessageFoundTroubled() {
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

	public void testSetMessageStatusMessageNotFound() {
		String messageId = "12345678-1234-1234-1234-123456789012";
		Boolean success = true;

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(contextService.getPersonService()).andReturn(personService);
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
