package org.motechproject.server.builder;

import org.junit.Ignore;
import org.openmrs.Patient;

@Ignore
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
