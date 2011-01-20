
import org.testng.annotations.Test;
import sikuliFiles.Care_History_Form;
import sikuliFiles.SikuliBaseClass.IPT_values;

import bsh.ParseException;


public class care_history_form_test {
	@Test
	public void test()throws ParseException, java.text.ParseException{
	Care_History_Form obj1 = new Care_History_Form();
	String staff_id = "465";
	String facility_id = "11117";
	String motech_id ="1234568";
    obj1.care_history_form_with_itp_value(staff_id,facility_id,"01/01/2011",motech_id,IPT_values.IPT1, "02/12/2010");
		
	}
}