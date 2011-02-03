package Tests;

import WebdriverFiles.HomePageLinksEnum;
import WebdriverFiles.MoTeCHDashBoardPage;
import WebdriverFiles.OpenMRSLoginPage;
import WebdriverFiles.ViewDataPage;
import bsh.ParseException;
import org.testng.annotations.Test;
import org.testng.Assert;
import sikuliFiles.RegisterClientForm;
import sikuliFiles.SikuliBaseClass;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegisterNewChildTest {
    @Test
    public void test()throws ParseException, java.text.ParseException{
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
        obj.registerNonInsuredChild(staffId, facilityId, regDate, regPhoneNo, SikuliBaseClass.clientTypes.CHILD_UNDER_5, firstName, middleName, lastName, preferredName, DOB, SikuliBaseClass.DOBTypes.EXACT_DOB, SikuliBaseClass.clientGender.MALE,motherMotechId );
        OpenMRSLoginPage loginPage = new OpenMRSLoginPage();
        loginPage.getOpenMRSDashBoard();
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.VIEW_DATA);
        ViewDataPage viewPage = new ViewDataPage();
        Assert.assertTrue(viewPage.verifyPatientLastName(firstName),"Patient is registered successfully");

    }
}

