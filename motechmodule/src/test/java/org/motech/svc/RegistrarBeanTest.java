package org.motech.svc;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.same;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motech.model.Gender;
import org.motech.model.Log;
import org.motech.model.LogType;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.module.motechmodule.ContextAuthenticator;
import org.openmrs.module.motechmodule.MotechService;

public class RegistrarBeanTest extends TestCase {

	RegistrarBean regBean;

	ContextAuthenticator contextAuthenticator;
	LocationService locationService;
	PersonService personService;
	UserService userService;
	PatientService patientService;
	EncounterService encounterService;
	ConceptService conceptService;
	MotechService motechService;

	String ghanaIdTypeName = "Ghana Clinic Id";
	String phoneAttrName = "Phone Number";
	String clinicAttrName = "Health Center";
	String nhisAttrName = "NHIS Number";
	String providerRoleName = "Provider";
	String matVisitTypeName = "MATERNALVISIT";
	String immunizationConceptName = "IMMUNIZATIONS ORDERED";
	String tetanusConceptName = "TETANUS BOOSTER";
	String iptConceptName = "INTERMITTENT PREVENTATIVE TREATMENT";
	String itnConceptName = "INSECTICIDE-TREATED NET USAGE";
	String visitNumConceptName = "PREGNANCY VISIT NUMBER";
	String arvConceptName = "ANTIRETROVIRAL USE DURING PREGNANCY";
	String onArvConceptName = "ON ANTIRETROVIRAL THERAPY";
	String prePMTCTConceptName = "PRE PREVENTING MATERNAL TO CHILD TRANSMISSION";
	String testPMTCTConceptName = "TEST PREVENTING MATERNAL TO CHILD TRANSMISSION";
	String postPMTCTConceptName = "POST PREVENTING MATERNAL TO CHILD TRANSMISSION";
	String hemoConceptName = "HEMOGLOBIN AT 36 WEEKS";

	PatientIdentifierType ghanaIdType;
	PersonAttributeType phoneAttributeType;
	PersonAttributeType clinicAttributeType;
	PersonAttributeType nhisAttributeType;
	Role providerRole;
	EncounterType matVisitType;
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
	ConceptName arvConceptNameObj;
	Concept arvConcept;
	ConceptName onArvConceptNameObj;
	Concept onArvConcept;
	ConceptName prePMTCTConceptNameObj;
	Concept prePMTCTConcept;
	ConceptName testPMTCTConceptNameObj;
	Concept testPMTCTConcept;
	ConceptName postPMTCTConceptNameObj;
	Concept postPMTCTConcept;
	ConceptName hemoConceptNameObj;
	Concept hemoConcept;

	@Override
	protected void setUp() throws Exception {
		contextAuthenticator = createMock(ContextAuthenticator.class);
		locationService = createMock(LocationService.class);
		personService = createMock(PersonService.class);
		userService = createMock(UserService.class);
		patientService = createMock(PatientService.class);
		encounterService = createMock(EncounterService.class);
		conceptService = createMock(ConceptService.class);
		motechService = createMock(MotechService.class);

		ghanaIdType = new PatientIdentifierType(1);
		ghanaIdType.setName(ghanaIdTypeName);

		phoneAttributeType = new PersonAttributeType(2);
		phoneAttributeType.setName(phoneAttrName);

		clinicAttributeType = new PersonAttributeType(3);
		clinicAttributeType.setName(clinicAttrName);

		nhisAttributeType = new PersonAttributeType(4);
		nhisAttributeType.setName(nhisAttrName);

		providerRole = new Role(providerRoleName);

		matVisitType = new EncounterType(5);
		matVisitType.setName(matVisitTypeName);

		immunizationConceptNameObj = new ConceptName(immunizationConceptName,
				Locale.getDefault());
		immunizationConcept = new Concept(6);

		tetanusConceptNameObj = new ConceptName(tetanusConceptName, Locale
				.getDefault());
		tetanusConcept = new Concept(7);

		iptConceptNameObj = new ConceptName(iptConceptName, Locale.getDefault());
		iptConcept = new Concept(8);

		itnConceptNameObj = new ConceptName(itnConceptName, Locale.getDefault());
		itnConcept = new Concept(9);

		visitNumConceptNameObj = new ConceptName(visitNumConceptName, Locale
				.getDefault());
		visitNumConcept = new Concept(10);

		arvConceptNameObj = new ConceptName(arvConceptName, Locale.getDefault());
		arvConcept = new Concept(11);

		onArvConceptNameObj = new ConceptName(onArvConceptName, Locale
				.getDefault());
		onArvConcept = new Concept(12);

		prePMTCTConceptNameObj = new ConceptName(prePMTCTConceptName, Locale
				.getDefault());
		prePMTCTConcept = new Concept(13);

		testPMTCTConceptNameObj = new ConceptName(testPMTCTConceptName, Locale
				.getDefault());
		testPMTCTConcept = new Concept(14);

		postPMTCTConceptNameObj = new ConceptName(postPMTCTConceptName, Locale
				.getDefault());
		postPMTCTConcept = new Concept(15);

		hemoConceptNameObj = new ConceptName(hemoConceptName, Locale
				.getDefault());
		hemoConcept = new Concept(16);

		RegistrarBeanImpl regBeanImpl = new RegistrarBeanImpl();
		regBeanImpl.setContextAuthenticator(contextAuthenticator);
		regBeanImpl.setLocationService(locationService);
		regBeanImpl.setPersonService(personService);
		regBeanImpl.setUserService(userService);
		regBeanImpl.setPatientService(patientService);
		regBeanImpl.setEncounterService(encounterService);
		regBeanImpl.setConceptService(conceptService);
		regBeanImpl.setMotechService(motechService);

		regBean = regBeanImpl;
	}

	@Override
	protected void tearDown() throws Exception {
		regBean = null;

		contextAuthenticator = null;
		locationService = null;
		personService = null;
		userService = null;
		patientService = null;
		encounterService = null;
		conceptService = null;
		motechService = null;
	}

	public void testRegisterClinic() {
		contextAuthenticator.authenticate((String) anyObject(),
				(String) anyObject());
		Capture<Location> locationCap = new Capture<Location>();
		expect(locationService.saveLocation(capture(locationCap))).andReturn(
				new Location());

		replay(contextAuthenticator, locationService);

		String clinicName = "A-Test-Clinic-Name";
		regBean.registerClinic(clinicName);

		verify(contextAuthenticator, locationService);

		Location location = locationCap.getValue();
		assertEquals(clinicName, location.getName());
		assertEquals("A Ghana Clinic Location", location.getDescription());
	}

	public void testRegisterNurse() {
		String name = "Jenny", phone = "12078675309", clinic = "Mayo Clinic";

		Location clinicLocation = new Location(1);
		clinicLocation.setName(clinic);

		Capture<User> nurseCap = new Capture<User>();

		contextAuthenticator.authenticate((String) anyObject(),
				(String) anyObject());
		expect(personService.getPersonAttributeTypeByName(phoneAttrName))
				.andReturn(phoneAttributeType);
		expect(userService.getRole(providerRoleName)).andReturn(providerRole);
		expect(locationService.getLocation(clinic)).andReturn(clinicLocation);
		expect(personService.getPersonAttributeTypeByName(clinicAttrName))
				.andReturn(clinicAttributeType);
		expect(userService.saveUser(capture(nurseCap), (String) anyObject()))
				.andReturn(new User());

		replay(contextAuthenticator, personService, userService,
				locationService);

		regBean.registerNurse(name, phone, clinic);

		verify(contextAuthenticator, personService, userService,
				locationService);

		User nurse = nurseCap.getValue();
		assertEquals(name, nurse.getGivenName());
		assertEquals(phone, nurse.getAttribute(phoneAttributeType).getValue());
		assertEquals(clinicLocation.getLocationId().toString(), nurse
				.getAttribute(clinicAttributeType).getValue());
	}

	public void testRegisterPatient() {
		String nPhone = "12075551212", serialId = "dbvhjdg4784", name = "Gaylord", community = "A Community", location = "A Location", pPhone = "120773733373";
		Date dob = new Date();
		Gender gender = Gender.male;
		Integer nhis = 28;

		Location locationObj = new Location(1);
		locationObj.setName(location);

		User nurse = new User(1);
		nurse.addAttribute(new PersonAttribute(phoneAttributeType, nPhone));
		nurse.addAttribute(new PersonAttribute(clinicAttributeType, locationObj
				.getLocationId().toString()));

		Capture<Patient> patientCap = new Capture<Patient>();

		contextAuthenticator.authenticate((String) anyObject(),
				(String) anyObject());
		expect(patientService.getPatientIdentifierTypeByName(ghanaIdTypeName))
				.andReturn(ghanaIdType);
		expect(motechService.getUserByPhoneNumber(nPhone)).andReturn(nurse);
		expect(personService.getPersonAttributeTypeByName(clinicAttrName))
				.andReturn(clinicAttributeType);
		expect(locationService.getLocation(locationObj.getLocationId()))
				.andReturn(locationObj);
		expect(personService.getPersonAttributeTypeByName(nhisAttrName))
				.andReturn(nhisAttributeType);
		expect(personService.getPersonAttributeTypeByName(phoneAttrName))
				.andReturn(phoneAttributeType);
		expect(patientService.savePatient(capture(patientCap))).andReturn(
				new Patient());

		replay(contextAuthenticator, patientService, motechService,
				personService, locationService);

		regBean.registerPatient(nPhone, serialId, name, community, location,
				dob, gender, nhis, pPhone);

		verify(contextAuthenticator, patientService, motechService,
				personService, locationService);

		Patient patient = patientCap.getValue();
		assertEquals(serialId, patient.getPatientIdentifier(ghanaIdType)
				.getIdentifier());
		assertEquals(name, patient.getGivenName());
		assertEquals(location, patient.getPersonAddress().getAddress1());
		assertEquals(community, patient.getPersonAddress().getCityVillage());
		assertEquals(pPhone, patient.getAttribute(phoneAttributeType)
				.getValue());
		assertEquals(dob, patient.getBirthdate());
		assertEquals(gender.toOpenMRSString(), patient.getGender());
		assertEquals(nhis.toString(), patient.getAttribute(nhisAttributeType)
				.getValue());
	}

	public void testRegisterMaternalVisit() {

		String nPhone = "12078888888", serialId = "478df-389489";
		Date date = new Date();
		Boolean tetanus = true, ipt = true, itn = true, onARV = true, prePMTCT = true, testPMTCT = true, postPMTCT = true;
		Double hemo = 12.3;
		Integer visit = 1;

		Location locationObj = new Location(1);

		User nurse = new User(1);
		nurse.addAttribute(new PersonAttribute(phoneAttributeType, nPhone));
		nurse.addAttribute(new PersonAttribute(clinicAttributeType, locationObj
				.getLocationId().toString()));

		Patient patient = new Patient();
		patient.addIdentifier(new PatientIdentifier(serialId, ghanaIdType,
				locationObj));
		List<Patient> patients = new ArrayList<Patient>();
		patients.add(patient);

		Capture<List<PatientIdentifierType>> typeList = new Capture<List<PatientIdentifierType>>();
		Capture<Encounter> encounterCap = new Capture<Encounter>();

		contextAuthenticator.authenticate((String) anyObject(),
				(String) anyObject());
		expect(patientService.getPatientIdentifierTypeByName(ghanaIdTypeName))
				.andReturn(ghanaIdType);
		expect(
				patientService.getPatients(same((String) null), eq(serialId),
						capture(typeList), eq(true))).andReturn(patients);
		expect(motechService.getUserByPhoneNumber(nPhone)).andReturn(nurse);
		expect(personService.getPersonAttributeTypeByName(clinicAttrName))
				.andReturn(clinicAttributeType);
		expect(locationService.getLocation(1)).andReturn(locationObj);
		expect(encounterService.getEncounterType(matVisitTypeName)).andReturn(
				matVisitType);
		expect(conceptService.getConcept(immunizationConceptName)).andReturn(
				immunizationConcept);
		expect(conceptService.getConcept(tetanusConceptName)).andReturn(
				tetanusConcept);
		expect(conceptService.getConcept(immunizationConceptName)).andReturn(
				immunizationConcept);
		expect(conceptService.getConcept(iptConceptName)).andReturn(iptConcept);
		expect(conceptService.getConcept(itnConceptName)).andReturn(itnConcept);
		expect(conceptService.getConcept(visitNumConceptName)).andReturn(
				visitNumConcept);
		expect(conceptService.getConcept(arvConceptName)).andReturn(arvConcept);
		expect(conceptService.getConcept(onArvConceptName)).andReturn(
				onArvConcept);
		expect(conceptService.getConcept(prePMTCTConceptName)).andReturn(
				prePMTCTConcept);
		expect(conceptService.getConcept(testPMTCTConceptName)).andReturn(
				testPMTCTConcept);
		expect(conceptService.getConcept(postPMTCTConceptName)).andReturn(
				postPMTCTConcept);
		expect(conceptService.getConcept(hemoConceptName)).andReturn(
				hemoConcept);
		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());

		replay(contextAuthenticator, patientService, motechService,
				personService, locationService, encounterService,
				conceptService);

		regBean.recordMaternalVisit(nPhone, date, serialId, tetanus, ipt, itn,
				visit, onARV, prePMTCT, testPMTCT, postPMTCT, hemo);

		verify(contextAuthenticator, patientService, motechService,
				personService, locationService, encounterService,
				conceptService);

		Encounter e = encounterCap.getValue();
		assertTrue(typeList.getValue().size() == 1);
		assertTrue(typeList.getValue().contains(ghanaIdType));
		assertEquals(nPhone, e.getProvider().getAttribute(phoneAttributeType)
				.getValue());
		assertEquals(serialId, e.getPatient().getPatientIdentifier()
				.getIdentifier());
		assertEquals(date, e.getEncounterDatetime());
		// TODO: Check each observation exists in encounter, and matches value
	}

	public void testLog() {
		LogType type = LogType.failure;
		String message = "A simple message";
		Date beforeCall = new Date();

		Capture<Log> logCap = new Capture<Log>();

		motechService.saveLog(capture(logCap));

		replay(motechService);

		regBean.log(type, message);

		verify(motechService);

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
}
