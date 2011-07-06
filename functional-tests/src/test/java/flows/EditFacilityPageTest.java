package flows;

import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;

public class EditFacilityPageTest {

  /*  private OpenMRSLoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        loginPage = new OpenMRSLoginPage();
        loginPage.loginIntoOpenMRS("admin", "Openmr5tw");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
       loginPage.close();
    }*/
   @Test (groups = {"smoke"})
   public void editFacilityWithOnePhoneNumber(){
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
        AddFacilityPage addFacilityPage = new AddFacilityPage();
        String facilityName = addFacilityPage.addFacility("0111111111");
        ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
        Assert.assertTrue(manageFacilityPage.findFacilityByName(facilityName), "Facility Not Found");
        Assert.assertTrue(manageFacilityPage.findFacilityByNameAndPhoneNumber(facilityName, "0111111111"),"Facility Details does not match");
        manageFacilityPage.navigateToEditFacilityPage(facilityName);
        EditFacilityPage editFacilityPage = new EditFacilityPage();
        editFacilityPage.editPhoneNumber("0222222222", "0333333333", "0444444444", "0555555555");
        Assert.assertTrue(manageFacilityPage.findFacilityByNameAndPhoneNumber(facilityName, "0222222222", "0333333333", "0444444444", "0555555555"),"Facility Details does not match");
   }
}
