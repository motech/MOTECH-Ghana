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
        linkAddNewFacility = driver.findElement(By.linkText("Add a new Facility"));
    }

    public String AddNewFacility(){

        linkAddNewFacility.click();


        String facilityName = "Facility-"+ getCurrentDate();
        inputFacilityName = driver.findElement(By.id("name"));
        inputCountryName = driver.findElement(By.id("country"));
        inputRegionName = driver.findElement(By.id("region"));
        inputDistrictName = driver.findElement(By.id("countyDistrict"));
        inputSubDistrictName = driver.findElement(By.id("stateProvince"));
        inputPhoneNumber = driver.findElement(By.id("phoneNumber"));
        btnSubmit = driver.findElement(By.xpath("//input[@type='submit']"));


        inputFacilityName.sendKeys(facilityName);
        inputCountryName.sendKeys("Country");
        inputRegionName.sendKeys("Region");
        inputDistrictName.sendKeys("District");
        inputSubDistrictName.sendKeys("Subdistrict");
        inputPhoneNumber.sendKeys("0123456789");
        btnSubmit.click();

        return facilityName;

    }

    public boolean FindFacilityByName(String facilityName){

       try {
                driver.findElement(By.linkText(facilityName));
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
