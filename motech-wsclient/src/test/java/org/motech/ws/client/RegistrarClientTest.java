package org.motech.ws.client;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.GregorianCalendar;
import java.util.logging.LogManager;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motech.svc.RegistrarBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RegistrarClientTest {

	static ApplicationContext ctx;
	static RegistrarWebService regWs;
	static RegistrarBean registrarBean;

	@BeforeClass
	public static void setUpClass() throws Exception {
		LogManager.getLogManager().readConfiguration(
				RegistrarClientTest.class
						.getResourceAsStream("/jul-test.properties"));
		registrarBean = createMock(RegistrarBean.class);
		ctx = new ClassPathXmlApplicationContext("test-context.xml");
		org.motech.ws.RegistrarWebService regService = (org.motech.ws.RegistrarWebService) ctx
				.getBean("registrarService");
		regService.setRegistrarBean(registrarBean);
		regWs = new RegistrarWebServiceService().getRegistrarWebServicePort();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ctx = null;
		regWs = null;
		registrarBean = null;
		LogManager.getLogManager().readConfiguration();
	}

	@Before
	public void setup() {
	}

	@After
	public void tearDown() throws Exception {
		reset(registrarBean);
	}

	@Test
	public void testRegisterClinic() {
		String clinicName = "A-Test-Clinic-Name";

		registrarBean.registerClinic(clinicName);

		replay(registrarBean);

		regWs.registerClinic(clinicName);

		verify(registrarBean);
	}

	@Test
	public void testRegisterNurse() {
		String name = "Sally", phone = "12075555555", clinic = "C-Clinic";

		registrarBean.registerNurse(name, phone, clinic);

		replay(registrarBean);

		regWs.registerNurse(name, phone, clinic);

		verify(registrarBean);
	}

	@Test
	public void testRegisterPatient() throws DatatypeConfigurationException {
		String nPhone = "12075551212", serialId = "387946894", name = "Francis", community = "somepeople", location = "somewhere", pPhone = "120755512525";
		GregorianCalendar dob = new GregorianCalendar();
		XMLGregorianCalendar dobXml = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(dob);
		org.motech.model.Gender gender = org.motech.model.Gender.female;
		Integer nhis = 3;
		org.motech.model.PhoneType phoneType = org.motech.model.PhoneType.personal;
		String language = "English";
		org.motech.model.NotificationType notificationType = org.motech.model.NotificationType.text;

		registrarBean.registerPatient(nPhone, serialId, name, community,
				location, dob.getTime(), gender, nhis, pPhone, phoneType,
				language, notificationType);

		replay(registrarBean);

		regWs.registerPatient(nPhone, serialId, name, community, location,
				dobXml, Gender.valueOf(gender.toString().toUpperCase()), nhis,
				pPhone, PhoneType.valueOf(phoneType.toString().toUpperCase()),
				language, NotificationType.valueOf(notificationType.toString()
						.toUpperCase()));

		verify(registrarBean);
	}

	@Test
	public void testRegisterPregnancy() throws DatatypeConfigurationException {
		String nPhone = "12075551212", serialId = "387946894";
		GregorianCalendar dateCal = new GregorianCalendar(), dueDateCal = new GregorianCalendar();
		XMLGregorianCalendar dateCalXml = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(dateCal), dueDateCalXml = DatatypeFactory
				.newInstance().newXMLGregorianCalendar(dueDateCal);
		Integer parity = 3;
		Double hemo = 2.0;

		registrarBean.registerPregnancy(nPhone, dateCal.getTime(), serialId,
				dueDateCal.getTime(), parity, hemo);

		replay(registrarBean);

		regWs.registerPregnancy(nPhone, dateCalXml, serialId, dueDateCalXml,
				parity, hemo);

		verify(registrarBean);
	}

	@Test
	public void testRecordMaternalVisit() throws DatatypeConfigurationException {
		String nPhone = "12077778383", serialId = "ghj347956y";
		GregorianCalendar dateCal = new GregorianCalendar();
		XMLGregorianCalendar dateCalXml = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(dateCal);
		Boolean tetanus = true, ipt = false, itn = true, onARV = false, prePMTCT = true, testPMTCT = false, postPMTCT = true;
		Integer visitNum = 3;
		Double hemo = 378.34;

		registrarBean.recordMaternalVisit(nPhone, dateCal.getTime(), serialId,
				tetanus, ipt, itn, visitNum, onARV, prePMTCT, testPMTCT,
				postPMTCT, hemo);

		replay(registrarBean);

		regWs.recordMaternalVisit(nPhone, dateCalXml, serialId, tetanus, ipt,
				itn, visitNum, onARV, prePMTCT, testPMTCT, postPMTCT, hemo);

		verify(registrarBean);
	}

	@Test
	public void testLog() {
		org.motech.model.LogType type = org.motech.model.LogType.success;
		String msg = "logging over ws is slow";

		registrarBean.log(type, msg);

		replay(registrarBean);

		regWs.log(LogType.valueOf(type.toString().toUpperCase()), msg);

		verify(registrarBean);
	}
}
