package org.motechproject.server.omod.builder;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.IdentifierGenerator;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.omod.PersonAttributeTypeEnum;
import org.motechproject.ws.RegistrantType;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;

import java.util.ArrayList;
import java.util.List;

class ChildBuilder extends PatientBuilder{

    private static Log log = LogFactory.getLog(ChildBuilder.class);

    private List<PersonAttributeTypeEnum> attributesInheritedFromParent;
    private final PatientBuilder patientBuilder;

    ChildBuilder(PersonService personService, MotechService motechService, IdentifierGenerator idGenerator,
                 PatientService patientService, LocationService locationService, PatientBuilder patientBuilder) {
        super(personService, motechService, idGenerator, RegistrantType.CHILD_UNDER_FIVE, patientService, locationService);
        this.patientBuilder = patientBuilder;
        attributesInheritedFromParent = new ArrayList<PersonAttributeTypeEnum>(){
            {
                add(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER);
                add(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_TYPE);
                add(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_MEDIA_TYPE);
                add(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_LANGUAGE);
                add(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_DELIVERY_DAY);
                add(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_DELIVERY_TIME);
            }
        };
    }

    private void handleInheritedAttributes() {
        for (PersonAttributeTypeEnum attributeType : attributesInheritedFromParent) {
            if (!patientBuilder.isAttributePresent(attributeType.getAttributeName())) {

                PersonAttribute attribute = patientBuilder.getParent().getAttribute(attributeType.getAttributeName());

                if (attribute != null) {
                    String parentAttribute = attribute.getValue();
                    try {
                        patientBuilder.addAttribute(attributeType, parentAttribute, "toString");
                    } catch (Exception e) {
                        log.error("Unexpected Exception when creating attribute");
                    }
                }
            }
        }
    }


    private String selectAddress1() {
        return (patientBuilder.getAddress1()!=null)? patientBuilder.getAddress1() : patientBuilder.getParent().getPersonAddress().getAddress1();
    }

    private String selectLastName() {
        return (patientBuilder.getLastName()!=null)? patientBuilder.getLastName() : patientBuilder.getParent().getFamilyName();
    }

    public Patient buildChild() {
        patientBuilder.setName(patientBuilder.getFirstName(), patientBuilder.getMiddleName(), selectLastName());
        patientBuilder.setAddress1(selectAddress1());
        handleInheritedAttributes();
        Patient child = patientBuilder.buildPatient();
        return child;
    }
}
