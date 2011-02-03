package WebdriverFiles;
import com.gargoylesoftware.htmlunit.html.HtmlArea;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;


public class WebDriverBaseClass {
	
	private static WebDriver driver;
    
	private WebDriverBaseClass(){
	}

    public static WebDriver getInstance(){
        if(driver == null){
           driver = new FirefoxDriver(); 
        }
        return driver;
    }
}
