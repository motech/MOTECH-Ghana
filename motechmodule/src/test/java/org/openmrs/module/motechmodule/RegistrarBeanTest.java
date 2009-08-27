package org.openmrs.module.motechmodule;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import org.easymock.Capture;
import org.motech.svc.RegistrarBean;
import org.motech.svc.RegistrarBeanImpl;
import org.openmrs.Location;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;

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

}
