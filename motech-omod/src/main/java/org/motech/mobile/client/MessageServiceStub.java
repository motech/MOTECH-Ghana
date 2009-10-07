package org.motech.mobile.client;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dreamoval.motech.omi.ws.client.ArrayList;
import com.dreamoval.motech.omi.ws.client.ContactNumberType;
import com.dreamoval.motech.omi.ws.client.MessageServiceImpl;
import com.dreamoval.motech.omi.ws.client.MessageType;

/**
 * A stub implementation of the motech mobile message interface. This enables us
 * to configure the application to a working state without knowing the presence
 * of an active we service endpoint. The intent is that when actually deploying
 * the application, it will be reconfigured to point to the real endpoint.
 */
public class MessageServiceStub implements MessageServiceImpl {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(MessageServiceStub.class);

	public String sendCHPSMessage(long messageId, String workerName,
			String workerNumber, ArrayList patientList, String langCode,
			MessageType mediaType, long notificationType,
			XMLGregorianCalendar startDate, XMLGregorianCalendar endDate) {

		log.info("Motech Mobile Web Service Message\n"
				+ "---------------------------\n" + "<sendCHPSMessage>\n"
				+ "<messageId>"
				+ messageId
				+ "</messageId>\n"
				+ "<workerName>"
				+ workerName
				+ "</workerName>\n"
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
		return "1";
	}

	public String sendPatientMessage(long messageId, String patientName,
			String patientNumber, ContactNumberType patientNumberType,
			String langCode, MessageType mediaType, long notificationType,
			XMLGregorianCalendar startDate, XMLGregorianCalendar endDate) {

		log.info("Motech Mobile Web Service Message\n"
				+ "---------------------------\n" + "<sendPatientMessage>\n"
				+ "<messageId>"
				+ messageId
				+ "</messageId>\n"
				+ "<patientName>"
				+ patientName
				+ "</patientName>\n"
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
		return "1";
	}

}
