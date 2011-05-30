package pages;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import util.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class RegisterClientPage {
    private WebDriver driver;
    public WebElement inputFirstName;
    WebElement inputMiddleName;
    WebElement inputLastName;
    WebElement inputPreferredName;
    WebElement inputDOB;
    WebElement inputAddress;
    WebElement EDD;
    WebElement consent;
    WebElement phoneNumber;
    WebElement submitButton;
    WebElement inputNHISField;
    WebElement inputNHISExpiryDate;
    WebElement inputMotherMotechID;

    public RegisterClientPage() {
        driver = DefaultPage.getInstance();
        inputFirstName = driver.findElement(By.id("firstName"));
        inputMiddleName = driver.findElement(By.id("middleName"));
        inputLastName = driver.findElement(By.id("lastName"));
        inputPreferredName = driver.findElement(By.id("prefName"));
        inputDOB = driver.findElement(By.id("birthDate"));
        inputAddress = driver.findElement(By.id("address"));
        EDD = driver.findElement(By.id("dueDate"));
        consent = driver.findElement(By.id("consent1"));
        phoneNumber = driver.findElement(By.id("phoneNumber"));
        submitButton = driver.findElement(By.xpath("//input[@type='submit']"));
        inputNHISField = driver.findElement(By.id("nhis"));
        inputNHISExpiryDate = driver.findElement(By.id("nhisExpDate"));
    }

    public String RegisterMotherClient() {
        String lastName = "Last-"+ UtilityClass.getInstance().getCurrentDate();
        selectOption("registrationMode", "Auto-generate MoTeCH ID");
        selectOption("registrantType", "Pregnant mother");
        inputFirstName.sendKeys("Foo");
        inputMiddleName.sendKeys("middle");
        inputLastName.sendKeys(lastName);
        inputPreferredName.sendKeys("preferred");
        inputDOB.sendKeys("01/01/1980");
        selectOption("birthDateEst", "Yes");
        selectOption("insured", "No");
        selectOption("region", "Upper East");
        selectOption("district", "Kassena-Nankana West");
        selectOption("subDistrict", "Chiana");
        selectOption("facility", "Chiana HC");
        selectOption("communityId", "Adasong");
        inputAddress.sendKeys("Address");
        setDueDate();
        selectOption("dueDateConfirmed","Yes");
        selectOption("enroll","Yes");
        consent.click();
        phoneNumber.sendKeys("0123456789");
        selectOption("phoneType","Personal phone");
        selectOption("mediaType","Text");
        selectOption("language","English");
        selectOption("interestReason","Currently pregnant");
        selectOption("howLearned","GHS Nurse");
        selectOption("messagesStartWeek","Pregnancy week 5");
        submitButton.click();
        return lastName;
    }

    //This is a temporary function to facilitate data entry for RCT.
    public String RegisterMotherClientForRCTDataEntry(String dueDate , String phoneType) {
        String lastName = "Last-"+ UtilityClass.getInstance().getCurrentDate();
        selectOption("registrationMode", "Auto-generate MoTeCH ID");
        selectOption("registrantType", "Pregnant mother");
        inputFirstName.sendKeys("Foo");
        inputMiddleName.sendKeys("middle");
        inputLastName.sendKeys(lastName);
        inputPreferredName.sendKeys("preferred");
        inputDOB.sendKeys("01/01/1980");
        selectOption("birthDateEst", "Yes");
        selectOption("insured", "No");
        selectOption("region", "Upper East");
        selectOption("district", "Kassena-Nankana West");
        selectOption("communityId", "Abulu");
        inputAddress.sendKeys("Address");
        setDueDate();
        selectOption("dueDateConfirmed","Yes");
        selectOption("enroll","Yes");
        consent.click();
        phoneNumber.sendKeys("0123456789");
        selectOption("phoneType","Personal phone");
        selectOption("mediaType","Text");
        selectOption("language","English");
        selectOption("interestReason","Currently pregnant");
        selectOption("howLearned","GHS Nurse");
        selectOption("messagesStartWeek","Pregnancy week 5");
        submitButton.click();
        return lastName;
    }

    public String RegisterOtherClient(){
        String lastName = "Last-"+ UtilityClass.getInstance().getCurrentDate();
        selectOption("registrationMode", "Auto-generate MoTeCH ID");
        selectOption("registrantType", "Other");
        inputFirstName.sendKeys("Foo");
        inputMiddleName.sendKeys("middle");
        inputLastName.sendKeys(lastName);
        inputDOB.sendKeys("01/01/1980");
        selectOption("birthDateEst", "Yes");
        selectOption("sex","Male");
        selectOption("insured", "Yes");
        inputNHISField.sendKeys(UtilityClass.getInstance().getCurrentDate());
        inputNHISExpiryDate.sendKeys("01/01/2015");
        selectOption("region", "Upper East");
        selectOption("district", "Kassena-Nankana West");
        selectOption("communityId", "Abulu");
        inputAddress.sendKeys("Address");
        selectOption("enroll","No");
        submitButton.click();
        return lastName;
    }

    public void RegisterChildClient(HashMap hm){
        String lastName = RegisterMotherClient();
        ViewDataPage viewDataPage = new ViewDataPage();
        String motherID = viewDataPage.returnPatientId(lastName);
        if (motherID != null){
            MoTeCHDashBoardPage moTeCHDashBoardPage = new MoTeCHDashBoardPage();
            moTeCHDashBoardPage.navigateToPage(HomePageLinksEnum.REGISTER_PATIENT);
            selectOption("registrationMode", "Auto-generate MoTeCH ID");
            selectOption("registrantType", "Child (age less than 5)");

            inputFirstName = driver.findElement(By.id("firstName"));
            inputMotherMotechID = driver.findElement(By.id("motherMotechId"));
            inputDOB = driver.findElement(By.id("birthDate"));
            consent = driver.findElement(By.id("consent1"));
            submitButton = driver.findElement(By.xpath("//input[@type='submit']"));

            String firstNameValue = "Child-" + UtilityClass.getInstance().getCurrentDate();
            inputFirstName.sendKeys(firstNameValue);
            inputMotherMotechID.sendKeys(motherID);
            inputDOB.sendKeys("01/01/2011");
            selectOption("birthDateEst", "Yes");
            selectOption("sex","Male");
            selectOption("insured", "No");
            selectOption("enroll","Yes");
            consent.click();
            submitButton.click();
            hm.put("lastName",lastName);
            hm.put("firstName",firstNameValue);
        }
        else {
            System.out.println("Mother ID is null");
        }
    }

    public void selectOption(String ParentId, String optionValue) {
        WebElement DOBType = driver.findElement(By.id(ParentId));
        List<WebElement> DOBOptions = DOBType.findElements(By.tagName("option"));
        for (WebElement option : DOBOptions) {
            if (option.getText().equals(optionValue)) {
                option.setSelected();
                break;
            }
        }
    }



    public void setDueDate(){
        // sets the due date nine month hence of the current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,1);
        String date = sdf.format(cal.getTime());
        EDD.sendKeys(date);
    }
}











