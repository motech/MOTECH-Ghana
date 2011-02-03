package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ViewDataPage {

    private WebDriver driver;

    public ViewDataPage(){
        driver = DefaultPage.getInstance();

    }

    public void getMostRecentlyRegisteredPatient() {
        //WebElement patientTable = driver.findElement(By.xpath("//div[@id='content']/div[5]/table"));
        List<WebElement> ab = driver.findElements(By.xpath("//div[@id='content']/div[5]/table/tbody/tr"));
        Integer largestId = Integer.parseInt(driver.findElement(By.xpath("//div[@id='content']/div[5]/table/tbody/tr[2]/td[1]")).getText().trim());
        for (Integer i = 2; i <= ab.size(); i++) {
            Integer temp = Integer.parseInt(driver.findElement(By.xpath("//div[@id='content']/div[5]/table/tbody/tr["+i +"]/td[1]")).getText().trim());
            if (temp > largestId){
                largestId = temp;}
            }
        System.out.println(largestId);
    }

    public boolean verifyPatientFirstName(String firstName){
        List<WebElement> patient_row = driver.findElements(By.xpath("//div[@id='content']/div[5]/table/tbody/tr"));
        for(Integer i =2 ; i< patient_row.size();i++){
            String temp = driver.findElement(By.xpath("//div[@id='content']/div[5]/table/tbody/tr["+i +"]/td[2]")).getText().trim();
            if(firstName.trim().equals(temp)){
                return true;
            }
        }
        return false;
    }

    public boolean verifyPatientLastName(String lastName){
        List<WebElement> patient_row = driver.findElements(By.xpath("//div[@id='content']/div[5]/table/tbody/tr"));
        for(Integer i= 2 ; i< patient_row.size();i++){
            String temp = driver.findElement(By.xpath("//div[@id='content']/div[5]/table/tbody/tr["+i+"]/td[3]")).getText().trim();
            if(lastName.trim().equals(temp)){
                return true;
            }
        }
        return false;
    }


}

