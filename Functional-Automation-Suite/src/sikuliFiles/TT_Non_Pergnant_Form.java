package sikuliFiles;
import java.text.ParseException;
import org.sikuli.script.FindFailed;

public class TT_Non_Pergnant_Form extends SikuliBaseClass{
	
	String btn_right = "img/btn_right.png"; 
	String btn_down_arrow = "img/btn_down_arrow.png";
	String btn_enter = "img/btn_enter.png";	
    public void Fill_TT_Non_Pregnant_Form(String staff_id, String facility_id,String date,String Motech_id, TT_values str) throws ParseException
	{ 
      try {
	     // selecting the TT_Non_pregnant form
    	  select_form(FormName.TT_NON_PREGNANT);
          
         //Entering values in TT_Non_Pregnant form 
         //1. filling the staff id
          input_textbox(staff_id);
         
          //2. Filling the facility_id 
          input_textbox(facility_id);
          
         //3. Filling the date of visit as current date 
          select_date(string_to_date_convertor(date));  
          
         //4.  Filling the motech id 
          input_textbox(Motech_id);
          
         // 5. Filling the TT value as TT1
          
          select_TT_values(str);
                   
          //6. Saving the form
          save_mform();
          
          //7. Moving to Main Menu and uploading the form
          travese_to_main_menu_and_upload_form();
       
	} catch (FindFailed e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
	

