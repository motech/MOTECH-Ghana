package org.motech.svc;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.LogType;
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

		log.info("Job run at " + new Date());

		Registrar registrarBean = (Registrar) applicationContext
				.getBean("registrarBean");
		Logger loggerBean = (Logger) applicationContext.getBean("loggerBean");

		Date startDate = new Date(System.currentTimeMillis() - (30 * 1000));
		Date endDate = new Date(System.currentTimeMillis() + (30 * 1000));
		List<FutureServiceDelivery> futureServices = registrarBean
				.getFutureServiceDeliveries(startDate, endDate);
		if (futureServices.size() > 0) {
			Date notificationDate = new Date();
			for (FutureServiceDelivery service : futureServices) {
				loggerBean.log(LogType.success,
						"Future Service Delivery Notifications: "
								+ service.getNurse().getPhoneNumber() + ","
								+ service.getPatient().getPhoneNumber());

				registrarBean.notifyFutureService(service, notificationDate);
			}
		}
	}
}