package org.motechproject.server.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CareConfigurationTest {

    @Test
    public void shouldCheckWhetherMaximumAlertsHaveBeenSent() {
        CareConfiguration careConfiguration = new CareConfiguration(1L, "ANC", 4);
        assertTrue(careConfiguration.canAlertBeSent(1));
        assertTrue(careConfiguration.canAlertBeSent(2));
        assertFalse(careConfiguration.canAlertBeSent(4));
        assertFalse(careConfiguration.canAlertBeSent(5));
    }


}
