package org.motechproject.server.omod.web.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.model.SupportCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:mail-template-context.xml"})
public class SupportCaseMailTemplateTest {

    @Autowired
    private SupportCaseMailTemplate mailTemplate;


    @Test
    public void shouldCreateTextFromTemplate() throws ParseException {
        Map data = new HashMap();
        WebStaff staff = new WebStaff();
        staff.setFirstName("Joe");
        staff.setLastName("Jee");
        staff.setStaffId("465");

        SupportCase supportCase = new SupportCase();
        supportCase.setDateRaisedOn("2011-06-06 10:10:10");
        supportCase.setDescription("Network Failure");

        data.put("staff",staff);
        data.put("case",supportCase);


        assertEquals("Support: Case reported by Joe Jee on 2011-06-06 10:10:10", mailTemplate.subject(data));
        assertEquals("Joe Jee with Staff id 465 reported the following issue: Network Failure", mailTemplate.text(data));
    }
}