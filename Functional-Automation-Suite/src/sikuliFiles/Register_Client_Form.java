package sikuliFiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import org.sikuli.script.FindFailed;

public class Register_Client_Form extends SikuliBaseClass {
	
	 String btn_right = "img/btn_right.png"; 
	 String btn_down_arrow = "img/btn_down_arrow.png";
	 String btn_enter = "img/btn_enter.png";
	 String successfully_saved_message = "expected_img/successfully_saved_message.png";
	 String img_pre_upload_data_message = "expected_img/pre_upload_data_message.png";
	 String img_successfully_uploaded_data_message = "expected_img/successfully_uploaded_message_single_form.png";
	 String register_client_screen = "expected_img/register_client_screen.png"; 
	  
	public void Register_non_insured_client(String staff_id, String facility_id,String reg_date, String reg_phone_no,Client_types client_type, String first_name,String middle_name, String last_name, String preferred_name, String DOB, DOB_types DOB_type   ) throws ParseException{
				 		
      try{  
    	  // selecting the Register-client form
      	  select_form(FormName.REG_CLIENT);
          mobile_screen.find(register_client_screen);
         
         //1.Entering the staff id 
          input_textbox(staff_id);
         //2. Filling the facility_id 
          input_textbox(facility_id);
         
         //3. Registration date  
         select_date(string_to_date_convertor(reg_date));
         
         //4. Mode of registration as Auto-generated ID
         select_reg_mode(Reg_mode_values.AUTOGENRATED_ID);
         
         // 5. Filling the register phone no
         input_textbox(reg_phone_no);
         
         // 6. Filling the client Type 
         select_type_of_patient(client_type);
         
         // 7. Filling the First Name
         input_textbox(first_name);
        
        // 8. Filling the Middle Name
         input_textbox(middle_name);
        
         // 9. Filling the last Name
         input_textbox(last_name);
        
         // 10. Filling the Preferred Name
         input_textbox(preferred_name);
        
         //11. Filling the date of birth
         select_date(string_to_date_convertor(DOB));
        
         // 12. Filling whether the DOB is estimated or exact
         select_DOB_type(DOB_type);
         
         // 13. Choosing the patient as Non-Insured
        is_patient_insured(insured_type.NO);
         
        // 14. Filling the district as KND
        select_District(District.KND);
       
        // 15. Filling the sub-district as Central East
        select_subdistrict_KND(Sub_Distrcit_KND.CENTRAL_EAST);
        
        // 16. Filling the community as Abempingu A
        input_radiobutton(1);
        mobile_screen.click(btn_right,0);
        
        //17. Filling the Address
        input_textbox("Address");
                
        //18. Filling the contact phone No
        input_textbox("0123456789");
        
        //19. Filling expected Delivery date as 9 month hence the registration date
        
         SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		 java.util.Date d = sdf.parse(reg_date);
		 Calendar cal=Calendar.getInstance();
	     cal.setTime(d);
	     cal.add(Calendar.MONTH, 9);
		 System.out.println(cal.getTime());
		 select_date(cal.getTime());
               
         //20. Delivery Date confirmed by CHW
		 input_radiobutton(1);
		 mobile_screen.click(btn_right,0);
        
        //21. Join mobile midwife as no
		 input_radiobutton(2);
		 mobile_screen.click(btn_right,0);
		 
		//22. Saving the form
         save_mform();
         
         //23. Moving to Main Menu and uploading the form
         travese_to_main_menu_and_upload_form();
        
         
      } catch (FindFailed e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();}
	}
         
}


