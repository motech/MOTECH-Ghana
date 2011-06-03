package org.motechproject.server.service.impl;

import org.junit.*;
import org.mockito.Mock;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.Encounter;
import org.openmrs.Patient;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegistrationRequirementTest {
    private RegistrationRequirement requirement;
    @Mock
    private RegistrarBean registerBean;

    @Before
    public void setup() {
        initMocks(this);
        requirement = new RegistrationRequirement();
        requirement.setRegistrarBean(registerBean);
    }

    @Test
    public void shouldReturnTrueWhenPatientIsRegisteredAfterValidDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2000, 1, 1);
        Date birthDate = cal.getTime();
        cal.set(2011, 4, 19);
        Date validRegDate = cal.getTime();
        cal.set(2011, 5, 19);
        Date encounterDate = cal.getTime();

        Patient patient = new Patient();
        patient.setBirthdate(birthDate);
        Encounter encounter = new Encounter();
        encounter.setEncounterDatetime(encounterDate);

        when(registerBean.getChildRegistrationDate()).thenReturn(validRegDate);
        when(registerBean.getEncounters(patient, MotechConstants.ENCOUNTER_TYPE_PATIENTREGVISIT, patient.getBirthdate())).thenReturn(Arrays.asList(encounter));
        assertTrue(requirement.meetsRequirement(patient, new Date()));
    }

    @Test
    public void shouldReturnFalseWhenPatientIsRegisteredBeforeValidDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2000, 1, 1);
        Date birthDate = cal.getTime();
        cal.set(2011, 4, 19);
        Date validRegDate = cal.getTime();
        cal.set(2011, 3, 19);
        Date encounterDate = cal.getTime();

        Patient patient = new Patient();
        patient.setBirthdate(birthDate);
        Encounter encounter = new Encounter();
        encounter.setEncounterDatetime(encounterDate);

        when(registerBean.getChildRegistrationDate()).thenReturn(validRegDate);
        when(registerBean.getEncounters(patient, MotechConstants.ENCOUNTER_TYPE_PATIENTREGVISIT, patient.getBirthdate())).thenReturn(Arrays.asList(encounter));
        assertFalse(requirement.meetsRequirement(patient, new Date()));
    }

    @Test
    public void shouldReturnFalseIfNoEncountersArePresentForPatient() {
        Calendar cal = Calendar.getInstance();
        cal.set(2000, 1, 1);
        Date birthDate = cal.getTime();
        cal.set(2011, 4, 19);
        Date validRegDate = cal.getTime();
        cal.set(2011, 3, 19);
        Date encounterDate = cal.getTime();

        Patient patient = new Patient();
        patient.setBirthdate(birthDate);
        Encounter encounter = new Encounter();
        encounter.setEncounterDatetime(encounterDate);

        when(registerBean.getChildRegistrationDate()).thenReturn(validRegDate);
        when(registerBean.getEncounters(patient, MotechConstants.ENCOUNTER_TYPE_PATIENTREGVISIT, patient.getBirthdate())).thenReturn(Collections.EMPTY_LIST);
        assertFalse(requirement.meetsRequirement(patient, new Date()));

    }


}
