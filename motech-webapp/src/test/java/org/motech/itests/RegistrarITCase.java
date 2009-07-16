package org.motech.itests;

import java.util.Date;

import junit.framework.TestCase;

import org.motech.model.Gender;
import org.motech.model.LogType;
import org.motech.ws.RegistrarWS;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RegistrarITCase extends TestCase {

	ApplicationContext ctx = new ClassPathXmlApplicationContext(
			"META-INF/spring/test-context.xml");
	RegistrarWS regWs = (RegistrarWS) ctx.getBean("registrarClient");

	Date time;

	@Override
	protected void setUp() throws Exception {
		time = new java.util.Date();
	}

	@Override
	protected void tearDown() throws Exception {
		time = null;
	}

	public void testRegClinic() throws Exception {
		regWs.registerClinic("A-Clinic");
	}

	public void testRegNurse() throws Exception {
		regWs.registerNurse("Gaylord", "37694676488", "A-Clinic");
	}

	public void testRegMother() throws Exception {
		regWs.registerMother("37694676488", time, "CAPNCRUNCH", "Betty",
				"KFJHKF", "HDJHJDKD", time, 313, "4674676747", time, 1, 1);
	}

	public void testRegPatient() throws Exception {
		regWs.registerPatient("37694676488", "HONEYGRAHAMS", "Super Dave",
				"DHJGDJK", "HDHJHDJK", time, Gender.male, 1232, "4674676747");

	}

	public void testRegPregnancy() throws Exception {
		regWs.registerPregnancy("37694676488", time, "HONEYGRAHAMS", time, 2,
				23);
	}

	public void testRecMaternalVisit() throws Exception {
		regWs.recordMaternalVisit("37694676488", time, "HONEYGRAHAMS", 1, 1, 1,
				1, 0, 0, 0, 0, 24);
	}

	public void testLog() throws Exception {
		regWs.log(LogType.success, "LOG MESSAGE");
	}
}