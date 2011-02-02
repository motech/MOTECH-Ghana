package Tests;


import WebdriverFiles.*;
import org.testng.annotations.Test;

public class TemprorayTest {

    @Test
    public void temp(){
        OpenMRSLoginPage loginPage = new OpenMRSLoginPage();
        loginPage.getOpenMRSDashBoard();
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.VIEW_DATA);
        ViewDataPage viewDataPage = new ViewDataPage();
        viewDataPage.getMostRecentlyRegisteredPatient();
    }
}
