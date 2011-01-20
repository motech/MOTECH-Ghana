package Tests;

import org.testng.annotations.Test;
import sikuliFiles.CareHistoryForm;
import sikuliFiles.SikuliBaseClass;

import bsh.ParseException;


public class CareHistoryFormTest {
	@Test
	public void test()throws ParseException, java.text.ParseException{
	CareHistoryForm obj1 = new CareHistoryForm();
	String staffId = "465";
	String facilityId = "11117";
	String motechId ="1234568";
    obj1.care_history_form_with_itp_value(staffId,facilityId,"01/01/2011",motechId, SikuliBaseClass.IPTValues.IPT1, "02/12/2010");
		
	}
}