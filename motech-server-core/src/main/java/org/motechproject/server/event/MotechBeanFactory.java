package org.motechproject.server.event;

import org.motechproject.server.event.impl.RemoveEnrollmentCommand;
import org.motechproject.server.event.impl.ScheduleMessageCommand;
import org.motechproject.server.messaging.MessageScheduler;
import org.motechproject.server.model.db.ProgramMessageKey;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.time.TimeBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MotechBeanFactory {
    private ApplicationContext context;

    public MotechBeanFactory() {
        context = new ClassPathXmlApplicationContext(new String[]{"classpath*:moduleApplicationContext.xml", "classpath:applicationContext-service.xml"});
    }

    public MessagesCommand createMessageCommandWith(ProgramMessageKey messageKey){
       return new ScheduleMessageCommand(messageKey, (MessageScheduler) context.getBean("userPrefmessageScheduler",MessageScheduler.class));
    }

    public TimeBean timeBean(){
      return (TimeBean) context.getBean("timeBean",TimeBean.class); 
    }

    public MessagesCommand createRemoveCommand() {
        RemoveEnrollmentCommand command = new RemoveEnrollmentCommand();
        command.setRegistrarBean((RegistrarBean) context.getBean("registrarBean"));
        return command;
    }
}
