package org.openmrs.module.motechmodule;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.logging.LogManager;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motech.svc.RegistrarBean;
import org.openmrs.module.motechmodule.web.ws.RegistrarService;
import org.openmrs.module.motechmodule.web.ws.RegistrarWebService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RegistrarServiceTest extends TestCase {

	ApplicationContext ctx;
	RegistrarService regWs;

	RegistrarBean registrarBean;

	@Override
	protected void setUp() throws Exception {
		LogManager.getLogManager().readConfiguration(
				getClass().getResourceAsStream("/jul-test.properties"));

		registrarBean = createMock(RegistrarBean.class);

		ctx = new ClassPathXmlApplicationContext("test-context.xml");

		RegistrarWebService regService = (RegistrarWebService) ctx
				.getBean("registrarService");
		regService.setRegistrarBean(registrarBean);

		regWs = (RegistrarService) ctx.getBean("registrarClient");
	}

	@Override
	protected void tearDown() throws Exception {
		regWs = null;
		ctx = null;

		registrarBean = null;

		LogManager.getLogManager().readConfiguration();
	}

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

}
