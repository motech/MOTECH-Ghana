package GenericLayer;

import pages.HomePageLinksEnum;
import pages.MoTeCHDashBoardPage;
import pages.OpenMRSLoginPage;
import pages.SearchPage;

public class WebClass implements searchRepository {

    public SearchPage searchPage;

    public boolean doesPatientExist(String patientID) {
        OpenMRSLoginPage openMRSLoginPage = new OpenMRSLoginPage();
        openMRSLoginPage.loginIntoOpenMRS("admin", "Openmr5tw");
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
        searchPage = new SearchPage();
        boolean searchResult= searchPage.searchClientByID(patientID);
        openMRSLoginPage.close();
        return searchResult;

 }
}
