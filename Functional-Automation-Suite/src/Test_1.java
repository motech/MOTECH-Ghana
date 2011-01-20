

import java.text.ParseException;

import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.*;
import WebdriverFiles.RegisterStaff;
import sikuliFiles.TT_Non_Pergnant_Form;
import sikuliFiles.SikuliBaseClass.TT_values;


public class Test_1 {

	@Test
	public void Register_new_staff_And_Fill_TTNonPregnantForm() throws ParseException {
		{
			RegisterStaff reg_staff = new RegisterStaff() ;
			WebElement str_staff_id = reg_staff.createNewStaff();
			Assert.assertTrue(str_staff_id.getText().contains("Added user: Name ="));
			String staff_id = reg_staff.extract_staffid(str_staff_id);
			TT_Non_Pergnant_Form obj1 = new TT_Non_Pergnant_Form();
		    obj1.Fill_TT_Non_Pregnant_Form(staff_id,"11117","10/12/2010","1234568",TT_values.TT1);
		}
	 }
}

