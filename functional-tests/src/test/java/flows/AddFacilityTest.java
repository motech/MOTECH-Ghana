package flows;

import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;


public class AddFacilityTest {

    private OpenMRSLoginPage loginPage;

    @BeforeTest
    //@BeforeGroups(groups = {"smoke"})
    public void SetUp(){
        loginPage = new OpenMRSLoginPage();
        loginPage.getOpenMRSDashBoard();
    }

    @AfterTest
    //@AfterGroups(groups = {"smoke"})
    public void TearDown(){
      loginPage.close();

    }

    @Test
    public void AddFacilityWithOnePhoneNumber(){
          MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
          moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
          AddFacilityPage addFacilityPage = new AddFacilityPage();
          String facilityName = addFacilityPage.AddFacility("0111111111");
          ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
          System.out.println(facilityName);
          Assert.assertTrue(manageFacilityPage.FindFacilityByName(facilityName),"Facility Not Found");
          Assert.assertTrue(manageFacilityPage.FindFacilityByNameAndPhoneNumber(facilityName,"0111111111"),"Facility Details does not match");
    }

    @Test//(groups = {"smoke"})
    public void AddFacilityWithTwoPhoneNumber(){
       MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
       moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
       AddFacilityPage addFacilityPage = new AddFacilityPage();
       String facilityName = addFacilityPage.AddFacility("0111111111","0222222222");
       ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
       System.out.println(facilityName);
       Assert.assertTrue(manageFacilityPage.FindFacilityByName(facilityName),"Facility Not Found");
       Assert.assertTrue(manageFacilityPage.FindFacilityByNameAndPhoneNumber(facilityName,"0111111111","0222222222"),"Facility Details does not match");
    }

    @Test//(groups = {"smoke"})
    public void AddFacilityWithThreePhoneNumber(){
       MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
       moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
       AddFacilityPage addFacilityPage = new AddFacilityPage();
       String facilityName = addFacilityPage.AddFacility("0111111111","0222222222","0333333333");
       ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
       System.out.println(facilityName);
       Assert.assertTrue(manageFacilityPage.FindFacilityByName(facilityName),"Facility Not Found");
       Assert.assertTrue(manageFacilityPage.FindFacilityByNameAndPhoneNumber(facilityName,"0111111111","0222222222","0333333333"),"Facility Details does not match");
    }


    @Test//(groups = {"smoke"})
    public void AddFacilityWithFourPhoneNumber(){
       MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
       moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
       AddFacilityPage addFacilityPage = new AddFacilityPage();
       String facilityName = addFacilityPage.AddFacility("0111111111","0222222222","0333333333","0444444444");
       ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
       System.out.println(facilityName);
       Assert.assertTrue(manageFacilityPage.FindFacilityByName(facilityName),"Facility Not Found");
       Assert.assertTrue(manageFacilityPage.FindFacilityByNameAndPhoneNumber(facilityName,"0111111111","0222222222","0333333333","0444444444"),"Facility Details does not match");
    }


}
