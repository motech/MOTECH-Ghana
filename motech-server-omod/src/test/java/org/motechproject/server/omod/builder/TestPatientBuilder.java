package org.motechproject.server.omod.builder;

import org.openmrs.Patient;

public class TestPatientBuilder {

    private Integer id ;

    public TestPatientBuilder withId(Integer id){
        this.id = id ;
        return this;
    }

    public Patient create(){
        Patient patient = new Patient();
        patient.setId(id);
        return patient;
    }
}
