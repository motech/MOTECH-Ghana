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
				logger.log(LogType.success,
						"Future Service Delivery Notifications: "
								+ service.getNurse().getPhoneNumber() + ","
								+ service.getPatient().getPhoneNumber());
				service.setNurseNotifiedDate(notificationDate);
				service.setPatientNotifiedDate(notificationDate);
				dao.updateFutureServiceDelivery(service);
			}
		}
	}
}
