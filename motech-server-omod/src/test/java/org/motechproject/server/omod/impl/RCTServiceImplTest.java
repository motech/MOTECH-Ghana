package org.motechproject.server.omod.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.svc.RCTService;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Patient;
import org.motechproject.ws.rct.RCTRegistrationConfirmation;
import org.openmrs.User;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class RCTServiceImplTest extends BaseModuleContextSensitiveTest {

    private static String RCT_DATA = "rct-dataset.xml";


    @Autowired
    @Qualifier("rctService")
    private RCTService service;

    @Autowired
    private ContextService contextService;

    @Before
    public void setUp() throws Exception {
        executeDataSet(RCT_DATA);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void shouldRegisterRCTPatient() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,2);
        Date deliveryDate = calendar.getTime();
        Patient patient = new Patient();
        patient.setMotechId("123654");
        patient.setEstimateDueDate(deliveryDate);
        patient.setContactNumberType(ContactNumberType.PERSONAL);
        RCTRegistrationConfirmation confirmation = service.register(patient, user(), facility());
        assertNotNull(confirmation);
    }

    private User user() {
        return contextService.getUserService().getUser(1);
    }

    private Facility facility() {
        return contextService.getRegistrarBean().getFacilityById(11117);
    }

}
