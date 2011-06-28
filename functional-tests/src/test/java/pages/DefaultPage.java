package pages;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import  org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class DefaultPage {

    //protected static HtmlUnitDriver driver;
    protected  static WebDriver driver;

    protected DefaultPage() {
    }

    public static WebDriver getInstance() {
        if (driver == null) {
            driver = new FirefoxDriver();
           //driver = new HtmlUnitDriver();
           //driver.setJavascriptEnabled(true);
           //driver = new ChromeDriver();
        }
        return driver;
    }
}
