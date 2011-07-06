package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import  org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import util.TestConfiguration;


public class DefaultPage {

    //protected static HtmlUnitDriver driver;
    protected  static WebDriver driver;
    private static TestConfiguration testConfiguration;

    public static WebDriver getInstance() {
       testConfiguration = new TestConfiguration();
        String driverValue = testConfiguration.getDriver();
        if (driverValue.equalsIgnoreCase("FF")){
            driver = new FirefoxDriver();
        }else if(driverValue.equalsIgnoreCase("IE")){
            driver =new InternetExplorerDriver();
        }else if (driverValue.equalsIgnoreCase("Chrome")){
            driver =new ChromeDriver();
        }else if (driverValue.equalsIgnoreCase("HTMLUnit")){
            driver= new HtmlUnitDriver();
        }
        return driver;
    }
}
