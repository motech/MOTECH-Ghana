package org.motech.openmrs.module.web.controller;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motech.model.Gender;
import org.motech.model.NotificationType;
import org.motech.model.PhoneType;
import org.motech.ws.RegistrarService;

public class MotechModuleFormControllerTest extends TestCase {

	RegistrarService registrarService;
	MotechModuleFormController controller;

	@Override
	protected void setUp() {
		registrarService = createMock(RegistrarService.class);
		controller = new MotechModuleFormController();
		controller.setRegistrarClient(registrarService);
	}

	@Override
	protected void tearDown() {
		controller = null;
		registrarService = null;
	}

	public void testQuickTest() throws Exception {
		String nurseName = "Nurse Name", nursePhone = "Nurse Phone", clinicName = "Clinic Name";
		String serialId = "Serial Id", name = "Patient Name", community = "Community", location = "Location", dateOfBirth = "01/01/2009";
		String nhis = "1", patientPhone = "Patient Phone", patientPhoneType = "personal", language = "Language", notificationType = "text";
		String dueDate = "01/01/2009", parity = "1", hemoglobin = "1.1";

		Capture<Date> dateOfBirthCapture = new Capture<Date>();
		Capture<Gender> genderCapture = new Capture<Gender>();
		Capture<Integer> nhisCapture = new Capture<Integer>();
		Capture<PhoneType> phoneTypeCapture = new Capture<PhoneType>();
		Capture<NotificationType> notificationTypeCapture = new Capture<NotificationType>();
		Capture<Date> dueDateCapture = new Capture<Date>();
		Capture<Integer> parityCapture = new Capture<Integer>();
		Capture<Double> hemoglobinCapture = new Capture<Double>();

		registrarService.registerClinic(clinicName);
		registrarService.registerNurse(nurseName, nursePhone, clinicName);
		registrarService.registerPatient(eq(nursePhone), eq(serialId),
				eq(name), eq(community), eq(location),
				capture(dateOfBirthCapture), capture(genderCapture),
				capture(nhisCapture), eq(patientPhone),
				capture(phoneTypeCapture), eq(language),
				capture(notificationTypeCapture));
		registrarService.registerPregnancy(eq(nursePhone), (Date) anyObject(),
				eq(serialId), capture(dueDateCapture), capture(parityCapture),
				capture(hemoglobinCapture));
		registrarService.recordMaternalVisit(eq(nursePhone),
				(Date) anyObject(), eq(serialId), (Boolean) anyObject(),
				(Boolean) anyObject(), (Boolean) anyObject(),
				(Integer) anyObject(), (Boolean) anyObject(),
				(Boolean) anyObject(), (Boolean) anyObject(),
				(Boolean) anyObject(), (Double) anyObject());

		replay(registrarService);

		controller.quickTest(nurseName, nursePhone, clinicName, serialId, name,
				community, location, nhis, patientPhone, patientPhoneType,
				language, notificationType, dateOfBirth, dueDate, parity,
				hemoglobin);

		verify(registrarService);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		assertEquals(dateOfBirth, dateFormat.format(dateOfBirthCapture
				.getValue()));
		assertEquals(Gender.female, genderCapture.getValue());
		assertEquals(nhis, nhisCapture.getValue().toString());
		assertEquals(patientPhoneType, phoneTypeCapture.getValue().toString());
		assertEquals(notificationType, notificationTypeCapture.getValue()
				.toString());
		assertEquals(dueDate, dateFormat.format(dueDateCapture.getValue()));
		assertEquals(parity, parityCapture.getValue().toString());
		assertEquals(hemoglobin, hemoglobinCapture.getValue().toString());
	}

	public void testRegisterClinic() throws Exception {
		String name = "Clinic Name";

		registrarService.registerClinic(name);

		replay(registrarService);

		controller.registerClinic(name);

		verify(registrarService);
	}

	public void testRegiserNurse() throws Exception {
		String name = "Nurse Name", nursePhone = "Nurse Phone", clinic = "Clinic Name";

		registrarService.registerNurse(name, nursePhone, clinic);

		replay(registrarService);

		controller.registerNurse(name, nursePhone, clinic);

		verify(registrarService);
	}

	public void testRegisterPatient() throws Exception {
		String nursePhone = "Nurse Phone";
		String serialId = "Serial Id", name = "Patient Name", community = "Community", location = "Location", dateOfBirth = "01/01/2009";
		String nhis = "1", patientPhone = "Patient Phone", patientPhoneType = "personal", language = "Language", notificationType = "text";
		String gender = "female";

		Capture<Date> dateOfBirthCapture = new Capture<Date>();
		Capture<Gender> genderCapture = new Capture<Gender>();
		Capture<Integer> nhisCapture = new Capture<Integer>();
		Capture<PhoneType> phoneTypeCapture = new Capture<PhoneType>();
		Capture<NotificationType> notificationTypeCapture = new Capture<NotificationType>();

		registrarService.registerPatient(eq(nursePhone), eq(serialId),
				eq(name), eq(community), eq(location),
				capture(dateOfBirthCapture), capture(genderCapture),
				capture(nhisCapture), eq(patientPhone),
				capture(phoneTypeCapture), eq(language),
				capture(notificationTypeCapture));

		replay(registrarService);

		controller.registerPatient(nursePhone, serialId, name, community,
				location, nhis, patientPhone, patientPhoneType, dateOfBirth,
				gender, language, notificationType);

		verify(registrarService);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		assertEquals(dateOfBirth, dateFormat.format(dateOfBirthCapture
				.getValue()));
		assertEquals(gender, genderCapture.getValue().toString());
		assertEquals(nhis, nhisCapture.getValue().toString());
		assertEquals(patientPhoneType, phoneTypeCapture.getValue().toString());
		assertEquals(notificationType, notificationTypeCapture.getValue()
				.toString());
	}

	public void testRegisterPregnancy() throws Exception {
		String nursePhone = "Nurse Phone", serialId = "Serial Id";
		String regDate = "01/01/2009";
		String dueDate = "01/01/2009", parity = "1", hemoglobin = "1.1";

		Capture<Date> regDateCapture = new Capture<Date>();
		Capture<Date> dueDateCapture = new Capture<Date>();
		Capture<Integer> parityCapture = new Capture<Integer>();
		Capture<Double> hemoglobinCapture = new Capture<Double>();

		registrarService.registerPregnancy(eq(nursePhone),
				capture(regDateCapture), eq(serialId), capture(dueDateCapture),
				capture(parityCapture), capture(hemoglobinCapture));

		replay(registrarService);

		controller.registerPregnancy(nursePhone, regDate, serialId, dueDate,
				parity, hemoglobin);

		verify(registrarService);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		assertEquals(regDate, dateFormat.format(regDateCapture.getValue()));
		assertEquals(dueDate, dateFormat.format(dueDateCapture.getValue()));
		assertEquals(parity, parityCapture.getValue().toString());
		assertEquals(hemoglobin, hemoglobinCapture.getValue().toString());
	}

	public void testRecordMaternalVisit() throws Exception {
		String nursePhone = "Nurse Phone", serialId = "Serial Id";
		String visitDate = "01/01/2009", tetanus = "true", ipt = "true", itn = "true", visitNumber = "1";
		String onARV = "true", prePMTCT = "true", testPMTCT = "true", postPMTCT = "true", hemoglobin = "1.1";

		Capture<Date> visitDateCapture = new Capture<Date>();
		Capture<Boolean> tetanusCapture = new Capture<Boolean>();
		Capture<Boolean> iptCapture = new Capture<Boolean>();
		Capture<Boolean> itnCapture = new Capture<Boolean>();
		Capture<Integer> visitNumberCapture = new Capture<Integer>();
		Capture<Boolean> onARVCapture = new Capture<Boolean>();
		Capture<Boolean> prePMTCTCapture = new Capture<Boolean>();
		Capture<Boolean> testPMTCTCapture = new Capture<Boolean>();
		Capture<Boolean> postPMTCTCapture = new Capture<Boolean>();
		Capture<Double> hemoglobin36Capture = new Capture<Double>();

		registrarService.recordMaternalVisit(eq(nursePhone),
				capture(visitDateCapture), eq(serialId),
				capture(tetanusCapture), capture(iptCapture),
				capture(itnCapture), capture(visitNumberCapture),
				capture(onARVCapture), capture(prePMTCTCapture),
				capture(testPMTCTCapture), capture(postPMTCTCapture),
				capture(hemoglobin36Capture));

		replay(registrarService);

		controller.recordMaternalVisit(nursePhone, visitDate, serialId,
				tetanus, ipt, itn, visitNumber, onARV, prePMTCT, testPMTCT,
				postPMTCT, hemoglobin);

		verify(registrarService);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		assertEquals(visitDate, dateFormat.format(visitDateCapture.getValue()));
		assertEquals(tetanus, tetanusCapture.getValue().toString());
		assertEquals(ipt, iptCapture.getValue().toString());
		assertEquals(itn, itnCapture.getValue().toString());
		assertEquals(visitNumber, visitNumberCapture.getValue().toString());
		assertEquals(onARV, onARVCapture.getValue().toString());
		assertEquals(prePMTCT, prePMTCTCapture.getValue().toString());
		assertEquals(testPMTCT, testPMTCTCapture.getValue().toString());
		assertEquals(postPMTCT, postPMTCTCapture.getValue().toString());
		assertEquals(hemoglobin, hemoglobin36Capture.getValue().toString());
	}
}
