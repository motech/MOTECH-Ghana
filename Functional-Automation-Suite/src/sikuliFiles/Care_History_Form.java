package sikuliFiles;

import org.sikuli.script.FindFailed;
import bsh.ParseException;

public class Care_History_Form extends SikuliBaseClass{
	
	public void care_history_form_with_itp_value(String staff_id,String facility_id,String date,String Motech_id,IPT_values ipt, String last_ipt_date )throws ParseException{
		try {
			// selecting form 
			select_form(FormName.CARE_HISTORY);
			
			 //Entering values in TT_Non_Pregnant form 
	         //1. filling the staff id
	          input_textbox(staff_id);
	         
	          //2. Filling the facility_id 
	          input_textbox(facility_id);
	          
	         //3. Filling the date of visit 
	          select_date(string_to_date_convertor(date));  
	          
	         //4. Filling the motech id 
	          input_textbox(Motech_id);
	          
	         //5. Filling History for IPT value and last IPT date
	          select_History_status(Add_History_values.IPT);
	          select_IPT_value(ipt);
	          select_date(string_to_date_convertor(date));  
	             
	        //6. Saving the form
	          save_mform();
	          
	          //7. Moving to Main Menu and uploading the form
	          travese_to_main_menu_and_upload_form();
		} catch (FindFailed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
