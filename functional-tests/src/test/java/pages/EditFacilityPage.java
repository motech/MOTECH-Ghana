package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class EditFacilityPage extends DefaultPage{

    private WebElement primaryPhoneNumber;
    private WebElement submitButton;

    private void initializeDefaultObjects(){
       primaryPhoneNumber = driver.findElement(By.id("phoneNumber"));
       submitButton = driver.findElement(By.id("submit_facility"));
    }

    public void editPhoneNumber(String phoneNumber1){
        initializeDefaultObjects();
        primaryPhoneNumber.clear();
        primaryPhoneNumber.sendKeys(phoneNumber1);
        submitButton.click();
    }

    public void editPhoneNumber(String phoneNumber, String phoneNumber1){
        initializeDefaultObjects();
        primaryPhoneNumber.clear();
        primaryPhoneNumber.sendKeys(phoneNumber);

        WebElement additionalPhoneNumberLink = driver.findElement(By.linkText("Add additional Phone Number"));
        additionalPhoneNumberLink.click();
        WebElement additionalPhoneNumber1 = driver.findElement(By.id("additionalPhoneNumber1"));
        additionalPhoneNumber1.sendKeys(phoneNumber1);

        submitButton.click();
    }

    public void editPhoneNumber(String phoneNumber, String phoneNumber1, String phoneNumber2){
        initializeDefaultObjects();
        primaryPhoneNumber.clear();
        primaryPhoneNumber.sendKeys(phoneNumber);

        WebElement additionalPhoneNumberLink = driver.findElement(By.linkText("Add additional Phone Number"));
        additionalPhoneNumberLink.click();
        WebElement additionalPhoneNumber1 = driver.findElement(By.id("additionalPhoneNumber1"));
        additionalPhoneNumber1.sendKeys(phoneNumber1);

        WebElement additionalPhoneNumberLink2 = driver.findElement(By.linkText("Add additional Phone Number"));
        additionalPhoneNumberLink2.click();
        WebElement additionalPhoneNumber2 = driver.findElement(By.id("additionalPhoneNumber2"));
        additionalPhoneNumber2.sendKeys(phoneNumber2);

        submitButton.click();
    }

    public void editPhoneNumber(String phoneNumber, String phoneNumber1, String phoneNumber2, String phoneNumber3){
        initializeDefaultObjects();
        primaryPhoneNumber.clear();
        primaryPhoneNumber.sendKeys(phoneNumber);

        WebElement additionalPhoneNumberLink = driver.findElement(By.linkText("Add additional Phone Number"));
        additionalPhoneNumberLink.click();
        WebElement additionalPhoneNumber1 = driver.findElement(By.id("additionalPhoneNumber1"));
        additionalPhoneNumber1.sendKeys(phoneNumber1);

        WebElement additionalPhoneNumberLink2 = driver.findElement(By.linkText("Add additional Phone Number"));
        additionalPhoneNumberLink2.click();
        WebElement additionalPhoneNumber2 = driver.findElement(By.id("additionalPhoneNumber2"));
        additionalPhoneNumber2.sendKeys(phoneNumber2);

        WebElement additionalPhoneNumberLink3 = driver.findElement(By.linkText("Add additional Phone Number"));
        additionalPhoneNumberLink3.click();
        WebElement additionalPhoneNumber3 = driver.findElement(By.id("additionalPhoneNumber3"));
        additionalPhoneNumber3.sendKeys(phoneNumber3);

        submitButton.click();
    }


}
