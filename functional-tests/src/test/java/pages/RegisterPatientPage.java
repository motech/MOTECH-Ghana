package pages;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class RegisterPatientPage {
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


    public RegisterPatientPage() {
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

    }

    public String RegisterMotherClient() {
        String lastName = "Last_"+ getCurrentDate();
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

   public String getCurrentDate(){
       SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
       String date = sdf.format(Calendar.getInstance().getTime());
       return date;
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











