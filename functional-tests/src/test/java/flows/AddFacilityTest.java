package flows;

import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;


public class AddFacilityTest {

  //private OpenMRSLoginPage openMRSLoginPage;


   /* @BeforeMethod(alwaysRun = true)
    public void setUp() {
        openMRSLoginPage = new OpenMRSLoginPage();
        openMRSLoginPage.loginIntoOpenMRS("admin", "Openmr5tw");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
       openMRSLoginPage.close();
    }
*/
    @Test(groups = {"smoke"})
    public void addFacilityWithOnePhoneNumber(){
          MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
          moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
          AddFacilityPage addFacilityPage = new AddFacilityPage();
          String facilityName = addFacilityPage.addFacility("0111111111");
          ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
          Assert.assertTrue(manageFacilityPage.findFacilityByName(facilityName),"Facility Not Found");
          Assert.assertTrue(manageFacilityPage.findFacilityByNameAndPhoneNumber(facilityName, "0111111111"),"Facility Details does not match");
    }

    @Test//(groups = {"smoke"})
    public void addFacilityWithTwoPhoneNumber(){
       MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
       moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
       AddFacilityPage addFacilityPage = new AddFacilityPage();
       String facilityName = addFacilityPage.addFacility("0111111111", "0222222222");
       ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
       Assert.assertTrue(manageFacilityPage.findFacilityByName(facilityName),"Facility Not Found");
       Assert.assertTrue(manageFacilityPage.findFacilityByNameAndPhoneNumber(facilityName, "0111111111", "0222222222"),"Facility Details does not match");
    }

    @Test//(groups = {"smoke"})
    public void addFacilityWithThreePhoneNumber(){
       MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
       moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
       AddFacilityPage addFacilityPage = new AddFacilityPage();
       String facilityName = addFacilityPage.addFacility("0111111111", "0222222222", "0333333333");
       ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
       Assert.assertTrue(manageFacilityPage.findFacilityByName(facilityName),"Facility Not Found");
       Assert.assertTrue(manageFacilityPage.findFacilityByNameAndPhoneNumber(facilityName, "0111111111", "0222222222", "0333333333"),"Facility Details does not match");
    }


    @Test(groups = {"smoke"})
    public void addFacilityWithFourPhoneNumber(){
       MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
       moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
       AddFacilityPage addFacilityPage = new AddFacilityPage();
       String facilityName = addFacilityPage.addFacility("0111111111", "0222222222", "0333333333", "0444444444");
       ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
       System.out.println(facilityName);
       Assert.assertTrue(manageFacilityPage.findFacilityByName(facilityName),"Facility Not Found");
       Assert.assertTrue(manageFacilityPage.findFacilityByNameAndPhoneNumber(facilityName, "0111111111", "0222222222", "0333333333", "0444444444"),"Facility Details does not match");
    }

}
