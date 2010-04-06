package org.motechproject.server.ws;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.ws.Care;
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

	private String patientToXML(Patient patient, boolean includeCares) {
		StringBuffer patientXmlString = new StringBuffer();
		patientXmlString.append("<patient>");
		patientXmlString.append("<motechId>");
		patientXmlString.append(patient.getMotechId());
		patientXmlString.append("</motechId>");
		patientXmlString.append("<preferredName>");
		patientXmlString.append(patient.getPreferredName());
		patientXmlString.append("</preferredName>");
		patientXmlString.append("<firstName>");
		patientXmlString.append(patient.getFirstName());
		patientXmlString.append("</firstName>");
		patientXmlString.append("<lastName>");
		patientXmlString.append(patient.getLastName());
		patientXmlString.append("</lastName>");
		patientXmlString.append("<birthDate>");
		patientXmlString.append(patient.getBirthDate());
		patientXmlString.append("</birthDate>");
		patientXmlString.append("<age>");
		patientXmlString.append(patient.getAge());
		patientXmlString.append("</age>");
		patientXmlString.append("<sex>");
		patientXmlString.append(patient.getSex());
		patientXmlString.append("</sex>");
		patientXmlString.append("<community>");
		patientXmlString.append(patient.getCommunity());
		patientXmlString.append("</community>");
		patientXmlString.append("<phoneNumber>");
		patientXmlString.append(patient.getPhoneNumber());
		patientXmlString.append("</phoneNumber>");
		patientXmlString.append("<estimateDueDate>");
		patientXmlString.append(patient.getEstimateDueDate());
		patientXmlString.append("</estimateDueDate>");
		patientXmlString.append("<deliveryDate>");
		patientXmlString.append(patient.getDeliveryDate());
		patientXmlString.append("</deliveryDate>");
		if (includeCares) {
			patientXmlString.append("<cares>");
			patientXmlString.append(caresToXML(patient.getCares(), false));
			patientXmlString.append("</cares>");
		}
		patientXmlString.append("</patient>");
		return patientXmlString.toString();
	}

	private String caresToXML(Care[] cares, boolean includePatients) {
		StringBuilder careInfoString = new StringBuilder();
		for (Care care : cares) {
			careInfoString.append("<name>" + care.getName() + "</name>");
			careInfoString.append("<date>" + care.getDate() + "</date>\n");
			if (includePatients) {
				for (Patient patient : care.getPatients()) {
					careInfoString.append(patientToXML(patient, false));
					careInfoString.append('\n');
				}
			}
		}
		return careInfoString.toString();
	}

	public MessageStatus sendDefaulterMessage(String messageId,
			String workerNumber, Care[] cares, MediaType mediaType,
			Date startDate, Date endDate) {

		log.info("Motech Mobile Web Service Message\n"
				+ "---------------------------\n" + "<sendDefaulterMessage>\n"
				+ "<messageId>" + messageId + "</messageId>\n" + "<careInfo>"
				+ caresToXML(cares, true) + "</careInfo>\n" + "<workerNumber>"
				+ workerNumber + "</workerNumber>\n" + "<mediaType>"
				+ mediaType + "</mediaType>\n" + "<startDate>" + startDate
				+ "</startDate>\n" + "<endDate>" + endDate + "</endDate>\n"
				+ "</sendDefaulterMessage>\n"
				+ "--------------------------------------");

		return MessageStatus.DELIVERED;
	}

	public MessageStatus sendDeliveriesMessage(String messageId,
			String workerNumber, Patient[] patients, String deliveryStatus,
			MediaType mediaType, Date startDate, Date endDate) {

		StringBuilder patientInfoString = new StringBuilder();
		for (Patient patient : patients) {
			patientInfoString.append(patientToXML(patient, false));
			patientInfoString.append("\n");
		}

		log.info("Motech Mobile Web Service Message\n"
				+ "---------------------------\n" + "<sendDeliveriesMessage>\n"
				+ "<messageId>"
				+ messageId
				+ "</messageId>\n"
				+ "<patientInfo>"
				+ patientInfoString.toString()
				+ "</patientInfo>\n"
				+ "<workerNumber>"
				+ workerNumber
				+ "</workerNumber>\n"
				+ "<deliveryStatus>"
				+ deliveryStatus
				+ "</deliveryStatus>\n"
				+ "<mediaType>"
				+ mediaType
				+ "</mediaType>\n"
				+ "<startDate>"
				+ startDate
				+ "</startDate>\n"
				+ "<endDate>"
				+ endDate
				+ "</endDate>\n"
				+ "</sendDeliveriesMessage>\n"
				+ "--------------------------------------");

		return MessageStatus.DELIVERED;
	}

	public MessageStatus sendUpcomingCaresMessage(String messageId,
			String workerNumber, Patient patient, MediaType mediaType,
			Date startDate, Date endDate) {

		log.info("Motech Mobile Web Service Message\n"
				+ "---------------------------\n"
				+ "<sendUpcomingCaresMessage>\n" + "<messageId>" + messageId
				+ "</messageId>\n" + "<patientInfo>"
				+ patientToXML(patient, true) + "</patientInfo>\n"
				+ "<workerNumber>" + workerNumber + "</workerNumber>\n"
				+ "<mediaType>" + mediaType + "</mediaType>\n" + "<startDate>"
				+ startDate + "</startDate>\n" + "<endDate>" + endDate
				+ "</endDate>\n" + "</sendUpcomingCaresMessage>\n"
				+ "--------------------------------------");

		return MessageStatus.DELIVERED;
	}

	public MessageStatus sendMessage(String content, String recipient) {

		log.info("Motech Mobile Web Service Message\n"
				+ "---------------------------\n" + "<sendMessage>\n"
				+ "<content>" + content + "</content>\n" + "<recipient>"
				+ recipient + "</recipient>\n" + "</sendMessage>\n"
				+ "--------------------------------------");

		return MessageStatus.DELIVERED;
	}

}
