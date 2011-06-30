package flows;

import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import pages.*;

public class EditFacilityPageTest {
    private OpenMRSLoginPage loginPage;
/*
     @BeforeGroups(groups = {"smoke"})
   // @BeforeMethod
    public void setUp() {
        loginPage = new OpenMRSLoginPage();
        loginPage.getOpenMRSDashBoard();
    }

   @AfterGroups(groups = {"smoke"})
    public void tearDown() {
       loginPage.close();
    }*/

   @Test (groups = {"smoke"})
   public void EditFacilityWithOnePhoneNumber(){
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
        AddFacilityPage addFacilityPage = new AddFacilityPage();
        String facilityName = addFacilityPage.AddFacility("0111111111");
        ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
        Assert.assertTrue(manageFacilityPage.FindFacilityByName(facilityName), "Facility Not Found");
        Assert.assertTrue(manageFacilityPage.FindFacilityByNameAndPhoneNumber(facilityName,"0111111111"),"Facility Details does not match");
        manageFacilityPage.NavigateToEditFacilityPage(facilityName);
        EditFacilityPage editFacilityPage = new EditFacilityPage();
        editFacilityPage.EditPhoneNumber("0222222222","0333333333","0444444444","0555555555");
        Assert.assertTrue(manageFacilityPage.FindFacilityByNameAndPhoneNumber(facilityName,"0222222222","0333333333","0444444444","0555555555"),"Facility Details does not match");
   }
}
