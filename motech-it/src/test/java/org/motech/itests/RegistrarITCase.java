package org.motech.itests;

import java.io.InputStream;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.motech.Gender;
import org.motech.Registrar;
import org.motech.RegistrarService;

public class RegistrarITCase extends TestCase {

	XMLGregorianCalendar time;
	Registrar reg;

	@Override
	protected void setUp() throws Exception {
		Properties testProps = new Properties();
		InputStream testPropStream = getClass().getResourceAsStream(
				"/motech-it-test.properties");
		testProps.load(testPropStream);
		reg = new RegistrarService(new URL(testProps
				.getProperty("motech.wsdlurl")), new QName(
				"http://motech.org/", "RegistrarService")).getRegistrarPort();
		time = DatatypeFactory.newInstance().newXMLGregorianCalendar(
				new GregorianCalendar());
	}

	@Override
	protected void tearDown() throws Exception {
		reg = null;
		time = null;
	}

	public void testRegNurse() throws Exception {
		reg.registerNurse("Gaylord", "37694676488", "A-Clinic");
	}

	public void testRegMother() throws Exception {
		reg.registerMother("37694676488", time, "CAPNCRUNCH", "Betty",
				"KFJHKF", "HDJHJDKD", 12, 313, "4674676747", time, 1, 1);
	}

	public void testRegPatient() throws Exception {
		reg.registerPatient("37694676488", "HONEYGRAHAMS", "Super Dave",
				"DHJGDJK", "HDHJHDJK", 23, Gender.MALE, 1232, "4674676747");
	}

	public void testRegPregnancy() throws Exception {
		reg.registerPregnancy("37694676488", time, "HONEYGRAHAMS", time, 2, 23);
	}

	public void testRecMaternalVisit() throws Exception {
		reg.recordMaternalVisit("37694676488", time, "HONEYGRAHAMS", 1, 1, 1,
				1, 0, 0, 0, 0, 24);
	}
}