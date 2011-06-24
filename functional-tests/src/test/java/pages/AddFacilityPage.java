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

    public String AddFacility(String phoneNumber){
        InitializeDefaultObjects();
        String facility = "foo-" + UtilityClass.getInstance().getCurrentDate();
        facilityName.sendKeys(facility);
        selectOption("country","Ghana");
        selectOption("region", "Upper East");
        selectOption("countyDistrict","Kassena-Nankana");
        selectOption("stateProvince","Central East");
        primaryPhoneNumber.sendKeys(phoneNumber);
        submit.click();
        return facility;
    }

    public String AddFacility(String phoneNumber, String phoneNumber1){
        InitializeDefaultObjects();
        String facility = "foo-" + UtilityClass.getInstance().getCurrentDate();
        facilityName.sendKeys(facility);
        selectOption("country","Ghana");
        selectOption("region", "Upper East");
        selectOption("countyDistrict","Kassena-Nankana");
        selectOption("stateProvince","Central East");
        primaryPhoneNumber.sendKeys(phoneNumber);

        WebElement addAdditionalPhoneNumberLink = driver.findElement(By.linkText("Add additional Phone Number"));
        addAdditionalPhoneNumberLink.click();
        WebElement phoneNumber1Field = driver.findElement(By.id("additionalPhoneNumber1"));
        phoneNumber1Field.sendKeys(phoneNumber1);

        submit.click();
        return facility;
    }

    public String AddFacility(String phoneNumber,String phoneNumber1, String phoneNumber2){
        InitializeDefaultObjects();
        String facility = "foo-" + UtilityClass.getInstance().getCurrentDate();
        facilityName.sendKeys(facility);
        selectOption("country","Ghana");
        selectOption("region", "Upper East");
        selectOption("countyDistrict","Kassena-Nankana");
        selectOption("stateProvince","Central East");
        primaryPhoneNumber.sendKeys(phoneNumber);

        WebElement addAdditionalPhoneNumberLink = driver.findElement(By.linkText("Add additional Phone Number"));
        addAdditionalPhoneNumberLink.click();
        WebElement phoneNumber1Field = driver.findElement(By.id("additionalPhoneNumber1"));
        phoneNumber1Field.sendKeys(phoneNumber1);

        WebElement addAdditionalPhoneNumberLink1 = driver.findElement(By.linkText("Add additional Phone Number"));
        addAdditionalPhoneNumberLink1.click();
        WebElement phoneNumber2Field = driver.findElement(By.id("additionalPhoneNumber2"));
        phoneNumber2Field.sendKeys(phoneNumber2);

        submit.click();
        return facility;
    }
    public String AddFacility(String phoneNumber,String phoneNumber1, String phoneNumber2,String phoneNumber3){

        InitializeDefaultObjects();
        String facility = "foo-" + UtilityClass.getInstance().getCurrentDate();
        facilityName.sendKeys(facility);
        selectOption("country","Ghana");
        selectOption("region", "Upper East");
        selectOption("countyDistrict","Kassena-Nankana");
        selectOption("stateProvince","Central East");
        primaryPhoneNumber.sendKeys(phoneNumber);

        WebElement addAdditionalPhoneNumberLink = driver.findElement(By.linkText("Add additional Phone Number"));
        addAdditionalPhoneNumberLink.click();
        WebElement phoneNumber1Field = driver.findElement(By.id("additionalPhoneNumber1"));
        phoneNumber1Field.sendKeys(phoneNumber1);

        WebElement addAdditionalPhoneNumberLink1 = driver.findElement(By.linkText("Add additional Phone Number"));
        addAdditionalPhoneNumberLink1.click();
        WebElement phoneNumber2Field = driver.findElement(By.id("additionalPhoneNumber2"));
        phoneNumber2Field.sendKeys(phoneNumber2);

        WebElement addAdditionalPhoneNumberLink2 = driver.findElement(By.linkText("Add additional Phone Number"));
        addAdditionalPhoneNumberLink2.click();
        WebElement phoneNumber3Field = driver.findElement(By.id("additionalPhoneNumber3"));
        phoneNumber3Field.sendKeys(phoneNumber3);

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

