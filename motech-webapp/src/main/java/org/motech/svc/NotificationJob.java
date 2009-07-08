package org.motech.svc;

import java.util.Date;
import java.util.List;

import org.motech.model.FutureServiceDelivery;
import org.motech.model.LogType;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class NotificationJob extends QuartzJobBean {

	Registrar registrarBean;
	Logger loggerBean;

	public Registrar getRegistrarBean() {
		return registrarBean;
	}

	public void setRegistrarBean(Registrar registrarBean) {
		this.registrarBean = registrarBean;
	}

	public Logger getLoggerBean() {
		return loggerBean;
	}

	public void setLoggerBean(Logger loggerBean) {
		this.loggerBean = loggerBean;
	}

	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {
		
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