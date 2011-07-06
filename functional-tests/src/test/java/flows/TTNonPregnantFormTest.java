package flows;


import forms.SikuliBase;
import forms.TTNonPregnantForm;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;

import java.text.ParseException;


public class TTNonPregnantFormTest {

    @Test
    public void registerNewStaffAndFillTTNonPregnantForm() throws ParseException {

       //1. Creating an Agent
        OpenMRSLoginPage loginPage = new OpenMRSLoginPage();
        loginPage.loginIntoOpenMRS("admin", "OpenMr5tw");
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_STAFF);
        RegisterStaffPage regStaff = new RegisterStaffPage() ;
        String staffId = regStaff.createNewStaff();

        //2. Creating Other Patient
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
        RegisterClientPage regPatientPage = new RegisterClientPage();
        String lastName = regPatientPage.registerOtherClient();
        ViewDataPage viewDataPage = new ViewDataPage();
        String patientID = viewDataPage.returnPatientId(lastName);
        Assert.assertNotNull(patientID, "Other Patient ID is null");
        loginPage.close();

        //3. Filling TT Form the registered changes
        TTNonPregnantForm ttNonPregnantForm = new TTNonPregnantForm();
        String facilityId = "11117";
        String date = "10/12/2010";
        String motechId = "1234568";
        ttNonPregnantForm.fillTTNonPregnantForm(staffId, facilityId, date, motechId, SikuliBase.TTValues.TT1);

    }
}

