package org.motechproject.server.omod.web.controller;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebPatient;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.support.SessionStatus;

public class EditPatientControllerTest extends TestCase {

	RegistrarBean registrarBean;
	EditPatientController controller;
	ContextService contextService;
	MotechService motechService;
	WebModelConverter webModelConverter;
	SessionStatus status;
	PatientService patientService;

	@Override
	protected void setUp() {
		registrarBean = createMock(RegistrarBean.class);
		contextService = createMock(ContextService.class);
		webModelConverter = createMock(WebModelConverter.class);
		controller = new EditPatientController();
		controller.setRegistrarBean(registrarBean);
		controller.setContextService(contextService);
		controller.setWebModelConverter(webModelConverter);
		motechService = createMock(MotechService.class);

		patientService = createMock(PatientService.class);
		status = createMock(SessionStatus.class);
	}

	@Override
	protected void tearDown() {
		controller = null;
		registrarBean = null;
		patientService = null;
		contextService = null;
		motechService = null;
		status = null;
	}

	public void testGetRegions() {
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getAllRegions())
				.andReturn(new ArrayList<String>());

		replay(contextService, motechService);

		controller.getRegions();

		verify(contextService, motechService);
	}

	public void testGetDistricts() {
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getAllDistricts()).andReturn(
				new ArrayList<String>());

		replay(contextService, motechService);

		controller.getDistricts();

		verify(contextService, motechService);
	}

	public void testGetCommunities() {
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getAllCommunities()).andReturn(
				new ArrayList<Community>());

		replay(contextService, motechService);

		controller.getCommunities();

		verify(contextService, motechService);
	}

	public void testGetWebPatientMissingId() {
		Integer patientId = null;

		replay(registrarBean, patientService);

		WebPatient webPatient = controller.getWebPatient(patientId);

		verify(registrarBean, patientService);

		assertNull("Patient is not new for null id", webPatient.getId());
	}

	public void testGetWebPatientInvalidId() {
		Integer patientId = 1;
		Patient patient = null;

		expect(contextService.getPatientService()).andReturn(patientService);
		expect(patientService.getPatient(patientId)).andReturn(patient);

		replay(registrarBean, contextService, patientService);

		WebPatient webPatient = controller.getWebPatient(patientId);

		verify(registrarBean, contextService, patientService);

		assertNull("Patient is not new for invalid id", webPatient.getId());
	}

	public void testGetWebPatientValidId() {
		Integer patientId = 1;
		Patient patient = new Patient(patientId);

		expect(contextService.getPatientService()).andReturn(patientService);
		expect(patientService.getPatient(patientId)).andReturn(patient);
		webModelConverter.patientToWeb(eq(patient), (WebPatient) anyObject());

		replay(registrarBean, contextService, patientService, webModelConverter);

		WebPatient webPatient = controller.getWebPatient(patientId);

		verify(registrarBean, contextService, patientService, webModelConverter);

		assertNotNull(webPatient);
	}

	public void testEditPatient() {
		Integer patientId = 1, communityId = 11112;
		String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
		String region = "Region", district = "District", address = "Address", nhis = "1234DEF";
		String phoneNumber = "12075555555";
		Boolean birthDateEst = true, enroll = true, consent = true, insured = true;
		Date date = new Date();
		Gender sex = Gender.FEMALE;
		ContactNumberType phoneType = ContactNumberType.PERSONAL;
		MediaType mediaType = MediaType.TEXT;
		String language = "en";
		DayOfWeek dayOfWeek = DayOfWeek.FRIDAY;

		WebPatient patient = new WebPatient();
		patient.setId(patientId);
		patient.setFirstName(firstName);
		patient.setMiddleName(middleName);
		patient.setLastName(lastName);
		patient.setPrefName(prefName);
		patient.setBirthDate(date);
		patient.setBirthDateEst(birthDateEst);
		patient.setSex(sex);
		patient.setInsured(insured);
		patient.setNhis(nhis);
		patient.setNhisExpDate(date);
		patient.setRegion(region);
		patient.setDistrict(district);
		patient.setCommunityId(communityId);
		patient.setAddress(address);
		patient.setPhoneNumber(phoneNumber);
		patient.setPhoneType(phoneType);
		patient.setMediaType(mediaType);
		patient.setLanguage(language);
		patient.setDueDate(date);
		patient.setEnroll(enroll);
		patient.setConsent(consent);
		patient.setDayOfWeek(dayOfWeek);
		patient.setTimeOfDay(date);

		Errors errors = new BeanPropertyBindingResult(patient, "patient");
		ModelMap model = new ModelMap();

		Patient openmrsPatient = new Patient(1);
		Community community = new Community();

		expect(registrarBean.getPatientById(patientId)).andReturn(
				openmrsPatient);
		expect(registrarBean.getCommunityById(communityId))
				.andReturn(community);

		registrarBean.editPatient(openmrsPatient, firstName, middleName,
				lastName, prefName, date, birthDateEst, sex, insured, nhis,
				date, community, address, phoneNumber, date, enroll, consent,
				phoneType, mediaType, language, dayOfWeek, date);

		status.setComplete();

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getAllFacilities()).andReturn(
				new ArrayList<Facility>());

		replay(registrarBean, status, contextService, motechService);

		controller.submitForm(patient, errors, model, status);

		verify(registrarBean, status, contextService, motechService);

		assertTrue("Missing success message in model", model
				.containsAttribute("successMsg"));
	}
}
