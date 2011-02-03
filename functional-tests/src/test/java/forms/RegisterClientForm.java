package forms;

import java.text.ParseException;
import java.util.Calendar;
import org.sikuli.script.FindFailed;

public class RegisterClientForm extends SikuliBase {
	
	 String btn_right = "img/btnRight.png";

	 String registerClientScreen = "expected_img/imgRegisterClientScreen.png";
	  
	public void registerNonInsuredPregnantMother(String staffId, String facilityId, String regDate, String regPhoneNo, clientTypes clientType, String firstName, String middleName, String lastName, String preferredName, String DOB, DOBTypes DOBType) throws ParseException{
				 		
      try{  
    	  // selecting the Register-client form
      	  selectForm(FormName.REG_CLIENT);
          mobileScreen.find(registerClientScreen);
         
         //1.Entering the staff id 
          inputStaffId(staffId);
         //2. Filling the facilityId
          inputTextbox(facilityId);
         
         //3. Registration date  
         selectDate(stringToDateConvertor(regDate));
         
         //4. Mode of registration as Auto-generated ID
         selectRegMode(regModeValues.AUTOGENRATED_ID);
         
         // 5. Filling the register phone no
         inputTextbox(regPhoneNo);
         
         // 6. Filling the client Type 
         selectTypeOfPatient(clientType);
         
         // 7. Filling the First Name
         inputTextbox(firstName);
        
        // 8. Filling the Middle Name
         inputTextbox(middleName);
        
         // 9. Filling the last Name
         inputTextbox(lastName);
        
         // 10. Filling the Preferred Name
         inputTextbox(preferredName);
        
         //11. Filling the date of birth
         selectDate(stringToDateConvertor(DOB));
        
         // 12. Filling whether the DOB is estimated or exact
         selectDOBType(DOBType);
         
         // 13. Choosing the patient as Non-Insured
        isPatientInsured(insuredType.NO);
         
        // 14. Filling the district as KND
        selectDistrict(district.KND);
       
        // 15. Filling the sub-district as Central East
        selectSubDistrictKND(subDistrictKND.CENTRAL_EAST);
        
        // 16. Filling the community as Abempingu A
        mobileScreen.click(btnEnter,0);
        inputRadioButton(2);
        mobileScreen.click(btn_right,0);
        
        //17. Filling the Address
        inputTextbox("Address");
                
        //18. Filling the contact phone No
        inputTextbox("0123456789");

        //19. Filling EDD as current Date
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,1);
	    selectDate(cal.getTime());

         //20. Delivery Date confirmed by CHW
         mobileScreen.click(btnEnter,0);
		 inputRadioButton(1);
		 mobileScreen.click(btn_right,0);
        
        //21. Join mobile midwife as no
		 mobileScreen.click(btnEnter,0);
         inputRadioButton(2);
		 mobileScreen.click(btn_right,0);
		 
		//22. Saving the form
         saveMform();
         
         //23. Moving to Main Menu and uploading the form
         traverseToMainMenuAndUploadForm();
        
         
      } catch (FindFailed e) {
  		// TODO Auto-generated catch block
  		e.printStackTrace();}
	}
         


    public void registerNonInsuredChild(String staffId, String facilityId, String regDate, String regPhoneNo, clientTypes clientType, String firstName, String middleName, String lastName, String preferredName, String DOB, DOBTypes DOBType,clientGender genderType,String motherMotechId ) throws ParseException{

          try{
              // selecting the Register-client form
              selectForm(FormName.REG_CLIENT);
              if(imgExist(registerClientScreen,3.00)){

                 //1.Entering the staff id
                  inputStaffId(staffId);
                 //2. Filling the facilityId
                  inputTextbox(facilityId);

                 //3. Registration date
                 selectDate(stringToDateConvertor(regDate));

                 //4. Mode of registration as Auto-generated ID
                 selectRegMode(regModeValues.AUTOGENRATED_ID);

                 // 5. Filling the register phone no
                 inputTextbox(regPhoneNo);

                 // 6. Filling the client Type
                 selectTypeOfPatient(clientType);

                 // 7. Filling the First Name
                 inputTextbox(firstName);

                // 8. Filling the Middle Name
                 inputTextbox(middleName);

                 // 9. Filling the last Name
                 inputTextbox(lastName);

                 // 10. Filling the Preferred Name
                 inputTextbox(preferredName);

                 //11. Filling the date of birth
                 selectDate(stringToDateConvertor(DOB));

                 // 12. Filling whether the DOB is estimated or exact
                 selectDOBType(DOBType);

                 //13 . Filling the client's gender
                 selectGender(genderType);

                 // 14. Choosing the patient as Non-Insured
                isPatientInsured(insuredType.NO);

                // 15. Filling the Mother's motech id
                 inputTextbox(motherMotechId);

                // 16. Filling the district as KND
                selectDistrict(district.KND);

                // 17. Filling the sub-district as Central East
                selectSubDistrictKND(subDistrictKND.CENTRAL_EAST);

                // 18. Filling the community as Abempingu A
                mobileScreen.click(btnEnter,0);
                inputRadioButton(2);
                mobileScreen.click(btn_right,0);

                //19. Filling the Address
                inputTextbox("Address");

                //20. Filling the contact phone No
                inputTextbox("0123456789");

                //21. Join mobile midwife as no
                 mobileScreen.click(btnEnter,0);
                 inputRadioButton(2);
                 mobileScreen.click(btn_right,0);

                //22. Saving the form
                 saveMform();

                 //23. Moving to Main Menu and uploading the form
                 traverseToMainMenuAndUploadForm();
              }

          }
          catch (FindFailed e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
    }

}

