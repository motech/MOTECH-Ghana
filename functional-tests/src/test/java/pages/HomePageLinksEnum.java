package pages;

import org.openqa.selenium.WebDriver;

public enum HomePageLinksEnum {

    ADMIN("http://localhost:8080/openmrs/admin/index.htm"),
    MANAGE_FACILITIES("http://localhost:8080/openmrs/module/motechmodule/facility.form"),
    REGISTER_STAFF("http://localhost:8080/openmrs/module/motechmodule/staff.form"),
    REGISTER_PATIENT("http://localhost:8080/openmrs/module/motechmodule/patient.form"),
    SEARCH("http://localhost:8080/openmrs/module/motechmodule/search.form"),
    MANAGED_TROUBLED_PHONES("http://localhost:8080/openmrs/module/motechmodule/troubledphone.form"),
    MANAGE_BLACKOUT("http://localhost:8080/openmrs/module/motechmodule/blackout.form"),
    VIEW_DATA("http://localhost:8080/openmrs/module/motechmodule/viewdata.form"),
    VIEW_COMMUNITIES("http://localhost:8080/openmrs/module/motechmodule/community.form"),
    DUPLICATE_PATIENTS("http://localhost:8080/openmrs/module/motechmodule/duplicatepatients.form");

    private String url;

    HomePageLinksEnum(String url){
        this.url = url;
    }

    public void navigate(WebDriver driver){
        driver.navigate().to(this.url);
    }

}
