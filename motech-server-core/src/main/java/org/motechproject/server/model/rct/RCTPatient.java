package org.motechproject.server.model.rct;

import org.motechproject.ws.rct.ControlGroup;
import org.openmrs.User;

import java.util.Date;

public class RCTPatient {

    private Long id;
    private String studyId;
    private Stratum stratum;
    private ControlGroup controlGroup;
    private Date enrollmentDate;
    private User enrolledBy;
    private Character enrolled;

    public RCTPatient() {}

    public RCTPatient(String studyId, Stratum stratum, ControlGroup controlGroup, User enrolledBy) {
        this.studyId = studyId;
        this.stratum = stratum;
        this.controlGroup = controlGroup;
        this.enrolledBy = enrolledBy;
        this.enrollmentDate = new Date();
    }
}
