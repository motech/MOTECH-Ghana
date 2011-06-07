package org.motechproject.server.omod;

import org.motechproject.server.omod.web.model.WebStaff;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.*;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.util.OpenmrsConstants;

public class MotechUserRepository {

    private IdentifierGenerator identifierGenerator;

    private UserService userService;

    private PersonService personService;

    public MotechUserRepository(IdentifierGenerator identifierGenerator, UserService userService, PersonService personService) {
        this.identifierGenerator = identifierGenerator;
        this.userService = userService;
        this.personService = personService;
    }

    public User newUser(WebStaff webStaff) {
        User user = new User();
        user.setSystemId(identifierGenerator.generateStaffId());
        user.setGender(MotechConstants.GENDER_UNKNOWN_OPENMRS);
        PersonName name = new PersonName(webStaff.getFirstName(), null, webStaff.getLastName());
        user.addName(name);

        String phoneValue = webStaff.getPhone() == null ? "" : webStaff.getPhone();
        String typeValue = webStaff.getType() == null ? "" : webStaff.getType();
        createPhoneNumber(user, phoneValue);
        createType(user, typeValue);

        Role role = userService.getRole(OpenmrsConstants.PROVIDER_ROLE);
        user.addRole(role);
        return user;
    }

    public User updateUser(User staff, WebStaff webStaff) {
        staff.getPersonName().setGivenName(webStaff.getFirstName());
        staff.getPersonName().setFamilyName(webStaff.getLastName());
        String phoneValue = webStaff.getPhone() == null ? "" : webStaff.getPhone();
        String typeValue = webStaff.getType() == null ? "" : webStaff.getType();
        if (attributeExists(staff, PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER)) {
            staff.getAttribute(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER.getAttributeName()).setValue(phoneValue);
        } else {
            createPhoneNumber(staff, phoneValue);
        }
        if (attributeExists(staff, PersonAttributeTypeEnum.PERSON_ATTRIBUTE_STAFF_TYPE)) {
            staff.getAttribute(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_STAFF_TYPE.getAttributeName()).setValue(typeValue);
        } else {
            createType(staff, typeValue);
        }
        return staff;
    }

    private void createPhoneNumber(User user, String phoneValue) {
        PersonAttributeType phoneNumberAttrType = PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER.
                getAttributeType(personService);
        user.addAttribute(new PersonAttribute(phoneNumberAttrType, phoneValue));
    }

    private void createType(User user, String typeValue) {
        PersonAttributeType staffTypeAttrType = PersonAttributeTypeEnum.PERSON_ATTRIBUTE_STAFF_TYPE.
                getAttributeType(personService);
        user.addAttribute(new PersonAttribute(staffTypeAttrType, typeValue));
    }

    private boolean attributeExists(User staff, PersonAttributeTypeEnum personAttributeTypeEnum) {
        return staff.getAttribute(personAttributeTypeEnum.getAttributeName()) != null;
    }
}
