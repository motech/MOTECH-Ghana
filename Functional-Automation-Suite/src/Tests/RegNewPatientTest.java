package Tests;

import org.testng.annotations.Test;
import sikuliFiles.RegisterClientForm;
import sikuliFiles.SikuliBaseClass.*;
import sikuliFiles.SikuliBaseClass.DOBTypes;


import bsh.ParseException;
public class RegNewPatientTest {

	@Test
	public void test()throws ParseException, java.text.ParseException{
	RegisterClientForm obj = new RegisterClientForm();
	String staffId = "465";
	String facilityId = "11117";
	String regDate = "1/4/2010";
	String regPhoneNo ="0123456789";
	String firstName ="Mom";
	String middleName ="middle";
	String lastName ="1";
	String preferredName = "preferred";
	String DOB ="1/1/1994";
    obj.registerNonInsuredPregnantMother(staffId, facilityId, regDate, regPhoneNo, clientTypes.PREGNANT_MOTHER, firstName, middleName, lastName, preferredName, DOB, DOBTypes.EXACT_DOB);
		
	}


}



