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

    public MotechUserRepository(IdentifierGenerator identifierGenerator){
        this.identifierGenerator = identifierGenerator;
    }

    public User newUser(WebStaff webStaff){
        User user = new User();
        user.setSystemId(identifierGenerator.generateStaffId());
        user.setGender(MotechConstants.GENDER_UNKNOWN_OPENMRS);
        PersonName name = new PersonName(webStaff.getFirstName(), null, webStaff.getLastName());
        user.addName(name);

        if (webStaff.getPhone() != null) {
            PersonAttributeType phoneNumberAttrType = PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER.
                    getAttributeType(personService);
            user.addAttribute(new PersonAttribute(phoneNumberAttrType, webStaff.getPhone()));
        }

        Role role = userService.getRole(OpenmrsConstants.PROVIDER_ROLE);
        user.addRole(role);
        return user;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setPersonService(PersonService personService){
        this.personService = personService;
    }
}
