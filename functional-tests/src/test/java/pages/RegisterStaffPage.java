package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by IntelliJ IDEA.
 * User: rupeshd
 * Date: 2/17/11
 * Time: 11:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class RegisterStaffPage extends DefaultPage {

    WebElement inputFirstName;
    WebElement inputLastName;
    WebElement inputPhoneNumber;
    WebElement inputStaffTypeFieldAgent;
    WebElement btnSubmit;
    WebElement spanStaffId;

    public RegisterStaffPage(){
        inputFirstName = driver.findElement(By.id("firstName"));
        inputLastName = driver.findElement(By.id("lastName"));
        inputPhoneNumber = driver.findElement(By.id("phone"));
        inputStaffTypeFieldAgent = driver.findElement(By.xpath("//option[@value='Field agent']"));
        btnSubmit = driver.findElement(By.xpath("//input[@type='submit']"));
    }

    public String createNewStaff(){
      inputFirstName.sendKeys("foo");
      inputLastName.sendKeys("last");
      inputPhoneNumber.sendKeys("0123456789");
      inputStaffTypeFieldAgent.setSelected();
      btnSubmit.click();
      spanStaffId= driver.findElement(By.xpath("//span[contains(text(),'Added user:')]"));
      String spanStaffIDContent = spanStaffId.getText();
      String staffID[] = spanStaffIDContent.split("Staff ID =");
      return staffID[1];
    }
}
