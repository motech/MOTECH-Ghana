package org.motechproject.server.model;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class MotechConfigurationTest {

    @Test
    public void shouldReturnValueAsDate() {
        MotechConfiguration configuration = new MotechConfiguration();
        configuration.setValue("");
        assertNull(configuration.asDate());

        configuration.setValue("10-05-2011");
        Date actual = configuration.asDate();

        DateFormat formatter = new SimpleDateFormat("dd-M-yyyy");
        assertEquals("10-5-2011",formatter.format(actual));
    }
}
