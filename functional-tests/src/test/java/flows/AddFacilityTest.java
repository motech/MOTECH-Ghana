package flows;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.*;


public class AddFacilityTest {

    private OpenMRSLoginPage loginPage;

    @BeforeTest
    public void SetUp(){
        loginPage = new OpenMRSLoginPage();
        loginPage.getOpenMRSDashBoard();
    }
    @AfterTest
    public void TearDown(){
      // loginPage.close();

    }

    @Test
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
