package WebdriverFiles;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.*;
import org.testng.Assert;
import org.testng.annotations.Test;


public class RegisterStaff extends WebdriverBaseClass {
	
	
	//@Test
	 public WebElement createNewStaff() {
	                 
		   driver.get("http://localhost:8080/openmrs/module/motechmodule/staff.form");
		
	        WebElement username = driver.findElement(By.id("username"));
	        WebElement password = driver.findElement(By.id("password"));
	        WebElement login = driver.findElement(By.xpath("//input[@value = 'Log In']"));
	        
	        // Loging into OpenMRS
            username.sendKeys("admin");
	        password.sendKeys("Openmr5tw");
	        login.click();
	      
            // Filling the Register Staff form
            WebElement input_First_Name = driver.findElement(By.id("firstName"));
            WebElement input_Last_Name = driver.findElement(By.id("lastName"));
            WebElement input_Phone_Number = driver.findElement(By.id("phone"));
            WebElement btn_Submit_Query = driver.findElement(By.xpath("//input[@type='submit']"));
            WebElement select_Staff_Type = driver.findElement(By.id("type"));
            input_First_Name.sendKeys("F_Agent=1");
            input_Last_Name.sendKeys("1");
            input_Phone_Number.sendKeys("0123456789");
            
            // selecting the Field Agent option
            List<WebElement> options = select_Staff_Type.findElements(By.tagName("option")); 
            for (Integer i=0;i<options.size();i++){
              if (options.get(i).getText().equals("Field agent")){
            		options.get(i).setSelected();
            	}
            }
                    
            btn_Submit_Query.click();
            
            // Extracting the staff id 
            WebElement staff_id_string = driver.findElement(By.xpath("//div[@id='content']/span"));
            return staff_id_string;
            
	}

public String extract_staffid (WebElement staff_id_string){
	 String temp_text[] = staff_id_string.getText().trim().split("=");
     Integer len = temp_text.length;
     
     String staff_id = temp_text[len -1];
     System.out.println(staff_id);
     return staff_id;
}

	  
}		
	
/*
//Assert.assertEquals(driver.getTitle(),"OpenMRS - Home");    
 //Navigating to MoTeCH page
	       // Assert.assertEquals(driver.getCurrentUrl(),"http://localhost:8080/openmrs/index.htm");	  
	        //WebElement link_MoTeCH = driver.findElement(By.linkText("MoTeCH"));
           // link_MoTeCH.click(); 
            
 
 */
    
