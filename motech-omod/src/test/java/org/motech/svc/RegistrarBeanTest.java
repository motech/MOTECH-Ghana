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
import org.motech.messaging.Message;
import org.motech.messaging.MessageNotFoundException;
import org.motech.messaging.MessageStatus;
import org.motech.messaging.ScheduledMessage;
import org.motech.model.GenderTypeConverter;
import org.motech.model.Log;
import org.motech.model.TroubledPhone;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveryTime;
import org.motechproject.ws.Gender;
import org.motechproject.ws.LogType;
import org.motechproject.ws.MediaType;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;

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

	String defaultLocationName = "Default Ghana Clinic";
	String ghanaIdTypeName = "Ghana Clinic Id";
	String phoneAttrName = "Phone Number";
	String clinicAttrName = "Health Center";
	String nhisAttrName = "NHIS Number";
	String languageAttrName = "Language";
	String phoneTypeAttrName = "Phone Type";
	String mediaTypeAttrName = "Media Type";
	String deliveryTimeAttrName = "Delivery Time";
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
	String hemo36ConceptName = "HEMOGLOBIN AT 36 WEEKS";
	String pregVisitName = "PREGNANCYVISIT";
	String pregStatusConceptName = "PREGNANCY STATUS";
	String dateConfConceptName = "ESTIMATED DATE OF CONFINEMENT";
	String gravidaConceptName = "GRAVIDA";
	String hemoConceptName = "HEMOGLOBIN";
	String regimenStartConceptName = "REGIMEN START";
	String regimenEndConceptName = "REGIMEN END";

	Location defaultClinic;
	PatientIdentifierType ghanaIdType;
	PersonAttributeType phoneAttributeType;
	PersonAttributeType clinicAttributeType;
	PersonAttributeType nhisAttributeType;
	PersonAttributeType languageAttributeType;
	PersonAttributeType phoneTypeAttributeType;
	PersonAttributeType mediaTypeAttributeType;
	PersonAttributeType deliveryTimeAttributeType;
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
	ConceptName hemo36ConceptNameObj;
	Concept hemo36Concept;
	EncounterType pregVisitType;
	ConceptName pregStatusConceptNameObj;
	Concept pregStatusConcept;
	ConceptName dateConfConceptNameObj;
	Concept dateConfConcept;
	ConceptName gravidaConceptNameObj;
	Concept gravidaConcept;
	ConceptName hemoConceptNameObj;
	Concept hemoConcept;
	ConceptName regimenStartConceptNameObj;
	Concept regimenStart;
	ConceptName regimenEndConceptNameObj;
	Concept regimenEnd;

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

		defaultClinic = new Location(1);
		defaultClinic.setName(defaultLocationName);

		ghanaIdType = new PatientIdentifierType(1);
		ghanaIdType.setName(ghanaIdTypeName);

		phoneAttributeType = new PersonAttributeType(2);
		phoneAttributeType.setName(phoneAttrName);

		clinicAttributeType = new PersonAttributeType(3);
		clinicAttributeType.setName(clinicAttrName);

		nhisAttributeType = new PersonAttributeType(4);
		nhisAttributeType.setName(nhisAttrName);

		languageAttributeType = new PersonAttributeType(5);
		languageAttributeType.setName(languageAttrName);

		phoneTypeAttributeType = new PersonAttributeType(6);
		phoneTypeAttributeType.setName(phoneTypeAttrName);

		mediaTypeAttributeType = new PersonAttributeType(7);
		mediaTypeAttributeType.setName(mediaTypeAttrName);

		deliveryTimeAttributeType = new PersonAttributeType(8);
		deliveryTimeAttributeType.setName(deliveryTimeAttrName);

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

		hemo36ConceptNameObj = new ConceptName(hemo36ConceptName, Locale
				.getDefault());
		hemo36Concept = new Concept(16);

		pregVisitType = new EncounterType(17);
		pregVisitType.setName(pregVisitName);

		pregStatusConceptNameObj = new ConceptName(pregStatusConceptName,
				Locale.getDefault());
		pregStatusConcept = new Concept(18);

		dateConfConceptNameObj = new ConceptName(dateConfConceptName, Locale
				.getDefault());
		dateConfConcept = new Concept(19);

		gravidaConceptNameObj = new ConceptName(gravidaConceptName, Locale
				.getDefault());
		gravidaConcept = new Concept(20);

		hemoConceptNameObj = new ConceptName(hemoConceptName, Locale
				.getDefault());
		hemoConcept = new Concept(21);

		regimenStartConceptNameObj = new ConceptName(regimenStartConceptName,
				Locale.getDefault());
		regimenStart = new Concept(22);

		regimenEndConceptNameObj = new ConceptName(regimenEndConceptName,
				Locale.getDefault());
		regimenEnd = new Concept(23);

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

	public void testRegisterClinic() {
		expect(contextService.getLocationService()).andReturn(locationService);
		contextService.authenticate((String) anyObject(), (String) anyObject());
		Capture<Location> locationCap = new Capture<Location>();
		expect(locationService.saveLocation(capture(locationCap))).andReturn(
				new Location());

		replay(contextService, locationService);

		String clinicName = "A-Test-Clinic-Name";
		regBean.registerClinic(clinicName);

		verify(contextService, locationService);

		Location location = locationCap.getValue();
		assertEquals(clinicName, location.getName());
		assertEquals("A Ghana Clinic Location", location.getDescription());
	}

	public void testRegisterNurse() {

		String name = "Jenny", phone = "12078675309", clinic = "Mayo Clinic";

		Location clinicLocation = new Location(1);
		clinicLocation.setName(clinic);

		Capture<User> nurseCap = new Capture<User>();

		expect(contextService.getPersonService()).andReturn(personService);
		expect(contextService.getUserService()).andReturn(userService);
		expect(contextService.getLocationService()).andReturn(locationService);

		contextService.authenticate((String) anyObject(), (String) anyObject());
		expect(personService.getPersonAttributeTypeByName(phoneAttrName))
				.andReturn(phoneAttributeType);
		expect(userService.getRole(providerRoleName)).andReturn(providerRole);
		expect(locationService.getLocation(clinic)).andReturn(clinicLocation);
		expect(personService.getPersonAttributeTypeByName(clinicAttrName))
				.andReturn(clinicAttributeType);
		expect(userService.saveUser(capture(nurseCap), (String) anyObject()))
				.andReturn(new User());

		replay(contextService, personService, userService, locationService);

		regBean.registerNurse(name, phone, clinic);

		verify(contextService, personService, userService, locationService);

		User nurse = nurseCap.getValue();
		assertEquals(name, nurse.getGivenName());
		assertEquals(phone, nurse.getAttribute(phoneAttributeType).getValue());
		assertEquals(clinicLocation.getLocationId().toString(), nurse
				.getAttribute(clinicAttributeType).getValue());
	}

	public void testRegisterPatient() {

		String nPhone = "12075551212", serialId = "dbvhjdg4784", name = "Gaylord", community = "A Community", location = "A Location", pPhone = "120773733373";
		Date dob = new Date();
		Gender gender = Gender.MALE;
		Integer nhis = 28;
		ContactNumberType contactNumberType = ContactNumberType.PERSONAL;
		String language = "English";
		MediaType mediaType = MediaType.TEXT;
		DeliveryTime deliveryTime = DeliveryTime.ANYTIME;
		String exampleRegimen = "Example regimen";
		String[] regimen = { exampleRegimen };

		Location locationObj = new Location(1);
		locationObj.setName(location);

		User nurse = new User(1);
		nurse.addAttribute(new PersonAttribute(phoneAttributeType, nPhone));
		nurse.addAttribute(new PersonAttribute(clinicAttributeType, locationObj
				.getLocationId().toString()));

		Capture<Patient> patientCap = new Capture<Patient>();
		Capture<Obs> obsCap = new Capture<Obs>();

		expect(contextService.getPatientService()).andReturn(patientService);
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(contextService.getPersonService()).andReturn(personService);
		expect(contextService.getLocationService()).andReturn(locationService);
		expect(contextService.getObsService()).andReturn(obsService);
		expect(contextService.getConceptService()).andReturn(conceptService);

		contextService.authenticate((String) anyObject(), (String) anyObject());
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
		expect(personService.getPersonAttributeTypeByName(phoneTypeAttrName))
				.andReturn(phoneTypeAttributeType);
		expect(personService.getPersonAttributeTypeByName(languageAttrName))
				.andReturn(languageAttributeType);
		expect(personService.getPersonAttributeTypeByName(mediaTypeAttrName))
				.andReturn(mediaTypeAttributeType);
		expect(personService.getPersonAttributeTypeByName(deliveryTimeAttrName))
				.andReturn(deliveryTimeAttributeType);
		expect(patientService.savePatient(capture(patientCap))).andReturn(
				new Patient(1));
		expect(conceptService.getConcept(regimenStartConceptName)).andReturn(
				regimenStart);
		expect(locationService.getLocation(defaultLocationName)).andReturn(
				defaultClinic);
		expect(obsService.saveObs(capture(obsCap), eq((String) null)))
				.andReturn(new Obs());

		replay(contextService, patientService, motechService, personService,
				locationService, obsService, conceptService);

		regBean.registerPatient(nPhone, serialId, name, community, location,
				dob, gender, nhis, pPhone, contactNumberType, language,
				mediaType, deliveryTime, regimen);

		verify(contextService, patientService, motechService, personService,
				locationService, obsService, conceptService);

		Patient patient = patientCap.getValue();
		assertEquals(serialId, patient.getPatientIdentifier(ghanaIdType)
				.getIdentifier());
		assertEquals(name, patient.getGivenName());
		assertEquals(location, patient.getPersonAddress().getAddress1());
		assertEquals(community, patient.getPersonAddress().getCityVillage());
		assertEquals(pPhone, patient.getAttribute(phoneAttributeType)
				.getValue());
		assertEquals(dob, patient.getBirthdate());
		assertEquals(GenderTypeConverter.toOpenMRSString(gender), patient
				.getGender());
		assertEquals(nhis.toString(), patient.getAttribute(nhisAttributeType)
				.getValue());
		assertEquals(contactNumberType.toString(), patient.getAttribute(
				phoneTypeAttributeType).getValue());
		assertEquals(language, patient.getAttribute(languageAttributeType)
				.getValue());
		assertEquals(mediaType.toString(), patient.getAttribute(
				mediaTypeAttributeType).getValue());
		assertEquals(deliveryTime.toString(), patient.getAttribute(
				deliveryTimeAttributeType).getValue());
		Obs regimenStartObs = obsCap.getValue();
		assertEquals(regimenStart, regimenStartObs.getConcept());
		assertEquals(exampleRegimen, regimenStartObs.getValueText());
		assertEquals(defaultClinic, regimenStartObs.getLocation());
	}

	public void testRegisterMaternalVisit() {

		String nPhone = "12078888888", serialId = "478df-389489";
		Date date = new Date();
		Boolean tetanus = true, ipt = true, itn = true, onARV = true, prePMTCT = true, testPMTCT = true, postPMTCT = true;
		Double hemo = 12.3;
		Integer visit = 1;

		Location locationObj = new Location(1);
		locationObj.setName("Test Location");

		User nurse = new User(1);
		nurse.addAttribute(new PersonAttribute(phoneAttributeType, nPhone));
		nurse.addAttribute(new PersonAttribute(clinicAttributeType, locationObj
				.getLocationId().toString()));

		Patient patient = new Patient(2);
		patient.addIdentifier(new PatientIdentifier(serialId, ghanaIdType,
				locationObj));
		List<Patient> patients = new ArrayList<Patient>();
		patients.add(patient);

		Capture<List<PatientIdentifierType>> typeList = new Capture<List<PatientIdentifierType>>();
		Capture<Encounter> encounterCap = new Capture<Encounter>();
		Capture<Obs> tetanusObsCap = new Capture<Obs>();
		Capture<Obs> iptObsCap = new Capture<Obs>();
		Capture<Obs> itnObsCap = new Capture<Obs>();
		Capture<Obs> visitNumberObsCap = new Capture<Obs>();
		Capture<Obs> arvObsCap = new Capture<Obs>();
		Capture<Obs> preObsCap = new Capture<Obs>();
		Capture<Obs> testObsCap = new Capture<Obs>();
		Capture<Obs> postObsCap = new Capture<Obs>();
		Capture<Obs> hemoglobinObsCap = new Capture<Obs>();

		expect(contextService.getPatientService()).andReturn(patientService);
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(contextService.getPersonService()).andReturn(personService);
		expect(contextService.getLocationService()).andReturn(locationService);
		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(contextService.getObsService()).andReturn(obsService);
		expect(contextService.getConceptService()).andReturn(conceptService);

		contextService.authenticate((String) anyObject(), (String) anyObject());
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
		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());
		expect(conceptService.getConcept(immunizationConceptName)).andReturn(
				immunizationConcept);
		expect(conceptService.getConcept(tetanusConceptName)).andReturn(
				tetanusConcept);
		expect(obsService.saveObs(capture(tetanusObsCap), (String) anyObject()))
				.andReturn(new Obs());
		expect(conceptService.getConcept(immunizationConceptName)).andReturn(
				immunizationConcept);
		expect(conceptService.getConcept(iptConceptName)).andReturn(iptConcept);
		expect(obsService.saveObs(capture(iptObsCap), (String) anyObject()))
				.andReturn(new Obs());
		expect(conceptService.getConcept(itnConceptName)).andReturn(itnConcept);
		expect(obsService.saveObs(capture(itnObsCap), (String) anyObject()))
				.andReturn(new Obs());
		expect(conceptService.getConcept(visitNumConceptName)).andReturn(
				visitNumConcept);
		expect(
				obsService.saveObs(capture(visitNumberObsCap),
						(String) anyObject())).andReturn(new Obs());
		expect(conceptService.getConcept(arvConceptName)).andReturn(arvConcept);
		expect(conceptService.getConcept(onArvConceptName)).andReturn(
				onArvConcept);
		expect(obsService.saveObs(capture(arvObsCap), (String) anyObject()))
				.andReturn(new Obs());
		expect(conceptService.getConcept(prePMTCTConceptName)).andReturn(
				prePMTCTConcept);
		expect(obsService.saveObs(capture(preObsCap), (String) anyObject()))
				.andReturn(new Obs());
		expect(conceptService.getConcept(testPMTCTConceptName)).andReturn(
				testPMTCTConcept);
		expect(obsService.saveObs(capture(testObsCap), (String) anyObject()))
				.andReturn(new Obs());
		expect(conceptService.getConcept(postPMTCTConceptName)).andReturn(
				postPMTCTConcept);
		expect(obsService.saveObs(capture(postObsCap), (String) anyObject()))
				.andReturn(new Obs());
		expect(conceptService.getConcept(hemo36ConceptName)).andReturn(
				hemo36Concept);
		expect(
				obsService.saveObs(capture(hemoglobinObsCap),
						(String) anyObject())).andReturn(new Obs());

		replay(contextService, patientService, motechService, personService,
				locationService, encounterService, obsService, conceptService);

		regBean.recordMaternalVisit(nPhone, date, serialId, tetanus, ipt, itn,
				visit, onARV, prePMTCT, testPMTCT, postPMTCT, hemo);

		verify(contextService, patientService, motechService, personService,
				locationService, encounterService, obsService, conceptService);

		Encounter e = encounterCap.getValue();
		assertEquals(1, typeList.getValue().size());
		assertTrue(typeList.getValue().contains(ghanaIdType));
		assertEquals(nPhone, e.getProvider().getAttribute(phoneAttributeType)
				.getValue());
		assertEquals(serialId, e.getPatient().getPatientIdentifier()
				.getIdentifier());
		assertEquals(date, e.getEncounterDatetime());

		Obs tetanusObs = tetanusObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), tetanusObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), tetanusObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), tetanusObs.getLocation());
		assertEquals(immunizationConcept, tetanusObs.getConcept());
		assertEquals(tetanusConcept, tetanusObs.getValueCoded());

		Obs iptObs = iptObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), iptObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), iptObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), iptObs.getLocation());
		assertEquals(immunizationConcept, iptObs.getConcept());
		assertEquals(iptConcept, iptObs.getValueCoded());

		Obs itnObs = itnObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), itnObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), itnObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), itnObs.getLocation());
		assertEquals(itnConcept, itnObs.getConcept());
		assertEquals(Boolean.TRUE, itnObs.getValueAsBoolean());

		Obs visitNumberObs = visitNumberObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), visitNumberObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), visitNumberObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), visitNumberObs.getLocation());
		assertEquals(visitNumConcept, visitNumberObs.getConcept());
		assertEquals(Double.valueOf(visit), visitNumberObs.getValueNumeric());

		Obs arvObs = arvObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), arvObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), arvObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), arvObs.getLocation());
		assertEquals(arvConcept, arvObs.getConcept());
		assertEquals(onArvConcept, arvObs.getValueCoded());

		Obs preObs = preObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), preObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), preObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), preObs.getLocation());
		assertEquals(prePMTCTConcept, preObs.getConcept());
		assertEquals(Boolean.TRUE, preObs.getValueAsBoolean());

		Obs testObs = testObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), testObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), testObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), testObs.getLocation());
		assertEquals(testPMTCTConcept, testObs.getConcept());
		assertEquals(Boolean.TRUE, testObs.getValueAsBoolean());

		Obs postObs = postObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), postObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), postObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), postObs.getLocation());
		assertEquals(postPMTCTConcept, postObs.getConcept());
		assertEquals(Boolean.TRUE, postObs.getValueAsBoolean());

		Obs hemoglobinObs = hemoglobinObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), hemoglobinObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), hemoglobinObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), hemoglobinObs.getLocation());
		assertEquals(hemo36Concept, hemoglobinObs.getConcept());
		assertEquals(hemo, hemoglobinObs.getValueNumeric());
	}

	public void testRegisterMaternalVisitNoObs() {

		String nPhone = "12078888888", serialId = "478df-389489";
		Date date = new Date();
		Boolean tetanus = false, ipt = false, itn = false, onARV = false, prePMTCT = false, testPMTCT = false, postPMTCT = false;
		Double hemo = 12.3;
		Integer visit = 1;

		Location locationObj = new Location(1);
		locationObj.setName("Test Location");

		User nurse = new User(1);
		nurse.addAttribute(new PersonAttribute(phoneAttributeType, nPhone));
		nurse.addAttribute(new PersonAttribute(clinicAttributeType, locationObj
				.getLocationId().toString()));

		Patient patient = new Patient(2);
		patient.addIdentifier(new PatientIdentifier(serialId, ghanaIdType,
				locationObj));
		List<Patient> patients = new ArrayList<Patient>();
		patients.add(patient);

		Capture<List<PatientIdentifierType>> typeList = new Capture<List<PatientIdentifierType>>();
		Capture<Encounter> encounterCap = new Capture<Encounter>();
		Capture<Obs> visitNumberObsCap = new Capture<Obs>();
		Capture<Obs> hemoglobinObsCap = new Capture<Obs>();

		expect(contextService.getPatientService()).andReturn(patientService);
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(contextService.getPersonService()).andReturn(personService);
		expect(contextService.getLocationService()).andReturn(locationService);
		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(contextService.getObsService()).andReturn(obsService);
		expect(contextService.getConceptService()).andReturn(conceptService);

		contextService.authenticate((String) anyObject(), (String) anyObject());
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
		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());
		expect(conceptService.getConcept(visitNumConceptName)).andReturn(
				visitNumConcept);
		expect(
				obsService.saveObs(capture(visitNumberObsCap),
						(String) anyObject())).andReturn(new Obs());
		expect(conceptService.getConcept(hemo36ConceptName)).andReturn(
				hemo36Concept);
		expect(
				obsService.saveObs(capture(hemoglobinObsCap),
						(String) anyObject())).andReturn(new Obs());

		replay(contextService, patientService, motechService, personService,
				locationService, encounterService, obsService, conceptService);

		regBean.recordMaternalVisit(nPhone, date, serialId, tetanus, ipt, itn,
				visit, onARV, prePMTCT, testPMTCT, postPMTCT, hemo);

		verify(contextService, patientService, motechService, personService,
				locationService, encounterService, obsService, conceptService);

		Encounter e = encounterCap.getValue();
		assertEquals(1, typeList.getValue().size());
		assertTrue(typeList.getValue().contains(ghanaIdType));
		assertEquals(nPhone, e.getProvider().getAttribute(phoneAttributeType)
				.getValue());
		assertEquals(serialId, e.getPatient().getPatientIdentifier()
				.getIdentifier());
		assertEquals(date, e.getEncounterDatetime());

		Obs visitNumberObs = visitNumberObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), visitNumberObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), visitNumberObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), visitNumberObs.getLocation());
		assertEquals(visitNumConcept, visitNumberObs.getConcept());
		assertEquals(Double.valueOf(visit), visitNumberObs.getValueNumeric());

		Obs hemoglobinObs = hemoglobinObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), hemoglobinObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), hemoglobinObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), hemoglobinObs.getLocation());
		assertEquals(hemo36Concept, hemoglobinObs.getConcept());
		assertEquals(hemo, hemoglobinObs.getValueNumeric());
	}

	public void testRegisterPregnancy() {
		String nPhone = "15555555555", serialId = "AFGHSFG";
		Date date = new Date();
		Integer parity = 1;
		Double hemo = 3893.1;

		Location locationObj = new Location(1);
		locationObj.setName("Test Location");

		User nurse = new User(1);
		nurse.addAttribute(new PersonAttribute(phoneAttributeType, nPhone));
		nurse.addAttribute(new PersonAttribute(clinicAttributeType, locationObj
				.getLocationId().toString()));

		Patient patient = new Patient(2);
		patient.addIdentifier(new PatientIdentifier(serialId, ghanaIdType,
				locationObj));
		List<Patient> patients = new ArrayList<Patient>();
		patients.add(patient);

		Capture<Encounter> encounterCap = new Capture<Encounter>();
		Capture<List<PatientIdentifierType>> typeListCap = new Capture<List<PatientIdentifierType>>();
		Capture<Obs> pregStatusObsCap = new Capture<Obs>();
		Capture<Obs> dueDateObsCap = new Capture<Obs>();
		Capture<Obs> parityObsCap = new Capture<Obs>();
		Capture<Obs> hemoglobinObsCap = new Capture<Obs>();

		expect(contextService.getPatientService()).andReturn(patientService);
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(contextService.getPersonService()).andReturn(personService);
		expect(contextService.getLocationService()).andReturn(locationService);
		expect(contextService.getEncounterService())
				.andReturn(encounterService);
		expect(contextService.getObsService()).andReturn(obsService);
		expect(contextService.getConceptService()).andReturn(conceptService);

		contextService.authenticate((String) anyObject(), (String) anyObject());
		expect(patientService.getPatientIdentifierTypeByName(ghanaIdTypeName))
				.andReturn(ghanaIdType);
		expect(
				patientService.getPatients(same((String) null), eq(serialId),
						capture(typeListCap), eq(true))).andReturn(patients);
		expect(motechService.getUserByPhoneNumber(nPhone)).andReturn(nurse);
		expect(personService.getPersonAttributeTypeByName(clinicAttrName))
				.andReturn(clinicAttributeType);
		expect(locationService.getLocation(1)).andReturn(locationObj);
		expect(encounterService.getEncounterType(pregVisitName)).andReturn(
				pregVisitType);
		expect(encounterService.saveEncounter(capture(encounterCap)))
				.andReturn(new Encounter());
		expect(conceptService.getConcept(pregStatusConceptName)).andReturn(
				pregStatusConcept);
		expect(
				obsService.saveObs(capture(pregStatusObsCap),
						(String) anyObject())).andReturn(new Obs());
		expect(conceptService.getConcept(dateConfConceptName)).andReturn(
				dateConfConcept);
		expect(obsService.saveObs(capture(dueDateObsCap), (String) anyObject()))
				.andReturn(new Obs());
		expect(conceptService.getConcept(gravidaConceptName)).andReturn(
				gravidaConcept);
		expect(obsService.saveObs(capture(parityObsCap), (String) anyObject()))
				.andReturn(new Obs());
		expect(conceptService.getConcept(hemoConceptName)).andReturn(
				hemoConcept);
		expect(
				obsService.saveObs(capture(hemoglobinObsCap),
						(String) anyObject())).andReturn(new Obs());

		replay(contextService, patientService, motechService, personService,
				locationService, encounterService, obsService, conceptService);

		regBean.registerPregnancy(nPhone, date, serialId, date, parity, hemo);

		verify(contextService, patientService, motechService, personService,
				locationService, encounterService, obsService, conceptService);

		Encounter e = encounterCap.getValue();
		assertEquals(nPhone, e.getProvider().getAttribute(phoneAttributeType)
				.getValue());
		assertEquals(serialId, e.getPatient().getPatientIdentifier()
				.getIdentifier());
		assertEquals(date, e.getEncounterDatetime());

		Obs pregStatusObs = pregStatusObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), pregStatusObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), pregStatusObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), pregStatusObs.getLocation());
		assertEquals(pregStatusConcept, pregStatusObs.getConcept());
		assertEquals(Boolean.TRUE, pregStatusObs.getValueAsBoolean());

		Obs dueDateObs = dueDateObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), dueDateObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), dueDateObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), dueDateObs.getLocation());
		assertEquals(dateConfConcept, dueDateObs.getConcept());
		assertEquals(date, dueDateObs.getValueDatetime());

		Obs parityObs = parityObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), parityObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), parityObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), parityObs.getLocation());
		assertEquals(gravidaConcept, parityObs.getConcept());
		assertEquals(Double.valueOf(parity), parityObs.getValueNumeric());

		Obs hemoglobinObs = hemoglobinObsCap.getValue();
		assertEquals(e.getEncounterDatetime(), hemoglobinObs.getObsDatetime());
		assertEquals(e.getPatient().getPatientId(), hemoglobinObs.getPerson()
				.getPersonId());
		assertEquals(e.getLocation(), hemoglobinObs.getLocation());
		assertEquals(hemoConcept, hemoglobinObs.getConcept());
		assertEquals(hemo, hemoglobinObs.getValueNumeric());
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
		expect(contextService.getPersonService()).andReturn(personService);
		expect(motechService.getMessage(messageId)).andReturn(message);
		expect(personService.getPerson(recipientId)).andReturn(recipient);
		expect(personService.getPersonAttributeTypeByName(phoneAttrName))
				.andReturn(phoneAttributeType);
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
		expect(contextService.getPersonService()).andReturn(personService);
		expect(motechService.getMessage(messageId)).andReturn(message);
		expect(personService.getPerson(recipientId)).andReturn(recipient);
		expect(personService.getPersonAttributeTypeByName(phoneAttrName))
				.andReturn(phoneAttributeType);
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
		expect(contextService.getPersonService()).andReturn(personService);
		expect(motechService.getMessage(messageId)).andReturn(message);
		expect(personService.getPerson(recipientId)).andReturn(recipient);
		expect(personService.getPersonAttributeTypeByName(phoneAttrName))
				.andReturn(phoneAttributeType);
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
		expect(contextService.getPersonService()).andReturn(personService);
		expect(motechService.getMessage(messageId)).andReturn(message);
		expect(personService.getPerson(recipientId)).andReturn(recipient);
		expect(personService.getPersonAttributeTypeByName(phoneAttrName))
				.andReturn(phoneAttributeType);
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
