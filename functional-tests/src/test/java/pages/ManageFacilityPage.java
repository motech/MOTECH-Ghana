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
 * Time: 12:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManageFacilityPage {

    private WebDriver driver;
    WebElement inputFacilityName;
    WebElement inputCountryName;
    WebElement inputRegionName;
    WebElement inputDistrictName;
    WebElement inputSubDistrictName;
    WebElement inputPhoneNumber;
    WebElement btnSubmit;

    WebElement linkAddNewFacility;

    public ManageFacilityPage(){
        driver = DefaultPage.getInstance();
    }


    public boolean FindFacilityByName(String facilityName){

       try {
                driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]"));
            return true;

        } catch (NoSuchElementException e) {
            return false;
        }

    }


}
