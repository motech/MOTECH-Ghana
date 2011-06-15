package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rupeshd
 * Date: 2/11/11
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchPage {
    private WebDriver driver;
    WebElement inputMotechID;
    WebElement buttonSubmit;
    WebElement matchTable;
    WebElement inputLastName;
    WebElement inputFirstName;

    public SearchPage(){
        driver = DefaultPage.getInstance();
        inputMotechID = driver.findElement(By.id("motechId"));
        buttonSubmit =driver.findElement(By.xpath("//input[@type='submit']"));
        inputLastName = driver.findElement(By.id("lastName"));
        inputFirstName = driver.findElement(By.id("firstName"));
    }

    public boolean searchClientByID(String motechID,String lastName){
        inputMotechID.sendKeys(motechID);
        buttonSubmit.click();
        matchTable = driver.findElement(By.xpath("//div[@id='content']/div[3]/table"));
        List<WebElement> tdList= matchTable.findElements(By.tagName("td"));
        for( WebElement td:tdList){
           if (td.getText().trim().equals(lastName)){
               return true;
           }
        }
        return false;
    }

    public boolean searchClientByFirstName(String firstName){
        inputFirstName.sendKeys(firstName);
        buttonSubmit.click();
        matchTable = driver.findElement(By.xpath("//div[@id='content']/div[3]/table"));
        List<WebElement> tdList= matchTable.findElements(By.tagName("td"));
        for( WebElement td:tdList){
           if (td.getText().trim().equals(firstName)){
               return true;
           }
        }
        return false;

    }

    public String returnClientIdBySearchingUsingFirstName(String firstName){
        inputFirstName.sendKeys(firstName);
        buttonSubmit.click();
        matchTable = driver.findElement(By.xpath("//div[@id='content']/div[3]/table"));
        List<WebElement> tdList= matchTable.findElements(By.tagName("td"));
        for(WebElement td:tdList){
            if (td.getText().trim().equals(firstName)){
                String MoTeCHid = driver.findElement(By.xpath("//div[@id='content']/div[3]/table/tbody/tr/td[1]")).toString();
                return MoTeCHid;
            }
        }
        return  null;
    }
}
