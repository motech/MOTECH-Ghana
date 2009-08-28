package org.openmrs.module.motechmodule;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.logging.LogManager;

import org.easymock.Capture;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motech.svc.RegistrarBean;
import org.openmrs.module.motechmodule.web.ws.RegistrarService;
import org.openmrs.module.motechmodule.web.ws.RegistrarWebService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RegistrarServiceTest {

	static ApplicationContext ctx;
	static RegistrarService regWs;
	static RegistrarBean registrarBean;

	@BeforeClass
	public static void setUpClass() throws Exception {
		LogManager.getLogManager().readConfiguration(
				RegistrarServiceTest.class
						.getResourceAsStream("/jul-test.properties"));
		registrarBean = createMock(RegistrarBean.class);
		ctx = new ClassPathXmlApplicationContext("test-context.xml");
		RegistrarWebService regService = (RegistrarWebService) ctx
				.getBean("registrarService");
		regService.setRegistrarBean(registrarBean);
		regWs = (RegistrarService) ctx.getBean("registrarClient");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ctx = null;
		regWs = null;
		registrarBean = null;
		LogManager.getLogManager().readConfiguration();
	}

	@After
	public void tearDown() throws Exception {
		reset(registrarBean);
	}

	@Test
	public void testRegisterClinic() {
		Capture<String> stringCap = new Capture<String>();
		registrarBean.registerClinic(capture(stringCap));

		replay(registrarBean);

		String clinicName = "A-Test-Clinic-Name";
		regWs.registerClinic(clinicName);

		verify(registrarBean);

		String capturedName = stringCap.getValue();
		assertEquals(clinicName, capturedName);
	}

	@Test
	public void testRegisterNurse() {
		Capture<String> nameCap = new Capture<String>();
		Capture<String> phoneCap = new Capture<String>();
		Capture<String> clinicCap = new Capture<String>();

		registrarBean.registerNurse(capture(nameCap), capture(phoneCap),
				capture(clinicCap));

		replay(registrarBean);

		String name = "Sally", phone = "12075555555", clinic = "C-Clinic";
		regWs.registerNurse(name, phone, clinic);

		verify(registrarBean);

		assertEquals(name, nameCap.getValue());
		assertEquals(phone, phoneCap.getValue());
		assertEquals(clinic, clinicCap.getValue());
	}
}
