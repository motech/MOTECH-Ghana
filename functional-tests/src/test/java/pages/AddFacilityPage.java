package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import util.UtilityClass;

import java.util.List;

public class AddFacilityPage {

    private WebDriver driver;
    WebElement facilityName;
    WebElement country;
    WebElement region;
    WebElement district;
    WebElement subDistrict;
    WebElement primaryPhoneNumber;
    WebElement submit;

    public AddFacilityPage(){
        driver = DefaultPage.getInstance();

    }

    private void InitializeDefaultObjects(){
        facilityName = driver.findElement(By.id("name"));
        country = driver.findElement(By.id("country"));
        region = driver.findElement(By.id("region"));
        district = driver.findElement(By.id("countyDistrict"));
        subDistrict = driver.findElement(By.id("stateProvince"));
        primaryPhoneNumber = driver.findElement(By.id("phoneNumber"));
        submit = driver.findElement(By.id("submit_facility"));

    }

    public String AddNewFacilityWithFourPhoneNumber(){
         InitializeDefaultObjects();
         String facility = "foo-" + UtilityClass.getInstance().getCurrentDate();
         facilityName.sendKeys(facility);
         selectOption("country","Ghana");
         selectOption("region", "Upper East");
         selectOption("countyDistrict","Kassena-Nankana");
         selectOption("stateProvince","Central East");
         primaryPhoneNumber.sendKeys("0123456789");
         WebElement addAdditionalPhoneNumberLink = driver.findElement(By.linkText("Add additional Phone Number"));
         addAdditionalPhoneNumberLink.click();
         WebElement phoneNumber1 = driver.findElement(By.id("additionalPhoneNumber1"));
         phoneNumber1.sendKeys("0234567891");
         WebElement addAdditionalPhoneNumberLink1 = driver.findElement(By.linkText("Add additional Phone Number"));
         addAdditionalPhoneNumberLink1.click();
         WebElement phoneNumber2 = driver.findElement(By.id("additionalPhoneNumber2"));
         phoneNumber2.sendKeys("0234567891");
         WebElement addAdditionalPhoneNumberLink2 = driver.findElement(By.linkText("Add additional Phone Number"));
         addAdditionalPhoneNumberLink2.click();
         WebElement phoneNumber3 = driver.findElement(By.id("additionalPhoneNumber3"));
         phoneNumber3.sendKeys("0234567891");
         submit.click();
         return facility;
    }

    public String AddNewFacilityWithOnePhoneNumber(){
        InitializeDefaultObjects();
        String facility = "foo-" + UtilityClass.getInstance().getCurrentDate();
        facilityName.sendKeys(facility);
        selectOption("country","Ghana");
        selectOption("region", "Upper East");
        selectOption("countyDistrict","Kassena-Nankana");
        selectOption("stateProvince","Central East");
        primaryPhoneNumber.sendKeys("0123456789");
        submit.click();
        return facility;
    }

    public void selectOption(String ParentId, String optionValue) {
        WebElement element = driver.findElement(By.id(ParentId));
        List<WebElement> elementElements = element.findElements(By.tagName("option"));
        for (WebElement option : elementElements) {
            if (option.getText().equals(optionValue)) {
                option.setSelected();
                break;
            }
        }
    }


}

