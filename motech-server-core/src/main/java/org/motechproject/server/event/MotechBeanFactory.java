package org.motechproject.server.event;

import org.motechproject.server.event.impl.ScheduleMessageCommand;
import org.motechproject.server.messaging.MessageScheduler;
import org.motechproject.server.model.db.ProgramMessageKey;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MotechBeanFactory {
    private ApplicationContext context;

    public MotechBeanFactory() {
        context = new ClassPathXmlApplicationContext("classpath*:moduleApplicationContext.xml");
    }

    public MessagesCommand scheduledMessageCommandWith(ProgramMessageKey messageKey){
       return new ScheduleMessageCommand(messageKey, (MessageScheduler) context.getBean("userPrefmessageScheduler"));
    }



}
