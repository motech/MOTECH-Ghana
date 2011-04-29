package org.motechproject.server.svc.impl;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.PatientEditor;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Patient;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PatientEditsTest extends BaseModuleContextSensitiveTest {

    @Autowired
    @Qualifier("registrarBean")
    private RegistrarBean registrarBean;
    private static final String PATIENT_EDIT_DATA = "patient-edit-data.xml";

    @Before
    public void setUp() throws Exception {
        executeDataSet(PATIENT_EDIT_DATA);
    }

    @Test
    public void shouldEditPatientFacility() {
        Patient patient = patient(100);
        Facility oldFacility = facilityFor(patient);
        Facility newFacility = facility(11118);
        patient = new PatientEditor(patient).removeFrom(oldFacility).addTo(newFacility).done();
        assertTrue(newFacility.hasPatients(patient)) ;
        assertFalse(oldFacility.hasPatients(patient)) ;
    }

    private Patient patient(Integer patientId) {
        return registrarBean.getPatientById(patientId);
    }

    private Facility facility(Integer facilityId) {
        return registrarBean.getFacilityById(facilityId);
    }

    private Facility facilityFor(Patient patient) {
        return registrarBean.getFacilityByPatient(patient);
    }

}
