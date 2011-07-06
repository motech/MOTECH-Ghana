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
public class ManageFacilityPage extends DefaultPage{

    WebElement inputFacilityName;
    WebElement inputCountryName;
    WebElement inputRegionName;
    WebElement inputDistrictName;
    WebElement inputSubDistrictName;
    WebElement inputPhoneNumber;
    WebElement btnSubmit;

    WebElement linkAddNewFacility;

    public boolean findFacilityByName(String facilityName){

       try {
                driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]"));
            return true;

        } catch (NoSuchElementException e) {
            return false;
        }

    }

    public boolean findFacilityByNameAndPhoneNumber(String facilityName, String phoneNumber){
        if(driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]")).getText().equalsIgnoreCase(facilityName)){
              if (driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]//..//span[1]")).getText().equals(phoneNumber)){
                  return true;
              }
        }

        return false;
    }

    public boolean findFacilityByNameAndPhoneNumber(String facilityName, String phoneNumber, String phoneNumber1){
       if(driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]")).getText().equalsIgnoreCase(facilityName)){
              if (driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]//..//span[1]")).getText().equals(phoneNumber)){
                  if (driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]//..//span[2]")).getText().equals(phoneNumber1)){
                      return true;
                  }
              }
       }
       return false;
    }
    public boolean findFacilityByNameAndPhoneNumber(String facilityName, String phoneNumber, String phoneNumber1, String phoneNumber2){
       if(driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]")).getText().equalsIgnoreCase(facilityName)){
              if (driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]//..//span[1]")).getText().equals(phoneNumber)){
                  if (driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]//..//span[2]")).getText().equals(phoneNumber1)){
                     if(driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]//..//span[3]")).getText().equals(phoneNumber2)){
                         return true;
                     }
                  }
              }
       }
       return false;
    }
    public boolean findFacilityByNameAndPhoneNumber(String facilityName, String phoneNumber, String phoneNumber1, String phoneNumber2, String phoneNumber3){
       if(driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]")).getText().equalsIgnoreCase(facilityName)){
          if (driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]//..//span[1]")).getText().equals(phoneNumber)){
              if (driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]//..//span[2]")).getText().equals(phoneNumber1)){
                 if(driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]//..//span[3]")).getText().equals(phoneNumber2)){
                     if(driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]//..//span[4]")).getText().equals(phoneNumber3)){
                        return true;
                     }
                 }
              }
          }
   }
       return false;
    }

    public void navigateToEditFacilityPage(String facilityName){
        WebElement editLink =  driver.findElement(By.xpath("//td[contains(text(),'"+facilityName+"')]//..//a"));
        editLink.click();
    }


}
