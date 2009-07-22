package org.motech.svc;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class NotificationJob extends QuartzJobBean {

	private static Log log = LogFactory.getLog(NotificationJob.class);

	private transient ApplicationContext applicationContext;

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	protected void executeInternal(JobExecutionContext jobContext)
			throws JobExecutionException {

		if (log.isDebugEnabled())
			log.debug("Job run at " + new Date());

		Notifier notifierBean = (Notifier) applicationContext
				.getBean("notifierBean");

		notifierBean.sendNotifications(60);
	}
}