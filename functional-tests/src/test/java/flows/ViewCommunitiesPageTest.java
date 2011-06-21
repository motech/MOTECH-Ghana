package flows;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;

/**
 * Created by IntelliJ IDEA.
 * User: rupeshd
 * Date: 2/15/11
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewCommunitiesPageTest {
    @Test
    public void AddCommunityTest(){
        OpenMRSLoginPage loginPage = new OpenMRSLoginPage();
        loginPage.getOpenMRSDashBoard();
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.MANAGE_FACILITIES);
        ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
        AddFacilityPage addFacilityPage = new AddFacilityPage();
        String facilityName = addFacilityPage.AddNewFacilityWithFourPhoneNumber();
        Assert.assertNotNull(facilityName, "Facility Name is null.Facility is not created");
        Assert.assertTrue(manageFacilityPage.FindFacilityByName(facilityName), "Facility name not found");
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.VIEW_COMMUNITIES);
        ViewCommunitiesPage viewCommunitiesPage =  new ViewCommunitiesPage();
        String communityName = viewCommunitiesPage.AddCommunity(facilityName);
        Assert.assertTrue(viewCommunitiesPage.DoesCommunityNameExist(communityName,facilityName),"Community name not found");

    }
}
