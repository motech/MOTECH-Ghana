package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AddFacilityPage {

    private WebDriver driver;
    WebElement name;
    WebElement country;
    WebElement region;
    WebElement district;
    WebElement subDistrict;
    WebElement phoneNumber;
    WebElement submit;

    public AddFacilityPage(){
        driver = DefaultPage.getInstance();
        name = driver.findElement(By.id("name"));
        country = driver.findElement(By.id("country"));
        region = driver.findElement(By.id("region"));
        district = driver.findElement(By.id("countyDistrict"));
        subDistrict = driver.findElement(By.id("stateProvince"));
        phoneNumber = driver.findElement(By.id("phoneNumber"));
        submit = driver.findElement(By.id("submit_facility"));
    }




}

