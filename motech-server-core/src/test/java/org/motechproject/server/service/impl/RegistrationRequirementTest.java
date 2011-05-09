package org.motechproject.server.service.impl;

import org.junit.*;
import org.mockito.Mock;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Patient;

import java.util.Calendar;
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
    public void setup(){
       initMocks(this);
       requirement = new RegistrationRequirement();
       requirement.setRegistrarBean(registerBean);
    }

    @Test
    public void shouldVerifyPatientRegistrationDate() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        Patient patient = new Patient();
        patient.setDateCreated(date);

        cal.set(2011,03,19);
        when(registerBean.getChildRegistrationDate()).thenReturn(cal.getTime());
        assertTrue(requirement.meetsRequirement(patient, date));

        cal.set(2010,01,01);
        patient.setDateCreated(cal.getTime());
        assertFalse(requirement.meetsRequirement(patient, date));

    }
}
