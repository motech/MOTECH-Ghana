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

import org.motechproject.server.model.HIVStatus;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.web.model.WebModelConverter;
import org.motechproject.server.omod.web.model.WebPatient;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.support.SessionStatus;

public class EditPatientControllerTest extends TestCase {

	RegistrarBean registrarBean;
	EditPatientController controller;
	ContextService contextService;
	MotechService motechService;
	WebModelConverter webModelConverter;
	Errors errors;
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
		errors = createMock(Errors.class);
		status = createMock(SessionStatus.class);
	}

	@Override
	protected void tearDown() {
		controller = null;
		registrarBean = null;
		patientService = null;
		contextService = null;
		motechService = null;
		errors = null;
		status = null;
	}

	public void testGetRegions() {
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getAllRegions()).andReturn(
				new ArrayList<Location>());

		replay(contextService, motechService);

		controller.getRegions();

		verify(contextService, motechService);
	}

	public void testGetDistricts() {
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getAllDistricts()).andReturn(
				new ArrayList<Location>());

		replay(contextService, motechService);

		controller.getDistricts();

		verify(contextService, motechService);
	}

	public void testGetCommunities() {
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getAllCommunities()).andReturn(
				new ArrayList<Location>());

		replay(contextService, motechService);

		controller.getCommunities();

		verify(contextService, motechService);
	}

	public void testGetClinics() {
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getAllClinics()).andReturn(
				new ArrayList<Location>());

		replay(contextService, motechService);

		controller.getClinics();

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
		Integer patientId = 1, clinic = 2;
		String firstName = "FirstName", middleName = "MiddleName", lastName = "LastName", prefName = "PrefName";
		String region = "Region", district = "District", community = "Community", address = "Address";
		String regNumberGHS = "123ABC", nhis = "1234DEF";
		String primaryPhone = "12075555555", secondaryPhone = "12075555556";
		String religion = "Religion", occupation = "Occupation";
		Boolean birthDateEst = true, registeredGHS = true, insured = true;
		Date date = new Date();
		Gender sex = Gender.FEMALE;
		ContactNumberType primaryPhoneType = ContactNumberType.PERSONAL, secondaryPhoneType = ContactNumberType.PUBLIC;
		MediaType mediaTypeInfo = MediaType.TEXT, mediaTypeReminder = MediaType.VOICE;
		String languageVoice = "LanguageVoice", languageText = "LanguageText";
		HIVStatus hivStatus = HIVStatus.UNKNOWN;

		WebPatient patient = new WebPatient();
		patient.setId(patientId);
		patient.setFirstName(firstName);
		patient.setMiddleName(middleName);
		patient.setLastName(lastName);
		patient.setPrefName(prefName);
		patient.setBirthDate(date);
		patient.setBirthDateEst(birthDateEst);
		patient.setSex(sex);
		patient.setRegisteredGHS(registeredGHS);
		patient.setRegNumberGHS(regNumberGHS);
		patient.setInsured(insured);
		patient.setNhis(nhis);
		patient.setNhisExpDate(date);
		patient.setRegion(region);
		patient.setDistrict(district);
		patient.setCommunity(community);
		patient.setAddress(address);
		patient.setClinic(clinic);
		patient.setPrimaryPhone(primaryPhone);
		patient.setPrimaryPhoneType(primaryPhoneType);
		patient.setSecondaryPhone(secondaryPhone);
		patient.setSecondaryPhoneType(secondaryPhoneType);
		patient.setMediaTypeInfo(mediaTypeInfo);
		patient.setMediaTypeReminder(mediaTypeReminder);
		patient.setLanguageVoice(languageVoice);
		patient.setLanguageText(languageText);
		patient.setReligion(religion);
		patient.setOccupation(occupation);
		patient.setHivStatus(hivStatus);

		ModelMap model = new ModelMap();

		expect(errors.hasErrors()).andReturn(false);

		registrarBean.editPatient(patientId, firstName, middleName, lastName,
				prefName, date, birthDateEst, sex, registeredGHS, regNumberGHS,
				insured, nhis, date, region, district, community, address,
				clinic, primaryPhone, primaryPhoneType, secondaryPhone,
				secondaryPhoneType, mediaTypeInfo, mediaTypeReminder,
				languageVoice, languageText, religion, occupation, hivStatus);

		status.setComplete();

		replay(registrarBean, errors, status);

		controller.submitForm(patient, errors, model, status);

		verify(registrarBean, errors, status);

		assertTrue("Missing success message in model", model
				.containsAttribute("successMsg"));
	}
}
