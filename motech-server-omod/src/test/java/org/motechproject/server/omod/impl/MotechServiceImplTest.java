package org.motechproject.server.omod.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.omod.MotechService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class MotechServiceImplTest extends BaseModuleContextSensitiveTest {

    private static String DUPLICATE_PATIENTS = "duplicate-patients-dataset.xml";
    
    @Before
    public void setUp() throws Exception {
        executeDataSet(DUPLICATE_PATIENTS);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetAllDuplicatePatients() throws Exception {
        MotechService motechService = Context.getService(MotechService.class);
        assertThat(motechService.getAllDuplicatePatients().size(), is(equalTo(2)));
    }
}
