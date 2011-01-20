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
public static Match mobile_screen;
String btn_left = "img/btn_left.png";
String btn_right = "img/btn_right.png"; 
String btn_left_arrow = "img/btn_left_arrow.png";
String btn_right_arrow = "img/btn_right_arrow.png";
String btn_down_arrow = "img/btn_down_arrow.png";
String btn_up_arrow = "img/btn_up_arrow.png";
String btn_enter = "img/btn_enter.png";
String btn_middlet_app = "img/Middle_App.png";
String login_screen = "expected_img/login_screen.png";
String main_menu_screen = "expected_img/main_menu_screen.png";
String btn_backspace = "img/btn_backspace.png";
String expected_connection_setting = "expected_img/expected_connection_setting.png";
String select_study_screen = "img/Select_study_screen.png";
String img_nurse_data_entry_form_screen = "expected_img/nurse_data_entry_forms.png";
String successfully_saved_message = "expected_img/successfully_saved_message.png";
String img_pre_upload_data_message = "expected_img/pre_upload_data_message.png";
String img_successfully_uploaded_data_message = "expected_img/successfully_uploaded_message_single_form.png";
	
	public SikuliBaseClass (){
		String btn_win  = "img/btn_win.png" ;
		screen = new Screen();   
		try {
			screen.click(btn_win, 0);
			screen.type(null,"C:/MoTech/Openxdata/epihandy-midlet/dist/obf/motech-mforms-tiny.jad"+"\n",0);
			screen.exists("expected_img/expected_before_launch.png", 3000);
			mobile_screen = screen.find("expected_img/expected_before_launch.png");
			loginConnectAndDownloadForm();
		} catch (FindFailed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
	};
	
	public void loginConnectAndDownloadForm() throws FindFailed {
		
		// Login into the Motech app
	    mobile_screen.click(btn_right,0);
	    mobile_screen.find(login_screen);
	    mobile_screen.type(null,"motech",0);
	    mobile_screen.click(btn_down_arrow,0);
	    mobile_screen.type(null,"ghs",0);
		mobile_screen.click(btn_right,0);
	    mobile_screen.exists(main_menu_screen);  
	    
		// Down load Nurse Data EntryStudy 
	     mobile_screen.click(btn_up_arrow,0);
	     mobile_screen.click(btn_enter,0);
	     
	     //Connection setting 
	     
	     for (int i=0;i<70;i++)
	     {
	    	 mobile_screen.click(btn_backspace, 0);
	     }
	     mobile_screen.type(null,"http://localhost:8080/motech-mobile-webapp/formdownload",0);
	     mobile_screen.click(btn_down_arrow,0);
	     
	     for (int i=0;i<70;i++)
	     {
	    	 mobile_screen.click(btn_backspace, 0);
	     }
	     mobile_screen.type(null,"http://localhost:8080/motech-mobile-webapp/formupload",0);
	     mobile_screen.click(btn_right,0);
	     mobile_screen.find(expected_connection_setting);
	    
	     mobile_screen.click(btn_right,0);
	     
	     // Selecting Nurse Data Entry Study and Form
	     mobile_screen.wait(select_study_screen,20000);
	     
        // mobile_screen.find(select_study_screen);
         mobile_screen.click(btn_right,0);
         mobile_screen.click(btn_down_arrow,0);
         mobile_screen.click(btn_right,0);
        
         mobile_screen.wait(img_nurse_data_entry_form_screen,5000);
	}
	
	public void input_textbox(String str) throws FindFailed{
		mobile_screen.click(btn_enter,0);
        mobile_screen.type(null,str,0);
        mobile_screen.click(btn_right,0);
	}
	
	
	public void save_mform()throws FindFailed{
		mobile_screen.click(btn_right,0);
        mobile_screen.wait(successfully_saved_message,2000);
        mobile_screen.click(btn_right,0);
	}
	
	public void travese_to_main_menu_and_upload_form() throws FindFailed{
		 mobile_screen.click(btn_right,0);
         for (int i=0;i<2;i++){
         mobile_screen.click(btn_down_arrow, 0);
         }
         mobile_screen.click(btn_enter,0);
         for (int i=0;i<3;i++){
         mobile_screen.click(btn_down_arrow, 0);
         }
         mobile_screen.click(btn_enter,0);
         mobile_screen.wait(img_pre_upload_data_message,2000);
         
         // 8. Uploading the form
         mobile_screen.click(btn_right,0);
         mobile_screen.find(img_successfully_uploaded_data_message);
         mobile_screen.click(btn_right,0);
	}
	public void select_date(Date date)throws FindFailed, ParseException{
		
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
      	  mobile_screen.click(btn_enter,0);
	    }
		
		// Navigating to year
		if (y>=0){
			for (int i = 0 ; i<y;i++){
				mobile_screen.click(btn_right_arrow, 0);
			}
		} else 
		{ y = -y;
		    for (int i =0 ; i<y; i++){
		    	mobile_screen.click(btn_left_arrow, 0);
		    }
		}
		
		mobile_screen.click(btn_down_arrow,0);
		
		// Navigating to month 
		if (m>=0){
			for (int i = 0 ; i<m;i++){
				mobile_screen.click(btn_right_arrow, 0);
			}
		} else 
		{ m = -m;
		    for (int i =0 ; i<m; i++){
		    	mobile_screen.click(btn_left_arrow, 0);
		    }
		}
		mobile_screen.click(btn_down_arrow,0);
		
		// Selecting the date
		 for (int i= 1; i<d; i++){
			 mobile_screen.click(btn_right_arrow, 0);
		 }
		
		 // Navigating back 
		  for (int i=0;i<2;i++){
       	  mobile_screen.click(btn_right,0);
 	     }
	}
	
	public enum FormName{ANC_VISIT,CARE_HISTORY,CWC_VISIT,DELIVERY,DELIVERY_NOTIFICATION,EDIT_CLIENT,OUTPATIENT_VISIT,CLIENT_DEATH,PNC_BABY,PNC_MOTHER,PREG_TERMINATION,REG_ANC,REG_CWC,REG_CLIENT,TT_NON_PREGNANT}
	public enum TT_values {TT1,TT2,TT3,TT4,TT5};
	public enum Reg_mode_values {PRE_PRINTED_ID,AUTOGENRATED_ID};
	public enum Client_types{PREGNANT_MOTHER,CHILD_UNDER_5,OTHER};
	public enum DOB_types{ESTIMATED_DOB,EXACT_DOB};
	public enum District {KND, KNW};
	public enum Sub_Distrcit_KND{CENTRAL_EAST,CENTRAL_NAVRONGO,CENTRAL_NORTH,CENTRAL_NORTH_EAST,CENTRAL_SOUTH,CENTRAL_WEST};
	public enum Sub_DIstrict_KNW{NAVIO,KATIU_NAKONG,KAYORO,CHIANA,KANDIGA_KUGURU,SIRIGU,PAGA_CENTRAL,MIRIGU};
	public enum insured_type{YES,NO};
	public enum Add_History_values{IPT,TT,Vit_A,IPTi,BCG,OPV,PENTA,MEASLES,YF};
	public enum IPT_values {IPT1,IPT2,IPT3};
	
	public void select_form(FormName str) throws FindFailed{
	     FormName form;
	     form = str;
	     
	     switch (form){
	     case ANC_VISIT: input_radiobutton(1);break;
	     case CARE_HISTORY: input_radiobutton(2);break; 
         case CWC_VISIT: input_radiobutton(3);break;
         case DELIVERY: input_radiobutton(4);break;
         case DELIVERY_NOTIFICATION: input_radiobutton(5);break;
         case EDIT_CLIENT: input_radiobutton(6);break;
         case OUTPATIENT_VISIT: input_radiobutton(7);break;
         case CLIENT_DEATH: input_radiobutton(8);break;
         case PNC_BABY: input_radiobutton(9);break;
         case PNC_MOTHER: input_radiobutton(10);break;
         case PREG_TERMINATION: input_radiobutton(11);break;
         case REG_ANC: input_radiobutton(12);break;
         case REG_CWC: input_radiobutton(13);break;
         case REG_CLIENT: input_radiobutton(14);break; 
	     case TT_NON_PREGNANT: input_radiobutton(15);break;	   
         default: System.out.println("Form Does not exist");break; 
	                     
	     }  
    }
	
	public void select_TT_values(TT_values str) throws FindFailed{
	     TT_values tt ;
	     tt = str;
	     
	     switch (tt){
	     case TT1: mobile_screen.click(btn_enter,0);input_radiobutton(1);mobile_screen.click(btn_right,0);break;
	     case TT2: mobile_screen.click(btn_enter,0);input_radiobutton(2);mobile_screen.click(btn_right,0);break; 
         case TT3: mobile_screen.click(btn_enter,0);input_radiobutton(3);mobile_screen.click(btn_right,0);break;
         case TT4: mobile_screen.click(btn_enter,0);input_radiobutton(4);mobile_screen.click(btn_right,0);break;
         case TT5: mobile_screen.click(btn_enter,0);input_radiobutton(5);mobile_screen.click(btn_right,0);break;
         default: System.out.println("Given TT values does not exist");mobile_screen.click(btn_right,0);break; 
	                     
	     }
   }
	public void select_reg_mode(Reg_mode_values str) throws FindFailed{
	     Reg_mode_values reg ;
	     reg = str;
	     
	     switch (reg){
	     case PRE_PRINTED_ID: mobile_screen.click(btn_enter,0);input_radiobutton(1);mobile_screen.click(btn_right,0);break;
	     case AUTOGENRATED_ID: mobile_screen.click(btn_enter,0);input_radiobutton(2);mobile_screen.click(btn_right,0);break; 
         default: System.out.println("Given Reg_mode value does not exist");break; 
	     }
  }
	public void select_DOB_type(DOB_types str) throws FindFailed{
	      DOB_types dob_type = str ;
	    
	     switch (dob_type){
	     case ESTIMATED_DOB: mobile_screen.click(btn_enter,0);input_radiobutton(1);mobile_screen.click(btn_right,0);break;
	     case EXACT_DOB: mobile_screen.click(btn_enter,0);input_radiobutton(2);mobile_screen.click(btn_right,0);break; 
         default: System.out.println("Given DOB type value does not exist");break; 
	     }
	}	
	public void select_District (District str) throws FindFailed{
	     District dist = str ;
	    
	     switch (dist){
	     case KND: mobile_screen.click(btn_enter,0);input_radiobutton(1);mobile_screen.click(btn_right,0);break;
	     case KNW: mobile_screen.click(btn_enter,0);input_radiobutton(2);mobile_screen.click(btn_right,0);break; 
         default: System.out.println("Given District value does not exist");break; 
	     }
	}
	public void select_type_of_patient(Client_types str) throws FindFailed{
	     Client_types client_type ;
	     client_type = str;
	     
	     switch (client_type){
	     case PREGNANT_MOTHER: mobile_screen.click(btn_enter,0);input_radiobutton(1);mobile_screen.click(btn_right,0);break;
	     case CHILD_UNDER_5: mobile_screen.click(btn_enter,0);input_radiobutton(2);mobile_screen.click(btn_right,0);break;
	     case OTHER:mobile_screen.click(btn_enter,0);input_radiobutton(3);mobile_screen.click(btn_right,0);break;
         default: System.out.println("Given Reg_mode value does not exist");mobile_screen.click(btn_right,0);break; 
	     }
    }
	public void select_History_status(Add_History_values str) throws FindFailed{
	    Add_History_values hist = str;
	     
	     switch (hist){
	     case IPT: mobile_screen.click(btn_enter,0);input_radiobutton(1); mobile_screen.click(btn_right,0);break;
	     case TT: mobile_screen.click(btn_enter,0);input_radiobutton(2); mobile_screen.click(btn_right,0);break; 
         case Vit_A: mobile_screen.click(btn_enter,0);input_radiobutton(3); mobile_screen.click(btn_right,0);break;
         case IPTi: mobile_screen.click(btn_enter,0);input_radiobutton(4); mobile_screen.click(btn_right,0);break;
         case BCG: mobile_screen.click(btn_enter,0);input_radiobutton(5); mobile_screen.click(btn_right,0);break;
         case OPV: mobile_screen.click(btn_enter,0);input_radiobutton(5); mobile_screen.click(btn_right,0);break;
         case PENTA: mobile_screen.click(btn_enter,0);input_radiobutton(5); mobile_screen.click(btn_right,0);break;
         case MEASLES: mobile_screen.click(btn_enter,0);input_radiobutton(5); mobile_screen.click(btn_right,0);break;
         case YF: mobile_screen.click(btn_enter,0);input_radiobutton(5); mobile_screen.click(btn_right,0);break;
         default: System.out.println("Given Add_History values does not exist");break; 
	   }
    }
	
	public void select_IPT_value(IPT_values str) throws FindFailed{
	    IPT_values ipt = str;
	     
	     switch (ipt){
	     case IPT1: mobile_screen.click(btn_enter,0);input_radiobutton(1);mobile_screen.click(btn_right,0);break;
	     case IPT2: mobile_screen.click(btn_enter,0);input_radiobutton(2);mobile_screen.click(btn_right,0);break; 
         case IPT3: mobile_screen.click(btn_enter,0);input_radiobutton(3);mobile_screen.click(btn_right,0);break;
         default: System.out.println("Given IPT values does not exist");mobile_screen.click(btn_right,0);break; 
	   }
    }
	public void is_patient_insured (insured_type str) throws FindFailed{
	     insured_type ins = str ;
	    
	     switch (ins){
	     case YES: mobile_screen.click(btn_enter,0);input_radiobutton(1);mobile_screen.click(btn_right,0);break;
	     case NO: mobile_screen.click(btn_enter,0);input_radiobutton(2);mobile_screen.click(btn_right,0);break; 
         default: System.out.println("Given Insured type does not exist");mobile_screen.click(btn_right,0);break; 
	     }
	}
	public void select_subdistrict_KND (Sub_Distrcit_KND str) throws FindFailed{
	     Sub_Distrcit_KND sub_dir = str ;
	    
	     switch (sub_dir){
	     case CENTRAL_EAST: mobile_screen.click(btn_enter,0);input_radiobutton(1);mobile_screen.click(btn_right,0);break;
	     case CENTRAL_NORTH: mobile_screen.click(btn_enter,0);input_radiobutton(2);mobile_screen.click(btn_right,0);break; 
	     case CENTRAL_NAVRONGO: mobile_screen.click(btn_enter,0);input_radiobutton(2);mobile_screen.click(btn_right,0);break; 
	     case CENTRAL_NORTH_EAST: mobile_screen.click(btn_enter,0);input_radiobutton(2);mobile_screen.click(btn_right,0);break; 
	     case CENTRAL_SOUTH: mobile_screen.click(btn_enter,0);input_radiobutton(2);mobile_screen.click(btn_right,0);break; 
	     case CENTRAL_WEST: mobile_screen.click(btn_enter,0);input_radiobutton(2);mobile_screen.click(btn_right,0);break; 
         default: System.out.println("Given subdistrict does not exist");break; 
	     }
	}
	
	
	public void input_radiobutton(int n)throws FindFailed{
		 for (int i= 1;i<n;i++)
	     {
	    	 mobile_screen.click(btn_down_arrow, 0);
	     }
         mobile_screen.click(btn_enter,0);
	 }
	
	
	public Date string_to_date_convertor(String date)throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date d = sdf.parse(date);
		return d;
	}
}