package flows;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;

/**
 * Created by IntelliJ IDEA.
 * User: rupeshd
 * Date: 2/15/11
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManageFacilityPageTest {

    @Test
    public void AddCommunityTest(){
        OpenMRSLoginPage loginPage = new OpenMRSLoginPage();
        loginPage.getOpenMRSDashBoard();
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
        AddFacilityPage addFacilityPage = new AddFacilityPage();
        String facilityName = addFacilityPage.AddNewFacilityWithFourPhoneNumber();
        ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
        Assert.assertNotNull(facilityName,"Facility Name is null.Facility is not created");
        Assert.assertTrue(manageFacilityPage.FindFacilityByName(facilityName), "Facility name not found");
   }
}
