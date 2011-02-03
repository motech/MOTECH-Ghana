package WebdriverFiles;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class OpenMRSLoginPage {

    private WebDriver driver;

    public OpenMRSLoginPage() {
        driver = WebDriverBaseClass.getInstance();
    }

    public void getOpenMRSDashBoard() {
        driver.get("http://localhost:8080/openmrs/module/motechmodule/index.htm");
        WebElement userName = driver.findElement(By.id("username"));
        WebElement password = driver.findElement(By.id("password"));
        WebElement login = driver.findElement(By.xpath("//input[@value = 'Log In']"));
        userName.sendKeys("admin");
        password.sendKeys("Openmr5tw");
        login.click();
    }
}
