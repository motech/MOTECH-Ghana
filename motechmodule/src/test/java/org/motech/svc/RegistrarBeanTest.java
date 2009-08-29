package org.motech.svc;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Date;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motech.model.Gender;
import org.openmrs.Location;
import org.openmrs.Patient;
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

	String phoneAttrName = "Phone Number";
	String clinicAttrName = "Health Center";
	String nhisAttrName = "NHIS Number";
	String providerRoleName = "Provider";

	PersonAttributeType phoneAttributeType;
	PersonAttributeType clinicAttributeType;
	PersonAttributeType nhisAttributeType;
	Role providerRole;

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

		phoneAttributeType = new PersonAttributeType(2);
		phoneAttributeType.setName(phoneAttrName);

		clinicAttributeType = new PersonAttributeType(3);
		clinicAttributeType.setName(clinicAttrName);

		nhisAttributeType = new PersonAttributeType(4);
		nhisAttributeType.setName(nhisAttrName);

		providerRole = new Role(providerRoleName);

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

		String ghanaIdTypeName = "Ghana Clinic Id";
		PatientIdentifierType ghanaIdType = new PatientIdentifierType(1);
		ghanaIdType.setName(ghanaIdTypeName);

		Location locationObj = new Location(1);
		locationObj.setName(location);

		User nurse = new User(1);
		nurse.addAttribute(new PersonAttribute(phoneAttributeType, pPhone));
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
}
