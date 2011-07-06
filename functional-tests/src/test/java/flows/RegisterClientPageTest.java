package flows;

import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;


public class RegisterClientPageTest {
    private OpenMRSLoginPage loginPage;

    //@BeforeGroups(groups = {"smoke"})
//    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        loginPage = new OpenMRSLoginPage();
        loginPage.loginIntoOpenMRS("admin", "Openmr5tw");
    }

   //@AfterGroups(groups = {"smoke"})
  //  @AfterMethod(alwaysRun = true)
    public void tearDown() {
       loginPage.close();
    }

    @Test(groups = {"smoke"})
    public void registerMotherViaUIAndSearch() {
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
        RegisterClientPage regPatientPage = new RegisterClientPage();
        String firstName = regPatientPage.registerMotherClient();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
        SearchPage searchPage = new SearchPage();
        Assert.assertTrue(searchPage.searchClientByFirstName(firstName),"Patient Search failed");
    }

    @Test(groups = {"smoke"})
    public void registerOtherPatientViaUIAndSearch(){
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
        RegisterClientPage regPatientPage = new RegisterClientPage();
        String firstName = regPatientPage.registerOtherClient();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
        SearchPage searchPage = new SearchPage();
        Assert.assertTrue(searchPage.searchClientByFirstName(firstName),"Patient Search failed");
    }

    @Test(groups = {"smoke"})
    public void registerChildPatientViaUIAndSearch(){
       
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
        RegisterClientPage regPatientPage = new RegisterClientPage();
        String motherName = regPatientPage.registerMotherClient();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
        SearchPage searchPage = new SearchPage();
        String motherID = searchPage.returnClientIdBySearchingUsingFirstName(motherName);
        System.out.println(motherID);
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
        String firstName = regPatientPage.registerChildClient(motherID);
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
        Assert.assertTrue(searchPage.searchClientByFirstName(firstName), "Patient Search failed"+ firstName);
    };
}
