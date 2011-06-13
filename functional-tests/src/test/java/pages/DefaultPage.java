package pages;

import org.openqa.selenium.WebDriver;
import  org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class DefaultPage {

    protected static WebDriver driver;

    protected DefaultPage() {
    }

    public static WebDriver getInstance() {
        if (driver == null) {
           driver = new FirefoxDriver();
          //  driver = new ChromeDriver();
        }
        return driver;
    }
}
