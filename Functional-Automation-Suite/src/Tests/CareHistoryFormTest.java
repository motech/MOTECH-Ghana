package Tests;

import org.testng.annotations.Test;
import sikuliFiles.CareHistoryForm;
import sikuliFiles.SikuliBaseClass;

import bsh.ParseException;


public class CareHistoryFormTest {
	@Test
	public void test()throws ParseException, java.text.ParseException{
	CareHistoryForm obj = new CareHistoryForm();
	String staffId = "465";
	String facilityId = "11117";
	String motechId ="1234568";
    obj.careHistoryFormWithItpValue(staffId, facilityId, "01/01/2011", motechId, SikuliBaseClass.IPTValues.IPT1, "02/12/2010");
		
	}
}