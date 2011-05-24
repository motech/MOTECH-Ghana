package org.motechproject.server.omod.builder;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.Community;
import org.motechproject.server.omod.IdentifierGenerator;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.PersonAttributeTypeEnum;
import org.motechproject.ws.RegistrantType;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;

import java.util.ArrayList;
import java.util.List;

class ChildBuilder extends PatientBuilder{

    private static Log log = LogFactory.getLog(ChildBuilder.class);

    private List<PersonAttributeTypeEnum> attributesInheritedFromParent;
    private final PatientBuilder patientBuilder;

    ChildBuilder(PersonService personService, MotechService motechService, IdentifierGenerator idGenerator, PatientService patientService, LocationService locationService, PatientBuilder patientBuilder) {
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
                String parentAttribute = patientBuilder.getParent().getAttribute(attributeType.getAttributeName()).getValue();
                try {
                    addAttribute(attributeType, parentAttribute, "toString");
                } catch (Exception e){
                    log.error("Unexpected Exception when creating attribute");
                }
            }
        }
    }

    private Community selectCommunity() {
        return (patientBuilder.getCommunity()!=null)? patientBuilder.getCommunity(): patientBuilder.getMothechService().getCommunityByPatient(getParent());
    }


    private String selectAddress1() {
        return (patientBuilder.getAddress1()!=null)? patientBuilder.getAddress1() : patientBuilder.getParent().getPersonAddress().getAddress1();
    }

    private String selectLastName() {
        return (patientBuilder.getLastName()!=null)? patientBuilder.getLastName() : patientBuilder.getParent().getFamilyName();
    }

    public Patient buildChild() {
        setName(patientBuilder.getFirstName(), patientBuilder.getMiddleName(), selectLastName());
        setAddress1(selectAddress1());
        setCommunity(selectCommunity());
        handleInheritedAttributes();
        return buildPatient();
    }
}
