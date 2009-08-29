package org.motech.svc;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import org.easymock.Capture;
import org.openmrs.Location;
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
		String phoneAttrName = "Phone Number";
		String clinicAttrName = "Health Center";
		String providerRoleName = "Provider";

		PersonAttributeType phoneAttributeType = new PersonAttributeType(2);
		phoneAttributeType.setName(phoneAttrName);

		PersonAttributeType clinicAttributeType = new PersonAttributeType(3);
		clinicAttributeType.setName(clinicAttrName);

		Role providerRole = new Role(providerRoleName);
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

}
