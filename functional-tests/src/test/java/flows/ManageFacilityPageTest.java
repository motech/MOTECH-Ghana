package flows;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import pages.*;

public class ManageFacilityPageTest  {

    private OpenMRSLoginPage loginPage;

    @BeforeTest
    public void SetUp(){
        loginPage = new OpenMRSLoginPage();
        loginPage.getOpenMRSDashBoard();
    }

    @AfterTest
    public void TearDown(){
        loginPage.close();
    }

    @Test
    public void EditFacilityTest(){
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.ADD_FACILITY);
        AddFacilityPage addFacilityPage = new AddFacilityPage();
        String facilityName = addFacilityPage.AddNewFacilityWithOnePhoneNumber();
        ManageFacilityPage manageFacilityPage = new ManageFacilityPage();
        Assert.assertTrue(manageFacilityPage.FindFacilityByName(facilityName), "Facility:"+facilityName+" name not found");
        manageFacilityPage.NavigateToEditFacilityPage(facilityName);
        EditFacilityPage editFacilityPage = new EditFacilityPage();
        editFacilityPage.EditPhoneNumber("0234567891","0345678912","0456789123","0567891234");


   }
}
