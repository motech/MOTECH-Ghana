package flows;

import org.testng.Assert;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;
import pages.*;

import java.util.HashMap;


/**
 * Created by IntelliJ IDEA.
 * User: rupeshd
 * Date: 2/9/11
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
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
        String lastName = regPatientPage.RegisterMotherClient();
        ViewDataPage viewDataPage = new ViewDataPage();
        String patientID = viewDataPage.returnPatientId(lastName);
        Assert.assertNotNull(patientID,"Patient ID is null");
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
        SearchPage searchPage = new SearchPage();
        Assert.assertTrue(searchPage.searchClientByID(patientID,lastName),"Patient Search failed");
    }

    @Test(groups = {"smoke"})
    public void registerOtherPatientViaUIAndSearch(){
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
        RegisterClientPage regPatientPage = new RegisterClientPage();
        String lastName = regPatientPage.RegisterOtherClient();
        ViewDataPage viewDataPage = new ViewDataPage();
        String patientID = viewDataPage.returnPatientId(lastName);
        Assert.assertNotNull(patientID,"Other Patient ID is null");
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
        SearchPage searchPage = new SearchPage();
        Assert.assertTrue(searchPage.searchClientByID(patientID, lastName), "Patient Search failed");
    }

    @Test
    public void registerChildPatientViaUIAndSearch(){
        HashMap hm = new HashMap();
        MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
        RegisterClientPage regPatientPage = new RegisterClientPage();
        regPatientPage.RegisterChildClient(hm);
        ViewDataPage viewDataPage = new ViewDataPage();
        String patientID = viewDataPage.returnPatientId(hm);
        Assert.assertNotNull(patientID,"Child Patient ID is null");
        moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
        SearchPage searchPage = new SearchPage();
        String lastName = (String) hm.get("lastName");
        Assert.assertTrue(searchPage.searchClientByID(patientID, lastName), "Patient Search failed");
    };
}
