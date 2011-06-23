package flows;

import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;


public class AddFacilityTest {

    private OpenMRSLoginPage loginPage;

    //@BeforeTest
    @BeforeGroups(groups = {"smoke"})
    public void SetUp(){
        loginPage = new OpenMRSLoginPage();
        loginPage.getOpenMRSDashBoard();
    }

    //@AfterTest
    @AfterGroups(groups = {"smoke"})
    public void TearDown(){
      loginPage.close();

    }

    @Test(groups = {"smoke"})
    public void AddFacilityWithMultiplePhoneNumber(){
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
        AddFacilityPage addFacilityPage = new AddFacilityPage();
        String facilityName = addFacilityPage.AddNewFacilityWithFourPhoneNumber();
        ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
        System.out.println(facilityName);
        Assert.assertTrue(manageFacilityPage.FindFacilityByName(facilityName),"Facility Not Found");

    }
}
