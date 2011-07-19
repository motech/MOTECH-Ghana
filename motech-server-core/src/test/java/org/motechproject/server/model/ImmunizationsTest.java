package org.motechproject.server.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ImmunizationsTest {

    @Test
    public void shouldGetEnumForKey() {
        assertEquals(Immunizations.BCG,Immunizations.enumFor("bcg"));
        assertEquals(Immunizations.YELLOW_FEVER,Immunizations.enumFor("yellowfever"));
        assertEquals(Immunizations.CSM,Immunizations.enumFor("csm"));
        assertEquals(Immunizations.MEASLES,Immunizations.enumFor("measles"));
        assertEquals(Immunizations.DEWORMER,Immunizations.enumFor("dewormer"));
        assertEquals(Immunizations.VITAMIN_A,Immunizations.enumFor("vitamina"));
        assertNull(Immunizations.enumFor("some random key"));
    }
}
