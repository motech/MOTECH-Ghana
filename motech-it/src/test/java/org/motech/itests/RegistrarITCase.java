package org.motech.itests;

import java.net.URL;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.motech.ws.Registrar;
import org.motech.ws.RegistrarService;

public class RegistrarITCase extends TestCase {

	XMLGregorianCalendar time;
	Registrar reg;

	@Override
	protected void setUp() throws Exception {
		reg = new RegistrarService(new URL(
				"http://127.0.0.1:8080/motechws/RegistrarService?wsdl"),
				new QName("http://ws.motech.org/", "RegistrarService"))
				.getRegistrarPort();
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
				"DHJGDJK", "HDHJHDJK", 23, "male", 1232, "4674676747");
	}

	public void testRegPregnancy() throws Exception {
		reg.registerPregnancy("37694676488", time, "HONEYGRAHAMS", time, 2, 23);
	}

}