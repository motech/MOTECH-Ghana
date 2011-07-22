package org.motechproject.server.omod.tasks;

import org.junit.Test;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.StaffMessageService;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.scheduler.TaskDefinition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.easymock.EasyMock.*;

public class StaffCareMessagingTaskTest {

    @Test
    public void shouldRetrieveTaskConfigurationProperties() throws ParseException {
        ContextService contextService = createMock(ContextService.class);
        StaffMessageService staffMessageService = createMock(StaffMessageService.class);

        TaskDefinition taskDefinition
                = new TaskDefinition(1, "Weekly Staff Messaging Task", "Send Messages to Nurses", "some.class");

        String deliveryTimeValue = "10:15";
        String careGroups = "ANC,TT,IPT";

        taskDefinition.setProperty(MotechConstants.TASK_PROPERTY_SEND_UPCOMING, "true");
        taskDefinition.setProperty(MotechConstants.TASK_PROPERTY_SEND_NO_DEFAULTER_AND_NO_UPCOMING_CARE_MESSAGE, "true");
        taskDefinition.setProperty(MotechConstants.TASK_PROPERTY_CARE_GROUPS, careGroups);
        taskDefinition.setProperty(MotechConstants.TASK_PROPERTY_AVOID_BLACKOUT, "true");
        taskDefinition.setProperty(MotechConstants.TASK_PROPERTY_DELIVERY_TIME, deliveryTimeValue);

        Long secondsInAWeek = 604800L;
        taskDefinition.setRepeatInterval(secondsInAWeek);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 7);
        Date dateAfterOneWeek = calendar.getTime();

        StaffCareMessagingTask task = new StaffCareMessagingTask();
        task.initialize(taskDefinition);

        task.setContextService(contextService);

        Date deliveryTime = new SimpleDateFormat(MotechConstants.TIME_FORMAT_DELIVERY_TIME).parse("10:15");

        contextService.openSession();
        expectLastCall();

        expect(contextService.getStaffMessageService()).andReturn(staffMessageService);

        Date today = new Date();
        staffMessageService.sendStaffCareMessages(equalsDate(today), equalsDate(dateAfterOneWeek), equalsDate(today), equalsDate(deliveryTime),
                equalsArray(careGroups.split(MotechConstants.TASK_PROPERTY_CARE_GROUPS_DELIMITER)), eq(true), eq(true), eq(true));
        expectLastCall();

        contextService.closeSession();
        expectLastCall();

        replay(contextService, staffMessageService);

        task.execute();

        verify(contextService, staffMessageService);

    }

    private static String[] equalsArray(String[] array){
        reportMatcher(new ArrayMatcher<String>(array));
        return array;
    }

    private static Date equalsDate(Date date) throws ParseException {
        reportMatcher(new DateMatcher(date));
       return date ;
    }



}


