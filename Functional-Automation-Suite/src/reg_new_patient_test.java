
import org.testng.annotations.Test;
import sikuliFiles.Register_Client_Form;
import sikuliFiles.SikuliBaseClass.Client_types;
import sikuliFiles.SikuliBaseClass.DOB_types;


import bsh.ParseException;
public class reg_new_patient_test {

	@Test
	public void test()throws ParseException, java.text.ParseException{
	Register_Client_Form obj1 = new Register_Client_Form();
	String staff_id = "465";
	String facility_id = "11117";
	String reg_date = "1/4/2010";
	String reg_phone_no ="0123456789";
	String first_name ="Mom";
	String middle_name ="middle";
	String last_name ="1";
	String preferred_name = "preferred";
	String DOB ="1/1/1994";
    obj1.Register_non_insured_client(staff_id, facility_id, reg_date, reg_phone_no, Client_types.PREGNANT_MOTHER, first_name, middle_name, last_name, preferred_name, DOB, DOB_types.EXACT_DOB);
		
	}


}



