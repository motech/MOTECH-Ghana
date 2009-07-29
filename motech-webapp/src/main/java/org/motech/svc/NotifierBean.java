package org.motech.svc;

import java.util.Date;
import java.util.List;

import org.motech.dao.SimpleDao;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotifierBean implements Notifier {

	SimpleDao dao;
	Logger logger;

	@Autowired
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Autowired
	public void setDao(SimpleDao dao) {
		this.dao = dao;
	}

	public void sendNotifications(int intervalInSecs) {
		int halfInterval = (int) (intervalInSecs / 2.0);
		Date startDate = new Date(System.currentTimeMillis()
				- (halfInterval * 1000));
		Date endDate = new Date(System.currentTimeMillis()
				+ (halfInterval * 1000));
		List<FutureServiceDelivery> futureServices = dao
				.getFutureServiceDeliveries(startDate, endDate);
		if (futureServices.size() > 0) {
			Date notificationDate = new Date();
			for (FutureServiceDelivery service : futureServices) {
				if (service.getPatientNotifiedDate() == null) {
					logger.log(LogType.success,
							"Future Service Delivery Notification, Patient: "
									+ service.getPatient().getPhoneNumber());
					service.setPatientNotifiedDate(notificationDate);
				}
				if (service.getNurseNotifiedDate() == null) {
					logger.log(LogType.success,
							"Future Service Delivery Notification, Nurse: "
									+ service.getNurse().getPhoneNumber());
					service.setNurseNotifiedDate(notificationDate);
				}
				dao.updateFutureServiceDelivery(service);
			}
		}
	}
}
