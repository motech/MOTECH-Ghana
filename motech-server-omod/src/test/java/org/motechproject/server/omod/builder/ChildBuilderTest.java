package org.motechproject.server.omod.builder;


import org.junit.Test;
import org.motechproject.server.omod.IdentifierGenerator;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.omod.PersonAttributeTypeEnum;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.RegistrantType;
import org.openmrs.*;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChildBuilderTest {
    private PatientBuilder builder;
    private Patient expectedPatient;
    private Patient mother;
    private PersonService personService = mock(PersonService.class);
    private MotechService motechService = mock(MotechService.class);
    private IdentifierGenerator idGenerator = mock(IdentifierGenerator.class);
    private PatientService patientService = mock(PatientService.class);
    private LocationService locationService = mock(LocationService.class);

    public ChildBuilderTest() {
        builder = new PatientBuilder(personService, motechService,idGenerator, RegistrantType.CHILD_UNDER_FIVE, patientService, locationService);
        expectedPatient = new Patient();
        initializeParent();
        builder.setParent(mother);

        when(personService.getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER.getAttributeName())).thenReturn(createAttributeType(1, MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER));
        when(personService.getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_TYPE.getAttributeName())).thenReturn(createAttributeType(2, MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE));
        when(personService.getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_MEDIA_TYPE.getAttributeName())).thenReturn(createAttributeType(3, MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE));
        when(personService.getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_LANGUAGE.getAttributeName())).thenReturn(createAttributeType(4, MotechConstants.PERSON_ATTRIBUTE_LANGUAGE));
        when(personService.getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_DELIVERY_DAY.getAttributeName())).thenReturn(createAttributeType(5, MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY));
        when(personService.getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_DELIVERY_TIME.getAttributeName())).thenReturn(createAttributeType(6, MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME));
    }

    private void initializeParent() {
        mother = new Patient();

        mother.addName(new PersonName("motherFirstName","motherMiddleName","motherLastName"));
        PersonAddress personAddress = new PersonAddress();
        personAddress.setAddress1("motherAddress");
        mother.addAddress(personAddress);

        mother.addAttribute(new PersonAttribute(createAttributeType(1, MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER), "1"));
        mother.addAttribute(new PersonAttribute(createAttributeType(2, MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE), ContactNumberType.PUBLIC.name()));
        mother.addAttribute(new PersonAttribute(createAttributeType(3, MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE), MediaType.TEXT.name()));
        mother.addAttribute(new PersonAttribute(createAttributeType(4, MotechConstants.PERSON_ATTRIBUTE_LANGUAGE),"motherLanguage"));
        mother.addAttribute(new PersonAttribute(createAttributeType(5, MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY),"delivery day"));
        mother.addAttribute(new PersonAttribute(createAttributeType(6, MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME),"delivery time"));
    }

    @Test
    public void shouldUseMotherFamilyNameWhenFamilyNameNotSpecified(){

        builder.setName("firstName", "middleName", null);
        Patient child = builder.build();

        expectedPatient.addName(new PersonName("firstName", "middleName", "motherLastName"));

        assertEquals(expectedPatient.getFamilyName(), child.getFamilyName());
        assertEquals(expectedPatient.getGivenName(), child.getGivenName());
        assertEquals(expectedPatient.getMiddleName(), child.getMiddleName());
    }


    @Test
    public void shouldUseMotherAddressWhenAddressNotSpecified(){

        builder.setAddress1(null);
        Patient child = builder.build();

        PersonAddress personAddress = new PersonAddress();
        personAddress.setAddress1("motherAddress");
        expectedPatient.addAddress(personAddress);

        assertEquals(expectedPatient.getPersonAddress().getAddress1(), child.getPersonAddress().getAddress1());
    }




    @Test
    public void shouldUseMotherPhoneNumberWhenPhoneNumberNotSpecified(){

        PersonAttributeType phoneNumberAttributeType = createAttributeType(1, MotechConstants.PERSON_ATTRIBUTE_PHONE_NUMBER);

        Patient child = builder.build();
        assertEquals("1", child.getAttribute(phoneNumberAttributeType.getName()).getValue());
    }

    @Test
    public void shouldUseMotherOwershipWhenOwnershipNotSpecified(){

        PersonAttributeType phoneTypeAttributeType = createAttributeType(2, MotechConstants.PERSON_ATTRIBUTE_PHONE_TYPE);

        Patient child = builder.build();
        assertEquals(ContactNumberType.PUBLIC.name(), child.getAttribute(phoneTypeAttributeType.getName()).getValue());
    }

    @Test
    public void shouldUseMotherFormatWhenFormatNotSpecified(){

        PersonAttributeType mediaTypeAttributeType = createAttributeType(3, MotechConstants.PERSON_ATTRIBUTE_MEDIA_TYPE);

        Patient child = builder.build();
        assertEquals(MediaType.TEXT.name(), child.getAttribute(mediaTypeAttributeType.getName()).getValue());
    }

    @Test
    public void shouldUseMotherLanguageWhenLanguageNotSpecified(){

        PersonAttributeType mediaTypeAttributeType = createAttributeType(4, MotechConstants.PERSON_ATTRIBUTE_LANGUAGE);

        Patient child = builder.build();
        assertEquals("motherLanguage", child.getAttribute(mediaTypeAttributeType.getName()).getValue());
    }

    @Test
    public void shouldUseMotherDeliveryDayWhenDeliveryDayNotSpecified(){

        PersonAttributeType deliveryDay = createAttributeType(5, MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY);

        Patient child = builder.build();
        assertEquals("delivery day", child.getAttribute(deliveryDay.getName()).getValue());
    }

    @Test
    public void shouldUseMotherTimeOfDayWhenTimeOfDayDayNotSpecified(){

        PersonAttributeType deliveryDay = createAttributeType(6, MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);

        Patient child = builder.build();
        assertEquals("delivery time", child.getAttribute(deliveryDay.getName()).getValue());
    }



    private PersonAttributeType createAttributeType(int id, String name) {
        PersonAttributeType phoneNumberAttributeType = new PersonAttributeType();
        phoneNumberAttributeType.setId(id);
        phoneNumberAttributeType.setName(name);
        return phoneNumberAttributeType;
    }
}
