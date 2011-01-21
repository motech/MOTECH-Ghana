package Tests;


import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.*;
import WebdriverFiles.RegisterStaff;
import sikuliFiles.SikuliBaseClass;
import sikuliFiles.TTNonPergnantForm;

import java.text.ParseException;


public class RegisterStaffAndFillTTNonPregnantForm {

	@Test
	public void registerNewStaffAndFillTTNonPregnantForm() throws ParseException {
		{
			RegisterStaff regStaff = new RegisterStaff() ;
			WebElement strStaffId = regStaff.createNewStaff();
			Assert.assertTrue(strStaffId.getText().contains("Added user: Name ="));
			String staffId = regStaff.extractStaffId(strStaffId);
            TTNonPergnantForm obj1 = new TTNonPergnantForm();
            String facilityId = "11117";
            String date = "10/12/2010";
            String motechId ="1234568";
		    obj1.Fill_TT_Non_Pregnant_Form(staffId,facilityId,date,motechId, SikuliBaseClass.TTValues.TT1);
		}
	 }
}

