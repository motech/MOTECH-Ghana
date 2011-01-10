package org.motechproject.server.omod;

import org.openmrs.PersonAttributeType;

public enum PersonAttributeTypeEnum {
    PERSON_ATTRIBUTE_INSURED("Insured"),
    PERSON_ATTRIBUTE_PHONE_NUMBER("Phone Number"),
    PERSON_ATTRIBUTE_NHIS_NUMBER("NHIS Number"),
    PERSON_ATTRIBUTE_NHIS_EXP_DATE("NHIS Expiration Date"),
    PERSON_ATTRIBUTE_PHONE_TYPE("Phone Type"),
    PERSON_ATTRIBUTE_LANGUAGE("Language"),
    PERSON_ATTRIBUTE_MEDIA_TYPE("Media Type"),
    PERSON_ATTRIBUTE_HOW_LEARNED("How learned of service"),
    PERSON_ATTRIBUTE_INTEREST_REASON("Reason interested in service"),
    PERSON_ATTRIBUTE_DELIVERY_DAY("Delivery Day"),
    PERSON_ATTRIBUTE_DELIVERY_TIME("Delivery Time");

    private String attributeName;

    private PersonAttributeTypeEnum(String attributeName){
        this.attributeName = attributeName;
    }
    
    public PersonAttributeType getAttributeType(ContextService contextService){
        return contextService.getPersonService().getPersonAttributeTypeByName(attributeName);
    }
}
