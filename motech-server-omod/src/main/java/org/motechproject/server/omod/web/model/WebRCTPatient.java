package org.motechproject.server.omod.web.model;

public class WebRCTPatient {

    private String firstName;

    private String lastName;

    private String studyId;

    private String staffId;

    private String staffFirstName;

    private String staffLastName;

    public WebRCTPatient(){}

    public WebRCTPatient(String firstName, String lastName, String studyId,
                         String staffId, String staffFirstName, String staffLastName){
        this.firstName = firstName;
        this.lastName = lastName;
        this.studyId = studyId;
        this.staffId = staffId;
        this.staffFirstName = staffFirstName;
        this.staffLastName = staffLastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStudyId() {
        return studyId;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getStaffFirstName() {
        return staffFirstName;
    }

    public String getStaffLastName() {
        return staffLastName;
    }
}
