package pages;

import org.openqa.selenium.WebDriver;

public class MoTeCHDashBoardPage {

    private WebDriver driver;

    public MoTeCHDashBoardPage() {
        driver = DefaultPage.getInstance();
    }

    public void navigateToPage(HomePageLinksEnum page) {
        page.navigate(driver);
    }

}
