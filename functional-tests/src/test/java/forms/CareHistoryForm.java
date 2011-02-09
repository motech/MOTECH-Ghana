package forms;

import java.text.ParseException;

public class CareHistoryForm extends SikuliBase {
	
	public void careHistoryFormWithItpValue(String staff_id, String facility_id, String date, String Motech_id, IPTValues ipt, String last_ipt_date)throws ParseException {
		try {
			// selecting form
            selectForm(FormName.CARE_HISTORY);
			
			 //Entering values in TT_Non_Pregnant form 
	         //1. filling the staff id
	          inputStaffId(staff_id);
	         
	          //2. Filling the facility_id 
	          inputTextbox(facility_id);
	          
	         //3. Filling the date of visit 
	          selectDate(stringToDateConvertor(date));
	          
	         //4. Filling the motech id 
	          inputTextbox(Motech_id);
	          
	         //5. Filling History for IPT value and last IPT date
	          selectHistoryStatus(addHistoryValues.IPT);
	          selectIPTValue(ipt);
	          selectDate(stringToDateConvertor(date));
	             
	        //6. Saving the form
	          saveMform();
	          
	          //7. Moving to Main Menu and uploading the form
	          traverseToMainMenuAndUploadForm();
              closeMobileApp();
		}
        catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
