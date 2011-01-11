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

        when(identifierGenerator.generateStaffId()).thenReturn("27");
        when(personService.getPersonAttributeTypeByName(anyString())).thenReturn(Matchers.<PersonAttributeType>anyObject());
        when(userService.getRole("Provider")).thenReturn(role);

        MotechUserRepository motechUserRepository = new MotechUserRepository(identifierGenerator, userService, personService);
        User user = motechUserRepository.newUser(new WebStaff("Jenny", "Jones ", "1001", "CHO"));

        verify(identifierGenerator).generateStaffId();
        verify(personService).getPersonAttributeTypeByName(anyString());
        verify(userService).getRole("Provider");
        assertThat(user.getSystemId(), equalTo("27"));
    }
}
