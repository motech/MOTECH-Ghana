package pages;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class RegisterPatientPage {
    private WebDriver driver;

    WebElement inputFirstName = driver.findElement(By.id("firstName"));
    WebElement inputMiddleName = driver.findElement(By.id("middleName"));
    WebElement inputLastName = driver.findElement(By.id("lastName"));
    WebElement inputPreferredName = driver.findElement(By.id("prefName"));
    WebElement inputDOB = driver.findElement(By.id("birthDate"));

    public RegisterPatientPage(){
         driver = DefaultPage.getInstance();
    }

    public void RegisterMotherClient(String firstName, String middleName, String preferredName, String lastName, String DOB){

      selectRegistrationMode("Auto-generate MoTeCH ID");
      selectPatientType("Pregnant mother");

      inputFirstName.sendKeys(firstName);
      inputMiddleName.sendKeys(middleName);
      inputLastName.sendKeys(lastName);
      inputPreferredName.sendKeys(preferredName);
      inputDOB.sendKeys(DOB);

      selectEstimatedDOB("Yes");

    }

    public void selectRegistrationMode(String regMode){
        List<WebElement> regOptions = driver.findElements(By.id("registrationMode"));
        for (Integer i=0;i<regOptions.size();i++){
          if (regOptions.get(i).getText().equals(regMode)){
              regOptions.get(i).setSelected();
          }
        }

    }

    public void selectPatientType(String patientType){
       List<WebElement> patientOptions = driver.findElements(By.id("registrantType"));
        for (Integer i=0;i<patientOptions.size();i++){
          if (patientOptions.get(i).getText().equals(patientType)){
                patientOptions.get(i).setSelected();
            }
        }
    }

    public void selectEstimatedDOB(String estimatedDOB){
       List<WebElement> estimatedDOBOptions = driver.findElements(By.id("birthDateEst"));
        for (Integer i=0;i<estimatedDOBOptions.size();i++){
          if (estimatedDOBOptions.get(i).getText().equals(estimatedDOB)){
                estimatedDOBOptions.get(i).setSelected();
            }
        }
    }

}












