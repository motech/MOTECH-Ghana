package org.motech.svc;

import java.util.Date;
import java.util.List;

import org.motech.dao.SimpleDao;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.LogType;
import org.motech.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dreamoval.motech.omi.service.ContactNumberType;
import com.dreamoval.motech.omi.service.MessageType;
import com.dreamoval.motech.webapp.webservices.MessageService;

@Service
@Transactional
public class NotifierBean implements Notifier {

	SimpleDao dao;
	Logger logger;
	MessageService mobileClient;

	@Autowired
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Autowired
	public void setDao(SimpleDao dao) {
		this.dao = dao;
	}

	@Autowired
	public void setMobileClient(MessageService mobileClient) {
		this.mobileClient = mobileClient;
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
					Patient patient = service.getPatient();
					logger.log(LogType.success,
							"Future Service Delivery Notification, Patient: "
									+ patient.getPhoneNumber());

					ContactNumberType patientNumberType = patient
							.getPhoneType().toContactNumberType();
					MessageType messageType = patient.getNotificationType()
							.toMessageType();
					mobileClient.sendPatientMessage(new Long(1), patient
							.getClinic().getName(), notificationDate, patient
							.getPhoneNumber(), patientNumberType, messageType);

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
