package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class DefaultPage {

    protected static WebDriver driver;

    protected DefaultPage() {
    }

    public static WebDriver getInstance() {
        if (driver == null) {
            driver = new FirefoxDriver();
        }
        return driver;
    }
}
