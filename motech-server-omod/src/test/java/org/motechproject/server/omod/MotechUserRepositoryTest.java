package org.motechproject.server.omod;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.MotechUserTypes;
import org.motechproject.server.model.db.hibernate.MotechUsers;
import org.motechproject.server.omod.web.model.WebStaff;
import org.openmrs.PersonAttributeType;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MotechUserRepositoryTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private MotechUsers motechUsers;

    @Before
    public void setUp() throws Exception {
      executeDataSet("patient-edit-data.xml");
    }

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

        MotechUserRepository motechUserRepository = new MotechUserRepository(identifierGenerator, userService, personService, motechUsers);
        User user = motechUserRepository.newUser(new WebStaff("Jenny", "Jones ", "1001", "CHO"));

        verify(identifierGenerator).generateStaffId();
        verify(personService).getPersonAttributeTypeByName("Phone Number");
        verify(personService).getPersonAttributeTypeByName("Staff Type");
        verify(userService).getRole("Provider");
        assertThat(user.getSystemId(), equalTo("27"));
    }

    @Test
    public void shouldRetrieveUserTypes() {
        MotechUserRepository repository = new MotechUserRepository(null, null, null, motechUsers);
        MotechUserTypes userTypes = repository.userTypes();
        assertTrue(userTypes.hasTypes(2));
        assertTrue(userTypes.hasType("CHO"));
        assertTrue(userTypes.hasType("CHN"));
    }
}
