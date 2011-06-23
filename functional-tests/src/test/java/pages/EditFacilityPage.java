package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class EditFacilityPage {
    private WebDriver driver;
    private WebElement primaryPhoneNumber;
    private WebElement submitButton;
    public EditFacilityPage(){
        driver = DefaultPage.getInstance();
    }

    private void InitializeDefaultObjects(){
       primaryPhoneNumber = driver.findElement(By.id("phoneNumber"));
       submitButton = driver.findElement(By.id("submit_facility"));
    }

    public void EditPhoneNumber(String phoneNumber1){
        InitializeDefaultObjects();
        primaryPhoneNumber.clear();
        primaryPhoneNumber.sendKeys(phoneNumber1);
        submitButton.click();
    }

    public void EditPhoneNumber(String phoneNumber, String phoneNumber1){
        InitializeDefaultObjects();
        primaryPhoneNumber.clear();
        primaryPhoneNumber.sendKeys(phoneNumber);

        WebElement additionalPhoneNumberLink = driver.findElement(By.linkText("Add additional Phone Number"));
        additionalPhoneNumberLink.click();
        WebElement additionalPhoneNumber1 = driver.findElement(By.id("additionalPhoneNumber1"));
        additionalPhoneNumber1.sendKeys(phoneNumber1);

        submitButton.click();
    }

    public void EditPhoneNumber(String phoneNumber, String phoneNumber1, String phoneNumber2){
        InitializeDefaultObjects();
        primaryPhoneNumber.clear();
        primaryPhoneNumber.sendKeys(phoneNumber);

        WebElement additionalPhoneNumberLink = driver.findElement(By.linkText("Add additional Phone Number"));
        additionalPhoneNumberLink.click();
        WebElement additionalPhoneNumber1 = driver.findElement(By.id("additionalPhoneNumber1"));
        additionalPhoneNumber1.sendKeys(phoneNumber1);

        WebElement additionalPhoneNumberLink2 = driver.findElement(By.linkText("Add additional Phone Number"));
        additionalPhoneNumberLink.click();
        WebElement additionalPhoneNumber2 = driver.findElement(By.id("additionalPhoneNumber1"));
        additionalPhoneNumber2.sendKeys(phoneNumber2);

        submitButton.click();
    }

    public void EditPhoneNumber(String phoneNumber , String phoneNumber1, String phoneNumber2, String phoneNumber3){
        InitializeDefaultObjects();
        primaryPhoneNumber.clear();
        primaryPhoneNumber.sendKeys(phoneNumber);

        WebElement additionalPhoneNumberLink = driver.findElement(By.linkText("Add additional Phone Number"));
        additionalPhoneNumberLink.click();
        WebElement additionalPhoneNumber1 = driver.findElement(By.id("additionalPhoneNumber1"));
        additionalPhoneNumber1.sendKeys(phoneNumber1);

        WebElement additionalPhoneNumberLink2 = driver.findElement(By.linkText("Add additional Phone Number"));
        additionalPhoneNumberLink.click();
        WebElement additionalPhoneNumber2 = driver.findElement(By.id("additionalPhoneNumber1"));
        additionalPhoneNumber2.sendKeys(phoneNumber2);

        WebElement additionalPhoneNumberLink3 = driver.findElement(By.linkText("Add additional Phone Number"));
        additionalPhoneNumberLink.click();
        WebElement additionalPhoneNumber3 = driver.findElement(By.id("additionalPhoneNumber1"));
        additionalPhoneNumber2.sendKeys(phoneNumber3);

        submitButton.click();
    }


}
