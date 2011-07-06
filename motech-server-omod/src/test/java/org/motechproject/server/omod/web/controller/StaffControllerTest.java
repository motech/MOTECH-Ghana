package org.motechproject.server.omod.web.controller;

import org.junit.Test;
import org.motechproject.server.omod.PersonAttributeTypeEnum;
import org.motechproject.server.omod.web.model.WebStaff;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class StaffControllerTest {

    @Test
    public void registerStaff() {
        RegistrarBean registrarBean = createMock(RegistrarBean.class);
        Errors errors = createMock(Errors.class);

        StaffController controller = new StaffController();
        controller.setRegistrarBean(registrarBean);

        ModelMap map = new ModelMap();
        WebStaff staff = new WebStaff();
        staff.setFirstName("Joseph");
        staff.setLastName("Jones");
        staff.setPhone("0123456789");
        staff.setType("CHO");

        expect(errors.getFieldValue("firstName")).andReturn(staff.getFirstName());
        expect(errors.getFieldValue("lastName")).andReturn(staff.getLastName());
        expect(errors.getFieldValue("type")).andReturn(staff.getType());
        expect(errors.hasErrors()).andReturn(false);
        expect(registrarBean.registerStaff(staff.getFirstName(),staff.getLastName(),staff.getPhone(),
                staff.getType(),staff.getStaffId())).andReturn(user(staff));
        List<String> staffTypes = new ArrayList<String>();
        staffTypes.add("CHO");
        expect(registrarBean.getStaffTypes()).andReturn(staffTypes);

        replay(errors,registrarBean);

        String view = controller.registerStaff(staff, errors, map);

        verify(errors,registrarBean);

        assertEquals("/module/motechmodule/staff",view);
        assertEquals(staffTypes,map.get("staffTypes"));
        assertEquals("Added user: Name = Joseph Jones, Staff ID = 465",map.get("successMsg"));
    }

    @Test
    public void editStaff() {
        RegistrarBean registrarBean = createMock(RegistrarBean.class);
        Errors errors = createMock(Errors.class);

        StaffController controller = new StaffController();
        controller.setRegistrarBean(registrarBean);

        ModelMap map = new ModelMap();
        WebStaff staff = new WebStaff();
        staff.setFirstName("Joseph");
        staff.setLastName("Jones");
        staff.setPhone("0123456789");
        staff.setType("CHO");
        staff.setStaffId("465");

        expect(errors.getFieldValue("firstName")).andReturn(staff.getFirstName());
        expect(errors.getFieldValue("lastName")).andReturn(staff.getLastName());
        expect(errors.getFieldValue("type")).andReturn(staff.getType());
        expect(errors.hasErrors()).andReturn(false);
        expect(registrarBean.registerStaff(staff.getFirstName(),staff.getLastName(),staff.getPhone(),
                staff.getType(),staff.getStaffId())).andReturn(user(staff));
        List<String> staffTypes = new ArrayList<String>();
        staffTypes.add("CHO");
        expect(registrarBean.getStaffTypes()).andReturn(staffTypes);

        replay(errors,registrarBean);

        String view = controller.registerStaff(staff, errors, map);

        verify(errors,registrarBean);

        assertEquals("/module/motechmodule/staff",view);
        assertEquals(staffTypes,map.get("staffTypes"));
        assertEquals("Saved user info: Name = Joseph Jones, Staff ID= 465, Phone number: 0123456789, Staff Type: CHO",map.get("successMsg"));
    }


    private User user(WebStaff staff) {
        User user = new User(1);
        PersonName name = new PersonName();
        name.setGivenName(staff.getFirstName());
        name.setFamilyName(staff.getLastName());
        user.addName(name);
        user.setSystemId("465");
        PersonAttributeType attributeTypePhone = new PersonAttributeType(1);
        attributeTypePhone.setName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_PHONE_NUMBER.getAttributeName());

        PersonAttributeType attributeStaffType = new PersonAttributeType(2);
        attributeStaffType.setName(PersonAttributeTypeEnum.PERSON_ATTRIBUTE_STAFF_TYPE.getAttributeName());

        Set<PersonAttribute> attributes = new HashSet<PersonAttribute>();
        attributes.add(new PersonAttribute(attributeTypePhone,staff.getPhone()));
        attributes.add(new PersonAttribute(attributeStaffType,staff.getType()));
        user.setAttributes(attributes);

        return user;
    }
}
