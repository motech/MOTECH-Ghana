package org.motech.tests;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Date;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motech.dao.SimpleDao;
import org.motech.model.Clinic;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.Gender;
import org.motech.model.LogType;
import org.motech.model.MaternalData;
import org.motech.model.MaternalVisit;
import org.motech.model.NotificationType;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.model.PhoneType;
import org.motech.model.Pregnancy;
import org.motech.svc.Logger;
import org.motech.svc.Registrar;
import org.motech.svc.RegistrarBean;

/**
 * Unit tests for registrar service. It currently tests more functionality than
 * it should because the DAO and other business code is intermixed with the
 * registrar service implementation.
 * 
 * @author batkinson
 * 
 */
public class RegistrarTest extends TestCase {

	private Logger mockLog;
	private SimpleDao mockDao;
	private Registrar reg;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Create mock objects
		mockLog = createMock(Logger.class);
		mockDao = createMock(SimpleDao.class);

		// Create POJO service, inject mock collaborators
		RegistrarBean regBean = new RegistrarBean();
		regBean.setLogger(mockLog);
		regBean.setDao(mockDao);

		reg = regBean;
	}

	public void testRegisterClinic() {

		String name = "A-Clinic";

		// Record the expected interaction, capturing the clinic object
		Capture<Clinic> c = new Capture<Clinic>();
		expect(mockDao.saveClinic(capture(c))).andReturn(Long.valueOf(1));
		mockLog.log((LogType) anyObject(), (String) anyObject());
		replay(mockDao, mockLog);

		// Test the method
		reg.registerClinic(name);

		// Verify that the actual interactions match the expected ones
		verify(mockDao, mockLog);

		// Check that the saved clinic has the expected name
		assertEquals(name, c.getValue().getName());
	}

	public void testRegisterNurse() {

		String name = "Jenny", phone = "8675309", clinic = "A-Clinic";

		Clinic c = new Clinic();
		c.setName(clinic);
		Capture<Nurse> nCap = new Capture<Nurse>();
		expect(mockDao.getClinic(clinic)).andReturn(c);
		expect(mockDao.saveNurse(capture(nCap))).andReturn(Long.valueOf(1));
		mockLog.log((LogType) anyObject(), (String) anyObject());
		replay(mockDao, mockLog);

		reg.registerNurse(name, phone, clinic);

		verify(mockDao, mockLog);

		Nurse n = nCap.getValue();
		assertEquals(name, n.getName());
		assertEquals(phone, n.getPhoneNumber());
		assertEquals(clinic, n.getClinic().getName());
	}

	public void testRegisterPatient() {
		String nursePhoneNumber = "8675309", serialId = "389489", name = "Jaques", community = "Community", location = "Location", phoneNumber = "5551212", language = "English";
		Date dateOfBirth = new Date();
		Gender gender = Gender.male;
		Integer nhis = 39;
		PhoneType phoneType = PhoneType.personal;
		NotificationType notificationType = NotificationType.text;

		Nurse n = new Nurse();
		Clinic c = new Clinic();
		n.setPhoneNumber(nursePhoneNumber);
		n.setClinic(c);

		Capture<Patient> pCap = new Capture<Patient>();
		expect(mockDao.getNurse(nursePhoneNumber)).andReturn(n);
		expect(mockDao.savePatient(capture(pCap))).andReturn(Long.valueOf(1));
		mockLog.log((LogType) anyObject(), (String) anyObject());
		replay(mockDao, mockLog);

		reg.registerPatient(nursePhoneNumber, serialId, name, community,
				location, dateOfBirth, gender, nhis, phoneNumber, phoneType,
				language, notificationType);

		verify(mockDao, mockLog);

		Patient p = pCap.getValue();
		assertEquals(p.getClinic(), n.getClinic());
		assertEquals(serialId, p.getSerial());
		assertEquals(name, p.getName());
		assertEquals(community, p.getCommunity());
		assertEquals(location, p.getLocation());
		assertEquals(phoneNumber, p.getPhoneNumber());
		assertEquals(dateOfBirth, p.getDateOfBirth());
		assertEquals(gender, p.getGender());
		assertEquals(nhis, p.getNhis());
	}

	public void testRegisterPregnancy() {

		String nursePhoneNumber = "8675309", serialId = "12345";
		Date date = new Date(), dueDate = new Date();
		Integer parity = 3, hemoglobin = 21;

		Nurse n = new Nurse();
		Clinic c = new Clinic();
		c.setId(1L);
		n.setPhoneNumber(nursePhoneNumber);
		n.setClinic(c);

		Patient p = new Patient();
		p.setSerial(serialId);

		expect(mockDao.getNurse(nursePhoneNumber)).andReturn(n);
		expect(mockDao.getPatient(serialId, c.getId())).andReturn(p);
		Capture<MaternalData> mCap = new Capture<MaternalData>();
		expect(mockDao.saveMaternalData(capture(mCap))).andReturn(
				Long.valueOf(1));
		mockLog.log((LogType) anyObject(), (String) anyObject());
		replay(mockDao, mockLog);

		reg.registerPregnancy(nursePhoneNumber, date, serialId, dueDate,
				parity, hemoglobin);

		verify(mockDao, mockLog);

		MaternalData m = mCap.getValue();
		Pregnancy pr = m.getPregnancies().get(0);
		assertEquals(serialId, m.getPatient().getSerial());
		assertEquals(nursePhoneNumber, pr.getNurse().getPhoneNumber());
		assertEquals(date, pr.getRegistrationDate());
		assertEquals(dueDate, pr.getDueDate());
		assertEquals(parity, pr.getParity());
		assertEquals(hemoglobin, pr.getHemoglobin());
	}

	public void testRecordMaternalVisit() {

		String nursePhoneNumber = "8675309", serialId = "12345";
		Date date = new Date();
		Integer tetanus = 20, ipt = 894, itn = 89, visitNumber = 46, onARV = 478, prePMTCT = 43, testPMTCT = 25, postPMTCT = 23, hemoglobinAt36Weeks = 38;

		Nurse n = new Nurse();
		Clinic c = new Clinic();
		c.setId(1L);
		n.setPhoneNumber(nursePhoneNumber);
		n.setClinic(c);

		Patient p = new Patient();
		p.setSerial(serialId);

		expect(mockDao.getNurse(nursePhoneNumber)).andReturn(n);
		expect(mockDao.getPatient(serialId, c.getId())).andReturn(p);
		Capture<MaternalData> mCap = new Capture<MaternalData>();
		Capture<FutureServiceDelivery> fCap = new Capture<FutureServiceDelivery>();
		expect(mockDao.saveMaternalData(capture(mCap))).andReturn(
				Long.valueOf(1L));
		mockLog.log((LogType) anyObject(), (String) anyObject());
		expect(mockDao.saveFutureServiceDelivery(capture(fCap))).andReturn(
				Long.valueOf(1L));
		mockLog.log((LogType) anyObject(), (String) anyObject());
		replay(mockDao, mockLog);

		reg.recordMaternalVisit(nursePhoneNumber, date, serialId, tetanus, ipt,
				itn, visitNumber, onARV, prePMTCT, testPMTCT, postPMTCT,
				hemoglobinAt36Weeks);

		verify(mockDao, mockLog);

		assertEquals(nursePhoneNumber, n.getPhoneNumber());
		MaternalData m = mCap.getValue();
		MaternalVisit mv = m.getMaternalVisits().get(0);
		assertEquals(serialId, m.getPatient().getSerial());
		assertEquals(date, mv.getDate());
		assertEquals(tetanus, mv.getTetanus());
		assertEquals(ipt, mv.getIpt());
		assertEquals(itn, mv.getItn());
		assertEquals(visitNumber, mv.getVisitNumber());
		assertEquals(onARV, mv.getOnARV());
		assertEquals(prePMTCT, mv.getPrePMTCT());
		assertEquals(testPMTCT, mv.getTestPMTCT());
		assertEquals(postPMTCT, mv.getPostPMTCT());
		assertEquals(hemoglobinAt36Weeks, mv.getHemoglobinAt36Weeks());
	}
}
