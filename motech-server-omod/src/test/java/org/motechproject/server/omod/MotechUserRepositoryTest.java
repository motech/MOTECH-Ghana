package org.motechproject.server.omod;

import org.junit.Test;
import org.mockito.Matchers;
import org.motechproject.server.omod.web.model.WebStaff;
import org.openmrs.PersonAttributeType;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class MotechUserRepositoryTest {

    @Test
    public void testNewUser() throws Exception {
        PersonService personService = mock(PersonService.class);
        IdentifierGenerator identifierGenerator = mock(IdentifierGenerator.class);
        UserService userService = mock(UserService.class);
        Role role = mock(Role.class);
        PersonAttributeType phoneNumberAttributeType = new PersonAttributeType();
        phoneNumberAttributeType.setId(1);
        PersonAttributeType staffTypeAttributeType = new PersonAttributeType();
        staffTypeAttributeType.setId(2);

        when(identifierGenerator.generateStaffId()).thenReturn("27");
        when(personService.getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER.getAttributeName())).thenReturn(phoneNumberAttributeType);
        when(personService.getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_STAFF_TYPE.getAttributeName())).thenReturn(staffTypeAttributeType);
        when(userService.getRole("Provider")).thenReturn(role);

        MotechUserRepository motechUserRepository = new MotechUserRepository(identifierGenerator, userService, personService);
        User user = motechUserRepository.newUser(new WebStaff("Jenny", "Jones ", "1001", "CHO"));

        verify(identifierGenerator).generateStaffId();
        verify(personService).getPersonAttributeTypeByName("Phone Number");
        verify(personService).getPersonAttributeTypeByName("Staff Type");
        verify(userService).getRole("Provider");
        assertThat(user.getSystemId(), equalTo("27"));
    }

    @Test
    public void testNewUserWithoutPhoneNumber() throws Exception {
        PersonService personService = mock(PersonService.class);
        IdentifierGenerator identifierGenerator = mock(IdentifierGenerator.class);
        UserService userService = mock(UserService.class);
        Role role = mock(Role.class);
        PersonAttributeType phoneNumberAttributeType = new PersonAttributeType();
        phoneNumberAttributeType.setId(1);
        PersonAttributeType staffTypeAttributeType = new PersonAttributeType();
        staffTypeAttributeType.setId(2);

        when(identifierGenerator.generateStaffId()).thenReturn("27");
        when(personService.getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER.getAttributeName())).thenReturn(phoneNumberAttributeType);
        when(personService.getPersonAttributeTypeByName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_STAFF_TYPE.getAttributeName())).thenReturn(staffTypeAttributeType);
        when(userService.getRole("Provider")).thenReturn(role);

        String emptyPhoneNumber = null;
        MotechUserRepository motechUserRepository = new MotechUserRepository(identifierGenerator, userService, personService);
        User user = motechUserRepository.newUser(new WebStaff("Jenny", "Jones ", emptyPhoneNumber, "CHO"));

        verify(identifierGenerator).generateStaffId();
        verify(personService).getPersonAttributeTypeByName("Phone Number");
        verify(personService).getPersonAttributeTypeByName("Staff Type");
        verify(userService).getRole("Provider");
        assertThat(user.getSystemId(), equalTo("27"));
        assertThat(user.getAttribute(1).getValue(), equalTo(""));
        assertThat(user.getAttribute(2).getValue(), equalTo("CHO"));
    }
}
