package flows;

import bsh.ParseException;
import forms.RegisterClientForm;
import forms.SikuliBase;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: rupeshd
 * Date: 2/16/11
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegisterPatientViaMfroms {

    @Test
    public void registerChildViaMform()throws ParseException, java.text.ParseException{
        RegisterClientForm obj = new RegisterClientForm();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
        String date = sdf.format(Calendar.getInstance().getTime());
        String staffId = "465";
        String facilityId = "11117";
        String regDate = "1/4/2010";
        String regPhoneNo ="0123456789";
        String firstName ="Child";
        String middleName ="middle";
        String lastName ="Last"+date;
        String preferredName = "preferred";
        String DOB ="1/1/2009";
        String motherMotechId = "1234568";
        obj.registerNonInsuredChild(staffId, facilityId, regDate, regPhoneNo, SikuliBase.clientTypes.CHILD_UNDER_5, firstName, middleName, lastName, preferredName, DOB, SikuliBase.DOBTypes.EXACT_DOB, SikuliBase.clientGender.MALE,motherMotechId );
        OpenMRSLoginPage loginPage = new OpenMRSLoginPage();
        loginPage.getOpenMRSDashBoard();
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.VIEW_DATA);
        ViewDataPage viewPage = new ViewDataPage();
        Assert.assertTrue(viewPage.verifyPatientLastName(firstName), "Patient is registered successfully");

    }

    @Test
	public void registerMotherViaMform()throws ParseException, java.text.ParseException{
	RegisterClientForm obj = new RegisterClientForm();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
    String date = sdf.format(Calendar.getInstance().getTime());
	String staffId = "465";
	String facilityId = "11117";
	String regDate = "1/4/2010";
	String regPhoneNo ="0123456789";
	String firstName ="Mom";
	String middleName ="middle";
	String lastName ="Last"+date;
	String preferredName = "preferred";
	String DOB ="1/1/1994";
    obj.registerNonInsuredPregnantMother(staffId, facilityId, regDate, regPhoneNo, SikuliBase.clientTypes.PREGNANT_MOTHER, firstName, middleName, lastName, preferredName, DOB, SikuliBase.DOBTypes.EXACT_DOB);

	}

    @Test
	public void registerStaffAndRegisterMotherViaMform() throws java.text.ParseException {
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
			String staffId = regStaff.createNewStaff();
            RegisterClientForm obj = new RegisterClientForm();
            obj.registerNonInsuredPregnantMother(staffId, facilityId, regDate, regPhoneNo, SikuliBase.clientTypes.PREGNANT_MOTHER, firstName, middleName, lastName, preferredName, DOB, SikuliBase.DOBTypes.EXACT_DOB);
            moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.VIEW_DATA);
            ViewDataPage viewPage = new ViewDataPage();
            Assert.assertTrue(viewPage.verifyPatientLastName(lastName),"Patient record not found");
        }
	}
}
