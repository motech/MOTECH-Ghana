package org.motechproject.server.omod.web.model;

import org.motechproject.server.model.rct.Stratum;
import org.motechproject.ws.rct.ControlGroup;

public class WebRCTPatient {

    private String firstName;

    private String lastName;

    private String studyId;

    private String staffId;

    private String staffFirstName;

    private String staffLastName;

    private ControlGroup controlGroup;

    private String facilityName;

    private Integer facilityId;

    public WebRCTPatient(){}

    public WebRCTPatient(String firstName, String lastName, String studyId,
                         String staffId, String staffFirstName, String staffLastName,
                         ControlGroup controlGroup, String facilityName, Integer facilityId){
        this.firstName = firstName;
        this.lastName = lastName;
        this.studyId = studyId;
        this.staffId = staffId;
        this.staffFirstName = staffFirstName;
        this.staffLastName = staffLastName;
        this.controlGroup = controlGroup;
        this.facilityName = facilityName;
        this.facilityId = facilityId;
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

    public ControlGroup getControlGroup(){
        return controlGroup;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public Integer getFacilityId() {
        return facilityId;
    }
}
