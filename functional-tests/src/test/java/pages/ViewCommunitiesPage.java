package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: rupeshd
 * Date: 2/15/11
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewCommunitiesPage {

    WebDriver driver;
    WebElement linkAddCommunity;
    WebElement inputCommunityName;
    WebElement selectFacilityName;
    WebElement btnSubmit;

    public ViewCommunitiesPage() {
        driver = DefaultPage.getInstance();
        linkAddCommunity = driver.findElement(By.linkText("Add a new Community"));
    }

    public String AddCommunity(String facilityName){
        linkAddCommunity.click();

        inputCommunityName = driver.findElement(By.id("name"));
        selectFacilityName = driver.findElement(By.id("facilityId"));
        btnSubmit = driver.findElement(By.xpath("//input[@type ='submit']"));
        String communityName = "Facility-"+ getCurrentDate();

        inputCommunityName.sendKeys(communityName);
        selectFacilityName.findElement(By.xpath("//option[contains(text(),'"+ facilityName+"')]")).setSelected();
        btnSubmit.click();

        return communityName;
    }

    public Boolean DoesCommunityNameExist(String communityName , String facilityName){
        String xpathForCommunityName = "//div[contains(text(),'"+facilityName+"')]/following-sibling::table[1]//a[contains(text(),'"+communityName+"')]";
        try {
                driver.findElement(By.xpath(xpathForCommunityName));
            return true;

        } catch (NoSuchElementException e) {
            return false;
        }
    }
    public String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String date = sdf.format(Calendar.getInstance().getTime());
        return date;
    }
}
