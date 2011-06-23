package flows;

import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;

import java.util.HashMap;


public class RegisterClientPageTest {
    private OpenMRSLoginPage loginPage;

    @BeforeGroups(groups = {"smoke"})
    public void setUp() {
        loginPage = new OpenMRSLoginPage();
        loginPage.getOpenMRSDashBoard();
    }

    @AfterGroups(groups = {"smoke"})
    public void tearDown() {
       loginPage.close();
    }

    @Test(groups = {"smoke"})
    public void registerMotherViaUIAndSearch() {
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
        RegisterClientPage regPatientPage = new RegisterClientPage();
        String firstName = regPatientPage.RegisterMotherClient();
        //ViewDataPage viewDataPage = new ViewDataPage();
        //String patientID = viewDataPage.returnPatientId(lastName);
        //Assert.assertNotNull(patientID,"Patient ID is null");
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
        SearchPage searchPage = new SearchPage();
        Assert.assertTrue(searchPage.searchClientByFirstName(firstName),"Patient Search failed");
    }

    @Test(groups = {"smoke"})
    public void registerOtherPatientViaUIAndSearch(){
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
        RegisterClientPage regPatientPage = new RegisterClientPage();
        String firstName = regPatientPage.RegisterOtherClient();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
        SearchPage searchPage = new SearchPage();
        Assert.assertTrue(searchPage.searchClientByFirstName(firstName),"Patient Search failed");
    }

    @Test(groups = {"smoke"})
    public void registerChildPatientViaUIAndSearch(){
       
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
        RegisterClientPage regPatientPage = new RegisterClientPage();
        String motherName = regPatientPage.RegisterMotherClient();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
        SearchPage searchPage = new SearchPage();
        String motherID = searchPage.returnClientIdBySearchingUsingFirstName(motherName);
        System.out.println(motherID);
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
        String firstName = regPatientPage.RegisterChildClient(motherID);
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
        Assert.assertTrue(searchPage.searchClientByFirstName(firstName), "Patient Search failed"+ firstName);
    };
}
