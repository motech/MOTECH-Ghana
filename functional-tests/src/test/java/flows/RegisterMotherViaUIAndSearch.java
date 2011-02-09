package flows;

import org.testng.annotations.Test;
import pages.HomePageLinksEnum;
import pages.MoTeCHDashBoardPage;
import pages.OpenMRSLoginPage;
import pages.RegisterPatientPage;

/**
 * Created by IntelliJ IDEA.
 * User: rupeshd
 * Date: 2/9/11
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegisterMotherViaUIAndSearch {
  @Test
  public void registerMotherViaUIAndSearch(){
      OpenMRSLoginPage loginPage = new OpenMRSLoginPage();
      loginPage.getOpenMRSDashBoard();
      MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
      moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
      RegisterPatientPage regPatientPage = new RegisterPatientPage();
      regPatientPage.RegisterMotherClient("Mom","middle","preferred","last","01/01/1980");
    }
}
