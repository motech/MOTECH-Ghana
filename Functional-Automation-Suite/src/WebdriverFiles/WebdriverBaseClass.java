package WebdriverFiles;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;


public class WebdriverBaseClass {
	
	public static WebDriver driver;
	
	public WebdriverBaseClass (){
	driver = new InternetExplorerDriver();    
	};
}
