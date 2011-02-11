package flows;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;

/**
 * Created by IntelliJ IDEA.
 * User: rupeshd
 * Date: 2/9/11
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegisterMotherViaUIAndSearch {
  @Test
  public void registerMotherViaUIAndSearch() {
      OpenMRSLoginPage loginPage = new OpenMRSLoginPage();
      loginPage.getOpenMRSDashBoard();
      MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
      moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
      RegisterPatientPage regPatientPage = new RegisterPatientPage();
      String lastName = regPatientPage.RegisterMotherClient();
      ViewDataPage viewDataPage = new ViewDataPage();
      String patientID = viewDataPage.returnPatientId(lastName);
      Assert.assertNotNull(patientID,"Patient ID is null");
      moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.SEARCH);
      SearchPage searchPage = new SearchPage();
      Assert.assertTrue(searchPage.searchClientByID(patientID,lastName),"Patient Search failed");
    }
}
