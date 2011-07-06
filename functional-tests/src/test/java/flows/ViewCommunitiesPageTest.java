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
    public void addCommunityTest(){
        OpenMRSLoginPage loginPage = new OpenMRSLoginPage();
        loginPage.loginIntoOpenMRS("admin", "OpenMr5tw");
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.MANAGE_FACILITIES);
        ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
        AddFacilityPage addFacilityPage = new AddFacilityPage();
        String facilityName = addFacilityPage.addFacility("0111111111");
        Assert.assertNotNull(facilityName, "Facility Name is null.Facility is not created");
        Assert.assertTrue(manageFacilityPage.findFacilityByName(facilityName), "Facility name not found");
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.VIEW_COMMUNITIES);
        ViewCommunitiesPage viewCommunitiesPage =  new ViewCommunitiesPage();
        String communityName = viewCommunitiesPage.addCommunity(facilityName);
        Assert.assertTrue(viewCommunitiesPage.doesCommunityNameExist(communityName, facilityName),"Community name not found");

    }
}
