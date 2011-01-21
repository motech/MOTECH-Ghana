package WebdriverFiles;
import com.gargoylesoftware.htmlunit.html.HtmlArea;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;


public class WebDriverBaseClass {
	
	public static WebDriver driver;
	
	public WebDriverBaseClass(){
	driver = new FirefoxDriver();
	};
}
