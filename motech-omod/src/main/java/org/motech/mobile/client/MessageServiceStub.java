package org.motech.mobile.client;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.MessageStatus;
import org.motechproject.ws.NameValuePair;
import org.motechproject.ws.Patient;
import org.motechproject.ws.mobile.MessageService;

/**
 * A stub implementation of the motech mobile message interface. This enables us
 * to configure the application to a working state without knowing the presence
 * of an active we service endpoint. The intent is that when actually deploying
 * the application, it will be reconfigured to point to the real endpoint.
 */
public class MessageServiceStub implements MessageService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(MessageServiceStub.class);

	public MessageStatus sendCHPSMessage(String messageId,
			NameValuePair[] personalInfo, String workerNumber,
			Patient[] patients, String langCode, MediaType mediaType,
			Long notificationType, Date startDate, Date endDate) {

		StringBuilder personalInfoString = new StringBuilder();
		for (NameValuePair pair : personalInfo) {
			personalInfoString.append("<name>" + pair.getName() + "</name>");
			personalInfoString.append("<value>" + pair.getValue()
					+ "</value>\n");
		}

		log.info("Motech Mobile Web Service Message\n"
				+ "---------------------------\n" + "<sendCHPSMessage>\n"
				+ "<messageId>"
				+ messageId
				+ "</messageId>\n"
				+ "<personalInfo>\n"
				+ personalInfoString.toString()
				+ "</personalInfo>\n"
				+ "<workerNumber>"
				+ workerNumber
				+ "</workerNumber>\n"
				+ "<patientList></patientList>\n"
				+ "<langCode>"
				+ langCode
				+ "</langCode>\n"
				+ "<mediaType>"
				+ mediaType
				+ "</mediaType>\n"
				+ "<notificationType>"
				+ notificationType
				+ "</notificationType>\n"
				+ "<startDate>"
				+ startDate
				+ "</startDate>\n"
				+ "<endDate>"
				+ endDate
				+ "</endDate>\n"
				+ "</sendCHPSMessage>\n"
				+ "--------------------------------------");
		return MessageStatus.DELIVERED;
	}

	public MessageStatus sendPatientMessage(String messageId,
			NameValuePair[] personalInfo, String patientNumber,
			ContactNumberType patientNumberType, String langCode,
			MediaType mediaType, Long notificationType, Date startDate,
			Date endDate) {

		StringBuilder personalInfoString = new StringBuilder();
		for (NameValuePair pair : personalInfo) {
			personalInfoString.append("<name>" + pair.getName() + "</name>");
			personalInfoString.append("<value>" + pair.getValue()
					+ "</value>\n");
		}

		log.info("Motech Mobile Web Service Message\n"
				+ "---------------------------\n" + "<sendPatientMessage>\n"
				+ "<messageId>"
				+ messageId
				+ "</messageId>\n"
				+ "<personalInfo>"
				+ personalInfoString.toString()
				+ "</personalInfo>\n"
				+ "<patientNumber>"
				+ patientNumber
				+ "</patientNumber>\n"
				+ "<contactNumberType>"
				+ patientNumberType
				+ "</contactNumberType>\n"
				+ "<langCode>"
				+ langCode
				+ "</langCode>\n"
				+ "<mediaType>"
				+ mediaType
				+ "</mediaType>\n"
				+ "<notificationType>"
				+ notificationType
				+ "</notificationType>\n"
				+ "<startDate>"
				+ startDate
				+ "</startDate>\n"
				+ "<endDate>"
				+ endDate
				+ "</endDate>\n"
				+ "</sendPatientMessage>\n"
				+ "--------------------------------------");
		return MessageStatus.DELIVERED;
	}

}
