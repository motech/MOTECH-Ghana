package flows;


import forms.SikuliBase;
import forms.TTNonPregnantForm;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;

import java.text.ParseException;


public class TTNonPregnantFormTest {

    @Test
    public void registerNewStaffAndFillTTNonPregnantForm() throws ParseException {
        OpenMRSLoginPage loginPage = new OpenMRSLoginPage();
        loginPage.getOpenMRSDashBoard();
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_STAFF);
        RegisterStaffPage regStaff = new RegisterStaffPage() ;
        WebElement strStaffId = regStaff.createNewStaff();
        Assert.assertTrue(strStaffId.getText().contains("Added user: Name ="));
        String staffId = regStaff.extractStaffId(strStaffId);
        TTNonPregnantForm obj1 = new TTNonPregnantForm();
        String facilityId = "11117";
        String date = "10/12/2010";
        String motechId = "1234568";
        obj1.Fill_TT_Non_Pregnant_Form(staffId, facilityId, date, motechId, SikuliBase.TTValues.TT1);

    }
}

