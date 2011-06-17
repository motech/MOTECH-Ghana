package org.motechproject.server.model;

import org.junit.Test;
import org.motechproject.server.model.ghana.Facility;

import static junit.framework.Assert.assertEquals;


public class FacilityTest {

    @Test
    public void getAvailablePhoneNumbersShouldReturnNonNullPhoneNumbers() throws Exception {
        Facility facility = new Facility();
        facility.setPhoneNumber("phoneNo");
        facility.setAdditionalPhoneNumber1("phoneNo");
        facility.setAdditionalPhoneNumber2(null);
        facility.setAdditionalPhoneNumber3("phoneNo");
        assertEquals(3, facility.getAvailablePhoneNumbers().size());
    }
}
