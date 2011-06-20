package pages;

import org.openqa.selenium.WebDriver;
import util.TestConfiguration;

public enum HomePageLinksEnum {

    ADMIN("/openmrs/admin/index.htm"),
    MANAGE_FACILITIES("/openmrs/module/motechmodule/facility.form"),
    REGISTER_STAFF("/openmrs/module/motechmodule/staff.form"),
    REGISTER_PATIENT("/openmrs/module/motechmodule/patient.form"),
    SEARCH("/openmrs/module/motechmodule/search.form"),
    MANAGED_TROUBLED_PHONES("/openmrs/module/motechmodule/troubledphone.form"),
    MANAGE_BLACKOUT("/openmrs/module/motechmodule/blackout.form"),
    VIEW_DATA("/openmrs/module/motechmodule/viewdata.form"),
    VIEW_COMMUNITIES("/openmrs/module/motechmodule/community.form"),
    DUPLICATE_PATIENTS("/openmrs/module/motechmodule/duplicatepatients.form"),
    ADD_FACILITY("/openmrs/module/motechmodule/addfacility.form");
    private String url;

    HomePageLinksEnum(String url){
        String port = TestConfiguration.instance().portNumber();
        this.url = new StringBuilder("http://localhost:").append(port).append(url).toString();
    }

    public void navigate(WebDriver driver)  {

        driver.navigate().to(this.url);
    }
}


