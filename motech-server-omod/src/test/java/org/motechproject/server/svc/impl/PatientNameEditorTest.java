package org.motechproject.server.svc.impl;

import org.junit.Test;
import org.motechproject.server.model.PatientEditor;
import org.openmrs.Patient;
import org.openmrs.PersonName;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PatientNameEditorTest {

    @Test
    public void shouldAddNameIfNoneExistsAndAlsoAddPreferredNameForTheSame() {
        Patient patient = new Patient();
        PatientEditor editor = new PatientEditor(patient);
        Patient editedPatient = editor.editName(new PersonName("firstName", "middleName", "familyName")).done();
        Set<PersonName> personNames = editedPatient.getNames();
        assertTrue(personNames.size() == 1);
        assertEquals("firstName", editedPatient.getGivenName());
        assertEquals("middleName", editedPatient.getMiddleName());
        assertEquals("familyName", editedPatient.getFamilyName());
    }

    @Test
    public void shouldEditNameIfExists() {
        Patient patient = new Patient();
        patient.addName(new PersonName("a1", "a2", "a3"));
        PatientEditor editor = new PatientEditor(patient);
        Patient editedPatient = editor.editName(new PersonName("firstName", "middleName", "familyName")).done();
        Set<PersonName> personNames = editedPatient.getNames();
        assertTrue(personNames.size() == 1);
        assertEquals("firstName", editedPatient.getGivenName());
        assertEquals("middleName", editedPatient.getMiddleName());
        assertEquals("familyName", editedPatient.getFamilyName());
    }

    @Test
    public void shouldEditPreferredNameOnlyIfPreferredNameSupplied() {
        Patient patient = new Patient();
        PersonName personName = new PersonName("a1", "a2", "a3");
        personName.setPreferred(true);
        patient.addName(personName);
        PatientEditor editor = new PatientEditor(patient);
        Patient editedPatient = editor.editName(new PersonName("firstName", "middleName", "familyName")).done();
        Set<PersonName> personNames = editedPatient.getNames();
        assertTrue(personNames.size() == 1);
        assertEquals("a1", editedPatient.getGivenName());
        assertEquals("a2", editedPatient.getMiddleName());
        assertEquals("a3", editedPatient.getFamilyName());
    }

    @Test
    public void shouldAddPreferredNameIfPreferredNameSuppliedAndNoneExists() {
        Patient patient = new Patient();
        PersonName name = new PersonName("a1", "a2", "a3");
        patient.addName(name);
        PatientEditor editor = new PatientEditor(patient);
        PersonName personName = new PersonName("firstName", "middleName", "familyName");
        personName.setPreferred(true);
        Patient editedPatient = editor.editName(personName).done();
        Set<PersonName> personNames = editedPatient.getNames();
        assertTrue(personNames.size() == 2);
        assertEquals("firstName", editedPatient.getGivenName());
        assertEquals("middleName", editedPatient.getMiddleName());
        assertEquals("familyName", editedPatient.getFamilyName());
    }

    @Test
    public void shouldEditPreferredNameIfPreferredNameSuppliedAndOneExists() {
        Patient patient = new Patient();
        PersonName name = new PersonName("a1", "a2", "a3");
        patient.addName(name);
        PersonName name2 = new PersonName("b1","b2","b3");
        name2.setPreferred(true);
        patient.addName(name2);
        PatientEditor editor = new PatientEditor(patient);
        PersonName newPreferredName = new PersonName("firstName", "middleName", "familyName");
        newPreferredName.setPreferred(true);
        Patient editedPatient = editor.editName(newPreferredName).done();
        Set<PersonName> personNames = editedPatient.getNames();
        assertTrue(personNames.size() == 2);
        assertEquals("firstName", editedPatient.getGivenName());
        assertEquals("middleName", editedPatient.getMiddleName());
        assertEquals("familyName", editedPatient.getFamilyName());
    }

    @Test
    public void doNotSetPreferredNameIfNotGiven() {
        Patient patient = new Patient();
        PersonName john = new PersonName("John", "J", "Jovi");
        patient.addName(john);

        PatientEditor editor = new PatientEditor(patient);
        
        patient = editor.editPreferredName(new PersonName("","","")).done();

        assertEquals(1,patient.getNames().size());
        assertEquals("John",patient.getGivenName());
    }

    @Test
    public void deletePreferredName() {
        Patient patient = new Patient();
        PersonName john = new PersonName("John", "J", "Jovi");
        john.setPreferred(true);
        PersonName jonny = new PersonName("Jonny", "J", "Jovi");
        patient.addName(john);
        patient.addName(jonny);

        PatientEditor editor = new PatientEditor(patient);

        patient = editor.editPreferredName(new PersonName("","","")).done();

        assertEquals(1,patient.getNames().size());
        assertEquals("Jonny",patient.getGivenName());
    }

}
