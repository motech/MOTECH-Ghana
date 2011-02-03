package Tests;


import WebdriverFiles.*;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import org.testng.*;
import sikuliFiles.RegisterClientForm;
import sikuliFiles.SikuliBaseClass;
import sikuliFiles.TTNonPregnantForm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class RegisterStaffAndRegisterMotherThroughMform {

	@Test
	public void registerStaffAndRegisterMotherViaMform() throws ParseException {
		{   SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
            String date = sdf.format(Calendar.getInstance().getTime());
            String facilityId = "11117";
            String regDate = "1/4/2010";
            String regPhoneNo ="0123456789";
            String firstName ="Mom";
            String middleName ="middle";
            String lastName ="Last"+date;
            String preferredName = "preferred";
            String DOB ="1/1/1998";
			OpenMRSLoginPage loginPage = new OpenMRSLoginPage();
            loginPage.getOpenMRSDashBoard();
            MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
            moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_STAFF);
            RegisterStaffPage regStaff = new RegisterStaffPage() ;
			WebElement strStaffId = regStaff.createNewStaff();
			Assert.assertTrue(strStaffId.getText().contains("Added user: Name ="));
			String staffId = regStaff.extractStaffId(strStaffId);
            RegisterClientForm obj = new RegisterClientForm();
            obj.registerNonInsuredPregnantMother(staffId, facilityId, regDate, regPhoneNo, SikuliBaseClass.clientTypes.PREGNANT_MOTHER, firstName, middleName, lastName, preferredName, DOB, SikuliBaseClass.DOBTypes.EXACT_DOB);
            moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.VIEW_DATA);
            ViewDataPage viewPage = new ViewDataPage();
            Assert.assertTrue(viewPage.verifyPatientLastName(lastName),"Patient record not found");
        }
	}
}

