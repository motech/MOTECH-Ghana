package forms;
import java.text.ParseException;
import org.sikuli.script.FindFailed;

public class TTNonPregnantForm extends SikuliBase {

    public void fillTTNonPregnantForm(String staffId, String facilityId, String date, String motechId, TTValues str) throws ParseException
	{ 
      try {
	     // selecting the TT_Non_pregnant form
         selectForm(FormName.TT_NON_PREGNANT);
          
         //Entering values in TT_Non_Pregnant form 
         //1. filling the staff id
          inputStaffId(staffId);
         
          //2. Filling the facility_id 
          inputTextbox(facilityId);
          
         //3. Filling the date of visit as current date 
          selectDate(stringToDateConvertor(date));
          
         //4.  Filling the motech id 
          inputTextbox(motechId);
          
         // 5. Filling the TT value as TT1
          selectTTValues(str);
                   
          //6. Saving the form
          saveMform();
          
          //7. Moving to Main Menu and uploading the form
          traverseToMainMenuAndUploadForm();
          closeMobileApp();
       
	} catch (FindFailed e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}
	

