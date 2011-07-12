package org.motechproject.server.svc.impl;

import org.junit.Test;
import org.motechproject.server.model.PatientEditor;
import org.openmrs.Patient;
import org.openmrs.PersonAddress;

import static org.junit.Assert.assertTrue;

public class PatientAddressEditorTest {

    @Test
    public void shouldAddNewAddressIfNoneExists() {
        Patient patient = new Patient();
        PatientEditor editor = new PatientEditor(patient);
        PersonAddress address = new PersonAddress();
        address.setAddress1("1st Cross");
        address.setRegion("Upper East");
        address.setCountyDistrict("Kassenna Nankana West");
        address.setStateProvince("Navio");
        address.setNeighborhoodCell("Navio CHPS");

        editor.editAddress(address).done();

        assertTrue(patient.getAddresses().size() == 1);
        for (PersonAddress personAddress : patient.getAddresses()) {
            assertTrue(personAddress.equalsContent(address));
        }
    }

    @Test
    public void shouldVoidExistingAddressAndAddNewOne() {
       Patient patient = new Patient();
        PatientEditor editor = new PatientEditor(patient);

        PersonAddress oldAddress = new PersonAddress();
        oldAddress.setAddress1("1st Cross");
        oldAddress.setRegion("Upper East");
        oldAddress.setCountyDistrict("Kassenna Nankana West");
        oldAddress.setStateProvince("Navio");
        oldAddress.setNeighborhoodCell("Navio CHPS");

        patient.addAddress(oldAddress);

        PersonAddress newAddress = new PersonAddress();
        newAddress.setAddress1("2nd Cross");
        newAddress.setRegion("Upper East");
        newAddress.setCountyDistrict("Kassenna Nankana West");
        newAddress.setStateProvince("Navio");
        newAddress.setNeighborhoodCell("Navio CHPS");

        editor.editAddress(newAddress).done();

        assertTrue(patient.getAddresses().size() == 2);
        for (PersonAddress personAddress : patient.getAddresses()) {
            if(personAddress.isVoided()){
                assertTrue(personAddress.equalsContent(oldAddress));
            }else{
                assertTrue(personAddress.equalsContent(newAddress));
            }
        }
    }


}
