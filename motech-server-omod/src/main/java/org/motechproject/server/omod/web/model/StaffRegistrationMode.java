package org.motechproject.server.omod.web.model;

import org.motechproject.server.omod.PersonAttributeTypeEnum;
import org.openmrs.PersonName;
import org.openmrs.User;

public enum StaffRegistrationMode {
       NEW("Added user: Name = %s, Staff ID = %s") {
           @Override
           public String message(User staff) {
               PersonName name = staff.getPersonName();
               String staffId = staff.getSystemId();
               return String.format(messageFormat, name, staffId);
           }},
       EDIT("Saved user info: Name = %s, Staff ID= %s, Phone number: %s, Staff Type: %s") {
           @Override
           public String message(User staff) {
               PersonName name = staff.getPersonName();
               String staffId = staff.getSystemId();
               String phoneNumber
                       = staff.getAttribute(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER.getAttributeName()).getValue();
               String staffType
                       = staff.getAttribute(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_STAFF_TYPE.getAttributeName()).getValue();
               return String.format(messageFormat, name, staffId,phoneNumber, staffType);
           }};

       String messageFormat;

       StaffRegistrationMode(String messageFormat) {
           this.messageFormat = messageFormat;
       }

       public abstract String message(User user);
   }
