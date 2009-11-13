package org.motech.openmrs.module.web.controller;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motech.model.Blackout;
import org.motech.model.TroubledPhone;
import org.motech.openmrs.module.ContextService;
import org.motech.openmrs.module.MotechService;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DeliveryTime;
import org.motechproject.ws.Gender;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.server.RegistrarService;
import org.springframework.ui.ModelMap;

public class MotechModuleFormControllerTest extends TestCase {

	RegistrarService registrarService;
	MotechModuleFormController controller;
	ContextService contextService;
	MotechService motechService;

	@Override
	protected void setUp() {
		registrarService = createMock(RegistrarService.class);
		motechService = createMock(MotechService.class);
		contextService = createMock(ContextService.class);
		controller = new MotechModuleFormController();
		controller.setRegistrarClient(registrarService);
		controller.setContextService(contextService);
	}

	@Override
	protected void tearDown() {
		controller = null;
		registrarService = null;
	}

	public void testQuickTest() throws Exception {
		String nurseName = "Nurse Name", nursePhone = "Nurse Phone", clinicName = "Clinic Name";
		String serialId = "Serial Id", name = "Patient Name", community = "Community", location = "Location", dateOfBirth = "01/01/2009";
		String nhis = "1", patientPhone = "Patient Phone", patientPhoneType = "PERSONAL", language = "Language", mediaType = "TEXT";
		String deliveryTime = "ANYTIME";
		String[] programs = { "minuteTetanus" };
		String dueDate = "01/01/2009", parity = "1", hemoglobin = "1.1";

		Capture<Date> dateOfBirthCapture = new Capture<Date>();
		Capture<Gender> genderCapture = new Capture<Gender>();
		Capture<Integer> nhisCapture = new Capture<Integer>();
		Capture<ContactNumberType> phoneTypeCapture = new Capture<ContactNumberType>();
		Capture<MediaType> mediaTypeCapture = new Capture<MediaType>();
		Capture<DeliveryTime> deliveryTimeCapture = new Capture<DeliveryTime>();
		Capture<String[]> programCapture = new Capture<String[]>();
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
				capture(mediaTypeCapture), capture(deliveryTimeCapture),
				capture(programCapture));
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
				language, mediaType, deliveryTime, programs, dateOfBirth,
				dueDate, parity, hemoglobin);

		verify(registrarService);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		assertEquals(dateOfBirth, dateFormat.format(dateOfBirthCapture
				.getValue()));
		assertEquals(Gender.FEMALE, genderCapture.getValue());
		assertEquals(nhis, nhisCapture.getValue().toString());
		assertEquals(patientPhoneType, phoneTypeCapture.getValue().toString());
		assertEquals(mediaType, mediaTypeCapture.getValue().toString());
		assertEquals(2, programCapture.getValue().length);
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
		String nhis = "1", patientPhone = "Patient Phone", patientPhoneType = "PERSONAL", language = "Language", mediaType = "TEXT";
		String deliveryTime = "ANYTIME";
		String[] programs = { "minuteTetanus", "dailyPregnancy" };
		String gender = "FEMALE";

		Capture<Date> dateOfBirthCapture = new Capture<Date>();
		Capture<Gender> genderCapture = new Capture<Gender>();
		Capture<Integer> nhisCapture = new Capture<Integer>();
		Capture<ContactNumberType> phoneTypeCapture = new Capture<ContactNumberType>();
		Capture<MediaType> mediaTypeCapture = new Capture<MediaType>();
		Capture<DeliveryTime> deliveryTimeCapture = new Capture<DeliveryTime>();
		Capture<String[]> programsCapture = new Capture<String[]>();

		registrarService.registerPatient(eq(nursePhone), eq(serialId),
				eq(name), eq(community), eq(location),
				capture(dateOfBirthCapture), capture(genderCapture),
				capture(nhisCapture), eq(patientPhone),
				capture(phoneTypeCapture), eq(language),
				capture(mediaTypeCapture), capture(deliveryTimeCapture),
				capture(programsCapture));

		replay(registrarService);

		controller.registerPatient(nursePhone, serialId, name, community,
				location, nhis, patientPhone, patientPhoneType, dateOfBirth,
				gender, language, mediaType, deliveryTime, programs);

		verify(registrarService);

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

		assertEquals(dateOfBirth, dateFormat.format(dateOfBirthCapture
				.getValue()));
		assertEquals(gender, genderCapture.getValue().toString());
		assertEquals(nhis, nhisCapture.getValue().toString());
		assertEquals(patientPhoneType, phoneTypeCapture.getValue().toString());
		assertEquals(mediaType, mediaTypeCapture.getValue().toString());
		assertEquals(deliveryTime, deliveryTimeCapture.getValue().toString());
		assertEquals(3, programsCapture.getValue().length);
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

	public void testViewBlackoutForm() throws ParseException {

		Time startTime = Time.valueOf("07:00:00"), endTime = Time
				.valueOf("19:00:00");

		Blackout interval = new Blackout(startTime, endTime);
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getBlackoutSettings()).andReturn(interval);

		replay(contextService, motechService);

		ModelMap model = new ModelMap();
		String path = controller.viewBlackoutSettings(model);

		verify(contextService, motechService);

		assertEquals("/module/motechmodule/blackout", path);
		assertEquals(model.get("startTime"), startTime);
		assertEquals(model.get("endTime"), endTime);
	}

	public void testViewBlackoutFormNoData() throws ParseException {
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getBlackoutSettings()).andReturn(null);

		replay(contextService, motechService);

		ModelMap model = new ModelMap();
		String path = controller.viewBlackoutSettings(model);

		verify(contextService, motechService);

		assertEquals("/module/motechmodule/blackout", path);
	}

	public void testSaveBlackoutSettings() throws ParseException {

		String startTime = "07:00:00", endTime = "19:00:00";

		Capture<Blackout> boCap = new Capture<Blackout>();

		expect(contextService.getMotechService()).andReturn(motechService);

		expect(motechService.getBlackoutSettings()).andReturn(null);
		motechService.setBlackoutSettings(capture(boCap));

		replay(contextService, motechService);

		ModelMap model = new ModelMap();
		String path = controller
				.saveBlackoutSettings(startTime, endTime, model);

		verify(contextService, motechService);

		assertEquals("/module/motechmodule/blackout", path);
		assertEquals(startTime, model.get("startTime").toString());
		assertEquals(endTime, model.get("endTime").toString());
		assertEquals(startTime, boCap.getValue().getStartTime().toString());
		assertEquals(endTime, boCap.getValue().getEndTime().toString());
	}

	public void testUpdateBlackoutSettings() throws ParseException {

		String startTime = "07:00:00", endTime = "19:00:00";

		Capture<Blackout> boCap = new Capture<Blackout>();

		expect(contextService.getMotechService()).andReturn(motechService);

		Blackout blackout = new Blackout(null, null);
		expect(motechService.getBlackoutSettings()).andReturn(blackout);
		motechService.setBlackoutSettings(capture(boCap));

		replay(contextService, motechService);

		ModelMap model = new ModelMap();
		String path = controller
				.saveBlackoutSettings(startTime, endTime, model);

		verify(contextService, motechService);

		assertEquals("/module/motechmodule/blackout", path);
		assertEquals(startTime, model.get("startTime").toString());
		assertEquals(endTime, model.get("endTime").toString());
		assertEquals(startTime, boCap.getValue().getStartTime().toString());
		assertEquals(endTime, boCap.getValue().getEndTime().toString());
	}

	public void testLookupTroubledPhoneNoPhone() {

		String phone = null;

		ModelMap model = new ModelMap();
		String path = controller.handleTroubledPhone(phone, null, model);

		assertNull(model.get("troubledPhone"));
		assertEquals("/module/motechmodule/troubledphone", path);
	}

	public void testLookupTroubledPhone() {

		String phone = "378378373";
		TroubledPhone tp = new TroubledPhone();
		tp.setId(38903L);
		tp.setPhoneNumber(phone);

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getTroubledPhone(phone)).andReturn(tp);

		replay(contextService, motechService);

		ModelMap model = new ModelMap();
		String path = controller.handleTroubledPhone(phone, null, model);

		verify(contextService, motechService);

		assertEquals(tp, model.get("troubledPhone"));
		assertEquals("/module/motechmodule/troubledphone", path);
	}

	public void testRemoveTroubledPhone() {
		String phone = "378378373";
		TroubledPhone tp = new TroubledPhone();
		tp.setId(38903L);
		tp.setPhoneNumber(phone);

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getTroubledPhone(phone)).andReturn(tp);
		motechService.removeTroubledPhone(phone);

		replay(contextService, motechService);

		ModelMap model = new ModelMap();
		String path = controller.handleTroubledPhone(phone, true, model);

		verify(contextService, motechService);

		assertNull(model.get("troubledPhone"));
		assertEquals("/module/motechmodule/troubledphone", path);
	}
}
