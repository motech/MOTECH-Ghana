package sikuliFiles;
import org.sikuli.script.FindFailed;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.sikuli.script.Match;
import org.sikuli.script.Screen;
import java.util.Calendar;



public class SikuliBaseClass {
public static Screen screen;
public static Match mobileScreen;
String btnLeft = "img/btnLeft.png";
String btnRight = "img/btnRight.png";
String btnLeftArrow = "img/btnLeftArrow.png";
String btnRightArrow = "img/btnRightArrow.png";
String btnDownArrow = "img/btnDownArrow.png";
String btnUpArrow = "img/btnUpArrow.png";
String btnEnter = "img/btnEnter.png";
String btnMiddletApp = "img/Middle_App.png";
String loginScreen = "expected_img/imgLoginScreen.png";
String mainMenuScreen = "expected_img/imgMainMenuScreen.png";
String btnBackspace = "img/btnBackspace.png";
String expectedConnectionSetting = "expected_img/imgExpectedConnectionSetting.png";
String selectStudyScreen = "img/imgSelectStudyScreen.png";
String imgNurseDataEntryFormScreen = "expected_img/imgNurseDataEntryForms.png";
String successfullySavedMessage = "expected_img/imgSuccessfullySavedMessage.png";
String imgPreUploadDataMessage = "expected_img/imgPreUploadDataMessage.png";
String imgSuccessfullyUploadedDataMessage = "expected_img/imgSuccessfullyUploadedMessageSingleForm.png";
String btnWindows = "img/btnWindows.png" ;

	public SikuliBaseClass (){

		screen = new Screen();   
		try {
			screen.click(btnWindows, 0);
			screen.type(null,"C:/MoTech/Openxdata/epihandy-midlet/dist/obf/motech-mforms-tiny.jad"+"\n",0);
			screen.exists("expected_img/imgExpectedBeforeLaunch.png", 3000);
			mobileScreen = screen.find("expected_img/imgExpectedBeforeLaunch.png");
			loginConnectAndDownloadForm();
		} catch (FindFailed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	};
	
	public void loginConnectAndDownloadForm() throws FindFailed {
		
		// Login into the Motech app
	    mobileScreen.click(btnRight,0);
	    mobileScreen.find(loginScreen);
	    mobileScreen.type(null,"motech",0);
	    mobileScreen.click(btnDownArrow,0);
	    mobileScreen.type(null,"ghs",0);
		mobileScreen.click(btnRight,0);
	    mobileScreen.exists(mainMenuScreen);
	    
		// Down load Nurse Data EntryStudy 
	     mobileScreen.click(btnUpArrow,0);
	     mobileScreen.click(btnEnter,0);
	     
	     //Connection setting 
	     
	     for (int i=0;i<70;i++)
	     {
	    	 mobileScreen.click(btnBackspace, 0);
	     }
	     mobileScreen.type(null,"http://localhost:8080/motech-mobile-webapp/formdownload",0);
	     mobileScreen.click(btnDownArrow,0);
	     
	     for (int i=0;i<70;i++)
	     {
	    	 mobileScreen.click(btnBackspace, 0);
	     }
	     mobileScreen.type(null,"http://localhost:8080/motech-mobile-webapp/formupload",0);
	     mobileScreen.click(btnRight,0);
	     mobileScreen.find(expectedConnectionSetting);
	    
	     mobileScreen.click(btnRight,0);
	     
	     // Selecting Nurse Data Entry Study and Form
	     mobileScreen.wait(selectStudyScreen,20000);
	     
        // mobileScreen.find(selectStudyScreen);
         mobileScreen.click(btnRight,0);
         mobileScreen.click(btnDownArrow,0);
         mobileScreen.click(btnRight,0);
        
         mobileScreen.wait(imgNurseDataEntryFormScreen,5000);
	}
	
	public void inputTextbox(String str) throws FindFailed{
		mobileScreen.click(btnEnter,0);
        mobileScreen.type(null,str,0);
        mobileScreen.click(btnRight,0);
	}
	
	
	public void saveMform()throws FindFailed{
		mobileScreen.click(btnRight,0);
        mobileScreen.wait(successfullySavedMessage,2000);
        mobileScreen.click(btnRight,0);
	}
	
	public void traverseToMainMenuAndUploadForm() throws FindFailed{
		 mobileScreen.click(btnRight,0);
         for (int i=0;i<2;i++){
         mobileScreen.click(btnDownArrow, 0);
         }
         mobileScreen.click(btnEnter,0);
         for (int i=0;i<3;i++){
         mobileScreen.click(btnDownArrow, 0);
         }
         mobileScreen.click(btnEnter,0);
         mobileScreen.wait(imgPreUploadDataMessage,2000);
         
         // 8. Uploading the form
         mobileScreen.click(btnRight,0);
         mobileScreen.find(imgSuccessfullyUploadedDataMessage);
         mobileScreen.click(btnRight,0);
	}
	public void selectDate(Date date)throws FindFailed, ParseException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date td = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
		System.out.println(td);
	    @SuppressWarnings("deprecation")
		int y =  date.getYear()-td.getYear();
		@SuppressWarnings("deprecation")
		int m =  date.getMonth()- td.getMonth();
		@SuppressWarnings("deprecation")
		int d = date.getDate();
		
		for (int i=0;i<2;i++){
      	  mobileScreen.click(btnEnter,0);
	    }
		
		// Navigating to year
		if (y>=0){
			for (int i = 0 ; i<y;i++){
				mobileScreen.click(btnRightArrow, 0);
			}
		} else 
		{ y = -y;
		    for (int i =0 ; i<y; i++){
		    	mobileScreen.click(btnLeftArrow, 0);
		    }
		}
		
		mobileScreen.click(btnDownArrow,0);
		
		// Navigating to month 
		if (m>=0){
			for (int i = 0 ; i<m;i++){
				mobileScreen.click(btnRightArrow, 0);
			}
		} else 
		{ m = -m;
		    for (int i =0 ; i<m; i++){
		    	mobileScreen.click(btnLeftArrow, 0);
		    }
		}
		mobileScreen.click(btnDownArrow,0);
		
		// Selecting the date
		 for (int i= 1; i<d; i++){
			 mobileScreen.click(btnRightArrow, 0);
		 }
		
		 // Navigating back 
		  for (int i=0;i<2;i++){
       	  mobileScreen.click(btnRight,0);
 	     }
	}
	
	public enum FormName{ANC_VISIT,CARE_HISTORY,CWC_VISIT,DELIVERY,DELIVERY_NOTIFICATION,EDIT_CLIENT,OUTPATIENT_VISIT,CLIENT_DEATH,PNC_BABY,PNC_MOTHER,PREG_TERMINATION,REG_ANC,REG_CWC,REG_CLIENT,TT_NON_PREGNANT}
	public enum TTValues {TT1,TT2,TT3,TT4,TT5};
	public enum regModeValues {PRE_PRINTED_ID,AUTOGENRATED_ID};
	public enum clientTypes {PREGNANT_MOTHER,CHILD_UNDER_5,OTHER};
	public enum DOBTypes {ESTIMATED_DOB,EXACT_DOB};
	public enum District {KND, KNW};
	public enum SubDistrictKND {CENTRAL_EAST,CENTRAL_NAVRONGO,CENTRAL_NORTH,CENTRAL_NORTH_EAST,CENTRAL_SOUTH,CENTRAL_WEST};
	public enum SubDistrictKNW {NAVIO,KATIU_NAKONG,KAYORO,CHIANA,KANDIGA_KUGURU,SIRIGU,PAGA_CENTRAL,MIRIGU};
	public enum insuredType {YES,NO};
	public enum addHistoryValues {IPT,TT,Vit_A,IPTi,BCG,OPV,PENTA,MEASLES,YF};
	public enum IPTValues {IPT1,IPT2,IPT3};
	
	public void selectForm(FormName str) throws FindFailed{
	     FormName form = str;

	     switch (form){
	     case ANC_VISIT: inputRadioButton(1);break;
	     case CARE_HISTORY: inputRadioButton(2);break;
         case CWC_VISIT: inputRadioButton(3);break;
         case DELIVERY: inputRadioButton(4);break;
         case DELIVERY_NOTIFICATION: inputRadioButton(5);break;
         case EDIT_CLIENT: inputRadioButton(6);break;
         case OUTPATIENT_VISIT: inputRadioButton(7);break;
         case CLIENT_DEATH: inputRadioButton(8);break;
         case PNC_BABY: inputRadioButton(9);break;
         case PNC_MOTHER: inputRadioButton(10);break;
         case PREG_TERMINATION: inputRadioButton(11);break;
         case REG_ANC: inputRadioButton(12);break;
         case REG_CWC: inputRadioButton(13);break;
         case REG_CLIENT: inputRadioButton(14);break;
	     case TT_NON_PREGNANT: inputRadioButton(15);break;
         default: System.out.println("Form Does not exist");break; 
	                     
	     }  
    }
	
	public void selectTTValues(TTValues str) throws FindFailed{
	     TTValues tt = str ;

	     switch (tt){
	     case TT1: mobileScreen.click(btnEnter,0);inputRadioButton(1);
             mobileScreen.click(btnRight,0);break;
	     case TT2: mobileScreen.click(btnEnter,0);inputRadioButton(2);
             mobileScreen.click(btnRight,0);break;
         case TT3: mobileScreen.click(btnEnter,0);inputRadioButton(3);
             mobileScreen.click(btnRight,0);break;
         case TT4: mobileScreen.click(btnEnter,0);inputRadioButton(4);
             mobileScreen.click(btnRight,0);break;
         case TT5: mobileScreen.click(btnEnter,0);inputRadioButton(5);
             mobileScreen.click(btnRight,0);break;
         default: System.out.println("Given TT values does not exist");break;
	                     
	     }
   }
	public void selectRegMode(regModeValues str) throws FindFailed{
	     regModeValues reg = str ;

	     switch (reg){
	     case PRE_PRINTED_ID: mobileScreen.click(btnEnter,0);inputRadioButton(1);
             mobileScreen.click(btnRight,0);break;
	     case AUTOGENRATED_ID: mobileScreen.click(btnEnter,0);inputRadioButton(2);
             mobileScreen.click(btnRight,0);break;
         default: System.out.println("Given Reg_mode value does not exist");break; 
	     }
  }
	public void selectDOBType(DOBTypes str) throws FindFailed{
	     DOBTypes dobType = str ;
	    
	     switch (dobType){
	     case ESTIMATED_DOB: mobileScreen.click(btnEnter,0);inputRadioButton(1);
             mobileScreen.click(btnRight,0);break;
	     case EXACT_DOB: mobileScreen.click(btnEnter,0);inputRadioButton(2);
             mobileScreen.click(btnRight,0);break;
         default: System.out.println("Given DOB type value does not exist");break; 
	     }
	}	
	public void selectDistrict(District str) throws FindFailed{
	     District dist = str ;
	    
	     switch (dist){
	     case KND: mobileScreen.click(btnEnter,0);inputRadioButton(1);
             mobileScreen.click(btnRight,0);break;
	     case KNW: mobileScreen.click(btnEnter,0);inputRadioButton(2);
             mobileScreen.click(btnRight,0);break;
         default: System.out.println("Given District value does not exist");break; 
	     }
	}
	public void selectTypeOfPatient(clientTypes str) throws FindFailed{
	     clientTypes clientType = str ;

	     switch (clientType){
	     case PREGNANT_MOTHER: mobileScreen.click(btnEnter,0);inputRadioButton(1);
             mobileScreen.click(btnRight,0);break;
	     case CHILD_UNDER_5: mobileScreen.click(btnEnter,0);inputRadioButton(2);
             mobileScreen.click(btnRight,0);break;
	     case OTHER:
             mobileScreen.click(btnEnter,0);inputRadioButton(3);
             mobileScreen.click(btnRight,0);break;
         default: System.out.println("Given Reg_mode value does not exist");break;
	     }
    }
	public void selectHistoryStatus(addHistoryValues str) throws FindFailed{
	    addHistoryValues hist = str;
	     
	     switch (hist){
	     case IPT: mobileScreen.click(btnEnter,0);inputRadioButton(1); mobileScreen.click(btnRight,0);break;
	     case TT: mobileScreen.click(btnEnter,0);inputRadioButton(2); mobileScreen.click(btnRight,0);break;
         case Vit_A: mobileScreen.click(btnEnter,0);inputRadioButton(3); mobileScreen.click(btnRight,0);break;
         case IPTi: mobileScreen.click(btnEnter,0);inputRadioButton(4); mobileScreen.click(btnRight,0);break;
         case BCG: mobileScreen.click(btnEnter,0);inputRadioButton(5); mobileScreen.click(btnRight,0);break;
         case OPV: mobileScreen.click(btnEnter,0);inputRadioButton(5); mobileScreen.click(btnRight,0);break;
         case PENTA: mobileScreen.click(btnEnter,0);inputRadioButton(5); mobileScreen.click(btnRight,0);break;
         case MEASLES: mobileScreen.click(btnEnter,0);inputRadioButton(5); mobileScreen.click(btnRight,0);break;
         case YF: mobileScreen.click(btnEnter,0);inputRadioButton(5); mobileScreen.click(btnRight,0);break;
         default: System.out.println("Given Add_History values does not exist");break; 
	   }
    }
	
	public void selectIPTValue(IPTValues str) throws FindFailed{
	    IPTValues ipt = str;
	     
	     switch (ipt){
	     case IPT1: mobileScreen.click(btnEnter,0);inputRadioButton(1);
             mobileScreen.click(btnRight,0);break;
	     case IPT2: mobileScreen.click(btnEnter,0);inputRadioButton(2);
             mobileScreen.click(btnRight,0);break;
         case IPT3: mobileScreen.click(btnEnter,0);inputRadioButton(3);
             mobileScreen.click(btnRight,0);break;
         default: System.out.println("Given IPT values does not exist");break;
	   }
    }
	public void isPatientInsured(insuredType str) throws FindFailed{
	     insuredType ins = str ;
	    
	     switch (ins){
	     case YES: mobileScreen.click(btnEnter,0);inputRadioButton(1);
             mobileScreen.click(btnRight,0);break;
	     case NO: mobileScreen.click(btnEnter,0);inputRadioButton(2);
             mobileScreen.click(btnRight,0);break;
         default: System.out.println("Given Insured type does not exist");break;
	     }
	}
	public void selectSubDistrictKND(SubDistrictKND str) throws FindFailed{
	     SubDistrictKND subDir = str ;
	    
	     switch (subDir){
	     case CENTRAL_EAST: mobileScreen.click(btnEnter,0);inputRadioButton(1);
             mobileScreen.click(btnRight,0);break;
	     case CENTRAL_NORTH: mobileScreen.click(btnEnter,0);inputRadioButton(2);
             mobileScreen.click(btnRight,0);break;
	     case CENTRAL_NAVRONGO: mobileScreen.click(btnEnter,0);inputRadioButton(2);
             mobileScreen.click(btnRight,0);break;
	     case CENTRAL_NORTH_EAST: mobileScreen.click(btnEnter,0);inputRadioButton(2);
             mobileScreen.click(btnRight,0);break;
	     case CENTRAL_SOUTH: mobileScreen.click(btnEnter,0);inputRadioButton(2);
             mobileScreen.click(btnRight,0);break;
	     case CENTRAL_WEST: mobileScreen.click(btnEnter,0);inputRadioButton(2);
             mobileScreen.click(btnRight,0);break;
         default: System.out.println("Given subdistrict does not exist");break; 
	     }
	}
	
	
	public void inputRadioButton(int n)throws FindFailed{
		 for (int i= 1;i<n;i++)
	     {
	    	 mobileScreen.click(btnDownArrow, 0);
	     }
         mobileScreen.click(btnEnter,0);
	 }
	
	
	public Date stringToDateConvertor(String date)throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date d = sdf.parse(date);
		return d;
	}
}