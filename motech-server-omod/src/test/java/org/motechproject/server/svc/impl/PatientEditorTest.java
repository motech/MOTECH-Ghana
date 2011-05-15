package org.motechproject.server.svc.impl;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.PatientEditor;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Patient;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PatientEditorTest extends BaseModuleContextSensitiveTest {

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
        assertTrue(newFacility.hasPatients(patient));
        assertFalse(oldFacility.hasPatients(patient));
    }

    @Test
    public void shouldEditPatientCommunity() {
        Patient patient = patient(100);
        Community oldCommunity = communityFor(patient);
        Community newCommunity = community(11112);
        patient = new PatientEditor(patient).removeFrom(oldCommunity).addTo(newCommunity).done();
        assertTrue(newCommunity.hasResident(patient));
        assertFalse(oldCommunity.hasResident(patient));
    }

    @Test
    public void shouldRemoveFromOldCommunityIfNewCommunityIsNull() {
        Patient patient = patient(100);
        Community oldCommunity = communityFor(patient);
        Community newCommunity = null;
        patient = new PatientEditor(patient).removeFrom(oldCommunity).addTo(newCommunity).done();
        assertFalse(oldCommunity.hasResident(patient));
    }

    @Test
    public void shouldAddToNewCommunityOnlyWhenOldCommunityIsNull() {
        Patient patient = patient(100);
        Community oldCommunity = null;
        Community newCommunity = community(11112);
        patient = new PatientEditor(patient).removeFrom(oldCommunity).addTo(newCommunity).done();
        assertTrue(newCommunity.hasResident(patient));
    }

    private Community community(Integer communityId) {
        return registrarBean.getCommunityById(communityId);
    }

    private Community communityFor(Patient patient) {
        return registrarBean.getCommunityByPatient(patient);
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
