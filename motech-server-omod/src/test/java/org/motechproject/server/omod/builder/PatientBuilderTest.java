package org.motechproject.server.omod.builder;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;
import org.motechproject.server.model.Community;
import org.motechproject.server.omod.IdentifierGenerator;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.PersonAttributeTypeEnum;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.Gender;
import org.motechproject.ws.RegistrantType;
import org.openmrs.*;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;


public class PatientBuilderTest {
    private PatientBuilder builder;
    private Patient expectedPatient;

    private PersonService personService = mock(PersonService.class);
    private MotechService motechService = mock(MotechService.class);
    private IdentifierGenerator idGenerator = mock(IdentifierGenerator.class);
    private PatientService patientService = mock(PatientService.class);
    private LocationService locationService = mock(LocationService.class);


    public PatientBuilderTest() {
        builder = new PatientBuilder(personService, motechService, idGenerator, RegistrantType.PREGNANT_MOTHER, patientService, locationService);
        expectedPatient = new Patient();
    }

    @Test
    public void shouldUseTheSpecifiedMotechId(){

        PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
        patientIdentifierType.setId(1);
        patientIdentifierType.setName("MoTeCH Id");
        Location location = new Location();
        location.setId(1);
        location.setName(MotechConstants.LOCATION_GHANA);

        when(patientService.getPatientIdentifierTypeByName("MoTeCH Id")).thenReturn(patientIdentifierType);
        when(locationService.getLocation(MotechConstants.LOCATION_GHANA)).thenReturn(location);

        builder.setMotechId(12345);
        Patient patient = builder.build();

        assertEquals("12345", patient.getActiveIdentifiers().get(0).getIdentifier());
    }

    @Test
    public void shouldAutoGenerateIdWhenIdNotSpecified() {
        when(idGenerator.generateMotechId()).thenReturn("12345");

        PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
        patientIdentifierType.setId(1);
        patientIdentifierType.setName("MoTeCH Id");
        Location location = new Location();
        location.setId(1);
        location.setName(MotechConstants.LOCATION_GHANA);

        when(patientService.getPatientIdentifierTypeByName("MoTeCH Id")).thenReturn(patientIdentifierType);
        when(locationService.getLocation(MotechConstants.LOCATION_GHANA)).thenReturn(location);
        Patient patient = builder.build();

        assertEquals("12345", patient.getActiveIdentifiers().get(0).getIdentifier());
    }

    @Test
    public void shouldReturnPatientObjectWithNameSet() {

        builder.setName("firstName", "middleName", "lastName");
        Patient patient = builder.build();

        expectedPatient.addName(new PersonName("firstName", "middleName", "lastName"));

        assertTrue(EqualsBuilder.reflectionEquals(expectedPatient.getFamilyName(), patient.getFamilyName()));
        assertTrue(EqualsBuilder.reflectionEquals(expectedPatient.getGivenName(), patient.getGivenName()));
        assertTrue(EqualsBuilder.reflectionEquals(expectedPatient.getMiddleName(), patient.getMiddleName()));
    }


    @Test
    public void shouldUseThePreferredNameWhenSet() {

        builder.setName("firstName", "middleName", "lastName");
        builder.setPreferredName("setPreferredName");
        Patient patient = builder.build();

        expectedPatient.addName(new PersonName("setPreferredName", "middleName", "lastName"));


        assertEquals(expectedPatient.getFamilyName(), patient.getFamilyName());
        assertEquals(expectedPatient.getGivenName(), patient.getGivenName());
        assertEquals(expectedPatient.getMiddleName(), patient.getMiddleName());
    }

    @Test
    public void shouldReturnPatientObjectWithAddressSet() {

        builder.setAddress1("address1");
        Patient patient = builder.build();

        PersonAddress personAddress = new PersonAddress();
        personAddress.setAddress1("address1");
        expectedPatient.addAddress(personAddress);

        assertEquals(expectedPatient.getPersonAddress().getAddress1(), patient.getPersonAddress().getAddress1());
    }

    @Test
    public void shouldReturnPatientObjectWithGenderSet() {

        builder.setGender(Gender.MALE);
        Patient patient = builder.build();

        expectedPatient.setGender(GenderTypeConverter.toOpenMRSString(Gender.MALE));

        assertTrue(EqualsBuilder.reflectionEquals(expectedPatient.getGender(), patient.getGender()));
    }

    @Test
    public void shouldReturnPatientObjectWithBirthDate() {

        Date birthDate = Calendar.getInstance().getTime();

        builder.setBirthDate(birthDate);
        builder.setBirthDateEstimated(false);
        Patient patient = builder.build();

        expectedPatient.setBirthdate(birthDate);

        assertTrue(EqualsBuilder.reflectionEquals(expectedPatient.getBirthdate(), patient.getBirthdate()));
        assertFalse(expectedPatient.getBirthdateEstimated());
    }

    @Test
    public void shouldReturnPatientObjectWithAnAttribute() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        PersonAttributeType phoneNumberAttributeType = new PersonAttributeType();
        phoneNumberAttributeType.setId(1);

        when(personService.getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER.getAttributeName())).thenReturn(phoneNumberAttributeType);
        Patient patient = builder.build();
        assertNull("Phone number should not be added when it is null", patient.getAttribute(phoneNumberAttributeType));

        builder.addAttribute(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER, "0123456789", "toString");
        patient = builder.build();

        assertEquals("0123456789", patient.getAttribute(phoneNumberAttributeType).toString());
    }

    @Test
    public void shouldReturnPatientWithDeliveryTimeAttribute() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {

        Date now = Calendar.getInstance().getTime();
        SimpleDateFormat timeFormatter = new SimpleDateFormat(MotechConstants.TIME_FORMAT_DELIVERY_TIME);
        PersonAttributeType timeOfDay = new PersonAttributeType();
        timeOfDay.setId(1);

        when(personService.getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_DELIVERY_TIME.getAttributeName())).thenReturn(timeOfDay);

        builder.addAttribute(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_DELIVERY_TIME, now, timeFormatter, "format");
        Patient patient = builder.build();

        assertEquals(timeFormatter.format(now), patient.getAttribute(timeOfDay).toString());


    }



    @Test
    public void shouldNotAddPatientToCommunityWhenCommunityIsNotSet() {
        Community community = mock(Community.class);

        Patient patient = builder.build();

        verify(community, never()).add(patient);
    }

}
